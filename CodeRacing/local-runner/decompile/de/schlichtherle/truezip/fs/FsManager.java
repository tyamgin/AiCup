package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.BitField;
import java.util.Iterator;

public abstract class FsManager
  implements Iterable
{
  public abstract FsController getController(FsMountPoint paramFsMountPoint, FsCompositeDriver paramFsCompositeDriver);
  
  public abstract int getSize();
  
  public abstract Iterator iterator();
  
  public void sync(BitField paramBitField)
    throws FsSyncWarningException, FsSyncException
  {
    if (paramBitField.get(FsSyncOption.ABORT_CHANGES)) {
      throw new IllegalArgumentException();
    }
    FsSyncExceptionBuilder localFsSyncExceptionBuilder = new FsSyncExceptionBuilder();
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      FsController localFsController = (FsController)localIterator.next();
      try
      {
        localFsController.sync(paramBitField);
      }
      catch (FsSyncException localFsSyncException)
      {
        localFsSyncExceptionBuilder.warn(localFsSyncException);
      }
    }
    localFsSyncExceptionBuilder.check();
  }
  
  public final boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString()
  {
    return String.format("%s[size=%d]", new Object[] { getClass().getName(), Integer.valueOf(getSize()) });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */