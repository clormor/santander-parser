package com.lormor.banking.parse;

import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
public interface ParsedResult {

    Multimap<String, Expense> getExpenses();

    Set<String> getSkippedFiles();
}
