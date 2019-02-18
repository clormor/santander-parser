package com.lormor.banking.expense;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.lormor.banking.expense.common.AmountMatchesExpenseRule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpenseCategoriserTest {

    private static final String TEST_CATEGORY = "test-category";

    @Test
    public void test_simple_rule_match() {
        ListMultimap<String, ExpenseRule> rules = ArrayListMultimap.create();
        rules.put(TEST_CATEGORY, new AmountMatchesExpenseRule(10.0));

        Expense test = ImmutableExpense.builder().amount(10.0).build();
        ExpenseCategoriser categoriser = new ExpenseCategoriser(rules);
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(test));
    }
}
