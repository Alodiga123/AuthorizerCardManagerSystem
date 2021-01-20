package com.alodiga.authorizer.cms.validations;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckAgeValidator implements ConstraintValidator<CheckAge, Date> {

	private int minAge, maxAge;

	public void initialize(CheckAge constraintAnnotation) {
		this.minAge = constraintAnnotation.minAge();
		this.maxAge = constraintAnnotation.maxAge();
	}

	@SuppressWarnings("deprecation")
	public boolean isValid(Date object,
			ConstraintValidatorContext constraintContext) {
		if (object == null) {
			return true;
		} else {
			Date now = new Date();
			int currentYear = now.getYear();
			int objectYear = object.getYear();
			int yearDifference = currentYear - objectYear;
			if (yearDifference > this.minAge && yearDifference < this.maxAge) {
				return true;
			} else {
				int currentMonth = now.getMonth();
				int objectMonth = object.getMonth();
				int currentDay = now.getDate();
				int objectDay = object.getDate();
				if (yearDifference == this.minAge) {
					if (objectMonth < currentMonth) {
						return true;
					} else {
						if (objectMonth == currentMonth) {
							return (objectDay <= currentDay);
						} else {
							return false;
						}
					}
				} else if (yearDifference == this.maxAge) {
					if (objectMonth > currentMonth) {
						return true;
					} else {
						if (objectMonth == currentMonth) {
							return (objectDay > currentDay);
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			}
		}
	}
}
