package com.google.inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.internal.MoreTypes;
import com.google.inject.util.Types;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public class TypeLiteral
{
  final Class rawType;
  final Type type;
  final int hashCode;
  
  protected TypeLiteral()
  {
    this.type = getSuperclassTypeParameter(getClass());
    this.rawType = MoreTypes.getRawType(this.type);
    this.hashCode = this.type.hashCode();
  }
  
  TypeLiteral(Type paramType)
  {
    this.type = MoreTypes.canonicalize((Type)Preconditions.checkNotNull(paramType, "type"));
    this.rawType = MoreTypes.getRawType(this.type);
    this.hashCode = this.type.hashCode();
  }
  
  static Type getSuperclassTypeParameter(Class paramClass)
  {
    Type localType = paramClass.getGenericSuperclass();
    if ((localType instanceof Class)) {
      throw new RuntimeException("Missing type parameter.");
    }
    ParameterizedType localParameterizedType = (ParameterizedType)localType;
    return MoreTypes.canonicalize(localParameterizedType.getActualTypeArguments()[0]);
  }
  
  static TypeLiteral fromSuperclassTypeParameter(Class paramClass)
  {
    return new TypeLiteral(getSuperclassTypeParameter(paramClass));
  }
  
  public final Class getRawType()
  {
    return this.rawType;
  }
  
  public final Type getType()
  {
    return this.type;
  }
  
  final TypeLiteral providerType()
  {
    return get(Types.providerOf(getType()));
  }
  
  public final int hashCode()
  {
    return this.hashCode;
  }
  
  public final boolean equals(Object paramObject)
  {
    return ((paramObject instanceof TypeLiteral)) && (MoreTypes.equals(this.type, ((TypeLiteral)paramObject).type));
  }
  
  public final String toString()
  {
    return MoreTypes.typeToString(this.type);
  }
  
  public static TypeLiteral get(Type paramType)
  {
    return new TypeLiteral(paramType);
  }
  
  public static TypeLiteral get(Class paramClass)
  {
    return new TypeLiteral(paramClass);
  }
  
  private List resolveAll(Type[] paramArrayOfType)
  {
    TypeLiteral[] arrayOfTypeLiteral = new TypeLiteral[paramArrayOfType.length];
    for (int i = 0; i < paramArrayOfType.length; i++) {
      arrayOfTypeLiteral[i] = resolve(paramArrayOfType[i]);
    }
    return ImmutableList.copyOf(arrayOfTypeLiteral);
  }
  
  TypeLiteral resolve(Type paramType)
  {
    return get(resolveType(paramType));
  }
  
  Type resolveType(Type paramType)
  {
    Object localObject1;
    while ((paramType instanceof TypeVariable))
    {
      localObject1 = (TypeVariable)paramType;
      paramType = MoreTypes.resolveTypeVariable(this.type, this.rawType, (TypeVariable)localObject1);
      if (paramType == localObject1) {
        return paramType;
      }
    }
    Object localObject2;
    Object localObject3;
    if ((paramType instanceof GenericArrayType))
    {
      localObject1 = (GenericArrayType)paramType;
      localObject2 = ((GenericArrayType)localObject1).getGenericComponentType();
      localObject3 = resolveType((Type)localObject2);
      return (Type)(localObject2 == localObject3 ? localObject1 : Types.arrayOf((Type)localObject3));
    }
    if ((paramType instanceof ParameterizedType))
    {
      localObject1 = (ParameterizedType)paramType;
      localObject2 = ((ParameterizedType)localObject1).getOwnerType();
      localObject3 = resolveType((Type)localObject2);
      int i = localObject3 != localObject2 ? 1 : 0;
      Type[] arrayOfType = ((ParameterizedType)localObject1).getActualTypeArguments();
      int j = 0;
      int k = arrayOfType.length;
      while (j < k)
      {
        Type localType2 = resolveType(arrayOfType[j]);
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
      return i != 0 ? Types.newParameterizedTypeWithOwner((Type)localObject3, ((ParameterizedType)localObject1).getRawType(), arrayOfType) : localObject1;
    }
    if ((paramType instanceof WildcardType))
    {
      localObject1 = (WildcardType)paramType;
      localObject2 = ((WildcardType)localObject1).getLowerBounds();
      localObject3 = ((WildcardType)localObject1).getUpperBounds();
      Type localType1;
      if (localObject2.length == 1)
      {
        localType1 = resolveType(localObject2[0]);
        if (localType1 != localObject2[0]) {
          return Types.supertypeOf(localType1);
        }
      }
      else if (localObject3.length == 1)
      {
        localType1 = resolveType(localObject3[0]);
        if (localType1 != localObject3[0]) {
          return Types.subtypeOf(localType1);
        }
      }
      return (Type)localObject1;
    }
    return paramType;
  }
  
  public TypeLiteral getSupertype(Class paramClass)
  {
    Preconditions.checkArgument(paramClass.isAssignableFrom(this.rawType), "%s is not a supertype of %s", new Object[] { paramClass, this.type });
    return resolve(MoreTypes.getGenericSupertype(this.type, this.rawType, paramClass));
  }
  
  public TypeLiteral getFieldType(Field paramField)
  {
    Preconditions.checkArgument(paramField.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", new Object[] { paramField, this.type });
    return resolve(paramField.getGenericType());
  }
  
  public List getParameterTypes(Member paramMember)
  {
    Object localObject;
    Type[] arrayOfType;
    if ((paramMember instanceof Method))
    {
      localObject = (Method)paramMember;
      Preconditions.checkArgument(((Method)localObject).getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", new Object[] { localObject, this.type });
      arrayOfType = ((Method)localObject).getGenericParameterTypes();
    }
    else if ((paramMember instanceof Constructor))
    {
      localObject = (Constructor)paramMember;
      Preconditions.checkArgument(((Constructor)localObject).getDeclaringClass().isAssignableFrom(this.rawType), "%s does not construct a supertype of %s", new Object[] { localObject, this.type });
      arrayOfType = ((Constructor)localObject).getGenericParameterTypes();
    }
    else
    {
      localObject = String.valueOf(String.valueOf(paramMember));
      throw new IllegalArgumentException(31 + ((String)localObject).length() + "Not a method or a constructor: " + (String)localObject);
    }
    return resolveAll(arrayOfType);
  }
  
  public List getExceptionTypes(Member paramMember)
  {
    Object localObject;
    Type[] arrayOfType;
    if ((paramMember instanceof Method))
    {
      localObject = (Method)paramMember;
      Preconditions.checkArgument(((Method)localObject).getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", new Object[] { localObject, this.type });
      arrayOfType = ((Method)localObject).getGenericExceptionTypes();
    }
    else if ((paramMember instanceof Constructor))
    {
      localObject = (Constructor)paramMember;
      Preconditions.checkArgument(((Constructor)localObject).getDeclaringClass().isAssignableFrom(this.rawType), "%s does not construct a supertype of %s", new Object[] { localObject, this.type });
      arrayOfType = ((Constructor)localObject).getGenericExceptionTypes();
    }
    else
    {
      localObject = String.valueOf(String.valueOf(paramMember));
      throw new IllegalArgumentException(31 + ((String)localObject).length() + "Not a method or a constructor: " + (String)localObject);
    }
    return resolveAll(arrayOfType);
  }
  
  public TypeLiteral getReturnType(Method paramMethod)
  {
    Preconditions.checkArgument(paramMethod.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", new Object[] { paramMethod, this.type });
    return resolve(paramMethod.getGenericReturnType());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\TypeLiteral.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */