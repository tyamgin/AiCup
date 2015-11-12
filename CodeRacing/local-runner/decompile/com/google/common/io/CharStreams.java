package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Beta
public final class CharStreams
{
  private static final int BUF_SIZE = 2048;
  
  public static InputSupplier newReaderSupplier(String paramString)
  {
    return asInputSupplier(asCharSource(paramString));
  }
  
  public static CharSource asCharSource(String paramString)
  {
    return new StringCharSource(paramString, null);
  }
  
  public static InputSupplier newReaderSupplier(InputSupplier paramInputSupplier, Charset paramCharset)
  {
    return asInputSupplier(ByteStreams.asByteSource(paramInputSupplier).asCharSource(paramCharset));
  }
  
  public static OutputSupplier newWriterSupplier(OutputSupplier paramOutputSupplier, Charset paramCharset)
  {
    return asOutputSupplier(ByteStreams.asByteSink(paramOutputSupplier).asCharSink(paramCharset));
  }
  
  public static void write(CharSequence paramCharSequence, OutputSupplier paramOutputSupplier)
    throws IOException
  {
    asCharSink(paramOutputSupplier).write(paramCharSequence);
  }
  
  public static long copy(InputSupplier paramInputSupplier, OutputSupplier paramOutputSupplier)
    throws IOException
  {
    return asCharSource(paramInputSupplier).copyTo(asCharSink(paramOutputSupplier));
  }
  
  public static long copy(InputSupplier paramInputSupplier, Appendable paramAppendable)
    throws IOException
  {
    return asCharSource(paramInputSupplier).copyTo(paramAppendable);
  }
  
  public static long copy(Readable paramReadable, Appendable paramAppendable)
    throws IOException
  {
    Preconditions.checkNotNull(paramReadable);
    Preconditions.checkNotNull(paramAppendable);
    CharBuffer localCharBuffer = CharBuffer.allocate(2048);
    long l = 0L;
    while (paramReadable.read(localCharBuffer) != -1)
    {
      localCharBuffer.flip();
      paramAppendable.append(localCharBuffer);
      l += localCharBuffer.remaining();
      localCharBuffer.clear();
    }
    return l;
  }
  
  public static String toString(Readable paramReadable)
    throws IOException
  {
    return toStringBuilder(paramReadable).toString();
  }
  
  public static String toString(InputSupplier paramInputSupplier)
    throws IOException
  {
    return asCharSource(paramInputSupplier).read();
  }
  
  private static StringBuilder toStringBuilder(Readable paramReadable)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    copy(paramReadable, localStringBuilder);
    return localStringBuilder;
  }
  
  public static String readFirstLine(InputSupplier paramInputSupplier)
    throws IOException
  {
    return asCharSource(paramInputSupplier).readFirstLine();
  }
  
  public static List readLines(InputSupplier paramInputSupplier)
    throws IOException
  {
    Closer localCloser = Closer.create();
    try
    {
      Readable localReadable = (Readable)localCloser.register((Closeable)paramInputSupplier.getInput());
      List localList = readLines(localReadable);
      return localList;
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
  
  public static List readLines(Readable paramReadable)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    LineReader localLineReader = new LineReader(paramReadable);
    String str;
    while ((str = localLineReader.readLine()) != null) {
      localArrayList.add(str);
    }
    return localArrayList;
  }
  
  public static Object readLines(Readable paramReadable, LineProcessor paramLineProcessor)
    throws IOException
  {
    Preconditions.checkNotNull(paramReadable);
    Preconditions.checkNotNull(paramLineProcessor);
    LineReader localLineReader = new LineReader(paramReadable);
    String str;
    while ((str = localLineReader.readLine()) != null) {
      if (!paramLineProcessor.processLine(str)) {
        break;
      }
    }
    return paramLineProcessor.getResult();
  }
  
  public static Object readLines(InputSupplier paramInputSupplier, LineProcessor paramLineProcessor)
    throws IOException
  {
    Preconditions.checkNotNull(paramInputSupplier);
    Preconditions.checkNotNull(paramLineProcessor);
    Closer localCloser = Closer.create();
    try
    {
      Readable localReadable = (Readable)localCloser.register((Closeable)paramInputSupplier.getInput());
      Object localObject1 = readLines(localReadable, paramLineProcessor);
      return localObject1;
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
  
  public static InputSupplier join(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    new InputSupplier()
    {
      public Reader getInput()
        throws IOException
      {
        return new MultiReader(this.val$suppliers.iterator());
      }
    };
  }
  
  public static InputSupplier join(InputSupplier... paramVarArgs)
  {
    return join(Arrays.asList(paramVarArgs));
  }
  
  public static void skipFully(Reader paramReader, long paramLong)
    throws IOException
  {
    Preconditions.checkNotNull(paramReader);
    while (paramLong > 0L)
    {
      long l = paramReader.skip(paramLong);
      if (l == 0L)
      {
        if (paramReader.read() == -1) {
          throw new EOFException();
        }
        paramLong -= 1L;
      }
      else
      {
        paramLong -= l;
      }
    }
  }
  
  public static Writer asWriter(Appendable paramAppendable)
  {
    if ((paramAppendable instanceof Writer)) {
      return (Writer)paramAppendable;
    }
    return new AppendableWriter(paramAppendable);
  }
  
  static Reader asReader(Readable paramReadable)
  {
    Preconditions.checkNotNull(paramReadable);
    if ((paramReadable instanceof Reader)) {
      return (Reader)paramReadable;
    }
    new Reader()
    {
      public int read(char[] paramAnonymousArrayOfChar, int paramAnonymousInt1, int paramAnonymousInt2)
        throws IOException
      {
        return read(CharBuffer.wrap(paramAnonymousArrayOfChar, paramAnonymousInt1, paramAnonymousInt2));
      }
      
      public int read(CharBuffer paramAnonymousCharBuffer)
        throws IOException
      {
        return this.val$readable.read(paramAnonymousCharBuffer);
      }
      
      public void close()
        throws IOException
      {
        ((Closeable)this.val$readable).close();
      }
    };
  }
  
  static InputSupplier asInputSupplier(CharSource paramCharSource)
  {
    Preconditions.checkNotNull(paramCharSource);
    new InputSupplier()
    {
      public Reader getInput()
        throws IOException
      {
        return this.val$source.openStream();
      }
    };
  }
  
  static OutputSupplier asOutputSupplier(CharSink paramCharSink)
  {
    Preconditions.checkNotNull(paramCharSink);
    new OutputSupplier()
    {
      public Writer getOutput()
        throws IOException
      {
        return this.val$sink.openStream();
      }
    };
  }
  
  static CharSource asCharSource(InputSupplier paramInputSupplier)
  {
    Preconditions.checkNotNull(paramInputSupplier);
    new CharSource()
    {
      public Reader openStream()
        throws IOException
      {
        return CharStreams.asReader((Readable)this.val$supplier.getInput());
      }
    };
  }
  
  static CharSink asCharSink(OutputSupplier paramOutputSupplier)
  {
    Preconditions.checkNotNull(paramOutputSupplier);
    new CharSink()
    {
      public Writer openStream()
        throws IOException
      {
        return CharStreams.asWriter((Appendable)this.val$supplier.getOutput());
      }
    };
  }
  
  private static final class StringCharSource
    extends CharSource
  {
    private static final Splitter LINE_SPLITTER = Splitter.on(Pattern.compile("\r\n|\n|\r"));
    private final String string;
    
    private StringCharSource(String paramString)
    {
      this.string = ((String)Preconditions.checkNotNull(paramString));
    }
    
    public Reader openStream()
    {
      return new StringReader(this.string);
    }
    
    public String read()
    {
      return this.string;
    }
    
    private Iterable lines()
    {
      new Iterable()
      {
        public Iterator iterator()
        {
          new AbstractIterator()
          {
            Iterator lines = CharStreams.StringCharSource.LINE_SPLITTER.split(CharStreams.StringCharSource.this.string).iterator();
            
            protected String computeNext()
            {
              if (this.lines.hasNext())
              {
                String str = (String)this.lines.next();
                if ((this.lines.hasNext()) || (!str.isEmpty())) {
                  return str;
                }
              }
              return (String)endOfData();
            }
          };
        }
      };
    }
    
    public String readFirstLine()
    {
      Iterator localIterator = lines().iterator();
      return localIterator.hasNext() ? (String)localIterator.next() : null;
    }
    
    public ImmutableList readLines()
    {
      return ImmutableList.copyOf(lines());
    }
    
    public String toString()
    {
      String str = this.string.substring(0, 12) + "...";
      return "CharStreams.asCharSource(" + str + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\CharStreams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */