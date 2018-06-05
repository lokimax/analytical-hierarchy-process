package de.x132.user.actions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

/**
 * Annotation zum Markieren von Controller Action für die Authentifizierung des
 * Benutzers.
 * @author Max Wick
 *
 */
@With({ AuthenticationAction.class })
@Target({ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {

}
