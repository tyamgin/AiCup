package com.google.inject.internal;

import com.google.common.collect.Lists;
import com.google.inject.spi.PrivateElements;
import java.util.List;

final class PrivateElementProcessor
  extends AbstractProcessor
{
  private final List injectorShellBuilders = Lists.newArrayList();
  
  PrivateElementProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  public Boolean visit(PrivateElements paramPrivateElements)
  {
    InjectorShell.Builder localBuilder = new InjectorShell.Builder().parent(this.injector).privateElements(paramPrivateElements);
    this.injectorShellBuilders.add(localBuilder);
    return Boolean.valueOf(true);
  }
  
  public List getInjectorShellBuilders()
  {
    return this.injectorShellBuilders;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\PrivateElementProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */