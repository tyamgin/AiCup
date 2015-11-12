package org.apache.commons.io;

import java.io.File;

public class FilenameUtils
{
  public static final String EXTENSION_SEPARATOR_STR = Character.toString('.');
  private static final char SYSTEM_SEPARATOR = File.separatorChar;
  private static final char OTHER_SEPARATOR;
  
  static boolean isSystemWindows()
  {
    return SYSTEM_SEPARATOR == '\\';
  }
  
  public static int indexOfLastSeparator(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    int i = paramString.lastIndexOf('/');
    int j = paramString.lastIndexOf('\\');
    return Math.max(i, j);
  }
  
  public static int indexOfExtension(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    int i = paramString.lastIndexOf('.');
    int j = indexOfLastSeparator(paramString);
    return j > i ? -1 : i;
  }
  
  public static String getName(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = indexOfLastSeparator(paramString);
    return paramString.substring(i + 1);
  }
  
  public static String getBaseName(String paramString)
  {
    return removeExtension(getName(paramString));
  }
  
  public static String getExtension(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = indexOfExtension(paramString);
    if (i == -1) {
      return "";
    }
    return paramString.substring(i + 1);
  }
  
  public static String removeExtension(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = indexOfExtension(paramString);
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(0, i);
  }
  
  static
  {
    if (isSystemWindows()) {
      OTHER_SEPARATOR = '/';
    } else {
      OTHER_SEPARATOR = '\\';
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\io\FilenameUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */