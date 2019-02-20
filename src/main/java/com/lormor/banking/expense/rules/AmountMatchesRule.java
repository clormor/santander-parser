package com.lormor.banking.expense.rules;

import com.lormor.banking.expense.Expense;

import java.util.function.Function;

public class AmountMatchesRule implements Function<Expense, Boolean> {

    private Double amount;

    public AmountMatchesRule(Double amount) {
        this.amount = amount;
    }

    @Override
    public Boolean apply(Expense expense) {
        return expense.getAmount().equals(amount);
    }
}
