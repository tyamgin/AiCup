package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlValidator
  implements Serializable
{
  private static final Pattern URL_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
  private static final Pattern SCHEME_PATTERN = Pattern.compile("^\\p{Alpha}[\\p{Alnum}\\+\\-\\.]*");
  private static final Pattern AUTHORITY_PATTERN = Pattern.compile("^([\\p{Alnum}\\-\\.]*)(:\\d*)?(.*)?");
  private static final Pattern PATH_PATTERN = Pattern.compile("^(/[-\\w:@&?=+,.!/~*'%$_;\\(\\)]*)?$");
  private static final Pattern QUERY_PATTERN = Pattern.compile("^(.*)$");
  private static final Pattern PORT_PATTERN = Pattern.compile("^:(\\d{1,5})$");
  private final long options;
  private final Set allowedSchemes;
  private final RegexValidator authorityValidator;
  private static final String[] DEFAULT_SCHEMES = { "http", "https", "ftp" };
  private static final UrlValidator DEFAULT_URL_VALIDATOR = new UrlValidator();
  
  public UrlValidator()
  {
    this(null);
  }
  
  public UrlValidator(String[] paramArrayOfString)
  {
    this(paramArrayOfString, 0L);
  }
  
  public UrlValidator(String[] paramArrayOfString, long paramLong)
  {
    this(paramArrayOfString, null, paramLong);
  }
  
  public UrlValidator(String[] paramArrayOfString, RegexValidator paramRegexValidator, long paramLong)
  {
    this.options = paramLong;
    if (isOn(1L))
    {
      this.allowedSchemes = Collections.EMPTY_SET;
    }
    else
    {
      if (paramArrayOfString == null) {
        paramArrayOfString = DEFAULT_SCHEMES;
      }
      this.allowedSchemes = new HashSet(paramArrayOfString.length);
      for (int i = 0; i < paramArrayOfString.length; i++) {
        this.allowedSchemes.add(paramArrayOfString[i].toLowerCase(Locale.ENGLISH));
      }
    }
    this.authorityValidator = paramRegexValidator;
  }
  
  public boolean isValid(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    Matcher localMatcher = URL_PATTERN.matcher(paramString);
    if (!localMatcher.matches()) {
      return false;
    }
    String str1 = localMatcher.group(2);
    if (!isValidScheme(str1)) {
      return false;
    }
    String str2 = localMatcher.group(4);
    if (((!"file".equals(str1)) || (!"".equals(str2))) && (!isValidAuthority(str2))) {
      return false;
    }
    if (!isValidPath(localMatcher.group(5))) {
      return false;
    }
    if (!isValidQuery(localMatcher.group(7))) {
      return false;
    }
    return isValidFragment(localMatcher.group(9));
  }
  
  protected boolean isValidScheme(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if (!SCHEME_PATTERN.matcher(paramString).matches()) {
      return false;
    }
    return (!isOff(1L)) || (this.allowedSchemes.contains(paramString.toLowerCase(Locale.ENGLISH)));
  }
  
  protected boolean isValidAuthority(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if ((this.authorityValidator != null) && (this.authorityValidator.isValid(paramString))) {
      return true;
    }
    String str1 = DomainValidator.unicodeToASCII(paramString);
    Matcher localMatcher = AUTHORITY_PATTERN.matcher(str1);
    if (!localMatcher.matches()) {
      return false;
    }
    String str2 = localMatcher.group(1);
    DomainValidator localDomainValidator = DomainValidator.getInstance(isOn(8L));
    if (!localDomainValidator.isValid(str2))
    {
      localObject = InetAddressValidator.getInstance();
      if (!((InetAddressValidator)localObject).isValid(str2)) {
        return false;
      }
    }
    Object localObject = localMatcher.group(2);
    if ((localObject != null) && (!PORT_PATTERN.matcher((CharSequence)localObject).matches())) {
      return false;
    }
    String str3 = localMatcher.group(3);
    return (str3 == null) || (str3.trim().length() <= 0);
  }
  
  protected boolean isValidPath(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if (!PATH_PATTERN.matcher(paramString).matches()) {
      return false;
    }
    int i = countToken("//", paramString);
    if ((isOff(2L)) && (i > 0)) {
      return false;
    }
    int j = countToken("/", paramString);
    int k = countToken("..", paramString);
    return (k <= 0) || (j - i - 1 > k);
  }
  
  protected boolean isValidQuery(String paramString)
  {
    if (paramString == null) {
      return true;
    }
    return QUERY_PATTERN.matcher(paramString).matches();
  }
  
  protected boolean isValidFragment(String paramString)
  {
    if (paramString == null) {
      return true;
    }
    return isOff(4L);
  }
  
  protected int countToken(String paramString1, String paramString2)
  {
    int i = 0;
    int j = 0;
    while (i != -1)
    {
      i = paramString2.indexOf(paramString1, i);
      if (i > -1)
      {
        i++;
        j++;
      }
    }
    return j;
  }
  
  private boolean isOn(long paramLong)
  {
    return (this.options & paramLong) > 0L;
  }
  
  private boolean isOff(long paramLong)
  {
    return (this.options & paramLong) == 0L;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\validator\routines\UrlValidator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */