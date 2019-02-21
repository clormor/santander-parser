package com.lormor.banking.parse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.NotValidExpenseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

abstract class AbstractStatementParser implements StatementParser {

    protected abstract Expense parseLine(String line) throws NotValidExpenseException;

    @VisibleForTesting
    List<String> getLinesFromFile(File file) throws NotValidStatementException {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            PDDocument doc = PDDocument.load(file);
            stripper.getText(doc);
            String text = stripper.getText(doc);
            doc.close();
            String lines[] = text.split("\\r?\\n");
            return Lists.newArrayList(lines);
        } catch (IOException e) {
            throw new NotValidStatementException(e);
        }
    }

    private ImmutableParseResult parseFile(File file) {
        Multimap<String, Expense> results = LinkedListMultimap.create();
        Set<String> skippedFiles = Sets.newLinkedHashSet();

        try {
            List<Expense> expenses = parse(getLinesFromFile(file));
            results.putAll(file.getName(), expenses);
        } catch (NotValidStatementException e) {
            skippedFiles.add(file.getName());
        }

        return ImmutableParseResult
                .builder()
                .addAllSkippedFiles(skippedFiles)
                .putAllExpenses(results)
                .build();
    }

    @Override
    public ImmutableParseResult parseExpenses(File file) {

        if (file.isFile()) {
            return parseFile(file);
        }

        Multimap<String, Expense> expenses = LinkedListMultimap.create();
        Set<String> skippedFiles = Sets.newLinkedHashSet();

        File[] children = Objects.requireNonNull(file.listFiles());
        for (File child : children) {
            ImmutableParseResult partialResult = parseFile(child);
            expenses.putAll(partialResult.getExpenses());
            skippedFiles.addAll(partialResult.getSkippedFiles());
        }

        return ImmutableParseResult
                .builder()
                .putAllExpenses(expenses)
                .addAllSkippedFiles(skippedFiles)
                .build();
    }

    @VisibleForTesting
    List<Expense> parse(List<String> lines) {
        List<Expense> result = Lists.newArrayList();

        for (String line : lines) {
            try {
                Expense expense = parseLine(line);
                result.add(expense);
            } catch (NotValidExpenseException e) {
                // skip line
            }
        }

        return result;
    }

}
