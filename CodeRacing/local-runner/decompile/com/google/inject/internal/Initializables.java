package com.google.inject.internal;

final class Initializables
{
  static Initializable of(Object paramObject)
  {
    new Initializable()
    {
      public Object get(Errors paramAnonymousErrors)
        throws ErrorsException
      {
        return this.val$instance;
      }
      
      public String toString()
      {
        return String.valueOf(this.val$instance);
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\Initializables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */