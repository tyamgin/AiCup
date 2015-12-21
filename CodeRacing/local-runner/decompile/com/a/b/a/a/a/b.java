package com.a.b.a.a.a;

import com.a.a.a.a.c;
import com.a.b.a.a.b.e.i;
import com.a.b.a.a.b.e.i.a;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.pair.SimplePair;
import com.codeforces.commons.text.StringUtil;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class b
{
  private static final Logger b = LoggerFactory.getLogger(b.class);
  public static final b a = new b();
  private static final Pattern c = Pattern.compile("[1-5][\\d]{4}");
  private static final Pattern d = Pattern.compile("[1-9][0-9]{0,7}");
  private static final Pattern e = Pattern.compile("[1-2]");
  private static final Pattern f = Pattern.compile("[1-9][0-9]{0,3}x[1-9][0-9]{0,3}");
  private final Map g;
  private final List h;
  private int i;
  private int j;
  private Integer k;
  
  private b()
  {
    this.g = Collections.emptyMap();
    this.h = Collections.emptyList();
  }
  
  public b(String[] paramArrayOfString)
  {
    this(a(paramArrayOfString), b(paramArrayOfString));
  }
  
  public b(Map paramMap, List paramList)
  {
    this.g = new HashMap(paramMap);
    this.h = new ArrayList(paramList);
    E();
    this.i = a(this.g, true);
    this.j = a(this.g, false);
    this.k = a(this.g);
  }
  
  public List a()
  {
    return Collections.unmodifiableList(this.h);
  }
  
  public int b()
  {
    return this.i;
  }
  
  public int c()
  {
    return this.j;
  }
  
  public String d()
  {
    String str = StringUtil.trimToNull((String)this.g.get("map"));
    return str == null ? "default" : str;
  }
  
  public void a(String paramString)
  {
    paramString = StringUtil.trimToNull(paramString);
    if (paramString == null) {
      this.g.remove("map");
    } else {
      this.g.put("map", FilenameUtils.getBaseName(paramString));
    }
  }
  
  public i.a b(String paramString)
  {
    return i.a(paramString, C());
  }
  
  public i.a e()
  {
    return b(d());
  }
  
  public int f()
  {
    return e().b().length;
  }
  
  public int g()
  {
    return e().b()[0].length;
  }
  
  public int h()
  {
    return this.k == null ? 180 + e().e() * 4 : this.k.intValue();
  }
  
  public String a(int paramInt)
  {
    String str = (String)this.g.get("p" + (paramInt + 1) + "-name");
    return StringUtils.isBlank(str) ? "Player #" + (paramInt + 1) : str;
  }
  
  public int b(int paramInt)
  {
    b.debug("Parsing team size for player #" + (paramInt + 1) + '.');
    String str = (String)this.g.get("p" + (paramInt + 1) + "-team-size");
    if (StringUtils.isBlank(str)) {
      return 2;
    }
    if (!e.matcher(str).matches()) {
      throw new IllegalArgumentException("Illegal team size value: '" + str + "'.");
    }
    return Integer.parseInt(str);
  }
  
  public File i()
  {
    String str = (String)this.g.get("replay-file");
    return StringUtil.isBlank(str) ? null : new File(str);
  }
  
  public File j()
  {
    String str = (String)this.g.get("results-file");
    return StringUtil.isBlank(str) ? null : new File(str);
  }
  
  public File k()
  {
    String str = (String)this.g.get("strategy-description-file");
    return StringUtil.isBlank(str) ? null : new File(str);
  }
  
  public File l()
  {
    String str = (String)this.g.get("attributes-file");
    return StringUtil.isBlank(str) ? null : new File(str);
  }
  
  public File m()
  {
    String str = (String)this.g.get("plugins-directory");
    return StringUtil.isBlank(str) ? null : new File(str);
  }
  
  public boolean n()
  {
    return c((String)this.g.get("debug"));
  }
  
  public boolean o()
  {
    return c((String)this.g.get("render-to-screen"));
  }
  
  public boolean p()
  {
    return c((String)this.g.get("render-to-screen-sync"));
  }
  
  public boolean q()
  {
    return c((String)this.g.get("local-test"));
  }
  
  public boolean r()
  {
    return c((String)this.g.get("verification-game"));
  }
  
  public File s()
  {
    String str = (String)this.g.get("write-to-text-file");
    return StringUtil.isBlank(str) ? null : new File(str);
  }
  
  public String t()
  {
    return (String)this.g.get("write-to-remote-storage");
  }
  
  public String u()
  {
    return (String)this.g.get("system-user-login");
  }
  
  public String v()
  {
    return (String)this.g.get("system-user-password");
  }
  
  public int w()
  {
    String str = (String)this.g.get("base-adapter-port");
    Preconditions.checkArgument((!StringUtils.isBlank(str)) && (c.matcher(str).matches()), "Argument 'base-adapter-port' is expected to be an integer between 10000 and 59999 inclusive.");
    return Integer.parseInt(str);
  }
  
  public File c(int paramInt)
  {
    boolean bool = c((String)this.g.get("dump-tcp-data"));
    return bool ? new File("p" + (paramInt + 1) + "-tcp-dump.bin") : null;
  }
  
  public Long x()
  {
    String str = (String)this.g.get("seed");
    return StringUtils.isBlank(str) ? null : Long.valueOf(Long.parseLong(str));
  }
  
  public File y()
  {
    String str = (String)this.g.get("cache-directory");
    return StringUtil.isBlank(str) ? null : new File(str);
  }
  
  public boolean z()
  {
    return c((String)this.g.get("log-drifting"));
  }
  
  public boolean A()
  {
    return c((String)this.g.get("log-max-speed"));
  }
  
  public boolean B()
  {
    return c((String)this.g.get("swap-car-types"));
  }
  
  public boolean C()
  {
    return c((String)this.g.get("loose-map-check"));
  }
  
  public int D()
  {
    try
    {
      return Math.max(Math.min(Integer.parseInt((String)this.g.get("psycho-level")), 255), 0);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return 0;
  }
  
  private static int a(Map paramMap, boolean paramBoolean)
  {
    String str = StringUtil.trimToNull((String)paramMap.get("render-to-screen-size"));
    if (str == null) {
      str = "1280x800";
    }
    int m = str.indexOf('x');
    if ((m <= 0) || (m == str.length() - 1) || (!f.matcher(str).matches())) {
      throw new IllegalArgumentException("Illegal screen size value: '" + str + "'.");
    }
    int n = Integer.parseInt(paramBoolean ? str.substring(0, m) : str.substring(m + 1));
    if ((n < 100) || (n > 100000)) {
      throw new IllegalArgumentException(String.format("Illegal screen size dimension (%s): '%d'.", new Object[] { paramBoolean ? "first" : "second", Integer.valueOf(n) }));
    }
    return n;
  }
  
  private static Integer a(Map paramMap)
  {
    b.debug("Parsing tick count.");
    String str = StringUtil.trimToNull((String)paramMap.get("tick-count"));
    if ((str == null) || ("0".equals(str)) || (str.startsWith("-"))) {
      return null;
    }
    if (!d.matcher(str).matches()) {
      throw new IllegalArgumentException("Illegal tick count value: '" + str + "'.");
    }
    return Integer.valueOf(Integer.parseInt(str));
  }
  
  private static boolean c(String paramString)
  {
    return (Boolean.parseBoolean(paramString)) || (BooleanUtils.toBoolean(paramString)) || ("1".equals(paramString));
  }
  
  private static Map a(String[] paramArrayOfString)
  {
    HashMap localHashMap = new HashMap();
    for (String str : paramArrayOfString) {
      if (str.startsWith("-"))
      {
        SimplePair localSimplePair = d(str.substring("-".length()));
        localHashMap.put(localSimplePair.getFirst(), localSimplePair.getSecond());
      }
    }
    return localHashMap;
  }
  
  private static List b(String[] paramArrayOfString)
  {
    ArrayList localArrayList = new ArrayList();
    for (String str : paramArrayOfString) {
      if (!str.startsWith("-")) {
        localArrayList.add(str);
      }
    }
    return localArrayList;
  }
  
  private static SimplePair d(String paramString)
  {
    int m = paramString.indexOf('=');
    if (m <= 0) {
      throw new IllegalArgumentException("Illegal property string: '" + paramString + "'.");
    }
    return new SimplePair(paramString.substring(0, m), paramString.substring(m + 1));
  }
  
  private void E()
  {
    Iterator localIterator = this.h.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ("#KeyboardPlayer".equals(str))
      {
        this.g.put("debug", "true");
        this.g.put("keyboard-player", "true");
        this.g.put("render-to-screen", "true");
        this.g.put("render-to-screen-sync", "true");
      }
      else if ("#LocalTestPlayer".equals(str))
      {
        this.g.put("debug", "true");
        this.g.put("local-test", "true");
      }
    }
    if (StringUtils.isBlank((CharSequence)this.g.get("map"))) {
      this.g.put("map", "default.map");
    }
  }
  
  public static void a(b paramb)
  {
    Long localLong = paramb.x();
    if (localLong == null)
    {
      paramb.g.put("seed", String.valueOf(c.a()));
      localLong = paramb.x();
    }
    c.a(true, localLong.longValue());
    b.info("Starting game with seed '" + localLong + "'.");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\a\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */