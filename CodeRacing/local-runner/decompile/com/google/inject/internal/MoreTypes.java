package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.inject.Provider;

public class MoreTypes
{
  public static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
  private static final Map PRIMITIVE_TO_WRAPPER = new ImmutableMap.Builder().put(TypeLiteral.get(Boolean.TYPE), TypeLiteral.get(Boolean.class)).put(TypeLiteral.get(Byte.TYPE), TypeLiteral.get(Byte.class)).put(TypeLiteral.get(Short.TYPE), TypeLiteral.get(Short.class)).put(TypeLiteral.get(Integer.TYPE), TypeLiteral.get(Integer.class)).put(TypeLiteral.get(Long.TYPE), TypeLiteral.get(Long.class)).put(TypeLiteral.get(Float.TYPE), TypeLiteral.get(Float.class)).put(TypeLiteral.get(Double.TYPE), TypeLiteral.get(Double.class)).put(TypeLiteral.get(Character.TYPE), TypeLiteral.get(Character.class)).put(TypeLiteral.get(Void.TYPE), TypeLiteral.get(Void.class)).build();
  
  public static Key canonicalizeKey(Key paramKey)
  {
    if (paramKey.getClass() == Key.class) {
      return paramKey;
    }
    if (paramKey.getAnnotation() != null) {
      return Key.get(paramKey.getTypeLiteral(), paramKey.getAnnotation());
    }
    if (paramKey.getAnnotationType() != null) {
      return Key.get(paramKey.getTypeLiteral(), paramKey.getAnnotationType());
    }
    return Key.get(paramKey.getTypeLiteral());
  }
  
  public static TypeLiteral canonicalizeForKey(TypeLiteral paramTypeLiteral)
  {
    Type localType = paramTypeLiteral.getType();
    if (!isFullySpecified(localType))
    {
      localObject = new Errors().keyNotFullySpecified(paramTypeLiteral);
      throw new ConfigurationException(((Errors)localObject).getMessages());
    }
    if (paramTypeLiteral.getRawType() == Provider.class)
    {
      localObject = (ParameterizedType)localType;
      localTypeLiteral = TypeLiteral.get(Types.providerOf(localObject.getActualTypeArguments()[0]));
      return localTypeLiteral;
    }
    Object localObject = (TypeLiteral)PRIMITIVE_TO_WRAPPER.get(paramTypeLiteral);
    if (localObject != null) {
      return (TypeLiteral)localObject;
    }
    if (paramTypeLiteral.getClass() == TypeLiteral.class) {
      return paramTypeLiteral;
    }
    TypeLiteral localTypeLiteral = TypeLiteral.get(paramTypeLiteral.getType());
    return localTypeLiteral;
  }
  
  private static boolean isFullySpecified(Type paramType)
  {
    if ((paramType instanceof Class)) {
      return true;
    }
    if ((paramType instanceof CompositeType)) {
      return ((CompositeType)paramType).isFullySpecified();
    }
    if ((paramType instanceof TypeVariable)) {
      return false;
    }
    return ((CompositeType)canonicalize(paramType)).isFullySpecified();
  }
  
  public static Type canonicalize(Type paramType)
  {
    Object localObject;
    if ((paramType instanceof Class))
    {
      localObject = (Class)paramType;
      return (Type)(((Class)localObject).isArray() ? new GenericArrayTypeImpl(canonicalize(((Class)localObject).getComponentType())) : localObject);
    }
    if ((paramType instanceof CompositeType)) {
      return paramType;
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
      localObject1 = (ParameterizedType)paramType;
      localObject2 = ((ParameterizedType)localObject1).getRawType();
      Preconditions.checkArgument(localObject2 instanceof Class, "Expected a Class, but <%s> is of type %s", new Object[] { paramType, paramType.getClass().getName() });
      return (Class)localObject2;
    }
    if ((paramType instanceof GenericArrayType))
    {
      localObject1 = ((GenericArrayType)paramType).getGenericComponentType();
      return Array.newInstance(getRawType((Type)localObject1), 0).getClass();
    }
    if ((paramType instanceof TypeVariable)) {
      return Object.class;
    }
    Object localObject1 = String.valueOf(String.valueOf("Expected a Class, ParameterizedType, or GenericArrayType, but <"));
    Object localObject2 = String.valueOf(String.valueOf(paramType));
    String str = String.valueOf(String.valueOf(paramType.getClass().getName()));
    throw new IllegalArgumentException(13 + ((String)localObject1).length() + ((String)localObject2).length() + str.length() + (String)localObject1 + (String)localObject2 + "> is of type " + str);
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
      return (Objects.equal(((ParameterizedType)localObject1).getOwnerType(), ((ParameterizedType)localObject2).getOwnerType())) && (((ParameterizedType)localObject1).getRawType().equals(((ParameterizedType)localObject2).getRawType())) && (Arrays.equals(((ParameterizedType)localObject1).getActualTypeArguments(), ((ParameterizedType)localObject2).getActualTypeArguments()));
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
      return (((TypeVariable)localObject1).getGenericDeclaration().equals(((TypeVariable)localObject2).getGenericDeclaration())) && (((TypeVariable)localObject1).getName().equals(((TypeVariable)localObject2).getName()));
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
  
  public static Type getGenericSupertype(Type paramType, Class paramClass1, Class paramClass2)
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
  
  public static Type resolveTypeVariable(Type paramType, Class paramClass, TypeVariable paramTypeVariable)
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
  
  private static void checkNotPrimitive(Type paramType, String paramString)
  {
    Preconditions.checkArgument((!(paramType instanceof Class)) || (!((Class)paramType).isPrimitive()), "Primitive types are not allowed in %s: %s", new Object[] { paramString, paramType });
  }
  
  private static abstract interface CompositeType
  {
    public abstract boolean isFullySpecified();
  }
  
  public static class WildcardTypeImpl
    implements MoreTypes.CompositeType, Serializable, WildcardType
  {
    private final Type upperBound;
    private final Type lowerBound;
    private static final long serialVersionUID = 0L;
    
    public WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
    {
      Preconditions.checkArgument(paramArrayOfType2.length <= 1, "Must have at most one lower bound.");
      Preconditions.checkArgument(paramArrayOfType1.length == 1, "Must have exactly one upper bound.");
      if (paramArrayOfType2.length == 1)
      {
        Preconditions.checkNotNull(paramArrayOfType2[0], "lowerBound");
        MoreTypes.checkNotPrimitive(paramArrayOfType2[0], "wildcard bounds");
        Preconditions.checkArgument(paramArrayOfType1[0] == Object.class, "bounded both ways");
        this.lowerBound = MoreTypes.canonicalize(paramArrayOfType2[0]);
        this.upperBound = Object.class;
      }
      else
      {
        Preconditions.checkNotNull(paramArrayOfType1[0], "upperBound");
        MoreTypes.checkNotPrimitive(paramArrayOfType1[0], "wildcard bounds");
        this.lowerBound = null;
        this.upperBound = MoreTypes.canonicalize(paramArrayOfType1[0]);
      }
    }
    
    public Type[] getUpperBounds()
    {
      return new Type[] { this.upperBound };
    }
    
    public Type[] getLowerBounds()
    {
      return this.lowerBound != null ? new Type[] { this.lowerBound } : MoreTypes.EMPTY_TYPE_ARRAY;
    }
    
    public boolean isFullySpecified()
    {
      return (MoreTypes.isFullySpecified(this.upperBound)) && ((this.lowerBound == null) || (MoreTypes.isFullySpecified(this.lowerBound)));
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof WildcardType)) && (MoreTypes.equals(this, (WildcardType)paramObject));
    }
    
    public int hashCode()
    {
      return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
    }
    
    /* Error */
    public String toString()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 23	com/google/inject/internal/MoreTypes$WildcardTypeImpl:lowerBound	Ljava/lang/reflect/Type;
      //   4: ifnull +38 -> 42
      //   7: ldc 3
      //   9: aload_0
      //   10: getfield 23	com/google/inject/internal/MoreTypes$WildcardTypeImpl:lowerBound	Ljava/lang/reflect/Type;
      //   13: invokestatic 31	com/google/inject/internal/MoreTypes:typeToString	(Ljava/lang/reflect/Type;)Ljava/lang/String;
      //   16: invokestatic 37	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   19: dup
      //   20: invokevirtual 36	java/lang/String:length	()I
      //   23: ifeq +9 -> 32
      //   26: invokevirtual 35	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
      //   29: goto +12 -> 41
      //   32: pop
      //   33: new 17	java/lang/String
      //   36: dup_x1
      //   37: swap
      //   38: invokespecial 34	java/lang/String:<init>	(Ljava/lang/String;)V
      //   41: areturn
      //   42: aload_0
      //   43: getfield 24	com/google/inject/internal/MoreTypes$WildcardTypeImpl:upperBound	Ljava/lang/reflect/Type;
      //   46: ldc 16
      //   48: if_acmpne +6 -> 54
      //   51: ldc 1
      //   53: areturn
      //   54: ldc 2
      //   56: aload_0
      //   57: getfield 24	com/google/inject/internal/MoreTypes$WildcardTypeImpl:upperBound	Ljava/lang/reflect/Type;
      //   60: invokestatic 31	com/google/inject/internal/MoreTypes:typeToString	(Ljava/lang/reflect/Type;)Ljava/lang/String;
      //   63: invokestatic 37	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   66: dup
      //   67: invokevirtual 36	java/lang/String:length	()I
      //   70: ifeq +9 -> 79
      //   73: invokevirtual 35	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
      //   76: goto +12 -> 88
      //   79: pop
      //   80: new 17	java/lang/String
      //   83: dup_x1
      //   84: swap
      //   85: invokespecial 34	java/lang/String:<init>	(Ljava/lang/String;)V
      //   88: areturn
    }
  }
  
  public static class GenericArrayTypeImpl
    implements MoreTypes.CompositeType, Serializable, GenericArrayType
  {
    private final Type componentType;
    private static final long serialVersionUID = 0L;
    
    public GenericArrayTypeImpl(Type paramType)
    {
      this.componentType = MoreTypes.canonicalize(paramType);
    }
    
    public Type getGenericComponentType()
    {
      return this.componentType;
    }
    
    public boolean isFullySpecified()
    {
      return MoreTypes.isFullySpecified(this.componentType);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof GenericArrayType)) && (MoreTypes.equals(this, (GenericArrayType)paramObject));
    }
    
    public int hashCode()
    {
      return this.componentType.hashCode();
    }
    
    public String toString()
    {
      return String.valueOf(MoreTypes.typeToString(this.componentType)).concat("[]");
    }
  }
  
  public static class ParameterizedTypeImpl
    implements MoreTypes.CompositeType, Serializable, ParameterizedType
  {
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;
    private static final long serialVersionUID = 0L;
    
    public ParameterizedTypeImpl(Type paramType1, Type paramType2, Type... paramVarArgs)
    {
      ensureOwnerType(paramType1, paramType2);
      this.ownerType = (paramType1 == null ? null : MoreTypes.canonicalize(paramType1));
      this.rawType = MoreTypes.canonicalize(paramType2);
      this.typeArguments = ((Type[])paramVarArgs.clone());
      for (int i = 0; i < this.typeArguments.length; i++)
      {
        Preconditions.checkNotNull(this.typeArguments[i], "type parameter");
        MoreTypes.checkNotPrimitive(this.typeArguments[i], "type parameters");
        this.typeArguments[i] = MoreTypes.canonicalize(this.typeArguments[i]);
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
    
    public boolean isFullySpecified()
    {
      if ((this.ownerType != null) && (!MoreTypes.isFullySpecified(this.ownerType))) {
        return false;
      }
      if (!MoreTypes.isFullySpecified(this.rawType)) {
        return false;
      }
      for (Type localType : this.typeArguments) {
        if (!MoreTypes.isFullySpecified(localType)) {
          return false;
        }
      }
      return true;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof ParameterizedType)) && (MoreTypes.equals(this, (ParameterizedType)paramObject));
    }
    
    public int hashCode()
    {
      return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ MoreTypes.hashCodeOrZero(this.ownerType);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
      localStringBuilder.append(MoreTypes.typeToString(this.rawType));
      if (this.typeArguments.length == 0) {
        return localStringBuilder.toString();
      }
      localStringBuilder.append("<").append(MoreTypes.typeToString(this.typeArguments[0]));
      for (int i = 1; i < this.typeArguments.length; i++) {
        localStringBuilder.append(", ").append(MoreTypes.typeToString(this.typeArguments[i]));
      }
      return ">";
    }
    
    private static void ensureOwnerType(Type paramType1, Type paramType2)
    {
      if ((paramType2 instanceof Class))
      {
        Class localClass = (Class)paramType2;
        Preconditions.checkArgument((paramType1 != null) || (localClass.getEnclosingClass() == null), "No owner type for enclosed %s", new Object[] { paramType2 });
        Preconditions.checkArgument((paramType1 == null) || (localClass.getEnclosingClass() != null), "Owner type for unenclosed %s", new Object[] { paramType2 });
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\MoreTypes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */