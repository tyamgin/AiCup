package com.google.common.reflect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

final class Types
{
  private static final Function TYPE_TO_STRING = new Function()
  {
    public String apply(Type paramAnonymousType)
    {
      return Types.toString(paramAnonymousType);
    }
  };
  private static final Joiner COMMA_JOINER = Joiner.on(", ").useForNull("null");
  
  static Type newArrayType(Type paramType)
  {
    if ((paramType instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramType;
      Type[] arrayOfType1 = localWildcardType.getLowerBounds();
      Preconditions.checkArgument(arrayOfType1.length <= 1, "Wildcard cannot have more than one lower bounds.");
      if (arrayOfType1.length == 1) {
        return supertypeOf(newArrayType(arrayOfType1[0]));
      }
      Type[] arrayOfType2 = localWildcardType.getUpperBounds();
      Preconditions.checkArgument(arrayOfType2.length == 1, "Wildcard should have only one upper bound.");
      return subtypeOf(newArrayType(arrayOfType2[0]));
    }
    return JavaVersion.CURRENT.newArrayType(paramType);
  }
  
  static ParameterizedType newParameterizedTypeWithOwner(Type paramType, Class paramClass, Type... paramVarArgs)
  {
    if (paramType == null) {
      return newParameterizedType(paramClass, paramVarArgs);
    }
    Preconditions.checkNotNull(paramVarArgs);
    Preconditions.checkArgument(paramClass.getEnclosingClass() != null, "Owner type for unenclosed %s", new Object[] { paramClass });
    return new ParameterizedTypeImpl(paramType, paramClass, paramVarArgs);
  }
  
  static ParameterizedType newParameterizedType(Class paramClass, Type... paramVarArgs)
  {
    return new ParameterizedTypeImpl(ClassOwnership.JVM_BEHAVIOR.getOwnerType(paramClass), paramClass, paramVarArgs);
  }
  
  static TypeVariable newTypeVariable(GenericDeclaration paramGenericDeclaration, String paramString, Type... paramVarArgs)
  {
    return new TypeVariableImpl(paramGenericDeclaration, paramString, paramVarArgs.length == 0 ? new Type[] { Object.class } : paramVarArgs);
  }
  
  @VisibleForTesting
  static WildcardType subtypeOf(Type paramType)
  {
    return new WildcardTypeImpl(new Type[0], new Type[] { paramType });
  }
  
  @VisibleForTesting
  static WildcardType supertypeOf(Type paramType)
  {
    return new WildcardTypeImpl(new Type[] { paramType }, new Type[] { Object.class });
  }
  
  static String toString(Type paramType)
  {
    return (paramType instanceof Class) ? ((Class)paramType).getName() : paramType.toString();
  }
  
  static Type getComponentType(Type paramType)
  {
    Preconditions.checkNotNull(paramType);
    if ((paramType instanceof Class)) {
      return ((Class)paramType).getComponentType();
    }
    if ((paramType instanceof GenericArrayType)) {
      return ((GenericArrayType)paramType).getGenericComponentType();
    }
    if ((paramType instanceof WildcardType)) {
      return subtypeOfComponentType(((WildcardType)paramType).getUpperBounds());
    }
    if ((paramType instanceof TypeVariable)) {
      return subtypeOfComponentType(((TypeVariable)paramType).getBounds());
    }
    return null;
  }
  
  private static Type subtypeOfComponentType(Type[] paramArrayOfType)
  {
    for (Type localType1 : paramArrayOfType)
    {
      Type localType2 = getComponentType(localType1);
      if (localType2 != null)
      {
        if ((localType2 instanceof Class))
        {
          Class localClass = (Class)localType2;
          if (localClass.isPrimitive()) {
            return localClass;
          }
        }
        return subtypeOf(localType2);
      }
    }
    return null;
  }
  
  static boolean containsTypeVariable(Type paramType)
  {
    if ((paramType instanceof TypeVariable)) {
      return true;
    }
    if ((paramType instanceof GenericArrayType)) {
      return containsTypeVariable(((GenericArrayType)paramType).getGenericComponentType());
    }
    if ((paramType instanceof ParameterizedType)) {
      return containsTypeVariable(((ParameterizedType)paramType).getActualTypeArguments());
    }
    if ((paramType instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramType;
      return (containsTypeVariable(localWildcardType.getUpperBounds())) || (containsTypeVariable(localWildcardType.getLowerBounds()));
    }
    return false;
  }
  
  private static boolean containsTypeVariable(Type[] paramArrayOfType)
  {
    for (Type localType : paramArrayOfType) {
      if (containsTypeVariable(localType)) {
        return true;
      }
    }
    return false;
  }
  
  private static Type[] toArray(Collection paramCollection)
  {
    return (Type[])paramCollection.toArray(new Type[paramCollection.size()]);
  }
  
  private static Iterable filterUpperBounds(Iterable paramIterable)
  {
    return Iterables.filter(paramIterable, Predicates.not(Predicates.equalTo(Object.class)));
  }
  
  private static void disallowPrimitiveType(Type[] paramArrayOfType, String paramString)
  {
    for (Type localType : paramArrayOfType) {
      if ((localType instanceof Class))
      {
        Class localClass = (Class)localType;
        Preconditions.checkArgument(!localClass.isPrimitive(), "Primitive type '%s' used as %s", new Object[] { localClass, paramString });
      }
    }
  }
  
  static Class getArrayClass(Class paramClass)
  {
    return Array.newInstance(paramClass, 0).getClass();
  }
  
  static abstract enum JavaVersion
  {
    JAVA6,  JAVA7;
    
    static final JavaVersion CURRENT = (new TypeCapture() {}.capture() instanceof Class) ? JAVA7 : JAVA6;
    
    abstract Type newArrayType(Type paramType);
    
    abstract Type usedInGenericType(Type paramType);
    
    final ImmutableList usedInGenericType(Type[] paramArrayOfType)
    {
      ImmutableList.Builder localBuilder = ImmutableList.builder();
      for (Type localType : paramArrayOfType) {
        localBuilder.add(usedInGenericType(localType));
      }
      return localBuilder.build();
    }
  }
  
  static final class WildcardTypeImpl
    implements Serializable, WildcardType
  {
    private final ImmutableList lowerBounds;
    private final ImmutableList upperBounds;
    private static final long serialVersionUID = 0L;
    
    WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
    {
      Types.disallowPrimitiveType(paramArrayOfType1, "lower bound for wildcard");
      Types.disallowPrimitiveType(paramArrayOfType2, "upper bound for wildcard");
      this.lowerBounds = Types.JavaVersion.CURRENT.usedInGenericType(paramArrayOfType1);
      this.upperBounds = Types.JavaVersion.CURRENT.usedInGenericType(paramArrayOfType2);
    }
    
    public Type[] getLowerBounds()
    {
      return Types.toArray(this.lowerBounds);
    }
    
    public Type[] getUpperBounds()
    {
      return Types.toArray(this.upperBounds);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof WildcardType))
      {
        WildcardType localWildcardType = (WildcardType)paramObject;
        return (this.lowerBounds.equals(Arrays.asList(localWildcardType.getLowerBounds()))) && (this.upperBounds.equals(Arrays.asList(localWildcardType.getUpperBounds())));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.lowerBounds.hashCode() ^ this.upperBounds.hashCode();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("?");
      Iterator localIterator = this.lowerBounds.iterator();
      Type localType;
      while (localIterator.hasNext())
      {
        localType = (Type)localIterator.next();
        localStringBuilder.append(" super ").append(Types.toString(localType));
      }
      localIterator = Types.filterUpperBounds(this.upperBounds).iterator();
      while (localIterator.hasNext())
      {
        localType = (Type)localIterator.next();
        localStringBuilder.append(" extends ").append(Types.toString(localType));
      }
      return localStringBuilder.toString();
    }
  }
  
  private static final class TypeVariableImpl
    implements TypeVariable
  {
    private final GenericDeclaration genericDeclaration;
    private final String name;
    private final ImmutableList bounds;
    
    TypeVariableImpl(GenericDeclaration paramGenericDeclaration, String paramString, Type[] paramArrayOfType)
    {
      Types.disallowPrimitiveType(paramArrayOfType, "bound for type variable");
      this.genericDeclaration = ((GenericDeclaration)Preconditions.checkNotNull(paramGenericDeclaration));
      this.name = ((String)Preconditions.checkNotNull(paramString));
      this.bounds = ImmutableList.copyOf(paramArrayOfType);
    }
    
    public Type[] getBounds()
    {
      return Types.toArray(this.bounds);
    }
    
    public GenericDeclaration getGenericDeclaration()
    {
      return this.genericDeclaration;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public String toString()
    {
      return this.name;
    }
    
    public int hashCode()
    {
      return this.genericDeclaration.hashCode() ^ this.name.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof TypeVariable))
      {
        TypeVariable localTypeVariable = (TypeVariable)paramObject;
        return (this.name.equals(localTypeVariable.getName())) && (this.genericDeclaration.equals(localTypeVariable.getGenericDeclaration()));
      }
      return false;
    }
  }
  
  private static final class ParameterizedTypeImpl
    implements Serializable, ParameterizedType
  {
    private final Type ownerType;
    private final ImmutableList argumentsList;
    private final Class rawType;
    private static final long serialVersionUID = 0L;
    
    ParameterizedTypeImpl(Type paramType, Class paramClass, Type[] paramArrayOfType)
    {
      Preconditions.checkNotNull(paramClass);
      Preconditions.checkArgument(paramArrayOfType.length == paramClass.getTypeParameters().length);
      Types.disallowPrimitiveType(paramArrayOfType, "type parameter");
      this.ownerType = paramType;
      this.rawType = paramClass;
      this.argumentsList = Types.JavaVersion.CURRENT.usedInGenericType(paramArrayOfType);
    }
    
    public Type[] getActualTypeArguments()
    {
      return Types.toArray(this.argumentsList);
    }
    
    public Type getRawType()
    {
      return this.rawType;
    }
    
    public Type getOwnerType()
    {
      return this.ownerType;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.ownerType != null) {
        localStringBuilder.append(Types.toString(this.ownerType)).append('.');
      }
      localStringBuilder.append(this.rawType.getName()).append('<').append(Types.COMMA_JOINER.join(Iterables.transform(this.argumentsList, Types.TYPE_TO_STRING))).append('>');
      return localStringBuilder.toString();
    }
    
