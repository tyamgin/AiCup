package com.a.b.a.a.b.e;

import com.a.b.a.a.c.t;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.IntPair;
import com.codeforces.commons.resource.CantReadResourceException;
import com.codeforces.commons.resource.ResourceUtil;
import com.codeforces.commons.text.Patterns;
import com.codeforces.commons.text.StringUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public final class i
{
  private static final Pattern a = Pattern.compile("[3-9]|[1-9][0-9]");
  private static final Pattern b = Pattern.compile("[8-9]|1[01-6]");
  private static final Pattern c = Pattern.compile("[1-9]?[0-9]");
  private static final Set d = Collections.unmodifiableSet(EnumSet.of(t.HORIZONTAL, new t[] { t.LEFT_TOP_CORNER, t.LEFT_BOTTOM_CORNER, t.RIGHT_HEADED_T, t.TOP_HEADED_T, t.BOTTOM_HEADED_T, t.CROSSROADS }));
  private static final Set e = Collections.unmodifiableSet(EnumSet.of(t.HORIZONTAL, new t[] { t.RIGHT_TOP_CORNER, t.RIGHT_BOTTOM_CORNER, t.LEFT_HEADED_T, t.TOP_HEADED_T, t.BOTTOM_HEADED_T, t.CROSSROADS }));
  private static final Set f = Collections.unmodifiableSet(EnumSet.of(t.VERTICAL, new t[] { t.LEFT_TOP_CORNER, t.RIGHT_TOP_CORNER, t.LEFT_HEADED_T, t.RIGHT_HEADED_T, t.BOTTOM_HEADED_T, t.CROSSROADS }));
  private static final Set g = Collections.unmodifiableSet(EnumSet.of(t.VERTICAL, new t[] { t.LEFT_BOTTOM_CORNER, t.RIGHT_BOTTOM_CORNER, t.LEFT_HEADED_T, t.RIGHT_HEADED_T, t.TOP_HEADED_T, t.CROSSROADS }));
  private static final int h;
  private static final ConcurrentMap i = new ConcurrentHashMap();
  
  public static a a(String paramString, boolean paramBoolean)
  {
    paramString = FilenameUtils.getBaseName(paramString);
    a locala = (a)i.get(paramString);
    if (locala == null)
    {
      byte[] arrayOfByte = a(paramString + ".map");
      String[] arrayOfString = Patterns.LINE_BREAK_PATTERN.split(new String(arrayOfByte, StandardCharsets.UTF_8));
      MutableInt localMutableInt1 = new MutableInt(0);
      MutableInt localMutableInt2 = new MutableInt(0);
      t[][] arrayOft = a(arrayOfString, localMutableInt1, paramBoolean);
      if (arrayOft.length > 0)
      {
        a(arrayOft, arrayOfString, localMutableInt1, arrayOft.length, arrayOft[0].length);
        a(arrayOft, arrayOft.length, arrayOft[0].length);
      }
      IntPair[] arrayOfIntPair = a(arrayOfString, localMutableInt1);
      if (arrayOfIntPair.length > 0)
      {
        a(arrayOfIntPair, arrayOfString, localMutableInt1, arrayOft, arrayOft.length, arrayOft[0].length);
        a(arrayOfIntPair, arrayOft, arrayOft.length, arrayOft[0].length, localMutableInt2);
      }
      com.a.b.a.a.c.i locali = a(arrayOfString, localMutableInt1, arrayOft, arrayOfIntPair);
      locala = new a(paramString, arrayOft, arrayOfIntPair, locali, localMutableInt2.intValue());
      i.putIfAbsent(paramString, locala);
    }
    return locala;
  }
  
  private static byte[] a(String paramString)
  {
    byte[] arrayOfByte = ResourceUtil.getResourceOrNull(q.class, "/maps/" + paramString);
    if (arrayOfByte == null) {
      try
      {
        File localFile = new File(paramString);
        if (!localFile.isFile()) {
          throw new CantReadResourceException("Map file '" + paramString + "' is not found in current directory.");
        }
        if (localFile.length() > 8388608L) {
          throw new CantReadResourceException(String.format("Size of the map file '%s' is greater than %d B.", new Object[] { paramString, Long.valueOf(8388608L) }));
        }
        arrayOfByte = FileUtils.readFileToByteArray(localFile);
      }
      catch (IOException localIOException)
      {
        throw new CantReadResourceException("Can't read map file '" + paramString + "' from current directory.", localIOException);
      }
    }
    return arrayOfByte;
  }
  
  private static t[][] a(String[] paramArrayOfString, MutableInt paramMutableInt, boolean paramBoolean)
  {
    int j = paramArrayOfString.length;
    t[][] arrayOft = (t[][])null;
    while ((paramMutableInt.intValue() < j) && (arrayOft == null))
    {
      String str1 = paramArrayOfString[paramMutableInt.intValue()];
      if ((StringUtil.isNotBlank(str1)) && (str1.indexOf('#') != 0))
      {
        String[] arrayOfString = Patterns.WHITESPACE_PATTERN.split(str1.trim());
        if (arrayOfString.length < 2) {
          throw new CantReadResourceException("Can't parse width and height of the map.");
        }
        Pattern localPattern = paramBoolean ? a : b;
        String str2 = arrayOfString[0];
        if (!localPattern.matcher(str2).matches()) {
          throw new IllegalArgumentException(String.format("Map width '%s' does not match pattern '%s'.", new Object[] { str2, localPattern.pattern() }));
        }
        int k = Integer.parseInt(str2);
        String str3 = arrayOfString[1];
        if (!localPattern.matcher(str3).matches()) {
          throw new IllegalArgumentException(String.format("Map height '%s' does not match pattern '%s'.", new Object[] { str3, localPattern.pattern() }));
        }
        int m = Integer.parseInt(str3);
        arrayOft = new t[k][m];
      }
      paramMutableInt.increment();
    }
    if (arrayOft == null) {
      throw new CantReadResourceException("Can't read width and height of the map.");
    }
    return arrayOft;
  }
  
  private static void a(t[][] paramArrayOft, String[] paramArrayOfString, MutableInt paramMutableInt, int paramInt1, int paramInt2)
  {
    int j = paramArrayOfString.length;
    int k = 0;
    while ((paramMutableInt.intValue() < j) && (k < paramInt2))
    {
      String str = paramArrayOfString[paramMutableInt.intValue()];
      if ((StringUtil.isNotBlank(str)) && (str.indexOf('#') != 0))
      {
        str = Patterns.WHITESPACE_PATTERN.matcher(str).replaceAll("");
        if (str.length() != paramInt1) {
          throw new IllegalArgumentException("Length of the map line is not " + paramInt1 + '.');
        }
        for (int m = 0; m < paramInt1; m++)
        {
          char c1 = str.charAt(m);
          paramArrayOft[m][k] = a(c1);
        }
        k++;
      }
      paramMutableInt.increment();
    }
    if (k < paramInt2) {
      throw new CantReadResourceException("Number of the map lines is not " + paramInt2 + '.');
    }
  }
  
  private static void a(t[][] paramArrayOft, int paramInt1, int paramInt2)
  {
    for (int j = 0; j < paramInt1; j++) {
      for (int k = 0; k < paramInt2; k++)
      {
        t localt1 = paramArrayOft[j][k];
        t localt2 = j > 0 ? paramArrayOft[(j - 1)][k] : null;
        t localt3 = j < paramInt1 - 1 ? paramArrayOft[(j + 1)][k] : null;
        t localt4 = k > 0 ? paramArrayOft[j][(k - 1)] : null;
        t localt5 = k < paramInt2 - 1 ? paramArrayOft[j][(k + 1)] : null;
        a(j, k, localt1, localt2, localt3, localt4, localt5);
      }
    }
  }
  
  private static void a(int paramInt1, int paramInt2, t paramt1, t paramt2, t paramt3, t paramt4, t paramt5)
  {
    if (((g.contains(paramt1)) && ((paramt4 == null) || (!f.contains(paramt4)))) || ((f.contains(paramt1)) && ((paramt5 == null) || (!g.contains(paramt5)))) || ((d.contains(paramt1)) && ((paramt3 == null) || (!e.contains(paramt3)))) || ((e.contains(paramt1)) && ((paramt2 == null) || (!d.contains(paramt2))))) {
      throw new IllegalArgumentException(String.format("Illegal environment of tile (%d, %d) (tileType=%s, leftTileType=%s, rightTileType=%s, topTileType=%s, bottomTileType=%s).", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramt1, paramt2, paramt3, paramt4, paramt5 }));
    }
  }
  
  private static IntPair[] a(String[] paramArrayOfString, MutableInt paramMutableInt)
  {
    int j = paramArrayOfString.length;
    IntPair[] arrayOfIntPair = null;
    while ((paramMutableInt.intValue() < j) && (arrayOfIntPair == null))
    {
      String str = paramArrayOfString[paramMutableInt.intValue()];
      if ((StringUtil.isNotBlank(str)) && (str.indexOf('#') != 0))
      {
        int k;
        try
        {
          k = Integer.parseInt(str.trim());
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new CantReadResourceException("Can't parse number of waypoints.", localNumberFormatException);
        }
        if ((k < 4) || (k > 1000)) {
          throw new IllegalArgumentException(String.format("Number of waypoints %d is not between %d and %d.", new Object[] { Integer.valueOf(k), Integer.valueOf(4), Integer.valueOf(1000) }));
        }
        arrayOfIntPair = new IntPair[k];
      }
      paramMutableInt.increment();
    }
    if (arrayOfIntPair == null) {
      throw new CantReadResourceException("Can't read number of waypoints.");
    }
    return arrayOfIntPair;
  }
  
  private static void a(IntPair[] paramArrayOfIntPair, String[] paramArrayOfString, MutableInt paramMutableInt, t[][] paramArrayOft, int paramInt1, int paramInt2)
  {
    int j = paramArrayOfIntPair.length;
    int k = paramArrayOfString.length;
    int m = 0;
    while ((paramMutableInt.intValue() < k) && (m < j))
    {
      String str1 = paramArrayOfString[paramMutableInt.intValue()];
      if ((StringUtil.isNotBlank(str1)) && (str1.indexOf('#') != 0))
      {
        String[] arrayOfString = Patterns.WHITESPACE_PATTERN.split(str1.trim());
        if (arrayOfString.length < 2) {
          throw new CantReadResourceException("Can't parse x and y of the waypoint.");
        }
        String str2 = arrayOfString[0];
        if (!c.matcher(str2).matches()) {
          throw new IllegalArgumentException(String.format("Waypoint x-coordinate '%s' does not match pattern '%s'.", new Object[] { str2, c.pattern() }));
        }
        int n = Integer.parseInt(str2);
        String str3 = arrayOfString[1];
        if (!c.matcher(str3).matches()) {
          throw new IllegalArgumentException(String.format("Waypoint y-coordinate '%s' does not match pattern '%s'.", new Object[] { str3, c.pattern() }));
        }
        int i1 = Integer.parseInt(str3);
        if ((n >= paramInt1) || (i1 >= paramInt2) || (paramArrayOft[n][i1] == t.EMPTY)) {
          throw new IllegalArgumentException(String.format("Waypoint (%d, %d) is outside the track.", new Object[] { Integer.valueOf(n), Integer.valueOf(i1) }));
        }
        if ((m == 0) && (paramArrayOft[n][i1] != t.VERTICAL) && (paramArrayOft[n][i1] != t.HORIZONTAL)) {
          throw new IllegalArgumentException(String.format("Starting waypoint (%d, %d) should lay either on VERTICAL or HORIZONTAL track tile (current is %s).", new Object[] { Integer.valueOf(n), Integer.valueOf(i1), paramArrayOft[n][i1] }));
        }
        if (m > 0)
        {
          IntPair localIntPair = paramArrayOfIntPair[(m - 1)];
          if ((NumberUtil.equals(Integer.valueOf(n), (Integer)localIntPair.getFirst())) && (NumberUtil.equals(Integer.valueOf(i1), (Integer)localIntPair.getSecond()))) {
            throw new IllegalArgumentException(String.format("Waypoint #%d (%d, %d) is the same as previous waypoint.", new Object[] { Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(i1) }));
          }
        }
        paramArrayOfIntPair[m] = new IntPair(Integer.valueOf(n), Integer.valueOf(i1));
        m++;
      }
      paramMutableInt.increment();
    }
    if (m < j) {
      throw new CantReadResourceException("Number of the waypoints is not " + j + '.');
    }
    if (paramArrayOfIntPair[0].equals(paramArrayOfIntPair[(j - 1)])) {
      throw new IllegalArgumentException("Last waypoint is the same as starting waypoint.");
    }
  }
  
  private static void a(IntPair[] paramArrayOfIntPair, t[][] paramArrayOft, int paramInt1, int paramInt2, MutableInt paramMutableInt)
  {
    int j = paramArrayOfIntPair.length;
    IntPair[] arrayOfIntPair = new IntPair[j + 1];
    System.arraycopy(paramArrayOfIntPair, 0, arrayOfIntPair, 0, j);
    arrayOfIntPair[j] = paramArrayOfIntPair[0];
    b[] arrayOfb = new b[j];
    for (int k = 0; k < j; k++)
    {
      IntPair localIntPair = arrayOfIntPair[k];
      b localb = new b(localIntPair);
      if (k > 0) {
        localb.b = arrayOfb[(k - 1)].b;
      }
      Integer[][] arrayOfInteger = new Integer[paramInt1][paramInt2];
      arrayOfInteger[((Integer)localIntPair.getFirst()).intValue()][((Integer)localIntPair.getSecond()).intValue()] = Integer.valueOf(0);
      arrayOfb[k] = a(localb, arrayOfIntPair[(k + 1)], paramArrayOft, arrayOfInteger, paramInt1, paramInt2);
      paramMutableInt.add(arrayOfb[k].c);
    }
  }
  
  private static b a(b paramb, IntPair paramIntPair, t[][] paramArrayOft, Integer[][] paramArrayOfInteger, int paramInt1, int paramInt2)
  {
    List localList = a(paramb.a, paramArrayOft, paramInt1, paramInt2);
    Object localObject = null;
    Iterator localIterator = localList.iterator();
    IntPair localIntPair;
    while (localIterator.hasNext())
    {
      localIntPair = (IntPair)localIterator.next();
      if (localIntPair.equals(paramIntPair))
      {
        localObject = new b(localIntPair);
        ((b)localObject).b = paramb;
        ((b)localObject).c = a(paramb, localIntPair);
        return (b)localObject;
      }
    }
    localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      localIntPair = (IntPair)localIterator.next();
      if ((paramb.b == null) || (!localIntPair.equals(paramb.b.a)))
      {
        int j = a(paramb, localIntPair);
        Integer localInteger = paramArrayOfInteger[((Integer)localIntPair.getFirst()).intValue()][((Integer)localIntPair.getSecond()).intValue()];
        if (localInteger == null)
        {
          paramArrayOfInteger[((Integer)localIntPair.getFirst()).intValue()][((Integer)localIntPair.getSecond()).intValue()] = Integer.valueOf(j);
        }
        else
        {
          if (j > localInteger.intValue() + h) {
            continue;
          }
          if (j < localInteger.intValue()) {
            paramArrayOfInteger[((Integer)localIntPair.getFirst()).intValue()][((Integer)localIntPair.getSecond()).intValue()] = Integer.valueOf(j);
          }
        }
        b localb = new b(localIntPair);
        localb.b = paramb;
        localb.c = j;
        localb = a(localb, paramIntPair, paramArrayOft, paramArrayOfInteger, paramInt1, paramInt2);
        if ((localb != null) && ((localObject == null) || (localb.c < ((b)localObject).c))) {
          localObject = localb;
        }
      }
    }
    return (b)localObject;
  }
  
  private static List a(IntPair paramIntPair, t[][] paramArrayOft, int paramInt1, int paramInt2)
  {
    int j = ((Integer)paramIntPair.getFirst()).intValue();
    int k = ((Integer)paramIntPair.getSecond()).intValue();
    ArrayList localArrayList = new ArrayList(4);
    if ((k > 0) && (f.contains(paramArrayOft[j][(k - 1)]))) {
      localArrayList.add(new IntPair(Integer.valueOf(j), Integer.valueOf(k - 1)));
    }
    if ((k < paramInt2 - 1) && (g.contains(paramArrayOft[j][(k + 1)]))) {
      localArrayList.add(new IntPair(Integer.valueOf(j), Integer.valueOf(k + 1)));
    }
    if ((j > 0) && (d.contains(paramArrayOft[(j - 1)][k]))) {
      localArrayList.add(new IntPair(Integer.valueOf(j - 1), Integer.valueOf(k)));
    }
    if ((j < paramInt1 - 1) && (e.contains(paramArrayOft[(j + 1)][k]))) {
      localArrayList.add(new IntPair(Integer.valueOf(j + 1), Integer.valueOf(k)));
    }
    return localArrayList;
  }
  
  private static int a(b paramb, IntPair paramIntPair)
  {
    int j = ((Integer)paramIntPair.getFirst()).intValue() - ((Integer)paramb.a.getFirst()).intValue();
    int k = ((Integer)paramIntPair.getSecond()).intValue() - ((Integer)paramb.a.getSecond()).intValue();
    int m = ((Integer)paramb.a.getFirst()).intValue() - j;
    int n = ((Integer)paramb.a.getSecond()).intValue() - k;
    int i1 = 60;
    for (b localb = paramb.b; (localb != null) && (localb.a.equals(Integer.valueOf(m), Integer.valueOf(n))); localb = localb.b)
    {
      i1 -= 5;
      if (i1 <= 30) {
        break;
      }
      m -= j;
      n -= k;
    }
    return paramb.c + i1;
  }
  
  private static com.a.b.a.a.c.i a(String[] paramArrayOfString, MutableInt paramMutableInt, t[][] paramArrayOft, IntPair[] paramArrayOfIntPair)
  {
    int j = paramArrayOfString.length;
    com.a.b.a.a.c.i locali = null;
    while ((paramMutableInt.intValue() < j) && (locali == null))
    {
      localObject = paramArrayOfString[paramMutableInt.intValue()];
      if ((StringUtil.isNotBlank((String)localObject)) && (((String)localObject).indexOf('#') != 0)) {
        try
        {
          locali = com.a.b.a.a.c.i.valueOf(((String)localObject).trim().toUpperCase());
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          throw new CantReadResourceException("Can't parse starting direction.", localIllegalArgumentException);
        }
      }
      paramMutableInt.increment();
    }
    if (locali == null) {
      throw new CantReadResourceException("Can't read starting direction.");
    }
    Object localObject = paramArrayOfIntPair[0];
    t localt = paramArrayOft[((Integer)localObject.getFirst()).intValue()][((Integer)localObject.getSecond()).intValue()];
    switch (j.a[localt.ordinal()])
    {
    case 1: 
      if ((locali != com.a.b.a.a.c.i.UP) && (locali != com.a.b.a.a.c.i.DOWN)) {
        throw new IllegalArgumentException(String.format("Starting direction should be either UP or DOWN for VERTICAL waypoint, but got %s.", new Object[] { locali }));
      }
      break;
    case 2: 
      if ((locali != com.a.b.a.a.c.i.LEFT) && (locali != com.a.b.a.a.c.i.RIGHT)) {
        throw new IllegalArgumentException(String.format("Starting direction should be either LEFT or RIGHT for HORIZONTAL waypoint, but got %s.", new Object[] { locali }));
      }
      break;
    default: 
      throw new IllegalArgumentException(String.format("Starting waypoint (%d, %d) should lay either on VERTICAL or HORIZONTAL track tile (current is %s).", new Object[] { ((IntPair)localObject).getFirst(), ((IntPair)localObject).getSecond(), localt }));
    }
    return locali;
  }
  
  private static t a(char paramChar)
  {
    switch (paramChar)
    {
    case '█': 
      return t.EMPTY;
    case '║': 
      return t.VERTICAL;
    case '═': 
      return t.HORIZONTAL;
    case '╔': 
      return t.LEFT_TOP_CORNER;
    case '╗': 
      return t.RIGHT_TOP_CORNER;
    case '╝': 
      return t.RIGHT_BOTTOM_CORNER;
    case '╚': 
      return t.LEFT_BOTTOM_CORNER;
    case '╠': 
      return t.RIGHT_HEADED_T;
    case '╣': 
      return t.LEFT_HEADED_T;
    case '╦': 
      return t.BOTTOM_HEADED_T;
    case '╩': 
      return t.TOP_HEADED_T;
    case '╬': 
      return t.CROSSROADS;
    }
    throw new IllegalArgumentException("Unexpected tile character '" + paramChar + "'.");
  }
  
  static
  {
    int j = 5;
    int k = 25;
    int m = (k - j) / 5 + 1;
    h = (j + k) * m / 2;
  }
  
  private static final class b
  {
    final IntPair a;
    b b;
    int c;
    
    b(IntPair paramIntPair)
    {
      this.a = paramIntPair;
    }
    
    public String toString()
    {
      return StringUtil.toString(this, true, new String[] { "tile", "previousTileInfo.tile", "weight" });
    }
  }
  
  public static final class a
  {
    private final String a;
    private final t[][] b;
    private final IntPair[] c;
    private final com.a.b.a.a.c.i d;
    private final int e;
    
    public a(String paramString, t[][] paramArrayOft, IntPair[] paramArrayOfIntPair, com.a.b.a.a.c.i parami, int paramInt)
    {
      this.a = paramString;
      this.b = paramArrayOft;
      this.c = paramArrayOfIntPair;
      this.d = parami;
      this.e = paramInt;
    }
    
    public String a()
    {
      return this.a;
    }
    
    public t[][] b()
    {
      return this.b;
    }
    
    public IntPair[] c()
    {
      return this.c;
    }
    
    public com.a.b.a.a.c.i d()
    {
      return this.d;
    }
    
    public int e()
    {
      return this.e;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\e\i.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */