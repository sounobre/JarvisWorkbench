package com.dnobretech.jarvisworkbench.shared.error;

public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }
}
