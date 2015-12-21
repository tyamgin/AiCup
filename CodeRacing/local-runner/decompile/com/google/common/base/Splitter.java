package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GwtCompatible(emulated=true)
public final class Splitter
{
  private final CharMatcher trimmer;
  private final boolean omitEmptyStrings;
  private final Strategy strategy;
  private final int limit;
  
  private Splitter(Strategy paramStrategy)
  {
    this(paramStrategy, false, CharMatcher.NONE, Integer.MAX_VALUE);
  }
  
  private Splitter(Strategy paramStrategy, boolean paramBoolean, CharMatcher paramCharMatcher, int paramInt)
  {
    this.strategy = paramStrategy;
    this.omitEmptyStrings = paramBoolean;
    this.trimmer = paramCharMatcher;
    this.limit = paramInt;
  }
  
  public static Splitter on(char paramChar)
  {
    return on(CharMatcher.is(paramChar));
  }
  
  public static Splitter on(CharMatcher paramCharMatcher)
  {
    Preconditions.checkNotNull(paramCharMatcher);
    new Splitter(new Strategy()
    {
      public Splitter.SplittingIterator iterator(Splitter paramAnonymousSplitter, CharSequence paramAnonymousCharSequence)
      {
        new Splitter.SplittingIterator(paramAnonymousSplitter, paramAnonymousCharSequence)
        {
          int separatorStart(int paramAnonymous2Int)
          {
            return Splitter.1.this.val$separatorMatcher.indexIn(this.toSplit, paramAnonymous2Int);
          }
          
          int separatorEnd(int paramAnonymous2Int)
          {
            return paramAnonymous2Int + 1;
          }
        };
      }
    });
  }
  
  public static Splitter on(String paramString)
  {
    Preconditions.checkArgument(paramString.length() != 0, "The separator may not be the empty string.");
    new Splitter(new Strategy()
    {
      public Splitter.SplittingIterator iterator(Splitter paramAnonymousSplitter, CharSequence paramAnonymousCharSequence)
      {
        new Splitter.SplittingIterator(paramAnonymousSplitter, paramAnonymousCharSequence)
        {
          public int separatorStart(int paramAnonymous2Int)
          {
            int i = Splitter.2.this.val$separator.length();
            int j = paramAnonymous2Int;
            int k = this.toSplit.length() - i;
            while (j <= k)
            {
              for (int m = 0; m < i; m++) {
                if (this.toSplit.charAt(m + j) != Splitter.2.this.val$separator.charAt(m)) {
                  break label80;
                }
              }
              return j;
              label80:
              j++;
            }
            return -1;
          }
          
          public int separatorEnd(int paramAnonymous2Int)
          {
            return paramAnonymous2Int + Splitter.2.this.val$separator.length();
          }
        };
      }
    });
  }
  
  @GwtIncompatible("java.util.regex")
  public static Splitter on(Pattern paramPattern)
  {
    Preconditions.checkNotNull(paramPattern);
    Preconditions.checkArgument(!paramPattern.matcher("").matches(), "The pattern may not match the empty string: %s", new Object[] { paramPattern });
    new Splitter(new Strategy()
    {
      public Splitter.SplittingIterator iterator(Splitter paramAnonymousSplitter, CharSequence paramAnonymousCharSequence)
      {
        final Matcher localMatcher = this.val$separatorPattern.matcher(paramAnonymousCharSequence);
        new Splitter.SplittingIterator(paramAnonymousSplitter, paramAnonymousCharSequence)
        {
          public int separatorStart(int paramAnonymous2Int)
          {
            return localMatcher.find(paramAnonymous2Int) ? localMatcher.start() : -1;
          }
          
          public int separatorEnd(int paramAnonymous2Int)
          {
            return localMatcher.end();
          }
        };
      }
    });
  }
  
  @GwtIncompatible("java.util.regex")
  public static Splitter onPattern(String paramString)
  {
    return on(Pattern.compile(paramString));
  }
  
  public static Splitter fixedLength(int paramInt)
  {
    Preconditions.checkArgument(paramInt > 0, "The length may not be less than 1");
    new Splitter(new Strategy()
    {
      public Splitter.SplittingIterator iterator(Splitter paramAnonymousSplitter, CharSequence paramAnonymousCharSequence)
      {
        new Splitter.SplittingIterator(paramAnonymousSplitter, paramAnonymousCharSequence)
        {
          public int separatorStart(int paramAnonymous2Int)
          {
            int i = paramAnonymous2Int + Splitter.4.this.val$length;
            return i < this.toSplit.length() ? i : -1;
          }
          
          public int separatorEnd(int paramAnonymous2Int)
          {
            return paramAnonymous2Int;
          }
        };
      }
    });
  }
  
  public Splitter omitEmptyStrings()
  {
    return new Splitter(this.strategy, true, this.trimmer, this.limit);
  }
  
