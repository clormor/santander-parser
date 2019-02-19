package com.lormor.banking.santander;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ImmutableExpense;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Parses a row of a credit card statement
 */
public class SantanderCreditStatementParser {

    private static final String CREDIT_PREFIX = "CR";
    // If a statement spans 2 years, then any December dates occur in the previous year
    private final boolean wrapYear;
    private final int year;
    private final DateTimeFormatter ndFormatter;
    private final DateTimeFormatter rdFormatter;
    private final DateTimeFormatter stFormatter;
    private final DateTimeFormatter thFormatter;
    private final NumberFormat format = NumberFormat.getInstance(Locale.UK);

    public SantanderCreditStatementParser() {
        this(DateTime.now().getYear(), false);
    }

    public SantanderCreditStatementParser(int year, boolean wrapYear) {
        this.year = year;
        this.wrapYear = wrapYear;
        ndFormatter = DateTimeFormat.forPattern("d'nd' MMM").withDefaultYear(year);
        rdFormatter = DateTimeFormat.forPattern("d'rd' MMM").withDefaultYear(year);
        stFormatter = DateTimeFormat.forPattern("d'st' MMM").withDefaultYear(year);
        thFormatter = DateTimeFormat.forPattern("d'th' MMM").withDefaultYear(year);
    }

    public List<Expense> parse(List<String> lines) {
        List<Expense> result = Lists.newArrayList();

        for (String line : lines) {

            try {
                Expense expense = parseLine(line);
                result.add(expense);
            } catch (NotValidExpenseException e) {
                // TODO continue or break?
                continue;
            }
        }
        return result;
    }

    Expense parseLine(String line) throws NotValidExpenseException {
        if (Strings.isNullOrEmpty(line)) {
            throw new NotValidExpenseException();
        }

        line = line.trim();
        int descStart = line.indexOf(" ", line.indexOf(" ", 0) + 1) + 1;
        int descEnd = line.lastIndexOf(" ");

        try {
            DateTime date = parseDate(line.substring(0, descStart).trim());
            String description = line.substring(descStart, descEnd).trim();
            Double amount = format.parse(line.substring(descEnd + 1)).doubleValue();

            if (description.endsWith(CREDIT_PREFIX)) {
                description = description.substring(0, description.lastIndexOf(" "));
                amount *= -1;
            }

            return ImmutableExpense.builder()
                    .date(date)
                    .description(description)
                    .amount(amount)
                    .build();

        } catch (RuntimeException | ParseException e) {
            throw new NotValidExpenseException();
        }
    }

    DateTime parseDate(String date) {
        DateTime result;

        try {
            result = thFormatter.parseDateTime(date);
        } catch (IllegalArgumentException e1) {
            try {
                result = stFormatter.parseDateTime(date);
            } catch (IllegalArgumentException e2) {
                try {
                    result = ndFormatter.parseDateTime(date);
                } catch (IllegalArgumentException e3) {
                    result = rdFormatter.parseDateTime(date);
                }
            }
        }

        if (wrapYear && result.getMonthOfYear() == 12) {
            return result.minusYears(1);
        } else {
            return result;
        }
    }

    public boolean isValidExpenseLine(String line) {
        try {
            parseLine(line);
            return true;
        } catch (NotValidExpenseException e) {
            return false;
        }
    }
}
