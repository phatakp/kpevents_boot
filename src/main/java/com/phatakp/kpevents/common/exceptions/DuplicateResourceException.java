package com.phatakp.kpevents.common.exceptions;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException{

    private final String resourceName;
    private final Object identifier;

    public DuplicateResourceException(String resourceName, Object identifier) {
        super(resourceName + " already exists: "+ identifier);
        this.resourceName = resourceName;
        this.identifier = identifier;
    }
}
