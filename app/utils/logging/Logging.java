package utils.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import play.mvc.With;

/**
 * Methode um den aufrufenden Kontext zu loggen.
 * @author Max Wick
 *
 */
@With({LoggingAction.class})
@Target({ java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

}
