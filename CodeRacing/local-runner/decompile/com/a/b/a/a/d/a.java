package com.a.b.a.a.d;

import com.a.b.a.a.c.k;
import com.a.b.a.a.c.o;
import com.a.b.a.a.c.q;
import com.a.b.a.a.c.v;
import com.a.b.a.a.e.a.c.a;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.io.FileUtil;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.process.ThreadUtil;
import com.codeforces.commons.reflection.MethodSignature;
import com.codeforces.commons.reflection.ReflectionUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class a
  implements n
{
  private static final Logger a = LoggerFactory.getLogger(a.class);
  private static final long[] b = { 8L, 10L, 13L, 17L, 34L, 67L, 134L };
  private static final Color c = new Color(0, 0, 0, 17);
  private static final Color d = new Color(255, 153, 32, 255);
  private final com.a.b.a.a.a.b e;
  private final int f;
  private final AtomicLong g = new AtomicLong(Long.MIN_VALUE);
  private final ConcurrentMap h = new ConcurrentHashMap();
  private volatile double i = 5120.0D;
  private volatile double j;
  private volatile double k;
  private volatile double l;
  private final Lock m = new ReentrantLock();
  private BufferedImage n;
  private BufferedImage o;
  private BufferedImage p;
  private Panel q;
  private final BlockingQueue r;
  private final e s;
  private final AtomicLong t = new AtomicLong(17L);
  private final AtomicBoolean u = new AtomicBoolean();
  private final AtomicInteger v = new AtomicInteger();
  private final AtomicInteger w = new AtomicInteger(2);
  private final AtomicBoolean x = new AtomicBoolean();
  private final AtomicBoolean y = new AtomicBoolean(true);
  private final AtomicInteger z = new AtomicInteger();
  private final c.a A = new c.a();
  private final ConcurrentMap B = new ConcurrentHashMap();
  
  public a(com.a.b.a.a.a.b paramb)
  {
    this.e = paramb;
    this.f = paramb.D();
    this.j = (this.i * paramb.c() / paramb.b());
    this.s = a(paramb);
    a(paramb.b(), paramb.c());
    this.r = new LinkedBlockingQueue(paramb.p() ? 1 : 32767);
    new Thread(new b(this, paramb)).start();
  }
  
  private static e a(com.a.b.a.a.a.b paramb)
  {
    File localFile = paramb.m();
    if (FileUtil.isDirectory(localFile))
    {
      ArrayList localArrayList = new ArrayList();
      try
      {
        localArrayList.add(new URLClassLoader(new URL[] { localFile.toURI().toURL() }));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        a.error("Can't convert plugins directory to URL.", localMalformedURLException);
      }
      localArrayList.add(a.class.getClassLoader());
      Object localObject = a("LocalTestRendererListener", localArrayList);
      return new d(localObject, null);
    }
    return new d(null, null);
  }
  
  private static Object a(String paramString, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ClassLoader localClassLoader = (ClassLoader)localIterator.next();
      try
      {
        Class localClass = localClassLoader.loadClass("LocalTestRendererListener");
        return localClass.getConstructor(new Class[0]).newInstance(new Object[0]);
      }
      catch (ClassNotFoundException|InvocationTargetException|NoSuchMethodException|IllegalAccessException|InstantiationException localClassNotFoundException)
      {
        a.error(String.format("Can't create an instance of %s using %s.", new Object[] { paramString, localClassLoader }), localClassNotFoundException);
      }
    }
    return null;
  }
  
  private void a(int paramInt1, int paramInt2)
  {
    this.n = new BufferedImage(paramInt1, paramInt2, 1);
    this.q = new d(this);
    e locale = new e(this);
    this.q.setSize(paramInt1, paramInt2);
    this.q.setPreferredSize(new Dimension(paramInt1, paramInt2));
    this.q.setFocusTraversalKeysEnabled(false);
    this.q.addKeyListener(locale);
    Frame localFrame = new Frame("CodeRacing 2015");
    localFrame.addWindowListener(new f(this));
    localFrame.setFocusTraversalKeysEnabled(false);
    localFrame.addKeyListener(locale);
    localFrame.add(this.q);
    localFrame.setResizable(false);
    localFrame.setVisible(true);
    localFrame.pack();
    d();
  }
  
  public void a(com.a.b.a.a.c.h paramh)
  {
    b(paramh);
  }
  
  public void close()
  {
    b(null);
  }
  
  private void b(com.a.b.a.a.c.h paramh)
  {
    for (;;)
    {
      try
      {
        this.r.put(new f(paramh, null));
        return;
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  private void d()
  {
    Graphics localGraphics = a(this.n.getGraphics());
    localGraphics.setColor(Color.WHITE);
    localGraphics.fillRect(0, 0, this.n.getWidth(), this.n.getHeight());
    localGraphics.setColor(Color.BLACK);
    localGraphics.setFont(new Font("Times New Roman", 1, a(0.0D, 30.0D).b()));
    FontMetrics localFontMetrics = localGraphics.getFontMetrics();
    String str1 = "Waiting for game client to connect...";
    int i1 = localFontMetrics.stringWidth(str1);
    localGraphics.drawString(str1, (this.q.getWidth() - i1) / 2, this.q.getHeight() / 2 - localFontMetrics.getHeight());
    String str2 = "Ожидание подключения стратегии...";
    int i2 = localFontMetrics.stringWidth(str2);
    localGraphics.drawString(str2, (this.q.getWidth() - i2) / 2, this.q.getHeight() / 2);
    this.q.repaint();
  }
  
  private void a(f paramf)
  {
    e();
    com.a.b.a.a.c.h localh = paramf.a();
    Preconditions.checkNotNull(localh);
    com.a.b.a.a.c.l locall = com.a.b.a.a.b.e.g.a(0L, localh.getTickCount(), this.e);
    Graphics localGraphics = a(this.n.getGraphics());
    if (this.f > 0) {
      localGraphics.setColor(com.a.a.a.a.a.a(Color.WHITE, 255 - this.f));
    } else {
      localGraphics.setColor(Color.WHITE);
    }
    localGraphics.fillRect(0, 0, this.n.getWidth(), this.n.getHeight());
    localGraphics.setColor(Color.BLACK);
    e(localh);
    c(localh);
    d(localh);
    this.s.a(localGraphics, localh, locall, this.n.getWidth(), this.n.getHeight(), this.k, this.l, this.i, this.j);
    a(localh, localGraphics);
    if (this.w.get() >= 2) {
      b(localh, localGraphics);
    }
    d(localh, localGraphics);
    e(localh, localGraphics);
    c(localh, localGraphics);
    f(localh, localGraphics);
    g(localh, localGraphics);
    h(localh, localGraphics);
    i(localh, localGraphics);
    this.s.b(localGraphics, localh, locall, this.n.getWidth(), this.n.getHeight(), this.k, this.l, this.i, this.j);
    if ((this.y.get()) && (this.o != null))
    {
      localGraphics.drawImage(this.o, 0, this.n.getHeight() - this.o.getHeight(), null);
      localGraphics.drawImage(this.p, 0, this.n.getHeight() - this.p.getHeight(), null);
    }
    this.q.getGraphics().drawImage(this.n, 0, 0, null);
  }
  
  private void e()
  {
    while ((this.u.get()) && (this.v.getAndDecrement() <= 0))
    {
      this.v.incrementAndGet();
      ThreadUtil.sleep(this.t.get());
    }
    this.v.set(0);
  }
  
  private void c(com.a.b.a.a.c.h paramh)
  {
    if (this.o == null)
    {
      Object localObject1 = (paramh.getSystemData() instanceof List) ? (List)paramh.getSystemData() : null;
      if (localObject1 == null) {
        return;
      }
      c localc = a(Math.min(paramh.getWidth(), 8) * 32, Math.min(paramh.getHeight(), 8) * 32);
      this.o = new BufferedImage(localc.a(), localc.b(), 2);
      Graphics localGraphics = a(this.o.getGraphics());
      localGraphics.setColor(new Color(245, 245, 245, 192));
      localGraphics.fillRect(0, 0, this.o.getWidth(), this.o.getHeight());
      localGraphics.setColor(Color.BLACK);
      Iterator localIterator = ((List)localObject1).iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        a(localGraphics, paramh, localObject2, this.o.getWidth(), this.o.getHeight());
      }
    }
  }
  
  private void d(com.a.b.a.a.c.h paramh)
  {
    if (this.p == null)
    {
      c localc1 = a(Math.min(paramh.getWidth(), 8) * 32, Math.min(paramh.getHeight(), 8) * 32);
      this.p = new BufferedImage(localc1.a(), localc1.b(), 2);
    }
    int i1 = this.p.getWidth();
    int i2 = this.p.getHeight();
    Graphics localGraphics = a(this.p.getGraphics());
    if ((localGraphics instanceof Graphics2D))
    {
      Graphics2D localGraphics2D = (Graphics2D)localGraphics;
      localGraphics2D.setBackground(new Color(0, 0, 0, 0));
      localGraphics.clearRect(0, 0, i1, i2);
    }
    double d1 = paramh.getWidth() * 800.0D;
    double d2 = paramh.getHeight() * 800.0D;
    com.a.b.a.a.c.c[] arrayOfc1 = paramh.getCars();
    Arrays.sort(arrayOfc1, new g(this));
    for (com.a.b.a.a.c.c localc : arrayOfc1)
    {
      localGraphics.setColor(a(localc.getPlayerId()));
      double d3 = com.a.b.a.a.b.e.h.a(new com.a.c.a.e(localc.getWidth(), localc.getHeight())) * 1.5D;
      c localc2 = a(localc.getX() - d3, localc.getY() - d3, 0.0D, 0.0D, d1, d2, i1, i2);
      c localc3 = a(2.0D * d3, 2.0D * d3, d1, d2, i1, i2);
      localGraphics.fillOval(localc2.a(), localc2.b(), localc3.a(), localc3.b());
    }
  }
  
  private static void a(Graphics paramGraphics, com.a.b.a.a.c.h paramh, Object paramObject, int paramInt1, int paramInt2)
  {
    double d1 = paramh.getWidth() * 800.0D;
    double d2 = paramh.getHeight() * 800.0D;
    Object localObject1;
    Object localObject2;
    if ((paramObject instanceof com.a.b.a.a.b.d.b.b.b))
    {
      localObject1 = (com.a.b.a.a.b.d.b.b.b)paramObject;
      localObject2 = (com.a.c.a.d)((com.a.b.a.a.b.d.b.b.b)localObject1).b().t();
      a(paramGraphics, (com.a.c.a.d)localObject2, d1, d2, paramInt1, paramInt2);
      return;
    }
    if ((paramObject instanceof com.a.b.a.a.b.d.b.a.b.a))
    {
      localObject1 = (com.a.b.a.a.b.d.b.a.b.a)paramObject;
      localObject2 = (com.a.c.a.a)((com.a.b.a.a.b.d.b.a.b.a)localObject1).b().t();
      a(paramGraphics, (com.a.b.a.a.b.d.b.a.b.a)localObject1, (com.a.c.a.a)localObject2, d1, d2, paramInt1, paramInt2);
      return;
    }
    if ((paramObject instanceof com.a.b.a.a.b.d.b.a.a.b))
    {
      localObject1 = (com.a.b.a.a.b.d.b.a.a.b)paramObject;
      if (((com.a.b.a.a.b.d.b.a.a.b)localObject1).k() != null)
      {
        a(paramGraphics, paramh, ((com.a.b.a.a.b.d.b.a.a.b)localObject1).k(), paramInt1, paramInt2);
        return;
      }
      localObject2 = (com.a.c.a.b)((com.a.b.a.a.b.d.b.a.a.b)localObject1).b().t();
      a(paramGraphics, (com.a.b.a.a.b.d.b.a.a.b)localObject1, (com.a.c.a.b)localObject2, d1, d2, paramInt1, paramInt2);
    }
  }
  
  private static void a(Graphics paramGraphics, com.a.c.a.d paramd, double paramDouble1, double paramDouble2, int paramInt1, int paramInt2)
  {
    c localc1 = a(paramd.a(), paramd.b(), 0.0D, 0.0D, paramDouble1, paramDouble2, paramInt1, paramInt2);
    c localc2 = a(paramd.c(), paramd.f(), 0.0D, 0.0D, paramDouble1, paramDouble2, paramInt1, paramInt2);
    paramGraphics.drawLine(localc1.a(), localc1.b(), localc2.a(), localc2.b());
  }
  
  private static void a(Graphics paramGraphics, com.a.b.a.a.b.d.b.a.b.a parama, com.a.c.a.a parama1, double paramDouble1, double paramDouble2, int paramInt1, int paramInt2)
  {
    double d1 = parama.b().c();
    double d2 = parama.b().d();
    double d3 = -parama.b().e() - parama1.b();
    double d4 = -parama1.c();
    Vector2D localVector2D1 = new Vector2D(parama1.a(), 0.0D).rotate(-d3);
    Vector2D localVector2D2 = new Vector2D(parama1.a(), 0.0D).rotate(-d3 - d4);
    c localc1 = a(d1 + localVector2D1.getX(), d2 + localVector2D1.getY(), 0.0D, 0.0D, paramDouble1, paramDouble2, paramInt1, paramInt2);
    c localc2 = a(d1 + localVector2D2.getX(), d2 + localVector2D2.getY(), 0.0D, 0.0D, paramDouble1, paramDouble2, paramInt1, paramInt2);
    paramGraphics.drawLine(localc1.a(), localc1.b(), localc2.a(), localc2.b());
  }
  
  private static void a(Graphics paramGraphics, com.a.b.a.a.b.d.b.a.a.b paramb, com.a.c.a.b paramb1, double paramDouble1, double paramDouble2, int paramInt1, int paramInt2)
  {
    double d1 = paramb.b().c();
    double d2 = paramb.b().d();
    double d3 = paramb1.a();
    c localc1 = a(d1 - d3, d2 - d3, 0.0D, 0.0D, paramDouble1, paramDouble2, paramInt1, paramInt2);
    c localc2 = a(2.0D * d3, 2.0D * d3, paramDouble1, paramDouble2, paramInt1, paramInt2);
    paramGraphics.drawOval(localc1.a(), localc1.b(), localc2.a(), localc2.b());
  }
  
  private void e(com.a.b.a.a.c.h paramh)
  {
    com.a.b.a.a.c.c[] arrayOfc = paramh.getCars();
    int i1 = arrayOfc.length;
    if (this.g.get() == Long.MIN_VALUE)
    {
      Arrays.sort(arrayOfc, new h(this));
      for (int i2 = 0; i2 < i1; i2++) {
        this.h.putIfAbsent(Integer.valueOf(i2), Long.valueOf(arrayOfc[i2].getId()));
      }
      localObject1 = null;
      Map localMap = paramh.getDecoratedPlayerById();
      if (localMap != null) {
        for (o localo : paramh.getPlayersUnsafe())
        {
          com.a.b.a.a.c.g localg = (com.a.b.a.a.c.g)localMap.get(Long.valueOf(localo.getId()));
          if ((localg != null) && (localg.isKeyboardPlayer()))
          {
            localObject1 = localo;
            break;
          }
        }
      }
      if (localObject1 == null)
      {
        if (i1 > 0) {
          this.g.set(arrayOfc[0].getId());
        }
      }
      else {
        for (int i4 = 0; i4 < i1; i4++)
        {
          com.a.b.a.a.c.c localc2 = arrayOfc[i4];
          if ((localc2.getPlayerId() == ((o)localObject1).getId()) && (localc2.getTeammateIndex() == 0))
          {
            this.g.set(localc2.getId());
            break;
          }
        }
      }
    }
    if (this.g.get() == Long.MIN_VALUE) {
      return;
    }
    Object localObject1 = null;
    for (int i3 = 0; i3 < i1; i3++)
    {
      com.a.b.a.a.c.c localc1 = arrayOfc[i3];
      if (localc1.getId() == this.g.get())
      {
        localObject1 = localc1;
        break;
      }
    }
    if (localObject1 == null) {
      return;
    }
    this.m.lock();
    try
    {
      this.k = (((com.a.b.a.a.c.c)localObject1).getX() - this.i * 0.5D);
      if ((this.k < 0.0D) || (this.i >= paramh.getWidth() * 800.0D)) {
        this.k = 0.0D;
      } else if (this.k + this.i > paramh.getWidth() * 800.0D) {
        this.k = (paramh.getWidth() * 800.0D - this.i);
      }
      this.l = (((com.a.b.a.a.c.c)localObject1).getY() - this.j * 0.5D);
      if ((this.l < 0.0D) || (this.j >= paramh.getHeight() * 800.0D)) {
        this.l = 0.0D;
      } else if (this.l + this.j > paramh.getHeight() * 800.0D) {
        this.l = (paramh.getHeight() * 800.0D - this.j);
      }
    }
    finally
    {
      this.m.unlock();
    }
  }
  
  private void a(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    Object localObject1 = (paramh.getSystemData() instanceof List) ? (List)paramh.getSystemData() : null;
    if (localObject1 != null)
    {
      Iterator localIterator = ((List)localObject1).iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        a(paramGraphics, localObject2);
      }
    }
  }
  
  private void a(Graphics paramGraphics, Object paramObject)
  {
    Object localObject1;
    Object localObject2;
    if ((paramObject instanceof com.a.b.a.a.b.d.b.b.b))
    {
      localObject1 = (com.a.b.a.a.b.d.b.b.b)paramObject;
      localObject2 = (com.a.c.a.d)((com.a.b.a.a.b.d.b.b.b)localObject1).b().t();
      a(paramGraphics, ((com.a.c.a.d)localObject2).a(), ((com.a.c.a.d)localObject2).b(), ((com.a.c.a.d)localObject2).c(), ((com.a.c.a.d)localObject2).f());
      return;
    }
    if ((paramObject instanceof com.a.b.a.a.b.d.b.a.b.a))
    {
      localObject1 = (com.a.b.a.a.b.d.b.a.b.a)paramObject;
      localObject2 = (com.a.c.a.a)((com.a.b.a.a.b.d.b.a.b.a)localObject1).b().t();
      a(paramGraphics, ((com.a.b.a.a.b.d.b.a.b.a)localObject1).b().c(), ((com.a.b.a.a.b.d.b.a.b.a)localObject1).b().d(), ((com.a.c.a.a)localObject2).a(), -((com.a.b.a.a.b.d.b.a.b.a)localObject1).b().e() - ((com.a.c.a.a)localObject2).b(), -((com.a.c.a.a)localObject2).c());
      return;
    }
    if ((paramObject instanceof com.a.b.a.a.b.d.b.a.a.b))
    {
      localObject1 = (com.a.b.a.a.b.d.b.a.a.b)paramObject;
      if (((com.a.b.a.a.b.d.b.a.a.b)localObject1).k() == null)
      {
        localObject2 = (com.a.c.a.b)((com.a.b.a.a.b.d.b.a.a.b)localObject1).b().t();
        b(paramGraphics, ((com.a.b.a.a.b.d.b.a.a.b)localObject1).b().c(), ((com.a.b.a.a.b.d.b.a.a.b)localObject1).b().d(), ((com.a.c.a.b)localObject2).a());
      }
      else
      {
        a(paramGraphics, ((com.a.b.a.a.b.d.b.a.a.b)localObject1).k());
      }
    }
  }
  
  private void b(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    Color localColor = paramGraphics.getColor();
    paramGraphics.setColor(c);
    double d1;
    for (int i1 = 0; i1 <= paramh.getWidth(); i1++)
    {
      d1 = 800.0D * i1;
      a(paramGraphics, d1, 0.0D, d1, 800.0D * paramh.getHeight());
    }
    for (i1 = 0; i1 <= paramh.getHeight(); i1++)
    {
      d1 = 800.0D * i1;
      a(paramGraphics, 0.0D, d1, 800.0D * paramh.getWidth(), d1);
    }
    paramGraphics.setColor(localColor);
  }
  
  private void f(com.a.b.a.a.c.h paramh)
  {
    Graphics localGraphics = a(this.n.getGraphics());
    Font localFont = new Font("Courier New", 1, a(0.0D, 48.0D).b());
    o[] arrayOfo = paramh.getPlayers();
    Arrays.sort(arrayOfo, com.a.b.a.a.b.e.l.b());
    int i1 = 1;
    c localc = a(200.0D, 400.0D - 37.5D * arrayOfo.length);
    int i2 = a(0.0D, 75.0D).b();
    Color localColor = localGraphics.getColor();
    int i3 = 0;
    int i4 = arrayOfo.length;
    while (i3 < i4)
    {
      o localo = arrayOfo[i3];
      int i5;
      if ((i3 == 0) || (localo.getScore() != arrayOfo[(i3 - 1)].getScore()))
      {
        i5 = i3 + 1;
        i1 = i5;
      }
      else
      {
        i5 = i1;
      }
      String str = String.format("%d. %-20s: %d", new Object[] { Integer.valueOf(i5), localo.getName(), Integer.valueOf(localo.getScore()) });
      int i6 = localc.a();
      int i7 = localc.b() + i2 * i3;
      localGraphics.setColor(new Color(255, 255, 255, 64));
      localGraphics.setFont(localFont);
      for (int i8 = -2; i8 <= 2; i8++) {
        for (int i9 = -2; i9 <= 2; i9++) {
          if (((Math.abs(i8) != 2) || (Math.abs(i9) != 2)) && ((i8 != 0) || (i9 != 0))) {
            localGraphics.drawString(str, i6 + i8, i7 + i9);
          }
        }
      }
      localGraphics.setColor(a(localo.getId()));
      localGraphics.setFont(localFont);
      localGraphics.drawString(str, i6, i7);
      i3++;
    }
    localGraphics.setColor(localColor);
    this.q.repaint();
  }
  
  private void c(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    Color localColor = paramGraphics.getColor();
    for (com.a.b.a.a.c.c localc : paramh.getCarsUnsafe())
    {
      a(paramh, localc);
      if (localc.getRemainingNitroTicks() > 0)
      {
        ConcurrentMap localConcurrentMap = (ConcurrentMap)this.B.get(Long.valueOf(localc.getId()));
        for (int i3 = Math.max(paramh.getTick() - Math.min(10, 120 - localc.getRemainingNitroTicks()), 0); i3 < paramh.getTick(); i3++) {
          a(paramh, paramGraphics, localc, (b)localConcurrentMap.get(Integer.valueOf(i3)), Integer.valueOf(110 - (paramh.getTick() - i3) * 10));
        }
      }
      a(paramh, paramGraphics, localc, null, null);
    }
    paramGraphics.setColor(localColor);
  }
  
  private void a(com.a.b.a.a.c.h paramh, com.a.b.a.a.c.c paramc)
  {
    ConcurrentMap localConcurrentMap = (ConcurrentMap)this.B.get(Long.valueOf(paramc.getId()));
    if (localConcurrentMap == null)
    {
      this.B.putIfAbsent(Long.valueOf(paramc.getId()), new ConcurrentHashMap());
      localConcurrentMap = (ConcurrentMap)this.B.get(Long.valueOf(paramc.getId()));
    }
    localConcurrentMap.put(Integer.valueOf(paramh.getTick()), new b(new Point2D(paramc.getX(), paramc.getY()), paramc.getAngle(), null));
  }
  
  private void a(com.a.b.a.a.c.h paramh, Graphics paramGraphics, com.a.b.a.a.c.c paramc, b paramb, Integer paramInteger)
  {
    Color localColor = a(paramc.getPlayerId());
    if ((paramb != null) && (paramInteger != null)) {
      localColor = com.a.a.a.a.a.a(localColor, paramInteger.intValue());
    }
    if ((paramc.isFinishedTrack()) || (com.a.b.a.a.b.e.b.a(paramc))) {
      localColor = com.a.a.a.a.a.a(localColor, localColor.getAlpha() / 2);
    }
    paramGraphics.setColor(localColor);
    Point2D localPoint2D = paramb == null ? new Point2D(paramc.getX(), paramc.getY()) : paramb.a();
    double d1 = paramb == null ? paramc.getAngle() : paramb.b();
    a(paramGraphics, com.a.b.a.a.b.e.h.a(localPoint2D, new Vector2D(paramc.getWidth(), paramc.getHeight()), d1));
    Vector2D localVector2D = new Vector2D(1.0D, 0.0D).rotate(d1);
    switch (c.a[paramc.getType().ordinal()])
    {
    case 1: 
      a(paramGraphics, com.a.b.a.a.b.e.h.a(localPoint2D.copy().add(localVector2D.copy().multiply(-0.15D * paramc.getWidth())), new Vector2D(0.5D * paramc.getWidth(), 0.5D * paramc.getHeight()), d1));
      break;
    case 2: 
      a(paramGraphics, com.a.b.a.a.b.e.h.a(localPoint2D.copy().add(localVector2D.copy().multiply(-0.3D * paramc.getWidth())), new Vector2D(0.2D * paramc.getWidth(), 0.8D * paramc.getHeight()), d1));
      a(paramGraphics, com.a.b.a.a.b.e.h.a(localPoint2D.copy().add(localVector2D.copy().multiply(-0.0D * paramc.getWidth())), new Vector2D(0.2D * paramc.getWidth(), 0.8D * paramc.getHeight()), d1));
      break;
    default: 
      throw new IllegalArgumentException("Unsupported car type: " + paramc.getType() + '.');
    }
    c(paramGraphics, paramc);
    b(paramh, paramGraphics, paramc);
    c(paramh, paramGraphics, paramc);
    if ((paramb == null) && (this.w.get() >= 2)) {
      a(paramh, paramGraphics, paramc);
    }
    if ((paramb == null) && (this.x.get()))
    {
      a(paramGraphics, paramc);
      b(paramGraphics, paramc);
    }
  }
  
  private void a(com.a.b.a.a.c.h paramh, Graphics paramGraphics, com.a.b.a.a.c.c paramc)
  {
    double d1 = 0.5D * Math.hypot(paramc.getWidth(), paramc.getHeight());
    double d2 = 0.5D * Math.min(paramc.getWidth(), paramc.getHeight());
    double d3 = paramc.getDurability();
    c localc = c(paramc.getX() - d2, paramc.getY() - d1 - 2.0D);
    paramGraphics.setColor(d3 > 0.3D ? Color.ORANGE : d3 > 0.7D ? Color.GREEN : Color.RED);
    a(paramGraphics, d2, d3, localc);
  }
  
  private void a(Graphics paramGraphics, double paramDouble1, double paramDouble2, c paramc)
  {
    c localc = b(2.0D * paramDouble1 * paramDouble2, 3.0D);
    paramGraphics.fillRect(paramc.a(), paramc.b(), localc.a(), 3);
  }
  
  private void a(Graphics paramGraphics, com.a.b.a.a.c.c paramc)
  {
    double d1 = 0.5D * Math.hypot(paramc.getWidth(), paramc.getHeight());
    double d2 = 0.5D * Math.min(paramc.getWidth(), paramc.getHeight());
    paramGraphics.setColor(Color.BLACK);
    paramGraphics.setFont(new Font("Times New Roman", 1, b(0.0D, 0.7D * d2).b()));
    String str = NumberUtil.toInt(Math.floor(paramc.getDurability() * 100.0D)) + "%";
    c localc = c(paramc.getX(), paramc.getY() - d1 - 10.0D);
    paramGraphics.drawString(str, localc.a() - paramGraphics.getFontMetrics().stringWidth(str) / 2, localc.b());
  }
  
  private void b(Graphics paramGraphics, com.a.b.a.a.c.c paramc)
  {
    double d1 = 0.5D * Math.hypot(paramc.getWidth(), paramc.getHeight());
    double d2 = 0.5D * Math.min(paramc.getWidth(), paramc.getHeight());
    double d3 = 0.35D * d2;
    int[] arrayOfInt = { paramc.getProjectileCount(), paramc.getNitroChargeCount(), paramc.getOilCanisterCount() };
    int i1 = arrayOfInt.length;
    Point2D[] arrayOfPoint2D = { a(paramGraphics, paramc, d1, d2, d3), a(paramGraphics, paramc, d1, d3), b(paramGraphics, paramc, d1, d2, d3) };
    paramGraphics.setColor(Color.BLACK);
    paramGraphics.setFont(new Font("Times New Roman", 1, b(0.0D, 2.0D * d3).b()));
    for (int i2 = 0; i2 < i1; i2++)
    {
      Point2D localPoint2D = arrayOfPoint2D[i2];
      c localc = c(localPoint2D.getX() + d3 + 5.0D, localPoint2D.getY() + 0.75D * d3);
      paramGraphics.drawString(Integer.toString(arrayOfInt[i2]), localc.a(), localc.b());
    }
  }
  
  private Point2D a(Graphics paramGraphics, com.a.b.a.a.c.c paramc, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    Point2D localPoint2D = new Point2D(paramc.getX() + paramDouble1 + 2.0D + paramDouble3, paramc.getY() - 0.85D * paramDouble2);
    paramGraphics.setColor(Color.BLACK);
    a(paramGraphics, localPoint2D, paramDouble3);
    paramGraphics.setColor(Color.WHITE);
    a(paramGraphics, localPoint2D, paramDouble3 / 2.0D);
    return localPoint2D;
  }
  
  private Point2D a(Graphics paramGraphics, com.a.b.a.a.c.c paramc, double paramDouble1, double paramDouble2)
  {
    Point2D localPoint2D1 = new Point2D(paramc.getX() + paramDouble1 + 2.0D + paramDouble2, paramc.getY());
    paramGraphics.setColor(Color.BLACK);
    double d1 = paramDouble2 / 2.0D;
    for (int i1 = 0; i1 < 4; i1++)
    {
      Point2D localPoint2D2 = localPoint2D1.copy().add(-paramDouble2 / 2.0D + d1 * i1, 0.0D);
      double d2 = -paramDouble2 + d1 * i1;
      a(paramGraphics, localPoint2D2, localPoint2D1.copy().add(d2, -paramDouble2));
      a(paramGraphics, localPoint2D2, localPoint2D1.copy().add(d2, paramDouble2));
    }
    return localPoint2D1;
  }
  
  private Point2D b(Graphics paramGraphics, com.a.b.a.a.c.c paramc, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d1 = paramDouble3 / 2.0D;
    Point2D localPoint2D = new Point2D(paramc.getX() + paramDouble1 + 2.0D + paramDouble3, paramc.getY() + 0.85D * paramDouble2);
    paramGraphics.setColor(Color.BLACK);
    b(paramGraphics, localPoint2D.getX() - paramDouble3, localPoint2D.getY() - paramDouble3, 2.0D * paramDouble3, 2.0D * paramDouble3);
    b(paramGraphics, localPoint2D.getX() - d1, localPoint2D.getY() - d1, 2.0D * d1, 2.0D * d1);
    a(paramGraphics, localPoint2D.copy().add(-paramDouble3, paramDouble3), localPoint2D.copy().add(-d1, d1));
    a(paramGraphics, localPoint2D.copy().add(-paramDouble3, -paramDouble3), localPoint2D.copy().add(-d1, -d1));
    a(paramGraphics, localPoint2D.copy().add(paramDouble3, -paramDouble3), localPoint2D.copy().add(d1, -d1));
    a(paramGraphics, localPoint2D.copy().add(paramDouble3, paramDouble3), localPoint2D.copy().add(d1, d1));
    return localPoint2D;
  }
  
  private void c(Graphics paramGraphics, com.a.b.a.a.c.c paramc)
  {
    Vector2D localVector2D = new Vector2D(paramc.getWidth() * 0.15D, paramc.getHeight() * 0.05D);
    double d1 = paramc.getWheelTurn() * 0.5235987755982988D;
    double d2 = 0.5D * paramc.getWidth() - 1.5D * localVector2D.getX();
    double d3 = 0.5D * paramc.getHeight();
    Point2D[] arrayOfPoint2D1 = com.a.b.a.a.b.e.h.a(new Point2D(paramc.getX(), paramc.getY()).add(new Vector2D(d2, d3).rotate(paramc.getAngle())), localVector2D, paramc.getAngle() + d1);
    a(paramGraphics, arrayOfPoint2D1);
    Point2D[] arrayOfPoint2D2 = com.a.b.a.a.b.e.h.a(new Point2D(paramc.getX(), paramc.getY()).add(new Vector2D(d2, -d3).rotate(paramc.getAngle())), localVector2D, paramc.getAngle() + d1);
    a(paramGraphics, arrayOfPoint2D2);
    Point2D[] arrayOfPoint2D3 = com.a.b.a.a.b.e.h.a(new Point2D(paramc.getX(), paramc.getY()).add(new Vector2D(-d2, d3).rotate(paramc.getAngle())), localVector2D, paramc.getAngle() - 0.0D);
    a(paramGraphics, arrayOfPoint2D3);
    Point2D[] arrayOfPoint2D4 = com.a.b.a.a.b.e.h.a(new Point2D(paramc.getX(), paramc.getY()).add(new Vector2D(-d2, -d3).rotate(paramc.getAngle())), localVector2D, paramc.getAngle() - 0.0D);
    a(paramGraphics, arrayOfPoint2D4);
  }
  
  private void b(com.a.b.a.a.c.h paramh, Graphics paramGraphics, com.a.b.a.a.c.c paramc)
  {
    if (paramc.getWidth() <= paramc.getHeight()) {
      return;
    }
    if (!(paramGraphics instanceof Graphics2D)) {
      return;
    }
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    Map localMap = paramh.getDecoratedCarById();
    com.a.b.a.a.c.f localf = localMap == null ? null : (com.a.b.a.a.c.f)localMap.get(Long.valueOf(paramc.getId()));
    int i1 = (localf != null) && (localf.getBrakes() != null) && (localf.getBrakes().booleanValue()) ? 1 : 0;
    int i2 = (localf != null) && (localf.getRemainingHitRecoverTicks() != null) ? 1 : 0;
    int i3 = (paramh.getTick() + paramc.getId() * 3L) / 15L % 2L == 0L ? 1 : 0;
    double d1 = 0.5D + 0.2D * paramc.getEnginePower();
    double d2 = paramc.getHeight() / 12.0D;
    double d3 = paramc.getWidth() * 0.5D - paramc.getHeight() * (i2 != 0 ? 0.3D : 0.2D) - d2;
    double d4 = paramc.getHeight() * 0.12D;
    Point2D localPoint2D1 = new Point2D(paramc.getX(), paramc.getY()).add(new Vector2D(d3, d4).rotate(paramc.getAngle()));
    if (i1 != 0) {
      a(localGraphics2D, localPoint2D1, paramc.getAngle());
    }
    double d5 = i2 != 0 ? (i3 != 0 ? 1.2D : 0.8D) * d2 : d2;
    b(paramGraphics, localPoint2D1, d5);
    a(paramGraphics, localPoint2D1, d1 * d5);
    if (i1 != 0) {
      localGraphics2D.setTransform(new AffineTransform());
    }
    Point2D localPoint2D2 = new Point2D(paramc.getX(), paramc.getY()).add(new Vector2D(d3, -d4).rotate(paramc.getAngle()));
    if (i1 != 0) {
      a(localGraphics2D, localPoint2D2, paramc.getAngle());
    }
    double d6 = i2 != 0 ? (i3 != 0 ? 0.8D : 1.2D) * d2 : d2;
    b(paramGraphics, localPoint2D2, d6);
    a(paramGraphics, localPoint2D2, d1 * d6);
    if (i1 != 0) {
      localGraphics2D.setTransform(new AffineTransform());
    }
  }
  
  private void a(Graphics2D paramGraphics2D, Point2D paramPoint2D, double paramDouble)
  {
    Point2D localPoint2D = d(paramPoint2D.getX(), paramPoint2D.getY());
    paramGraphics2D.translate(localPoint2D.getX(), localPoint2D.getY());
    paramGraphics2D.rotate(paramDouble);
    paramGraphics2D.scale(0.3D, 1.25D);
    paramGraphics2D.translate(-localPoint2D.getX(), -localPoint2D.getY());
  }
  
  private void c(com.a.b.a.a.c.h paramh, Graphics paramGraphics, com.a.b.a.a.c.c paramc)
  {
    if (paramc.getWidth() <= paramc.getHeight()) {
      return;
    }
    Map localMap = paramh.getDecoratedCarById();
    com.a.b.a.a.c.f localf = localMap == null ? null : (com.a.b.a.a.c.f)localMap.get(Long.valueOf(paramc.getId()));
    int i1 = (localf != null) && (localf.getRemainingHitRecoverTicks() != null) ? 1 : 0;
    double d1 = paramc.getHeight() / 6.0D;
    double d2;
    double d3;
    if (i1 != 0)
    {
      d2 = d1;
      d3 = paramc.getWidth() * 0.5D - paramc.getHeight() * 0.2D + d2;
    }
    else
    {
      d2 = d1 + (1.0D - paramc.getDurability()) * paramc.getHeight() / 2.0D;
      d3 = paramc.getWidth() * 0.5D - paramc.getHeight() * 0.1D - d2;
    }
    Vector2D localVector2D = new Vector2D(d3, 0.0D).rotate(paramc.getAngle()).add(paramc.getX(), paramc.getY());
    a(paramGraphics, localVector2D.getX(), localVector2D.getY(), d2, (i1 != 0 ? 3.141592653589793D : 0.0D) - paramc.getAngle() - 0.7853981633974483D * d1 / d2, 1.5707963267948966D * d1 / d2);
  }
  
  private void d(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    Color localColor = paramGraphics.getColor();
    for (com.a.b.a.a.c.n localn : paramh.getOilSlicksUnsafe()) {
      a(paramGraphics, localn);
    }
    paramGraphics.setColor(localColor);
  }
  
  private void a(Graphics paramGraphics, com.a.b.a.a.c.n paramn)
  {
    paramGraphics.setColor(com.a.a.a.a.a.a(Color.BLACK, NumberUtil.toInt(255.0D * (paramn.getRemainingLifetime() / 600.0D))));
    a(paramGraphics, paramn.getX(), paramn.getY(), paramn.getRadius());
  }
  
  private void e(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    Color localColor = paramGraphics.getColor();
    for (com.a.b.a.a.c.a locala : paramh.getBonusesUnsafe()) {
      a(paramGraphics, locala);
    }
    paramGraphics.setColor(localColor);
  }
  
  private void a(Graphics paramGraphics, com.a.b.a.a.c.a parama)
  {
    switch (c.b[parama.getType().ordinal()])
    {
    case 1: 
      b(paramGraphics, parama);
      break;
    case 2: 
      c(paramGraphics, parama);
      break;
    case 3: 
      d(paramGraphics, parama);
      break;
    case 4: 
      e(paramGraphics, parama);
      break;
    case 5: 
      f(paramGraphics, parama);
      break;
    default: 
      throw new IllegalArgumentException("Unsupported bonus type: " + parama.getType() + '.');
    }
    paramGraphics.setColor(Color.BLACK);
    b(paramGraphics, parama.getX() - parama.getWidth() / 2.0D, parama.getY() - parama.getHeight() / 2.0D, parama.getWidth(), parama.getHeight());
  }
  
  private void b(Graphics paramGraphics, com.a.b.a.a.c.a parama)
  {
    paramGraphics.setColor(new Color(14487062));
    double d1 = Math.min(parama.getWidth(), parama.getHeight()) * 0.4D;
    double d2 = Math.min(parama.getWidth(), parama.getHeight()) * 0.1D;
    a(paramGraphics, new Point2D[] { new Point2D(parama.getX() - d1, parama.getY() - d2), new Point2D(parama.getX() - d2, parama.getY() - d2), new Point2D(parama.getX() - d2, parama.getY() - d1), new Point2D(parama.getX() + d2, parama.getY() - d1), new Point2D(parama.getX() + d2, parama.getY() - d2), new Point2D(parama.getX() + d1, parama.getY() - d2), new Point2D(parama.getX() + d1, parama.getY() + d2), new Point2D(parama.getX() + d2, parama.getY() + d2), new Point2D(parama.getX() + d2, parama.getY() + d1), new Point2D(parama.getX() - d2, parama.getY() + d1), new Point2D(parama.getX() - d2, parama.getY() + d2), new Point2D(parama.getX() - d1, parama.getY() + d2) });
  }
  
  private void c(Graphics paramGraphics, com.a.b.a.a.c.a parama)
  {
    paramGraphics.setColor(new Color(2367521));
    a(paramGraphics, parama.getX(), parama.getY(), Math.min(parama.getWidth(), parama.getHeight()) / 2.0D);
    paramGraphics.setColor(Color.WHITE);
    a(paramGraphics, parama.getX(), parama.getY(), Math.min(parama.getWidth(), parama.getHeight()) / 4.0D);
  }
  
  private void d(Graphics paramGraphics, com.a.b.a.a.c.a parama)
  {
    paramGraphics.setColor(new Color(1066854));
    double d1 = parama.getWidth() / 4.0D;
    double d2 = parama.getHeight() / 2.0D;
    for (int i1 = 0; i1 < 4; i1++)
    {
      Point2D localPoint2D = new Point2D(parama.getX(), parama.getY()).add(-d1 + d1 * i1, 0.0D);
      a(paramGraphics, localPoint2D, localPoint2D.copy().add(-d1, -d2));
      a(paramGraphics, localPoint2D, localPoint2D.copy().add(-d1, d2));
    }
  }
  
  private void e(Graphics paramGraphics, com.a.b.a.a.c.a parama)
  {
    paramGraphics.setColor(new Color(12028738));
    b(paramGraphics, parama.getX() - parama.getWidth() / 4.0D, parama.getY() - parama.getHeight() / 4.0D, parama.getWidth() / 2.0D, parama.getHeight() / 2.0D);
    a(paramGraphics, parama.getX() - parama.getWidth() / 2.0D, parama.getY() - parama.getHeight() / 2.0D, parama.getX() - parama.getWidth() / 4.0D, parama.getY() - parama.getHeight() / 4.0D);
    a(paramGraphics, parama.getX() + parama.getWidth() / 2.0D, parama.getY() + parama.getHeight() / 2.0D, parama.getX() + parama.getWidth() / 4.0D, parama.getY() + parama.getHeight() / 4.0D);
    a(paramGraphics, parama.getX() + parama.getWidth() / 2.0D, parama.getY() - parama.getHeight() / 2.0D, parama.getX() + parama.getWidth() / 4.0D, parama.getY() - parama.getHeight() / 4.0D);
    a(paramGraphics, parama.getX() - parama.getWidth() / 2.0D, parama.getY() + parama.getHeight() / 2.0D, parama.getX() - parama.getWidth() / 4.0D, parama.getY() + parama.getHeight() / 4.0D);
  }
  
  private void f(Graphics paramGraphics, com.a.b.a.a.c.a parama)
  {
    paramGraphics.setColor(new Color(9526102));
    String str = "₽";
    double d1 = parama.getHeight();
    Font localFont = new Font("Times New Roman", 1, b(0.0D, d1).b());
    paramGraphics.setFont(localFont);
    c localc = c(parama.getX(), parama.getY() + d1 / 3.0D);
    paramGraphics.drawString(str, localc.a() - paramGraphics.getFontMetrics().stringWidth(str) / 2, localc.b());
  }
  
  private void f(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    Color localColor = paramGraphics.getColor();
    for (q localq : paramh.getProjectilesUnsafe()) {
      a(paramGraphics, localq);
    }
    paramGraphics.setColor(localColor);
  }
  
  private void a(Graphics paramGraphics, q paramq)
  {
    paramGraphics.setColor(Color.BLACK);
    a(paramGraphics, paramq.getX(), paramq.getY(), paramq.getRadius());
    paramGraphics.setColor(Color.WHITE);
    a(paramGraphics, paramq.getX(), paramq.getY(), paramq.getRadius() / 2.0D);
  }
  
  private void g(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    Color localColor = paramGraphics.getColor();
    com.a.b.a.a.c.j[] arrayOfj1 = paramh.getEffects();
    Arrays.sort(arrayOfj1, new i(this));
    for (com.a.b.a.a.c.j localj : arrayOfj1) {
      switch (c.c[localj.getType().ordinal()])
      {
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
        break;
      case 6: 
        a(paramh, paramGraphics, localj);
        break;
      default: 
        throw new IllegalArgumentException("Unsupported effect type: " + localj.getType() + '.');
      }
    }
    paramGraphics.setColor(localColor);
  }
  
  private void a(com.a.b.a.a.c.h paramh, Graphics paramGraphics, com.a.b.a.a.c.j paramj)
  {
    Long localLong = paramj.getAffectedUnitId();
    if (localLong == null) {
      return;
    }
    com.a.b.a.a.c.c localc = a(paramh, localLong.longValue());
    if (localc == null) {
      return;
    }
    double d1 = 0.5D * Math.hypot(localc.getWidth(), localc.getHeight());
    double d2 = 0.5D * Math.min(localc.getWidth(), localc.getHeight());
    Font localFont = new Font("Times New Roman", 1, b(0.0D, (0.7D - 0.35D * paramj.getTick() / paramj.getType().getDuration()) * d2).b());
    paramGraphics.setFont(localFont);
    Integer localInteger = NumberUtil.toInt(paramj.getAttribute("durabilityPercentsChange"));
    if ((localInteger != null) && (localInteger.intValue() != 0))
    {
      c localc1 = c(localc.getX(), localc.getY() - d1 - 10.0D - (this.x.get() ? 0.7D * d2 : 0.0D) - 1.5D * paramj.getTick());
      String str;
      if (localInteger.intValue() > 0)
      {
        str = "+" + localInteger + '%';
        localColor = Color.GREEN;
      }
      else
      {
        str = localInteger + "%";
        localColor = Color.RED;
      }
      Color localColor = com.a.a.a.a.a.a(localColor, NumberUtil.toInt(Math.round(255.0D * (1.0D - 0.75D * paramj.getTick() / paramj.getType().getDuration()))));
      paramGraphics.setColor(localColor);
      paramGraphics.drawString(str, localc1.a() - paramGraphics.getFontMetrics().stringWidth(str) / 2, localc1.b());
    }
  }
  
  private static com.a.b.a.a.c.c a(v paramv, long paramLong)
  {
    for (com.a.b.a.a.c.c localc : paramv.getCarsUnsafe()) {
      if (localc.getId() == paramLong) {
        return localc;
      }
    }
    return null;
  }
  
  private void h(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    if (this.w.get() < 1) {
      return;
    }
    paramGraphics.setFont(new Font("Courier New", 1, a(0.0D, 15.0D).b()));
    o[] arrayOfo = paramh.getPlayers();
    Arrays.sort(arrayOfo, new j(this));
    if (arrayOfo.length > 0) {
      a(paramGraphics, 20.0D, 30.0D, arrayOfo[0]);
    }
    if (arrayOfo.length > 1) {
      a(paramGraphics, 20.0D, 60.0D, arrayOfo[1]);
    }
    if (arrayOfo.length > 2) {
      a(paramGraphics, 235.0D, 30.0D, arrayOfo[2]);
    }
    if (arrayOfo.length > 3) {
      a(paramGraphics, 235.0D, 60.0D, arrayOfo[3]);
    }
    if (arrayOfo.length > 4) {
      a(paramGraphics, 450.0D, 30.0D, arrayOfo[4]);
    }
    if (arrayOfo.length > 5) {
      a(paramGraphics, 450.0D, 60.0D, arrayOfo[5]);
    }
    c localc1 = a(1070.0D, 770.0D);
    String str1 = paramh.getTick() + " / " + (paramh.getLastTickIndex() + 1);
    if (str1.length() < 20) {
      str1 = Strings.repeat(" ", 20 - str1.length()) + str1;
    }
    paramGraphics.drawString(str1, localc1.a(), localc1.b());
    c localc2 = a(1070.0D, 30.0D);
    String str2 = "Speed: " + f();
    if (str2.length() < 20) {
      str2 = Strings.repeat(" ", 20 - str2.length()) + str2;
    }
    paramGraphics.drawString(str2, localc2.a(), localc2.b());
    c localc3 = a(1070.0D, 55.0D);
    String str3 = "FPS: " + this.z.get();
    if (str3.length() < 20) {
      str3 = Strings.repeat(" ", 20 - str3.length()) + str3;
    }
    paramGraphics.drawString(str3, localc3.a(), localc3.b());
  }
  
  private void i(com.a.b.a.a.c.h paramh, Graphics paramGraphics)
  {
    int i1 = 60;
    if (paramh.getTick() >= 4 * i1) {
      return;
    }
    int i2 = i1 / 5;
    int i3 = paramh.getTick() % i1;
    double d1 = 512.0D;
    String str;
    if (paramh.getTick() < i1)
    {
      str = "3";
    }
    else if (paramh.getTick() < 2 * i1)
    {
      str = "2";
    }
    else if (paramh.getTick() < 3 * i1)
    {
      str = "1";
    }
    else
    {
      str = "START!";
      d1 = 256.0D;
    }
    double d2;
    if (i3 < i2) {
      d2 = i3 / i2;
    } else if (i3 >= i1 - i2) {
      d2 = 1.0D - (i3 - i1 + i2) / i2;
    } else {
      d2 = 1.0D;
    }
    paramGraphics.setColor(com.a.a.a.a.a.a(d, NumberUtil.toInt(Math.round(d2 * 255.0D))));
    paramGraphics.setFont(new Font("Courier New", 1, a(0.0D, d1).b()));
    FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
    c localc = a((1280.0D - localFontMetrics.stringWidth(str)) / 2.0D, 400.0D + d1 / 4.0D);
    paramGraphics.drawString(str, localc.a(), localc.b());
  }
  
  private String f()
  {
    long l1 = this.t.get();
    int i1 = Arrays.binarySearch(b, l1);
    switch (i1)
    {
    case 0: 
      return "fast forward";
    case 1: 
      return "very fast";
    case 2: 
      return "fast";
    case 3: 
      return "normal";
    case 4: 
      return "slow";
    case 5: 
      return "very slow";
    case 6: 
      return "slideshow";
    }
    throw new IllegalStateException(String.format("Illegal current screen interval index %d for interval %d ms.", new Object[] { Integer.valueOf(i1), Long.valueOf(l1) }));
  }
  
  private void a(Graphics paramGraphics, double paramDouble1, double paramDouble2, o paramo)
  {
    Color localColor = paramGraphics.getColor();
    paramGraphics.setColor(a(paramo.getId()));
    c localc = a(paramDouble1, paramDouble2);
    paramGraphics.drawString(String.format("%-17s: %d", new Object[] { (paramo.isStrategyCrashed() ? "? " : "") + paramo.getName(), Integer.valueOf(paramo.getScore()) }), localc.a(), localc.b());
    paramGraphics.setColor(localColor);
  }
  
  private c a(double paramDouble1, double paramDouble2)
  {
    return new c(paramDouble1 * this.e.b() / 1280.0D, paramDouble2 * this.e.c() / 800.0D, null);
  }
  
  private c b(double paramDouble1, double paramDouble2)
  {
    return a(paramDouble1, paramDouble2, 0.0D, 0.0D, this.i, this.j, this.q.getWidth(), this.q.getHeight());
  }
  
  private static c a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt1, int paramInt2)
  {
    return a(paramDouble1, paramDouble2, 0.0D, 0.0D, paramDouble3, paramDouble4, paramInt1, paramInt2);
  }
  
  private c c(double paramDouble1, double paramDouble2)
  {
    return a(paramDouble1, paramDouble2, this.k, this.l, this.i, this.j, this.q.getWidth(), this.q.getHeight());
  }
  
  private static c a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt1, int paramInt2)
  {
    return new c((paramDouble1 - paramDouble3) * paramInt1 / paramDouble5, (paramDouble2 - paramDouble4) * paramInt2 / paramDouble6, null);
  }
  
  private Point2D d(double paramDouble1, double paramDouble2)
  {
    return a(paramDouble1, paramDouble2, this.k, this.l, this.i, this.j, this.q.getWidth(), this.q.getHeight());
  }
  
  private static Point2D a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8)
  {
    return new Point2D((paramDouble1 - paramDouble3) * paramDouble7 / paramDouble5, (paramDouble2 - paramDouble4) * paramDouble8 / paramDouble6);
  }
  
  private void a(Graphics paramGraphics, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if (!a(a(paramDouble1, paramDouble2, paramDouble3, paramDouble4))) {
      return;
    }
    c localc1 = c(paramDouble1, paramDouble2);
    c localc2 = c(paramDouble3, paramDouble4);
    paramGraphics.drawLine(localc1.a(), localc1.b(), localc2.a(), localc2.b());
  }
  
  private void a(Graphics paramGraphics, Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    a(paramGraphics, paramPoint2D1.getX(), paramPoint2D1.getY(), paramPoint2D2.getX(), paramPoint2D2.getY());
  }
  
  private void a(Graphics paramGraphics, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    if (!a(a(paramDouble1, paramDouble2, paramDouble3))) {
      return;
    }
    c localc1 = c(paramDouble1 - paramDouble3, paramDouble2 - paramDouble3);
    c localc2 = b(2.0D * paramDouble3, 2.0D * paramDouble3);
    paramGraphics.fillOval(localc1.a(), localc1.b(), localc2.a(), localc2.b());
  }
  
  private void a(Graphics paramGraphics, Point2D paramPoint2D, double paramDouble)
  {
    a(paramGraphics, paramPoint2D.getX(), paramPoint2D.getY(), paramDouble);
  }
  
  private void b(Graphics paramGraphics, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    if (!a(a(paramDouble1, paramDouble2, paramDouble3))) {
      return;
    }
    c localc1 = c(paramDouble1 - paramDouble3, paramDouble2 - paramDouble3);
    c localc2 = b(2.0D * paramDouble3, 2.0D * paramDouble3);
    paramGraphics.drawOval(localc1.a(), localc1.b(), localc2.a(), localc2.b());
  }
  
  private void b(Graphics paramGraphics, Point2D paramPoint2D, double paramDouble)
  {
    b(paramGraphics, paramPoint2D.getX(), paramPoint2D.getY(), paramDouble);
  }
  
  private void a(Graphics paramGraphics, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    if (!a(a(paramDouble1, paramDouble2, paramDouble3))) {
      return;
    }
    c localc1 = c(paramDouble1 - paramDouble3, paramDouble2 - paramDouble3);
    c localc2 = b(2.0D * paramDouble3, 2.0D * paramDouble3);
    paramGraphics.drawArc(localc1.a(), localc1.b(), localc2.a(), localc2.b(), NumberUtil.toInt(Math.round(paramDouble4 * 57.29577951308232D)), NumberUtil.toInt(Math.round(paramDouble5 * 57.29577951308232D)));
  }
  
  private void b(Graphics paramGraphics, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if (!a(b(paramDouble1, paramDouble2, paramDouble3, paramDouble4))) {
      return;
    }
    c localc1 = c(paramDouble1, paramDouble2);
    c localc2 = b(paramDouble3, paramDouble4);
    paramGraphics.drawRect(localc1.a(), localc1.b(), localc2.a(), localc2.b());
  }
  
  private void a(Graphics paramGraphics, Point2D... paramVarArgs)
  {
    int i1 = paramVarArgs.length;
    for (int i2 = 1; i2 < i1; i2++)
    {
      localPoint2D2 = paramVarArgs[i2];
      Point2D localPoint2D3 = paramVarArgs[(i2 - 1)];
      a(paramGraphics, localPoint2D2.getX(), localPoint2D2.getY(), localPoint2D3.getX(), localPoint2D3.getY());
    }
    Point2D localPoint2D1 = paramVarArgs[0];
    Point2D localPoint2D2 = paramVarArgs[(i1 - 1)];
    a(paramGraphics, localPoint2D1.getX(), localPoint2D1.getY(), localPoint2D2.getX(), localPoint2D2.getY());
  }
  
  private boolean a(a parama)
  {
    return (parama.c() >= this.k) && (parama.a() <= this.k + this.i) && (parama.d() >= this.l) && (parama.b() <= this.l + this.j);
  }
  
  private static a a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    return new a(Math.min(paramDouble1, paramDouble3), Math.min(paramDouble2, paramDouble4), Math.max(paramDouble1, paramDouble3), Math.max(paramDouble2, paramDouble4), null);
  }
  
  private static a a(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    return new a(paramDouble1 - paramDouble3, paramDouble2 - paramDouble3, paramDouble1 + paramDouble3, paramDouble2 + paramDouble3, null);
  }
  
  private static a b(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    return new a(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4, null);
  }
  
  private static Graphics a(Graphics paramGraphics)
  {
    if ((paramGraphics instanceof Graphics2D))
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      localGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      localGraphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    return paramGraphics;
  }
  
  private static Color a(long paramLong)
  {
    switch (NumberUtil.toInt(paramLong))
    {
    case 1: 
      return com.a.a.a.a.a.b();
    case 2: 
      return com.a.a.a.a.a.a();
    case 3: 
      return com.a.a.a.a.a.c();
    case 4: 
      return com.a.a.a.a.a.d();
    }
    throw new IllegalArgumentException("Can't get color for Player {id=" + paramLong + "}.");
  }
  
  public c.a a()
  {
    return this.A;
  }
  
  private static final class d
    implements a.e
  {
    private final Object a;
    private volatile MethodSignature b;
    private volatile Method c;
    private volatile MethodSignature d;
    private volatile Method e;
    private volatile v f;
    private volatile com.a.b.a.a.c.l g;
    private volatile Object h;
    private volatile Object i;
    
    private d(Object paramObject)
    {
      this.a = paramObject;
      if (this.a == null) {
        return;
      }
      Map localMap = ReflectionUtil.getPublicMethodBySignatureMap(paramObject.getClass());
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        MethodSignature localMethodSignature = (MethodSignature)localEntry.getKey();
        List localList = localMethodSignature.getParameterTypes();
        if (("beforeDrawScene".equals(localMethodSignature.getName())) && (localList.size() == 9) && (localList.get(0) == Graphics.class) && ("World".equals(((Class)localList.get(1)).getSimpleName())) && (((Class)localList.get(1)).getPackage() != null) && ("model".equals(((Class)localList.get(1)).getPackage().getName())) && ("Game".equals(((Class)localList.get(2)).getSimpleName())) && (((Class)localList.get(2)).getPackage() != null) && ("model".equals(((Class)localList.get(2)).getPackage().getName())) && (localList.get(3) == Integer.TYPE) && (localList.get(4) == Integer.TYPE) && (localList.get(5) == Double.TYPE) && (localList.get(6) == Double.TYPE) && (localList.get(7) == Double.TYPE) && (localList.get(8) == Double.TYPE))
        {
          this.b = localMethodSignature;
          this.c = ((Method)localEntry.getValue());
        }
        else if (("afterDrawScene".equals(localMethodSignature.getName())) && (localList.size() == 9) && (localList.get(0) == Graphics.class) && ("World".equals(((Class)localList.get(1)).getSimpleName())) && (((Class)localList.get(1)).getPackage() != null) && ("model".equals(((Class)localList.get(1)).getPackage().getName())) && ("Game".equals(((Class)localList.get(2)).getSimpleName())) && (((Class)localList.get(2)).getPackage() != null) && ("model".equals(((Class)localList.get(2)).getPackage().getName())) && (localList.get(3) == Integer.TYPE) && (localList.get(4) == Integer.TYPE) && (localList.get(5) == Double.TYPE) && (localList.get(6) == Double.TYPE) && (localList.get(7) == Double.TYPE) && (localList.get(8) == Double.TYPE))
        {
          this.d = localMethodSignature;
          this.e = ((Method)localEntry.getValue());
        }
      }
    }
    
    public void a(Graphics paramGraphics, v paramv, com.a.b.a.a.c.l paraml, int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      if (this.c == null) {
        return;
      }
      try
      {
        List localList = this.b.getParameterTypes();
        Object localObject1 = paramv == this.f ? this.h : a(paramv, (Class)localList.get(1));
        Object localObject2 = paraml == this.g ? this.i : a(paraml, (Class)localList.get(2));
        this.f = paramv;
        this.g = paraml;
        this.h = localObject1;
        this.i = localObject2;
        this.c.invoke(this.a, new Object[] { paramGraphics, localObject1, localObject2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Double.valueOf(paramDouble1), Double.valueOf(paramDouble2), Double.valueOf(paramDouble3), Double.valueOf(paramDouble4) });
      }
      catch (IllegalAccessException|InvocationTargetException|InstantiationException localIllegalAccessException)
      {
        a.b().error("Can't invoke beforeDrawScene(...) method of custom renderer.", localIllegalAccessException);
      }
    }
    
    public void b(Graphics paramGraphics, v paramv, com.a.b.a.a.c.l paraml, int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      if (this.e == null) {
        return;
      }
      try
      {
        List localList = this.d.getParameterTypes();
        Object localObject1 = paramv == this.f ? this.h : a(paramv, (Class)localList.get(1));
        Object localObject2 = paraml == this.g ? this.i : a(paraml, (Class)localList.get(2));
        this.f = paramv;
        this.g = paraml;
        this.h = localObject1;
        this.i = localObject2;
        this.e.invoke(this.a, new Object[] { paramGraphics, localObject1, localObject2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Double.valueOf(paramDouble1), Double.valueOf(paramDouble2), Double.valueOf(paramDouble3), Double.valueOf(paramDouble4) });
      }
      catch (IllegalAccessException|InvocationTargetException|InstantiationException localIllegalAccessException)
      {
        a.b().error("Can't invoke afterDrawScene(...) method of custom renderer.", localIllegalAccessException);
      }
    }
    
    private static Object a(Object paramObject, Class paramClass)
      throws IllegalAccessException, InvocationTargetException, InstantiationException
    {
      return com.a.a.a.a.d.a(paramObject, paramClass);
    }
  }
  
  private static final class c
  {
    private int a;
    private int b;
    
    private c(double paramDouble1, double paramDouble2)
    {
      this.a = a(Math.round(paramDouble1));
      this.b = a(Math.round(paramDouble2));
    }
    
    private c() {}
    
    public int a()
    {
      return this.a;
    }
    
    public int b()
    {
      return this.b;
    }
    
    private static int a(double paramDouble)
    {
      int i = (int)paramDouble;
      if (Math.abs(i - paramDouble) < 1.0D) {
        return i;
      }
      throw new IllegalArgumentException("Can't convert double " + paramDouble + " to int.");
    }
  }
  
  private static abstract interface e
  {
    public abstract void a(Graphics paramGraphics, v paramv, com.a.b.a.a.c.l paraml, int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
    
    public abstract void b(Graphics paramGraphics, v paramv, com.a.b.a.a.c.l paraml, int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  }
  
  private static final class f
  {
    private final com.a.b.a.a.c.h a;
    
    private f(com.a.b.a.a.c.h paramh)
    {
      this.a = paramh;
    }
    
    public com.a.b.a.a.c.h a()
    {
      return this.a;
    }
  }
  
  private static final class a
  {
    private final double a;
    private final double b;
    private final double c;
    private final double d;
    
    private a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      this.a = paramDouble1;
      this.b = paramDouble2;
      this.c = paramDouble3;
      this.d = paramDouble4;
    }
    
    public double a()
    {
      return this.a;
    }
    
    public double b()
    {
      return this.b;
    }
    
    public double c()
    {
      return this.c;
    }
    
    public double d()
    {
      return this.d;
    }
  }
  
  private static final class b
  {
    private final Point2D a;
    private final double b;
    
    private b(Point2D paramPoint2D, double paramDouble)
    {
      this.a = paramPoint2D;
      this.b = paramDouble;
    }
    
    public Point2D a()
    {
      return this.a;
    }
    
    public double b()
    {
      return this.b;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\d\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */