package com.lormor.banking.expense;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ExpenseTest {

    @Test
    public void test_defaults() {
        Expense expense = ImmutableExpense.builder().amount(0.0).build();

        assertEquals(DateTime.now().getDayOfMonth(), expense.getDate().getDayOfMonth());
        assertEquals(DateTime.now().getMonthOfYear(), expense.getDate().getMonthOfYear());
        assertEquals(DateTime.now().getYear(), expense.getDate().getYear());

        assertEquals("", expense.getDescription());
    }

    @Test
    public void test_equality() {
        // use a fixed date, as DateTime.now() will change slightly between each instantiation
        DateTime date = DateTime.now();

        ImmutableExpense expense1 = ImmutableExpense.builder().amount(0.0).date(date).build();

        ImmutableExpense expense2 = ImmutableExpense.copyOf(expense1);
        assertNotEquals(expense1, expense2);
        ImmutableExpense expense3 = ImmutableExpense.builder().amount(0.0).date(date).build();
        assertNotEquals(expense1, expense3);
        ImmutableExpense expense4 = ImmutableExpense.builder().amount((double) 0).date(date).build();
        assertNotEquals(expense1, expense4);
        ImmutableExpense expense5 = ImmutableExpense.builder().amount(10.0).date(date).build();
        assertNotEquals(expense1, expense5);
    }
}
