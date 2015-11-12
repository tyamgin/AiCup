package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.UriBuilder;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Locale;

public final class FsScheme
  implements Serializable, Comparable
{
  private final String scheme;
  
  public static FsScheme create(String paramString)
  {
    try
    {
      return new FsScheme(paramString);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new IllegalArgumentException(localURISyntaxException);
    }
  }
  
  public FsScheme(String paramString)
    throws URISyntaxException
  {
    UriBuilder.validateScheme(paramString);
    this.scheme = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof FsScheme)) && (this.scheme.equalsIgnoreCase(((FsScheme)paramObject).scheme)));
  }
  
  public int compareTo(FsScheme paramFsScheme)
  {
    return this.scheme.compareToIgnoreCase(paramFsScheme.scheme);
  }
  
  public int hashCode()
  {
    return this.scheme.toLowerCase(Locale.ROOT).hashCode();
  }
  
  public String toString()
  {
    return this.scheme;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */