package com.lormor.banking.expense;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.Map;
import java.util.function.Function;

public class ExpenseCategoriser {

    public static final String UNCATEGORISED = "UNCATEGORISED";

    private final LinkedListMultimap<String, Function<Expense, Boolean>> rules;

    public ExpenseCategoriser(LinkedListMultimap<String, Function<Expense, Boolean>> rules) {
        this.rules = rules;
    }

    public String categoriseExpense(Expense test) {
        for (Map.Entry<String, Function<Expense, Boolean>> entry : rules.entries()) {
            if (entry.getValue().apply(test)) {
                return entry.getKey();
            }
        }

        return UNCATEGORISED;
    }
}
