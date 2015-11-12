package com.codeforces.commons.text;

import java.net.URI;
import org.apache.commons.validator.routines.UrlValidator;

public class UrlUtil
{
  private static final String[] ALLOWED_SCHEMES = { "http", "https" };
  
  public static String appendParameterToUrl(String paramString1, String paramString2, String paramString3)
  {
    if ((!isValidUri(paramString1)) || (StringUtil.isBlank(paramString2))) {
      return paramString1;
    }
    String str = paramString2 + '=' + paramString3;
    int i = paramString1.indexOf('?');
    int j = paramString1.indexOf('#');
    if ((i == -1) && (j == -1)) {
      return paramString1 + '?' + str;
    }
    if ((i == -1) || ((j != -1) && (i > j))) {
      return paramString1.substring(0, j) + '?' + str + paramString1.substring(j);
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString1.substring(0, i + 1)).append(str);
    if (paramString1.length() > i + 1) {
      localStringBuilder.append('&').append(paramString1.substring(i + 1));
    }
    return localStringBuilder.toString();
  }
  
  public static boolean isValidUrl(String paramString)
  {
    return isValidUrl(paramString, ALLOWED_SCHEMES);
  }
  
  public static boolean isValidUrl(String paramString, String[] paramArrayOfString)
  {
    if (StringUtil.isBlank(paramString)) {
      return false;
    }
    UrlValidator localUrlValidator = new UrlValidator(paramArrayOfString, 8L);
    return localUrlValidator.isValid(paramString);
  }
  
  public static boolean isValidUri(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    try
    {
      URI.create(paramString);
      return true;
    }
    catch (RuntimeException localRuntimeException) {}
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\text\UrlUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */