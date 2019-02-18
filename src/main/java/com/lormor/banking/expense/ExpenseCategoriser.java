package com.lormor.banking.expense;

import com.google.common.collect.ListMultimap;

import java.util.Map;

public class ExpenseCategoriser {

    public static final String UNCATEGORISED = "UNCATEGORISED";

    private final ListMultimap<String, ExpenseRule> rules;

    public ExpenseCategoriser(ListMultimap<String, ExpenseRule> rules) {
        this.rules = rules;
    }

    public String categoriseExpense(Expense test) {
        for (Map.Entry<String, ExpenseRule> entry : rules.entries()) {
            if (entry.getValue().apply(test)) {
                return entry.getKey();
            }
        }

        return UNCATEGORISED;
    }
}
