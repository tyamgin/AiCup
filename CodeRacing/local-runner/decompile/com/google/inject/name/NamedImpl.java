package com.google.inject.name;

import com.google.common.base.Preconditions;
import java.io.Serializable;

class NamedImpl
  implements Named, Serializable
{
  private final String value;
  private static final long serialVersionUID = 0L;
  
  public NamedImpl(String paramString)
  {
    this.value = ((String)Preconditions.checkNotNull(paramString, "name"));
  }
  
  public String value()
  {
    return this.value;
  }
  
  public int hashCode()
  {
    return 127 * "value".hashCode() ^ this.value.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Named)) {
      return false;
    }
    Named localNamed = (Named)paramObject;
    return this.value.equals(localNamed.value());
  }
  
  public String toString()
  {
    String str1 = String.valueOf(String.valueOf(Named.class.getName()));
    String str2 = String.valueOf(String.valueOf(this.value));
    return 9 + str1.length() + str2.length() + "@" + str1 + "(value=" + str2 + ")";
  }
  
  public Class annotationType()
  {
    return Named.class;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\name\NamedImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */