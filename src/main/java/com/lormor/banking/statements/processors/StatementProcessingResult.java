package com.lormor.banking.statements.processors;

import com.lormor.banking.expense.Expense;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatementProcessingResult {

    private final Map<String, List<Expense>> processedFiles;
    private final Set<String> skippedFiles;

    public StatementProcessingResult(Map<String, List<Expense>> result, Set<String> skippedFiles) {
        this.processedFiles = result;
        this.skippedFiles = skippedFiles;
    }

    public Map<String, List<Expense>> getProcessedFiles() {
        return processedFiles;
    }

    public Set<String> getSkippedFiles() {
        return skippedFiles;
    }
}
