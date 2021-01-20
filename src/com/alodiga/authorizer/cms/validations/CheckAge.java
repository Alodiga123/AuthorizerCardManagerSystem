package com.alodiga.authorizer.cms.validations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckAgeValidator.class)
@Documented
public @interface CheckAge {
	
	String message() default "{invalid.age}";
	int minAge() default 14;
	int maxAge() default 65;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
}
