package com.google.gson;

import com.google.gson.internal..Gson.Preconditions;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

final class TreeTypeAdapter
  extends TypeAdapter
{
  private final JsonSerializer serializer;
  private final JsonDeserializer deserializer;
  private final Gson gson;
  private final TypeToken typeToken;
  private final TypeAdapterFactory skipPast;
  private TypeAdapter delegate;
  
  private TreeTypeAdapter(JsonSerializer paramJsonSerializer, JsonDeserializer paramJsonDeserializer, Gson paramGson, TypeToken paramTypeToken, TypeAdapterFactory paramTypeAdapterFactory)
  {
    this.serializer = paramJsonSerializer;
    this.deserializer = paramJsonDeserializer;
    this.gson = paramGson;
    this.typeToken = paramTypeToken;
    this.skipPast = paramTypeAdapterFactory;
  }
  
  public Object read(JsonReader paramJsonReader)
    throws IOException
  {
    if (this.deserializer == null) {
      return delegate().read(paramJsonReader);
    }
    JsonElement localJsonElement = Streams.parse(paramJsonReader);
    if (localJsonElement.isJsonNull()) {
      return null;
    }
    return this.deserializer.deserialize(localJsonElement, this.typeToken.getType(), this.gson.deserializationContext);
  }
  
  public void write(JsonWriter paramJsonWriter, Object paramObject)
    throws IOException
  {
    if (this.serializer == null)
    {
      delegate().write(paramJsonWriter, paramObject);
      return;
    }
    if (paramObject == null)
    {
      paramJsonWriter.nullValue();
      return;
    }
    JsonElement localJsonElement = this.serializer.serialize(paramObject, this.typeToken.getType(), this.gson.serializationContext);
    Streams.write(localJsonElement, paramJsonWriter);
  }
  
  private TypeAdapter delegate()
  {
    TypeAdapter localTypeAdapter = this.delegate;
    return localTypeAdapter != null ? localTypeAdapter : (this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
  }
  
  public static TypeAdapterFactory newFactory(TypeToken paramTypeToken, Object paramObject)
  {
    return new SingleTypeFactory(paramObject, paramTypeToken, false, null, null);
  }
  
  public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken paramTypeToken, Object paramObject)
  {
    boolean bool = paramTypeToken.getType() == paramTypeToken.getRawType();
    return new SingleTypeFactory(paramObject, paramTypeToken, bool, null, null);
  }
  
  public static TypeAdapterFactory newTypeHierarchyFactory(Class paramClass, Object paramObject)
  {
    return new SingleTypeFactory(paramObject, null, false, paramClass, null);
  }
  
  private static class SingleTypeFactory
    implements TypeAdapterFactory
  {
    private final TypeToken exactType;
    private final boolean matchRawType;
    private final Class hierarchyType;
    private final JsonSerializer serializer;
    private final JsonDeserializer deserializer;
    
    private SingleTypeFactory(Object paramObject, TypeToken paramTypeToken, boolean paramBoolean, Class paramClass)
    {
      this.serializer = ((paramObject instanceof JsonSerializer) ? (JsonSerializer)paramObject : null);
      this.deserializer = ((paramObject instanceof JsonDeserializer) ? (JsonDeserializer)paramObject : null);
      .Gson.Preconditions.checkArgument((this.serializer != null) || (this.deserializer != null));
      this.exactType = paramTypeToken;
      this.matchRawType = paramBoolean;
      this.hierarchyType = paramClass;
    }
    
    public TypeAdapter create(Gson paramGson, TypeToken paramTypeToken)
    {
      boolean bool = this.exactType != null ? false : (this.exactType.equals(paramTypeToken)) || ((this.matchRawType) && (this.exactType.getType() == paramTypeToken.getRawType())) ? true : this.hierarchyType.isAssignableFrom(paramTypeToken.getRawType());
      return bool ? new TreeTypeAdapter(this.serializer, this.deserializer, paramGson, paramTypeToken, this, null) : null;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\TreeTypeAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */