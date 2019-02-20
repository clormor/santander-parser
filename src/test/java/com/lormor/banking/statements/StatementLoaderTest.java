package com.lormor.banking.statements;

import com.lormor.banking.expense.Expense;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StatementLoaderTest {

    private static final String EXAMPLE_DIR = "examples";
    private static final String EXAMPLE_PDF = EXAMPLE_DIR + File.separator + "example.pdf";
    private static final String EXAMPLE_JPG = EXAMPLE_DIR + File.separator + "example.jpg";
    private static final String EXAMPLE_TXT = EXAMPLE_DIR + File.separator + "example.txt";

    private StatementLoader loader;

    @Before
    public void setup() {
    }

    @Test
    public void load_simple_pdf() {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_PDF).getFile());
        loader = new StatementLoader();
        List<String> lines = loader.getLinesFromFile(testFile);
        assertEquals(1, lines.size());
    }

    @Test(expected = NotValidStatementException.class)
    public void load_image_file() {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_JPG).getFile());
        loader = new StatementLoader();
        loader.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test(expected = NotValidStatementException.class)
    public void load_text_file() {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_TXT).getFile());
        loader = new StatementLoader();
        loader.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test(expected = NotValidStatementException.class)
    public void load_directory_as_file() {
        File testFile = new File(getClass().getClassLoader().getResource(EXAMPLE_DIR).getFile());
        loader = new StatementLoader();
        loader.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test
    public void load_pdfs_from_directory() {
        File testDir = new File(getClass().getClassLoader().getResource(EXAMPLE_DIR).getFile());
        loader = new StatementLoader();
        List<File> files = loader.loadPdfFilesFromDirectory(testDir);
        assertEquals(2, files.size());
    }

    @Test
    public void process_simple_pdf() {
        File testDir = new File(getClass().getClassLoader().getResource(EXAMPLE_DIR).getFile());
        loader = new StatementLoader();
        List<File> files = loader.loadPdfFilesFromDirectory(testDir);
        List<Expense> expenses = loader.processExpenses(files.get(0), new SantanderCreditStatementParser());
        assertEquals(0, expenses.size());
    }

}
