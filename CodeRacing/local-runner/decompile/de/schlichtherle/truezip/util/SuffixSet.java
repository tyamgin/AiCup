package de.schlichtherle.truezip.util;

import java.util.Collection;
import java.util.Locale;

public final class SuffixSet
  extends CanonicalStringSet
{
  public SuffixSet()
  {
    super(new SuffixMapper(null), '|');
  }
  
  public SuffixSet(String paramString)
  {
    super(new SuffixMapper(null), '|');
    super.addAll(paramString);
  }
  
  public SuffixSet(Collection paramCollection)
  {
    super(new SuffixMapper(null), '|');
    super.addAll(paramCollection);
  }
  
  private static class SuffixMapper
    implements CanonicalStringSet.Canonicalizer
  {
    public String map(Object paramObject)
    {
      for (String str = paramObject.toString(); (0 < str.length()) && (str.charAt(0) == '.'); str = str.substring(1)) {}
      return str.toLowerCase(Locale.ROOT);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\SuffixSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */