package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class ComputationException
  extends RuntimeException
{
  private static final long serialVersionUID = 0L;
  
  public ComputationException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ComputationException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */