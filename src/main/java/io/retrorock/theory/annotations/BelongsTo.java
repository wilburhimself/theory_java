package io.retrorock.theory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BelongsTo {
    Class entity();
    String name();
    String fieldName() default "[unassigned]";
    String identifier() default "[unassigned]";
    String namePrefix() default "[unassigned]";
}
