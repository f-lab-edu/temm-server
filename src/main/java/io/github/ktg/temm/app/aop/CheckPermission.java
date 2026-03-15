package io.github.ktg.temm.app.aop;

import io.github.ktg.temm.domain.model.Authorization;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {

    Authorization role() default Authorization.MEMBER;

    String storeId() default "";

    String productId() default "";
}
