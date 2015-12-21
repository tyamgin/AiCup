package org.apache.commons.lang3;

import java.util.Iterator;

public class StringUtils
{
  public static boolean isEmpty(CharSequence paramCharSequence)
  {
    return (paramCharSequence == null) || (paramCharSequence.length() == 0);
  }
  
  public static boolean isBlank(CharSequence paramCharSequence)
  {
    int i;
    if ((paramCharSequence == null) || ((i = paramCharSequence.length()) == 0)) {
      return true;
    }
    for (int j = 0; j < i; j++) {
      if (!Character.isWhitespace(paramCharSequence.charAt(j))) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isNotBlank(CharSequence paramCharSequence)
  {
    return !isBlank(paramCharSequence);
  }
  
  public static boolean containsAny(CharSequence paramCharSequence, char... paramVarArgs)
  {
    if ((isEmpty(paramCharSequence)) || (ArrayUtils.isEmpty(paramVarArgs))) {
      return false;
    }
    int i = paramCharSequence.length();
    int j = paramVarArgs.length;
    int k = i - 1;
    int m = j - 1;
    for (int n = 0; n < i; n++)
    {
      char c = paramCharSequence.charAt(n);
      for (int i1 = 0; i1 < j; i1++) {
        if (paramVarArgs[i1] == c) {
          if (Character.isHighSurrogate(c))
          {
            if (i1 == m) {
              return true;
            }
            if ((n < k) && (paramVarArgs[(i1 + 1)] == paramCharSequence.charAt(n + 1))) {
              return true;
            }
          }
          else
          {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public static boolean containsNone(CharSequence paramCharSequence, char... paramVarArgs)
  {
    if ((paramCharSequence == null) || (paramVarArgs == null)) {
      return true;
    }
    int i = paramCharSequence.length();
    int j = i - 1;
    int k = paramVarArgs.length;
    int m = k - 1;
    for (int n = 0; n < i; n++)
    {
      char c = paramCharSequence.charAt(n);
      for (int i1 = 0; i1 < k; i1++) {
        if (paramVarArgs[i1] == c) {
          if (Character.isHighSurrogate(c))
          {
            if (i1 == m) {
              return false;
            }
            if ((n < j) && (paramVarArgs[(i1 + 1)] == paramCharSequence.charAt(n + 1))) {
              return false;
            }
          }
          else
          {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  public static String join(Iterator paramIterator, String paramString)
  {
    if (paramIterator == null) {
      return null;
    }
    if (!paramIterator.hasNext()) {
      return "";
    }
    Object localObject1 = paramIterator.next();
    if (!paramIterator.hasNext())
    {
      localObject2 = ObjectUtils.toString(localObject1);
      return (String)localObject2;
    }
    Object localObject2 = new StringBuilder(256);
    if (localObject1 != null) {
      ((StringBuilder)localObject2).append(localObject1);
    }
    while (paramIterator.hasNext())
    {
      if (paramString != null) {
        ((StringBuilder)localObject2).append(paramString);
      }
      Object localObject3 = paramIterator.next();
      if (localObject3 != null) {
        ((StringBuilder)localObject2).append(localObject3);
      }
    }
    return ((StringBuilder)localObject2).toString();
  }
  
  public static String join(Iterable paramIterable, String paramString)
  {
    if (paramIterable == null) {
      return null;
    }
    return join(paramIterable.iterator(), paramString);
  }
  
  public static String replace(String paramString1, String paramString2, String paramString3)
  {
    return replace(paramString1, paramString2, paramString3, -1);
  }
  
  public static String replace(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if ((isEmpty(paramString1)) || (isEmpty(paramString2)) || (paramString3 == null) || (paramInt == 0)) {
      return paramString1;
    }
    int i = 0;
    int j = paramString1.indexOf(paramString2, i);
    if (j == -1) {
      return paramString1;
    }
    int k = paramString2.length();
    int m = paramString3.length() - k;
    m = m < 0 ? 0 : m;
    m *= (paramInt > 64 ? 64 : paramInt < 0 ? 16 : paramInt);
    StringBuilder localStringBuilder = new StringBuilder(paramString1.length() + m);
    while (j != -1)
    {
      localStringBuilder.append(paramString1.substring(i, j)).append(paramString3);
      i = j + k;
      paramInt--;
      if (paramInt == 0) {
        break;
      }
      j = paramString1.indexOf(paramString2, i);
    }
    localStringBuilder.append(paramString1.substring(i));
    return localStringBuilder.toString();
  }
  
  public static String repeat(String paramString, int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    if (paramInt <= 0) {
      return "";
    }
    int i = paramString.length();
    if ((paramInt == 1) || (i == 0)) {
      return paramString;
    }
    if ((i == 1) && (paramInt <= 8192)) {
      return repeat(paramString.charAt(0), paramInt);
    }
    int j = i * paramInt;
    switch (i)
    {
    case 1: 
      return repeat(paramString.charAt(0), paramInt);
    case 2: 
      int k = paramString.charAt(0);
      int m = paramString.charAt(1);
      char[] arrayOfChar = new char[j];
      for (int n = paramInt * 2 - 2; n >= 0; n--)
      {
        arrayOfChar[n] = k;
        arrayOfChar[(n + 1)] = m;
        n--;
      }
      return new String(arrayOfChar);
    }
    StringBuilder localStringBuilder = new StringBuilder(j);
    for (int i1 = 0; i1 < paramInt; i1++) {
      localStringBuilder.append(paramString);
    }
    return localStringBuilder.toString();
  }
  
  public static String repeat(char paramChar, int paramInt)
  {
    char[] arrayOfChar = new char[paramInt];
    for (int i = paramInt - 1; i >= 0; i--) {
      arrayOfChar[i] = paramChar;
    }
    return new String(arrayOfChar);
  }
  
  public static String capitalize(String paramString)
  {
    int i;
    if ((paramString == null) || ((i = paramString.length()) == 0)) {
      return paramString;
    }
    char c = paramString.charAt(0);
    if (Character.isTitleCase(c)) {
      return paramString;
    }
    return i + Character.toTitleCase(c) + paramString.substring(1);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\StringUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */