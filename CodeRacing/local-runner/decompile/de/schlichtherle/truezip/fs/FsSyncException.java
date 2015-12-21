package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.io.SequentialIOException;
import java.io.IOException;

public class FsSyncException
  extends SequentialIOException
{
  public IOException getCause()
  {
    return (IOException)super.getCause();
  }
  
  public final FsSyncException initCause(Throwable paramThrowable)
  {
    super.initCause((IOException)paramThrowable);
    return this;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsSyncException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */