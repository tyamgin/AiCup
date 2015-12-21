package com.google.inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Errors;
import java.util.Collection;

public final class ConfigurationException
  extends RuntimeException
{
  private final ImmutableSet messages;
  private Object partialValue = null;
  private static final long serialVersionUID = 0L;
  
  public ConfigurationException(Iterable paramIterable)
  {
    this.messages = ImmutableSet.copyOf(paramIterable);
    initCause(Errors.getOnlyCause(this.messages));
  }
  
  public ConfigurationException withPartialValue(Object paramObject)
  {
    Preconditions.checkState(this.partialValue == null, "Can't clobber existing partial value %s with %s", new Object[] { this.partialValue, paramObject });
    ConfigurationException localConfigurationException = new ConfigurationException(this.messages);
    localConfigurationException.partialValue = paramObject;
    return localConfigurationException;
  }
  
  public Collection getErrorMessages()
  {
    return this.messages;
  }
  
  public Object getPartialValue()
  {
    return this.partialValue;
  }
  
  public String getMessage()
  {
    return Errors.format("Guice configuration errors", this.messages);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\ConfigurationException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */