package com.google.inject.spi;

import com.google.inject.internal.util.StackTraceElements;
import java.lang.reflect.Member;

public final class DependencyAndSource
{
  private final Dependency dependency;
  private final Object source;
  
  public DependencyAndSource(Dependency paramDependency, Object paramObject)
  {
    this.dependency = paramDependency;
    this.source = paramObject;
  }
  
  public Dependency getDependency()
  {
    return this.dependency;
  }
  
  public String getBindingSource()
  {
    if ((this.source instanceof Class)) {
      return StackTraceElements.forType((Class)this.source).toString();
    }
    if ((this.source instanceof Member)) {
      return StackTraceElements.forMember((Member)this.source).toString();
    }
    return this.source.toString();
  }
  
  public String toString()
  {
    Dependency localDependency = getDependency();
    String str1 = getBindingSource();
    if (localDependency != null)
    {
      str2 = String.valueOf(String.valueOf(localDependency));
      String str3 = String.valueOf(String.valueOf(str1));
      return 22 + str2.length() + str3.length() + "Dependency: " + str2 + ", source: " + str3;
    }
    String str2 = String.valueOf(String.valueOf(str1));
    return 8 + str2.length() + "Source: " + str2;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\DependencyAndSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */