package com.lormor.banking.expense.rules;

import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ImmutableExpense;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExpenseRulesTest {

    @Test
    public void test_amount_matches_rule() {
        AmountMatchesRule rule = ExpenseRules.amountMatchesRule(10.0);

        Expense expense = ImmutableExpense.builder().amount(10.0).build();
        assertTrue(rule.apply(expense));

        expense = ImmutableExpense.builder().amount((double) 10).build();
        assertTrue(rule.apply(expense));

        expense = ImmutableExpense.builder().amount(10.01).build();
        assertFalse(rule.apply(expense));

        expense = ImmutableExpense.builder().amount(9.99).build();
        assertFalse(rule.apply(expense));

        expense = ImmutableExpense.builder().amount(-10.00).build();
        assertFalse(rule.apply(expense));

        expense = ImmutableExpense.builder().amount((double) 0).build();
        assertFalse(rule.apply(expense));
    }

    @SuppressFBWarnings
    @Test(expected = RuntimeException.class)
    public void test_throw_exception_rule() {
        Function<Expense, Boolean> rule = ExpenseRules.throwExceptionRule();

        Expense expense = ImmutableExpense.builder().amount(10.0).build();
        rule.apply(expense);
        fail("A runtime exception should have been thrown");
    }
}
