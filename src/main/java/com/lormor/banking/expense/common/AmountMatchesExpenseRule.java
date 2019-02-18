package com.lormor.banking.expense.common;

import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ExpenseRule;

public class AmountMatchesExpenseRule extends ExpenseRule {

    private Double amount;

    public AmountMatchesExpenseRule(Double amount) {
        this.amount = amount;
    }

    @Override
    public Boolean apply(Expense expense) {
        return expense.getAmount().equals(amount);
    }
}
