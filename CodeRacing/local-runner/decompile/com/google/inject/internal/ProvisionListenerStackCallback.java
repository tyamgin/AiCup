package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.Binding;
import com.google.inject.ProvisionException;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.ProvisionListener.ProvisionInvocation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class ProvisionListenerStackCallback
{
  private static final ProvisionListener[] EMPTY_LISTENER = new ProvisionListener[0];
  private static final ProvisionListenerStackCallback EMPTY_CALLBACK = new ProvisionListenerStackCallback(null, ImmutableList.of());
  private final ProvisionListener[] listeners;
  private final Binding binding;
  
  public static ProvisionListenerStackCallback emptyListener()
  {
    return EMPTY_CALLBACK;
  }
  
  public ProvisionListenerStackCallback(Binding paramBinding, List paramList)
  {
    this.binding = paramBinding;
    if (paramList.isEmpty())
    {
      this.listeners = EMPTY_LISTENER;
    }
    else
    {
      LinkedHashSet localLinkedHashSet = Sets.newLinkedHashSet(paramList);
      this.listeners = ((ProvisionListener[])localLinkedHashSet.toArray(new ProvisionListener[localLinkedHashSet.size()]));
    }
  }
  
  public boolean hasListeners()
  {
    return this.listeners.length > 0;
  }
  
  public Object provision(Errors paramErrors, InternalContext paramInternalContext, ProvisionCallback paramProvisionCallback)
    throws ErrorsException
  {
    Provision localProvision = new Provision(paramErrors, paramInternalContext, paramProvisionCallback);
    Object localObject = null;
    try
    {
      localProvision.provision();
    }
    catch (RuntimeException localRuntimeException)
    {
      localObject = localRuntimeException;
    }
    if (localProvision.exceptionDuringProvision != null) {
      throw localProvision.exceptionDuringProvision;
    }
    if (localObject != null)
    {
      String str = localProvision.erredListener != null ? localProvision.erredListener.getClass() : "(unknown)";
      throw paramErrors.errorInUserCode((Throwable)localObject, "Error notifying ProvisionListener %s of %s.%n Reason: %s", new Object[] { str, this.binding.getKey(), localObject }).toException();
    }
    return localProvision.result;
  }
  
  private class Provision
    extends ProvisionListener.ProvisionInvocation
  {
    final Errors errors;
    final int numErrorsBefore;
    final InternalContext context;
    final ProvisionListenerStackCallback.ProvisionCallback callable;
    int index = -1;
    Object result;
    ErrorsException exceptionDuringProvision;
    ProvisionListener erredListener;
    
    public Provision(Errors paramErrors, InternalContext paramInternalContext, ProvisionListenerStackCallback.ProvisionCallback paramProvisionCallback)
    {
      this.callable = paramProvisionCallback;
      this.context = paramInternalContext;
      this.errors = paramErrors;
      this.numErrorsBefore = paramErrors.size();
    }
    
    public Object provision()
    {
      this.index += 1;
      if (this.index == ProvisionListenerStackCallback.this.listeners.length)
      {
        try
        {
          this.result = this.callable.call();
          this.errors.throwIfNewErrors(this.numErrorsBefore);
        }
        catch (ErrorsException localErrorsException)
        {
          this.exceptionDuringProvision = localErrorsException;
          throw new ProvisionException(this.errors.merge(localErrorsException.getErrors()).getMessages());
        }
      }
      else if (this.index < ProvisionListenerStackCallback.this.listeners.length)
      {
        int i = this.index;
        try
        {
          ProvisionListenerStackCallback.this.listeners[this.index].onProvision(this);
        }
        catch (RuntimeException localRuntimeException)
        {
          this.erredListener = ProvisionListenerStackCallback.this.listeners[i];
          throw localRuntimeException;
        }
        if (i == this.index) {
          provision();
        }
      }
      else
      {
        throw new IllegalStateException("Already provisioned in this listener.");
      }
      return this.result;
    }
    
    public Binding getBinding()
    {
      return ProvisionListenerStackCallback.this.binding;
    }
    
    public List getDependencyChain()
    {
      return this.context.getDependencyChain();
    }
  }
  
  public static abstract interface ProvisionCallback
  {
    public abstract Object call()
      throws ErrorsException;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProvisionListenerStackCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */