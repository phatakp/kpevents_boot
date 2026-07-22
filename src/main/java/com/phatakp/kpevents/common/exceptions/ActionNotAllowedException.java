package com.phatakp.kpevents.common.exceptions;

import lombok.Getter;

@Getter
public class ActionNotAllowedException extends RuntimeException {
    private final String actionName;

    public ActionNotAllowedException(String actionName) {
        super(actionName +" not allowed: ");
        this.actionName = actionName;
    }
}
