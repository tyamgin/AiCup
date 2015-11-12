package com.google.common.base;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum CaseFormat
{
  LOWER_HYPHEN(CharMatcher.is('-'), "-"),  LOWER_UNDERSCORE(CharMatcher.is('_'), "_"),  LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), ""),  UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), ""),  UPPER_UNDERSCORE(CharMatcher.is('_'), "_");
  
  private final CharMatcher wordBoundary;
  private final String wordSeparator;
  
  private CaseFormat(CharMatcher paramCharMatcher, String paramString1)
  {
    this.wordBoundary = paramCharMatcher;
    this.wordSeparator = paramString1;
  }
  
  public final String to(CaseFormat paramCaseFormat, String paramString)
  {
    Preconditions.checkNotNull(paramCaseFormat);
    Preconditions.checkNotNull(paramString);
    return paramCaseFormat == this ? paramString : convert(paramCaseFormat, paramString);
  }
  
  String convert(CaseFormat paramCaseFormat, String paramString)
  {
    StringBuilder localStringBuilder = null;
    int i = 0;
    int j = -1;
    while ((j = this.wordBoundary.indexIn(paramString, ++j)) != -1)
    {
      if (i == 0)
      {
        localStringBuilder = new StringBuilder(paramString.length() + 4 * this.wordSeparator.length());
        localStringBuilder.append(paramCaseFormat.normalizeFirstWord(paramString.substring(i, j)));
      }
      else
      {
        localStringBuilder.append(paramCaseFormat.normalizeWord(paramString.substring(i, j)));
      }
      localStringBuilder.append(paramCaseFormat.wordSeparator);
      i = j + this.wordSeparator.length();
    }
    return paramCaseFormat.normalizeWord(paramString.substring(i));
  }
  
  abstract String normalizeWord(String paramString);
  
  private String normalizeFirstWord(String paramString)
  {
    return this == LOWER_CAMEL ? Ascii.toLowerCase(paramString) : normalizeWord(paramString);
  }
  
  private static String firstCharOnlyToUpper(String paramString)
  {
    return paramString.length() + Ascii.toUpperCase(paramString.charAt(0)) + Ascii.toLowerCase(paramString.substring(1));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\CaseFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */