package com.lormor.banking.parse;

public class StatementParsers {

    public static StatementParser santanderCreditCardStatementParser() {
        return new SantanderCreditStatementParser();
    }

}
