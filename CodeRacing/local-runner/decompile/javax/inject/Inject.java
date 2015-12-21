package javax.inject;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\javax\inject\Inject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */