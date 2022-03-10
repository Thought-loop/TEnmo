package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class FraudulentTransferException extends Exception{

    private static final long serialVersionUID = 1L;

    public FraudulentTransferException(){ super("Attempted transfer sender does not match logged in user!");}

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
