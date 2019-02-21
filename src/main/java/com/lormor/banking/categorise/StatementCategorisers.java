package com.lormor.banking.categorise;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.parse.ImmutableParseResult;
import com.lormor.banking.parse.StatementParser;
import com.lormor.banking.parse.StatementParsers;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class StatementCategorisers {

    public static final String CATEGORY_UNCATEGORISED = "UNCATEGORISED";

    private StatementCategorisers() {
        // utility class
    }

    public static StatementCategoriser create(LinkedListMultimap<String, Function<Expense, Boolean>> rules) {
        return new DefaultStatementCategoriser(rules);
    }

}

class DefaultStatementCategoriser implements StatementCategoriser {

    private static final StatementParser DEFAULT_PARSER = StatementParsers.santanderCreditCardStatementParser();
    private final LinkedListMultimap<String, Function<Expense, Boolean>> rules;

    DefaultStatementCategoriser(LinkedListMultimap<String, Function<Expense, Boolean>> rules) {
        this.rules = rules;
    }

    @Override
    public CategoriseResult categoriseExpenses(File file) {
        Map<String, Multimap<String, Expense>> processedFiles = Maps.newLinkedHashMap();

        ImmutableParseResult parsedResults = (ImmutableParseResult) DEFAULT_PARSER.parseExpenses(file);

        for (String key : parsedResults.getParsedFiles()) {
            Collection<Expense> expenses = parsedResults.getFileExpenses(key);
            Multimap<String, Expense> categorisedResult = categoriseExpense(expenses);
            processedFiles.put(key, categorisedResult);
        }

        return ImmutableCategoriseResult
                .builder()
                .addAllSkippedFiles(parsedResults.getSkippedFiles())
                .putAllCategorisedExpenses(processedFiles)
                .build();
    }

    @VisibleForTesting
    String categoriseExpense(Expense expense) {
        for (Map.Entry<String, Function<Expense, Boolean>> entry : rules.entries()) {
            if (entry.getValue().apply(expense)) {
                return entry.getKey();
            }
        }

        return StatementCategorisers.CATEGORY_UNCATEGORISED;
    }

    private Multimap<String, Expense> categoriseExpense(Collection<Expense> expenses) {
        Multimap<String, Expense> result = LinkedListMultimap.create();

        for (Expense expense : expenses) {
            String category = categoriseExpense(expense);
            result.put(category, expense);
        }

        return result;
    }
}
