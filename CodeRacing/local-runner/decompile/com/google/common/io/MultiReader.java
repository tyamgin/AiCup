package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

class MultiReader
  extends Reader
{
  private final Iterator it;
  private Reader current;
  
  MultiReader(Iterator paramIterator)
    throws IOException
  {
    this.it = paramIterator;
    advance();
  }
  
  private void advance()
    throws IOException
  {
    close();
    if (this.it.hasNext()) {
      this.current = ((Reader)((InputSupplier)this.it.next()).getInput());
    }
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.current == null) {
      return -1;
    }
    int i = this.current.read(paramArrayOfChar, paramInt1, paramInt2);
    if (i == -1)
    {
      advance();
      return read(paramArrayOfChar, paramInt1, paramInt2);
    }
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    Preconditions.checkArgument(paramLong >= 0L, "n is negative");
    if (paramLong > 0L) {
      while (this.current != null)
      {
        long l = this.current.skip(paramLong);
        if (l > 0L) {
          return l;
        }
        advance();
      }
    }
    return 0L;
  }
  
  public boolean ready()
    throws IOException
  {
    return (this.current != null) && (this.current.ready());
  }
  
  public void close()
    throws IOException
  {
    if (this.current != null) {
      try
      {
        this.current.close();
      }
      finally
      {
        this.current = null;
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\MultiReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */