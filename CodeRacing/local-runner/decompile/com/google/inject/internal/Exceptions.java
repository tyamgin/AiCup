package com.google.inject.internal;

class Exceptions
{
  public static RuntimeException rethrowCause(Throwable paramThrowable)
  {
    Throwable localThrowable = paramThrowable;
    if (localThrowable.getCause() != null) {
      localThrowable = localThrowable.getCause();
    }
    return rethrow(localThrowable);
  }
  
  public static RuntimeException rethrow(Throwable paramThrowable)
  {
    if ((paramThrowable instanceof RuntimeException)) {
      throw ((RuntimeException)paramThrowable);
    }
    if ((paramThrowable instanceof Error)) {
      throw ((Error)paramThrowable);
    }
    throw new UnhandledCheckedUserException(paramThrowable);
  }
  
  static class UnhandledCheckedUserException
    extends RuntimeException
  {
    public UnhandledCheckedUserException(Throwable paramThrowable)
    {
      super();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\Exceptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */