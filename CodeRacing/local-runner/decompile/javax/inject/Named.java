package javax.inject;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Named
{
  String value() default "";
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\javax\inject\Named.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */