package com.lormor.banking.categorise;

import java.io.File;

public interface StatementCategoriser {

    CategoriseResult categoriseExpenses(File file);

}
