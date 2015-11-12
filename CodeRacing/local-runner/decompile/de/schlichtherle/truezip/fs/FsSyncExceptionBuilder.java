package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.io.SequentialIOExceptionBuilder;
import java.io.IOException;

public final class FsSyncExceptionBuilder
  extends SequentialIOExceptionBuilder
{
  public FsSyncExceptionBuilder()
  {
    super(IOException.class, FsSyncException.class);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsSyncExceptionBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */