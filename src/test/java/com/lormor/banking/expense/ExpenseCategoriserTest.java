package com.lormor.banking.expense;

import com.google.common.collect.LinkedListMultimap;
import com.lormor.banking.expense.rules.ExpenseRules;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class ExpenseCategoriserTest {

    private static final String TEST_CATEGORY = "test-category";

    private LinkedListMultimap<String, Function<Expense, Boolean>> rules;
    private ExpenseCategoriser categoriser;

    @Before
    public void setup() {
        rules = LinkedListMultimap.create();
    }

    @Test
    public void simple_rule_match() {
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        categoriser = new ExpenseCategoriser(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));

        Expense nonMatching = ImmutableExpense.builder().amount(1.0).build();
        assertEquals(ExpenseCategoriser.UNCATEGORISED, categoriser.categoriseExpense(nonMatching));
    }

    @Test
    public void rule_ordering_preserved() {
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        rules.put("some other category", ExpenseRules.amountMatchesRule(10.0));
        rules.put("another category", ExpenseRules.amountMatchesRule(10.0));
        rules.put("yet another category", ExpenseRules.amountMatchesRule(10.0));
        categoriser = new ExpenseCategoriser(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));
    }

    @Test
    public void ordering_preserved_if_keys_match() {
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        rules.put(TEST_CATEGORY, ExpenseRules.throwExceptionRule());
        categoriser = new ExpenseCategoriser(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));
    }

    // Same as above but check our test logic works by switching the order
    @Test (expected = RuntimeException.class)
    public void ordering_preserved_if_keys_match_reversed() {
        rules.put(TEST_CATEGORY, ExpenseRules.throwExceptionRule());
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        categoriser = new ExpenseCategoriser(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));
    }
}
