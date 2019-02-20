package com.lormor.banking.expense.rules;

import com.lormor.banking.expense.Expense;

import java.util.function.Function;

public class MatchesExactAmountExpenseRule implements Function<Expense, Boolean> {

    private Double amount;

    public MatchesExactAmountExpenseRule(Double amount) {
        this.amount = amount;
    }

    @Override
    public Boolean apply(Expense expense) {
        return expense.getAmount().equals(amount);
    }
}
