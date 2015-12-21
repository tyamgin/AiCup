package com.google.gson.reflect;

import com.google.gson.internal..Gson.Preconditions;
import com.google.gson.internal..Gson.Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class TypeToken
{
  final Class rawType;
  final Type type;
  final int hashCode;
  
  protected TypeToken()
  {
    this.type = getSuperclassTypeParameter(getClass());
    this.rawType = .Gson.Types.getRawType(this.type);
    this.hashCode = this.type.hashCode();
  }
  
  TypeToken(Type paramType)
  {
    this.type = .Gson.Types.canonicalize((Type).Gson.Preconditions.checkNotNull(paramType));
    this.rawType = .Gson.Types.getRawType(this.type);
    this.hashCode = this.type.hashCode();
  }
  
  static Type getSuperclassTypeParameter(Class paramClass)
  {
    Type localType = paramClass.getGenericSuperclass();
    if ((localType instanceof Class)) {
      throw new RuntimeException("Missing type parameter.");
    }
    ParameterizedType localParameterizedType = (ParameterizedType)localType;
    return .Gson.Types.canonicalize(localParameterizedType.getActualTypeArguments()[0]);
  }
  
  public final Class getRawType()
  {
    return this.rawType;
  }
  
  public final Type getType()
  {
    return this.type;
  }
  
  @Deprecated
  public boolean isAssignableFrom(Class paramClass)
  {
    return isAssignableFrom(paramClass);
  }
  
  @Deprecated
  public boolean isAssignableFrom(Type paramType)
  {
    if (paramType == null) {
      return false;
    }
    if (this.type.equals(paramType)) {
      return true;
    }
    if ((this.type instanceof Class)) {
      return this.rawType.isAssignableFrom(.Gson.Types.getRawType(paramType));
    }
    if ((this.type instanceof ParameterizedType)) {
      return isAssignableFrom(paramType, (ParameterizedType)this.type, new HashMap());
    }
    if ((this.type instanceof GenericArrayType)) {
      return (this.rawType.isAssignableFrom(.Gson.Types.getRawType(paramType))) && (isAssignableFrom(paramType, (GenericArrayType)this.type));
    }
    throw buildUnexpectedTypeError(this.type, new Class[] { Class.class, ParameterizedType.class, GenericArrayType.class });
  }
  
  @Deprecated
  public boolean isAssignableFrom(TypeToken paramTypeToken)
  {
    return isAssignableFrom(paramTypeToken.getType());
  }
  
  private static boolean isAssignableFrom(Type paramType, GenericArrayType paramGenericArrayType)
  {
    Type localType = paramGenericArrayType.getGenericComponentType();
    if ((localType instanceof ParameterizedType))
    {
      Object localObject = paramType;
      if ((paramType instanceof GenericArrayType))
      {
        localObject = ((GenericArrayType)paramType).getGenericComponentType();
      }
      else if ((paramType instanceof Class))
      {
        for (Class localClass = (Class)paramType; localClass.isArray(); localClass = localClass.getComponentType()) {}
        localObject = localClass;
      }
      return isAssignableFrom((Type)localObject, (ParameterizedType)localType, new HashMap());
    }
    return true;
  }
  
  private static boolean isAssignableFrom(Type paramType, ParameterizedType paramParameterizedType, Map paramMap)
  {
    if (paramType == null) {
      return false;
    }
    if (paramParameterizedType.equals(paramType)) {
      return true;
    }
    Class localClass = .Gson.Types.getRawType(paramType);
    ParameterizedType localParameterizedType = null;
    if ((paramType instanceof ParameterizedType)) {
      localParameterizedType = (ParameterizedType)paramType;
    }
    int j;
    Type localType;
    if (localParameterizedType != null)
    {
      localObject = localParameterizedType.getActualTypeArguments();
      TypeVariable[] arrayOfTypeVariable = localClass.getTypeParameters();
      for (j = 0; j < localObject.length; j++)
      {
        localType = localObject[j];
        TypeVariable localTypeVariable1 = arrayOfTypeVariable[j];
        while ((localType instanceof TypeVariable))
        {
          TypeVariable localTypeVariable2 = (TypeVariable)localType;
          localType = (Type)paramMap.get(localTypeVariable2.getName());
        }
        paramMap.put(localTypeVariable1.getName(), localType);
      }
      if (typeEquals(localParameterizedType, paramParameterizedType, paramMap)) {
        return true;
      }
    }
    for (localType : localClass.getGenericInterfaces()) {
      if (isAssignableFrom(localType, paramParameterizedType, new HashMap(paramMap))) {
        return true;
      }
    }
    Object localObject = localClass.getGenericSuperclass();
    return isAssignableFrom((Type)localObject, paramParameterizedType, new HashMap(paramMap));
  }
  
  private static boolean typeEquals(ParameterizedType paramParameterizedType1, ParameterizedType paramParameterizedType2, Map paramMap)
  {
    if (paramParameterizedType1.getRawType().equals(paramParameterizedType2.getRawType()))
    {
      Type[] arrayOfType1 = paramParameterizedType1.getActualTypeArguments();
      Type[] arrayOfType2 = paramParameterizedType2.getActualTypeArguments();
      for (int i = 0; i < arrayOfType1.length; i++) {
        if (!matches(arrayOfType1[i], arrayOfType2[i], paramMap)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  private static AssertionError buildUnexpectedTypeError(Type paramType, Class... paramVarArgs)
  {
    StringBuilder localStringBuilder = new StringBuilder("Unexpected type. Expected one of: ");
    for (Class localClass : paramVarArgs) {
      localStringBuilder.append(localClass.getName()).append(", ");
    }
    localStringBuilder.append("but got: ").append(paramType.getClass().getName()).append(", for type token: ").append(paramType.toString()).append('.');
    return new AssertionError(localStringBuilder.toString());
  }
  
  private static boolean matches(Type paramType1, Type paramType2, Map paramMap)
  {
    return (paramType2.equals(paramType1)) || (((paramType1 instanceof TypeVariable)) && (paramType2.equals(paramMap.get(((TypeVariable)paramType1).getName()))));
  }
  
  public final int hashCode()
  {
    return this.hashCode;
  }
  
  public final boolean equals(Object paramObject)
  {
    return ((paramObject instanceof TypeToken)) && (.Gson.Types.equals(this.type, ((TypeToken)paramObject).type));
  }
  
  public final String toString()
  {
    return .Gson.Types.typeToString(this.type);
  }
  
  public static TypeToken get(Type paramType)
  {
    return new TypeToken(paramType);
  }
  
  public static TypeToken get(Class paramClass)
  {
    return new TypeToken(paramClass);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\reflect\TypeToken.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */