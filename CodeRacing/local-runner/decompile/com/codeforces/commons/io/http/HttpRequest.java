package com.codeforces.commons.io.http;

import com.codeforces.commons.io.CountingInputStream;
import com.codeforces.commons.io.CountingInputStream.ReadEvent;
import com.codeforces.commons.io.IoUtil;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.process.ThreadUtil;
import com.codeforces.commons.process.ThreadUtil.ExecutionStrategy;
import com.codeforces.commons.process.ThreadUtil.ExecutionStrategy.Type;
import com.codeforces.commons.properties.internal.CommonsPropertiesUtil;
import com.codeforces.commons.text.StringUtil;
import com.codeforces.commons.text.UrlUtil;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

public final class HttpRequest
{
  private static final Logger logger = Logger.getLogger(HttpRequest.class);
  private final String url;
  private final Map parametersByName = new LinkedHashMap(8);
  private byte[] binaryEntity;
  private boolean gzip;
  private final Map headersByName = new LinkedHashMap(8);
  private HttpMethod method = HttpMethod.GET;
  private int timeoutMillis = NumberUtil.toInt(600000L);
  private int maxRetryCount = 1;
  private HttpResponseChecker responseChecker = new HttpResponseChecker()
  {
    public boolean check(HttpResponse paramAnonymousHttpResponse)
    {
      return !paramAnonymousHttpResponse.hasIoException();
    }
  };
  private ThreadUtil.ExecutionStrategy retryStrategy = new ThreadUtil.ExecutionStrategy(250L, ThreadUtil.ExecutionStrategy.Type.LINEAR);
  private long maxSizeBytes = 1073741824L;
  
  public static HttpRequest create(String paramString, Object... paramVarArgs)
  {
    return new HttpRequest(paramString, paramVarArgs);
  }
  
  private HttpRequest(String paramString, Object... paramVarArgs)
  {
    this.url = paramString;
    appendParameters(paramVarArgs);
  }
  
  public HttpRequest appendParameters(Object... paramVarArgs)
  {
    if (hasBinaryEntity()) {
      throw new IllegalStateException("Can't send parameters and binary entity with a single request.");
    }
    String[] arrayOfString = validateAndEncodeParameters(this.url, paramVarArgs);
    int i = arrayOfString.length;
    for (int j = 0; j < i; j += 2)
    {
      String str1 = arrayOfString[j];
      String str2 = arrayOfString[(j + 1)];
      Object localObject = (List)this.parametersByName.get(str1);
      if (localObject == null)
      {
        localObject = new ArrayList(1);
        this.parametersByName.put(str1, localObject);
      }
      ((List)localObject).add(str2);
    }
    return this;
  }
  
  public HttpRequest setBinaryEntity(byte[] paramArrayOfByte)
  {
    if (!this.parametersByName.isEmpty()) {
      throw new IllegalStateException("Can't send parameters and binary entity with a single request.");
    }
    this.binaryEntity = paramArrayOfByte;
    return this;
  }
  
  public boolean hasBinaryEntity()
  {
    return this.binaryEntity != null;
  }
  
  public HttpRequest setGzip(boolean paramBoolean)
  {
    this.gzip = paramBoolean;
    return this;
  }
  
  public HttpRequest setMethod(HttpMethod paramHttpMethod)
  {
    Preconditions.checkNotNull(paramHttpMethod, "Argument 'method' is null.");
    this.method = paramHttpMethod;
    return this;
  }
  
  public HttpRequest setTimeoutMillis(int paramInt)
  {
    Preconditions.checkArgument(paramInt > 0, "Argument 'timeoutMillis' is zero or negative.");
    this.timeoutMillis = paramInt;
    return this;
  }
  
  public HttpResponse executeAndReturnResponse()
  {
    return internalExecute(true);
  }
  
  private HttpResponse internalExecute(boolean paramBoolean)
  {
    String str1 = appendGetParametersToUrl(this.url);
    if ((this.method == HttpMethod.GET) && (hasBinaryEntity()))
    {
      String str2 = "Can't write binary entity to '" + str1 + "' with GET method.";
      logger.warn(str2);
      return new HttpResponse(-1, null, null, new IOException(str2));
    }
    long l = System.currentTimeMillis();
    for (int i = 1; i < this.maxRetryCount; i++)
    {
      HttpResponse localHttpResponse = internalGetHttpResponse(paramBoolean, str1, l);
      if (this.responseChecker.check(localHttpResponse)) {
        return localHttpResponse;
      }
      ThreadUtil.sleep(this.retryStrategy.getDelayTimeMillis(i));
    }
    return internalGetHttpResponse(paramBoolean, str1, l);
  }
  
  private HttpResponse internalGetHttpResponse(boolean paramBoolean, String paramString, long paramLong)
  {
    HttpURLConnection localHttpURLConnection;
    Object localObject1;
    try
    {
      localHttpURLConnection = newConnection(paramString, (this.method == HttpMethod.POST) && ((!this.parametersByName.isEmpty()) || (hasBinaryEntity())));
    }
    catch (IOException localIOException1)
    {
      localObject1 = "Can't create connection to '" + paramString + "'.";
      logger.warn(localObject1, localIOException1);
      return new HttpResponse(-1, null, null, new IOException((String)localObject1, localIOException1));
    }
    if (this.method == HttpMethod.POST)
    {
      if (!this.parametersByName.isEmpty()) {
        try
        {
          writePostParameters(localHttpURLConnection, this.parametersByName);
        }
        catch (IOException localIOException2)
        {
          localObject1 = "Can't write POST parameters to '" + paramString + "'.";
          logger.warn(localObject1, localIOException2);
          return new HttpResponse(-1, null, null, new IOException((String)localObject1, localIOException2));
        }
      }
      if (hasBinaryEntity()) {
        try
        {
          writeEntity(localHttpURLConnection, this.binaryEntity);
        }
        catch (IOException localIOException3)
        {
          localObject1 = "Can't write binary entity to '" + paramString + "'.";
          logger.warn(localObject1, localIOException3);
          return new HttpResponse(-1, null, null, new IOException((String)localObject1, localIOException3));
        }
      }
    }
    try
    {
      localHttpURLConnection.connect();
      int i = localHttpURLConnection.getResponseCode();
      localObject1 = getBytes(localHttpURLConnection, paramBoolean, paramLong);
      localHttpResponse = new HttpResponse(i, (byte[])localObject1, localHttpURLConnection.getHeaderFields(), null);
      return localHttpResponse;
    }
    catch (IOException localIOException4)
    {
      localObject1 = "Can't read response from '" + paramString + "'.";
      logger.warn(localObject1, localIOException4);
      HttpResponse localHttpResponse = new HttpResponse(-1, null, localHttpURLConnection.getHeaderFields(), new IOException((String)localObject1, localIOException4));
      return localHttpResponse;
    }
    finally
    {
      localHttpURLConnection.disconnect();
    }
  }
  
  private byte[] getBytes(HttpURLConnection paramHttpURLConnection, boolean paramBoolean, final long paramLong)
    throws IOException
  {
    byte[] arrayOfByte;
    if (paramBoolean)
    {
      Object localObject;
      try
      {
        localObject = paramHttpURLConnection.getInputStream();
      }
      catch (IOException localIOException)
      {
        localObject = paramHttpURLConnection.getErrorStream();
        if (localObject == null) {
          throw localIOException;
        }
      }
      if (localObject == null)
      {
        arrayOfByte = null;
      }
      else
      {
        if ("gzip".equalsIgnoreCase(paramHttpURLConnection.getContentEncoding())) {
          localObject = new GZIPInputStream((InputStream)localObject);
        } else if ("deflate".equalsIgnoreCase(paramHttpURLConnection.getContentEncoding())) {
          localObject = new InflaterInputStream((InputStream)localObject);
        } else if ("zip".equalsIgnoreCase(paramHttpURLConnection.getContentEncoding())) {
          localObject = new ZipInputStream((InputStream)localObject);
        }
        localObject = new CountingInputStream((InputStream)localObject, new CountingInputStream.ReadEvent()
        {
          public void onRead(long paramAnonymousLong1, long paramAnonymousLong2)
            throws IOException
          {
            if (System.currentTimeMillis() - paramLong > HttpRequest.this.timeoutMillis) {
              throw new IOException("Can't read response within " + HttpRequest.this.timeoutMillis + " ms.");
            }
          }
        });
        arrayOfByte = IoUtil.toByteArray((InputStream)localObject, NumberUtil.toInt(this.maxSizeBytes), true);
      }
    }
    else
    {
      arrayOfByte = null;
    }
    return arrayOfByte;
  }
  
  private String appendGetParametersToUrl(String paramString)
  {
    if (this.method == HttpMethod.GET)
    {
      Iterator localIterator1 = this.parametersByName.entrySet().iterator();
      while (localIterator1.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator1.next();
        String str1 = (String)localEntry.getKey();
        Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
        while (localIterator2.hasNext())
        {
          String str2 = (String)localIterator2.next();
          paramString = UrlUtil.appendParameterToUrl(paramString, str1, str2);
        }
      }
    }
    return paramString;
  }
  
  private static String[] validateAndEncodeParameters(String paramString, Object... paramVarArgs)
  {
    if (!UrlUtil.isValidUrl(paramString)) {
      throw new IllegalArgumentException('\'' + paramString + "' is not a valid URL.");
    }
    boolean bool;
    try
    {
      bool = CommonsPropertiesUtil.getSecureHosts().contains(new URL(paramString).getHost());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new IllegalArgumentException('\'' + paramString + "' is not a valid URL.", localMalformedURLException);
    }
    int i = paramVarArgs.length;
    if (i == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    if (i % 2 != 0) {
      throw new IllegalArgumentException("Argument 'parameters' should contain even number of elements, i.e. should consist of key-value pairs.");
    }
    List localList1 = CommonsPropertiesUtil.getSecurePasswords();
    List localList2 = CommonsPropertiesUtil.getPrivateParameters();
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j += 2)
    {
      Object localObject1 = paramVarArgs[j];
      Object localObject2 = paramVarArgs[(j + 1)];
      if ((!(localObject1 instanceof String)) || (StringUtil.isBlank((String)localObject1))) {
        throw new IllegalArgumentException(String.format("Each parameter name should be non-blank string, but found: '%s'.", new Object[] { localObject1 }));
      }
      if (localObject2 == null) {
        throw new IllegalArgumentException(String.format("Value of parameter '%s' is null.", new Object[] { localObject1 }));
      }
      try
      {
        arrayOfString[j] = URLEncoder.encode((String)localObject1, "UTF-8");
        if ((bool) || ((!localList2.contains(localObject1)) && (!localList1.contains(localObject2.toString())))) {
          arrayOfString[(j + 1)] = URLEncoder.encode(localObject2.toString(), "UTF-8");
        } else {
          arrayOfString[(j + 1)] = "";
        }
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new RuntimeException("UTF-8 is unsupported.", localUnsupportedEncodingException);
      }
    }
    return arrayOfString;
  }
  
  private HttpURLConnection newConnection(String paramString, boolean paramBoolean)
    throws IOException
  {
    URL localURL = new URL(paramString);
    Proxy localProxy = getProxy(localURL.getProtocol());
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)(localProxy == null ? localURL.openConnection() : localURL.openConnection(localProxy));
    if ((localHttpURLConnection instanceof HttpsURLConnection)) {
      bypassSecureHostSslCertificateCheck((HttpsURLConnection)localHttpURLConnection, localURL);
    }
    localHttpURLConnection.setReadTimeout(this.timeoutMillis);
    localHttpURLConnection.setConnectTimeout(this.timeoutMillis);
    localHttpURLConnection.setRequestMethod(this.method.name());
    localHttpURLConnection.setDoInput(true);
    localHttpURLConnection.setDoOutput(paramBoolean);
    localHttpURLConnection.setInstanceFollowRedirects(true);
    localHttpURLConnection.setRequestProperty("Connection", "close");
    if (this.method == HttpMethod.POST)
    {
      if (hasBinaryEntity()) {
        localHttpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
      } else if (!this.parametersByName.isEmpty()) {
        localHttpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      }
      if ((this.gzip) && ((hasBinaryEntity()) || (!this.parametersByName.isEmpty()))) {
        localHttpURLConnection.setRequestProperty("Content-Encoding", "gzip");
      }
    }
    Iterator localIterator1 = this.headersByName.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str1 = (String)localEntry.getKey();
      int i = 1;
      Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        if (i != 0)
        {
          localHttpURLConnection.setRequestProperty(str1, str2);
          i = 0;
        }
        else
        {
          localHttpURLConnection.addRequestProperty(str1, str2);
        }
      }
    }
    return localHttpURLConnection;
  }
  
  private static void bypassSecureHostSslCertificateCheck(HttpsURLConnection paramHttpsURLConnection, URL paramURL)
  {
    if ((!CommonsPropertiesUtil.isBypassCertificateCheck()) || (!CommonsPropertiesUtil.getSecureHosts().contains(paramURL.getHost()))) {
      return;
    }
    X509TrustManager local3 = new X509TrustManager()
    {
      public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString) {}
      
      public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString) {}
      
      public X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }
    };
    SSLContext localSSLContext;
    try
    {
      localSSLContext = SSLContext.getInstance("SSL");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      logger.warn("Can't get instance of SSL context.", localNoSuchAlgorithmException);
      return;
    }
    try
    {
      localSSLContext.init(null, new TrustManager[] { local3 }, new SecureRandom());
    }
    catch (KeyManagementException localKeyManagementException)
    {
      logger.warn("Can't initialize SSL context.", localKeyManagementException);
      return;
    }
    paramHttpsURLConnection.setSSLSocketFactory(localSSLContext.getSocketFactory());
  }
  
  private static Proxy getProxy(String paramString)
  {
    if (!Boolean.parseBoolean(System.getProperty("proxySet"))) {
      return null;
    }
    if ((!"http".equalsIgnoreCase(paramString)) && (!"https".equalsIgnoreCase(paramString))) {
      return null;
    }
    String str = System.getProperty(paramString + ".proxyHost");
    if (StringUtil.isBlank(str)) {
      return null;
    }
    int i;
    try
    {
      i = Integer.parseInt(System.getProperty(paramString + ".proxyPort"));
      if ((i <= 0) || (i > 65535)) {
        return null;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return null;
    }
    return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(str, i));
  }
  
  private void writePostParameters(HttpURLConnection paramHttpURLConnection, Map paramMap)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator1 = paramMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str1 = (String)localEntry.getKey();
      Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append('&');
        }
        localStringBuilder.append(str1).append('=').append(str2);
      }
    }
    writeEntity(paramHttpURLConnection, localStringBuilder.toString().getBytes(Charsets.UTF_8));
  }
  
  /* Error */
  private void writeEntity(HttpURLConnection paramHttpURLConnection, byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: new 57	java/io/BufferedOutputStream
    //   3: dup
    //   4: aload_0
    //   5: getfield 116	com/codeforces/commons/io/http/HttpRequest:gzip	Z
    //   8: ifeq +17 -> 25
    //   11: new 95	java/util/zip/GZIPOutputStream
    //   14: dup
    //   15: aload_1
    //   16: invokevirtual 199	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
    //   19: invokespecial 224	java/util/zip/GZIPOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   22: goto +7 -> 29
    //   25: aload_1
    //   26: invokevirtual 199	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
    //   29: getstatic 112	com/codeforces/commons/io/IoUtil:BUFFER_SIZE	I
    //   32: invokespecial 165	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   35: astore_3
    //   36: invokestatic 190	java/lang/System:currentTimeMillis	()J
    //   39: lstore 4
    //   41: aload_3
    //   42: aload_2
    //   43: invokevirtual 170	java/io/OutputStream:write	([B)V
    //   46: aload_3
    //   47: invokevirtual 169	java/io/OutputStream:flush	()V
    //   50: aload_3
    //   51: invokevirtual 168	java/io/OutputStream:close	()V
    //   54: invokestatic 190	java/lang/System:currentTimeMillis	()J
    //   57: lload 4
    //   59: lsub
    //   60: lstore 6
    //   62: lload 6
    //   64: ldc2_w 104
    //   67: lcmp
    //   68: ifle +44 -> 112
    //   71: getstatic 118	com/codeforces/commons/io/http/HttpRequest:logger	Lorg/apache/log4j/Logger;
    //   74: ldc 26
    //   76: iconst_3
    //   77: anewarray 68	java/lang/Object
    //   80: dup
    //   81: iconst_0
    //   82: lload 6
    //   84: invokestatic 178	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   87: aastore
    //   88: dup
    //   89: iconst_1
    //   90: aload_2
    //   91: arraylength
    //   92: invokestatic 177	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   95: aastore
    //   96: dup
    //   97: iconst_2
    //   98: aload_0
    //   99: getfield 116	com/codeforces/commons/io/http/HttpRequest:gzip	Z
    //   102: invokestatic 172	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   105: aastore
    //   106: invokestatic 183	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   109: invokevirtual 232	org/apache/log4j/Logger:info	(Ljava/lang/Object;)V
    //   112: goto +75 -> 187
    //   115: astore 6
    //   117: aload_3
    //   118: invokestatic 132	com/codeforces/commons/io/IoUtil:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   121: aload 6
    //   123: athrow
    //   124: astore 8
    //   126: invokestatic 190	java/lang/System:currentTimeMillis	()J
    //   129: lload 4
    //   131: lsub
    //   132: lstore 9
    //   134: lload 9
    //   136: ldc2_w 104
    //   139: lcmp
    //   140: ifle +44 -> 184
    //   143: getstatic 118	com/codeforces/commons/io/http/HttpRequest:logger	Lorg/apache/log4j/Logger;
    //   146: ldc 26
    //   148: iconst_3
    //   149: anewarray 68	java/lang/Object
    //   152: dup
    //   153: iconst_0
    //   154: lload 9
    //   156: invokestatic 178	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   159: aastore
    //   160: dup
    //   161: iconst_1
    //   162: aload_2
    //   163: arraylength
    //   164: invokestatic 177	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   167: aastore
    //   168: dup
    //   169: iconst_2
    //   170: aload_0
    //   171: getfield 116	com/codeforces/commons/io/http/HttpRequest:gzip	Z
    //   174: invokestatic 172	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   177: aastore
    //   178: invokestatic 183	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   181: invokevirtual 232	org/apache/log4j/Logger:info	(Ljava/lang/Object;)V
    //   184: aload 8
    //   186: athrow
    //   187: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	188	0	this	HttpRequest
    //   0	188	1	paramHttpURLConnection	HttpURLConnection
    //   0	188	2	paramArrayOfByte	byte[]
    //   35	83	3	localBufferedOutputStream	java.io.BufferedOutputStream
    //   39	91	4	l1	long
    //   60	23	6	l2	long
    //   115	7	6	localIOException	IOException
    //   124	61	8	localObject	Object
    //   132	23	9	l3	long
    // Exception table:
    //   from	to	target	type
    //   41	54	115	java/io/IOException
    //   41	54	124	finally
    //   115	126	124	finally
  }
  
  static Map getDeepUnmodifiableMap(Map paramMap)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap(paramMap);
    Iterator localIterator = localLinkedHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localEntry.setValue(Collections.unmodifiableList(new ArrayList((Collection)localEntry.getValue())));
    }
    return Collections.unmodifiableMap(localLinkedHashMap);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\http\HttpRequest.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */