package com.lormor.banking.parse;

import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
public abstract class ParseResult {

    abstract Multimap<String, Expense> getExpenses();

    public abstract Set<String> getSkippedFiles();

    @Value.Lazy
    public Collection<String> getParsedFiles() {
        return getExpenses().keySet();
    }

    @Value.Lazy
    public Collection<Expense> getFileExpenses(String file) {
        return getExpenses().get(file);
    }
}
