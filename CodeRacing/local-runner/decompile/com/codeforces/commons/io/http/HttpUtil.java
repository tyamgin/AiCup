package com.codeforces.commons.io.http;

public final class HttpUtil
{
  public static HttpRequest newRequest(String paramString, Object... paramVarArgs)
  {
    return HttpRequest.create(paramString, paramVarArgs);
  }
  
  public static HttpResponse executePostRequestAndReturnResponse(int paramInt, String paramString, Object... paramVarArgs)
  {
    return newRequest(paramString, paramVarArgs).setTimeoutMillis(paramInt).setMethod(HttpMethod.POST).executeAndReturnResponse();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\http\HttpUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */