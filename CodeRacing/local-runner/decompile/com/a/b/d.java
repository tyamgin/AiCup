package com.a.b;

import org.slf4j.Logger;

class d
  implements Thread.UncaughtExceptionHandler
{
  d(c paramc) {}
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    c.a().error("Got unexpected exception in thread '" + paramThread + "'.", paramThrowable);
    paramThrowable.printStackTrace();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */