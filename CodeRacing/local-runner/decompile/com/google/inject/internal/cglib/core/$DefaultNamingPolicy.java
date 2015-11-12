package com.google.inject.internal.cglib.core;

public class $DefaultNamingPolicy
  implements .NamingPolicy
{
  public static final DefaultNamingPolicy INSTANCE = new DefaultNamingPolicy();
  
  public String getClassName(String paramString1, String paramString2, Object paramObject, .Predicate paramPredicate)
  {
    if (paramString1 == null) {
      paramString1 = "com.google.inject.internal.cglib.empty.$Object";
    } else if (paramString1.startsWith("java")) {
      paramString1 = "$" + paramString1;
    }
    String str1 = paramString1 + "$$" + paramString2.substring(paramString2.lastIndexOf('.') + 1) + getTag() + "$$" + Integer.toHexString(paramObject.hashCode());
    String str2 = str1;
    int i = 2;
    while (paramPredicate.evaluate(str2)) {
      str2 = str1 + "_" + i++;
    }
    return str2;
  }
  
  protected String getTag()
  {
    return "ByCGLIB";
  }
  
  public int hashCode()
  {
    return getTag().hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof DefaultNamingPolicy)) && (((DefaultNamingPolicy)paramObject).getTag().equals(getTag()));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$DefaultNamingPolicy.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */