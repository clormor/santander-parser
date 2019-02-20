package com.lormor.banking.statements;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ImmutableExpense;
import com.lormor.banking.expense.NotValidExpenseException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Parses a row of a credit card statement
 */
class SantanderCreditStatementParser extends AbstractStatementParser {

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

    @Override
    protected Expense parseLine(String line) throws NotValidExpenseException {
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

    @VisibleForTesting
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
}
