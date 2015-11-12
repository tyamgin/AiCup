package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class SqlDateTypeAdapter
  extends TypeAdapter
{
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
  {
    public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
    {
      return paramAnonymousTypeToken.getRawType() == java.sql.Date.class ? new SqlDateTypeAdapter() : null;
    }
  };
  private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");
  
  public synchronized java.sql.Date read(JsonReader paramJsonReader)
    throws IOException
  {
    if (paramJsonReader.peek() == JsonToken.NULL)
    {
      paramJsonReader.nextNull();
      return null;
    }
    try
    {
      long l = this.format.parse(paramJsonReader.nextString()).getTime();
      return new java.sql.Date(l);
    }
    catch (ParseException localParseException)
    {
      throw new JsonSyntaxException(localParseException);
    }
  }
  
  public synchronized void write(JsonWriter paramJsonWriter, java.sql.Date paramDate)
    throws IOException
  {
    paramJsonWriter.value(paramDate == null ? null : this.format.format(paramDate));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\bind\SqlDateTypeAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */