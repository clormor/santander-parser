package com.lormor.banking.statements;

public class NotValidStatementException extends Exception {

    public NotValidStatementException(Exception e) {
        super(e);
    }

}
