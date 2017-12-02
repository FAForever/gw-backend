package com.faforever.gw.messaging.client;

import java.lang.annotation.*;

/**
 * Annotation for mapping any WebSocket message envelope
 * to the right action handler
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActionMapping {
    Class<?> value();
}
