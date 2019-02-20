package com.lormor.banking.statements;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.lormor.banking.expense.Expense;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

public class StatementLoader {

    private static final FileFilter PDF_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            return pathname.getName().substring(name.length() - 3, name.length()).equalsIgnoreCase("pdf");
        }
    };

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

    public List<File> loadPdfFilesFromDirectory(File directory) {
        File[] files = directory.listFiles(PDF_FILE_FILTER);
        return Lists.newArrayList(files);
    }

    public List<Expense> processExpenses(File file, StatementParser parser) {
        return parser.parse(getLinesFromFile(file));
    }
}
