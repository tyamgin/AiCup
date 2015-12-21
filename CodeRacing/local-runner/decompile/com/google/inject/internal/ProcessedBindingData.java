package com.google.inject.internal;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

class ProcessedBindingData
{
  private final List creationListeners = Lists.newArrayList();
  private final List uninitializedBindings = Lists.newArrayList();
  
  void addCreationListener(CreationListener paramCreationListener)
  {
    this.creationListeners.add(paramCreationListener);
  }
  
  void addUninitializedBinding(Runnable paramRunnable)
  {
    this.uninitializedBindings.add(paramRunnable);
  }
  
  void initializeBindings()
  {
    Iterator localIterator = this.uninitializedBindings.iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      localRunnable.run();
    }
  }
  
  void runCreationListeners(Errors paramErrors)
  {
    Iterator localIterator = this.creationListeners.iterator();
    while (localIterator.hasNext())
    {
      CreationListener localCreationListener = (CreationListener)localIterator.next();
      localCreationListener.notify(paramErrors);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProcessedBindingData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */