package de.schlichtherle.truezip.entry;

import de.schlichtherle.truezip.util.QuotedUriSyntaxException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class EntryName
  implements Serializable, Comparable
{
  private URI uri;
  
  public EntryName(URI paramURI)
    throws URISyntaxException
  {
    parse(paramURI);
  }
  
  private void parse(URI paramURI)
    throws URISyntaxException
  {
    if (paramURI.isAbsolute()) {
      throw new QuotedUriSyntaxException(paramURI, "Scheme component defined.");
    }
    if (null != paramURI.getRawAuthority()) {
      throw new QuotedUriSyntaxException(paramURI, "Authority component defined.");
    }
    if (null == paramURI.getRawPath()) {
      throw new QuotedUriSyntaxException(paramURI, "Path component undefined.");
    }
    if (null != paramURI.getRawFragment()) {
      throw new QuotedUriSyntaxException(paramURI, "Fragment component defined.");
    }
    this.uri = paramURI;
    assert (invariants());
  }
  
  private boolean invariants()
  {
    assert (null != toUri());
    assert (!toUri().isAbsolute());
    assert (null != toUri().getRawPath());
    assert (null == toUri().getRawFragment());
    return true;
  }
  
  public final URI toUri()
  {
    return this.uri;
  }
  
  public final String getPath()
  {
    return this.uri.getPath();
  }
  
  public final boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof EntryName)) && (this.uri.equals(((EntryName)paramObject).uri)));
  }
  
  public final int compareTo(EntryName paramEntryName)
  {
    return this.uri.compareTo(paramEntryName.uri);
  }
  
  public final int hashCode()
  {
    return this.uri.hashCode();
  }
  
  public final String toString()
  {
    return this.uri.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\entry\EntryName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */