package com.google.inject.internal;

public class ErrorsException
  extends Exception
{
  private final Errors errors;
  
  public ErrorsException(Errors paramErrors)
  {
    this.errors = paramErrors;
  }
  
  public Errors getErrors()
  {
    return this.errors;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ErrorsException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */