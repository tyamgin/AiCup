package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InetAddressValidator
  implements Serializable
{
  private static final InetAddressValidator VALIDATOR = new InetAddressValidator();
  private final RegexValidator ipv4Validator = new RegexValidator("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
  
  public static InetAddressValidator getInstance()
  {
    return VALIDATOR;
  }
  
  public boolean isValid(String paramString)
  {
    return (isValidInet4Address(paramString)) || (isValidInet6Address(paramString));
  }
  
  public boolean isValidInet4Address(String paramString)
  {
    String[] arrayOfString = this.ipv4Validator.match(paramString);
    if (arrayOfString == null) {
      return false;
    }
    for (int i = 0; i <= 3; i++)
    {
      String str = arrayOfString[i];
      if ((str == null) || (str.length() == 0)) {
        return false;
      }
      int j = 0;
      try
      {
        j = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return false;
      }
      if (j > 255) {
        return false;
      }
      if ((str.length() > 1) && (str.startsWith("0"))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isValidInet6Address(String paramString)
  {
    int i = paramString.indexOf("::") > -1 ? 1 : 0;
    if ((i != 0) && (paramString.indexOf("::") != paramString.lastIndexOf("::"))) {
      return false;
    }
    if (((paramString.startsWith(":")) && (!paramString.startsWith("::"))) || ((paramString.endsWith(":")) && (!paramString.endsWith("::")))) {
      return false;
    }
    Object localObject = paramString.split(":");
    if (i != 0)
    {
      ArrayList localArrayList = new ArrayList(Arrays.asList((Object[])localObject));
      if (paramString.endsWith("::")) {
        localArrayList.add("");
      } else if ((paramString.startsWith("::")) && (!localArrayList.isEmpty())) {
        localArrayList.remove(0);
      }
      localObject = localArrayList.toArray();
    }
    if (localObject.length > 8) {
      return false;
    }
    int j = 0;
    int k = 0;
    for (int m = 0; m < localObject.length; m++)
    {
      String str = (String)localObject[m];
      if (str.length() == 0)
      {
        k++;
        if (k > 1) {
          return false;
        }
      }
      else
      {
        k = 0;
        if (str.indexOf(".") > -1)
        {
          if (!paramString.endsWith(str)) {
            return false;
          }
          if ((m > localObject.length - 1) || (m > 6)) {
            return false;
          }
          if (!isValidInet4Address(str)) {
            return false;
          }
          j += 2;
          continue;
        }
        if (str.length() > 4) {
          return false;
        }
        int n = 0;
        try
        {
          n = Integer.valueOf(str, 16).intValue();
        }
        catch (NumberFormatException localNumberFormatException)
        {
          return false;
        }
        if ((n < 0) || (n > 65535)) {
          return false;
        }
      }
      j++;
    }
    return (j >= 8) || (i != 0);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\validator\routines\InetAddressValidator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */