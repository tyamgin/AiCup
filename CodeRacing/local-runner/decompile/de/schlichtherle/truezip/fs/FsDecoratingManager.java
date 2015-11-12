package de.schlichtherle.truezip.fs;

import java.util.Iterator;

public abstract class FsDecoratingManager
  extends FsManager
{
  protected final FsManager delegate;
  
  protected FsDecoratingManager(FsManager paramFsManager)
  {
    if (null == paramFsManager) {
      throw new NullPointerException();
    }
    this.delegate = paramFsManager;
  }
  
  public FsController getController(FsMountPoint paramFsMountPoint, FsCompositeDriver paramFsCompositeDriver)
  {
    return this.delegate.getController(paramFsMountPoint, paramFsCompositeDriver);
  }
  
  public int getSize()
  {
    return this.delegate.getSize();
  }
  
  public Iterator iterator()
  {
    return this.delegate.iterator();
  }
  
  public String toString()
  {
    return String.format("%s[delegate=%s]", new Object[] { getClass().getName(), this.delegate });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsDecoratingManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */