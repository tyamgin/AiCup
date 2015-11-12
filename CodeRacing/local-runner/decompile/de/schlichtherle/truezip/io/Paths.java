package de.schlichtherle.truezip.io;

import java.io.File;
import java.util.Locale;

public final class Paths
{
  public static String normalize(String paramString, char paramChar)
  {
    return new Normalizer(paramChar).normalize(paramString);
  }
  
  public static int prefixLength(String paramString, char paramChar, boolean paramBoolean)
  {
    int i = paramString.length();
    if (i <= 0) {
      return 0;
    }
    char c = paramString.charAt(0);
    if ('\\' == File.separatorChar)
    {
      if ((2 <= i) && (':' == paramString.charAt(1)) && ((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')))) {
        return (3 <= i) && (paramChar == paramString.charAt(2)) ? 3 : 2;
      }
      if (paramChar == c)
      {
        if ((2 <= i) && (paramChar == paramString.charAt(1)))
        {
          if (!paramBoolean) {
            return 2;
          }
          int j = paramString.indexOf(paramChar, 2) + 1;
          if (0 == j) {
            return i;
          }
          int k = paramString.indexOf(paramChar, j) + 1;
          if (0 == k) {
            return i;
          }
          return k;
        }
        return 1;
      }
      return 0;
    }
    return paramChar == c ? 1 : 0;
  }
  
  public static boolean contains(String paramString1, String paramString2, char paramChar)
  {
    if ('\\' == File.separatorChar)
    {
      paramString1 = paramString1.toLowerCase(Locale.getDefault());
      paramString2 = paramString2.toLowerCase(Locale.getDefault());
    }
    if (!paramString2.startsWith(paramString1)) {
      return false;
    }
    int i = paramString1.length();
    int j = paramString2.length();
    if (i == j) {
      return true;
    }
    if (i < j) {
      return paramString2.charAt(i) == paramChar;
    }
    return false;
  }
  
  public static class Splitter
  {
    private final char separatorChar;
    private final int fixum;
    private String parentPath;
    private String memberName;
    
    public Splitter(char paramChar, boolean paramBoolean)
    {
      this.separatorChar = paramChar;
      this.fixum = (paramBoolean ? 1 : 0);
    }
    
    public Splitter split(String paramString)
    {
      int i = Paths.prefixLength(paramString, this.separatorChar, false);
      int j = paramString.length() - 1;
      if (i > j)
      {
        this.parentPath = null;
        this.memberName = "";
        return this;
      }
      j = lastIndexNot(paramString, this.separatorChar, j);
      int k = paramString.lastIndexOf(this.separatorChar, j) + 1;
      j++;
      if (i >= j)
      {
        this.parentPath = null;
        this.memberName = "";
      }
      else if (i >= k)
      {
        this.parentPath = (0 >= i ? null : paramString.substring(0, i));
        this.memberName = paramString.substring(i, j);
      }
      else
      {
        int m;
        if (i >= (m = lastIndexNot(paramString, this.separatorChar, k - 1) + 1))
        {
          this.parentPath = paramString.substring(0, i);
          this.memberName = paramString.substring(k, j);
        }
        else
        {
          this.parentPath = paramString.substring(0, m + this.fixum);
          this.memberName = paramString.substring(k, j);
        }
      }
      return this;
    }
    
    private static int lastIndexNot(String paramString, char paramChar, int paramInt)
    {
      do
      {
        if (paramChar != paramString.charAt(paramInt)) {
          break;
        }
        paramInt--;
      } while (paramInt >= 0);
      return paramInt;
    }
    
    public String getParentPath()
    {
      return this.parentPath;
    }
    
    public String getMemberName()
    {
      return this.memberName;
    }
  }
  
  public static class Normalizer
  {
    private final char separatorChar;
    private final StringBuilder builder = new StringBuilder();
    private String path;
    
    public Normalizer(char paramChar)
    {
      this.separatorChar = paramChar;
    }
    
    public String normalize(String paramString)
    {
      int i = Paths.prefixLength(paramString, this.separatorChar, false);
      int j = paramString.length();
      this.path = paramString.substring(i, j);
      this.builder.setLength(0);
      this.builder.ensureCapacity(j);
      normalize(0, j - i);
      this.builder.insert(0, paramString.substring(0, i));
      int k = this.builder.length();
      if (((j > 0) && (paramString.charAt(j - 1) == this.separatorChar)) || ((j > 1) && (paramString.charAt(j - 2) == this.separatorChar) && (paramString.charAt(j - 1) == '.')))
      {
        slashify();
        k = this.builder.length();
      }
      String str;
      if (k == paramString.length())
      {
        assert (paramString.equals(this.builder.toString()));
        str = paramString;
      }
      else
      {
        str = this.builder.toString();
        if (paramString.startsWith(str)) {
          str = paramString.substring(0, k);
        }
      }
      assert ((!str.equals(paramString)) || (str == paramString));
      return str;
    }
    
    private int normalize(int paramInt1, int paramInt2)
    {
      assert (paramInt1 >= 0);
      if (0 >= paramInt2) {
        return paramInt1;
      }
      int i = this.path.lastIndexOf(this.separatorChar, paramInt2 - 1);
      String str = this.path.substring(i + 1, paramInt2);
      if ((0 >= str.length()) || (".".equals(str))) {
        return normalize(paramInt1, i);
      }
      int j;
      if ("..".equals(str))
      {
        j = normalize(paramInt1 + 1, i) - 1;
        if (0 > j) {
          return 0;
        }
      }
      else
      {
        if (0 < paramInt1)
        {
          j = normalize(paramInt1 - 1, i);
          slashify();
          return j;
        }
        assert (0 == paramInt1);
        j = normalize(0, i);
        assert (0 == j);
      }
      slashify();
      this.builder.append(str);
      return j;
    }
    
    private void slashify()
    {
      int i = this.builder.length();
      if ((i > 0) && (this.builder.charAt(i - 1) != this.separatorChar)) {
        this.builder.append(this.separatorChar);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\io\Paths.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */