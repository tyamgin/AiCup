package com.a.b.a.a.b;

import com.a.b.a.a.b.e.i.a;
import com.a.b.a.a.b.e.q;
import com.a.b.a.a.c.m;
import com.a.b.a.a.c.t;
import com.a.b.a.a.c.v;
import com.a.b.a.a.d.k;
import com.a.b.a.a.d.o;
import com.codeforces.commons.compress.ZipUtil;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.holder.Mutable;
import com.codeforces.commons.holder.SimpleMutable;
import com.codeforces.commons.io.IoUtil;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.IntPair;
import com.codeforces.commons.text.StringUtil;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class a
  implements com.a.b.a
{
  private static final Logger a = LoggerFactory.getLogger(a.class);
  @Inject
  private com.a.b.h b;
  private com.a.b.a.a.a.b c = com.a.b.a.a.a.b.a;
  private i.a d;
  private IntPair[] e;
  private final AtomicBoolean f = new AtomicBoolean(false);
  private final AtomicReference g = new AtomicReference(null);
  private int h;
  private int i;
  private int j;
  private int k;
  private Integer l;
  private int m;
  private final List n = new ArrayList();
  private final Map o = new LinkedHashMap();
  private final List p = new ArrayList();
  private final List q = new ArrayList();
  private final List r = new ArrayList();
  private ExecutorService s;
  private long t;
  private boolean u;
  private BufferedReader v;
  private com.a.b.a.a.c.h w;
  
  public void a(com.a.b.a.a.a.b paramb)
  {
    a.info("Game has been started.");
    this.t = System.currentTimeMillis();
    this.c = paramb;
    this.u = (paramb.i() != null);
    if (this.u)
    {
      try
      {
        this.v = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(paramb.i())), StandardCharsets.UTF_8));
      }
      catch (IOException localIOException)
      {
        a(String.format("Can't open log file: %s", new Object[] { ExceptionUtils.getStackTrace(localIOException) }));
        return;
      }
      if ((this.w = w()) != null) {
        this.c.a(this.w.getMapName());
      }
    }
    l();
    this.j = paramb.h();
    this.m = (this.j - 1);
    m();
    n();
    if (!this.u)
    {
      o();
      p();
      q();
      r();
      t();
      u();
    }
    a.info("Game has been initialized.");
  }
  
  public void a()
  {
    for (this.k = 0; ((this.u) || (this.k <= this.m)) && (!this.f.get()) && ((this.u) || (!v())); this.k += 1)
    {
      a(false);
      if (this.u)
      {
        this.w = w();
        if ((this.w == null) || (this.w.isLastTick())) {
          break;
        }
        this.m = this.w.getLastTickIndex();
      }
      else
      {
        i();
        j();
        this.b.a(this.k);
        k();
      }
    }
    a(true);
  }
  
  public void b()
  {
    a.info("Game has been finished in " + (System.currentTimeMillis() - this.t) + " ms.");
    Object localObject;
    if (this.c.A())
    {
      localIterator1 = this.o.values().iterator();
      while (localIterator1.hasNext())
      {
        localObject = (List)localIterator1.next();
        Iterator localIterator2 = ((List)localObject).iterator();
        while (localIterator2.hasNext())
        {
          com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)localIterator2.next();
          double d1 = this.k > 180 ? localb.i().doubleValue() / (this.k - 180) : 0.0D;
          a.info(String.format("Average speed of %s is %.2f per tick.", new Object[] { localb, Double.valueOf(d1) }));
          a.info(String.format("Max speed of %s is %.2f per tick (tick=%d).", new Object[] { localb, Double.valueOf(localb.c()), Integer.valueOf(localb.d()) }));
          a.info(String.format("Max angular speed of %s is %.3f/%.1f° per tick (tick=%d).", new Object[] { localb, Double.valueOf(localb.e()), Double.valueOf(localb.e() * 57.29577951308232D), Integer.valueOf(localb.f()) }));
        }
      }
    }
    Iterator localIterator1 = this.o.keySet().iterator();
    while (localIterator1.hasNext())
    {
      localObject = (n)localIterator1.next();
      a.info("Player '" + ((n)localObject).b() + "' scored " + ((n)localObject).i() + " point(s).");
    }
    if (!this.u) {
      c();
    }
    d();
    if (!this.u)
    {
      e();
      f();
      g();
    }
  }
  
  private void c()
  {
    Iterator localIterator = this.o.keySet().iterator();
    while (localIterator.hasNext())
    {
      n localn = (n)localIterator.next();
      IoUtil.closeQuietly(localn.d());
    }
  }
  
  private void d()
  {
    Iterator localIterator = this.n.iterator();
    while (localIterator.hasNext())
    {
      com.a.b.a.a.d.n localn = (com.a.b.a.a.d.n)localIterator.next();
      try
      {
        localn.close();
      }
      catch (IOException localIOException)
      {
        a.error(String.format("Can't close renderer '%s'.", new Object[] { localn.getClass().getSimpleName() }), localIOException);
        a(String.format("Can't close renderer '%s': %s", new Object[] { localn.getClass().getSimpleName(), ExceptionUtils.getStackTrace(localIOException) }));
      }
    }
  }
  
  private void e()
  {
    File localFile = this.c.k();
    if (localFile == null) {
      return;
    }
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator1 = this.o.entrySet().iterator();
      while (localIterator1.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator1.next();
        int i1 = 1;
        Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
        while (localIterator2.hasNext())
        {
          com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)localIterator2.next();
          if (i1 != 0) {
            i1 = 0;
          } else {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(localb.M() == null ? -1 : localb.M().intValue());
        }
        localStringBuilder.append('\n');
      }
      FileUtils.writeByteArrayToFile(localFile, localStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }
    catch (IOException localIOException)
    {
      a.error(String.format("Can't write strategy descriptions to file '%s'.", new Object[] { localFile.getPath() }), localIOException);
    }
  }
  
  private void f()
  {
    File localFile = this.c.l();
    if (localFile == null) {
      return;
    }
    try
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("map", this.c.d());
      if (this.c.B()) {
        localHashMap.put("swap-car-types", Boolean.toString(this.c.B()));
      }
      byte[] arrayOfByte = new GsonBuilder().create().toJson(localHashMap).getBytes(StandardCharsets.UTF_8);
      FileUtils.writeByteArrayToFile(localFile, arrayOfByte);
    }
    catch (IOException localIOException)
    {
      a.error(String.format("Can't write attributes to file '%s'.", new Object[] { localFile.getPath() }), localIOException);
    }
  }
  
  private void g()
  {
    File localFile = this.c.j();
    if (localFile == null) {
      return;
    }
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.f.get())
      {
        a.error("Game has failed with the message: " + (String)this.g.get());
        localStringBuilder.append("FAILED\n").append((String)this.g.get()).append('\n');
      }
      else
      {
        localStringBuilder.append("OK\nSEED ").append(this.c.x()).append('\n');
        Map localMap = h();
        Iterator localIterator = this.o.keySet().iterator();
        while (localIterator.hasNext())
        {
          n localn = (n)localIterator.next();
          localStringBuilder.append(localMap.get(Integer.valueOf(localn.i()))).append(' ').append(localn.i()).append(localn.f() ? " CRASHED" : " OK");
          if (!this.c.q())
          {
            String str = a(localn);
            if (!StringUtil.isBlank(str)) {
              localStringBuilder.append(' ').append(Base64.encodeBase64URLSafeString(ZipUtil.compress(str.getBytes(StandardCharsets.UTF_8), 9)));
            }
          }
          localStringBuilder.append('\n');
        }
      }
      FileUtils.writeByteArrayToFile(localFile, localStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }
    catch (IOException localIOException)
    {
      a.error(String.format("Can't write results to file '%s'.", new Object[] { localFile.getPath() }), localIOException);
    }
  }
  
  private String a(n paramn)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (!StringUtil.isEmpty(paramn.g())) {
      localStringBuilder.append(paramn.g());
    }
    if ((paramn.d() instanceof com.a.b.a.a.e.a.d))
    {
      File localFile = ((com.a.b.a.a.e.a.d)paramn.d()).b();
      if (localFile != null)
      {
        a(localStringBuilder, "Вывод runner'а в stdout:", localFile, "runexe.output");
        a(localStringBuilder, "Вывод runner'а в stderr:", localFile, "runexe.error");
        if (this.c.r())
        {
          a(localStringBuilder, "Вывод стратегии в stdout:", localFile, "process.output");
          a(localStringBuilder, "Вывод стратегии в stderr:", localFile, "process.error");
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  private static void a(StringBuilder paramStringBuilder, String paramString1, File paramFile, String paramString2)
  {
    String str = a(new File(paramFile, paramString2));
    if (!StringUtil.isBlank(str))
    {
      if (paramStringBuilder.length() > 0) {
        paramStringBuilder.append("\r\n\r\n");
      }
      paramStringBuilder.append(paramString1).append("\r\n").append(str);
    }
  }
  
  private static String a(File paramFile)
  {
    if (!paramFile.isFile()) {
      return "";
    }
    List localList;
    try
    {
      localList = FileUtils.readLines(paramFile, Charsets.UTF_8);
      while ((!localList.isEmpty()) && (StringUtil.isBlank((String)localList.get(0)))) {
        localList.remove(0);
      }
      while ((!localList.isEmpty()) && (StringUtil.isBlank((String)localList.get(localList.size() - 1)))) {
        localList.remove(localList.size() - 1);
      }
      if (localList.isEmpty()) {
        return "";
      }
    }
    catch (IOException localIOException)
    {
      a.error("Can't read file '" + paramFile.getAbsolutePath() + "'.", localIOException);
      return "";
    }
    return StringUtils.join(StringUtil.shrinkLinesTo(localList, 100, 17), "\r\n");
  }
  
  private Map h()
  {
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList = new ArrayList(this.o.keySet());
    Collections.sort(localArrayList, com.a.b.a.a.b.e.l.a());
    for (int i1 = localArrayList.size() - 1; i1 >= 0; i1--) {
      localHashMap.put(Integer.valueOf(((n)localArrayList.get(i1)).i()), Integer.valueOf(i1 + 1));
    }
    return localHashMap;
  }
  
  private void a(boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(this.o.keySet());
    List localList1 = this.b.a();
    List localList2 = this.b.b();
    double d1 = 1.0D / this.b.c();
    com.a.b.a.a.c.h localh;
    if (this.w == null) {
      localh = q.a(q.a(this.k, this.j, this.m, this.h, this.i, d1, localArrayList, this.c, localList1, null), this.c.x(), d1, paramBoolean, localList2, localArrayList, localList1);
    } else {
      localh = this.w;
    }
    Iterator localIterator = this.n.iterator();
    while (localIterator.hasNext())
    {
      com.a.b.a.a.d.n localn = (com.a.b.a.a.d.n)localIterator.next();
      try
      {
        localn.a(localh);
      }
      catch (IOException localIOException)
      {
        a.error(String.format("Can't render world using renderer '%s' [tick=%d].", new Object[] { localn.getClass().getSimpleName(), Integer.valueOf(this.k) }), localIOException);
        a(String.format("Can't render world using renderer '%s': %s [tick=%d]", new Object[] { localn.getClass().getSimpleName(), ExceptionUtils.getStackTrace(localIOException), Integer.valueOf(this.k) }));
      }
    }
  }
  
  private void i()
  {
    ArrayList localArrayList1 = new ArrayList(this.o.keySet());
    List localList1 = this.b.a();
    ArrayList localArrayList2 = new ArrayList(this.o.entrySet());
    ArrayList localArrayList3 = new ArrayList();
    Iterator localIterator = localArrayList2.iterator();
    Object localObject1;
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      n localn = (n)((Map.Entry)localObject1).getKey();
      List localList2 = (List)((Map.Entry)localObject1).getValue();
      int i1 = localList2.size();
      Object localObject2 = localList2.iterator();
      Object localObject3;
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (com.a.b.a.a.b.d.c.b)((Iterator)localObject2).next();
        ((com.a.b.a.a.b.d.c.b)localObject3).a(false);
        if ((com.a.b.a.a.b.e.b.a((com.a.b.a.a.b.d.c.b)localObject3)) || (((com.a.b.a.a.b.d.c.b)localObject3).N()) || (localn.f()))
        {
          double d1 = ((com.a.b.a.a.b.d.c.b)localObject3).b().f().dotProduct(new Vector2D(1.0D, 0.0D).rotate(((com.a.b.a.a.b.d.c.b)localObject3).b().e())) * ((com.a.b.a.a.b.d.c.b)localObject3).q() * 0.0017453292519943296D;
          ((com.a.b.a.a.b.d.c.b)localObject3).b().e(d1);
        }
      }
      if (!localn.f())
      {
        localObject2 = q.a(this.k, this.j, this.m, this.h, this.i, 1.0D / this.b.c(), localArrayList1, this.c, localList1, localn);
        localObject3 = new com.a.b.a.a.c.c[i1];
        for (int i2 = 0; i2 < i1; i2++) {
          localObject3[i2] = com.a.b.a.a.b.e.b.a((com.a.b.a.a.b.d.c.b)localList2.get(i2), 1.0D / this.b.c(), localn);
        }
        Future localFuture = this.s.submit(new b(this, localn, (com.a.b.a.a.c.c[])localObject3, (v)localObject2));
        SimpleMutable localSimpleMutable = new SimpleMutable();
        if (!a(localn, localFuture, localSimpleMutable)) {
          break;
        }
        m[] arrayOfm = (m[])localSimpleMutable.get();
        if ((arrayOfm == null) || (arrayOfm.length != i1)) {
          throw new RuntimeException(String.format("Strategy adapter '%s' of %s returned %d moves for %d cars at tick %d.", new Object[] { localn.d().getClass().getSimpleName(), localn, Integer.valueOf(arrayOfm == null ? 0 : arrayOfm.length), Integer.valueOf(i1), Integer.valueOf(this.k) }));
        }
        for (int i3 = 0; i3 < i1; i3++)
        {
          m localm = arrayOfm[i3];
          if (localm != null)
          {
            com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)localList2.get(i3);
            if ((!com.a.b.a.a.b.e.b.a(localb)) && (!localb.N())) {
              localArrayList3.add(new a(localb, localm, null));
            }
          }
        }
      }
    }
    com.a.a.a.a.c.a(localArrayList3);
    localIterator = localArrayList3.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (a)localIterator.next();
      a(a.a((a)localObject1), a.b((a)localObject1));
    }
    localIterator = localArrayList3.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (a)localIterator.next();
      b(a.a((a)localObject1), a.b((a)localObject1));
    }
  }
  
  private boolean a(n paramn, Future paramFuture, Mutable paramMutable)
  {
    long l1 = System.currentTimeMillis();
    try
    {
      if (this.c.n()) {
        paramMutable.set(paramFuture.get(20L, TimeUnit.MINUTES));
      } else {
        paramMutable.set(paramFuture.get(5000L, TimeUnit.MILLISECONDS));
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      a.error(String.format("Strategy adapter '%s' of %s has been interrupted at a tick %d.", new Object[] { paramn.d().getClass().getSimpleName(), paramn, Integer.valueOf(this.k) }), localInterruptedException);
      paramFuture.cancel(true);
      paramn.a("Ожидание отклика от стратегии было прервано.");
      return false;
    }
    catch (ExecutionException localExecutionException)
    {
      a.warn(String.format("Strategy adapter '%s' of %s has failed at a tick %d.", new Object[] { paramn.d().getClass().getSimpleName(), paramn, Integer.valueOf(this.k) }), localExecutionException);
      paramFuture.cancel(true);
      paramn.a("Процесс стратегии непредвиденно завершился на тике " + this.k + '.');
      return false;
    }
    catch (TimeoutException localTimeoutException)
    {
      a.warn(String.format("Strategy adapter '%s' of %s has timed out at a tick %d.", new Object[] { paramn.d().getClass().getSimpleName(), paramn, Integer.valueOf(this.k) }), localTimeoutException);
      paramFuture.cancel(true);
      paramn.a("Процесс стратегии превысил ограничение по времени на тик.");
      return false;
    }
    if (!this.c.n())
    {
      long l2 = System.currentTimeMillis() - l1;
      long l3 = paramn.h();
      if (l3 < l2)
      {
        a.warn(String.format("Strategy adapter '%s' of %s has consumed all available game time at a tick %d.", new Object[] { paramn.d().getClass().getSimpleName(), paramn, Integer.valueOf(this.k) }));
        paramn.a("Процесс стратегии превысил ограничение по времени на игру.");
        return false;
      }
      paramn.b(l2);
    }
    return true;
  }
  
  private void j()
  {
    Iterator localIterator = this.q.iterator();
    while (localIterator.hasNext())
    {
      com.a.b.b localb = (com.a.b.b)localIterator.next();
      localb.a(this.b, this.k);
    }
  }
  
  private void k()
  {
    Iterator localIterator = this.r.iterator();
    while (localIterator.hasNext())
    {
      com.a.b.b localb = (com.a.b.b)localIterator.next();
      localb.a(this.b, this.k);
    }
  }
  
  private void a(com.a.b.a.a.b.d.c.b paramb, m paramm)
  {
    if ((com.a.b.a.a.b.e.b.a(paramb)) || (paramb.N())) {
      return;
    }
    com.a.c.c localc;
    Point2D localPoint2D;
    if ((paramm.isThrowProjectile()) && (paramb.y() == 0) && (paramb.v() > 0))
    {
      localc = paramb.b();
      localPoint2D = new Point2D(localc.c(), localc.d());
      switch (j.a[paramb.m().ordinal()])
      {
      case 1: 
        this.b.a(new com.a.b.a.a.b.d.e.c(paramb, paramb.k(), this.k, localPoint2D.copy(), localc.e()));
        this.b.a(new com.a.b.a.a.b.d.e.c(paramb, paramb.k(), this.k, localPoint2D.copy(), localc.e() + 0.03490658503988659D));
        this.b.a(new com.a.b.a.a.b.d.e.c(paramb, paramb.k(), this.k, localPoint2D.copy(), localc.e() - 0.03490658503988659D));
        break;
      case 2: 
        this.b.a(new com.a.b.a.a.b.d.e.b(paramb, paramb.k(), this.k, localPoint2D.copy(), localc.e()));
        break;
      default: 
        throw new IllegalArgumentException("Unsupported car type: " + paramb.m() + '.');
      }
      paramb.g(60);
      paramb.d(paramb.v() - 1);
    }
    if ((paramm.isUseNitro()) && (paramb.A() == 0) && (paramb.w() > 0))
    {
      paramb.j(120);
      paramb.h(120);
      paramb.e(paramb.w() - 1);
    }
    if ((paramm.isSpillOil()) && (paramb.C() == 0) && (paramb.x() > 0))
    {
      localc = paramb.b();
      localPoint2D = new Point2D(localc.c(), localc.d()).add(new Vector2D(265.0D, 0.0D).rotate(localc.e() + 3.141592653589793D));
      this.b.a(new com.a.b.a.a.b.d.d.a(localPoint2D.copy()));
      paramb.i(120);
      paramb.f(paramb.x() - 1);
    }
  }
  
  private void b(com.a.b.a.a.b.d.c.b paramb, m paramm)
  {
    if ((com.a.b.a.a.b.e.b.a(paramb)) || (paramb.N())) {
      return;
    }
    c(paramb, paramm);
    d(paramb, paramm);
  }
  
  private void c(com.a.b.a.a.b.d.c.b paramb, m paramm)
  {
    double d1 = paramm.getEnginePower();
    if ((Double.isNaN(d1)) || (Double.isInfinite(d1)))
    {
      a.warn(String.format("Received unexpected 'enginePower' value (%s) for %s.", new Object[] { Double.valueOf(d1), paramb }));
      return;
    }
    if (paramb.E() > 0)
    {
      paramb.a(2.0D, true);
      d1 = 2.0D;
    }
    else
    {
      paramb.b(d1);
      d1 = paramb.p();
    }
    if (paramm.isBrake())
    {
      paramb.a(true);
      paramb.b().h(paramb.G() > 0 ? 0.001D : 0.25D);
    }
    else
    {
      paramb.b().h(0.001D);
      double d2 = d1 * (d1 >= 0.0D ? paramb.r() : paramb.s());
      if (this.k >= 180) {
        paramb.b().j().add(new Vector2D(d2, 0.0D).rotate(paramb.b().e()));
      }
    }
  }
  
  private static void d(com.a.b.a.a.b.d.c.b paramb, m paramm)
  {
    double d1 = paramm.getWheelTurn();
    if ((Double.isNaN(d1)) || (Double.isInfinite(d1)))
    {
      a.warn(String.format("Received unexpected 'wheelTurn' value (%s) for %s.", new Object[] { Double.valueOf(d1), paramb }));
      return;
    }
    paramb.d(d1);
    d1 = paramb.q();
    double d2 = paramb.b().f().dotProduct(new Vector2D(1.0D, 0.0D).rotate(paramb.b().e())) * d1 * 0.0017453292519943296D;
    double d3 = d2 - paramb.b().i();
    paramb.b().e(d2);
    paramb.b().d(paramb.b().h() + d3);
  }
  
  private void l()
  {
    String str = this.c.d();
    a.debug("Started to load '" + str + "' map.");
    this.d = this.c.b(str);
    this.h = this.c.f();
    this.i = this.c.g();
    t[][] arrayOft = this.d.b();
    ArrayList localArrayList = new ArrayList();
    for (int i1 = 0; i1 < this.h; i1++) {
      for (int i2 = 0; i2 < this.i; i2++)
      {
        t localt = arrayOft[i1][i2];
        if (localt != t.EMPTY) {
          localArrayList.add(new IntPair(Integer.valueOf(i1), Integer.valueOf(i2)));
        }
      }
    }
    this.e = ((IntPair[])localArrayList.toArray(new IntPair[localArrayList.size()]));
    a.debug("Finished to load '" + str + "' map.");
  }
  
  private void m()
  {
    a.debug("Started to add renderers.");
    if (this.c.o())
    {
      a.debug("Adding " + com.a.b.a.a.d.a.class.getSimpleName() + '.');
      this.n.add(new com.a.b.a.a.d.a(this.c));
    }
    File localFile = this.c.s();
    if (localFile != null) {
      try
      {
        a.debug("Adding " + o.class.getSimpleName() + '.');
        this.n.add(new o(localFile, this.c));
      }
      catch (IOException localIOException)
      {
        a.error(String.format("Can't create renderer '%s'.", new Object[] { o.class.getSimpleName() }), localIOException);
        a(String.format("Can't create renderer '%s': %s", new Object[] { o.class.getSimpleName(), ExceptionUtils.getStackTrace(localIOException) }));
      }
    }
    String str = this.c.t();
    if (StringUtils.isNotBlank(str)) {
      try
      {
        a.debug("Adding " + k.class.getSimpleName() + '.');
        this.n.add(new k(str, this.c));
      }
      catch (RuntimeException localRuntimeException)
      {
        a.error(String.format("Can't create renderer '%s'.", new Object[] { k.class.getSimpleName() }), localRuntimeException);
        a(String.format("Can't create renderer '%s': %s", new Object[] { k.class.getSimpleName(), ExceptionUtils.getStackTrace(localRuntimeException) }));
      }
    }
    a.debug("Finished to add renderers.");
  }
  
  private void n()
  {
    a.debug("Started to create static objects.");
    t[][] arrayOft = this.d.b();
    for (int i1 = 0; i1 < this.h; i1++) {
      for (int i2 = 0; i2 < this.i; i2++)
      {
        t localt = arrayOft[i1][i2];
        double d1 = 800.0D * i1;
        double d2 = 800.0D * i2;
        double d3 = d1 + 800.0D;
        double d4 = d2 + 800.0D;
        double d5 = (d1 + d3) / 2.0D;
        double d6 = (d2 + d4) / 2.0D;
        switch (j.b[localt.ordinal()])
        {
        case 1: 
          break;
        case 2: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1 + 80.0D, d2, d1 + 80.0D, d4));
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d3 - 80.0D, d2, d3 - 80.0D, d4));
          break;
        case 3: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1, d2 + 80.0D, d3, d2 + 80.0D));
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1, d4 - 80.0D, d3, d4 - 80.0D));
          break;
        case 4: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1 + 80.0D, d2 + 160.0D, d1 + 80.0D, d4));
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1 + 160.0D, d2 + 80.0D, d3, d2 + 80.0D));
          this.b.a(new com.a.b.a.a.b.d.b.a.b.b(d1 + 160.0D, d2 + 160.0D, 3.141592653589793D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d4, 3.141592653589793D, 1.5707963267948966D));
          break;
        case 5: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d3 - 80.0D, d2 + 160.0D, d3 - 80.0D, d4));
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1, d2 + 80.0D, d3 - 160.0D, d2 + 80.0D));
          this.b.a(new com.a.b.a.a.b.d.b.a.b.b(d3 - 160.0D, d2 + 160.0D, -1.5707963267948966D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d4, -1.5707963267948966D, 1.5707963267948966D));
          break;
        case 6: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1 + 80.0D, d2, d1 + 80.0D, d4 - 160.0D));
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1 + 160.0D, d4 - 80.0D, d3, d4 - 80.0D));
          this.b.a(new com.a.b.a.a.b.d.b.a.b.b(d1 + 160.0D, d4 - 160.0D, 1.5707963267948966D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d2, 1.5707963267948966D, 1.5707963267948966D));
          break;
        case 7: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d3 - 80.0D, d2, d3 - 80.0D, d4 - 160.0D));
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1, d4 - 80.0D, d3 - 160.0D, d4 - 80.0D));
          this.b.a(new com.a.b.a.a.b.d.b.a.b.b(d3 - 160.0D, d4 - 160.0D, 0.0D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d2, 0.0D, 1.5707963267948966D));
          break;
        case 8: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d3 - 80.0D, d2, d3 - 80.0D, d4));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d2, 0.0D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d4, -1.5707963267948966D, 1.5707963267948966D));
          break;
        case 9: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1 + 80.0D, d2, d1 + 80.0D, d4));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d2, 1.5707963267948966D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d4, 3.141592653589793D, 1.5707963267948966D));
          break;
        case 10: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1, d4 - 80.0D, d3, d4 - 80.0D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d2, 0.0D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d2, 1.5707963267948966D, 1.5707963267948966D));
          break;
        case 11: 
          this.b.a(new com.a.b.a.a.b.d.b.b.a(d1, d2 + 80.0D, d3, d2 + 80.0D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d4, -1.5707963267948966D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d4, 3.141592653589793D, 1.5707963267948966D));
          break;
        case 12: 
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d2, 0.0D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d1, d4, -1.5707963267948966D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d2, 1.5707963267948966D, 1.5707963267948966D));
          this.b.a(new com.a.b.a.a.b.d.b.a.a.b(d3, d4, 3.141592653589793D, 1.5707963267948966D));
          break;
        default: 
          throw new IllegalArgumentException("Unsupported tile type: " + localt + '.');
        }
      }
    }
    a.debug("Finished to create static objects.");
  }
  
  private void o()
  {
    a.debug("Started to add players.");
    List localList = this.c.a();
    int i1 = localList.size();
    if ((i1 != 2) && (i1 != 4)) {
      throw new IllegalArgumentException("Unexpected player count: " + i1 + '.');
    }
    if (this.s != null) {
      this.s.shutdown();
    }
    this.s = Executors.newFixedThreadPool(i1, new c(this));
    for (int i2 = 0; i2 < i1; i2++)
    {
      int i3 = this.c.b(i2);
      n localn = com.a.b.a.a.b.e.l.a(this.c, i2, this.c.a(i2), (String)localList.get(i2), i3, this.n);
      localn.a(i3 * (this.j + 1) * 50L + 5000L);
      if (localn.e())
      {
        if (this.p.size() >= 1) {
          throw new IllegalArgumentException(String.format("Can only add %d keyboard player(s).", new Object[] { Integer.valueOf(1) }));
        }
        this.p.add(localn);
      }
      this.o.put(localn, new ArrayList());
    }
    a.debug("Finished to add players.");
  }
  
  private void p()
  {
    a.debug("Sending game contexts.");
    Iterator localIterator = this.o.keySet().iterator();
    while (localIterator.hasNext())
    {
      n localn = (n)localIterator.next();
      com.a.b.a.a.c.l locall = com.a.b.a.a.b.e.g.a(localn.c(), this.j, this.c);
      Future localFuture = this.s.submit(new d(this, localn, locall));
      SimpleMutable localSimpleMutable = new SimpleMutable();
      if (a(localn, localFuture, localSimpleMutable))
      {
        Integer localInteger = (Integer)localSimpleMutable.get();
        if ((!localn.f()) && (!com.a.b.a.a.a.c.a(localInteger)))
        {
          a.warn(String.format("Strategy adapter '%s' returned unsupported protocol version %d.", new Object[] { localn.d().getClass().getSimpleName(), localInteger }));
          localn.a("Процесс стратегии использует устаревшую версию протокола.");
        }
      }
    }
  }
  
  private void q()
  {
    a.debug("Adding player units.");
    int i1 = this.o.size();
    int i2 = 0;
    Iterator localIterator = this.o.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      n localn = (n)localEntry.getKey();
      int i3 = this.c.b(i2);
      com.a.b.a.a.c.d[] arrayOfd = a(i3, this.c.B());
      if ((arrayOfd == null) || (arrayOfd.length != i3)) {
        throw new RuntimeException(String.format("Got %d car types, while %d expected.", new Object[] { Integer.valueOf(ArrayUtils.getLength(arrayOfd)), Integer.valueOf(i3) }));
      }
      for (int i4 = 0; i4 < i3; i4++)
      {
        Point2D localPoint2D = com.a.b.a.a.b.e.b.a(this.d, i2, i1, i3, i4);
        double d1 = com.a.b.a.a.b.e.b.b(this.d, i2, i1, i3, i4);
        Object localObject;
        switch (j.a[arrayOfd[i4].ordinal()])
        {
        case 1: 
          localObject = new com.a.b.a.a.b.d.c.a(localn, i4, localPoint2D.getX(), localPoint2D.getY(), d1, this.d.c()[1]);
          break;
        case 2: 
          localObject = new com.a.b.a.a.b.d.c.c(localn, i4, localPoint2D.getX(), localPoint2D.getY(), d1, this.d.c()[1]);
          break;
        default: 
          throw new IllegalArgumentException("Car type is unsupported: " + arrayOfd[i4] + '.');
        }
        this.b.a((com.a.b.e)localObject);
        ((List)localEntry.getValue()).add(localObject);
      }
      i2++;
    }
  }
  
  private void r()
  {
    a.debug("Adding bonuses.");
    int i1 = NumberUtil.toInt(Math.floor(0.25D * this.e.length));
    for (int i2 = 0; i2 < i1; i2++) {
      s();
    }
  }
  
  private void s()
  {
    com.a.b.a.a.c.b localb = com.a.b.a.a.a.c.a[com.a.a.a.a.c.a(0, com.a.b.a.a.a.c.a.length - 1)];
    double d1 = 570.0D;
    double d2 = 70.0D / Math.SQRT_2;
    for (int i1 = 0; i1 < 100; i1++)
    {
      int i2 = com.a.a.a.a.c.a(0, this.e.length - 1);
      double d3 = com.a.a.a.a.c.d() * d1 + 80.0D + 35.0D;
      double d4 = com.a.a.a.a.c.d() * d1 + 80.0D + 35.0D;
      IntPair localIntPair = this.e[i2];
      Preconditions.checkNotNull(localIntPair.getFirst());
      Preconditions.checkNotNull(localIntPair.getSecond());
      double d5 = ((Integer)localIntPair.getFirst()).intValue() * 800.0D + d3;
      double d6 = ((Integer)localIntPair.getSecond()).intValue() * 800.0D + d4;
      int i3 = 0;
      Iterator localIterator = this.b.a().iterator();
      while (localIterator.hasNext())
      {
        com.a.b.e locale = (com.a.b.e)localIterator.next();
        if ((locale instanceof com.a.b.a.a.b.d.a.a))
        {
          if ((Math.abs(d5 - locale.b().c()) <= 70.0D) && (Math.abs(d6 - locale.b().d()) <= 70.0D))
          {
            i3 = 1;
            break;
          }
        }
        else if ((locale instanceof com.a.b.a.a.b.d.c.b))
        {
          if (com.a.b.a.a.b.e.h.a(locale.b().t()) + d2 >= locale.a(d5, d6))
          {
            i3 = 1;
            break;
          }
        }
        else if ((!(locale instanceof com.a.b.a.a.b.d.e.a)) && (!(locale instanceof com.a.b.a.a.b.d.b.a)) && (!(locale instanceof com.a.b.a.a.b.d.d.a))) {
          throw new IllegalArgumentException("Unsupported unit class: " + locale.getClass() + '.');
        }
      }
      if (i3 == 0)
      {
        this.b.a(new com.a.b.a.a.b.d.a.a(localb, d5, d6));
        return;
      }
    }
  }
  
  private void t()
  {
    a.debug("Adding collision listeners.");
    e locale = new e(this);
    this.b.a(com.a.b.e.class, com.a.b.e.class, new com.a.b.a.a.b.c.f());
    this.b.a(com.a.b.a.a.b.d.e.a.class, com.a.b.a.a.b.d.c.b.class, new com.a.b.a.a.b.c.g(locale));
    this.b.a(com.a.b.a.a.b.d.c.b.class, com.a.b.a.a.b.d.a.a.class, new com.a.b.a.a.b.c.a());
    this.b.a(com.a.b.a.a.b.d.c.b.class, com.a.b.a.a.b.d.c.b.class, new com.a.b.a.a.b.c.d(locale));
    this.b.a(com.a.b.a.a.b.d.c.b.class, com.a.b.a.a.b.d.b.a.class, new com.a.b.a.a.b.c.c(locale));
    this.b.a(com.a.b.a.a.b.d.e.a.class, com.a.b.e.class, new com.a.b.a.a.b.c.i());
    this.b.a(com.a.b.a.a.b.d.c.b.class, com.a.b.a.a.b.d.d.a.class, new com.a.b.a.a.b.c.e());
  }
  
  private void u()
  {
    a.debug("Adding game events.");
    this.q.add(new com.a.b.a.a.b.b.g(this.c));
    this.r.add(new com.a.b.a.a.b.b.g(this.c));
    this.r.add(new com.a.b.a.a.b.b.d(new f(this), this.c));
    this.r.add(new com.a.b.a.a.b.b.h(this.c, new g(this), new h(this)));
    this.r.add(new com.a.b.a.a.b.b.f());
    this.r.add(new com.a.b.a.a.b.b.a(this.e, new i(this)));
    this.r.add(new com.a.b.a.a.b.b.c());
    this.r.add(new com.a.b.a.a.b.b.b());
    this.r.add(new com.a.b.a.a.b.b.e(this.h, this.i));
  }
  
  private static com.a.b.a.a.c.d[] a(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    case 1: 
      return new com.a.b.a.a.c.d[] { paramBoolean ? com.a.b.a.a.c.d.JEEP : com.a.b.a.a.c.d.BUGGY };
    case 2: 
      return new com.a.b.a.a.c.d[] { com.a.b.a.a.c.d.BUGGY, paramBoolean ? new com.a.b.a.a.c.d[] { com.a.b.a.a.c.d.JEEP, com.a.b.a.a.c.d.BUGGY } : com.a.b.a.a.c.d.JEEP };
    }
    throw new IllegalArgumentException("Unsupported team size: " + paramInt + '.');
  }
  
  private boolean v()
  {
    Iterator localIterator1 = this.o.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      if (!((n)localEntry.getKey()).f())
      {
        Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
        while (localIterator2.hasNext())
        {
          com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)localIterator2.next();
          if (!localb.N()) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  private void a(String paramString)
  {
    if (!this.f.getAndSet(true)) {
      this.g.set(paramString);
    }
  }
  
  private com.a.b.a.a.c.h w()
  {
    try
    {
      return q.a(this.v.readLine(), this.w, this.b.a());
    }
    catch (IOException localIOException)
    {
      a(String.format("Can't read world from log file: %s", new Object[] { ExceptionUtils.getStackTrace(localIOException) }));
    }
    return null;
  }
  
  private static final class a
  {
    private final com.a.b.a.a.b.d.c.b a;
    private final m b;
    
    private a(com.a.b.a.a.b.d.c.b paramb, m paramm)
    {
      this.a = paramb;
      this.b = paramm;
    }
    
    private com.a.b.a.a.b.d.c.b a()
    {
      return this.a;
    }
    
    private m b()
    {
      return this.b;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */