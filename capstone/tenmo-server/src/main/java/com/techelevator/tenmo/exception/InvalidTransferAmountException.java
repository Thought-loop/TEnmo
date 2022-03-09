package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class InvalidTransferAmountException extends Exception{
    private static final long serialVersionUID = 1L;

    public InvalidTransferAmountException(){ super("invalid transfer amount (negative or insufficient balance)");}

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
