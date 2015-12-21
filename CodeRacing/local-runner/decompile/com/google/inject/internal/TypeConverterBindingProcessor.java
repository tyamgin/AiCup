package com.google.inject.internal;

import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class TypeConverterBindingProcessor
  extends AbstractProcessor
{
  TypeConverterBindingProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  static void prepareBuiltInConverters(InjectorImpl paramInjectorImpl)
  {
    convertToPrimitiveType(paramInjectorImpl, Integer.TYPE, Integer.class);
    convertToPrimitiveType(paramInjectorImpl, Long.TYPE, Long.class);
    convertToPrimitiveType(paramInjectorImpl, Boolean.TYPE, Boolean.class);
    convertToPrimitiveType(paramInjectorImpl, Byte.TYPE, Byte.class);
    convertToPrimitiveType(paramInjectorImpl, Short.TYPE, Short.class);
    convertToPrimitiveType(paramInjectorImpl, Float.TYPE, Float.class);
    convertToPrimitiveType(paramInjectorImpl, Double.TYPE, Double.class);
    convertToClass(paramInjectorImpl, Character.class, new TypeConverter()
    {
      public Object convert(String paramAnonymousString, TypeLiteral paramAnonymousTypeLiteral)
      {
        paramAnonymousString = paramAnonymousString.trim();
        if (paramAnonymousString.length() != 1) {
          throw new RuntimeException("Length != 1.");
        }
        return Character.valueOf(paramAnonymousString.charAt(0));
      }
      
      public String toString()
      {
        return "TypeConverter<Character>";
      }
    });
    convertToClasses(paramInjectorImpl, Matchers.subclassesOf(Enum.class), new TypeConverter()
    {
      public Object convert(String paramAnonymousString, TypeLiteral paramAnonymousTypeLiteral)
      {
        return Enum.valueOf(paramAnonymousTypeLiteral.getRawType(), paramAnonymousString);
      }
      
      public String toString()
      {
        return "TypeConverter<E extends Enum<E>>";
      }
    });
    internalConvertToTypes(paramInjectorImpl, new AbstractMatcher()new TypeConverter
    {
      public boolean matches(TypeLiteral paramAnonymousTypeLiteral)
      {
        return paramAnonymousTypeLiteral.getRawType() == Class.class;
      }
      
      public String toString()
      {
        return "Class<?>";
      }
    }, new TypeConverter()
    {
      public Object convert(String paramAnonymousString, TypeLiteral paramAnonymousTypeLiteral)
      {
        try
        {
          return Class.forName(paramAnonymousString);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new RuntimeException(localClassNotFoundException.getMessage());
        }
      }
      
      public String toString()
      {
        return "TypeConverter<Class<?>>";
      }
    });
  }
  
  /* Error */
  private static void convertToPrimitiveType(InjectorImpl arg0, Class arg1, Class arg2)
  {
    // Byte code:
    //   0: aload_2
    //   1: ldc 1
    //   3: aload_1
    //   4: invokevirtual 62	java/lang/Class:getName	()Ljava/lang/String;
    //   7: invokestatic 40	com/google/inject/internal/TypeConverterBindingProcessor:capitalize	(Ljava/lang/String;)Ljava/lang/String;
    //   10: invokestatic 68	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   13: dup
    //   14: invokevirtual 66	java/lang/String:length	()I
    //   17: ifeq +9 -> 26
    //   20: invokevirtual 65	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   23: goto +12 -> 35
    //   26: pop
    //   27: new 27	java/lang/String
    //   30: dup_x1
    //   31: swap
    //   32: invokespecial 63	java/lang/String:<init>	(Ljava/lang/String;)V
    //   35: iconst_1
    //   36: anewarray 19	java/lang/Class
    //   39: dup
    //   40: iconst_0
    //   41: ldc 27
    //   43: aastore
    //   44: invokevirtual 61	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   47: astore_3
    //   48: new 10	com/google/inject/internal/TypeConverterBindingProcessor$5
    //   51: dup
    //   52: aload_3
    //   53: aload_2
    //   54: invokespecial 50	com/google/inject/internal/TypeConverterBindingProcessor$5:<init>	(Ljava/lang/reflect/Method;Ljava/lang/Class;)V
    //   57: astore 4
    //   59: aload_0
    //   60: aload_2
    //   61: aload 4
    //   63: invokestatic 41	com/google/inject/internal/TypeConverterBindingProcessor:convertToClass	(Lcom/google/inject/internal/InjectorImpl;Ljava/lang/Class;Lcom/google/inject/spi/TypeConverter;)V
    //   66: goto +13 -> 79
    //   69: astore_3
    //   70: new 15	java/lang/AssertionError
    //   73: dup
    //   74: aload_3
    //   75: invokespecial 58	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
    //   78: athrow
    //   79: return
    // Exception table:
    //   from	to	target	type
    //   0	66	69	java/lang/NoSuchMethodException
  }
  
  private static void convertToClass(InjectorImpl paramInjectorImpl, Class paramClass, TypeConverter paramTypeConverter)
  {
    convertToClasses(paramInjectorImpl, Matchers.identicalTo(paramClass), paramTypeConverter);
  }
  
  private static void convertToClasses(InjectorImpl paramInjectorImpl, Matcher paramMatcher, TypeConverter paramTypeConverter)
  {
    internalConvertToTypes(paramInjectorImpl, new AbstractMatcher()
    {
      public boolean matches(TypeLiteral paramAnonymousTypeLiteral)
      {
        Type localType = paramAnonymousTypeLiteral.getType();
        if (!(localType instanceof Class)) {
          return false;
        }
        Class localClass = (Class)localType;
        return this.val$typeMatcher.matches(localClass);
      }
      
      public String toString()
      {
        return this.val$typeMatcher.toString();
      }
    }, paramTypeConverter);
  }
  
  private static void internalConvertToTypes(InjectorImpl paramInjectorImpl, Matcher paramMatcher, TypeConverter paramTypeConverter)
  {
    paramInjectorImpl.state.addConverter(new TypeConverterBinding(SourceProvider.UNKNOWN_SOURCE, paramMatcher, paramTypeConverter));
  }
  
  public Boolean visit(TypeConverterBinding paramTypeConverterBinding)
  {
    this.injector.state.addConverter(new TypeConverterBinding(paramTypeConverterBinding.getSource(), paramTypeConverterBinding.getTypeMatcher(), paramTypeConverterBinding.getTypeConverter()));
    return Boolean.valueOf(true);
  }
  
  private static String capitalize(String paramString)
  {
    if (paramString.length() == 0) {
      return paramString;
    }
    char c1 = paramString.charAt(0);
    char c2 = Character.toUpperCase(c1);
    char c3 = c2;
    String str = String.valueOf(String.valueOf(paramString.substring(1)));
    return 1 + str.length() + c3 + str;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\TypeConverterBindingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */