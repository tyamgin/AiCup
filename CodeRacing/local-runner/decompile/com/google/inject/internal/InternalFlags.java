package com.google.inject.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.logging.Logger;

public class InternalFlags
{
  private static final Logger logger = Logger.getLogger(InternalFlags.class.getName());
  private static final IncludeStackTraceOption INCLUDE_STACK_TRACES = parseIncludeStackTraceOption();
  private static final CustomClassLoadingOption CUSTOM_CLASS_LOADING = parseCustomClassLoadingOption();
  private static final NullableProvidesOption NULLABLE_PROVIDES = parseNullableProvidesOption(NullableProvidesOption.ERROR);
  
  public static IncludeStackTraceOption getIncludeStackTraceOption()
  {
    return INCLUDE_STACK_TRACES;
  }
  
  public static CustomClassLoadingOption getCustomClassLoadingOption()
  {
    return CUSTOM_CLASS_LOADING;
  }
  
  public static NullableProvidesOption getNullableProvidesOption()
  {
    return NULLABLE_PROVIDES;
  }
  
  private static IncludeStackTraceOption parseIncludeStackTraceOption()
  {
    return (IncludeStackTraceOption)getSystemOption("guice_include_stack_traces", IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE);
  }
  
  private static CustomClassLoadingOption parseCustomClassLoadingOption()
  {
    return (CustomClassLoadingOption)getSystemOption("guice_custom_class_loading", CustomClassLoadingOption.BRIDGE, CustomClassLoadingOption.OFF);
  }
  
  private static NullableProvidesOption parseNullableProvidesOption(NullableProvidesOption paramNullableProvidesOption)
  {
    return (NullableProvidesOption)getSystemOption("guice_check_nullable_provides_params", paramNullableProvidesOption);
  }
  
  private static Enum getSystemOption(String paramString, Enum paramEnum)
  {
    return getSystemOption(paramString, paramEnum, paramEnum);
  }
  
  private static Enum getSystemOption(String paramString, Enum paramEnum1, Enum paramEnum2)
  {
    Class localClass = paramEnum1.getDeclaringClass();
    String str1 = null;
    try
    {
      str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty(this.val$name);
        }
      });
      return (str1 != null) && (str1.length() > 0) ? Enum.valueOf(localClass, str1) : paramEnum1;
    }
    catch (SecurityException localSecurityException)
    {
      return paramEnum2;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      String str2 = String.valueOf(String.valueOf(str1));
      String str3 = String.valueOf(String.valueOf(paramString));
      String str4 = String.valueOf(String.valueOf(Arrays.asList(localClass.getEnumConstants())));
      logger.warning(56 + str2.length() + str3.length() + str4.length() + str2 + " is not a valid flag value for " + str3 + ". " + " Values must be one of " + str4);
    }
    return paramEnum1;
  }
  
  public static enum NullableProvidesOption
  {
    IGNORE,  WARN,  ERROR;
  }
  
  public static enum CustomClassLoadingOption
  {
    OFF,  BRIDGE;
  }
  
  public static enum IncludeStackTraceOption
  {
    OFF,  ONLY_FOR_DECLARING_SOURCE,  COMPLETE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InternalFlags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */