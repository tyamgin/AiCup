package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class $TypeUtils
{
  private static final Map transforms = new HashMap();
  private static final Map rtransforms = new HashMap();
  
  public static .Type getType(String paramString)
  {
    return .Type.getType("L" + paramString.replace('.', '/') + ";");
  }
  
  public static boolean isFinal(int paramInt)
  {
    return (0x10 & paramInt) != 0;
  }
  
  public static boolean isStatic(int paramInt)
  {
    return (0x8 & paramInt) != 0;
  }
  
  public static boolean isProtected(int paramInt)
  {
    return (0x4 & paramInt) != 0;
  }
  
  public static boolean isPublic(int paramInt)
  {
    return (0x1 & paramInt) != 0;
  }
  
  public static boolean isAbstract(int paramInt)
  {
    return (0x400 & paramInt) != 0;
  }
  
  public static boolean isInterface(int paramInt)
  {
    return (0x200 & paramInt) != 0;
  }
  
  public static boolean isPrivate(int paramInt)
  {
    return (0x2 & paramInt) != 0;
  }
  
  public static boolean isSynthetic(int paramInt)
  {
    return (0x1000 & paramInt) != 0;
  }
  
  public static boolean isBridge(int paramInt)
  {
    return (0x40 & paramInt) != 0;
  }
  
  public static String getPackageName(.Type paramType)
  {
    return getPackageName(getClassName(paramType));
  }
  
  public static String getPackageName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    return i < 0 ? "" : paramString.substring(0, i);
  }
  
  public static String upperFirst(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    return Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1);
  }
  
  public static String getClassName(.Type paramType)
  {
    if (isPrimitive(paramType)) {
      return (String)rtransforms.get(paramType.getDescriptor());
    }
    if (isArray(paramType)) {
      return getClassName(getComponentType(paramType)) + "[]";
    }
    return paramType.getClassName();
  }
  
  public static .Type[] add(.Type[] paramArrayOfType, .Type paramType)
  {
    if (paramArrayOfType == null) {
      return new .Type[] { paramType };
    }
    List localList = Arrays.asList(paramArrayOfType);
    if (localList.contains(paramType)) {
      return paramArrayOfType;
    }
    .Type[] arrayOfType = new .Type[paramArrayOfType.length + 1];
    System.arraycopy(paramArrayOfType, 0, arrayOfType, 0, paramArrayOfType.length);
    arrayOfType[paramArrayOfType.length] = paramType;
    return arrayOfType;
  }
  
  public static .Type[] add(.Type[] paramArrayOfType1, .Type[] paramArrayOfType2)
  {
    .Type[] arrayOfType = new .Type[paramArrayOfType1.length + paramArrayOfType2.length];
    System.arraycopy(paramArrayOfType1, 0, arrayOfType, 0, paramArrayOfType1.length);
    System.arraycopy(paramArrayOfType2, 0, arrayOfType, paramArrayOfType1.length, paramArrayOfType2.length);
    return arrayOfType;
  }
  
  public static .Type fromInternalName(String paramString)
  {
    return .Type.getType("L" + paramString + ";");
  }
  
  public static .Type[] fromInternalNames(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    .Type[] arrayOfType = new .Type[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++) {
      arrayOfType[i] = fromInternalName(paramArrayOfString[i]);
    }
    return arrayOfType;
  }
  
  public static int getStackSize(.Type[] paramArrayOfType)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfType.length; j++) {
      i += paramArrayOfType[j].getSize();
    }
    return i;
  }
  
  public static String[] toInternalNames(.Type[] paramArrayOfType)
  {
    if (paramArrayOfType == null) {
      return null;
    }
    String[] arrayOfString = new String[paramArrayOfType.length];
    for (int i = 0; i < paramArrayOfType.length; i++) {
      arrayOfString[i] = paramArrayOfType[i].getInternalName();
    }
    return arrayOfString;
  }
  
  public static .Signature parseSignature(String paramString)
  {
    int i = paramString.indexOf(' ');
    int j = paramString.indexOf('(', i);
    int k = paramString.indexOf(')', j);
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1, j);
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    Iterator localIterator = parseTypes(paramString, j + 1, k).iterator();
    while (localIterator.hasNext()) {
      localStringBuffer.append(localIterator.next());
    }
    localStringBuffer.append(')');
    localStringBuffer.append(map(str1));
    return new .Signature(str2, localStringBuffer.toString());
  }
  
  public static .Type parseType(String paramString)
  {
    return .Type.getType(map(paramString));
  }
  
  public static .Type[] parseTypes(String paramString)
  {
    List localList = parseTypes(paramString, 0, paramString.length());
    .Type[] arrayOfType = new .Type[localList.size()];
    for (int i = 0; i < arrayOfType.length; i++) {
      arrayOfType[i] = .Type.getType((String)localList.get(i));
    }
    return arrayOfType;
  }
  
  public static .Signature parseConstructor(.Type[] paramArrayOfType)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    for (int i = 0; i < paramArrayOfType.length; i++) {
      localStringBuffer.append(paramArrayOfType[i].getDescriptor());
    }
    localStringBuffer.append(")");
    localStringBuffer.append("V");
    return new .Signature("<init>", localStringBuffer.toString());
  }
  
  public static .Signature parseConstructor(String paramString)
  {
    return parseSignature("void <init>(" + paramString + ")");
  }
  
  private static List parseTypes(String paramString, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList(5);
    for (;;)
    {
      int i = paramString.indexOf(',', paramInt1);
      if (i < 0) {
        break;
      }
      localArrayList.add(map(paramString.substring(paramInt1, i).trim()));
      paramInt1 = i + 1;
    }
    localArrayList.add(map(paramString.substring(paramInt1, paramInt2).trim()));
    return localArrayList;
  }
  
  private static String map(String paramString)
  {
    if (paramString.equals("")) {
      return paramString;
    }
    String str = (String)transforms.get(paramString);
    if (str != null) {
      return str;
    }
    if (paramString.indexOf('.') < 0) {
      return map("java.lang." + paramString);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while ((i = paramString.indexOf("[]", i) + 1) > 0) {
      localStringBuffer.append('[');
    }
    paramString = paramString.substring(0, paramString.length() - localStringBuffer.length() * 2);
    localStringBuffer.append('L').append(paramString.replace('.', '/')).append(';');
    return localStringBuffer.toString();
  }
  
  public static .Type getBoxedType(.Type paramType)
  {
    switch (paramType.getSort())
    {
    case 2: 
      return .Constants.TYPE_CHARACTER;
    case 1: 
      return .Constants.TYPE_BOOLEAN;
    case 8: 
      return .Constants.TYPE_DOUBLE;
    case 6: 
      return .Constants.TYPE_FLOAT;
    case 7: 
      return .Constants.TYPE_LONG;
    case 5: 
      return .Constants.TYPE_INTEGER;
    case 4: 
      return .Constants.TYPE_SHORT;
    case 3: 
      return .Constants.TYPE_BYTE;
    }
    return paramType;
  }
  
  public static .Type getUnboxedType(.Type paramType)
  {
    if (.Constants.TYPE_INTEGER.equals(paramType)) {
      return .Type.INT_TYPE;
    }
    if (.Constants.TYPE_BOOLEAN.equals(paramType)) {
      return .Type.BOOLEAN_TYPE;
    }
    if (.Constants.TYPE_DOUBLE.equals(paramType)) {
      return .Type.DOUBLE_TYPE;
    }
    if (.Constants.TYPE_LONG.equals(paramType)) {
      return .Type.LONG_TYPE;
    }
    if (.Constants.TYPE_CHARACTER.equals(paramType)) {
      return .Type.CHAR_TYPE;
    }
    if (.Constants.TYPE_BYTE.equals(paramType)) {
      return .Type.BYTE_TYPE;
    }
    if (.Constants.TYPE_FLOAT.equals(paramType)) {
      return .Type.FLOAT_TYPE;
    }
    if (.Constants.TYPE_SHORT.equals(paramType)) {
      return .Type.SHORT_TYPE;
    }
    return paramType;
  }
  
  public static boolean isArray(.Type paramType)
  {
    return paramType.getSort() == 9;
  }
  
  public static .Type getComponentType(.Type paramType)
  {
    if (!isArray(paramType)) {
      throw new IllegalArgumentException("Type " + paramType + " is not an array");
    }
    return .Type.getType(paramType.getDescriptor().substring(1));
  }
  
  public static boolean isPrimitive(.Type paramType)
  {
    switch (paramType.getSort())
    {
    case 9: 
    case 10: 
      return false;
    }
    return true;
  }
  
  public static String emulateClassGetName(.Type paramType)
  {
    if (isArray(paramType)) {
      return paramType.getDescriptor().replace('/', '.');
    }
    return getClassName(paramType);
  }
  
  public static boolean isConstructor(.MethodInfo paramMethodInfo)
  {
    return paramMethodInfo.getSignature().getName().equals("<init>");
  }
  
  public static .Type[] getTypes(Class[] paramArrayOfClass)
  {
    if (paramArrayOfClass == null) {
      return null;
    }
    .Type[] arrayOfType = new .Type[paramArrayOfClass.length];
    for (int i = 0; i < paramArrayOfClass.length; i++) {
      arrayOfType[i] = .Type.getType(paramArrayOfClass[i]);
    }
    return arrayOfType;
  }
  
  public static int ICONST(int paramInt)
  {
    switch (paramInt)
    {
    case -1: 
      return 2;
    case 0: 
      return 3;
    case 1: 
      return 4;
    case 2: 
      return 5;
    case 3: 
      return 6;
    case 4: 
      return 7;
    case 5: 
      return 8;
    }
    return -1;
  }
  
  public static int LCONST(long paramLong)
  {
    if (paramLong == 0L) {
      return 9;
    }
    if (paramLong == 1L) {
      return 10;
    }
    return -1;
  }
  
  public static int FCONST(float paramFloat)
  {
    if (paramFloat == 0.0F) {
      return 11;
    }
    if (paramFloat == 1.0F) {
      return 12;
    }
    if (paramFloat == 2.0F) {
      return 13;
    }
    return -1;
  }
  
  public static int DCONST(double paramDouble)
  {
    if (paramDouble == 0.0D) {
      return 14;
    }
    if (paramDouble == 1.0D) {
      return 15;
    }
    return -1;
  }
  
  public static int NEWARRAY(.Type paramType)
  {
    switch (paramType.getSort())
    {
    case 3: 
      return 8;
    case 2: 
      return 5;
    case 8: 
      return 7;
    case 6: 
      return 6;
    case 5: 
      return 10;
    case 7: 
      return 11;
    case 4: 
      return 9;
    case 1: 
      return 4;
    }
    return -1;
  }
  
  public static String escapeType(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    int j = paramString.length();
    while (i < j)
    {
      char c = paramString.charAt(i);
      switch (c)
      {
      case '$': 
        localStringBuffer.append("$24");
        break;
      case '.': 
        localStringBuffer.append("$2E");
        break;
      case '[': 
        localStringBuffer.append("$5B");
        break;
      case ';': 
        localStringBuffer.append("$3B");
        break;
      case '(': 
        localStringBuffer.append("$28");
        break;
      case ')': 
        localStringBuffer.append("$29");
        break;
      case '/': 
        localStringBuffer.append("$2F");
        break;
      default: 
        localStringBuffer.append(c);
      }
      i++;
    }
    return localStringBuffer.toString();
  }
  
  static
  {
    transforms.put("void", "V");
    transforms.put("byte", "B");
    transforms.put("char", "C");
    transforms.put("double", "D");
    transforms.put("float", "F");
    transforms.put("int", "I");
    transforms.put("long", "J");
    transforms.put("short", "S");
    transforms.put("boolean", "Z");
    .CollectionUtils.reverse(transforms, rtransforms);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$TypeUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */