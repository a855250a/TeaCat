package com.teacat.exception;

import com.teacat.entity.Pet;

public class PetNotFoundException extends RuntimeException{
    public PetNotFoundException(String message) {
        super(message);
    }
}
