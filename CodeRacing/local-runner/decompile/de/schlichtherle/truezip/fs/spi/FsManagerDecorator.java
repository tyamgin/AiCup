package de.schlichtherle.truezip.fs.spi;

import de.schlichtherle.truezip.fs.FsManager;

public abstract class FsManagerDecorator
{
  public abstract FsManager decorate(FsManager paramFsManager);
  
  public int getPriority()
  {
    return 0;
  }
  
  public String toString()
  {
    return String.format("%s[priority=%d]", new Object[] { getClass().getName(), Integer.valueOf(getPriority()) });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\spi\FsManagerDecorator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */