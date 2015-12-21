package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.ControlFlowException;

final class FsNeedsWriteLockException
  extends ControlFlowException
{
  private static final FsNeedsWriteLockException INSTANCE = new FsNeedsWriteLockException();
  
  static FsNeedsWriteLockException get()
  {
    return isTraceable() ? new FsNeedsWriteLockException() : INSTANCE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsNeedsWriteLockException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */