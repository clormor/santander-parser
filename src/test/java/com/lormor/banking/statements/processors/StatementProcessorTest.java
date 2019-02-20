package com.lormor.banking.statements.processors;

import com.google.common.collect.Iterables;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.statements.NotValidStatementException;
import com.lormor.banking.statements.parsers.StatementParsers;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StatementProcessorTest {

    private static final String EXAMPLE_DIR = "examples";
    private static final String EXAMPLE_PDF = EXAMPLE_DIR + File.separator + "example.pdf";
    private static final String EXAMPLE_JPG = EXAMPLE_DIR + File.separator + "example.jpg";
    private static final String EXAMPLE_TXT = EXAMPLE_DIR + File.separator + "example.txt";

    private DefaultStatementProcessor loader;

    @Before
    public void setup() {
        loader = (DefaultStatementProcessor) StatementProcessors.create();
    }

    @Test
    public void load_simple_pdf() throws NotValidStatementException {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_PDF).getFile());
        List<String> lines = loader.getLinesFromFile(testFile);
        assertEquals(1, lines.size());
    }

    @Test(expected = NotValidStatementException.class)
    public void load_image_file() throws NotValidStatementException {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_JPG).getFile());
        loader.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test(expected = NotValidStatementException.class)
    public void load_text_file() throws NotValidStatementException {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_TXT).getFile());
        loader.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test(expected = NotValidStatementException.class)
    public void load_directory_as_file() throws NotValidStatementException {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_DIR).getFile());
        loader.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test
    public void process_simple_pdf() {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_PDF).getFile());
        StatementProcessingResult result = loader.processExpenses(testFile, StatementParsers.santanderCreditCardStatementParser());
        assertEquals(0, result.getSkippedFiles().size());
        assertEquals(1, result.getProcessedFiles().size());
        assertEquals(1, result.getProcessedFiles().keySet().size());

        String key = Iterables.getOnlyElement(result.getProcessedFiles().keySet());
        List<Expense> expenses = result.getProcessedFiles().get(key);
        assertEquals(0, expenses.size());
    }

    @Test
    public void process_directory() {
        File testDir = new File(getClass().getClassLoader().getResource(EXAMPLE_DIR).getFile());
        StatementProcessingResult result = loader.processExpenses(testDir, StatementParsers.santanderCreditCardStatementParser());
        assertEquals(2, result.getSkippedFiles().size());
        assertEquals(2, result.getProcessedFiles().size());
        assertEquals(2, result.getProcessedFiles().keySet().size());

        String key = Iterables.getFirst(result.getProcessedFiles().keySet(), "error");
        List<Expense> expenses = result.getProcessedFiles().get(key);
        assertEquals(0, expenses.size());
    }

}
