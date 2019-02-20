package com.lormor.banking.statements.parsers;

import com.google.common.collect.Lists;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.NotValidExpenseException;
import com.lormor.banking.statements.parsers.SantanderCreditStatementParser;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SantanderCreditStatementParserTest {

    private static final int current_year = DateTime.now().getYear();

    private SantanderCreditStatementParser parser;

    @Before
    public void setup() {
        parser = new SantanderCreditStatementParser();
    }

    @Test
    public void parse_simple_line() throws NotValidExpenseException {
        String test = "7th Dec Harry Ramsdens Leeds GB 10.72";
        Expense expense = parser.parseLine(test);
        assertEquals((Double) 10.72, expense.getAmount());
        assertEquals("Harry Ramsdens Leeds GB", expense.getDescription());
        assertEquals(12, expense.getDate().getMonthOfYear());
        assertEquals(7, expense.getDate().getDayOfMonth());
        assertEquals(DateTime.now().getYear(), expense.getDate().getYear());
    }

    @Test
    public void parse_padded_line() throws NotValidExpenseException {
        String test = " 7th Dec Harry Ramsdens Leeds GB 10.72  ";
        Expense expense = parser.parseLine(test);
        assertEquals((Double) 10.72, expense.getAmount());
        assertEquals("Harry Ramsdens Leeds GB", expense.getDescription());
    }

    @Test
    public void parse_dates() {
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
    public void validate_lines() {
        check_not_valid(null);
        check_not_valid("");
        check_not_valid("   ");
        check_not_valid("hello");
        check_not_valid("hello 5th Dec hello again 21.00");
        check_not_valid("5th Dec hello");
        check_not_valid("5th Dec hello hello");
        check_not_valid("5th hello Dec hello again 21.00");
        check_not_valid("hello 21.00");
        check_not_valid("5th Dec  something hello 21.00 hello again");

        check_valid("5th Dec  something hello 21.00", 5, 12, current_year, 21.00);
        check_valid("5th Dec  something h ello   21.00    ", 5, 12, current_year, 21.00);
        check_valid("22nd Dec Kindle Svcs*m224a7b74 353-12477661 LU 0.49", 22, 12, current_year, 0.49);
        check_valid("23rd Dec Mac Hotels Limited 856.05", 23, 12, current_year, 856.05);
        check_valid("23rd Dec Mac Hotels Limited 1,856.05", 23, 12, current_year, 1856.05);
        check_valid("23rd Dec Mac Hotels Limited CR 1,856.05", 23, 12, current_year, -1856.05);
    }

    @Test
    public void parse_multiple_valid_lines() {
        List<String> lines = Lists.newArrayList();
        lines.add("5th Dec something 8.00");
        lines.add("23rd Feb another thing 21.38");
        lines.add("21st Jun and another thing 101.99");

        List<Expense> result = parser.parse(lines);
        assertEquals(3, result.size());
        check_valid_expense(result.get(0), 5, 12, current_year, 8.00);
        check_valid_expense(result.get(1), 23, 2, current_year, 21.38);
        check_valid_expense(result.get(2), 21, 6, current_year, 101.99);
    }

    @Test
    public void parse_multiple_lines_some_invalid() {
        List<String> lines = Lists.newArrayList();
        lines.add(null);
        lines.add("5th Dec something 8.00");
        lines.add(" ");
        lines.add("23rd Feb another thing 21.38");
        lines.add("not a date hello 31.80");
        lines.add("21st Jun and another thing 101.99");
        lines.add("no value at the end");

        List<Expense> result = parser.parse(lines);
        assertEquals(3, result.size());
        check_valid_expense(result.get(0), 5, 12, current_year, 8.00);
        check_valid_expense(result.get(1), 23, 2, current_year, 21.38);
        check_valid_expense(result.get(2), 21, 6, current_year, 101.99);
    }

    private void check_valid_expense(Expense expense, int expectDay, int expectMonth, int expectYear, Double expectValue) {
        assertEquals(expectDay, expense.getDate().getDayOfMonth());
        assertEquals(expectMonth, expense.getDate().getMonthOfYear());
        assertEquals(expectYear, expense.getDate().getYear());
        assertEquals(expectValue, expense.getAmount());
    }

    private void check_not_valid(String line) {
        assertFalse(parser.isValidExpenseLine(line));
    }

    private void check_valid(String line, int expectDay, int expectMonth, int expectYear, Double expectValue) {
        try {
            assertTrue(parser.isValidExpenseLine(line));
            Expense expense = parser.parseLine(line);
            check_valid_expense(expense, expectDay, expectMonth, expectYear, expectValue);
        } catch (NotValidExpenseException e) {
            fail(String.format("Line could not be parsed: %s", line));
        }
    }
}
