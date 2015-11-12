package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public class DeadEvent
{
  private final Object source;
  private final Object event;
  
  public DeadEvent(Object paramObject1, Object paramObject2)
  {
    this.source = Preconditions.checkNotNull(paramObject1);
    this.event = Preconditions.checkNotNull(paramObject2);
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Object getEvent()
  {
    return this.event;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\eventbus\DeadEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */