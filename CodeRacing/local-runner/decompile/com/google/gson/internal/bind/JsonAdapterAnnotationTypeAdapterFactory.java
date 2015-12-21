package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory
  implements TypeAdapterFactory
{
  private final ConstructorConstructor constructorConstructor;
  
  public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor paramConstructorConstructor)
  {
    this.constructorConstructor = paramConstructorConstructor;
  }
  
  public TypeAdapter create(Gson paramGson, TypeToken paramTypeToken)
  {
    JsonAdapter localJsonAdapter = (JsonAdapter)paramTypeToken.getRawType().getAnnotation(JsonAdapter.class);
    if (localJsonAdapter == null) {
      return null;
    }
    return getTypeAdapter(this.constructorConstructor, paramGson, paramTypeToken, localJsonAdapter);
  }
  
  static TypeAdapter getTypeAdapter(ConstructorConstructor paramConstructorConstructor, Gson paramGson, TypeToken paramTypeToken, JsonAdapter paramJsonAdapter)
  {
    Class localClass1 = paramJsonAdapter.value();
    Class localClass2;
    if (TypeAdapter.class.isAssignableFrom(localClass1))
    {
      localClass2 = localClass1;
      return (TypeAdapter)paramConstructorConstructor.get(TypeToken.get(localClass2)).construct();
    }
    if (TypeAdapterFactory.class.isAssignableFrom(localClass1))
    {
      localClass2 = localClass1;
      return ((TypeAdapterFactory)paramConstructorConstructor.get(TypeToken.get(localClass2)).construct()).create(paramGson, paramTypeToken);
    }
    throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\bind\JsonAdapterAnnotationTypeAdapterFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */