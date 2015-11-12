package com.google.inject.internal.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class $Type
{
  public static final int VOID = 0;
  public static final int BOOLEAN = 1;
  public static final int CHAR = 2;
  public static final int BYTE = 3;
  public static final int SHORT = 4;
  public static final int INT = 5;
  public static final int FLOAT = 6;
  public static final int LONG = 7;
  public static final int DOUBLE = 8;
  public static final int ARRAY = 9;
  public static final int OBJECT = 10;
  public static final int METHOD = 11;
  public static final Type VOID_TYPE = new Type(0, null, 1443168256, 1);
  public static final Type BOOLEAN_TYPE = new Type(1, null, 1509950721, 1);
  public static final Type CHAR_TYPE = new Type(2, null, 1124075009, 1);
  public static final Type BYTE_TYPE = new Type(3, null, 1107297537, 1);
  public static final Type SHORT_TYPE = new Type(4, null, 1392510721, 1);
  public static final Type INT_TYPE = new Type(5, null, 1224736769, 1);
  public static final Type FLOAT_TYPE = new Type(6, null, 1174536705, 1);
  public static final Type LONG_TYPE = new Type(7, null, 1241579778, 1);
  public static final Type DOUBLE_TYPE = new Type(8, null, 1141048066, 1);
  private final int a;
  private final char[] b;
  private final int c;
  private final int d;
  
  private $Type(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    this.a = paramInt1;
    this.b = paramArrayOfChar;
    this.c = paramInt2;
    this.d = paramInt3;
  }
  
  public static Type getType(String paramString)
  {
    return a(paramString.toCharArray(), 0);
  }
  
  public static Type getObjectType(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    return new Type(arrayOfChar[0] == '[' ? 9 : 10, arrayOfChar, 0, arrayOfChar.length);
  }
  
  public static Type getMethodType(String paramString)
  {
    return a(paramString.toCharArray(), 0);
  }
  
  public static Type getMethodType(Type paramType, Type... paramVarArgs)
  {
    return getType(getMethodDescriptor(paramType, paramVarArgs));
  }
  
  public static Type getType(Class paramClass)
  {
    if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        return INT_TYPE;
      }
      if (paramClass == Void.TYPE) {
        return VOID_TYPE;
      }
      if (paramClass == Boolean.TYPE) {
        return BOOLEAN_TYPE;
      }
      if (paramClass == Byte.TYPE) {
        return BYTE_TYPE;
      }
      if (paramClass == Character.TYPE) {
        return CHAR_TYPE;
      }
      if (paramClass == Short.TYPE) {
        return SHORT_TYPE;
      }
      if (paramClass == Double.TYPE) {
        return DOUBLE_TYPE;
      }
      if (paramClass == Float.TYPE) {
        return FLOAT_TYPE;
      }
      return LONG_TYPE;
    }
    return getType(getDescriptor(paramClass));
  }
  
  public static Type getType(Constructor paramConstructor)
  {
    return getType(getConstructorDescriptor(paramConstructor));
  }
  
  public static Type getType(Method paramMethod)
  {
    return getType(getMethodDescriptor(paramMethod));
  }
  
  public static Type[] getArgumentTypes(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 1;
    int j = 0;
    for (;;)
    {
      int k = arrayOfChar[(i++)];
      if (k == 41) {
        break;
      }
      if (k == 76)
      {
        while (arrayOfChar[(i++)] != ';') {}
        j++;
      }
      else if (k != 91)
      {
        j++;
      }
    }
    Type[] arrayOfType = new Type[j];
    i = 1;
    for (j = 0; arrayOfChar[i] != ')'; j++)
    {
      arrayOfType[j] = a(arrayOfChar, i);
      i += arrayOfType[j].d + (arrayOfType[j].a == 10 ? 2 : 0);
    }
    return arrayOfType;
  }
  
  public static Type[] getArgumentTypes(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Type[] arrayOfType = new Type[arrayOfClass.length];
    for (int i = arrayOfClass.length - 1; i >= 0; i--) {
      arrayOfType[i] = getType(arrayOfClass[i]);
    }
    return arrayOfType;
  }
  
  public static Type getReturnType(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    return a(arrayOfChar, paramString.indexOf(')') + 1);
  }
  
  public static Type getReturnType(Method paramMethod)
  {
    return getType(paramMethod.getReturnType());
  }
  
  public static int getArgumentsAndReturnSizes(String paramString)
  {
    int i = 1;
    int j = 1;
    for (;;)
    {
      int k = paramString.charAt(j++);
      if (k == 41)
      {
        k = paramString.charAt(j);
        return i << 2 | ((k == 68) || (k == 74) ? 2 : k == 86 ? 0 : 1);
      }
      if (k == 76)
      {
        while (paramString.charAt(j++) != ';') {}
        i++;
      }
      else if (k == 91)
      {
        while ((k = paramString.charAt(j)) == '[') {
          j++;
        }
        if ((k == 68) || (k == 74)) {
          i--;
        }
      }
      else if ((k == 68) || (k == 74))
      {
        i += 2;
      }
      else
      {
        i++;
      }
    }
  }
  
  private static Type a(char[] paramArrayOfChar, int paramInt)
  {
    int i;
    switch (paramArrayOfChar[paramInt])
    {
    case 'V': 
      return VOID_TYPE;
    case 'Z': 
      return BOOLEAN_TYPE;
    case 'C': 
      return CHAR_TYPE;
    case 'B': 
      return BYTE_TYPE;
    case 'S': 
      return SHORT_TYPE;
    case 'I': 
      return INT_TYPE;
    case 'F': 
      return FLOAT_TYPE;
    case 'J': 
      return LONG_TYPE;
    case 'D': 
      return DOUBLE_TYPE;
    case '[': 
      for (i = 1; paramArrayOfChar[(paramInt + i)] == '['; i++) {}
      if (paramArrayOfChar[(paramInt + i)] == 'L')
      {
        i++;
        while (paramArrayOfChar[(paramInt + i)] != ';') {
          i++;
        }
      }
      return new Type(9, paramArrayOfChar, paramInt, i + 1);
    case 'L': 
      for (i = 1; paramArrayOfChar[(paramInt + i)] != ';'; i++) {}
      return new Type(10, paramArrayOfChar, paramInt + 1, i - 1);
    }
    return new Type(11, paramArrayOfChar, paramInt, paramArrayOfChar.length - paramInt);
  }
  
  public int getSort()
  {
    return this.a;
  }
  
  public int getDimensions()
  {
    for (int i = 1; this.b[(this.c + i)] == '['; i++) {}
    return i;
  }
  
  public Type getElementType()
  {
    return a(this.b, this.c + getDimensions());
  }
  
  public String getClassName()
  {
    switch (this.a)
    {
    case 0: 
      return "void";
    case 1: 
      return "boolean";
    case 2: 
      return "char";
    case 3: 
      return "byte";
    case 4: 
      return "short";
    case 5: 
      return "int";
    case 6: 
      return "float";
    case 7: 
      return "long";
    case 8: 
      return "double";
    case 9: 
      StringBuffer localStringBuffer = new StringBuffer(getElementType().getClassName());
      for (int i = getDimensions(); i > 0; i--) {
        localStringBuffer.append("[]");
      }
      return localStringBuffer.toString();
    case 10: 
      return new String(this.b, this.c, this.d).replace('/', '.');
    }
    return null;
  }
  
  public String getInternalName()
  {
    return new String(this.b, this.c, this.d);
  }
  
  public Type[] getArgumentTypes()
  {
    return getArgumentTypes(getDescriptor());
  }
  
  public Type getReturnType()
  {
    return getReturnType(getDescriptor());
  }
  
  public int getArgumentsAndReturnSizes()
  {
    return getArgumentsAndReturnSizes(getDescriptor());
  }
  
  public String getDescriptor()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    a(localStringBuffer);
    return localStringBuffer.toString();
  }
  
  public static String getMethodDescriptor(Type paramType, Type... paramVarArgs)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    for (int i = 0; i < paramVarArgs.length; i++) {
      paramVarArgs[i].a(localStringBuffer);
    }
    localStringBuffer.append(')');
    paramType.a(localStringBuffer);
    return localStringBuffer.toString();
  }
  
  private void a(StringBuffer paramStringBuffer)
  {
    if (this.b == null)
    {
      paramStringBuffer.append((char)((this.c & 0xFF000000) >>> 24));
    }
    else if (this.a == 10)
    {
      paramStringBuffer.append('L');
      paramStringBuffer.append(this.b, this.c, this.d);
      paramStringBuffer.append(';');
    }
    else
    {
      paramStringBuffer.append(this.b, this.c, this.d);
    }
  }
  
  public static String getInternalName(Class paramClass)
  {
    return paramClass.getName().replace('.', '/');
  }
  
  public static String getDescriptor(Class paramClass)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    a(localStringBuffer, paramClass);
    return localStringBuffer.toString();
  }
  
  public static String getConstructorDescriptor(Constructor paramConstructor)
  {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    for (int i = 0; i < arrayOfClass.length; i++) {
      a(localStringBuffer, arrayOfClass[i]);
    }
    return ")V";
  }
  
  public static String getMethodDescriptor(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    for (int i = 0; i < arrayOfClass.length; i++) {
      a(localStringBuffer, arrayOfClass[i]);
    }
    localStringBuffer.append(')');
    a(localStringBuffer, paramMethod.getReturnType());
    return localStringBuffer.toString();
  }
  
  private static void a(StringBuffer paramStringBuffer, Class paramClass)
  {
    for (Class localClass = paramClass;; localClass = localClass.getComponentType())
    {
      if (localClass.isPrimitive())
      {
        char c1;
        if (localClass == Integer.TYPE) {
          c1 = 'I';
        } else if (localClass == Void.TYPE) {
          c1 = 'V';
        } else if (localClass == Boolean.TYPE) {
          c1 = 'Z';
        } else if (localClass == Byte.TYPE) {
          c1 = 'B';
        } else if (localClass == Character.TYPE) {
          c1 = 'C';
        } else if (localClass == Short.TYPE) {
          c1 = 'S';
        } else if (localClass == Double.TYPE) {
          c1 = 'D';
        } else if (localClass == Float.TYPE) {
          c1 = 'F';
        } else {
          c1 = 'J';
        }
        paramStringBuffer.append(c1);
        return;
      }
      if (!localClass.isArray()) {
        break;
      }
      paramStringBuffer.append('[');
    }
    paramStringBuffer.append('L');
    String str = localClass.getName();
    int i = str.length();
    for (int j = 0; j < i; j++)
    {
      char c2 = str.charAt(j);
      paramStringBuffer.append(c2 == '.' ? '/' : c2);
    }
    paramStringBuffer.append(';');
  }
  
  public int getSize()
  {
    return this.b == null ? this.c & 0xFF : 1;
  }
  
  public int getOpcode(int paramInt)
  {
    if ((paramInt == 46) || (paramInt == 79)) {
      return paramInt + (this.b == null ? (this.c & 0xFF00) >> 8 : 4);
    }
    return paramInt + (this.b == null ? (this.c & 0xFF0000) >> 16 : 4);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Type)) {
      return false;
    }
    Type localType = (Type)paramObject;
    if (this.a != localType.a) {
      return false;
    }
    if (this.a >= 9)
    {
      if (this.d != localType.d) {
        return false;
      }
      int i = this.c;
      int j = localType.c;
      int k = i + this.d;
      while (i < k)
      {
        if (this.b[i] != localType.b[j]) {
          return false;
        }
        i++;
        j++;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 13 * this.a;
    if (this.a >= 9)
    {
      int j = this.c;
      int k = j + this.d;
      while (j < k)
      {
        i = 17 * (i + this.b[j]);
        j++;
      }
    }
    return i;
  }
  
  public String toString()
  {
    return getDescriptor();
  }
  
  static {}
  
  static void _clinit_() {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$Type.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */