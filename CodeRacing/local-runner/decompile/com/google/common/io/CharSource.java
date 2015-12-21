package com.google.common.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class CharSource
{
  public abstract Reader openStream()
    throws IOException;
  
  public BufferedReader openBufferedStream()
    throws IOException
  {
    Reader localReader = openStream();
    return (localReader instanceof BufferedReader) ? (BufferedReader)localReader : new BufferedReader(localReader);
  }
  
  public long copyTo(Appendable paramAppendable)
    throws IOException
  {
    Preconditions.checkNotNull(paramAppendable);
    Closer localCloser = Closer.create();
    try
    {
      Reader localReader = (Reader)localCloser.register(openStream());
      long l = CharStreams.copy(localReader, paramAppendable);
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
  
  public long copyTo(CharSink paramCharSink)
    throws IOException
  {
    Preconditions.checkNotNull(paramCharSink);
    Closer localCloser = Closer.create();
    try
    {
      Reader localReader = (Reader)localCloser.register(openStream());
      Writer localWriter = (Writer)localCloser.register(paramCharSink.openStream());
      long l = CharStreams.copy(localReader, localWriter);
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
  
  public String read()
    throws IOException
  {
    Closer localCloser = Closer.create();
    try
    {
      Reader localReader = (Reader)localCloser.register(openStream());
      String str = CharStreams.toString(localReader);
      return str;
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
  
  public String readFirstLine()
    throws IOException
  {
    Closer localCloser = Closer.create();
    try
    {
      BufferedReader localBufferedReader = (BufferedReader)localCloser.register(openBufferedStream());
      String str = localBufferedReader.readLine();
      return str;
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
  
  public ImmutableList readLines()
    throws IOException
  {
    Closer localCloser = Closer.create();
    try
    {
      BufferedReader localBufferedReader = (BufferedReader)localCloser.register(openBufferedStream());
      ArrayList localArrayList = Lists.newArrayList();
      String str;
      while ((str = localBufferedReader.readLine()) != null) {
        localArrayList.add(str);
      }
      ImmutableList localImmutableList = ImmutableList.copyOf(localArrayList);
      return localImmutableList;
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\CharSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */