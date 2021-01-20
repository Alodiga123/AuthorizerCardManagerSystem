package com.alodiga.authorizer.cms.ws.test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class ValidationMessagesHandler {

	public static boolean messageExists(String message,
			ConstraintViolationException e) {
		Set<ConstraintViolation<?>> validations = e.getConstraintViolations();
		for (ConstraintViolation<?> v : validations) {
			if (v.getMessage().toUpperCase().equals(message)) {
				return true;
			}
		}
		return false;
	}
}
