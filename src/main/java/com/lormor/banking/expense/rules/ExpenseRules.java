package com.lormor.banking.expense.rules;

import com.google.common.annotations.VisibleForTesting;
import com.lormor.banking.expense.Expense;

import java.util.function.Function;

public class ExpenseRules {

    public static AmountMatchesRule amountMatchesRule(double amount) {
        return new AmountMatchesRule(amount);
    }

    @VisibleForTesting
    public static Function<Expense, Boolean> throwExceptionRule() {
        return new Function<Expense, Boolean>() {
            @Override
            public Boolean apply(Expense expense) {
                throw new RuntimeException("Thrown by throwExceptionRule() in " + ExpenseRules.class.getName());
            }
        };
    }
}
