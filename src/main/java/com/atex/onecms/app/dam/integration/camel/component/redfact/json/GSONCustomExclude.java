package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allow you to exclude a field from serialization/deserialization.
 *
 * @author mnova
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GSONCustomExclude {
}
