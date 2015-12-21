package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Excluder
  implements TypeAdapterFactory, Cloneable
{
  private static final double IGNORE_VERSIONS = -1.0D;
  public static final Excluder DEFAULT = new Excluder();
  private double version = -1.0D;
  private int modifiers = 136;
  private boolean serializeInnerClasses = true;
  private boolean requireExpose;
  private List serializationStrategies = Collections.emptyList();
  private List deserializationStrategies = Collections.emptyList();
  
  protected Excluder clone()
  {
    try
    {
      return (Excluder)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError();
    }
  }
  
  public Excluder withVersion(double paramDouble)
  {
    Excluder localExcluder = clone();
    localExcluder.version = paramDouble;
    return localExcluder;
  }
  
  public Excluder withModifiers(int... paramVarArgs)
  {
    Excluder localExcluder = clone();
    localExcluder.modifiers = 0;
    for (int k : paramVarArgs) {
      localExcluder.modifiers |= k;
    }
    return localExcluder;
  }
  
  public Excluder disableInnerClassSerialization()
  {
    Excluder localExcluder = clone();
    localExcluder.serializeInnerClasses = false;
    return localExcluder;
  }
  
  public Excluder excludeFieldsWithoutExposeAnnotation()
  {
    Excluder localExcluder = clone();
    localExcluder.requireExpose = true;
    return localExcluder;
  }
  
  public Excluder withExclusionStrategy(ExclusionStrategy paramExclusionStrategy, boolean paramBoolean1, boolean paramBoolean2)
  {
    Excluder localExcluder = clone();
    if (paramBoolean1)
    {
      localExcluder.serializationStrategies = new ArrayList(this.serializationStrategies);
      localExcluder.serializationStrategies.add(paramExclusionStrategy);
    }
    if (paramBoolean2)
    {
      localExcluder.deserializationStrategies = new ArrayList(this.deserializationStrategies);
      localExcluder.deserializationStrategies.add(paramExclusionStrategy);
    }
    return localExcluder;
  }
  
  public TypeAdapter create(final Gson paramGson, final TypeToken paramTypeToken)
  {
    Class localClass = paramTypeToken.getRawType();
    final boolean bool1 = excludeClass(localClass, true);
    final boolean bool2 = excludeClass(localClass, false);
    if ((!bool1) && (!bool2)) {
      return null;
    }
    new TypeAdapter()
    {
      private TypeAdapter delegate;
      
      public Object read(JsonReader paramAnonymousJsonReader)
        throws IOException
      {
        if (bool2)
        {
          paramAnonymousJsonReader.skipValue();
          return null;
        }
        return delegate().read(paramAnonymousJsonReader);
      }
      
      public void write(JsonWriter paramAnonymousJsonWriter, Object paramAnonymousObject)
        throws IOException
      {
        if (bool1)
        {
          paramAnonymousJsonWriter.nullValue();
          return;
        }
        delegate().write(paramAnonymousJsonWriter, paramAnonymousObject);
      }
      
      private TypeAdapter delegate()
      {
        TypeAdapter localTypeAdapter = this.delegate;
        return localTypeAdapter != null ? localTypeAdapter : (this.delegate = paramGson.getDelegateAdapter(Excluder.this, paramTypeToken));
      }
    };
  }
  
  public boolean excludeField(Field paramField, boolean paramBoolean)
  {
    if ((this.modifiers & paramField.getModifiers()) != 0) {
      return true;
    }
    if ((this.version != -1.0D) && (!isValidVersion((Since)paramField.getAnnotation(Since.class), (Until)paramField.getAnnotation(Until.class)))) {
      return true;
    }
    if (paramField.isSynthetic()) {
      return true;
    }
    if (this.requireExpose)
    {
      localObject = (Expose)paramField.getAnnotation(Expose.class);
      if ((localObject == null) || (paramBoolean ? !((Expose)localObject).serialize() : !((Expose)localObject).deserialize())) {
        return true;
      }
    }
    if ((!this.serializeInnerClasses) && (isInnerClass(paramField.getType()))) {
      return true;
    }
    if (isAnonymousOrLocal(paramField.getType())) {
      return true;
    }
    Object localObject = paramBoolean ? this.serializationStrategies : this.deserializationStrategies;
    if (!((List)localObject).isEmpty())
    {
      FieldAttributes localFieldAttributes = new FieldAttributes(paramField);
      Iterator localIterator = ((List)localObject).iterator();
      while (localIterator.hasNext())
      {
        ExclusionStrategy localExclusionStrategy = (ExclusionStrategy)localIterator.next();
        if (localExclusionStrategy.shouldSkipField(localFieldAttributes)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean excludeClass(Class paramClass, boolean paramBoolean)
  {
    if ((this.version != -1.0D) && (!isValidVersion((Since)paramClass.getAnnotation(Since.class), (Until)paramClass.getAnnotation(Until.class)))) {
      return true;
    }
    if ((!this.serializeInnerClasses) && (isInnerClass(paramClass))) {
      return true;
    }
    if (isAnonymousOrLocal(paramClass)) {
      return true;
    }
    List localList = paramBoolean ? this.serializationStrategies : this.deserializationStrategies;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ExclusionStrategy localExclusionStrategy = (ExclusionStrategy)localIterator.next();
      if (localExclusionStrategy.shouldSkipClass(paramClass)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isAnonymousOrLocal(Class paramClass)
  {
    return (!Enum.class.isAssignableFrom(paramClass)) && ((paramClass.isAnonymousClass()) || (paramClass.isLocalClass()));
  }
  
  private boolean isInnerClass(Class paramClass)
  {
    return (paramClass.isMemberClass()) && (!isStatic(paramClass));
  }
  
  private boolean isStatic(Class paramClass)
  {
    return (paramClass.getModifiers() & 0x8) != 0;
  }
  
  private boolean isValidVersion(Since paramSince, Until paramUntil)
  {
    return (isValidSince(paramSince)) && (isValidUntil(paramUntil));
  }
  
  private boolean isValidSince(Since paramSince)
  {
    if (paramSince != null)
    {
      double d = paramSince.value();
      if (d > this.version) {
        return false;
      }
    }
    return true;
  }
  
  private boolean isValidUntil(Until paramUntil)
  {
    if (paramUntil != null)
    {
      double d = paramUntil.value();
      if (d <= this.version) {
        return false;
      }
    }
    return true;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\Excluder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */