package com.lormor.banking.expense.rules;

import com.google.common.annotations.VisibleForTesting;
import com.lormor.banking.expense.Expense;

import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class ExpenseRules {

    public static AmountMatchesRule amountMatchesRule(double amount) {
        return new AmountMatchesRule(amount);
    }

    @VisibleForTesting
    public static Function<Expense, Boolean> throwExceptionRule() {
        return expense -> {
            throw new RuntimeException("Thrown by throwExceptionRule() in " + ExpenseRules.class.getName());
        };
    }
}
