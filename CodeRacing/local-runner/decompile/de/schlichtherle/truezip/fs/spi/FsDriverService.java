package de.schlichtherle.truezip.fs.spi;

import de.schlichtherle.truezip.fs.FsDriverProvider;

public abstract class FsDriverService
  implements FsDriverProvider
{
  public String toString()
  {
    return getClass().getName();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\spi\FsDriverService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */