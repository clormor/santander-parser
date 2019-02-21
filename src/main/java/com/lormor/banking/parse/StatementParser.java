package com.lormor.banking.parse;

import java.io.File;

public interface StatementParser {

    ParseResult parseExpenses(File file);

}
