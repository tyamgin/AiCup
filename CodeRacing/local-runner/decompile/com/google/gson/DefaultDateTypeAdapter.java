package com.google.gson;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

final class DefaultDateTypeAdapter
  implements JsonDeserializer, JsonSerializer
{
  private final DateFormat enUsFormat;
  private final DateFormat localFormat;
  private final DateFormat iso8601Format;
  
  DefaultDateTypeAdapter()
  {
    this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
  }
  
  DefaultDateTypeAdapter(String paramString)
  {
    this(new SimpleDateFormat(paramString, Locale.US), new SimpleDateFormat(paramString));
  }
  
  DefaultDateTypeAdapter(int paramInt)
  {
    this(DateFormat.getDateInstance(paramInt, Locale.US), DateFormat.getDateInstance(paramInt));
  }
  
  public DefaultDateTypeAdapter(int paramInt1, int paramInt2)
  {
    this(DateFormat.getDateTimeInstance(paramInt1, paramInt2, Locale.US), DateFormat.getDateTimeInstance(paramInt1, paramInt2));
  }
  
  DefaultDateTypeAdapter(DateFormat paramDateFormat1, DateFormat paramDateFormat2)
  {
    this.enUsFormat = paramDateFormat1;
    this.localFormat = paramDateFormat2;
    this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  public JsonElement serialize(java.util.Date paramDate, Type paramType, JsonSerializationContext paramJsonSerializationContext)
  {
    synchronized (this.localFormat)
    {
      String str = this.enUsFormat.format(paramDate);
      return new JsonPrimitive(str);
    }
  }
  
  public java.util.Date deserialize(JsonElement paramJsonElement, Type paramType, JsonDeserializationContext paramJsonDeserializationContext)
    throws JsonParseException
  {
    if (!(paramJsonElement instanceof JsonPrimitive)) {
      throw new JsonParseException("The date should be a string value");
    }
    java.util.Date localDate = deserializeToDate(paramJsonElement);
    if (paramType == java.util.Date.class) {
      return localDate;
    }
    if (paramType == Timestamp.class) {
      return new Timestamp(localDate.getTime());
    }
    if (paramType == java.sql.Date.class) {
      return new java.sql.Date(localDate.getTime());
    }
    throw new IllegalArgumentException(getClass() + " cannot deserialize to " + paramType);
  }
  
  private java.util.Date deserializeToDate(JsonElement paramJsonElement)
  {
    synchronized (this.localFormat)
    {
      try
      {
        return this.localFormat.parse(paramJsonElement.getAsString());
      }
      catch (ParseException localParseException1)
      {
        try
        {
          return this.enUsFormat.parse(paramJsonElement.getAsString());
        }
        catch (ParseException localParseException2)
        {
          try
          {
            return this.iso8601Format.parse(paramJsonElement.getAsString());
          }
          catch (ParseException localParseException3)
          {
            throw new JsonSyntaxException(paramJsonElement.getAsString(), localParseException3);
          }
        }
      }
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(DefaultDateTypeAdapter.class.getSimpleName());
    localStringBuilder.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
    return localStringBuilder.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\DefaultDateTypeAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */