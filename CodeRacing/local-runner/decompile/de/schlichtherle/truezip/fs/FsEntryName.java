package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.entry.EntryName;
import de.schlichtherle.truezip.util.QuotedUriSyntaxException;
import java.net.URI;
import java.net.URISyntaxException;

public final class FsEntryName
  extends EntryName
{
  public static final FsEntryName ROOT;
  
  public FsEntryName(URI paramURI)
    throws URISyntaxException
  {
    this(paramURI, FsUriModifier.NULL);
  }
  
  public FsEntryName(URI paramURI, FsUriModifier paramFsUriModifier)
    throws URISyntaxException
  {
    super(paramURI = paramFsUriModifier.modify(paramURI, FsUriModifier.PostFix.ENTRY_NAME));
    parse(paramURI);
  }
  
  private void parse(URI paramURI)
    throws URISyntaxException
  {
    String str = paramURI.getRawPath();
    if (str.startsWith("/")) {
      throw new QuotedUriSyntaxException(paramURI, "Illegal start of URI path component");
    }
    if ((!str.isEmpty()) && ("../".startsWith(str.substring(0, Math.min(str.length(), "../".length()))))) {
      throw new QuotedUriSyntaxException(paramURI, "Illegal start of URI path component");
    }
    if (str.endsWith("/")) {
      throw new QuotedUriSyntaxException(paramURI, "Illegal separator \"/\" at end of URI path");
    }
    assert (invariants());
  }
  
  private boolean invariants()
  {
    assert (null != toUri());
    assert (toUri().normalize() == toUri());
    String str = toUri().getRawPath();
    assert (!"..".equals(str));
    assert (!str.startsWith("/"));
    assert (!str.startsWith("./"));
    assert (!str.startsWith("../"));
    assert (!str.endsWith("/"));
    return true;
  }
  
  public boolean isRoot()
  {
    URI localURI = toUri();
    String str1 = localURI.getRawPath();
    if ((null != str1) && (!str1.isEmpty())) {
      return false;
    }
    String str2 = localURI.getRawQuery();
    return null == str2;
  }
  
  static
  {
    try
    {
      ROOT = new FsEntryName(new URI(""));
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new AssertionError(localURISyntaxException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsEntryName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */