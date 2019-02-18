package com.lormor.banking.santander;

import com.lormor.banking.Expense;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SantanderCreditStatementParserTest {

    private SantanderCreditStatementParser parser;

    @Before
    public void setup() {
        parser = new SantanderCreditStatementParser();
    }

    @Test
    public void parseValidLine() throws NotValidExpenseException {
        String test = "7th Dec Harry Ramsdens Leeds GB 10.72";
        Expense expense = parser.parseLine(test);
        assertEquals((Double) 10.72, expense.getAmount());
        assertEquals("Harry Ramsdens Leeds GB", expense.getDescription());
        assertEquals(12, expense.getDate().getMonthOfYear());
        assertEquals(7, expense.getDate().getDayOfMonth());
        assertEquals(DateTime.now().getYear(), expense.getDate().getYear());
    }

    @Test
    public void parseValidLinePadded() throws NotValidExpenseException {
        String test = " 7th Dec Harry Ramsdens Leeds GB 10.72  ";
        Expense expense = parser.parseLine(test);
        assertEquals((Double) 10.72, expense.getAmount());
        assertEquals("Harry Ramsdens Leeds GB", expense.getDescription());
    }

    @Test
    public void parseDate() {
        DateTime date = parser.parseDate("5th Dec");
        assertEquals(5, date.getDayOfMonth());
        assertEquals(12, date.getMonthOfYear());
        assertEquals(DateTime.now().getYear(), date.getYear());

        parser = new SantanderCreditStatementParser(2015, true);
        date = parser.parseDate("5th Dec");
        assertEquals(5, date.getDayOfMonth());
        assertEquals(12, date.getMonthOfYear());
        assertEquals(2014, date.getYear());

        date = parser.parseDate("17th Aug");
        assertEquals(17, date.getDayOfMonth());
        assertEquals(8, date.getMonthOfYear());
        assertEquals(2015, date.getYear());

        date = parser.parseDate("1st Feb");
        assertEquals(1, date.getDayOfMonth());
        assertEquals(2, date.getMonthOfYear());
        assertEquals(2015, date.getYear());

        date = parser.parseDate("22nd Jun");
        assertEquals(22, date.getDayOfMonth());
        assertEquals(6, date.getMonthOfYear());
        assertEquals(2015, date.getYear());

        date = parser.parseDate("23rd Jan");
        assertEquals(23, date.getDayOfMonth());
        assertEquals(1, date.getMonthOfYear());
        assertEquals(2015, date.getYear());

        parser = new SantanderCreditStatementParser(2015, false);
        date = parser.parseDate("5th Dec");
        assertEquals(5, date.getDayOfMonth());
        assertEquals(12, date.getMonthOfYear());
        assertEquals(2015, date.getYear());
    }

    @Test
    public void testValidateExpenseLines() {
        check_not_valid("hello");
        check_not_valid("hello 5th Dec hello again 21.00");
        check_not_valid("5th Dec hello");
        check_not_valid("5th Dec hello hello");
        check_not_valid("5th hello Dec hello again 21.00");
        check_not_valid("hello 21.00");
        check_not_valid("5th Dec  something hello 21.00 hello again");

        int expectYear = DateTime.now().getYear();
        check_valid("5th Dec  something hello 21.00", 5, 12, expectYear, 21.00);
        check_valid("5th Dec  something h ello   21.00    ", 5, 12, expectYear, 21.00);
        check_valid("22nd Dec Kindle Svcs*m224a7b74 353-12477661 LU 0.49", 22, 12, expectYear, 0.49);
        check_valid("23rd Dec Mac Hotels Limited 856.05", 23, 12, expectYear, 856.05);
        check_valid("23rd Dec Mac Hotels Limited 1,856.05", 23, 12, expectYear, 1856.05);
        check_valid("23rd Dec Mac Hotels Limited CR 1,856.05", 23, 12, expectYear, -1856.05);
    }

    private void check_not_valid(String line) {
        assertFalse(parser.isValidExpenseLine(line));
    }

    private void check_valid(String line, int expectDay, int expectMonth, int expectYear, Double expectValue) {
        try {
            Expense expense = parser.parseLine(line);
            assertEquals(expectDay, expense.getDate().getDayOfMonth());
            assertEquals(expectMonth, expense.getDate().getMonthOfYear());
            assertEquals(expectYear, expense.getDate().getYear());
            assertEquals(expectValue, expense.getAmount());
        } catch (NotValidExpenseException e) {
            fail(String.format("Line could not be parsed: %s", line));
        }
    }
}
