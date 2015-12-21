package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.internal.util.StackTraceElements.InMemoryStackTraceElement;
import java.util.List;

public final class ElementSource
{
  final ElementSource originalElementSource;
  final ModuleSource moduleSource;
  final StackTraceElements.InMemoryStackTraceElement[] partialCallStack;
  final Object declaringSource;
  
  ElementSource(ElementSource paramElementSource, Object paramObject, ModuleSource paramModuleSource, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    Preconditions.checkNotNull(paramObject, "declaringSource cannot be null.");
    Preconditions.checkNotNull(paramModuleSource, "moduleSource cannot be null.");
    Preconditions.checkNotNull(paramArrayOfStackTraceElement, "partialCallStack cannot be null.");
    this.originalElementSource = paramElementSource;
    this.declaringSource = paramObject;
    this.moduleSource = paramModuleSource;
    this.partialCallStack = StackTraceElements.convertToInMemoryStackTraceElement(paramArrayOfStackTraceElement);
  }
  
  public ElementSource getOriginalElementSource()
  {
    return this.originalElementSource;
  }
  
  public Object getDeclaringSource()
  {
    return this.declaringSource;
  }
  
  public List getModuleClassNames()
  {
    return this.moduleSource.getModuleClassNames();
  }
  
  public List getModuleConfigurePositionsInStackTrace()
  {
    int i = this.moduleSource.size();
    Integer[] arrayOfInteger = new Integer[i];
    int j = this.partialCallStack.length;
    arrayOfInteger[0] = Integer.valueOf(j - 1);
    ModuleSource localModuleSource = this.moduleSource;
    for (int k = 1; k < i; k++)
    {
      j = localModuleSource.getPartialCallStackSize();
      arrayOfInteger[k] = Integer.valueOf(arrayOfInteger[(k - 1)].intValue() + j);
      localModuleSource = localModuleSource.getParent();
    }
    return ImmutableList.copyOf(arrayOfInteger);
  }
  
  public StackTraceElement[] getStackTrace()
  {
    int i = this.moduleSource.getStackTraceSize();
    int j = this.partialCallStack.length;
    int k = this.moduleSource.getStackTraceSize() + j;
    StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[k];
    System.arraycopy(StackTraceElements.convertToStackTraceElement(this.partialCallStack), 0, arrayOfStackTraceElement, 0, j);
    System.arraycopy(this.moduleSource.getStackTrace(), 0, arrayOfStackTraceElement, j, i);
    return arrayOfStackTraceElement;
  }
  
  public String toString()
  {
    return getDeclaringSource().toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ElementSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */