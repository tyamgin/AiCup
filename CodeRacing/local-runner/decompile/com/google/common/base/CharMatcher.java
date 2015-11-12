package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Arrays;
import java.util.BitSet;

@Beta
@GwtCompatible(emulated=true)
public abstract class CharMatcher
  implements Predicate
{
  public static final CharMatcher BREAKING_WHITESPACE = new CharMatcher()
  {
    public boolean matches(char paramAnonymousChar)
    {
      switch (paramAnonymousChar)
      {
      case '\t': 
      case '\n': 
      case '\013': 
      case '\f': 
      case '\r': 
      case ' ': 
      case '': 
      case ' ': 
      case ' ': 
      case ' ': 
      case ' ': 
      case '　': 
        return true;
      case ' ': 
        return false;
      }
      return (paramAnonymousChar >= ' ') && (paramAnonymousChar <= ' ');
    }
    
    public String toString()
    {
      return "CharMatcher.BREAKING_WHITESPACE";
    }
  };
  public static final CharMatcher ASCII = inRange('\000', '', "CharMatcher.ASCII");
  private static final String ZEROES = "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０";
  private static final String NINES;
  public static final CharMatcher DIGIT = new RangesMatcher("CharMatcher.DIGIT", "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".toCharArray(), NINES.toCharArray());
  public static final CharMatcher JAVA_DIGIT = new CharMatcher("CharMatcher.JAVA_DIGIT")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return Character.isDigit(paramAnonymousChar);
    }
  };
  public static final CharMatcher JAVA_LETTER = new CharMatcher("CharMatcher.JAVA_LETTER")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return Character.isLetter(paramAnonymousChar);
    }
  };
  public static final CharMatcher JAVA_LETTER_OR_DIGIT = new CharMatcher("CharMatcher.JAVA_LETTER_OR_DIGIT")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return Character.isLetterOrDigit(paramAnonymousChar);
    }
  };
  public static final CharMatcher JAVA_UPPER_CASE = new CharMatcher("CharMatcher.JAVA_UPPER_CASE")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return Character.isUpperCase(paramAnonymousChar);
    }
  };
  public static final CharMatcher JAVA_LOWER_CASE = new CharMatcher("CharMatcher.JAVA_LOWER_CASE")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return Character.isLowerCase(paramAnonymousChar);
    }
  };
  public static final CharMatcher JAVA_ISO_CONTROL = inRange('\000', '\037').or(inRange('', '')).withToString("CharMatcher.JAVA_ISO_CONTROL");
  public static final CharMatcher INVISIBLE = new RangesMatcher("CharMatcher.INVISIBLE", "\000­؀۝܏ ᠎   ⁪　?﻿￹￺".toCharArray(), "  ­؄۝܏ ᠎‏ ⁤⁯　﻿￹￻".toCharArray());
  public static final CharMatcher SINGLE_WIDTH = new RangesMatcher("CharMatcher.SINGLE_WIDTH", "\000־א׳؀ݐ฀Ḁ℀ﭐﹰ｡".toCharArray(), "ӹ־ת״ۿݿ๿₯℺﷿﻿ￜ".toCharArray());
  public static final CharMatcher ANY = new FastMatcher("CharMatcher.ANY")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return true;
    }
    
    public int indexIn(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.length() == 0 ? -1 : 0;
    }
    
    public int indexIn(CharSequence paramAnonymousCharSequence, int paramAnonymousInt)
    {
      int i = paramAnonymousCharSequence.length();
      Preconditions.checkPositionIndex(paramAnonymousInt, i);
      return paramAnonymousInt == i ? -1 : paramAnonymousInt;
    }
    
    public int lastIndexIn(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.length() - 1;
    }
    
    public boolean matchesAllOf(CharSequence paramAnonymousCharSequence)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence);
      return true;
    }
    
    public boolean matchesNoneOf(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.length() == 0;
    }
    
    public String removeFrom(CharSequence paramAnonymousCharSequence)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence);
      return "";
    }
    
    public String replaceFrom(CharSequence paramAnonymousCharSequence, char paramAnonymousChar)
    {
      char[] arrayOfChar = new char[paramAnonymousCharSequence.length()];
      Arrays.fill(arrayOfChar, paramAnonymousChar);
      return new String(arrayOfChar);
    }
    
    public String replaceFrom(CharSequence paramAnonymousCharSequence1, CharSequence paramAnonymousCharSequence2)
    {
      StringBuilder localStringBuilder = new StringBuilder(paramAnonymousCharSequence1.length() * paramAnonymousCharSequence2.length());
      for (int i = 0; i < paramAnonymousCharSequence1.length(); i++) {
        localStringBuilder.append(paramAnonymousCharSequence2);
      }
      return localStringBuilder.toString();
    }
    
    public String collapseFrom(CharSequence paramAnonymousCharSequence, char paramAnonymousChar)
    {
      return paramAnonymousCharSequence.length() == 0 ? "" : String.valueOf(paramAnonymousChar);
    }
    
    public String trimFrom(CharSequence paramAnonymousCharSequence)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence);
      return "";
    }
    
    public int countIn(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.length();
    }
    
    public CharMatcher and(CharMatcher paramAnonymousCharMatcher)
    {
      return (CharMatcher)Preconditions.checkNotNull(paramAnonymousCharMatcher);
    }
    
    public CharMatcher or(CharMatcher paramAnonymousCharMatcher)
    {
      Preconditions.checkNotNull(paramAnonymousCharMatcher);
      return this;
    }
    
    public CharMatcher negate()
    {
      return NONE;
    }
  };
  public static final CharMatcher NONE = new FastMatcher("CharMatcher.NONE")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return false;
    }
    
    public int indexIn(CharSequence paramAnonymousCharSequence)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence);
      return -1;
    }
    
    public int indexIn(CharSequence paramAnonymousCharSequence, int paramAnonymousInt)
    {
      int i = paramAnonymousCharSequence.length();
      Preconditions.checkPositionIndex(paramAnonymousInt, i);
      return -1;
    }
    
    public int lastIndexIn(CharSequence paramAnonymousCharSequence)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence);
      return -1;
    }
    
    public boolean matchesAllOf(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.length() == 0;
    }
    
    public boolean matchesNoneOf(CharSequence paramAnonymousCharSequence)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence);
      return true;
    }
    
    public String removeFrom(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.toString();
    }
    
    public String replaceFrom(CharSequence paramAnonymousCharSequence, char paramAnonymousChar)
    {
      return paramAnonymousCharSequence.toString();
    }
    
    public String replaceFrom(CharSequence paramAnonymousCharSequence1, CharSequence paramAnonymousCharSequence2)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence2);
      return paramAnonymousCharSequence1.toString();
    }
    
    public String collapseFrom(CharSequence paramAnonymousCharSequence, char paramAnonymousChar)
    {
      return paramAnonymousCharSequence.toString();
    }
    
    public String trimFrom(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.toString();
    }
    
    public String trimLeadingFrom(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.toString();
    }
    
    public String trimTrailingFrom(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.toString();
    }
    
    public int countIn(CharSequence paramAnonymousCharSequence)
    {
      Preconditions.checkNotNull(paramAnonymousCharSequence);
      return 0;
    }
    
    public CharMatcher and(CharMatcher paramAnonymousCharMatcher)
    {
      Preconditions.checkNotNull(paramAnonymousCharMatcher);
      return this;
    }
    
    public CharMatcher or(CharMatcher paramAnonymousCharMatcher)
    {
      return (CharMatcher)Preconditions.checkNotNull(paramAnonymousCharMatcher);
    }
    
    public CharMatcher negate()
    {
      return ANY;
    }
  };
  final String description;
  private static final int DISTINCT_CHARS = 65536;
  private static final String WHITESPACE_TABLE = "\001\000 \000\000\000\000\000\000\t\n\013\f\r\000\000  \000\000\000\000\000 \000\000\000\000\000\000\000\000 \000\000\000\000\000\000\000\000\000\000　\000\000\000\000\000\000\000\000\000\000           \000\000\000\000\000  \000\000᠎\000\000\000";
  public static final CharMatcher WHITESPACE = new FastMatcher("CharMatcher.WHITESPACE")
  {
    public boolean matches(char paramAnonymousChar)
    {
      return "\001\000 \000\000\000\000\000\000\t\n\013\f\r\000\000  \000\000\000\000\000 \000\000\000\000\000\000\000\000 \000\000\000\000\000\000\000\000\000\000　\000\000\000\000\000\000\000\000\000\000           \000\000\000\000\000  \000\000᠎\000\000\000".charAt(paramAnonymousChar % 'O') == paramAnonymousChar;
    }
  };
  
  private static String showCharacter(char paramChar)
  {
    String str = "0123456789ABCDEF";
    char[] arrayOfChar = { '\\', 'u', '\000', '\000', '\000', '\000' };
    for (int i = 0; i < 4; i++)
    {
      arrayOfChar[(5 - i)] = str.charAt(paramChar & 0xF);
      paramChar = (char)(paramChar >> '\004');
    }
    return String.copyValueOf(arrayOfChar);
  }
  
  public static CharMatcher is(final char paramChar)
  {
    String str = "CharMatcher.is('" + showCharacter(paramChar) + "')";
    new FastMatcher(str)
    {
      public boolean matches(char paramAnonymousChar)
      {
        return paramAnonymousChar == paramChar;
      }
      
      public String replaceFrom(CharSequence paramAnonymousCharSequence, char paramAnonymousChar)
      {
        return paramAnonymousCharSequence.toString().replace(paramChar, paramAnonymousChar);
      }
      
      public CharMatcher and(CharMatcher paramAnonymousCharMatcher)
      {
        return paramAnonymousCharMatcher.matches(paramChar) ? this : NONE;
      }
      
      public CharMatcher or(CharMatcher paramAnonymousCharMatcher)
      {
        return paramAnonymousCharMatcher.matches(paramChar) ? paramAnonymousCharMatcher : super.or(paramAnonymousCharMatcher);
      }
      
      public CharMatcher negate()
      {
        return isNot(paramChar);
      }
      
      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet paramAnonymousBitSet)
      {
        paramAnonymousBitSet.set(paramChar);
      }
    };
  }
  
  public static CharMatcher isNot(final char paramChar)
  {
    String str = "CharMatcher.isNot(" + Integer.toHexString(paramChar) + ")";
    new FastMatcher(str)
    {
      public boolean matches(char paramAnonymousChar)
      {
        return paramAnonymousChar != paramChar;
      }
      
      public CharMatcher and(CharMatcher paramAnonymousCharMatcher)
      {
        return paramAnonymousCharMatcher.matches(paramChar) ? super.and(paramAnonymousCharMatcher) : paramAnonymousCharMatcher;
      }
      
      public CharMatcher or(CharMatcher paramAnonymousCharMatcher)
      {
        return paramAnonymousCharMatcher.matches(paramChar) ? ANY : this;
      }
      
      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet paramAnonymousBitSet)
      {
        paramAnonymousBitSet.set(0, paramChar);
        paramAnonymousBitSet.set(paramChar + '\001', 65536);
      }
      
      public CharMatcher negate()
      {
        return is(paramChar);
      }
    };
  }
  
  public static CharMatcher anyOf(CharSequence paramCharSequence)
  {
    switch (paramCharSequence.length())
    {
    case 0: 
      return NONE;
    case 1: 
      return is(paramCharSequence.charAt(0));
    case 2: 
      return isEither(paramCharSequence.charAt(0), paramCharSequence.charAt(1));
    }
    final char[] arrayOfChar1 = paramCharSequence.toString().toCharArray();
    Arrays.sort(arrayOfChar1);
    StringBuilder localStringBuilder = new StringBuilder("CharMatcher.anyOf(\"");
    for (char c : arrayOfChar1) {
      localStringBuilder.append(showCharacter(c));
    }
    localStringBuilder.append("\")");
    new CharMatcher(localStringBuilder.toString())
    {
      public boolean matches(char paramAnonymousChar)
      {
        return Arrays.binarySearch(arrayOfChar1, paramAnonymousChar) >= 0;
      }
      
      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet paramAnonymousBitSet)
      {
        for (int k : arrayOfChar1) {
          paramAnonymousBitSet.set(k);
        }
      }
    };
  }
  
  private static CharMatcher isEither(final char paramChar1, final char paramChar2)
  {
    String str = "CharMatcher.anyOf(\"" + showCharacter(paramChar1) + showCharacter(paramChar2) + "\")";
    new FastMatcher(str)
    {
      public boolean matches(char paramAnonymousChar)
      {
        return (paramAnonymousChar == paramChar1) || (paramAnonymousChar == paramChar2);
      }
      
      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet paramAnonymousBitSet)
      {
        paramAnonymousBitSet.set(paramChar1);
        paramAnonymousBitSet.set(paramChar2);
      }
    };
  }
  
  public static CharMatcher noneOf(CharSequence paramCharSequence)
  {
    return anyOf(paramCharSequence).negate();
  }
  
  public static CharMatcher inRange(char paramChar1, char paramChar2)
  {
    Preconditions.checkArgument(paramChar2 >= paramChar1);
    String str = "CharMatcher.inRange('" + showCharacter(paramChar1) + "', '" + showCharacter(paramChar2) + "')";
    return inRange(paramChar1, paramChar2, str);
  }
  
  static CharMatcher inRange(final char paramChar1, final char paramChar2, String paramString)
  {
    new FastMatcher(paramString)
    {
      public boolean matches(char paramAnonymousChar)
      {
        return (paramChar1 <= paramAnonymousChar) && (paramAnonymousChar <= paramChar2);
      }
      
      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet paramAnonymousBitSet)
      {
        paramAnonymousBitSet.set(paramChar1, paramChar2 + '\001');
      }
    };
  }
  
  public static CharMatcher forPredicate(final Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    if ((paramPredicate instanceof CharMatcher)) {
      return (CharMatcher)paramPredicate;
    }
    String str = "CharMatcher.forPredicate(" + paramPredicate + ")";
    new CharMatcher(str)
    {
      public boolean matches(char paramAnonymousChar)
      {
        return paramPredicate.apply(Character.valueOf(paramAnonymousChar));
      }
      
      public boolean apply(Character paramAnonymousCharacter)
      {
        return paramPredicate.apply(Preconditions.checkNotNull(paramAnonymousCharacter));
      }
    };
  }
  
  CharMatcher(String paramString)
  {
    this.description = paramString;
  }
  
  protected CharMatcher()
  {
    this.description = super.toString();
  }
  
  public abstract boolean matches(char paramChar);
  
  public CharMatcher negate()
  {
    return new NegatedMatcher(this);
  }
  
  public CharMatcher and(CharMatcher paramCharMatcher)
  {
    return new And(this, (CharMatcher)Preconditions.checkNotNull(paramCharMatcher));
  }
  
  public CharMatcher or(CharMatcher paramCharMatcher)
  {
    return new Or(this, (CharMatcher)Preconditions.checkNotNull(paramCharMatcher));
  }
  
  public CharMatcher precomputed()
  {
    return Platform.precomputeCharMatcher(this);
  }
  
  CharMatcher withToString(String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  @GwtIncompatible("java.util.BitSet")
  CharMatcher precomputedInternal()
  {
    BitSet localBitSet = new BitSet();
    setBits(localBitSet);
    int i = localBitSet.cardinality();
    if (i * 2 <= 65536) {
      return precomputedPositive(i, localBitSet, this.description);
    }
    localBitSet.flip(0, 65536);
    int j = 65536 - i;
    return new NegatedFastMatcher(toString(), precomputedPositive(j, localBitSet, this.description + ".negate()"));
  }
  
  @GwtIncompatible("java.util.BitSet")
  private static CharMatcher precomputedPositive(int paramInt, BitSet paramBitSet, String paramString)
  {
    switch (paramInt)
    {
    case 0: 
      return NONE;
    case 1: 
      return is((char)paramBitSet.nextSetBit(0));
    case 2: 
      char c1 = (char)paramBitSet.nextSetBit(0);
      char c2 = (char)paramBitSet.nextSetBit(c1 + '\001');
      return isEither(c1, c2);
    }
    return isSmall(paramInt, paramBitSet.length()) ? SmallCharMatcher.from(paramBitSet, paramString) : new BitSetMatcher(paramBitSet, paramString, null);
  }
  
  private static boolean isSmall(int paramInt1, int paramInt2)
  {
    return (paramInt1 <= 1023) && (paramInt2 > paramInt1 * 16);
  }
  
  @GwtIncompatible("java.util.BitSet")
  void setBits(BitSet paramBitSet)
  {
    for (int i = 65535; i >= 0; i--) {
      if (matches((char)i)) {
        paramBitSet.set(i);
      }
    }
  }
  
  public boolean matchesAnyOf(CharSequence paramCharSequence)
  {
    return !matchesNoneOf(paramCharSequence);
  }
  
  public boolean matchesAllOf(CharSequence paramCharSequence)
  {
    for (int i = paramCharSequence.length() - 1; i >= 0; i--) {
      if (!matches(paramCharSequence.charAt(i))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean matchesNoneOf(CharSequence paramCharSequence)
  {
    return indexIn(paramCharSequence) == -1;
  }
  
  public int indexIn(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if (matches(paramCharSequence.charAt(j))) {
        return j;
      }
    }
    return -1;
  }
  
  public int indexIn(CharSequence paramCharSequence, int paramInt)
  {
    int i = paramCharSequence.length();
    Preconditions.checkPositionIndex(paramInt, i);
    for (int j = paramInt; j < i; j++) {
      if (matches(paramCharSequence.charAt(j))) {
        return j;
      }
    }
    return -1;
  }
  
  public int lastIndexIn(CharSequence paramCharSequence)
  {
    for (int i = paramCharSequence.length() - 1; i >= 0; i--) {
      if (matches(paramCharSequence.charAt(i))) {
        return i;
      }
    }
    return -1;
  }
  
  public int countIn(CharSequence paramCharSequence)
  {
    int i = 0;
    for (int j = 0; j < paramCharSequence.length(); j++) {
      if (matches(paramCharSequence.charAt(j))) {
        i++;
      }
    }
    return i;
  }
  
  public String removeFrom(CharSequence paramCharSequence)
  {
    String str = paramCharSequence.toString();
    int i = indexIn(str);
    if (i == -1) {
      return str;
    }
    char[] arrayOfChar = str.toCharArray();
    for (int j = 1;; j++)
    {
      i++;
      for (;;)
      {
        if (i == arrayOfChar.length) {
          break label79;
        }
        if (matches(arrayOfChar[i])) {
          break;
        }
        arrayOfChar[(i - j)] = arrayOfChar[i];
        i++;
      }
    }
    label79:
    return new String(arrayOfChar, 0, i - j);
  }
  
  public String retainFrom(CharSequence paramCharSequence)
  {
    return negate().removeFrom(paramCharSequence);
  }
  
  public String replaceFrom(CharSequence paramCharSequence, char paramChar)
  {
    String str = paramCharSequence.toString();
    int i = indexIn(str);
    if (i == -1) {
      return str;
    }
    char[] arrayOfChar = str.toCharArray();
    arrayOfChar[i] = paramChar;
    for (int j = i + 1; j < arrayOfChar.length; j++) {
      if (matches(arrayOfChar[j])) {
        arrayOfChar[j] = paramChar;
      }
    }
    return new String(arrayOfChar);
  }
  
  public String replaceFrom(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    int i = paramCharSequence2.length();
    if (i == 0) {
      return removeFrom(paramCharSequence1);
    }
    if (i == 1) {
      return replaceFrom(paramCharSequence1, paramCharSequence2.charAt(0));
    }
    String str = paramCharSequence1.toString();
    int j = indexIn(str);
    if (j == -1) {
      return str;
    }
    int k = str.length();
    StringBuilder localStringBuilder = new StringBuilder(k * 3 / 2 + 16);
    int m = 0;
    do
    {
      localStringBuilder.append(str, m, j);
      localStringBuilder.append(paramCharSequence2);
      m = j + 1;
      j = indexIn(str, m);
    } while (j != -1);
    localStringBuilder.append(str, m, k);
    return localStringBuilder.toString();
  }
  
  public String trimFrom(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    for (int j = 0; (j < i) && (matches(paramCharSequence.charAt(j))); j++) {}
    for (int k = i - 1; (k > j) && (matches(paramCharSequence.charAt(k))); k--) {}
    return paramCharSequence.subSequence(j, k + 1).toString();
  }
  
  public String trimLeadingFrom(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++) {
      if (!matches(paramCharSequence.charAt(j))) {
        return paramCharSequence.subSequence(j, i).toString();
      }
    }
    return "";
  }
  
  public String trimTrailingFrom(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    for (int j = i - 1; j >= 0; j--) {
      if (!matches(paramCharSequence.charAt(j))) {
        return paramCharSequence.subSequence(0, j + 1).toString();
      }
    }
    return "";
  }
  
  public String collapseFrom(CharSequence paramCharSequence, char paramChar)
  {
    int i = paramCharSequence.length();
    for (int j = 0; j < i; j++)
    {
      char c = paramCharSequence.charAt(j);
      if (matches(c)) {
        if ((c == paramChar) && ((j == i - 1) || (!matches(paramCharSequence.charAt(j + 1)))))
        {
          j++;
        }
        else
        {
          StringBuilder localStringBuilder = new StringBuilder(i).append(paramCharSequence.subSequence(0, j)).append(paramChar);
          return finishCollapseFrom(paramCharSequence, j + 1, i, paramChar, localStringBuilder, true);
        }
      }
    }
    return paramCharSequence.toString();
  }
  
  public String trimAndCollapseFrom(CharSequence paramCharSequence, char paramChar)
  {
    int i = paramCharSequence.length();
    for (int j = 0; (j < i) && (matches(paramCharSequence.charAt(j))); j++) {}
    for (int k = i - 1; (k > j) && (matches(paramCharSequence.charAt(k))); k--) {}
    return (j == 0) && (k == i - 1) ? collapseFrom(paramCharSequence, paramChar) : finishCollapseFrom(paramCharSequence, j, k + 1, paramChar, new StringBuilder(k + 1 - j), false);
  }
  
  private String finishCollapseFrom(CharSequence paramCharSequence, int paramInt1, int paramInt2, char paramChar, StringBuilder paramStringBuilder, boolean paramBoolean)
  {
    for (int i = paramInt1; i < paramInt2; i++)
    {
      char c = paramCharSequence.charAt(i);
      if (matches(c))
      {
        if (!paramBoolean)
        {
          paramStringBuilder.append(paramChar);
          paramBoolean = true;
        }
      }
      else
      {
        paramStringBuilder.append(c);
        paramBoolean = false;
      }
    }
    return paramStringBuilder.toString();
  }
  
  public boolean apply(Character paramCharacter)
  {
    return matches(paramCharacter.charValue());
  }
  
  public String toString()
  {
    return this.description;
  }
  
  static
  {
    StringBuilder localStringBuilder = new StringBuilder("0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".length());
    for (int i = 0; i < "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".length(); i++) {
      localStringBuilder.append((char)("0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".charAt(i) + '\t'));
    }
    NINES = localStringBuilder.toString();
  }
  
  @GwtIncompatible("java.util.BitSet")
  private static class BitSetMatcher
    extends CharMatcher.FastMatcher
  {
    private final BitSet table;
    
    private BitSetMatcher(BitSet paramBitSet, String paramString)
    {
      super();
      if (paramBitSet.length() + 64 < paramBitSet.size()) {
        paramBitSet = (BitSet)paramBitSet.clone();
      }
      this.table = paramBitSet;
    }
    
    public boolean matches(char paramChar)
    {
      return this.table.get(paramChar);
    }
    
    void setBits(BitSet paramBitSet)
    {
      paramBitSet.or(this.table);
    }
  }
  
  static final class NegatedFastMatcher
    extends CharMatcher.NegatedMatcher
  {
    NegatedFastMatcher(CharMatcher paramCharMatcher)
    {
      super();
    }
    
    NegatedFastMatcher(String paramString, CharMatcher paramCharMatcher)
    {
      super(paramCharMatcher);
    }
    
    public final CharMatcher precomputed()
    {
      return this;
    }
    
    CharMatcher withToString(String paramString)
    {
      return new NegatedFastMatcher(paramString, this.original);
    }
  }
  
  static abstract class FastMatcher
    extends CharMatcher
  {
    FastMatcher() {}
    
    FastMatcher(String paramString)
    {
      super();
    }
    
    public final CharMatcher precomputed()
    {
      return this;
    }
    
    public CharMatcher negate()
    {
      return new CharMatcher.NegatedFastMatcher(this);
    }
  }
  
  private static class Or
    extends CharMatcher
  {
    final CharMatcher first;
    final CharMatcher second;
    
    Or(CharMatcher paramCharMatcher1, CharMatcher paramCharMatcher2, String paramString)
    {
      super();
      this.first = ((CharMatcher)Preconditions.checkNotNull(paramCharMatcher1));
      this.second = ((CharMatcher)Preconditions.checkNotNull(paramCharMatcher2));
    }
    
    Or(CharMatcher paramCharMatcher1, CharMatcher paramCharMatcher2)
    {
      this(paramCharMatcher1, paramCharMatcher2, "CharMatcher.or(" + paramCharMatcher1 + ", " + paramCharMatcher2 + ")");
    }
    
    @GwtIncompatible("java.util.BitSet")
    void setBits(BitSet paramBitSet)
    {
      this.first.setBits(paramBitSet);
      this.second.setBits(paramBitSet);
    }
    
    public boolean matches(char paramChar)
    {
      return (this.first.matches(paramChar)) || (this.second.matches(paramChar));
    }
    
    CharMatcher withToString(String paramString)
    {
      return new Or(this.first, this.second, paramString);
    }
  }
  
  private static class And
    extends CharMatcher
  {
    final CharMatcher first;
    final CharMatcher second;
    
    And(CharMatcher paramCharMatcher1, CharMatcher paramCharMatcher2)
    {
      this(paramCharMatcher1, paramCharMatcher2, "CharMatcher.and(" + paramCharMatcher1 + ", " + paramCharMatcher2 + ")");
    }
    
    And(CharMatcher paramCharMatcher1, CharMatcher paramCharMatcher2, String paramString)
    {
      super();
      this.first = ((CharMatcher)Preconditions.checkNotNull(paramCharMatcher1));
      this.second = ((CharMatcher)Preconditions.checkNotNull(paramCharMatcher2));
    }
    
    public boolean matches(char paramChar)
    {
      return (this.first.matches(paramChar)) && (this.second.matches(paramChar));
    }
    
    @GwtIncompatible("java.util.BitSet")
    void setBits(BitSet paramBitSet)
    {
      BitSet localBitSet1 = new BitSet();
      this.first.setBits(localBitSet1);
      BitSet localBitSet2 = new BitSet();
      this.second.setBits(localBitSet2);
      localBitSet1.and(localBitSet2);
      paramBitSet.or(localBitSet1);
    }
    
    CharMatcher withToString(String paramString)
    {
      return new And(this.first, this.second, paramString);
    }
  }
  
  private static class NegatedMatcher
    extends CharMatcher
  {
    final CharMatcher original;
    
    NegatedMatcher(String paramString, CharMatcher paramCharMatcher)
    {
      super();
      this.original = paramCharMatcher;
    }
    
    NegatedMatcher(CharMatcher paramCharMatcher)
    {
      this(paramCharMatcher + ".negate()", paramCharMatcher);
    }
    
    public boolean matches(char paramChar)
    {
      return !this.original.matches(paramChar);
    }
    
    public boolean matchesAllOf(CharSequence paramCharSequence)
    {
      return this.original.matchesNoneOf(paramCharSequence);
    }
    
    public boolean matchesNoneOf(CharSequence paramCharSequence)
    {
      return this.original.matchesAllOf(paramCharSequence);
    }
    
    public int countIn(CharSequence paramCharSequence)
    {
      return paramCharSequence.length() - this.original.countIn(paramCharSequence);
    }
    
    @GwtIncompatible("java.util.BitSet")
    void setBits(BitSet paramBitSet)
    {
      BitSet localBitSet = new BitSet();
      this.original.setBits(localBitSet);
      localBitSet.flip(0, 65536);
      paramBitSet.or(localBitSet);
    }
    
    public CharMatcher negate()
    {
      return this.original;
    }
    
    CharMatcher withToString(String paramString)
    {
      return new NegatedMatcher(paramString, this.original);
    }
  }
  
  private static class RangesMatcher
    extends CharMatcher
  {
    private final char[] rangeStarts;
    private final char[] rangeEnds;
    
    RangesMatcher(String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
    {
      super();
      this.rangeStarts = paramArrayOfChar1;
      this.rangeEnds = paramArrayOfChar2;
      Preconditions.checkArgument(paramArrayOfChar1.length == paramArrayOfChar2.length);
      for (int i = 0; i < paramArrayOfChar1.length; i++)
      {
        Preconditions.checkArgument(paramArrayOfChar1[i] <= paramArrayOfChar2[i]);
        if (i + 1 < paramArrayOfChar1.length) {
          Preconditions.checkArgument(paramArrayOfChar2[i] < paramArrayOfChar1[(i + 1)]);
        }
      }
    }
    
    public boolean matches(char paramChar)
    {
      int i = Arrays.binarySearch(this.rangeStarts, paramChar);
      if (i >= 0) {
        return true;
      }
      i = (i ^ 0xFFFFFFFF) - 1;
      return (i >= 0) && (paramChar <= this.rangeEnds[i]);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\CharMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */