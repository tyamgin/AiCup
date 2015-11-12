package com.a.b.a.a.e.a.a;

import com.codeforces.commons.text.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class d
{
  private static final Pattern a = Pattern.compile("(%[a-zA-Z_1-90]+%)");
  private final String b;
  private final List c;
  private final Set d;
  
  public d(String paramString, List paramList, Set paramSet)
  {
    this.b = paramString;
    this.c = new ArrayList(paramList);
    this.d = new HashSet(paramSet);
  }
  
  public String a(String paramString, Map paramMap)
  {
    String str1 = this.b;
    Matcher localMatcher = a.matcher(this.b);
    while (localMatcher.find())
    {
      int i = 0;
      int j = 0;
      int k = localMatcher.groupCount();
      while (j < k)
      {
        String str2 = localMatcher.group(j);
        String str3 = System.getenv(str2.substring(1, str2.length() - 1));
        if (str3 != null)
        {
          str1 = StringUtil.replace(str1, str2, str3);
          i = 1;
        }
        j++;
      }
      if (i == 0) {
        break;
      }
    }
    if (paramMap != null)
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        str1 = StringUtil.replace(str1, "${" + (String)localEntry.getKey() + '}', (String)localEntry.getValue());
      }
    }
    return String.format(str1, new Object[] { paramString });
  }
  
  public List a()
  {
    return Collections.unmodifiableList(this.c);
  }
  
  public boolean a(String paramString)
  {
    return this.d.contains(paramString);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\a\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */