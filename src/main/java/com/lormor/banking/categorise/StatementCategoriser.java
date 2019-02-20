package com.lormor.banking.categorise;

import java.io.File;

public interface StatementCategoriser {

    CategorisedResult categoriseExpenses(File file);

}
