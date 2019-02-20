package com.lormor.banking.statements.processors;

public class StatementProcessors {

    public static final StatementProcessor create() {
        return new DefaultStatementProcessor();
    }

}

