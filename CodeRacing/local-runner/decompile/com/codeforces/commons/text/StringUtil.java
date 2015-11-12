package com.codeforces.commons.text;

import com.codeforces.commons.holder.Holders;
import com.codeforces.commons.holder.Mutable;
import com.codeforces.commons.holder.SimpleMutable;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.pair.SimplePair;
import com.codeforces.commons.reflection.ReflectionUtil;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

public final class StringUtil
{
  private static final Pattern FORMAT_COMMENTS_COMMENT_SPLIT_PATTERN = Pattern.compile("\\[pre\\]|\\[/pre\\]");
  private static final Pattern FORMAT_COMMENTS_LINE_BREAK_REPLACE_PATTERN = Pattern.compile("[\n\r][\n\r]+");
  private static final Map toStringConverterByClass = new HashMap();
  private static final ReadWriteLock toStringConverterByClassMapLock = new ReentrantReadWriteLock();
  
  public static boolean isWhitespace(char paramChar)
  {
    return (Character.isWhitespace(paramChar)) || (paramChar == ' ') || (paramChar == '​');
  }
  
  public static boolean isEmpty(String paramString)
  {
    return (paramString == null) || (paramString.isEmpty());
  }
  
  public static boolean isBlank(String paramString)
  {
    if ((paramString == null) || (paramString.isEmpty())) {
      return true;
    }
    for (int i = paramString.length() - 1; i >= 0; i--) {
      if (!isWhitespace(paramString.charAt(i))) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isNotBlank(String paramString)
  {
    if ((paramString == null) || (paramString.isEmpty())) {
      return false;
    }
    for (int i = paramString.length() - 1; i >= 0; i--) {
      if (!isWhitespace(paramString.charAt(i))) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean equals(String paramString1, String paramString2)
  {
    return paramString1 == null ? false : paramString2 == null ? true : paramString1.equals(paramString2);
  }
  
  public static String trim(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length() - 1;
    int j = 0;
    int k = i;
    while ((j <= i) && (isWhitespace(paramString.charAt(j)))) {
      j++;
    }
    while ((k > j) && (isWhitespace(paramString.charAt(k)))) {
      k--;
    }
    return (j == 0) && (k == i) ? paramString : paramString.substring(j, k + 1);
  }
  
  public static String trimToNull(String paramString)
  {
    return (paramString = trim(paramString)).isEmpty() ? null : paramString == null ? null : paramString;
  }
  
  public static String trimToEmpty(String paramString)
  {
    return paramString == null ? "" : trim(paramString);
  }
  
  public static String[] split(String paramString, char paramChar)
  {
    int i = paramString.length();
    int j = 0;
    int k = 0;
    Object localObject = null;
    int m = 0;
    while (k < i) {
      if (paramString.charAt(k) == paramChar)
      {
        if (localObject == null)
        {
          localObject = new String[8];
        }
        else if (m == localObject.length)
        {
          arrayOfString = new String[m << 1];
          System.arraycopy(localObject, 0, arrayOfString, 0, m);
          localObject = arrayOfString;
        }
        localObject[(m++)] = paramString.substring(j, k);
        k++;
        j = k;
      }
      else
      {
        k++;
      }
    }
    if (localObject == null) {
      return new String[] { paramString };
    }
    if (m == localObject.length)
    {
      arrayOfString = new String[m + 1];
      System.arraycopy(localObject, 0, arrayOfString, 0, m);
      localObject = arrayOfString;
    }
    localObject[(m++)] = paramString.substring(j, k);
    if (m == localObject.length) {
      return (String[])localObject;
    }
    String[] arrayOfString = new String[m];
    System.arraycopy(localObject, 0, arrayOfString, 0, m);
    return arrayOfString;
  }
  
  public static String replace(String paramString1, String paramString2, String paramString3)
  {
    if ((isEmpty(paramString1)) || (isEmpty(paramString2)) || (paramString3 == null)) {
      return paramString1;
    }
    int i = paramString1.indexOf(paramString2);
    if (i == -1) {
      return paramString1;
    }
    int j = 0;
    int k = paramString2.length();
    StringBuilder localStringBuilder = new StringBuilder(paramString1.length() + (Math.max(paramString3.length() - k, 0) << 4));
    do
    {
      if (i > j) {
        localStringBuilder.append(paramString1.substring(j, i));
      }
      localStringBuilder.append(paramString3);
      j = i + k;
      i = paramString1.indexOf(paramString2, j);
    } while (i != -1);
    return paramString1.substring(j);
  }
  
  public static String toString(Class paramClass, Object paramObject, boolean paramBoolean, String... paramVarArgs)
  {
    ToStringOptions localToStringOptions = new ToStringOptions();
    localToStringOptions.skipNulls = paramBoolean;
    return toString(paramClass, paramObject, localToStringOptions, paramVarArgs);
  }
  
  public static String toString(Class paramClass, Object paramObject, ToStringOptions paramToStringOptions, String... paramVarArgs)
  {
    if (paramObject == null) {
      return getSimpleName(paramClass, paramToStringOptions.addEnclosingClassNames) + " {null}";
    }
    return toString(paramObject, paramToStringOptions, paramVarArgs);
  }
  
  public static String toString(Object paramObject, boolean paramBoolean, String... paramVarArgs)
  {
    ToStringOptions localToStringOptions = new ToStringOptions();
    localToStringOptions.skipNulls = paramBoolean;
    return toString(paramObject, localToStringOptions, paramVarArgs);
  }
  
  public static String toString(Object paramObject, ToStringOptions paramToStringOptions, String... paramVarArgs)
  {
    Class localClass = paramObject.getClass();
    if (paramVarArgs.length == 0)
    {
      localObject1 = ReflectionUtil.getFieldsByNameMap(localClass).keySet();
      paramVarArgs = (String[])((Set)localObject1).toArray(new String[((Set)localObject1).size()]);
    }
    Object localObject1 = new StringBuilder(getSimpleName(localClass, paramToStringOptions.addEnclosingClassNames)).append(" {");
    int i = 1;
    int j = 0;
    int k = paramVarArgs.length;
    while (j < k)
    {
      String str1 = paramVarArgs[j];
      if (isBlank(str1)) {
        throw new IllegalArgumentException("Field name can not be neither 'null' nor blank.");
      }
      Object localObject2 = ReflectionUtil.getDeepValue(paramObject, str1);
      String str2;
      if (localObject2 == null)
      {
        if ((paramToStringOptions.skipNulls) || (paramToStringOptions.skipEmptyStrings) || (paramToStringOptions.skipBlankStrings)) {
          break label212;
        }
        str2 = str1 + "=null";
      }
      else
      {
        str2 = fieldToString(localObject2, str1, paramToStringOptions);
        if (str2 == null) {
          break label212;
        }
      }
      if (i != 0) {
        i = 0;
      } else {
        ((StringBuilder)localObject1).append(", ");
      }
      ((StringBuilder)localObject1).append(str2);
      label212:
      j++;
    }
    return '}';
  }
  
  public static ToStringConverter getToStringConverter(Class paramClass, boolean paramBoolean)
  {
    Lock localLock = toStringConverterByClassMapLock.readLock();
    localLock.lock();
    try
    {
      if (paramBoolean)
      {
        for (localObject1 = paramClass; localObject1 != null; localObject1 = ((Class)localObject1).getSuperclass())
        {
          localToStringConverter1 = (ToStringConverter)toStringConverterByClass.get(localObject1);
          if (localToStringConverter1 != null)
          {
            ToStringConverter localToStringConverter2 = localToStringConverter1;
            return localToStringConverter2;
          }
        }
        ToStringConverter localToStringConverter1 = null;
        return localToStringConverter1;
      }
      Object localObject1 = (ToStringConverter)toStringConverterByClass.get(paramClass);
      return (ToStringConverter)localObject1;
    }
    finally
    {
      localLock.unlock();
    }
  }
  
  private static String getSimpleName(Class paramClass, boolean paramBoolean)
  {
    String str = paramClass.getSimpleName();
    if (paramBoolean) {
      while ((paramClass = paramClass.getEnclosingClass()) != null) {
        str = String.format("%s.%s", new Object[] { paramClass.getSimpleName(), str });
      }
    }
    return str;
  }
  
  private static String fieldToString(Object paramObject, String paramString, ToStringOptions paramToStringOptions)
  {
    if ((paramObject.getClass() == Boolean.class) || (paramObject.getClass() == Boolean.TYPE)) {
      return '!' + paramString;
    }
    SimpleMutable localSimpleMutable = new SimpleMutable();
    String str = valueToString(paramObject, localSimpleMutable);
    if (shouldSkipField(str, paramToStringOptions, localSimpleMutable)) {
      return null;
    }
    return paramString + '=' + str;
  }
  
  private static boolean shouldSkipField(String paramString, ToStringOptions paramToStringOptions, Mutable paramMutable)
  {
    if ((paramToStringOptions.skipNulls) && (paramString == null)) {
      return true;
    }
    if (paramToStringOptions.skipEmptyStrings) {
      if ((paramMutable != null) && (paramMutable.get() != null) && (((Boolean)paramMutable.get()).booleanValue()))
      {
        if (("''".equals(paramString)) || ("\"\"".equals(paramString))) {
          return true;
        }
      }
      else if (isEmpty(paramString)) {
        return true;
      }
    }
    if (paramToStringOptions.skipBlankStrings) {
      if ((paramMutable != null) && (paramMutable.get() != null) && (((Boolean)paramMutable.get()).booleanValue()))
      {
        if ((isBlank(paramString)) || (isBlank(paramString.substring(1, paramString.length() - 1)))) {
          return true;
        }
      }
      else if (isBlank(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  private static String valueToString(Object paramObject, Mutable paramMutable)
  {
    if (paramObject == null) {
      return null;
    }
    Class localClass = paramObject.getClass();
    if (localClass.isArray()) {
      return arrayToString(paramObject);
    }
    ToStringConverter localToStringConverter;
    if ((localToStringConverter = getToStringConverter(localClass, true)) != null) {
      return localToStringConverter.convert(paramObject);
    }
    if ((paramObject instanceof Collection)) {
      return collectionToString((Collection)paramObject);
    }
    if ((paramObject instanceof Map)) {
      return mapToString((Map)paramObject);
    }
    Object localObject;
    if ((paramObject instanceof Map.Entry))
    {
      localObject = (Map.Entry)paramObject;
      return valueToString(((Map.Entry)localObject).getKey(), null) + ": " + valueToString(((Map.Entry)localObject).getValue(), null);
    }
    if ((paramObject instanceof SimplePair))
    {
      localObject = (SimplePair)paramObject;
      return '(' + valueToString(((SimplePair)localObject).getFirst(), null) + ", " + valueToString(((SimplePair)localObject).getSecond(), null) + ')';
    }
    if (localClass == Character.class)
    {
      Holders.setQuietly(paramMutable, Boolean.valueOf(true));
      return "'" + paramObject + '\'';
    }
    if ((localClass == Boolean.class) || (localClass == Byte.class) || (localClass == Short.class) || (localClass == Integer.class) || (localClass == Long.class) || (localClass == Float.class) || (localClass == Double.class)) {
      return paramObject.toString();
    }
    if (localClass.isEnum()) {
      return ((Enum)paramObject).name();
    }
    if (localClass == String.class)
    {
      Holders.setQuietly(paramMutable, Boolean.valueOf(true));
      return '\'' + (String)paramObject + '\'';
    }
    Holders.setQuietly(paramMutable, Boolean.valueOf(true));
    return '\'' + String.valueOf(paramObject) + '\'';
  }
  
  private static String arrayToString(Object paramObject)
  {
    StringBuilder localStringBuilder = new StringBuilder("[");
    int i = Array.getLength(paramObject);
    if (i > 0)
    {
      localStringBuilder.append(valueToString(Array.get(paramObject, 0), null));
      for (int j = 1; j < i; j++) {
        localStringBuilder.append(", ").append(valueToString(Array.get(paramObject, j), null));
      }
    }
    return ']';
  }
  
  private static String collectionToString(Collection paramCollection)
  {
    StringBuilder localStringBuilder = new StringBuilder("[");
    Iterator localIterator = paramCollection.iterator();
    if (localIterator.hasNext())
    {
      localStringBuilder.append(valueToString(localIterator.next(), null));
      while (localIterator.hasNext()) {
        localStringBuilder.append(", ").append(valueToString(localIterator.next(), null));
      }
    }
    return ']';
  }
  
  private static String mapToString(Map paramMap)
  {
    StringBuilder localStringBuilder = new StringBuilder("{");
    Iterator localIterator = paramMap.entrySet().iterator();
    if (localIterator.hasNext())
    {
      localStringBuilder.append(valueToString(localIterator.next(), null));
      while (localIterator.hasNext()) {
        localStringBuilder.append(", ").append(valueToString(localIterator.next(), null));
      }
    }
    return '}';
  }
  
  public static String shrinkTo(String paramString, int paramInt)
  {
    if (paramInt < 8) {
      throw new IllegalArgumentException("Argument maxLength is expected to be at least 8.");
    }
    if ((paramString == null) || (paramString.length() <= paramInt)) {
      return paramString;
    }
    int i = paramInt / 2;
    int j = paramInt - i - 3;
    return paramString.substring(0, i) + "..." + paramString.substring(paramString.length() - j);
  }
  
  public static List shrinkLinesTo(List paramList, int paramInt1, int paramInt2)
  {
    if (paramInt2 < 3) {
      throw new IllegalArgumentException("Argument 'maxLineCount' is expected to be at least 3.");
    }
    if (paramList == null) {
      return null;
    }
    int i = paramList.size();
    ArrayList localArrayList = new ArrayList(Math.min(paramInt2, i));
    if (i <= paramInt2)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localArrayList.add(shrinkTo(str, paramInt1));
      }
    }
    else
    {
      int j = paramInt2 / 2;
      int k = paramInt2 - j - 1;
      for (int m = 0; m < j; m++) {
        localArrayList.add(shrinkTo((String)paramList.get(m), paramInt1));
      }
      localArrayList.add("...");
      for (m = i - k; m < i; m++) {
        localArrayList.add(shrinkTo((String)paramList.get(m), paramInt1));
      }
    }
    return localArrayList;
  }
  
  public static final class ToStringOptions
  {
    private boolean skipNulls;
    private boolean skipEmptyStrings;
    private boolean skipBlankStrings;
    private boolean addEnclosingClassNames;
  }
  
  public static abstract interface ToStringConverter
  {
    public abstract String convert(Object paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\text\StringUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */