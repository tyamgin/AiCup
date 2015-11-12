package com.google.inject.internal.util;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class Classes
{
  public static boolean isInnerClass(Class paramClass)
  {
    return (!Modifier.isStatic(paramClass.getModifiers())) && (paramClass.getEnclosingClass() != null);
  }
  
  public static boolean isConcrete(Class paramClass)
  {
    int i = paramClass.getModifiers();
    return (!paramClass.isInterface()) && (!Modifier.isAbstract(i));
  }
  
  public static String toString(Member paramMember)
  {
    Class localClass = memberType(paramMember);
    String str1;
    String str2;
    if (localClass == Method.class)
    {
      str1 = String.valueOf(String.valueOf(paramMember.getDeclaringClass().getName()));
      str2 = String.valueOf(String.valueOf(paramMember.getName()));
      return 3 + str1.length() + str2.length() + str1 + "." + str2 + "()";
    }
    if (localClass == Field.class)
    {
      str1 = String.valueOf(String.valueOf(paramMember.getDeclaringClass().getName()));
      str2 = String.valueOf(String.valueOf(paramMember.getName()));
      return 1 + str1.length() + str2.length() + str1 + "." + str2;
    }
    if (localClass == Constructor.class) {
      return String.valueOf(paramMember.getDeclaringClass().getName()).concat(".<init>()");
    }
    throw new AssertionError();
  }
  
  public static Class memberType(Member paramMember)
  {
    Preconditions.checkNotNull(paramMember, "member");
    if ((paramMember instanceof Field)) {
      return Field.class;
    }
    if ((paramMember instanceof Method)) {
      return Method.class;
    }
    if ((paramMember instanceof Constructor)) {
      return Constructor.class;
    }
    String str = String.valueOf(String.valueOf(paramMember.getClass()));
    throw new IllegalArgumentException(45 + str.length() + "Unsupported implementation class for Member, " + str);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\util\Classes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */