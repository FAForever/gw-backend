package com.faforever.gw.websocket;

import java.lang.annotation.*;

/**
 * Annotation for mapping any WebSocket message envelope
 * to the right action handler
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActionMapping {
    String value();
}
