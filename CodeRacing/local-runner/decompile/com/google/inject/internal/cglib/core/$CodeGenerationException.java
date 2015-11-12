package com.google.inject.internal.cglib.core;

public class $CodeGenerationException
  extends RuntimeException
{
  private Throwable cause;
  
  public $CodeGenerationException(Throwable paramThrowable)
  {
    super(paramThrowable.getClass().getName() + "-->" + paramThrowable.getMessage());
    this.cause = paramThrowable;
  }
  
  public Throwable getCause()
  {
    return this.cause;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$CodeGenerationException.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */