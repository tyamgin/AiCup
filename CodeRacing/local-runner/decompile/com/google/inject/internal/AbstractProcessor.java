package com.google.inject.internal;

import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import java.util.Iterator;
import java.util.List;

abstract class AbstractProcessor
  extends DefaultElementVisitor
{
  protected Errors errors;
  protected InjectorImpl injector;
  
  protected AbstractProcessor(Errors paramErrors)
  {
    this.errors = paramErrors;
  }
  
  public void process(Iterable paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      InjectorShell localInjectorShell = (InjectorShell)localIterator.next();
      process(localInjectorShell.getInjector(), localInjectorShell.getElements());
    }
  }
  
  public void process(InjectorImpl paramInjectorImpl, List paramList)
  {
    Errors localErrors = this.errors;
    this.injector = paramInjectorImpl;
    try
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Element localElement = (Element)localIterator.next();
        this.errors = localErrors.withSource(localElement.getSource());
        Boolean localBoolean = (Boolean)localElement.acceptVisitor(this);
        if (localBoolean.booleanValue()) {
          localIterator.remove();
        }
      }
    }
    finally
    {
      this.errors = localErrors;
      this.injector = null;
    }
  }
  
  protected Boolean visitOther(Element paramElement)
  {
    return Boolean.valueOf(false);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\AbstractProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */