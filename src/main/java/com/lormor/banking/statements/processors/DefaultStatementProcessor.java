package com.lormor.banking.statements.processors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ExpenseCategoriser;
import com.lormor.banking.statements.NotValidStatementException;
import com.lormor.banking.statements.parsers.StatementParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DefaultStatementProcessor implements StatementProcessor {

    @VisibleForTesting
    public List<String> getLinesFromFile(File file) throws NotValidStatementException {
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

    private StatementProcessingResult processFile(File file, StatementParser parser, StatementProcessingResult partialResult) {
        Map<String, List<Expense>> processedFiles = partialResult.getProcessedFiles();
        Set<String> skippedFiles = partialResult.getSkippedFiles();

        try {
            List<Expense> parse = parser.parse(getLinesFromFile(file));
            processedFiles.put(file.getName(), parse);
        } catch (NotValidStatementException e) {
            skippedFiles.add(file.getName());
        }

        return new StatementProcessingResult(processedFiles, skippedFiles);
    }

    @Override
    public StatementProcessingResult processExpenses(File file, StatementParser parser) {
        StatementProcessingResult result = new StatementProcessingResult(Maps.newLinkedHashMap(), Sets.newLinkedHashSet());

        if (file.isDirectory()) {
            List<File> files = Lists.newArrayList(file.listFiles());

            for (File child : files) {
                result = processFile(child, parser, result);
            }

        } else {
            result = processFile(file, parser, result);
        }

        return result;
    }

    @Override
    public Multimap<String, Expense> categoriseExpenses(List<Expense> expenses, ExpenseCategoriser categoriser) {
        return null;
    }
}
