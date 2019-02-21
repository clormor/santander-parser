package com.lormor.banking.parse;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StatementParserTest {

    private static final String MISC_DIR = "misc";
    private static final String SIMPLE_PDF = MISC_DIR + File.separator + "example.pdf";
    private static final String SIMPLE_JPG = MISC_DIR + File.separator + "example.jpg";
    private static final String SIMPLE_TXT = MISC_DIR + File.separator + "example.txt";

    private static final String SAMPLE_STATEMENT_DIR = "samples";
    private static final String SAMPLE_STATEMENT_1 = SAMPLE_STATEMENT_DIR + File.separator + "sample-1.pdf";

    private AbstractStatementParser parser;

    @Before
    public void setup() {
        parser = (AbstractStatementParser) StatementParsers.santanderCreditCardStatementParser();
    }

    @Test
    public void load_simple_pdf() throws NotValidStatementException {
        File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SIMPLE_PDF)).getFile());
        List<String> lines = parser.getLinesFromFile(testFile);
        assertEquals(1, lines.size());
    }

    @Test(expected = NotValidStatementException.class)
    public void load_image_file() throws NotValidStatementException {
        File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SIMPLE_JPG)).getFile());
        parser.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test(expected = NotValidStatementException.class)
    public void load_text_file() throws NotValidStatementException {
        File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SIMPLE_TXT)).getFile());
        parser.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test(expected = NotValidStatementException.class)
    public void load_directory_as_file() throws NotValidStatementException {
        File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(MISC_DIR)).getFile());
        parser.getLinesFromFile(testFile);
        fail("Expected NotValidStatementException when opening non-pdf files");
    }

    @Test
    public void parse_simple_pdf() {
        File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SIMPLE_PDF)).getFile());
        ImmutableParseResult result = parser.parseExpenses(testFile);
        assertEquals(0, result.getSkippedFiles().size());
        assertEquals(0, result.getExpenses().size());
    }

    @Test
    public void parse_directory() {
        File testDir = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(MISC_DIR)).getFile());
        ImmutableParseResult result = parser.parseExpenses(testDir);
        assertEquals(2, result.getSkippedFiles().size());
        assertEquals(0, result.getExpenses().size());
    }

    @Test
    public void parse_valid_sample() {
        File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SAMPLE_STATEMENT_1)).getFile());
        ImmutableParseResult result = parser.parseExpenses(testFile);
        assertEquals(0, result.getSkippedFiles().size());
        assertEquals(2, result.getExpenses().get(testFile.getName()).size());
    }
}
