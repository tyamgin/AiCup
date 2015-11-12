package com.google.inject.internal;

import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.TypeListenerBinding;

final class ListenerBindingProcessor
  extends AbstractProcessor
{
  ListenerBindingProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  public Boolean visit(TypeListenerBinding paramTypeListenerBinding)
  {
    this.injector.state.addTypeListener(paramTypeListenerBinding);
    return Boolean.valueOf(true);
  }
  
  public Boolean visit(ProvisionListenerBinding paramProvisionListenerBinding)
  {
    this.injector.state.addProvisionListener(paramProvisionListenerBinding);
    return Boolean.valueOf(true);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ListenerBindingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */