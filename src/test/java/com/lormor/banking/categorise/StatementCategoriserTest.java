package com.lormor.banking.categorise;

import com.google.common.collect.LinkedListMultimap;
import com.lormor.banking.expense.Expense;
import com.lormor.banking.expense.ImmutableExpense;
import com.lormor.banking.expense.rules.ExpenseRules;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class StatementCategoriserTest {

    private static final String MISC_DIR = "misc";
    private static final String SIMPLE_PDF = MISC_DIR + File.separator + "example.pdf";

    private static final String SAMPLE_STATEMENT_DIR = "samples";
    private static final String SAMPLE_STATEMENT_1 = SAMPLE_STATEMENT_DIR + File.separator + "sample-1.pdf";
    private static final String SAMPLE_STATEMENT_2 = SAMPLE_STATEMENT_DIR + File.separator + "sample-2.pdf";

    private static final String TEST_CATEGORY = "test-category";

    private LinkedListMultimap<String, Function<Expense, Boolean>> rules;

    private DefaultStatementCategoriser categoriser;

    @Test
    public void categorise_simple_file() {
        File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SIMPLE_PDF)).getFile());
        LinkedListMultimap<String, Function<Expense, Boolean>> rules = LinkedListMultimap.create();
        categoriser = (DefaultStatementCategoriser) StatementCategorisers.create(rules);

        CategoriseResult result = categoriser.categoriseExpenses(testFile);
        assertEquals(0, result.getSkippedFiles().size());
        assertEquals(0, result.getCategorisedFiles().size());
    }

    @Before
    public void setup() {
        rules = LinkedListMultimap.create();
    }

    @Test
    public void simple_rule_match() {
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        categoriser = (DefaultStatementCategoriser) StatementCategorisers.create(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));

        Expense nonMatching = ImmutableExpense.builder().amount(1.0).build();
        assertEquals(StatementCategorisers.CATEGORY_UNCATEGORISED, categoriser.categoriseExpense(nonMatching));
    }

    @Test
    public void rule_ordering_preserved() {
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        rules.put("some other category", ExpenseRules.amountMatchesRule(10.0));
        rules.put("another category", ExpenseRules.amountMatchesRule(10.0));
        rules.put("yet another category", ExpenseRules.amountMatchesRule(10.0));
        categoriser = (DefaultStatementCategoriser) StatementCategorisers.create(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));
    }

    @Test
    public void ordering_preserved_if_keys_match() {
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        rules.put(TEST_CATEGORY, ExpenseRules.throwExceptionRule());
        categoriser = (DefaultStatementCategoriser) StatementCategorisers.create(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));
    }

    // Same as above but check our test logic works by switching the order
    @Test(expected = RuntimeException.class)
    public void ordering_preserved_if_keys_match_reversed() {
        rules.put(TEST_CATEGORY, ExpenseRules.throwExceptionRule());
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        categoriser = (DefaultStatementCategoriser) StatementCategorisers.create(rules);

        Expense matching = ImmutableExpense.builder().amount(10.0).build();
        assertEquals(TEST_CATEGORY, categoriser.categoriseExpense(matching));
    }

    @Test
    public void categorise_valid_pdf_sample() {
        File sample = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SAMPLE_STATEMENT_1)).getFile());
        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        categoriser = (DefaultStatementCategoriser) StatementCategorisers.create(rules);
        CategoriseResult result = categoriser.categoriseExpenses(sample);

        assertEquals(1, result.getCategorisedExpenses().keySet().size());

        Collection<String> categories = result.getFileCategories(sample.getName());
        assertEquals(2, categories.size());

        assertEquals(1, result.getFileExpenses(sample.getName(), StatementCategorisers.CATEGORY_UNCATEGORISED).size());
        assertEquals(1, result.getFileExpenses(sample.getName(), TEST_CATEGORY).size());
    }

    @Test
    public void categorise_valid_pdf_samples() {
        File sampleDir = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SAMPLE_STATEMENT_DIR)).getFile());
        File sample1 = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SAMPLE_STATEMENT_1)).getFile());
        File sample2 = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SAMPLE_STATEMENT_2)).getFile());

        rules.put(TEST_CATEGORY, ExpenseRules.amountMatchesRule(10.0));
        categoriser = (DefaultStatementCategoriser) StatementCategorisers.create(rules);
        CategoriseResult result = categoriser.categoriseExpenses(sampleDir);

        assertEquals(2, result.getCategorisedFiles().size());

        Collection<String> categories = result.getFileCategories(sample1.getName());
        assertEquals(2, categories.size());
        assertEquals(1, result.getFileExpenses(sample1.getName(), StatementCategorisers.CATEGORY_UNCATEGORISED).size());
        assertEquals(1, result.getFileExpenses(sample1.getName(), TEST_CATEGORY).size());

        categories = result.getFileCategories(sample2.getName());
        assertEquals(2, categories.size());
        assertEquals(1, result.getFileExpenses(sample2.getName(), StatementCategorisers.CATEGORY_UNCATEGORISED).size());
        assertEquals(3, result.getFileExpenses(sample2.getName(), TEST_CATEGORY).size());
    }
}
