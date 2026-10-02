package com.teacat.exception;

public class InvalidSearchConditionException
        extends RuntimeException{

    public InvalidSearchConditionException(String message) {
        super(message);
    }
}
