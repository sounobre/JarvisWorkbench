package com.dnobretech.jarvisworkbench.shared.error;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message){
        super(message);
    }
}
