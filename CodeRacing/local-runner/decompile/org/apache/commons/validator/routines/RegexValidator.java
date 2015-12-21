package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator
  implements Serializable
{
  private final Pattern[] patterns;
  
  public RegexValidator(String paramString)
  {
    this(paramString, true);
  }
  
  public RegexValidator(String paramString, boolean paramBoolean)
  {
    this(new String[] { paramString }, paramBoolean);
  }
  
  public RegexValidator(String[] paramArrayOfString, boolean paramBoolean)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      throw new IllegalArgumentException("Regular expressions are missing");
    }
    this.patterns = new Pattern[paramArrayOfString.length];
    int i = paramBoolean ? 0 : 2;
    for (int j = 0; j < paramArrayOfString.length; j++)
    {
      if ((paramArrayOfString[j] == null) || (paramArrayOfString[j].length() == 0)) {
        throw new IllegalArgumentException("Regular expression[" + j + "] is missing");
      }
      this.patterns[j] = Pattern.compile(paramArrayOfString[j], i);
    }
  }
  
  public boolean isValid(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    for (int i = 0; i < this.patterns.length; i++) {
      if (this.patterns[i].matcher(paramString).matches()) {
        return true;
      }
    }
    return false;
  }
  
  public String[] match(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    for (int i = 0; i < this.patterns.length; i++)
    {
      Matcher localMatcher = this.patterns[i].matcher(paramString);
      if (localMatcher.matches())
      {
        int j = localMatcher.groupCount();
        String[] arrayOfString = new String[j];
        for (int k = 0; k < j; k++) {
          arrayOfString[k] = localMatcher.group(k + 1);
        }
        return arrayOfString;
      }
    }
    return null;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("RegexValidator{");
    for (int i = 0; i < this.patterns.length; i++)
    {
      if (i > 0) {
        localStringBuffer.append(",");
      }
      localStringBuffer.append(this.patterns[i].pattern());
    }
    localStringBuffer.append("}");
    return localStringBuffer.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\validator\routines\RegexValidator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */