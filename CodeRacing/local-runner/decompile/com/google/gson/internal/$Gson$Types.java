package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public final class $Gson$Types
{
  static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
  
  public static ParameterizedType newParameterizedTypeWithOwner(Type paramType1, Type paramType2, Type... paramVarArgs)
  {
    return new ParameterizedTypeImpl(paramType1, paramType2, paramVarArgs);
  }
  
  public static GenericArrayType arrayOf(Type paramType)
  {
    return new GenericArrayTypeImpl(paramType);
  }
  
  public static WildcardType subtypeOf(Type paramType)
  {
    return new WildcardTypeImpl(new Type[] { paramType }, EMPTY_TYPE_ARRAY);
  }
  
  public static WildcardType supertypeOf(Type paramType)
  {
    return new WildcardTypeImpl(new Type[] { Object.class }, new Type[] { paramType });
  }
  
  public static Type canonicalize(Type paramType)
  {
    Object localObject;
    if ((paramType instanceof Class))
    {
      localObject = (Class)paramType;
      return (Type)(((Class)localObject).isArray() ? new GenericArrayTypeImpl(canonicalize(((Class)localObject).getComponentType())) : localObject);
    }
    if ((paramType instanceof ParameterizedType))
    {
      localObject = (ParameterizedType)paramType;
      return new ParameterizedTypeImpl(((ParameterizedType)localObject).getOwnerType(), ((ParameterizedType)localObject).getRawType(), ((ParameterizedType)localObject).getActualTypeArguments());
    }
    if ((paramType instanceof GenericArrayType))
    {
      localObject = (GenericArrayType)paramType;
      return new GenericArrayTypeImpl(((GenericArrayType)localObject).getGenericComponentType());
    }
    if ((paramType instanceof WildcardType))
    {
      localObject = (WildcardType)paramType;
      return new WildcardTypeImpl(((WildcardType)localObject).getUpperBounds(), ((WildcardType)localObject).getLowerBounds());
    }
    return paramType;
  }
  
  public static Class getRawType(Type paramType)
  {
    if ((paramType instanceof Class)) {
      return (Class)paramType;
    }
    if ((paramType instanceof ParameterizedType))
    {
      localObject = (ParameterizedType)paramType;
      Type localType = ((ParameterizedType)localObject).getRawType();
      .Gson.Preconditions.checkArgument(localType instanceof Class);
      return (Class)localType;
    }
    if ((paramType instanceof GenericArrayType))
    {
      localObject = ((GenericArrayType)paramType).getGenericComponentType();
      return Array.newInstance(getRawType((Type)localObject), 0).getClass();
    }
    if ((paramType instanceof TypeVariable)) {
      return Object.class;
    }
    if ((paramType instanceof WildcardType)) {
      return getRawType(((WildcardType)paramType).getUpperBounds()[0]);
    }
    Object localObject = paramType == null ? "null" : paramType.getClass().getName();
    throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + paramType + "> is of type " + (String)localObject);
  }
  
  static boolean equal(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }
  
  public static boolean equals(Type paramType1, Type paramType2)
  {
    if (paramType1 == paramType2) {
      return true;
    }
    if ((paramType1 instanceof Class)) {
      return paramType1.equals(paramType2);
    }
    Object localObject1;
    Object localObject2;
    if ((paramType1 instanceof ParameterizedType))
    {
      if (!(paramType2 instanceof ParameterizedType)) {
        return false;
      }
      localObject1 = (ParameterizedType)paramType1;
      localObject2 = (ParameterizedType)paramType2;
      return (equal(((ParameterizedType)localObject1).getOwnerType(), ((ParameterizedType)localObject2).getOwnerType())) && (((ParameterizedType)localObject1).getRawType().equals(((ParameterizedType)localObject2).getRawType())) && (Arrays.equals(((ParameterizedType)localObject1).getActualTypeArguments(), ((ParameterizedType)localObject2).getActualTypeArguments()));
    }
    if ((paramType1 instanceof GenericArrayType))
    {
      if (!(paramType2 instanceof GenericArrayType)) {
        return false;
      }
      localObject1 = (GenericArrayType)paramType1;
      localObject2 = (GenericArrayType)paramType2;
      return equals(((GenericArrayType)localObject1).getGenericComponentType(), ((GenericArrayType)localObject2).getGenericComponentType());
    }
    if ((paramType1 instanceof WildcardType))
    {
      if (!(paramType2 instanceof WildcardType)) {
        return false;
      }
      localObject1 = (WildcardType)paramType1;
      localObject2 = (WildcardType)paramType2;
      return (Arrays.equals(((WildcardType)localObject1).getUpperBounds(), ((WildcardType)localObject2).getUpperBounds())) && (Arrays.equals(((WildcardType)localObject1).getLowerBounds(), ((WildcardType)localObject2).getLowerBounds()));
    }
    if ((paramType1 instanceof TypeVariable))
    {
      if (!(paramType2 instanceof TypeVariable)) {
        return false;
      }
      localObject1 = (TypeVariable)paramType1;
      localObject2 = (TypeVariable)paramType2;
      return (((TypeVariable)localObject1).getGenericDeclaration() == ((TypeVariable)localObject2).getGenericDeclaration()) && (((TypeVariable)localObject1).getName().equals(((TypeVariable)localObject2).getName()));
    }
    return false;
  }
  
  private static int hashCodeOrZero(Object paramObject)
  {
    return paramObject != null ? paramObject.hashCode() : 0;
  }
  
  public static String typeToString(Type paramType)
  {
    return (paramType instanceof Class) ? ((Class)paramType).getName() : paramType.toString();
  }
  
  static Type getGenericSupertype(Type paramType, Class paramClass1, Class paramClass2)
  {
    if (paramClass2 == paramClass1) {
      return paramType;
    }
    Object localObject;
    if (paramClass2.isInterface())
    {
      localObject = paramClass1.getInterfaces();
      int i = 0;
      int j = localObject.length;
      while (i < j)
      {
        if (localObject[i] == paramClass2) {
          return paramClass1.getGenericInterfaces()[i];
        }
        if (paramClass2.isAssignableFrom(localObject[i])) {
          return getGenericSupertype(paramClass1.getGenericInterfaces()[i], localObject[i], paramClass2);
        }
        i++;
      }
    }
    if (!paramClass1.isInterface()) {
      while (paramClass1 != Object.class)
      {
        localObject = paramClass1.getSuperclass();
        if (localObject == paramClass2) {
          return paramClass1.getGenericSuperclass();
        }
        if (paramClass2.isAssignableFrom((Class)localObject)) {
          return getGenericSupertype(paramClass1.getGenericSuperclass(), (Class)localObject, paramClass2);
        }
        paramClass1 = (Class)localObject;
      }
    }
    return paramClass2;
  }
  
  static Type getSupertype(Type paramType, Class paramClass1, Class paramClass2)
  {
    .Gson.Preconditions.checkArgument(paramClass2.isAssignableFrom(paramClass1));
    return resolve(paramType, paramClass1, getGenericSupertype(paramType, paramClass1, paramClass2));
  }
  
  public static Type getArrayComponentType(Type paramType)
  {
    return (paramType instanceof GenericArrayType) ? ((GenericArrayType)paramType).getGenericComponentType() : ((Class)paramType).getComponentType();
  }
  
  public static Type getCollectionElementType(Type paramType, Class paramClass)
  {
    Type localType = getSupertype(paramType, paramClass, Collection.class);
    if ((localType instanceof WildcardType)) {
      localType = ((WildcardType)localType).getUpperBounds()[0];
    }
    if ((localType instanceof ParameterizedType)) {
      return ((ParameterizedType)localType).getActualTypeArguments()[0];
    }
    return Object.class;
  }
  
  public static Type[] getMapKeyAndValueTypes(Type paramType, Class paramClass)
  {
    if (paramType == Properties.class) {
      return new Type[] { String.class, String.class };
    }
    Type localType = getSupertype(paramType, paramClass, Map.class);
    if ((localType instanceof ParameterizedType))
    {
      ParameterizedType localParameterizedType = (ParameterizedType)localType;
      return localParameterizedType.getActualTypeArguments();
    }
    return new Type[] { Object.class, Object.class };
  }
  
  public static Type resolve(Type paramType1, Class paramClass, Type paramType2)
  {
    Object localObject1;
    while ((paramType2 instanceof TypeVariable))
    {
      localObject1 = (TypeVariable)paramType2;
      paramType2 = resolveTypeVariable(paramType1, paramClass, (TypeVariable)localObject1);
      if (paramType2 == localObject1) {
        return paramType2;
      }
    }
    Object localObject2;
    Object localObject3;
    if (((paramType2 instanceof Class)) && (((Class)paramType2).isArray()))
    {
      localObject1 = (Class)paramType2;
      localObject2 = ((Class)localObject1).getComponentType();
      localObject3 = resolve(paramType1, paramClass, (Type)localObject2);
      return (Type)(localObject2 == localObject3 ? localObject1 : arrayOf((Type)localObject3));
    }
    if ((paramType2 instanceof GenericArrayType))
    {
      localObject1 = (GenericArrayType)paramType2;
      localObject2 = ((GenericArrayType)localObject1).getGenericComponentType();
      localObject3 = resolve(paramType1, paramClass, (Type)localObject2);
      return (Type)(localObject2 == localObject3 ? localObject1 : arrayOf((Type)localObject3));
    }
    if ((paramType2 instanceof ParameterizedType))
    {
      localObject1 = (ParameterizedType)paramType2;
      localObject2 = ((ParameterizedType)localObject1).getOwnerType();
      localObject3 = resolve(paramType1, paramClass, (Type)localObject2);
      int i = localObject3 != localObject2 ? 1 : 0;
      Type[] arrayOfType = ((ParameterizedType)localObject1).getActualTypeArguments();
      int j = 0;
      int k = arrayOfType.length;
      while (j < k)
      {
        Type localType2 = resolve(paramType1, paramClass, arrayOfType[j]);
        if (localType2 != arrayOfType[j])
        {
          if (i == 0)
          {
            arrayOfType = (Type[])arrayOfType.clone();
            i = 1;
          }
          arrayOfType[j] = localType2;
        }
        j++;
      }
      return i != 0 ? newParameterizedTypeWithOwner((Type)localObject3, ((ParameterizedType)localObject1).getRawType(), arrayOfType) : localObject1;
    }
    if ((paramType2 instanceof WildcardType))
    {
      localObject1 = (WildcardType)paramType2;
      localObject2 = ((WildcardType)localObject1).getLowerBounds();
      localObject3 = ((WildcardType)localObject1).getUpperBounds();
      Type localType1;
      if (localObject2.length == 1)
      {
        localType1 = resolve(paramType1, paramClass, localObject2[0]);
        if (localType1 != localObject2[0]) {
          return supertypeOf(localType1);
        }
      }
      else if (localObject3.length == 1)
      {
        localType1 = resolve(paramType1, paramClass, localObject3[0]);
        if (localType1 != localObject3[0]) {
          return subtypeOf(localType1);
        }
      }
      return (Type)localObject1;
    }
    return paramType2;
  }
  
  static Type resolveTypeVariable(Type paramType, Class paramClass, TypeVariable paramTypeVariable)
  {
    Class localClass = declaringClassOf(paramTypeVariable);
    if (localClass == null) {
      return paramTypeVariable;
    }
    Type localType = getGenericSupertype(paramType, paramClass, localClass);
    if ((localType instanceof ParameterizedType))
    {
      int i = indexOf(localClass.getTypeParameters(), paramTypeVariable);
      return ((ParameterizedType)localType).getActualTypeArguments()[i];
    }
    return paramTypeVariable;
  }
  
  private static int indexOf(Object[] paramArrayOfObject, Object paramObject)
  {
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      if (paramObject.equals(paramArrayOfObject[i])) {
        return i;
      }
    }
    throw new NoSuchElementException();
  }
  
  private static Class declaringClassOf(TypeVariable paramTypeVariable)
  {
    GenericDeclaration localGenericDeclaration = paramTypeVariable.getGenericDeclaration();
    return (localGenericDeclaration instanceof Class) ? (Class)localGenericDeclaration : null;
  }
  
  private static void checkNotPrimitive(Type paramType)
  {
    .Gson.Preconditions.checkArgument((!(paramType instanceof Class)) || (!((Class)paramType).isPrimitive()));
  }
  
  private static final class WildcardTypeImpl
    implements Serializable, WildcardType
  {
    private final Type upperBound;
    private final Type lowerBound;
    private static final long serialVersionUID = 0L;
    
    public WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
    {
      .Gson.Preconditions.checkArgument(paramArrayOfType2.length <= 1);
      .Gson.Preconditions.checkArgument(paramArrayOfType1.length == 1);
      if (paramArrayOfType2.length == 1)
      {
        .Gson.Preconditions.checkNotNull(paramArrayOfType2[0]);
        .Gson.Types.checkNotPrimitive(paramArrayOfType2[0]);
        .Gson.Preconditions.checkArgument(paramArrayOfType1[0] == Object.class);
        this.lowerBound = .Gson.Types.canonicalize(paramArrayOfType2[0]);
        this.upperBound = Object.class;
      }
      else
      {
        .Gson.Preconditions.checkNotNull(paramArrayOfType1[0]);
        .Gson.Types.checkNotPrimitive(paramArrayOfType1[0]);
        this.lowerBound = null;
        this.upperBound = .Gson.Types.canonicalize(paramArrayOfType1[0]);
      }
    }
    
    public Type[] getUpperBounds()
    {
      return new Type[] { this.upperBound };
    }
    
    public Type[] getLowerBounds()
    {
      return this.lowerBound != null ? new Type[] { this.lowerBound } : .Gson.Types.EMPTY_TYPE_ARRAY;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof WildcardType)) && (.Gson.Types.equals(this, (WildcardType)paramObject));
    }
    
    public int hashCode()
    {
      return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
    }
    
    public String toString()
    {
      if (this.lowerBound != null) {
        return "? super " + .Gson.Types.typeToString(this.lowerBound);
      }
      if (this.upperBound == Object.class) {
        return "?";
      }
      return "? extends " + .Gson.Types.typeToString(this.upperBound);
    }
  }
  
  private static final class GenericArrayTypeImpl
    implements Serializable, GenericArrayType
  {
    private final Type componentType;
    private static final long serialVersionUID = 0L;
    
    public GenericArrayTypeImpl(Type paramType)
    {
      this.componentType = .Gson.Types.canonicalize(paramType);
    }
    
    public Type getGenericComponentType()
    {
      return this.componentType;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof GenericArrayType)) && (.Gson.Types.equals(this, (GenericArrayType)paramObject));
    }
    
    public int hashCode()
    {
      return this.componentType.hashCode();
    }
    
    public String toString()
    {
      return .Gson.Types.typeToString(this.componentType) + "[]";
    }
  }
  
  private static final class ParameterizedTypeImpl
    implements Serializable, ParameterizedType
  {
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;
    private static final long serialVersionUID = 0L;
    
    public ParameterizedTypeImpl(Type paramType1, Type paramType2, Type... paramVarArgs)
    {
      if ((paramType2 instanceof Class))
      {
        Class localClass = (Class)paramType2;
        int j = (Modifier.isStatic(localClass.getModifiers())) || (localClass.getEnclosingClass() == null) ? 1 : 0;
        .Gson.Preconditions.checkArgument((paramType1 != null) || (j != 0));
      }
      this.ownerType = (paramType1 == null ? null : .Gson.Types.canonicalize(paramType1));
      this.rawType = .Gson.Types.canonicalize(paramType2);
      this.typeArguments = ((Type[])paramVarArgs.clone());
      for (int i = 0; i < this.typeArguments.length; i++)
      {
        .Gson.Preconditions.checkNotNull(this.typeArguments[i]);
        .Gson.Types.checkNotPrimitive(this.typeArguments[i]);
        this.typeArguments[i] = .Gson.Types.canonicalize(this.typeArguments[i]);
      }
    }
    
    public Type[] getActualTypeArguments()
    {
      return (Type[])this.typeArguments.clone();
    }
    
    public Type getRawType()
    {
      return this.rawType;
    }
    
    public Type getOwnerType()
    {
      return this.ownerType;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof ParameterizedType)) && (.Gson.Types.equals(this, (ParameterizedType)paramObject));
    }
    
    public int hashCode()
    {
      return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ .Gson.Types.hashCodeOrZero(this.ownerType);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
      localStringBuilder.append(.Gson.Types.typeToString(this.rawType));
      if (this.typeArguments.length == 0) {
        return localStringBuilder.toString();
      }
      localStringBuilder.append("<").append(.Gson.Types.typeToString(this.typeArguments[0]));
      for (int i = 1; i < this.typeArguments.length; i++) {
        localStringBuilder.append(", ").append(.Gson.Types.typeToString(this.typeArguments[i]));
      }
      return ">";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\$Gson$Types.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */