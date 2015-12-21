package com.a.b.a.a.d;

import com.a.b.a.a.a.b;
import com.a.b.a.a.b.e.d;
import com.a.b.a.a.c.h;
import com.codeforces.commons.io.FileUtil;
import com.codeforces.commons.io.http.HttpMethod;
import com.codeforces.commons.io.http.HttpRequest;
import com.codeforces.commons.io.http.HttpResponse;
import com.codeforces.commons.io.http.HttpUtil;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.process.ThreadUtil.ExecutionStrategy.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;

public class k
  implements n
{
  private static final Logger a = Logger.getLogger(k.class);
  private final ExecutorService b = Executors.newSingleThreadExecutor();
  private final AtomicReference c = new AtomicReference();
  private final String d;
  private final b e;
  private final d f;
  private int g;
  private boolean h;
  private final StringBuilder i = new StringBuilder(NumberUtil.toInt(67108864L));
  private StringBuilder j = new StringBuilder();
  
  public k(String paramString, b paramb)
  {
    this.d = paramString;
    this.e = paramb;
    this.f = new d(paramb);
    a("");
  }
  
  public void a(h paramh)
    throws IOException
  {
    a();
    this.b.execute(new l(this, paramh));
  }
  
  public void close()
    throws IOException
  {
    this.b.shutdown();
    try
    {
      if (!this.b.awaitTermination(5L, TimeUnit.MINUTES)) {
        a(new IOException("Can't write game log file in the allotted time."));
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      a(new IOException("Unexpectedly interrupted while writing game log file.", localInterruptedException));
    }
    a();
    a("", true);
    FileUtil.executeIoOperation(new m(this), 4, 60000L, ThreadUtil.ExecutionStrategy.Type.LINEAR);
    try
    {
      this.h = false;
      a("-meta");
      a(new GsonBuilder().create().toJson(new a(InetAddress.getLocalHost().getHostName(), new Date(), this.g, this.e.f(), this.e.g(), null)), this.d, "-meta", true);
    }
    catch (RuntimeException localRuntimeException) {}
  }
  
  private static boolean c(h paramh)
  {
    return (paramh.getTick() + 1) % 500 == 0;
  }
  
  private void a(String paramString)
  {
    String str = this.d + paramString + "/begin";
    try
    {
      HttpResponse localHttpResponse = HttpUtil.executePostRequestAndReturnResponse(20000, str, new Object[0]);
      if (localHttpResponse.hasIoException()) {
        throw localHttpResponse.getIoException();
      }
      if (localHttpResponse.getCode() != 200) {
        throw new IOException(String.format("Got unexpected %s from remote storage '%s' while creating new document.", new Object[] { localHttpResponse, str }));
      }
    }
    catch (IOException localIOException)
    {
      a.error("Got I/O-exception while starting document '" + str + "'.", localIOException);
      this.h = true;
    }
  }
  
  private void a(String paramString, boolean paramBoolean)
  {
    a(this.j.toString(), this.d, paramString, paramBoolean);
    this.j = new StringBuilder();
  }
  
  private void a(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (this.h) {
      return;
    }
    String str = paramString2 + paramString3 + '/' + (paramBoolean ? "end" : "append");
    try
    {
      a(paramString1, str);
    }
    catch (IOException localIOException)
    {
      a.error("Got I/O-exception while appending document '" + str + "'.", localIOException);
      this.h = true;
    }
  }
  
  private void a(Throwable paramThrowable)
  {
    this.c.compareAndSet(null, paramThrowable);
  }
  
  private void a()
    throws IOException
  {
    Throwable localThrowable = (Throwable)this.c.get();
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
  
  private static void a(String paramString1, String paramString2)
    throws IOException
  {
    HttpResponse localHttpResponse = HttpRequest.create(paramString2, new Object[0]).setMethod(HttpMethod.POST).setBinaryEntity(paramString1.getBytes(StandardCharsets.UTF_8)).setGzip(true).setTimeoutMillis(20000).executeAndReturnResponse();
    if (localHttpResponse.getCode() != 200) {
      throw new IOException(String.format("Got unexpected %s from remote storage '%s' while appending document.", new Object[] { localHttpResponse, paramString2 }));
    }
  }
  
  private static final class a
  {
    private final String a;
    private final Date b;
    private final int c;
    private final int d;
    private final int e;
    
    private a(String paramString, Date paramDate, int paramInt1, int paramInt2, int paramInt3)
    {
      this.a = paramString;
      this.b = paramDate;
      this.c = paramInt1;
      this.d = paramInt2;
      this.e = paramInt3;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\d\k.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */