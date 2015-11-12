package com.a.b.a.a.b;

import com.a.b.a.a.e.a.f;
import com.codeforces.commons.io.IoUtil;
import com.codeforces.commons.reflection.Name;
import com.codeforces.commons.text.StringUtil;
import java.util.concurrent.atomic.AtomicLong;

public class n
{
  private static final AtomicLong a = new AtomicLong();
  @Name("id")
  private final long b = a.incrementAndGet();
  @Name("name")
  private final String c;
  private final long d = com.a.a.a.a.c.b();
  private final f e;
  private boolean f;
  private String g;
  private long h;
  private long i;
  private int j;
  
  public n(String paramString, f paramf)
  {
    this.c = paramString;
    this.e = paramf;
  }
  
  public final long a()
  {
    return this.b;
  }
  
  public String b()
  {
    return this.c;
  }
  
  public long c()
  {
    return this.d;
  }
  
  public f d()
  {
    return this.e;
  }
  
  public boolean e()
  {
    return this.e instanceof com.a.b.a.a.e.a.c;
  }
  
  public boolean f()
  {
    return this.f;
  }
  
  public String g()
  {
    return this.g;
  }
  
  public void a(String paramString)
  {
    if (e()) {
      return;
    }
    this.f = true;
    this.g = paramString;
    IoUtil.closeQuietly(this.e);
  }
  
  public long h()
  {
    return this.h;
  }
  
  public void a(long paramLong)
  {
    this.h = paramLong;
  }
  
  public void b(long paramLong)
  {
    this.i = paramLong;
    this.h -= paramLong;
  }
  
  public int i()
  {
    return this.j;
  }
  
  public void a(int paramInt)
  {
    this.j += paramInt;
  }
  
  public final boolean equals(Object paramObject)
  {
    return (this == paramObject) || ((paramObject != null) && (getClass() == paramObject.getClass()) && (this.b == ((n)paramObject).b));
  }
  
  public final int hashCode()
  {
    return Long.valueOf(this.b).hashCode();
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "id", "name" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\n.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */