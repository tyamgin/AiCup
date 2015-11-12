package com.google.inject.name;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class Names
{
  public static Named named(String paramString)
  {
    return new NamedImpl(paramString);
  }
  
  public static void bindProperties(Binder paramBinder, Map paramMap)
  {
    paramBinder = paramBinder.skipSources(new Class[] { Names.class });
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      String str2 = (String)localEntry.getValue();
      paramBinder.bind(Key.get(String.class, new NamedImpl(str1))).toInstance(str2);
    }
  }
  
  public static void bindProperties(Binder paramBinder, Properties paramProperties)
  {
    paramBinder = paramBinder.skipSources(new Class[] { Names.class });
    Enumeration localEnumeration = paramProperties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = paramProperties.getProperty(str1);
      paramBinder.bind(Key.get(String.class, new NamedImpl(str1))).toInstance(str2);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\name\Names.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */