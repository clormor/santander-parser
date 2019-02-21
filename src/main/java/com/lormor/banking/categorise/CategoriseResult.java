package com.lormor.banking.categorise;

import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
public abstract class CategoriseResult {

    public abstract Set<String> getSkippedFiles();

    abstract Map<String, Multimap<String, Expense>> getCategorisedExpenses();

    @Value.Lazy
    public Collection<String> getCategorisedFiles() {
        return getCategorisedExpenses().keySet();
    }

    @Value.Lazy
    public Collection<String> getFileCategories(String file) {
        return getCategorisedExpenses().get(file).keySet();
    }

    @Value.Lazy
    public Collection<Expense> getFileExpenses(String file, String category) {
        return getCategorisedExpenses().get(file).get(category);
    }
}
