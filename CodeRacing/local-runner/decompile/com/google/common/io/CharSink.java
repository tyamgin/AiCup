package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public abstract class CharSink
{
  public abstract Writer openStream()
    throws IOException;
  
  public BufferedWriter openBufferedStream()
    throws IOException
  {
    Writer localWriter = openStream();
    return (localWriter instanceof BufferedWriter) ? (BufferedWriter)localWriter : new BufferedWriter(localWriter);
  }
  
  public void write(CharSequence paramCharSequence)
    throws IOException
  {
    Preconditions.checkNotNull(paramCharSequence);
    Closer localCloser = Closer.create();
    try
    {
      Writer localWriter = (Writer)localCloser.register(openStream());
      localWriter.append(paramCharSequence);
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  public void writeLines(Iterable paramIterable)
    throws IOException
  {
    writeLines(paramIterable, System.getProperty("line.separator"));
  }
  
  public void writeLines(Iterable paramIterable, String paramString)
    throws IOException
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkNotNull(paramString);
    Closer localCloser = Closer.create();
    try
    {
      BufferedWriter localBufferedWriter = (BufferedWriter)localCloser.register(openBufferedStream());
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        CharSequence localCharSequence = (CharSequence)localIterator.next();
        localBufferedWriter.append(localCharSequence).append(paramString);
      }
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  public long writeFrom(Readable paramReadable)
    throws IOException
  {
    Preconditions.checkNotNull(paramReadable);
    Closer localCloser = Closer.create();
    try
    {
      Writer localWriter = (Writer)localCloser.register(openStream());
      long l = CharStreams.copy(paramReadable, localWriter);
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\CharSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */