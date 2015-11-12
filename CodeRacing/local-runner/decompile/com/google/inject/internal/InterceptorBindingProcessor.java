package com.google.inject.internal;

import com.google.inject.spi.InterceptorBinding;

final class InterceptorBindingProcessor
  extends AbstractProcessor
{
  InterceptorBindingProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  public Boolean visit(InterceptorBinding paramInterceptorBinding)
  {
    this.injector.state.addMethodAspect(new MethodAspect(paramInterceptorBinding.getClassMatcher(), paramInterceptorBinding.getMethodMatcher(), paramInterceptorBinding.getInterceptors()));
    return Boolean.valueOf(true);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InterceptorBindingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */