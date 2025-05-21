package com.holiday.system.exception;

import java.io.Serial;

public class MandatoryFieldException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = 332944443682380785L;

	public MandatoryFieldException(String message) {
        super(message);
    }
}