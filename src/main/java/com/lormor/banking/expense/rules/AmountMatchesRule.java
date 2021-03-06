package com.lormor.banking.expense.rules;

import com.lormor.banking.expense.Expense;

import java.util.function.Function;

class AmountMatchesRule implements Function<Expense, Boolean> {

    private Double amount;

    AmountMatchesRule(Double amount) {
        this.amount = amount;
    }

    @Override
    public Boolean apply(Expense expense) {
        return expense.getAmount().equals(amount);
    }
}
