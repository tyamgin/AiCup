package com.google.inject.internal;

import com.google.inject.Guice;
import java.util.logging.Logger;

final class MessageProcessor
  extends AbstractProcessor
{
  private static final Logger logger = Logger.getLogger(Guice.class.getName());
  
  MessageProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  /* Error */
  public Boolean visit(com.google.inject.spi.Message arg1)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 20	com/google/inject/spi/Message:getCause	()Ljava/lang/Throwable;
    //   4: ifnull +52 -> 56
    //   7: aload_1
    //   8: invokevirtual 20	com/google/inject/spi/Message:getCause	()Ljava/lang/Throwable;
    //   11: invokestatic 18	com/google/inject/internal/MessageProcessor:getRootMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   14: astore_2
    //   15: getstatic 14	com/google/inject/internal/MessageProcessor:logger	Ljava/util/logging/Logger;
    //   18: getstatic 15	java/util/logging/Level:INFO	Ljava/util/logging/Level;
    //   21: ldc 1
    //   23: aload_2
    //   24: invokestatic 26	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   27: dup
    //   28: invokevirtual 25	java/lang/String:length	()I
    //   31: ifeq +9 -> 40
    //   34: invokevirtual 24	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   37: goto +12 -> 49
    //   40: pop
    //   41: new 9	java/lang/String
    //   44: dup_x1
    //   45: swap
    //   46: invokespecial 23	java/lang/String:<init>	(Ljava/lang/String;)V
    //   49: aload_1
    //   50: invokevirtual 20	com/google/inject/spi/Message:getCause	()Ljava/lang/Throwable;
    //   53: invokevirtual 30	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   56: aload_0
    //   57: getfield 13	com/google/inject/internal/MessageProcessor:errors	Lcom/google/inject/internal/Errors;
    //   60: aload_1
    //   61: invokevirtual 17	com/google/inject/internal/Errors:addMessage	(Lcom/google/inject/spi/Message;)Lcom/google/inject/internal/Errors;
    //   64: pop
    //   65: iconst_1
    //   66: invokestatic 21	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   69: areturn
  }
  
  public static String getRootMessage(Throwable paramThrowable)
  {
    Throwable localThrowable = paramThrowable.getCause();
    return localThrowable == null ? paramThrowable.toString() : getRootMessage(localThrowable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\MessageProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */