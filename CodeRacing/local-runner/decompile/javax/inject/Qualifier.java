package javax.inject;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\javax\inject\Qualifier.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */