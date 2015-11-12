package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@Beta
public final class Resources
{
  public static InputSupplier newInputStreamSupplier(URL paramURL)
  {
    return ByteStreams.asInputSupplier(asByteSource(paramURL));
  }
  
  public static ByteSource asByteSource(URL paramURL)
  {
    return new UrlByteSource(paramURL, null);
  }
  
  public static InputSupplier newReaderSupplier(URL paramURL, Charset paramCharset)
  {
    return CharStreams.asInputSupplier(asCharSource(paramURL, paramCharset));
  }
  
  public static CharSource asCharSource(URL paramURL, Charset paramCharset)
  {
    return asByteSource(paramURL).asCharSource(paramCharset);
  }
  
  public static byte[] toByteArray(URL paramURL)
    throws IOException
  {
    return asByteSource(paramURL).read();
  }
  
  public static String toString(URL paramURL, Charset paramCharset)
    throws IOException
  {
    return asCharSource(paramURL, paramCharset).read();
  }
  
  public static Object readLines(URL paramURL, Charset paramCharset, LineProcessor paramLineProcessor)
    throws IOException
  {
    return CharStreams.readLines(newReaderSupplier(paramURL, paramCharset), paramLineProcessor);
  }
  
  public static List readLines(URL paramURL, Charset paramCharset)
    throws IOException
  {
    return CharStreams.readLines(newReaderSupplier(paramURL, paramCharset));
  }
  
  public static void copy(URL paramURL, OutputStream paramOutputStream)
    throws IOException
  {
    asByteSource(paramURL).copyTo(paramOutputStream);
  }
  
  public static URL getResource(String paramString)
  {
    URL localURL = Resources.class.getClassLoader().getResource(paramString);
    Preconditions.checkArgument(localURL != null, "resource %s not found.", new Object[] { paramString });
    return localURL;
  }
  
  public static URL getResource(Class paramClass, String paramString)
  {
    URL localURL = paramClass.getResource(paramString);
    Preconditions.checkArgument(localURL != null, "resource %s relative to %s not found.", new Object[] { paramString, paramClass.getName() });
    return localURL;
  }
  
  private static final class UrlByteSource
    extends ByteSource
  {
    private final URL url;
    
    private UrlByteSource(URL paramURL)
    {
      this.url = ((URL)Preconditions.checkNotNull(paramURL));
    }
    
    public InputStream openStream()
      throws IOException
    {
      return this.url.openStream();
    }
    
    public String toString()
    {
      return "Resources.newByteSource(" + this.url + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\Resources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */