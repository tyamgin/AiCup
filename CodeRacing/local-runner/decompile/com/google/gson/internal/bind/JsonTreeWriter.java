package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class JsonTreeWriter
  extends JsonWriter
{
  private static final Writer UNWRITABLE_WRITER = new Writer()
  {
    public void write(char[] paramAnonymousArrayOfChar, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      throw new AssertionError();
    }
    
    public void flush()
      throws IOException
    {
      throw new AssertionError();
    }
    
    public void close()
      throws IOException
    {
      throw new AssertionError();
    }
  };
  private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");
  private final List stack = new ArrayList();
  private String pendingName;
  private JsonElement product = JsonNull.INSTANCE;
  
  public JsonTreeWriter()
  {
    super(UNWRITABLE_WRITER);
  }
  
  public JsonElement get()
  {
    if (!this.stack.isEmpty()) {
      throw new IllegalStateException("Expected one JSON element but was " + this.stack);
    }
    return this.product;
  }
  
  private JsonElement peek()
  {
    return (JsonElement)this.stack.get(this.stack.size() - 1);
  }
  
  private void put(JsonElement paramJsonElement)
  {
    Object localObject;
    if (this.pendingName != null)
    {
      if ((!paramJsonElement.isJsonNull()) || (getSerializeNulls()))
      {
        localObject = (JsonObject)peek();
        ((JsonObject)localObject).add(this.pendingName, paramJsonElement);
      }
      this.pendingName = null;
    }
    else if (this.stack.isEmpty())
    {
      this.product = paramJsonElement;
    }
    else
    {
      localObject = peek();
      if ((localObject instanceof JsonArray)) {
        ((JsonArray)localObject).add(paramJsonElement);
      } else {
        throw new IllegalStateException();
      }
    }
  }
  
  public JsonWriter beginArray()
    throws IOException
  {
    JsonArray localJsonArray = new JsonArray();
    put(localJsonArray);
    this.stack.add(localJsonArray);
    return this;
  }
  
  public JsonWriter endArray()
    throws IOException
  {
    if ((this.stack.isEmpty()) || (this.pendingName != null)) {
      throw new IllegalStateException();
    }
    JsonElement localJsonElement = peek();
    if ((localJsonElement instanceof JsonArray))
    {
      this.stack.remove(this.stack.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }
  
  public JsonWriter beginObject()
    throws IOException
  {
    JsonObject localJsonObject = new JsonObject();
    put(localJsonObject);
    this.stack.add(localJsonObject);
    return this;
  }
  
  public JsonWriter endObject()
    throws IOException
  {
    if ((this.stack.isEmpty()) || (this.pendingName != null)) {
      throw new IllegalStateException();
    }
    JsonElement localJsonElement = peek();
    if ((localJsonElement instanceof JsonObject))
    {
      this.stack.remove(this.stack.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }
  
  public JsonWriter name(String paramString)
    throws IOException
  {
    if ((this.stack.isEmpty()) || (this.pendingName != null)) {
      throw new IllegalStateException();
    }
    JsonElement localJsonElement = peek();
    if ((localJsonElement instanceof JsonObject))
    {
      this.pendingName = paramString;
      return this;
    }
    throw new IllegalStateException();
  }
  
  public JsonWriter value(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return nullValue();
    }
    put(new JsonPrimitive(paramString));
    return this;
  }
  
  public JsonWriter nullValue()
    throws IOException
  {
    put(JsonNull.INSTANCE);
    return this;
  }
  
  public JsonWriter value(boolean paramBoolean)
    throws IOException
  {
    put(new JsonPrimitive(Boolean.valueOf(paramBoolean)));
    return this;
  }
  
  public JsonWriter value(double paramDouble)
    throws IOException
  {
    if ((!isLenient()) && ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)))) {
      throw new IllegalArgumentException("JSON forbids NaN and infinities: " + paramDouble);
    }
    put(new JsonPrimitive(Double.valueOf(paramDouble)));
    return this;
  }
  
  public JsonWriter value(long paramLong)
    throws IOException
  {
    put(new JsonPrimitive(Long.valueOf(paramLong)));
    return this;
  }
  
  public JsonWriter value(Number paramNumber)
    throws IOException
  {
    if (paramNumber == null) {
      return nullValue();
    }
    if (!isLenient())
    {
      double d = paramNumber.doubleValue();
      if ((Double.isNaN(d)) || (Double.isInfinite(d))) {
        throw new IllegalArgumentException("JSON forbids NaN and infinities: " + paramNumber);
      }
    }
    put(new JsonPrimitive(paramNumber));
    return this;
  }
  
  public void flush()
    throws IOException
  {}
  
  public void close()
    throws IOException
  {
    if (!this.stack.isEmpty()) {
      throw new IOException("Incomplete document");
    }
    this.stack.add(SENTINEL_CLOSED);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\bind\JsonTreeWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */