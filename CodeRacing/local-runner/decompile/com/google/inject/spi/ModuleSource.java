package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.internal.util.StackTraceElements.InMemoryStackTraceElement;
import java.util.List;

final class ModuleSource
{
  private final String moduleClassName;
  private final ModuleSource parent;
  private final StackTraceElements.InMemoryStackTraceElement[] partialCallStack;
  
  ModuleSource(Object paramObject, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    this(null, paramObject, paramArrayOfStackTraceElement);
  }
  
  private ModuleSource(ModuleSource paramModuleSource, Object paramObject, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    Preconditions.checkNotNull(paramObject, "module cannot be null.");
    Preconditions.checkNotNull(paramArrayOfStackTraceElement, "partialCallStack cannot be null.");
    this.parent = paramModuleSource;
    this.moduleClassName = paramObject.getClass().getName();
    this.partialCallStack = StackTraceElements.convertToInMemoryStackTraceElement(paramArrayOfStackTraceElement);
  }
  
  String getModuleClassName()
  {
    return this.moduleClassName;
  }
  
  StackTraceElement[] getPartialCallStack()
  {
    return StackTraceElements.convertToStackTraceElement(this.partialCallStack);
  }
  
  int getPartialCallStackSize()
  {
    return this.partialCallStack.length;
  }
  
  ModuleSource createChild(Object paramObject, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    return new ModuleSource(this, paramObject, paramArrayOfStackTraceElement);
  }
  
  ModuleSource getParent()
  {
    return this.parent;
  }
  
  List getModuleClassNames()
  {
    ImmutableList.Builder localBuilder = ImmutableList.builder();
    for (ModuleSource localModuleSource = this; localModuleSource != null; localModuleSource = localModuleSource.parent)
    {
      String str = localModuleSource.moduleClassName;
      localBuilder.add(str);
    }
    return localBuilder.build();
  }
  
  int size()
  {
    if (this.parent == null) {
      return 1;
    }
    return this.parent.size() + 1;
  }
  
  int getStackTraceSize()
  {
    if (this.parent == null) {
      return this.partialCallStack.length;
    }
    return this.parent.getStackTraceSize() + this.partialCallStack.length;
  }
  
  StackTraceElement[] getStackTrace()
  {
    int i = getStackTraceSize();
    StackTraceElement[] arrayOfStackTraceElement1 = new StackTraceElement[i];
    int j = 0;
    ModuleSource localModuleSource = this;
    while (localModuleSource != null)
    {
      StackTraceElement[] arrayOfStackTraceElement2 = StackTraceElements.convertToStackTraceElement(localModuleSource.partialCallStack);
      int k = arrayOfStackTraceElement2.length;
      System.arraycopy(arrayOfStackTraceElement2, 0, arrayOfStackTraceElement1, j, k);
      localModuleSource = localModuleSource.parent;
      j += k;
    }
    return arrayOfStackTraceElement1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ModuleSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */