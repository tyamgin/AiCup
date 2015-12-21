package com.a.b.a.a.e.a.a;

import com.a.b.a.a.a.b;
import com.a.b.a.a.e.a.g;
import com.codeforces.commons.io.IoUtil;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.IOUtils;

public abstract class f
  implements e
{
  private static final int a = IoUtil.BUFFER_SIZE;
  private static final ByteOrder b = ByteOrder.LITTLE_ENDIAN;
  private final AtomicBoolean c = new AtomicBoolean();
  private final int d;
  private ServerSocket e;
  private Socket f;
  private InputStream g;
  private OutputStream h;
  private final ByteArrayOutputStream i;
  private final File j;
  private OutputStream k;
  
  protected f(b paramb, File paramFile)
  {
    if (paramb.n()) {
      this.d = NumberUtil.toInt(TimeUnit.MINUTES.toMillis(20L));
    } else {
      this.d = NumberUtil.toInt(Math.max(TimeUnit.SECONDS.toMillis(10L), 10000L));
    }
    this.i = new ByteArrayOutputStream(a);
    this.j = paramFile;
  }
  
  public void a(int paramInt)
  {
    try
    {
      this.e = new ServerSocket(paramInt);
      this.e.setSoTimeout(this.d);
      this.e.setReceiveBufferSize(a);
    }
    catch (IOException localIOException)
    {
      throw new g(String.format("Can't start %s.", new Object[] { getClass() }), localIOException);
    }
  }
  
  public void a()
  {
    IoUtil.closeQuietly(this.f);
    try
    {
      this.f = this.e.accept();
      this.f.setSoTimeout(this.d);
      this.f.setSendBufferSize(a);
      this.f.setReceiveBufferSize(a);
      this.f.setTcpNoDelay(true);
      this.g = this.f.getInputStream();
      this.h = this.f.getOutputStream();
      if (this.j != null) {
        this.k = new FileOutputStream(this.j);
      }
    }
    catch (IOException localIOException)
    {
      throw new g("Can't accept remote process connection.", localIOException);
    }
  }
  
  protected final void a(Object[] paramArrayOfObject, a parama)
  {
    if (paramArrayOfObject == null)
    {
      c(-1);
    }
    else
    {
      int m = paramArrayOfObject.length;
      c(m);
      for (int n = 0; n < m; n++) {
        parama.a(paramArrayOfObject[n]);
      }
    }
  }
  
  protected final void a(Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null)
    {
      if (paramBoolean) {
        c(-1);
      }
    }
    else
    {
      int m = Array.getLength(paramObject);
      if (paramBoolean) {
        c(m);
      }
      Class localClass = paramObject.getClass().getComponentType();
      for (int n = 0; n < m; n++)
      {
        Object localObject = Array.get(paramObject, n);
        if (localClass.isArray()) {
          a(localObject, paramBoolean);
        } else if (localClass.isEnum()) {
          a((Enum)localObject);
        } else if (localClass == String.class) {
          a((String)localObject);
        } else if (localObject == null) {
          a(false);
        } else if ((localClass == Boolean.class) || (localClass == Boolean.TYPE)) {
          a(((Boolean)localObject).booleanValue());
        } else if ((localClass == Integer.class) || (localClass == Integer.TYPE)) {
          c(((Integer)localObject).intValue());
        } else if ((localClass == Long.class) || (localClass == Long.TYPE)) {
          a(((Long)localObject).longValue());
        } else if ((localClass == Double.class) || (localClass == Double.TYPE)) {
          a(((Double)localObject).doubleValue());
        } else {
          throw new IllegalArgumentException("Unsupported array item class: " + localClass + '.');
        }
      }
    }
  }
  
  protected final Enum a(Class paramClass)
  {
    int m = d(1)[0];
    Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
    int n = 0;
    int i1 = arrayOfEnum.length;
    while (n < i1)
    {
      Enum localEnum = arrayOfEnum[n];
      if (localEnum.ordinal() == m) {
        return localEnum;
      }
      n++;
    }
    return null;
  }
  
  protected final void a(Enum paramEnum)
  {
    a(new byte[] { paramEnum == null ? -1 : NumberUtil.toByte(paramEnum.ordinal()) });
  }
  
  protected final String g()
  {
    int m = i();
    if (m < 0) {
      return null;
    }
    return new String(d(m), StandardCharsets.UTF_8);
  }
  
  protected final void a(String paramString)
  {
    if (paramString == null)
    {
      c(-1);
      return;
    }
    byte[] arrayOfByte = paramString.getBytes(StandardCharsets.UTF_8);
    c(arrayOfByte.length);
    a(arrayOfByte);
  }
  
  protected final boolean h()
  {
    return d(1)[0] != 0;
  }
  
  protected final void a(boolean paramBoolean)
  {
    a(new byte[] { paramBoolean ? 1 : 0 });
  }
  
  protected final int i()
  {
    return ByteBuffer.wrap(d(4)).order(b).getInt();
  }
  
  protected final void c(int paramInt)
  {
    a(ByteBuffer.allocate(4).order(b).putInt(paramInt).array());
  }
  
  protected final long j()
  {
    return ByteBuffer.wrap(d(8)).order(b).getLong();
  }
  
  protected final void a(long paramLong)
  {
    a(ByteBuffer.allocate(8).order(b).putLong(paramLong).array());
  }
  
  protected final double k()
  {
    return Double.longBitsToDouble(j());
  }
  
  protected final void a(double paramDouble)
  {
    a(Double.doubleToLongBits(paramDouble));
  }
  
  protected final byte[] d(int paramInt)
  {
    m();
    try
    {
      return IOUtils.toByteArray(this.g, paramInt);
    }
    catch (IOException localIOException)
    {
      throw new g(String.format("Can't read %d bytes from input stream.", new Object[] { Integer.valueOf(paramInt) }), localIOException);
    }
  }
  
  protected final void a(byte[] paramArrayOfByte)
  {
    m();
    try
    {
      this.i.write(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new g(String.format("Can't write %d bytes into output stream.", new Object[] { Integer.valueOf(paramArrayOfByte.length) }), localIOException);
    }
  }
  
  protected final void l()
  {
    m();
    try
    {
      byte[] arrayOfByte = this.i.toByteArray();
      this.i.reset();
      this.h.write(arrayOfByte);
      this.h.flush();
      if (this.k != null) {
        this.k.write(arrayOfByte);
      }
    }
    catch (IOException localIOException)
    {
      throw new g("Can't flush output stream.", localIOException);
    }
  }
  
  private void m()
  {
    if (this.c.get()) {
      throw new IllegalStateException(String.format("%s is stopped.", new Object[] { getClass() }));
    }
  }
  
  public void f()
  {
    if (!this.c.compareAndSet(false, true)) {
      return;
    }
    IoUtil.closeQuietly(new AutoCloseable[] { this.k, this.f, this.e });
  }
  
  protected static abstract interface a
  {
    public abstract void a(Object paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\a\f.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */