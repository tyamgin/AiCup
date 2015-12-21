package com.a.a.b.a;

import com.a.a.b.a;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.IntPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

public class c
  extends b
{
  private final Set a = new HashSet();
  private final Map b = new HashMap();
  private final int[] c = new int['✐'];
  private final int[] d = new int['✐'];
  private final Point2D[] e = new Point2D['✐'];
  private final Point2D[] f = new Point2D['✐'];
  private final a[][][] g = new a['ߑ']['ߑ'][];
  private final Map h = new HashMap();
  private final Set i = new HashSet();
  private double j;
  private final double k;
  
  public c(double paramDouble1, double paramDouble2)
  {
    this.j = paramDouble1;
    this.k = paramDouble2;
  }
  
  public void a(a parama)
  {
    e(parama);
    if (this.a.contains(parama)) {
      throw new IllegalStateException(parama + " is already added.");
    }
    double d1 = parama.c().d();
    double d2 = 2.0D * d1;
    if ((d2 > this.j) && (d2 <= this.k))
    {
      this.j = d2;
      b();
    }
    this.a.add(parama);
    this.b.put(Long.valueOf(parama.a()), parama);
    f(parama);
    parama.o().a(new d(this, d2, parama), getClass().getSimpleName() + "Listener");
  }
  
  public void b(a parama)
  {
    if (parama == null) {
      return;
    }
    if (this.b.remove(Long.valueOf(parama.a())) == null) {
      return;
    }
    this.a.remove(parama);
    g(parama);
  }
  
  public boolean c(a parama)
  {
    e(parama);
    return this.a.contains(parama);
  }
  
  public List a()
  {
    return new a(this.a, null);
  }
  
  public List d(a parama)
  {
    e(parama);
    if (!this.a.contains(parama)) {
      throw new IllegalStateException("Can't find " + parama + '.');
    }
    ArrayList localArrayList = new ArrayList();
    if (!this.i.isEmpty())
    {
      Iterator localIterator = this.i.iterator();
      while (localIterator.hasNext())
      {
        a locala = (a)localIterator.next();
        a(parama, locala, localArrayList);
      }
    }
    int m;
    int n;
    if ((parama.a() >= 0L) && (parama.a() <= 9999L))
    {
      i1 = (int)parama.a();
      m = this.c[i1];
      n = this.d[i1];
    }
    else
    {
      m = a(parama.s());
      n = b(parama.t());
    }
    for (int i1 = -1; i1 <= 1; i1++) {
      for (int i2 = -1; i2 <= 1; i2++)
      {
        a[] arrayOfa = a(m + i1, n + i2);
        a(parama, arrayOfa, localArrayList);
      }
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  private static void a(a parama, a[] paramArrayOfa, List paramList)
  {
    if (paramArrayOfa == null) {
      return;
    }
    int m = 0;
    int n = paramArrayOfa.length;
    while (m < n)
    {
      a(parama, paramArrayOfa[m], paramList);
      m++;
    }
  }
  
  private static void a(a parama1, a parama2, List paramList)
  {
    if (parama2.b(parama1)) {
      return;
    }
    if ((parama1.e()) && (parama2.e())) {
      return;
    }
    if (Math.sqr(parama2.c().d() + parama1.c().d()) < parama2.a(parama1)) {
      return;
    }
    paramList.add(parama2);
  }
  
  private void b()
  {
    for (int m = 64536; m <= 1000; m++) {
      for (int n = 64536; n <= 1000; n++) {
        this.g[(m - 64536)][(n - 64536)] = null;
      }
    }
    this.h.clear();
    this.i.clear();
    Iterator localIterator = this.a.iterator();
    while (localIterator.hasNext())
    {
      a locala = (a)localIterator.next();
      f(locala);
    }
  }
  
  private void f(a parama)
  {
    double d1 = parama.c().d();
    double d2 = 2.0D * d1;
    if (d2 > this.j)
    {
      if (!this.i.add(parama)) {
        throw new IllegalStateException("Can't add Body {id=" + parama.a() + "} to index.");
      }
    }
    else {
      a(parama, a(parama.s()), b(parama.t()));
    }
  }
  
  private void a(a parama, int paramInt1, int paramInt2)
  {
    Object localObject;
    if ((paramInt1 >= 64536) && (paramInt1 <= 1000) && (paramInt2 >= 64536) && (paramInt2 <= 1000))
    {
      localObject = this.g[(paramInt1 - 64536)][(paramInt2 - 64536)];
      localObject = a((a[])localObject, parama);
      this.g[(paramInt1 - 64536)][(paramInt2 - 64536)] = localObject;
    }
    else
    {
      localObject = new IntPair(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      a[] arrayOfa = (a[])this.h.get(localObject);
      arrayOfa = a(arrayOfa, parama);
      this.h.put(localObject, arrayOfa);
    }
    if ((parama.a() >= 0L) && (parama.a() <= 9999L))
    {
      int m = (int)parama.a();
      this.c[m] = paramInt1;
      this.d[m] = paramInt2;
      this.e[m] = new Point2D(paramInt1 * this.j, paramInt2 * this.j);
      this.f[m] = this.e[m].copy().add(this.j, this.j);
    }
  }
  
  private void g(a parama)
  {
    double d1 = parama.c().d();
    double d2 = 2.0D * d1;
    if (d2 > this.j)
    {
      if (!this.i.remove(parama)) {
        throw new IllegalStateException("Can't remove Body {id=" + parama.a() + "} from index.");
      }
    }
    else {
      b(parama, a(parama.s()), b(parama.t()));
    }
  }
  
  private void b(a parama, int paramInt1, int paramInt2)
  {
    Object localObject;
    if ((paramInt1 >= 64536) && (paramInt1 <= 1000) && (paramInt2 >= 64536) && (paramInt2 <= 1000))
    {
      localObject = this.g[(paramInt1 - 64536)][(paramInt2 - 64536)];
      localObject = b((a[])localObject, parama);
      this.g[(paramInt1 - 64536)][(paramInt2 - 64536)] = localObject;
    }
    else
    {
      localObject = new IntPair(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      a[] arrayOfa = (a[])this.h.get(localObject);
      arrayOfa = b(arrayOfa, parama);
      if (arrayOfa == null) {
        this.h.remove(localObject);
      } else {
        this.h.put(localObject, arrayOfa);
      }
    }
  }
  
  private static a[] a(a[] paramArrayOfa, a parama)
  {
    if (paramArrayOfa == null) {
      return new a[] { parama };
    }
    int m = ArrayUtils.indexOf(paramArrayOfa, parama);
    if (m != -1) {
      throw new IllegalStateException("Can't add Body {id=" + parama.a() + "} to index.");
    }
    int n = paramArrayOfa.length;
    a[] arrayOfa = new a[n + 1];
    System.arraycopy(paramArrayOfa, 0, arrayOfa, 0, n);
    arrayOfa[n] = parama;
    return arrayOfa;
  }
  
  private static a[] b(a[] paramArrayOfa, a parama)
  {
    int m = ArrayUtils.indexOf(paramArrayOfa, parama);
    if (m == -1) {
      throw new IllegalStateException("Can't remove Body {id=" + parama.a() + "} from index.");
    }
    int n = paramArrayOfa.length;
    if (n == 1) {
      return null;
    }
    a[] arrayOfa = new a[n - 1];
    System.arraycopy(paramArrayOfa, 0, arrayOfa, 0, m);
    System.arraycopy(paramArrayOfa, m + 1, arrayOfa, m, n - m - 1);
    return arrayOfa;
  }
  
  private a[] a(int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 64536) && (paramInt1 <= 1000) && (paramInt2 >= 64536) && (paramInt2 <= 1000)) {
      return this.g[(paramInt1 - 64536)][(paramInt2 - 64536)];
    }
    return (a[])this.h.get(new IntPair(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)));
  }
  
  private int a(double paramDouble)
  {
    return NumberUtil.toInt(Math.floor(paramDouble / this.j));
  }
  
  private int b(double paramDouble)
  {
    return NumberUtil.toInt(Math.floor(paramDouble / this.j));
  }
  
  private static final class a
    implements List
  {
    private final Collection a;
    
    private a(Collection paramCollection)
    {
      this.a = paramCollection;
    }
    
    public int size()
    {
      return this.a.size();
    }
    
    public boolean isEmpty()
    {
      return this.a.isEmpty();
    }
    
    public boolean contains(Object paramObject)
    {
      return this.a.contains(paramObject);
    }
    
    public Iterator iterator()
    {
      Iterator localIterator = this.a.iterator();
      return new e(this, localIterator);
    }
    
    public Object[] toArray()
    {
      return this.a.toArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return this.a.toArray(paramArrayOfObject);
    }
    
    public boolean add(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return this.a.containsAll(paramCollection);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int paramInt, Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public Object get(int paramInt)
    {
      if ((this.a instanceof List)) {
        return ((List)this.a).get(paramInt);
      }
      if ((paramInt < 0) || (paramInt >= this.a.size())) {
        throw new IndexOutOfBoundsException("Illegal index: " + paramInt + ", size: " + this.a.size() + '.');
      }
      Iterator localIterator = this.a.iterator();
      for (int i = 0; i < paramInt; i++) {
        localIterator.next();
      }
      return localIterator.next();
    }
    
    public Object set(int paramInt, Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(int paramInt, Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public Object remove(int paramInt)
    {
      throw new UnsupportedOperationException();
    }
    
    public int indexOf(Object paramObject)
    {
      Iterator localIterator = this.a.iterator();
      int i = 0;
      if (paramObject == null) {
        while (localIterator.hasNext())
        {
          if (localIterator.next() == null) {
            return i;
          }
          i++;
        }
      }
      while (localIterator.hasNext())
      {
        if (paramObject.equals(localIterator.next())) {
          return i;
        }
        i++;
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((this.a instanceof List)) {
        return ((List)this.a).lastIndexOf(paramObject);
      }
      Iterator localIterator = this.a.iterator();
      int i = 0;
      int j = -1;
      if (paramObject == null) {
        while (localIterator.hasNext())
        {
          if (localIterator.next() == null) {
            j = i;
          }
          i++;
        }
      }
      while (localIterator.hasNext())
      {
        if (paramObject.equals(localIterator.next())) {
          j = i;
        }
        i++;
      }
      return j;
    }
    
    public ListIterator listIterator()
    {
      return (this.a instanceof List) ? Collections.unmodifiableList((List)this.a).listIterator() : Collections.unmodifiableList(new ArrayList(this.a)).listIterator();
    }
    
    public ListIterator listIterator(int paramInt)
    {
      return (this.a instanceof List) ? Collections.unmodifiableList((List)this.a).listIterator(paramInt) : Collections.unmodifiableList(new ArrayList(this.a)).listIterator(paramInt);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      return (this.a instanceof List) ? Collections.unmodifiableList(((List)this.a).subList(paramInt1, paramInt2)) : Collections.unmodifiableList(new ArrayList(this.a)).subList(paramInt1, paramInt2);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\a\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */