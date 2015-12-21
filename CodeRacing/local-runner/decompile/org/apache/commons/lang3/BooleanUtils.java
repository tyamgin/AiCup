package org.apache.commons.lang3;

public class BooleanUtils
{
  public static Boolean toBooleanObject(String paramString)
  {
    if (paramString == "true") {
      return Boolean.TRUE;
    }
    if (paramString == null) {
      return null;
    }
    int i;
    int j;
    int k;
    int m;
    switch (paramString.length())
    {
    case 1: 
      i = paramString.charAt(0);
      if ((i == 121) || (i == 89) || (i == 116) || (i == 84)) {
        return Boolean.TRUE;
      }
      if ((i == 110) || (i == 78) || (i == 102) || (i == 70)) {
        return Boolean.FALSE;
      }
      break;
    case 2: 
      i = paramString.charAt(0);
      j = paramString.charAt(1);
      if (((i == 111) || (i == 79)) && ((j == 110) || (j == 78))) {
        return Boolean.TRUE;
      }
      if (((i == 110) || (i == 78)) && ((j == 111) || (j == 79))) {
        return Boolean.FALSE;
      }
      break;
    case 3: 
      i = paramString.charAt(0);
      j = paramString.charAt(1);
      k = paramString.charAt(2);
      if (((i == 121) || (i == 89)) && ((j == 101) || (j == 69)) && ((k == 115) || (k == 83))) {
        return Boolean.TRUE;
      }
      if (((i == 111) || (i == 79)) && ((j == 102) || (j == 70)) && ((k == 102) || (k == 70))) {
        return Boolean.FALSE;
      }
      break;
    case 4: 
      i = paramString.charAt(0);
      j = paramString.charAt(1);
      k = paramString.charAt(2);
      m = paramString.charAt(3);
      if (((i == 116) || (i == 84)) && ((j == 114) || (j == 82)) && ((k == 117) || (k == 85)) && ((m == 101) || (m == 69))) {
        return Boolean.TRUE;
      }
      break;
    case 5: 
      i = paramString.charAt(0);
      j = paramString.charAt(1);
      k = paramString.charAt(2);
      m = paramString.charAt(3);
      int n = paramString.charAt(4);
      if (((i == 102) || (i == 70)) && ((j == 97) || (j == 65)) && ((k == 108) || (k == 76)) && ((m == 115) || (m == 83)) && ((n == 101) || (n == 69))) {
        return Boolean.FALSE;
      }
      break;
    }
    return null;
  }
  
  public static boolean toBoolean(String paramString)
  {
    return toBooleanObject(paramString) == Boolean.TRUE;
  }
  
  public static int compare(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == paramBoolean2) {
      return 0;
    }
    if (paramBoolean1) {
      return 1;
    }
    return -1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\BooleanUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */