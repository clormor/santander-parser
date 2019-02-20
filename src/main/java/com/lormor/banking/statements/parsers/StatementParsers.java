package com.lormor.banking.statements.parsers;

public class StatementParsers {

    public static StatementParser santanderCreditCardStatementParser() {
        return new SantanderCreditStatementParser();
    }

}