  public Splitter limit(int paramInt)
  {
    Preconditions.checkArgument(paramInt > 0, "must be greater than zero: %s", new Object[] { Integer.valueOf(paramInt) });
    return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, paramInt);
  }
  
  public Splitter trimResults()
  {
    return trimResults(CharMatcher.WHITESPACE);
  }
  
  public Splitter trimResults(CharMatcher paramCharMatcher)
  {
    Preconditions.checkNotNull(paramCharMatcher);
    return new Splitter(this.strategy, this.omitEmptyStrings, paramCharMatcher, this.limit);
  }
  
  public Iterable split(final CharSequence paramCharSequence)
  {
    Preconditions.checkNotNull(paramCharSequence);
    new Iterable()
    {
      public Iterator iterator()
      {
        return Splitter.this.spliterator(paramCharSequence);
      }
      
      public String toString()
      {
        return ']';
      }
    };
  }
  
  private Iterator spliterator(CharSequence paramCharSequence)
  {
    return this.strategy.iterator(this, paramCharSequence);
  }
  
  @Beta
  public MapSplitter withKeyValueSeparator(String paramString)
  {
    return withKeyValueSeparator(on(paramString));
  }
  
  @Beta
  public MapSplitter withKeyValueSeparator(char paramChar)
  {
    return withKeyValueSeparator(on(paramChar));
  }
  
  @Beta
  public MapSplitter withKeyValueSeparator(Splitter paramSplitter)
  {
    return new MapSplitter(this, paramSplitter, null);
  }
  
  private static abstract class SplittingIterator
    extends AbstractIterator
  {
    final CharSequence toSplit;
    final CharMatcher trimmer;
    final boolean omitEmptyStrings;
    int offset = 0;
    int limit;
    
    abstract int separatorStart(int paramInt);
    
    abstract int separatorEnd(int paramInt);
    
    protected SplittingIterator(Splitter paramSplitter, CharSequence paramCharSequence)
    {
      this.trimmer = paramSplitter.trimmer;
      this.omitEmptyStrings = paramSplitter.omitEmptyStrings;
      this.limit = paramSplitter.limit;
      this.toSplit = paramCharSequence;
    }
    
    protected String computeNext()
    {
      int i = this.offset;
      while (this.offset != -1)
      {
        int j = i;
        int m = separatorStart(this.offset);
        int k;
        if (m == -1)
        {
          k = this.toSplit.length();
          this.offset = -1;
        }
        else
        {
          k = m;
          this.offset = separatorEnd(m);
        }
        if (this.offset == i)
        {
          this.offset += 1;
          if (this.offset >= this.toSplit.length()) {
            this.offset = -1;
          }
        }
        else
        {
          while ((j < k) && (this.trimmer.matches(this.toSplit.charAt(j)))) {
            j++;
          }
          while ((k > j) && (this.trimmer.matches(this.toSplit.charAt(k - 1)))) {
            k--;
          }
          if ((this.omitEmptyStrings) && (j == k))
          {
            i = this.offset;
          }
          else
          {
            if (this.limit == 1)
            {
              k = this.toSplit.length();
              this.offset = -1;
              while ((k > j) && (this.trimmer.matches(this.toSplit.charAt(k - 1)))) {
                k--;
              }
            }
            this.limit -= 1;
            return this.toSplit.subSequence(j, k).toString();
          }
        }
      }
      return (String)endOfData();
    }
  }
  
  private static abstract interface Strategy
  {
    public abstract Iterator iterator(Splitter paramSplitter, CharSequence paramCharSequence);
  }
  
  @Beta
  public static final class MapSplitter
  {
    private static final String INVALID_ENTRY_MESSAGE = "Chunk [%s] is not a valid entry";
    private final Splitter outerSplitter;
    private final Splitter entrySplitter;
    
    private MapSplitter(Splitter paramSplitter1, Splitter paramSplitter2)
    {
      this.outerSplitter = paramSplitter1;
      this.entrySplitter = ((Splitter)Preconditions.checkNotNull(paramSplitter2));
    }
    
    public Map split(CharSequence paramCharSequence)
    {
      LinkedHashMap localLinkedHashMap = new LinkedHashMap();
      Iterator localIterator1 = this.outerSplitter.split(paramCharSequence).iterator();
      while (localIterator1.hasNext())
      {
        String str1 = (String)localIterator1.next();
        Iterator localIterator2 = this.entrySplitter.spliterator(str1);
        Preconditions.checkArgument(localIterator2.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { str1 });
        String str2 = (String)localIterator2.next();
        Preconditions.checkArgument(!localLinkedHashMap.containsKey(str2), "Duplicate key [%s] found.", new Object[] { str2 });
        Preconditions.checkArgument(localIterator2.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { str1 });
        String str3 = (String)localIterator2.next();
        localLinkedHashMap.put(str2, str3);
        Preconditions.checkArgument(!localIterator2.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { str1 });
      }
      return Collections.unmodifiableMap(localLinkedHashMap);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Splitter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */