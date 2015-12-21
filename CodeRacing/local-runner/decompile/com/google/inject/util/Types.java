package com.google.inject.util;

import com.google.inject.Provider;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.MoreTypes.GenericArrayTypeImpl;
import com.google.inject.internal.MoreTypes.ParameterizedTypeImpl;
import com.google.inject.internal.MoreTypes.WildcardTypeImpl;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Types
{
  public static ParameterizedType newParameterizedType(Type paramType, Type... paramVarArgs)
  {
    return newParameterizedTypeWithOwner(null, paramType, paramVarArgs);
  }
  
  public static ParameterizedType newParameterizedTypeWithOwner(Type paramType1, Type paramType2, Type... paramVarArgs)
  {
    return new MoreTypes.ParameterizedTypeImpl(paramType1, paramType2, paramVarArgs);
  }
  
  public static GenericArrayType arrayOf(Type paramType)
  {
    return new MoreTypes.GenericArrayTypeImpl(paramType);
  }
  
  public static WildcardType subtypeOf(Type paramType)
  {
    return new MoreTypes.WildcardTypeImpl(new Type[] { paramType }, MoreTypes.EMPTY_TYPE_ARRAY);
  }
  
  public static WildcardType supertypeOf(Type paramType)
  {
    return new MoreTypes.WildcardTypeImpl(new Type[] { Object.class }, new Type[] { paramType });
  }
  
  public static ParameterizedType listOf(Type paramType)
  {
    return newParameterizedType(List.class, new Type[] { paramType });
  }
  
  public static ParameterizedType setOf(Type paramType)
  {
    return newParameterizedType(Set.class, new Type[] { paramType });
  }
  
  public static ParameterizedType mapOf(Type paramType1, Type paramType2)
  {
    return newParameterizedType(Map.class, new Type[] { paramType1, paramType2 });
  }
  
  public static ParameterizedType providerOf(Type paramType)
  {
    return newParameterizedType(Provider.class, new Type[] { paramType });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\util\Types.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */