package com.lormor.banking.categorise;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.parse.ParsedResult;
import com.lormor.banking.parse.StatementParser;
import com.lormor.banking.parse.StatementParsers;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class StatementCategorisers {

    public static final String CATEGORY_UNCATEGORISED = "UNCATEGORISED";

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
    public CategorisedResult categoriseExpenses(File file) {
        Map<String, Multimap<String, Expense>> processedFiles = Maps.newLinkedHashMap();

        ParsedResult parsedResults = DEFAULT_PARSER.parseExpenses(file);

        for (String fileKey : parsedResults.getExpenses().keySet()) {
            Collection<Expense> expenses = parsedResults.getExpenses().get(fileKey);
            Multimap<String, Expense> categorisedResult = categoriseExpense(expenses);
            processedFiles.put(fileKey, categorisedResult);
        }

        return ImmutableCategorisedResult
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
