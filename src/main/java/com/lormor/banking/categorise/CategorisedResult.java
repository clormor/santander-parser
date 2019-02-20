package com.lormor.banking.categorise;

import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Set;

@Value.Immutable
public interface CategorisedResult {

    Set<String> getSkippedFiles();

    Map<String, Multimap<String, Expense>> getCategorisedExpenses();
}