    public int hashCode()
    {
      return (this.ownerType == null ? 0 : this.ownerType.hashCode()) ^ this.argumentsList.hashCode() ^ this.rawType.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof ParameterizedType)) {
        return false;
      }
      ParameterizedType localParameterizedType = (ParameterizedType)paramObject;
      return (getRawType().equals(localParameterizedType.getRawType())) && (Objects.equal(getOwnerType(), localParameterizedType.getOwnerType())) && (Arrays.equals(getActualTypeArguments(), localParameterizedType.getActualTypeArguments()));
    }
  }
  
  private static final class GenericArrayTypeImpl
    implements Serializable, GenericArrayType
  {
    private final Type componentType;
    private static final long serialVersionUID = 0L;
    
    GenericArrayTypeImpl(Type paramType)
    {
      this.componentType = Types.JavaVersion.CURRENT.usedInGenericType(paramType);
    }
    
    public Type getGenericComponentType()
    {
      return this.componentType;
    }
    
    public String toString()
    {
      return Types.toString(this.componentType) + "[]";
    }
    
    public int hashCode()
    {
      return this.componentType.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof GenericArrayType))
      {
        GenericArrayType localGenericArrayType = (GenericArrayType)paramObject;
        return Objects.equal(getGenericComponentType(), localGenericArrayType.getGenericComponentType());
      }
      return false;
    }
  }
  
  private static abstract enum ClassOwnership
  {
    OWNED_BY_ENCLOSING_CLASS,  LOCAL_CLASS_HAS_NO_OWNER;
    
    static final ClassOwnership JVM_BEHAVIOR = detectJvmBehavior();
    
    abstract Class getOwnerType(Class paramClass);
    
    private static ClassOwnership detectJvmBehavior()
    {
      Class localClass = new 1LocalClass() {}.getClass();
      ParameterizedType localParameterizedType = (ParameterizedType)localClass.getGenericSuperclass();
      for (ClassOwnership localClassOwnership : values()) {
        if (localClassOwnership.getOwnerType(1LocalClass.class) == localParameterizedType.getOwnerType()) {
          return localClassOwnership;
        }
      }
      throw new AssertionError();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\Types.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */