package com.lormor.banking.parse;

public class StatementParsers {

    private StatementParsers() {
        // utility class
    }

    public static StatementParser santanderCreditCardStatementParser() {
        return new SantanderCreditStatementParser();
    }

}
