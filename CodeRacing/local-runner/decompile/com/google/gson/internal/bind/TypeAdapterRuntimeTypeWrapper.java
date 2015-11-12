package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class TypeAdapterRuntimeTypeWrapper
  extends TypeAdapter
{
  private final Gson context;
  private final TypeAdapter delegate;
  private final Type type;
  
  TypeAdapterRuntimeTypeWrapper(Gson paramGson, TypeAdapter paramTypeAdapter, Type paramType)
  {
    this.context = paramGson;
    this.delegate = paramTypeAdapter;
    this.type = paramType;
  }
  
  public Object read(JsonReader paramJsonReader)
    throws IOException
  {
    return this.delegate.read(paramJsonReader);
  }
  
  public void write(JsonWriter paramJsonWriter, Object paramObject)
    throws IOException
  {
    Object localObject = this.delegate;
    Type localType = getRuntimeTypeIfMoreSpecific(this.type, paramObject);
    if (localType != this.type)
    {
      TypeAdapter localTypeAdapter = this.context.getAdapter(TypeToken.get(localType));
      if (!(localTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
        localObject = localTypeAdapter;
      } else if (!(this.delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
        localObject = this.delegate;
      } else {
        localObject = localTypeAdapter;
      }
    }
    ((TypeAdapter)localObject).write(paramJsonWriter, paramObject);
  }
  
  private Type getRuntimeTypeIfMoreSpecific(Type paramType, Object paramObject)
  {
    if ((paramObject != null) && ((paramType == Object.class) || ((paramType instanceof TypeVariable)) || ((paramType instanceof Class)))) {
      paramType = paramObject.getClass();
    }
    return paramType;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\bind\TypeAdapterRuntimeTypeWrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */