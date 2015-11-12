package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

public final class TypeAdapters
{
  public static final TypeAdapter CLASS = new TypeAdapter()
  {
    public void write(JsonWriter paramAnonymousJsonWriter, Class paramAnonymousClass)
      throws IOException
    {
      if (paramAnonymousClass == null) {
        paramAnonymousJsonWriter.nullValue();
      } else {
        throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + paramAnonymousClass.getName() + ". Forgot to register a type adapter?");
      }
    }
    
    public Class read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
    }
  };
  public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);
  public static final TypeAdapter BIT_SET = new TypeAdapter()
  {
    public BitSet read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      BitSet localBitSet = new BitSet();
      paramAnonymousJsonReader.beginArray();
      int i = 0;
      for (JsonToken localJsonToken = paramAnonymousJsonReader.peek(); localJsonToken != JsonToken.END_ARRAY; localJsonToken = paramAnonymousJsonReader.peek())
      {
        boolean bool;
        switch (TypeAdapters.32.$SwitchMap$com$google$gson$stream$JsonToken[localJsonToken.ordinal()])
        {
        case 1: 
          bool = paramAnonymousJsonReader.nextInt() != 0;
          break;
        case 2: 
          bool = paramAnonymousJsonReader.nextBoolean();
          break;
        case 3: 
          String str = paramAnonymousJsonReader.nextString();
          try
          {
            bool = Integer.parseInt(str) != 0;
          }
          catch (NumberFormatException localNumberFormatException)
          {
            throw new JsonSyntaxException("Error: Expecting: bitset number value (1, 0), Found: " + str);
          }
        default: 
          throw new JsonSyntaxException("Invalid bitset value type: " + localJsonToken);
        }
        if (bool) {
          localBitSet.set(i);
        }
        i++;
      }
      paramAnonymousJsonReader.endArray();
      return localBitSet;
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, BitSet paramAnonymousBitSet)
      throws IOException
    {
      if (paramAnonymousBitSet == null)
      {
        paramAnonymousJsonWriter.nullValue();
        return;
      }
      paramAnonymousJsonWriter.beginArray();
      for (int i = 0; i < paramAnonymousBitSet.length(); i++)
      {
        int j = paramAnonymousBitSet.get(i) ? 1 : 0;
        paramAnonymousJsonWriter.value(j);
      }
      paramAnonymousJsonWriter.endArray();
    }
  };
  public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
  public static final TypeAdapter BOOLEAN = new TypeAdapter()
  {
    public Boolean read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      if (paramAnonymousJsonReader.peek() == JsonToken.STRING) {
        return Boolean.valueOf(Boolean.parseBoolean(paramAnonymousJsonReader.nextString()));
      }
      return Boolean.valueOf(paramAnonymousJsonReader.nextBoolean());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Boolean paramAnonymousBoolean)
      throws IOException
    {
      if (paramAnonymousBoolean == null)
      {
        paramAnonymousJsonWriter.nullValue();
        return;
      }
      paramAnonymousJsonWriter.value(paramAnonymousBoolean.booleanValue());
    }
  };
  public static final TypeAdapter BOOLEAN_AS_STRING = new TypeAdapter()
  {
    public Boolean read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      return Boolean.valueOf(paramAnonymousJsonReader.nextString());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Boolean paramAnonymousBoolean)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousBoolean == null ? "null" : paramAnonymousBoolean.toString());
    }
  };
  public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
  public static final TypeAdapter BYTE = new TypeAdapter()
  {
    public Number read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      try
      {
        int i = paramAnonymousJsonReader.nextInt();
        return Byte.valueOf((byte)i);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new JsonSyntaxException(localNumberFormatException);
      }
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousNumber);
    }
  };
  public static final TypeAdapterFactory BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, BYTE);
  public static final TypeAdapter SHORT = new TypeAdapter()
  {
    public Number read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      try
      {
        return Short.valueOf((short)paramAnonymousJsonReader.nextInt());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new JsonSyntaxException(localNumberFormatException);
      }
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousNumber);
    }
  };
  public static final TypeAdapterFactory SHORT_FACTORY = newFactory(Short.TYPE, Short.class, SHORT);
  public static final TypeAdapter INTEGER = new TypeAdapter()
  {
    public Number read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      try
      {
        return Integer.valueOf(paramAnonymousJsonReader.nextInt());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new JsonSyntaxException(localNumberFormatException);
      }
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousNumber);
    }
  };
  public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, INTEGER);
  public static final TypeAdapter LONG = new TypeAdapter()
  {
    public Number read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      try
      {
        return Long.valueOf(paramAnonymousJsonReader.nextLong());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new JsonSyntaxException(localNumberFormatException);
      }
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousNumber);
    }
  };
  public static final TypeAdapter FLOAT = new TypeAdapter()
  {
    public Number read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      return Float.valueOf((float)paramAnonymousJsonReader.nextDouble());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousNumber);
    }
  };
  public static final TypeAdapter DOUBLE = new TypeAdapter()
  {
    public Number read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      return Double.valueOf(paramAnonymousJsonReader.nextDouble());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousNumber);
    }
  };
  public static final TypeAdapter NUMBER = new TypeAdapter()
  {
    public Number read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      JsonToken localJsonToken = paramAnonymousJsonReader.peek();
      switch (TypeAdapters.32.$SwitchMap$com$google$gson$stream$JsonToken[localJsonToken.ordinal()])
      {
      case 4: 
        paramAnonymousJsonReader.nextNull();
        return null;
      case 1: 
        return new LazilyParsedNumber(paramAnonymousJsonReader.nextString());
      }
      throw new JsonSyntaxException("Expecting number, got: " + localJsonToken);
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousNumber);
    }
  };
  public static final TypeAdapterFactory NUMBER_FACTORY = newFactory(Number.class, NUMBER);
  public static final TypeAdapter CHARACTER = new TypeAdapter()
  {
    public Character read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      String str = paramAnonymousJsonReader.nextString();
      if (str.length() != 1) {
        throw new JsonSyntaxException("Expecting character, got: " + str);
      }
      return Character.valueOf(str.charAt(0));
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Character paramAnonymousCharacter)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousCharacter == null ? null : String.valueOf(paramAnonymousCharacter));
    }
  };
  public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, CHARACTER);
  public static final TypeAdapter STRING = new TypeAdapter()
  {
    public String read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      JsonToken localJsonToken = paramAnonymousJsonReader.peek();
      if (localJsonToken == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      if (localJsonToken == JsonToken.BOOLEAN) {
        return Boolean.toString(paramAnonymousJsonReader.nextBoolean());
      }
      return paramAnonymousJsonReader.nextString();
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, String paramAnonymousString)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousString);
    }
  };
  public static final TypeAdapter BIG_DECIMAL = new TypeAdapter()
  {
    public BigDecimal read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      try
      {
        return new BigDecimal(paramAnonymousJsonReader.nextString());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new JsonSyntaxException(localNumberFormatException);
      }
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, BigDecimal paramAnonymousBigDecimal)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousBigDecimal);
    }
  };
  public static final TypeAdapter BIG_INTEGER = new TypeAdapter()
  {
    public BigInteger read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      try
      {
        return new BigInteger(paramAnonymousJsonReader.nextString());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new JsonSyntaxException(localNumberFormatException);
      }
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, BigInteger paramAnonymousBigInteger)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousBigInteger);
    }
  };
  public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
  public static final TypeAdapter STRING_BUILDER = new TypeAdapter()
  {
    public StringBuilder read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      return new StringBuilder(paramAnonymousJsonReader.nextString());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, StringBuilder paramAnonymousStringBuilder)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousStringBuilder == null ? null : paramAnonymousStringBuilder.toString());
    }
  };
  public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
  public static final TypeAdapter STRING_BUFFER = new TypeAdapter()
  {
    public StringBuffer read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      return new StringBuffer(paramAnonymousJsonReader.nextString());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, StringBuffer paramAnonymousStringBuffer)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousStringBuffer == null ? null : paramAnonymousStringBuffer.toString());
    }
  };
  public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
  public static final TypeAdapter URL = new TypeAdapter()
  {
    public URL read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      String str = paramAnonymousJsonReader.nextString();
      return "null".equals(str) ? null : new URL(str);
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, URL paramAnonymousURL)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousURL == null ? null : paramAnonymousURL.toExternalForm());
    }
  };
  public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
  public static final TypeAdapter URI = new TypeAdapter()
  {
    public URI read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      try
      {
        String str = paramAnonymousJsonReader.nextString();
        return "null".equals(str) ? null : new URI(str);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new JsonIOException(localURISyntaxException);
      }
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, URI paramAnonymousURI)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousURI == null ? null : paramAnonymousURI.toASCIIString());
    }
  };
  public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
  public static final TypeAdapter INET_ADDRESS = new TypeAdapter()
  {
    public InetAddress read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      return InetAddress.getByName(paramAnonymousJsonReader.nextString());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, InetAddress paramAnonymousInetAddress)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousInetAddress == null ? null : paramAnonymousInetAddress.getHostAddress());
    }
  };
  public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
  public static final TypeAdapter UUID = new TypeAdapter()
  {
    public UUID read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      return UUID.fromString(paramAnonymousJsonReader.nextString());
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, UUID paramAnonymousUUID)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousUUID == null ? null : paramAnonymousUUID.toString());
    }
  };
  public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);
  public static final TypeAdapterFactory TIMESTAMP_FACTORY = new TypeAdapterFactory()
  {
    public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
    {
      if (paramAnonymousTypeToken.getRawType() != Timestamp.class) {
        return null;
      }
      final TypeAdapter localTypeAdapter = paramAnonymousGson.getAdapter(Date.class);
      new TypeAdapter()
      {
        public Timestamp read(JsonReader paramAnonymous2JsonReader)
          throws IOException
        {
          Date localDate = (Date)localTypeAdapter.read(paramAnonymous2JsonReader);
          return localDate != null ? new Timestamp(localDate.getTime()) : null;
        }
        
        public void write(JsonWriter paramAnonymous2JsonWriter, Timestamp paramAnonymous2Timestamp)
          throws IOException
        {
          localTypeAdapter.write(paramAnonymous2JsonWriter, paramAnonymous2Timestamp);
        }
      };
    }
  };
  public static final TypeAdapter CALENDAR = new TypeAdapter()
  {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY_OF_MONTH = "dayOfMonth";
    private static final String HOUR_OF_DAY = "hourOfDay";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";
    
    public Calendar read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      paramAnonymousJsonReader.beginObject();
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      while (paramAnonymousJsonReader.peek() != JsonToken.END_OBJECT)
      {
        String str = paramAnonymousJsonReader.nextName();
        int i2 = paramAnonymousJsonReader.nextInt();
        if ("year".equals(str)) {
          i = i2;
        } else if ("month".equals(str)) {
          j = i2;
        } else if ("dayOfMonth".equals(str)) {
          k = i2;
        } else if ("hourOfDay".equals(str)) {
          m = i2;
        } else if ("minute".equals(str)) {
          n = i2;
        } else if ("second".equals(str)) {
          i1 = i2;
        }
      }
      paramAnonymousJsonReader.endObject();
      return new GregorianCalendar(i, j, k, m, n, i1);
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Calendar paramAnonymousCalendar)
      throws IOException
    {
      if (paramAnonymousCalendar == null)
      {
        paramAnonymousJsonWriter.nullValue();
        return;
      }
      paramAnonymousJsonWriter.beginObject();
      paramAnonymousJsonWriter.name("year");
      paramAnonymousJsonWriter.value(paramAnonymousCalendar.get(1));
      paramAnonymousJsonWriter.name("month");
      paramAnonymousJsonWriter.value(paramAnonymousCalendar.get(2));
      paramAnonymousJsonWriter.name("dayOfMonth");
      paramAnonymousJsonWriter.value(paramAnonymousCalendar.get(5));
      paramAnonymousJsonWriter.name("hourOfDay");
      paramAnonymousJsonWriter.value(paramAnonymousCalendar.get(11));
      paramAnonymousJsonWriter.name("minute");
      paramAnonymousJsonWriter.value(paramAnonymousCalendar.get(12));
      paramAnonymousJsonWriter.name("second");
      paramAnonymousJsonWriter.value(paramAnonymousCalendar.get(13));
      paramAnonymousJsonWriter.endObject();
    }
  };
  public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
  public static final TypeAdapter LOCALE = new TypeAdapter()
  {
    public Locale read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      if (paramAnonymousJsonReader.peek() == JsonToken.NULL)
      {
        paramAnonymousJsonReader.nextNull();
        return null;
      }
      String str1 = paramAnonymousJsonReader.nextString();
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, "_");
      String str2 = null;
      String str3 = null;
      String str4 = null;
      if (localStringTokenizer.hasMoreElements()) {
        str2 = localStringTokenizer.nextToken();
      }
      if (localStringTokenizer.hasMoreElements()) {
        str3 = localStringTokenizer.nextToken();
      }
      if (localStringTokenizer.hasMoreElements()) {
        str4 = localStringTokenizer.nextToken();
      }
      if ((str3 == null) && (str4 == null)) {
        return new Locale(str2);
      }
      if (str4 == null) {
        return new Locale(str2, str3);
      }
      return new Locale(str2, str3, str4);
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, Locale paramAnonymousLocale)
      throws IOException
    {
      paramAnonymousJsonWriter.value(paramAnonymousLocale == null ? null : paramAnonymousLocale.toString());
    }
  };
  public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
  public static final TypeAdapter JSON_ELEMENT = new TypeAdapter()
  {
    public JsonElement read(JsonReader paramAnonymousJsonReader)
      throws IOException
    {
      switch (TypeAdapters.32.$SwitchMap$com$google$gson$stream$JsonToken[paramAnonymousJsonReader.peek().ordinal()])
      {
      case 3: 
        return new JsonPrimitive(paramAnonymousJsonReader.nextString());
      case 1: 
        String str = paramAnonymousJsonReader.nextString();
        return new JsonPrimitive(new LazilyParsedNumber(str));
      case 2: 
        return new JsonPrimitive(Boolean.valueOf(paramAnonymousJsonReader.nextBoolean()));
      case 4: 
        paramAnonymousJsonReader.nextNull();
        return JsonNull.INSTANCE;
      case 5: 
        JsonArray localJsonArray = new JsonArray();
        paramAnonymousJsonReader.beginArray();
        while (paramAnonymousJsonReader.hasNext()) {
          localJsonArray.add(read(paramAnonymousJsonReader));
        }
        paramAnonymousJsonReader.endArray();
        return localJsonArray;
      case 6: 
        JsonObject localJsonObject = new JsonObject();
        paramAnonymousJsonReader.beginObject();
        while (paramAnonymousJsonReader.hasNext()) {
          localJsonObject.add(paramAnonymousJsonReader.nextName(), read(paramAnonymousJsonReader));
        }
        paramAnonymousJsonReader.endObject();
        return localJsonObject;
      }
      throw new IllegalArgumentException();
    }
    
    public void write(JsonWriter paramAnonymousJsonWriter, JsonElement paramAnonymousJsonElement)
      throws IOException
    {
      if ((paramAnonymousJsonElement == null) || (paramAnonymousJsonElement.isJsonNull()))
      {
        paramAnonymousJsonWriter.nullValue();
      }
      else
      {
        Object localObject1;
        if (paramAnonymousJsonElement.isJsonPrimitive())
        {
          localObject1 = paramAnonymousJsonElement.getAsJsonPrimitive();
          if (((JsonPrimitive)localObject1).isNumber()) {
            paramAnonymousJsonWriter.value(((JsonPrimitive)localObject1).getAsNumber());
          } else if (((JsonPrimitive)localObject1).isBoolean()) {
            paramAnonymousJsonWriter.value(((JsonPrimitive)localObject1).getAsBoolean());
          } else {
            paramAnonymousJsonWriter.value(((JsonPrimitive)localObject1).getAsString());
          }
        }
        else
        {
          Object localObject2;
          if (paramAnonymousJsonElement.isJsonArray())
          {
            paramAnonymousJsonWriter.beginArray();
            localObject1 = paramAnonymousJsonElement.getAsJsonArray().iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localObject2 = (JsonElement)((Iterator)localObject1).next();
              write(paramAnonymousJsonWriter, (JsonElement)localObject2);
            }
            paramAnonymousJsonWriter.endArray();
          }
          else if (paramAnonymousJsonElement.isJsonObject())
          {
            paramAnonymousJsonWriter.beginObject();
            localObject1 = paramAnonymousJsonElement.getAsJsonObject().entrySet().iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localObject2 = (Map.Entry)((Iterator)localObject1).next();
              paramAnonymousJsonWriter.name((String)((Map.Entry)localObject2).getKey());
              write(paramAnonymousJsonWriter, (JsonElement)((Map.Entry)localObject2).getValue());
            }
            paramAnonymousJsonWriter.endObject();
          }
          else
          {
            throw new IllegalArgumentException("Couldn't write " + paramAnonymousJsonElement.getClass());
          }
        }
      }
    }
  };
  public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
  public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory()
  {
    public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
    {
      Class localClass = paramAnonymousTypeToken.getRawType();
      if ((!Enum.class.isAssignableFrom(localClass)) || (localClass == Enum.class)) {
        return null;
      }
      if (!localClass.isEnum()) {
        localClass = localClass.getSuperclass();
      }
      return new TypeAdapters.EnumTypeAdapter(localClass);
    }
  };
  
  public static TypeAdapterFactory newFactory(TypeToken paramTypeToken, final TypeAdapter paramTypeAdapter)
  {
    new TypeAdapterFactory()
    {
      public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
      {
        return paramAnonymousTypeToken.equals(this.val$type) ? paramTypeAdapter : null;
      }
    };
  }
  
  public static TypeAdapterFactory newFactory(Class paramClass, final TypeAdapter paramTypeAdapter)
  {
    new TypeAdapterFactory()
    {
      public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
      {
        return paramAnonymousTypeToken.getRawType() == this.val$type ? paramTypeAdapter : null;
      }
      
      public String toString()
      {
        return "Factory[type=" + this.val$type.getName() + ",adapter=" + paramTypeAdapter + "]";
      }
    };
  }
  
  public static TypeAdapterFactory newFactory(Class paramClass1, final Class paramClass2, final TypeAdapter paramTypeAdapter)
  {
    new TypeAdapterFactory()
    {
      public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
      {
        Class localClass = paramAnonymousTypeToken.getRawType();
        return (localClass == this.val$unboxed) || (localClass == paramClass2) ? paramTypeAdapter : null;
      }
      
      public String toString()
      {
        return "Factory[type=" + paramClass2.getName() + "+" + this.val$unboxed.getName() + ",adapter=" + paramTypeAdapter + "]";
      }
    };
  }
  
  public static TypeAdapterFactory newFactoryForMultipleTypes(Class paramClass1, final Class paramClass2, final TypeAdapter paramTypeAdapter)
  {
    new TypeAdapterFactory()
    {
      public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
      {
        Class localClass = paramAnonymousTypeToken.getRawType();
        return (localClass == this.val$base) || (localClass == paramClass2) ? paramTypeAdapter : null;
      }
      
      public String toString()
      {
        return "Factory[type=" + this.val$base.getName() + "+" + paramClass2.getName() + ",adapter=" + paramTypeAdapter + "]";
      }
    };
  }
  
  public static TypeAdapterFactory newTypeHierarchyFactory(Class paramClass, final TypeAdapter paramTypeAdapter)
  {
    new TypeAdapterFactory()
    {
      public TypeAdapter create(Gson paramAnonymousGson, TypeToken paramAnonymousTypeToken)
      {
        return this.val$clazz.isAssignableFrom(paramAnonymousTypeToken.getRawType()) ? paramTypeAdapter : null;
      }
      
      public String toString()
      {
        return "Factory[typeHierarchy=" + this.val$clazz.getName() + ",adapter=" + paramTypeAdapter + "]";
      }
    };
  }
  
  private static final class EnumTypeAdapter
    extends TypeAdapter
  {
    private final Map nameToConstant = new HashMap();
    private final Map constantToName = new HashMap();
    
    public EnumTypeAdapter(Class paramClass)
    {
      try
      {
        for (Enum localEnum : (Enum[])paramClass.getEnumConstants())
        {
          String str = localEnum.name();
          SerializedName localSerializedName = (SerializedName)paramClass.getField(str).getAnnotation(SerializedName.class);
          if (localSerializedName != null) {
            str = localSerializedName.value();
          }
          this.nameToConstant.put(str, localEnum);
          this.constantToName.put(localEnum, str);
        }
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        throw new AssertionError();
      }
    }
    
    public Enum read(JsonReader paramJsonReader)
      throws IOException
    {
      if (paramJsonReader.peek() == JsonToken.NULL)
      {
        paramJsonReader.nextNull();
        return null;
      }
      return (Enum)this.nameToConstant.get(paramJsonReader.nextString());
    }
    
    public void write(JsonWriter paramJsonWriter, Enum paramEnum)
      throws IOException
    {
      paramJsonWriter.value(paramEnum == null ? null : (String)this.constantToName.get(paramEnum));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\bind\TypeAdapters.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */