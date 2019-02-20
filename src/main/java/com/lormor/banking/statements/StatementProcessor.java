package com.lormor.banking.statements;

import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ExpenseCategoriser;

import java.io.File;
import java.util.List;

public interface StatementProcessor {

    List<File> loadPdfFilesFromDirectory(File directory);

    List<Expense> processExpenses(File file, StatementParser parser);

    Multimap<String, Expense> categoriseExpenses(List<Expense> expenses, ExpenseCategoriser categoriser);
}
