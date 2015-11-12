package com.google.inject.internal.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SourceProvider
{
  public static final Object UNKNOWN_SOURCE = "[unknown source]";
  private final SourceProvider parent;
  private final ImmutableSet classNamesToSkip;
  public static final SourceProvider DEFAULT_INSTANCE = new SourceProvider(ImmutableSet.of(SourceProvider.class.getName()));
  
  private SourceProvider(Iterable paramIterable)
  {
    this(null, paramIterable);
  }
  
  private SourceProvider(SourceProvider paramSourceProvider, Iterable paramIterable)
  {
    this.parent = paramSourceProvider;
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((paramSourceProvider == null) || (!paramSourceProvider.shouldBeSkipped(str))) {
        localBuilder.add(str);
      }
    }
    this.classNamesToSkip = localBuilder.build();
  }
  
  public SourceProvider plusSkippedClasses(Class... paramVarArgs)
  {
    return new SourceProvider(this, asStrings(paramVarArgs));
  }
  
  private boolean shouldBeSkipped(String paramString)
  {
    return ((this.parent != null) && (this.parent.shouldBeSkipped(paramString))) || (this.classNamesToSkip.contains(paramString));
  }
  
  private static List asStrings(Class... paramVarArgs)
  {
    ArrayList localArrayList = Lists.newArrayList();
    for (Class localClass : paramVarArgs) {
      localArrayList.add(localClass.getName());
    }
    return localArrayList;
  }
  
  public StackTraceElement get(StackTraceElement[] paramArrayOfStackTraceElement)
  {
    Preconditions.checkNotNull(paramArrayOfStackTraceElement, "The stack trace elements cannot be null.");
    for (StackTraceElement localStackTraceElement : paramArrayOfStackTraceElement)
    {
      String str = localStackTraceElement.getClassName();
      if (!shouldBeSkipped(str)) {
        return localStackTraceElement;
      }
    }
    throw new AssertionError();
  }
  
  public Object getFromClassNames(List paramList)
  {
    Preconditions.checkNotNull(paramList, "The list of module class names cannot be null.");
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!shouldBeSkipped(str)) {
        return new StackTraceElement(str, "configure", null, -1);
      }
    }
    return UNKNOWN_SOURCE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\util\SourceProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */