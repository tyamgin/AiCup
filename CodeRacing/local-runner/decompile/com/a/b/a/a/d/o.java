package com.a.b.a.a.d;

import com.a.b.a.a.a.b;
import com.a.b.a.a.b.e.d;
import com.a.b.a.a.c.h;
import com.codeforces.commons.io.FileUtil;
import com.codeforces.commons.io.IoUtil;
import com.codeforces.commons.math.NumberUtil;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class o
  implements n
{
  private final ExecutorService a = Executors.newSingleThreadExecutor();
  private final AtomicReference b = new AtomicReference();
  private final Writer c;
  private final d d;
  
  public o(File paramFile, b paramb)
    throws IOException
  {
    FileUtil.ensureParentDirectoryExists(paramFile);
    this.c = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(paramFile, false), NumberUtil.toInt(262144L)), StandardCharsets.UTF_8);
    this.d = new d(paramb);
  }
  
  public void a(h paramh)
    throws IOException
  {
    a();
    this.a.execute(new p(this, paramh));
  }
  
  public void close()
    throws IOException
  {
    try
    {
      this.a.shutdown();
      try
      {
        if (!this.a.awaitTermination(1L, TimeUnit.MINUTES)) {
          a(new IOException("Can't write game log file in the allotted time."));
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        a(new IOException("Unexpectedly interrupted while writing game log file.", localInterruptedException));
      }
      a();
      this.c.close();
    }
    catch (IOException|RuntimeException|Error localIOException)
    {
      IoUtil.closeQuietly(this.c);
      throw localIOException;
    }
  }
  
  private void a(Throwable paramThrowable)
  {
    this.b.compareAndSet(null, paramThrowable);
  }
  
  private void a()
    throws IOException
  {
    Throwable localThrowable = (Throwable)this.b.get();
    if (localThrowable != null)
    {
      if ((localThrowable instanceof IOException)) {
        throw ((IOException)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new IllegalStateException("Got unexpected async. throwable.", localThrowable);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\d\o.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */