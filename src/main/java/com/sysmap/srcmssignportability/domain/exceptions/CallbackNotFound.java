package com.sysmap.srcmssignportability.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CallbackNotFound extends RuntimeException {

    public CallbackNotFound(String message){
        super(message);
    }
}
