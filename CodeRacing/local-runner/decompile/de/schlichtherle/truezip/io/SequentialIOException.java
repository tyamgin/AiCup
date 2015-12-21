package de.schlichtherle.truezip.io;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Comparator;

public class SequentialIOException
  extends IOException
  implements Cloneable
{
  private static int maxPrintExceptions = 3;
  static final Comparator INDEX_COMP = new Comparator()
  {
    public int compare(SequentialIOException paramAnonymousSequentialIOException1, SequentialIOException paramAnonymousSequentialIOException2)
    {
      return paramAnonymousSequentialIOException1.index - paramAnonymousSequentialIOException2.index;
    }
  };
  static final Comparator PRIORITY_COMP = new Comparator()
  {
    public int compare(SequentialIOException paramAnonymousSequentialIOException1, SequentialIOException paramAnonymousSequentialIOException2)
    {
      int i = paramAnonymousSequentialIOException1.priority;
      int j = paramAnonymousSequentialIOException2.priority;
      return i > j ? 1 : i < j ? -1 : SequentialIOException.INDEX_COMP.compare(paramAnonymousSequentialIOException1, paramAnonymousSequentialIOException2);
    }
  };
  private SequentialIOException predecessor = this;
  private final int priority;
  private int index;
  int maxIndex;
  
  public SequentialIOException()
  {
    this((String)null, 0);
  }
  
  public SequentialIOException(String paramString, int paramInt)
  {
    super(paramString);
    this.priority = paramInt;
  }
  
  public SequentialIOException clone()
  {
    try
    {
      return (SequentialIOException)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
  }
  
  public SequentialIOException initCause(Throwable paramThrowable)
  {
    super.initCause(paramThrowable);
    return this;
  }
  
  public synchronized SequentialIOException initPredecessor(SequentialIOException paramSequentialIOException)
  {
    setPredecessor(paramSequentialIOException);
    paramSequentialIOException = getPredecessor();
    if (paramSequentialIOException != null) {
      this.index = (++paramSequentialIOException.maxIndex);
    }
    return this;
  }
  
  private void setPredecessor(SequentialIOException paramSequentialIOException)
  {
    if (isInitPredecessor())
    {
      if (this.predecessor == paramSequentialIOException) {
        return;
      }
      throw new IllegalStateException("Cannot overwrite predecessor!");
    }
    if (paramSequentialIOException == this) {
      throw new IllegalArgumentException("Cannot be predecessor of myself!");
    }
    if ((null != paramSequentialIOException) && (!paramSequentialIOException.isInitPredecessor())) {
      throw new IllegalArgumentException("The predecessor's predecessor must be initialized in order to inhibit loops!");
    }
    this.predecessor = paramSequentialIOException;
  }
  
  public final synchronized SequentialIOException getPredecessor()
  {
    return isInitPredecessor() ? this.predecessor : null;
  }
  
  final boolean isInitPredecessor()
  {
    return this.predecessor != this;
  }
  
  public SequentialIOException sortPriority()
  {
    return sort(PRIORITY_COMP);
  }
  
  private SequentialIOException sort(Comparator paramComparator)
  {
    SequentialIOException localSequentialIOException1 = getPredecessor();
    if (null == localSequentialIOException1) {
      return this;
    }
    SequentialIOException localSequentialIOException2 = localSequentialIOException1.sort(paramComparator);
    if ((localSequentialIOException2 == localSequentialIOException1) && (paramComparator.compare(this, localSequentialIOException2) >= 0)) {
      return this;
    }
    return localSequentialIOException2.insert(clone(), paramComparator);
  }
  
  private SequentialIOException insert(SequentialIOException paramSequentialIOException, Comparator paramComparator)
  {
    if (paramComparator.compare(paramSequentialIOException, this) >= 0)
    {
      paramSequentialIOException.predecessor = this;
      paramSequentialIOException.maxIndex = Math.max(paramSequentialIOException.index, this.maxIndex);
      return paramSequentialIOException;
    }
    SequentialIOException localSequentialIOException1 = this.predecessor;
    assert (localSequentialIOException1 != this);
    SequentialIOException localSequentialIOException2 = clone();
    if (localSequentialIOException1 != null)
    {
      localSequentialIOException2.predecessor = localSequentialIOException1.insert(paramSequentialIOException, paramComparator);
      localSequentialIOException2.maxIndex = Math.max(localSequentialIOException2.index, localSequentialIOException2.predecessor.maxIndex);
    }
    else
    {
      paramSequentialIOException.predecessor = null;
      localSequentialIOException2.predecessor = paramSequentialIOException;
      localSequentialIOException2.maxIndex = paramSequentialIOException.maxIndex;
    }
    return localSequentialIOException2;
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    printStackTrace(paramPrintStream, getMaxPrintExceptions());
  }
  
  public void printStackTrace(PrintStream paramPrintStream, int paramInt)
  {
    paramInt--;
    SequentialIOException localSequentialIOException = getPredecessor();
    if (null != localSequentialIOException) {
      if (paramInt > 0)
      {
        localSequentialIOException.printStackTrace(paramPrintStream, paramInt);
        paramPrintStream.println("\nFollowed, but not caused by:");
      }
      else
      {
        paramPrintStream.println("\nOmitting " + localSequentialIOException.getNumExceptions() + " more exception(s) at the start of this list!");
      }
    }
    super.printStackTrace(paramPrintStream);
  }
  
  private int getNumExceptions()
  {
    SequentialIOException localSequentialIOException = getPredecessor();
    return null != localSequentialIOException ? localSequentialIOException.getNumExceptions() + 1 : 1;
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    printStackTrace(paramPrintWriter, getMaxPrintExceptions());
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter, int paramInt)
  {
    paramInt--;
    SequentialIOException localSequentialIOException = getPredecessor();
    if (null != localSequentialIOException) {
      if (0 < paramInt)
      {
        localSequentialIOException.printStackTrace(paramPrintWriter, paramInt);
        paramPrintWriter.println("\nFollowed, but not caused by:");
      }
      else
      {
        paramPrintWriter.println("\nOmitting " + localSequentialIOException.getNumExceptions() + " more exception(s) at the start of this list!");
      }
    }
    super.printStackTrace(paramPrintWriter);
  }
  
  public static int getMaxPrintExceptions()
  {
    return maxPrintExceptions;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\io\SequentialIOException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */