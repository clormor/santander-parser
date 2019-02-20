package com.lormor.banking.statements;

public class NotValidStatementException extends RuntimeException {

    public NotValidStatementException(Exception e) {
        super(e);
    }

}
