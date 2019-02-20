package com.lormor.banking.expense.rules;

public class ExpenseRules {

    public static AmountMatchesRule amountMatchesRule(double amount) {
        return new AmountMatchesRule(amount);
    }
}
