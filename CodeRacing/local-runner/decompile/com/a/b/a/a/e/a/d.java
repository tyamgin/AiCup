package com.a.b.a.a.e.a;

import com.a.b.a.a.a.b;
import com.a.b.a.a.c.c;
import com.a.b.a.a.c.l;
import com.a.b.a.a.c.m;
import com.a.b.a.a.c.p;
import com.a.b.a.a.c.v;
import com.a.b.a.a.e.a.a.a;
import com.codeforces.commons.text.StringUtil;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class d
  implements f
{
  private static final Logger a = LoggerFactory.getLogger(d.class);
  private static final AtomicInteger b = new AtomicInteger();
  private final b c;
  private final String d;
  private final boolean e;
  private final int f;
  private final int g;
  private a h;
  private com.a.b.a.a.e.a.a.e i;
  private final AtomicBoolean j = new AtomicBoolean(true);
  
  public static d a(b paramb, int paramInt1, String paramString, int paramInt2)
  {
    a(paramInt1, paramInt2);
    Preconditions.checkArgument(new File(paramString).isFile(), "Argument 'playerDefinition' is expected to be a file.");
    return new d(paramb, paramInt1, paramString, paramInt2, false);
  }
  
  public static d b(b paramb, int paramInt1, String paramString, int paramInt2)
  {
    a(paramInt1, paramInt2);
    Preconditions.checkArgument("#LocalTestPlayer".equals(paramString), "Argument 'playerDefinition' is not '#LocalTestPlayer'.");
    return new d(paramb, paramInt1, paramString, paramInt2, true);
  }
  
  private static void a(int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument((paramInt1 >= 0) && (paramInt1 <= 9), "Unexpected argument 'playerIndex': " + paramInt1 + '.');
    Preconditions.checkArgument((paramInt2 >= 1) && (paramInt2 <= 9), "Unexpected argument 'teamSize': " + paramInt2 + '.');
  }
  
  private d(b paramb, int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
  {
    this.f = paramInt1;
    this.c = paramb;
    this.d = paramString;
    this.g = paramInt2;
    this.e = paramBoolean;
  }
  
  public File b()
  {
    return this.h == null ? null : this.h.a();
  }
  
  public void c()
  {
    int k = this.c.w() + b.getAndIncrement();
    String str1 = Integer.toString(k);
    String str2;
    if (this.c.n()) {
      str2 = StringUtils.repeat("0", 16);
    } else {
      str2 = RandomStringUtils.randomAlphanumeric(16);
    }
    File localFile1 = this.c.c(this.f);
    this.i = new com.a.b.a.a.e.a.a.g(this.c, localFile1);
    this.i.a(k);
    if (!this.e)
    {
      long l = TimeUnit.MILLISECONDS.toSeconds(15L * this.g * this.c.h() + 5000L + TimeUnit.SECONDS.toMillis(1L) - 1L);
      StringBuilder localStringBuilder = new StringBuilder();
      String str3 = this.c.u();
      if (!StringUtils.isBlank(str3)) {
        localStringBuilder.append(" -l ").append(str3);
      }
      String str4 = this.c.v();
      if (!StringUtils.isBlank(str4)) {
        localStringBuilder.append(" -p ").append(str4);
      }
      String str5 = d();
      HashMap localHashMap = new HashMap();
      localHashMap.put("remote-process.port", str1);
      localHashMap.put("time-limit-seconds", String.valueOf(l));
      localHashMap.put("system-user-credentials", localStringBuilder.toString());
      localHashMap.put("jruby-home", str5);
      localHashMap.put("jruby-home.double-backslashed", StringUtil.replace(str5, "/", "\\\\"));
      try
      {
        File localFile2 = this.c.y();
        this.h = a.a(this.d, localHashMap, localFile2, new String[] { "127.0.0.1", str1, str2 });
      }
      catch (IOException localIOException)
      {
        throw new g(String.format("Failed to start process for player '%s'.", new Object[] { this.d }), localIOException);
      }
    }
    a(str2);
  }
  
  private void a(String paramString)
  {
    for (int k = 2; k >= 0; k--)
    {
      String str1;
      try
      {
        this.i.a();
        str1 = this.i.b();
        if (paramString.equals(str1)) {
          break;
        }
      }
      catch (g localg)
      {
        a.error("Got unexpected exception while authenticating strategy '" + this.d + "'.", localg);
        if (k == 0) {
          throw localg;
        }
        continue;
      }
      String str2 = String.format("Player '%s' has returned unexpected token: '%s' expected, but '%s' found.", new Object[] { this.d, paramString, str1 });
      a.error(str2);
      if (k == 0) {
        throw new g(str2);
      }
    }
  }
  
  public int a()
  {
    this.i.b(this.g);
    return this.i.c();
  }
  
  public void a(l paraml)
  {
    this.i.a(paraml);
  }
  
  public m[] a(c[] paramArrayOfc, v paramv)
  {
    if (paramArrayOfc.length != this.g) {
      throw new IllegalArgumentException(String.format("Strategy adapter '%s' got %d cars while team size is %d.", new Object[] { getClass().getSimpleName(), Integer.valueOf(paramArrayOfc.length), Integer.valueOf(this.g) }));
    }
    this.i.a(new p(paramArrayOfc, paramv), this.j.getAndSet(false));
    return this.i.d();
  }
  
  public void close()
  {
    if (this.i != null) {
      this.i.e();
    }
    new Thread(new e(this)).start();
  }
  
  private static String d()
  {
    String str1 = System.getenv("JRUBY_HOME");
    if (StringUtil.isNotBlank(str1))
    {
      localObject = new File(str1);
      if ((((File)localObject).isDirectory()) && (new File((File)localObject, "bin").isDirectory()))
      {
        for (str1 = ((File)localObject).getAbsolutePath().replace('\\', '/'); str1.contains("//"); str1 = StringUtil.replace(str1, "//", "/")) {}
        while (str1.endsWith("/")) {
          str1 = str1.substring(0, str1.length() - 1);
        }
        return str1;
      }
    }
    Object localObject = { "C:/Programs/", "C:/", "C:/Program Files/", "C:/Program Files (x86)/" };
    String[] arrayOfString = { "jruby-9.0.3.0", "jruby-9.0.1.0", "jruby-1.7.13", "jruby" };
    int k = localObject.length;
    int m = arrayOfString.length;
    for (int n = 0; n < k; n++) {
      for (int i1 = 0; i1 < m; i1++)
      {
        String str2 = localObject[n] + arrayOfString[i1];
        if ((new File(str2).isDirectory()) && (new File(str2, "bin").isDirectory())) {
          return str2;
        }
      }
    }
    throw new g("Can't find JRuby home directory.");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */