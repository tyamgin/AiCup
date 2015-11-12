package de.schlichtherle.truezip.util;

public abstract class Resource
{
  private boolean closed;
  
  public final void close()
    throws Exception
  {
    if (!this.closed)
    {
      onClose();
      this.closed = true;
    }
  }
  
  protected abstract void onClose()
    throws Exception;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\Resource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */