package com.lormor.banking.statements.parsers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.NotValidExpenseException;

import java.util.List;

abstract class AbstractStatementParser implements StatementParser {

    @Override
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

    @VisibleForTesting
    boolean isValidExpenseLine(String line) {
        try {
            parseLine(line);
            return true;
        } catch (NotValidExpenseException e) {
            return false;
        }
    }

    protected abstract Expense parseLine(String line) throws NotValidExpenseException;

}
