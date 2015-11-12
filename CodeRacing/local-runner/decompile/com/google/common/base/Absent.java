package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Collections;
import java.util.Set;

@GwtCompatible
final class Absent
  extends Optional
{
  static final Absent INSTANCE = new Absent();
  private static final long serialVersionUID = 0L;
  
  public boolean isPresent()
  {
    return false;
  }
  
  public Object get()
  {
    throw new IllegalStateException("Optional.get() cannot be called on an absent value");
  }
  
  public Object or(Object paramObject)
  {
    return Preconditions.checkNotNull(paramObject, "use Optional.orNull() instead of Optional.or(null)");
  }
  
  public Optional or(Optional paramOptional)
  {
    return (Optional)Preconditions.checkNotNull(paramOptional);
  }
  
  public Object or(Supplier paramSupplier)
  {
    return Preconditions.checkNotNull(paramSupplier.get(), "use Optional.orNull() instead of a Supplier that returns null");
  }
  
  public Object orNull()
  {
    return null;
  }
  
  public Set asSet()
  {
    return Collections.emptySet();
  }
  
  public Optional transform(Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    return Optional.absent();
  }
  
  public boolean equals(Object paramObject)
  {
    return paramObject == this;
  }
  
  public int hashCode()
  {
    return 1502476572;
  }
  
  public String toString()
  {
    return "Optional.absent()";
  }
  
  private Object readResolve()
  {
    return INSTANCE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Absent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */