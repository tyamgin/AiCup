package com.google.common.reflect;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class TypeResolver
{
  private final ImmutableMap typeTable;
  
  public TypeResolver()
  {
    this.typeTable = ImmutableMap.of();
  }
  
  private TypeResolver(ImmutableMap paramImmutableMap)
  {
    this.typeTable = paramImmutableMap;
  }
  
  static TypeResolver accordingTo(Type paramType)
  {
    return new TypeResolver().where(TypeMappingIntrospector.getTypeMappings(paramType));
  }
  
  public final TypeResolver where(Type paramType1, Type paramType2)
  {
    HashMap localHashMap = Maps.newHashMap();
    populateTypeMappings(localHashMap, (Type)Preconditions.checkNotNull(paramType1), (Type)Preconditions.checkNotNull(paramType2));
    return where(localHashMap);
  }
  
  final TypeResolver where(Map paramMap)
  {
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    localBuilder.putAll(this.typeTable);
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      TypeVariable localTypeVariable = (TypeVariable)localEntry.getKey();
      Type localType = (Type)localEntry.getValue();
      Preconditions.checkArgument(!localTypeVariable.equals(localType), "Type variable %s bound to itself", new Object[] { localTypeVariable });
      localBuilder.put(localTypeVariable, localType);
    }
    return new TypeResolver(localBuilder.build());
  }
  
  private static void populateTypeMappings(Map paramMap, Type paramType1, Type paramType2)
  {
    if (paramType1.equals(paramType2)) {
      return;
    }
    if ((paramType1 instanceof TypeVariable))
    {
      paramMap.put((TypeVariable)paramType1, paramType2);
    }
    else if ((paramType1 instanceof GenericArrayType))
    {
      populateTypeMappings(paramMap, ((GenericArrayType)paramType1).getGenericComponentType(), (Type)checkNonNullArgument(Types.getComponentType(paramType2), "%s is not an array type.", new Object[] { paramType2 }));
    }
    else
    {
      Object localObject1;
      Object localObject2;
      Type[] arrayOfType1;
      Type[] arrayOfType2;
      if ((paramType1 instanceof ParameterizedType))
      {
        localObject1 = (ParameterizedType)paramType1;
        localObject2 = (ParameterizedType)expectArgument(ParameterizedType.class, paramType2);
        Preconditions.checkArgument(((ParameterizedType)localObject1).getRawType().equals(((ParameterizedType)localObject2).getRawType()), "Inconsistent raw type: %s vs. %s", new Object[] { paramType1, paramType2 });
        arrayOfType1 = ((ParameterizedType)localObject1).getActualTypeArguments();
        arrayOfType2 = ((ParameterizedType)localObject2).getActualTypeArguments();
        Preconditions.checkArgument(arrayOfType1.length == arrayOfType2.length);
        for (int i = 0; i < arrayOfType1.length; i++) {
          populateTypeMappings(paramMap, arrayOfType1[i], arrayOfType2[i]);
        }
      }
      else if ((paramType1 instanceof WildcardType))
      {
        localObject1 = (WildcardType)paramType1;
        localObject2 = (WildcardType)expectArgument(WildcardType.class, paramType2);
        arrayOfType1 = ((WildcardType)localObject1).getUpperBounds();
        arrayOfType2 = ((WildcardType)localObject2).getUpperBounds();
        Type[] arrayOfType3 = ((WildcardType)localObject1).getLowerBounds();
        Type[] arrayOfType4 = ((WildcardType)localObject2).getLowerBounds();
        Preconditions.checkArgument((arrayOfType1.length == arrayOfType2.length) && (arrayOfType3.length == arrayOfType4.length), "Incompatible type: %s vs. %s", new Object[] { paramType1, paramType2 });
        for (int j = 0; j < arrayOfType1.length; j++) {
          populateTypeMappings(paramMap, arrayOfType1[j], arrayOfType2[j]);
        }
        for (j = 0; j < arrayOfType3.length; j++) {
          populateTypeMappings(paramMap, arrayOfType3[j], arrayOfType4[j]);
        }
      }
      else
      {
        throw new IllegalArgumentException("No type mapping from " + paramType1);
      }
    }
  }
  
  public final Type resolveType(Type paramType)
  {
    Preconditions.checkNotNull(paramType);
    if ((paramType instanceof TypeVariable)) {
      return resolveTypeVariable((TypeVariable)paramType);
    }
    if ((paramType instanceof ParameterizedType)) {
      return resolveParameterizedType((ParameterizedType)paramType);
    }
    if ((paramType instanceof GenericArrayType)) {
      return resolveGenericArrayType((GenericArrayType)paramType);
    }
    if ((paramType instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramType;
      return new Types.WildcardTypeImpl(resolveTypes(localWildcardType.getLowerBounds()), resolveTypes(localWildcardType.getUpperBounds()));
    }
    return paramType;
  }
  
  private Type[] resolveTypes(Type[] paramArrayOfType)
  {
    Type[] arrayOfType = new Type[paramArrayOfType.length];
    for (int i = 0; i < paramArrayOfType.length; i++) {
      arrayOfType[i] = resolveType(paramArrayOfType[i]);
    }
    return arrayOfType;
  }
  
  private Type resolveGenericArrayType(GenericArrayType paramGenericArrayType)
  {
    Type localType = resolveType(paramGenericArrayType.getGenericComponentType());
    return Types.newArrayType(localType);
  }
  
  private Type resolveTypeVariable(final TypeVariable paramTypeVariable)
  {
    final TypeResolver localTypeResolver = this;
    TypeResolver local1 = new TypeResolver(this.typeTable, paramTypeVariable)
    {
      Type resolveTypeVariable(TypeVariable paramAnonymousTypeVariable, TypeResolver paramAnonymousTypeResolver)
      {
        if (paramAnonymousTypeVariable.getGenericDeclaration().equals(paramTypeVariable.getGenericDeclaration())) {
          return paramAnonymousTypeVariable;
        }
        return localTypeResolver.resolveTypeVariable(paramAnonymousTypeVariable, paramAnonymousTypeResolver);
      }
    };
    return resolveTypeVariable(paramTypeVariable, local1);
  }
  
  Type resolveTypeVariable(TypeVariable paramTypeVariable, TypeResolver paramTypeResolver)
  {
    Preconditions.checkNotNull(paramTypeResolver);
    Type localType = (Type)this.typeTable.get(paramTypeVariable);
    if (localType == null)
    {
      Type[] arrayOfType = paramTypeVariable.getBounds();
      if (arrayOfType.length == 0) {
        return paramTypeVariable;
      }
      return Types.newTypeVariable(paramTypeVariable.getGenericDeclaration(), paramTypeVariable.getName(), paramTypeResolver.resolveTypes(arrayOfType));
    }
    return paramTypeResolver.resolveType(localType);
  }
  
  private ParameterizedType resolveParameterizedType(ParameterizedType paramParameterizedType)
  {
    Type localType1 = paramParameterizedType.getOwnerType();
    Type localType2 = localType1 == null ? null : resolveType(localType1);
    Type localType3 = resolveType(paramParameterizedType.getRawType());
    Type[] arrayOfType1 = paramParameterizedType.getActualTypeArguments();
    Type[] arrayOfType2 = new Type[arrayOfType1.length];
    for (int i = 0; i < arrayOfType1.length; i++) {
      arrayOfType2[i] = resolveType(arrayOfType1[i]);
    }
    return Types.newParameterizedTypeWithOwner(localType2, (Class)localType3, arrayOfType2);
  }
  
  private static Object checkNonNullArgument(Object paramObject, String paramString, Object... paramVarArgs)
  {
    Preconditions.checkArgument(paramObject != null, paramString, paramVarArgs);
    return paramObject;
  }
  
  private static Object expectArgument(Class paramClass, Object paramObject)
  {
    try
    {
      return paramClass.cast(paramObject);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException(paramObject + " is not a " + paramClass.getSimpleName());
    }
  }
  
  private static final class WildcardCapturer
  {
    private final AtomicInteger id = new AtomicInteger();
    
    Type capture(Type paramType)
    {
      Preconditions.checkNotNull(paramType);
      if ((paramType instanceof Class)) {
        return paramType;
      }
      if ((paramType instanceof TypeVariable)) {
        return paramType;
      }
      Object localObject;
      if ((paramType instanceof GenericArrayType))
      {
        localObject = (GenericArrayType)paramType;
        return Types.newArrayType(capture(((GenericArrayType)localObject).getGenericComponentType()));
      }
      if ((paramType instanceof ParameterizedType))
      {
        localObject = (ParameterizedType)paramType;
        return Types.newParameterizedTypeWithOwner(captureNullable(((ParameterizedType)localObject).getOwnerType()), (Class)((ParameterizedType)localObject).getRawType(), capture(((ParameterizedType)localObject).getActualTypeArguments()));
      }
      if ((paramType instanceof WildcardType))
      {
        localObject = (WildcardType)paramType;
        Type[] arrayOfType1 = ((WildcardType)localObject).getLowerBounds();
        if (arrayOfType1.length == 0)
        {
          Type[] arrayOfType2 = ((WildcardType)localObject).getUpperBounds();
          String str = "capture#" + this.id.incrementAndGet() + "-of ? extends " + Joiner.on('&').join(arrayOfType2);
          return Types.newTypeVariable(WildcardCapturer.class, str, ((WildcardType)localObject).getUpperBounds());
        }
        return paramType;
      }
      throw new AssertionError("must have been one of the known types");
    }
    
    private Type captureNullable(Type paramType)
    {
      if (paramType == null) {
        return null;
      }
      return capture(paramType);
    }
    
    private Type[] capture(Type[] paramArrayOfType)
    {
      Type[] arrayOfType = new Type[paramArrayOfType.length];
      for (int i = 0; i < paramArrayOfType.length; i++) {
        arrayOfType[i] = capture(paramArrayOfType[i]);
      }
      return arrayOfType;
    }
  }
  
  private static final class TypeMappingIntrospector
  {
    private static final TypeResolver.WildcardCapturer wildcardCapturer = new TypeResolver.WildcardCapturer(null);
    private final Map mappings = Maps.newHashMap();
    private final Set introspectedTypes = Sets.newHashSet();
    
    static ImmutableMap getTypeMappings(Type paramType)
    {
      TypeMappingIntrospector localTypeMappingIntrospector = new TypeMappingIntrospector();
      localTypeMappingIntrospector.introspect(wildcardCapturer.capture(paramType));
      return ImmutableMap.copyOf(localTypeMappingIntrospector.mappings);
    }
    
    private void introspect(Type paramType)
    {
      if (!this.introspectedTypes.add(paramType)) {
        return;
      }
      if ((paramType instanceof ParameterizedType))
      {
        introspectParameterizedType((ParameterizedType)paramType);
      }
      else if ((paramType instanceof Class))
      {
        introspectClass((Class)paramType);
      }
      else
      {
        Type localType;
        if ((paramType instanceof TypeVariable)) {
          for (localType : ((TypeVariable)paramType).getBounds()) {
            introspect(localType);
          }
        } else if ((paramType instanceof WildcardType)) {
          for (localType : ((WildcardType)paramType).getUpperBounds()) {
            introspect(localType);
          }
        }
      }
    }
    
    private void introspectClass(Class paramClass)
    {
      introspect(paramClass.getGenericSuperclass());
      for (Type localType : paramClass.getGenericInterfaces()) {
        introspect(localType);
      }
    }
    
    private void introspectParameterizedType(ParameterizedType paramParameterizedType)
    {
      Class localClass = (Class)paramParameterizedType.getRawType();
      TypeVariable[] arrayOfTypeVariable = localClass.getTypeParameters();
      Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
      Preconditions.checkState(arrayOfTypeVariable.length == arrayOfType.length);
      for (int i = 0; i < arrayOfTypeVariable.length; i++) {
        map(arrayOfTypeVariable[i], arrayOfType[i]);
      }
      introspectClass(localClass);
      introspect(paramParameterizedType.getOwnerType());
    }
    
    private void map(TypeVariable paramTypeVariable, Type paramType)
    {
      if (this.mappings.containsKey(paramTypeVariable)) {
        return;
      }
      for (Type localType1 = paramType; localType1 != null; localType1 = (Type)this.mappings.get(localType1)) {
        if (paramTypeVariable.equals(localType1))
        {
          for (Type localType2 = paramType; localType2 != null; localType2 = (Type)this.mappings.remove(localType2)) {}
          return;
        }
      }
      this.mappings.put(paramTypeVariable, paramType);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\TypeResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */