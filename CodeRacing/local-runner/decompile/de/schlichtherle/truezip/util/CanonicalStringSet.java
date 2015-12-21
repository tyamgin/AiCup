package de.schlichtherle.truezip.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class CanonicalStringSet
  extends AbstractSet
{
  private final Canonicalizer canonicalizer;
  private final char separator;
  private final Set set = new TreeSet();
  
  public CanonicalStringSet(Canonicalizer paramCanonicalizer, char paramChar)
  {
    if (null == paramCanonicalizer) {
      throw new NullPointerException();
    }
    this.canonicalizer = paramCanonicalizer;
    this.separator = paramChar;
  }
  
  public boolean isEmpty()
  {
    return this.set.isEmpty();
  }
  
  public int size()
  {
    return this.set.size();
  }
  
  public Iterator iterator()
  {
    return this.set.iterator();
  }
  
  public Object[] toArray()
  {
    return this.set.toArray();
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return this.set.toArray(paramArrayOfObject);
  }
  
  public String toString()
  {
    int i = size() * 11;
    if (0 >= i) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(i);
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(this.separator);
      }
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  public boolean contains(Object paramObject)
  {
    return this.set.contains(this.canonicalizer.map(paramObject));
  }
  
  public boolean add(String paramString)
  {
    return this.set.add(this.canonicalizer.map(paramString));
  }
  
  public boolean remove(Object paramObject)
  {
    return this.set.remove(this.canonicalizer.map(paramObject));
  }
  
  public void clear()
  {
    this.set.clear();
  }
  
  public boolean addAll(String paramString)
  {
    boolean bool = false;
    CanonicalStringIterator localCanonicalStringIterator = new CanonicalStringIterator(paramString, null);
    while (localCanonicalStringIterator.hasNext()) {
      bool |= this.set.add(localCanonicalStringIterator.next());
    }
    return bool;
  }
  
  public boolean retainAll(CanonicalStringSet paramCanonicalStringSet)
  {
    return this.set.retainAll(paramCanonicalStringSet.set);
  }
  
  public boolean removeAll(CanonicalStringSet paramCanonicalStringSet)
  {
    return this.set.removeAll(paramCanonicalStringSet.set);
  }
  
  private class CanonicalStringIterator
    implements Iterator
  {
    private final StringTokenizer tokenizer;
    private String canonical;
    
    private CanonicalStringIterator(String paramString)
    {
      this.tokenizer = new StringTokenizer(paramString, "" + CanonicalStringSet.this.separator);
      advance();
    }
    
    private void advance()
    {
      while (this.tokenizer.hasMoreTokens()) {
        if (null != (this.canonical = CanonicalStringSet.this.canonicalizer.map(this.tokenizer.nextToken()))) {
          return;
        }
      }
      this.canonical = null;
    }
    
    public boolean hasNext()
    {
      return null != this.canonical;
    }
    
    public String next()
    {
      String str = this.canonical;
      if (null == str) {
        throw new NoSuchElementException();
      }
      advance();
      return str;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  public static abstract interface Canonicalizer
  {
    public abstract String map(Object paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\CanonicalStringSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */