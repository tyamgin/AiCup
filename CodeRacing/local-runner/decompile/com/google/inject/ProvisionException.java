package com.google.inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Errors;
import com.google.inject.spi.Message;
import java.util.Collection;

public final class ProvisionException
  extends RuntimeException
{
  private final ImmutableSet messages;
  private static final long serialVersionUID = 0L;
  
  public ProvisionException(Iterable paramIterable)
  {
    this.messages = ImmutableSet.copyOf(paramIterable);
    Preconditions.checkArgument(!this.messages.isEmpty());
    initCause(Errors.getOnlyCause(this.messages));
  }
  
  public ProvisionException(String paramString, Throwable paramThrowable)
  {
    super(paramThrowable);
    this.messages = ImmutableSet.of(new Message(paramString, paramThrowable));
  }
  
  public ProvisionException(String paramString)
  {
    this.messages = ImmutableSet.of(new Message(paramString));
  }
  
  public Collection getErrorMessages()
  {
    return this.messages;
  }
  
  public String getMessage()
  {
    return Errors.format("Unable to provision, see the following errors", this.messages);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\ProvisionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */