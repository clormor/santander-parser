package com.lormor.banking.statements;

import com.lormor.banking.expense.Expense;

import java.util.List;

public interface StatementParser {

    List<Expense> parse(List<String> lines);

}
