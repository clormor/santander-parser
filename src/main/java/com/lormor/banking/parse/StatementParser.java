package com.lormor.banking.parse;

import java.io.File;

public interface StatementParser {

    ParsedResult parseExpenses(File file);

}
