package com.google.gson;

import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public abstract class TypeAdapter
{
  public abstract void write(JsonWriter paramJsonWriter, Object paramObject)
    throws IOException;
  
  public final void toJson(Writer paramWriter, Object paramObject)
    throws IOException
  {
    JsonWriter localJsonWriter = new JsonWriter(paramWriter);
    write(localJsonWriter, paramObject);
  }
  
  public final TypeAdapter nullSafe()
  {
    new TypeAdapter()
    {
      public void write(JsonWriter paramAnonymousJsonWriter, Object paramAnonymousObject)
        throws IOException
      {
        if (paramAnonymousObject == null) {
          paramAnonymousJsonWriter.nullValue();
        } else {
          TypeAdapter.this.write(paramAnonymousJsonWriter, paramAnonymousObject);
        }
      }
      
      public Object read(JsonReader paramAnonymousJsonReader)
        throws IOException
      {
        if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
        {
          paramAnonymousJsonReader.nextNull();
          return null;
        }
        return TypeAdapter.this.read(paramAnonymousJsonReader);
      }
    };
  }
  
  public final String toJson(Object paramObject)
    throws IOException
  {
    StringWriter localStringWriter = new StringWriter();
    toJson(localStringWriter, paramObject);
    return localStringWriter.toString();
  }
  
  public final JsonElement toJsonTree(Object paramObject)
  {
    try
    {
      JsonTreeWriter localJsonTreeWriter = new JsonTreeWriter();
      write(localJsonTreeWriter, paramObject);
      return localJsonTreeWriter.get();
    }
    catch (IOException localIOException)
    {
      throw new JsonIOException(localIOException);
    }
  }
  
  public abstract Object read(JsonReader paramJsonReader)
    throws IOException;
  
  public final Object fromJson(Reader paramReader)
    throws IOException
  {
    JsonReader localJsonReader = new JsonReader(paramReader);
    return read(localJsonReader);
  }
  
  public final Object fromJson(String paramString)
    throws IOException
  {
    return fromJson(new StringReader(paramString));
  }
  
  public final Object fromJsonTree(JsonElement paramJsonElement)
  {
    try
    {
      JsonTreeReader localJsonTreeReader = new JsonTreeReader(paramJsonElement);
      return read(localJsonTreeReader);
    }
    catch (IOException localIOException)
    {
      throw new JsonIOException(localIOException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\TypeAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */