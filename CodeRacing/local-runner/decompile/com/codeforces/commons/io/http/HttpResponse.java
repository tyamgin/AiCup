package com.codeforces.commons.io.http;

import com.codeforces.commons.text.StringUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpResponse
{
  private final int code;
  private final byte[] bytes;
  private final Map headersByName;
  private final IOException ioException;
  
  HttpResponse(int paramInt, byte[] paramArrayOfByte, Map paramMap, IOException paramIOException)
  {
    if ((paramInt == -1 ? 1 : 0) == (paramIOException == null ? 1 : 0)) {
      throw new IllegalArgumentException("Argument 'ioException' should be set if and only if argument 'code' is -1.");
    }
    this.code = paramInt;
    this.bytes = paramArrayOfByte;
    this.headersByName = ((paramMap == null) || (paramMap.isEmpty()) ? null : HttpRequest.getDeepUnmodifiableMap(paramMap));
    this.ioException = paramIOException;
  }
  
  public int getCode()
  {
    return this.code;
  }
  
  public IOException getIoException()
  {
    return this.ioException;
  }
  
  public boolean hasIoException()
  {
    return this.ioException != null;
  }
  
  public String getUtf8String()
  {
    return this.bytes == null ? null : new String(this.bytes, StandardCharsets.UTF_8);
  }
  
  public String toString()
  {
    return String.format("Response {code=%d, size=%s, s='%s'}", new Object[] { Integer.valueOf(this.code), this.bytes == null ? "null" : Integer.toString(this.bytes.length), StringUtil.shrinkTo(getUtf8String(), 50) });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\http\HttpResponse.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */