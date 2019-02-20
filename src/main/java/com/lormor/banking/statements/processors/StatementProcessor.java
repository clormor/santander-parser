package com.lormor.banking.statements.processors;

import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ExpenseCategoriser;
import com.lormor.banking.statements.parsers.StatementParser;

import java.io.File;
import java.util.List;

public interface StatementProcessor {

    StatementProcessingResult processExpenses(File file, StatementParser parser);

    Multimap<String, Expense> categoriseExpenses(List<Expense> expenses, ExpenseCategoriser categoriser);
}
