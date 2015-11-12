package de.schlichtherle.truezip.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.CharBuffer;

public final class UriBuilder
{
  private final UriEncoder encoder;
  private StringBuilder builder;
  private String scheme;
  private String authority;
  private String path;
  private String query;
  private String fragment;
  
  public UriBuilder()
  {
    this(false);
  }
  
  public UriBuilder(boolean paramBoolean)
  {
    this.encoder = new UriEncoder(null, paramBoolean);
  }
  
  public UriBuilder(URI paramURI, boolean paramBoolean)
  {
    this.encoder = new UriEncoder(null, paramBoolean);
    setUri(paramURI);
  }
  
  public UriBuilder clear()
  {
    this.scheme = null;
    this.authority = null;
    this.path = null;
    this.query = null;
    this.fragment = null;
    return this;
  }
  
  public String toString()
  {
    try
    {
      return getString();
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new IllegalStateException(localURISyntaxException);
    }
  }
  
  public String getString()
    throws URISyntaxException
  {
    StringBuilder localStringBuilder = resetBuilder();
    int i = -1;
    String str1 = null;
    String str2 = this.scheme;
    String str3 = this.authority;
    String str4 = this.path;
    String str5 = this.query;
    String str6 = this.fragment;
    int j = null != str2 ? 1 : 0;
    if (j != 0) {
      localStringBuilder.append(str2).append(':');
    }
    int k = localStringBuilder.length();
    int m = null != str3 ? 1 : 0;
    if (m != 0) {
      this.encoder.encode(str3, UriEncoder.Encoding.AUTHORITY, localStringBuilder.append("//"));
    }
    int n = 0;
    if ((null != str4) && (!str4.isEmpty())) {
      if (str4.startsWith("/"))
      {
        n = 1;
        this.encoder.encode(str4, UriEncoder.Encoding.ABSOLUTE_PATH, localStringBuilder);
      }
      else if (m != 0)
      {
        n = 1;
        i = localStringBuilder.length();
        str1 = "Relative path with " + (str3.isEmpty() ? "" : "non-") + "empty authority";
        this.encoder.encode(str4, UriEncoder.Encoding.ABSOLUTE_PATH, localStringBuilder);
      }
      else if (j != 0)
      {
        this.encoder.encode(str4, UriEncoder.Encoding.QUERY, localStringBuilder);
      }
      else
      {
        this.encoder.encode(str4, UriEncoder.Encoding.PATH, localStringBuilder);
      }
    }
    if (null != str5)
    {
      localStringBuilder.append('?');
      if ((j != 0) && (n == 0))
      {
        i = localStringBuilder.length();
        str1 = "Query in opaque URI";
      }
      this.encoder.encode(str5, UriEncoder.Encoding.QUERY, localStringBuilder);
    }
    if (!$assertionsDisabled) {
      if (j != (0 < k ? 1 : 0)) {
        throw new AssertionError();
      }
    }
    if ((j != 0) && (k >= localStringBuilder.length()))
    {
      i = localStringBuilder.length();
      str1 = "Empty scheme specific part in absolute URI";
    }
    if (null != str6) {
      this.encoder.encode(str6, UriEncoder.Encoding.FRAGMENT, localStringBuilder.append('#'));
    }
    if (j != 0) {
      validateScheme((CharBuffer)CharBuffer.wrap(localStringBuilder).limit(str2.length()));
    }
    String str7 = localStringBuilder.toString();
    if (0 <= i) {
      throw new QuotedUriSyntaxException(str7, str1, i);
    }
    return str7;
  }
  
  private StringBuilder resetBuilder()
  {
    StringBuilder localStringBuilder = this.builder;
    if (null == localStringBuilder) {
      this.builder = (localStringBuilder = new StringBuilder());
    } else {
      localStringBuilder.setLength(0);
    }
    return localStringBuilder;
  }
  
  public static void validateScheme(String paramString)
    throws URISyntaxException
  {
    validateScheme(CharBuffer.wrap(paramString));
  }
  
  private static void validateScheme(CharBuffer paramCharBuffer)
    throws URISyntaxException
  {
    if (!paramCharBuffer.hasRemaining()) {
      throw newURISyntaxException(paramCharBuffer, "Empty URI scheme");
    }
    int i = paramCharBuffer.get();
    if (((i < 97) || (122 < i)) && ((i < 65) || (90 < i))) {
      throw newURISyntaxException(paramCharBuffer, "Illegal character in URI scheme");
    }
    while (paramCharBuffer.hasRemaining())
    {
      i = paramCharBuffer.get();
      if (((i < 97) || (122 < i)) && ((i < 65) || (90 < i)) && ((i < 48) || (57 < i)) && (i != 43) && (i != 45) && (i != 46)) {
        throw newURISyntaxException(paramCharBuffer, "Illegal character in URI scheme");
      }
    }
  }
  
  private static URISyntaxException newURISyntaxException(CharBuffer paramCharBuffer, String paramString)
  {
    int i = paramCharBuffer.position() - 1;
    return new QuotedUriSyntaxException(paramCharBuffer.rewind().limit(paramCharBuffer.capacity()), paramString, i);
  }
  
  public URI toUri()
  {
    try
    {
      return getUri();
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new IllegalStateException(localURISyntaxException);
    }
  }
  
  public URI getUri()
    throws URISyntaxException
  {
    String str = getString();
    try
    {
      return new URI(str);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new AssertionError(localURISyntaxException);
    }
  }
  
  public void setUri(URI paramURI)
  {
    if (this.encoder.isRaw())
    {
      setScheme(paramURI.getScheme());
      setAuthority(paramURI.getRawAuthority());
      setPath(paramURI.isOpaque() ? paramURI.getRawSchemeSpecificPart() : paramURI.getRawPath());
      setQuery(paramURI.getRawQuery());
      setFragment(paramURI.getRawFragment());
    }
    else
    {
      setScheme(paramURI.getScheme());
      setAuthority(paramURI.getAuthority());
      setPath(paramURI.isOpaque() ? paramURI.getSchemeSpecificPart() : paramURI.getPath());
      setQuery(paramURI.getQuery());
      setFragment(paramURI.getFragment());
    }
  }
  
  public void setScheme(String paramString)
  {
    this.scheme = paramString;
  }
  
  public UriBuilder scheme(String paramString)
  {
    setScheme(paramString);
    return this;
  }
  
  public void setAuthority(String paramString)
  {
    this.authority = paramString;
  }
  
  public UriBuilder authority(String paramString)
  {
    setAuthority(paramString);
    return this;
  }
  
  public void setPath(String paramString)
  {
    this.path = paramString;
  }
  
  public UriBuilder path(String paramString)
  {
    setPath(paramString);
    return this;
  }
  
  public void setQuery(String paramString)
  {
    this.query = paramString;
  }
  
  public void setPathQuery(String paramString)
  {
    int i;
    if ((null != paramString) && (0 <= (i = paramString.indexOf('?'))))
    {
      this.path = paramString.substring(0, i);
      this.query = paramString.substring(i + 1);
    }
    else
    {
      this.path = paramString;
      this.query = null;
    }
  }
  
  public UriBuilder pathQuery(String paramString)
  {
    setPathQuery(paramString);
    return this;
  }
  
  public void setFragment(String paramString)
  {
    this.fragment = paramString;
  }
  
  public UriBuilder fragment(String paramString)
  {
    setFragment(paramString);
    return this;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\UriBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */