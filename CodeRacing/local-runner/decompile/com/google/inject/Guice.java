package com.google.inject;

import com.google.inject.internal.InternalInjectorCreator;
import java.util.Arrays;

public final class Guice
{
  public static Injector createInjector(Module... paramVarArgs)
  {
    return createInjector(Arrays.asList(paramVarArgs));
  }
  
  public static Injector createInjector(Iterable paramIterable)
  {
    return createInjector(Stage.DEVELOPMENT, paramIterable);
  }
  
  public static Injector createInjector(Stage paramStage, Module... paramVarArgs)
  {
    return createInjector(paramStage, Arrays.asList(paramVarArgs));
  }
  
  public static Injector createInjector(Stage paramStage, Iterable paramIterable)
  {
    return new InternalInjectorCreator().stage(paramStage).addModules(paramIterable).build();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\Guice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */