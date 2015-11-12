package com.google.inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Errors;
import java.util.Collection;

public class CreationException
  extends RuntimeException
{
  private final ImmutableSet messages;
  private static final long serialVersionUID = 0L;
  
  public CreationException(Collection paramCollection)
  {
    this.messages = ImmutableSet.copyOf(paramCollection);
    Preconditions.checkArgument(!this.messages.isEmpty());
    initCause(Errors.getOnlyCause(this.messages));
  }
  
  public Collection getErrorMessages()
  {
    return this.messages;
  }
  
  public String getMessage()
  {
    return Errors.format("Unable to create injector, see the following errors", this.messages);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\CreationException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */