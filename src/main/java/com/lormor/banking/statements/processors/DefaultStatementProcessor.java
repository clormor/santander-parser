package com.lormor.banking.statements.processors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ExpenseCategoriser;
import com.lormor.banking.statements.NotValidStatementException;
import com.lormor.banking.statements.parsers.StatementParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

class DefaultStatementProcessor implements StatementProcessor {

    private static final FileFilter PDF_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            return pathname.getName().substring(name.length() - 3, name.length()).equalsIgnoreCase("pdf");
        }
    };

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

    @VisibleForTesting
    public List<File> loadPdfFilesFromDirectory(File directory) {
        File[] files = directory.listFiles(PDF_FILE_FILTER);
        return Lists.newArrayList(files);
    }

    private List<Expense> processFile(File file, StatementParser parser) {
        try {
           return parser.parse(getLinesFromFile(file));
        } catch (NotValidStatementException e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public List<Expense> processExpenses(File file, StatementParser parser) {
        if (file.isDirectory()) {
            List<File> files = loadPdfFilesFromDirectory(file);

            List<Expense> result = Lists.newArrayList();
            for (File child : files) {
                result.addAll(processFile(child, parser));
            }

            return result;
        } else {
            return processFile(file, parser);
        }
    }

    @Override
    public Multimap<String, Expense> categoriseExpenses(List<Expense> expenses, ExpenseCategoriser categoriser) {
        return null;
    }
}
