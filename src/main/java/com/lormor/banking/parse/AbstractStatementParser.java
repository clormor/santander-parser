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

    private ParsedResult parseFile(File file) {
        Multimap<String, Expense> results = LinkedListMultimap.create();
        Set<String> skippedFiles = Sets.newLinkedHashSet();

        try {
            List<Expense> expenses = parse(getLinesFromFile(file));
            results.putAll(file.getName(), expenses);
        } catch (NotValidStatementException e) {
            skippedFiles.add(file.getName());
        }

        return ImmutableParsedResult
                .builder()
                .addAllSkippedFiles(skippedFiles)
                .putAllExpenses(results)
                .build();
    }

    @Override
    public ParsedResult parseExpenses(File file) {

        if (file.isDirectory()) {
            Multimap<String, Expense> expenses = LinkedListMultimap.create();
            Set<String> skippedFiles = Sets.newLinkedHashSet();

            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    ParsedResult partialResult = parseFile(child);
                    expenses.putAll(partialResult.getExpenses());
                    skippedFiles.addAll(partialResult.getSkippedFiles());
                }
            }
            return ImmutableParsedResult
                    .builder()
                    .putAllExpenses(expenses)
                    .addAllSkippedFiles(skippedFiles)
                    .build();
        } else {
            return parseFile(file);
        }
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
