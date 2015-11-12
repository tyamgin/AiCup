package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GwtCompatible(emulated=true)
public final class Predicates
{
  private static final Joiner COMMA_JOINER = Joiner.on(",");
  
  @GwtCompatible(serializable=true)
  public static Predicate alwaysTrue()
  {
    return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
  }
  
  @GwtCompatible(serializable=true)
  public static Predicate alwaysFalse()
  {
    return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
  }
  
  @GwtCompatible(serializable=true)
  public static Predicate isNull()
  {
    return ObjectPredicate.IS_NULL.withNarrowedType();
  }
  
  @GwtCompatible(serializable=true)
  public static Predicate notNull()
  {
    return ObjectPredicate.NOT_NULL.withNarrowedType();
  }
  
  public static Predicate not(Predicate paramPredicate)
  {
    return new NotPredicate(paramPredicate);
  }
  
  public static Predicate and(Iterable paramIterable)
  {
    return new AndPredicate(defensiveCopy(paramIterable), null);
  }
  
  public static Predicate and(Predicate... paramVarArgs)
  {
    return new AndPredicate(defensiveCopy(paramVarArgs), null);
  }
  
  public static Predicate and(Predicate paramPredicate1, Predicate paramPredicate2)
  {
    return new AndPredicate(asList((Predicate)Preconditions.checkNotNull(paramPredicate1), (Predicate)Preconditions.checkNotNull(paramPredicate2)), null);
  }
  
  public static Predicate or(Iterable paramIterable)
  {
    return new OrPredicate(defensiveCopy(paramIterable), null);
  }
  
  public static Predicate or(Predicate... paramVarArgs)
  {
    return new OrPredicate(defensiveCopy(paramVarArgs), null);
  }
  
  public static Predicate or(Predicate paramPredicate1, Predicate paramPredicate2)
  {
    return new OrPredicate(asList((Predicate)Preconditions.checkNotNull(paramPredicate1), (Predicate)Preconditions.checkNotNull(paramPredicate2)), null);
  }
  
  public static Predicate equalTo(Object paramObject)
  {
    return paramObject == null ? isNull() : new IsEqualToPredicate(paramObject, null);
  }
  
  @GwtIncompatible("Class.isInstance")
  public static Predicate instanceOf(Class paramClass)
  {
    return new InstanceOfPredicate(paramClass, null);
  }
  
  @GwtIncompatible("Class.isAssignableFrom")
  @Beta
  public static Predicate assignableFrom(Class paramClass)
  {
    return new AssignableFromPredicate(paramClass, null);
  }
  
  public static Predicate in(Collection paramCollection)
  {
    return new InPredicate(paramCollection, null);
  }
  
  public static Predicate compose(Predicate paramPredicate, Function paramFunction)
  {
    return new CompositionPredicate(paramPredicate, paramFunction, null);
  }
  
  @GwtIncompatible("java.util.regex.Pattern")
  public static Predicate containsPattern(String paramString)
  {
    return new ContainsPatternPredicate(paramString);
  }
  
  @GwtIncompatible("java.util.regex.Pattern")
  public static Predicate contains(Pattern paramPattern)
  {
    return new ContainsPatternPredicate(paramPattern);
  }
  
  private static List asList(Predicate paramPredicate1, Predicate paramPredicate2)
  {
    return Arrays.asList(new Predicate[] { paramPredicate1, paramPredicate2 });
  }
  
  private static List defensiveCopy(Object... paramVarArgs)
  {
    return defensiveCopy(Arrays.asList(paramVarArgs));
  }
  
  static List defensiveCopy(Iterable paramIterable)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      localArrayList.add(Preconditions.checkNotNull(localObject));
    }
    return localArrayList;
  }
  
  @GwtIncompatible("Only used by other GWT-incompatible code.")
  private static class ContainsPatternPredicate
    implements Predicate, Serializable
  {
    final Pattern pattern;
    private static final long serialVersionUID = 0L;
    
    ContainsPatternPredicate(Pattern paramPattern)
    {
      this.pattern = ((Pattern)Preconditions.checkNotNull(paramPattern));
    }
    
    ContainsPatternPredicate(String paramString)
    {
      this(Pattern.compile(paramString));
    }
    
    public boolean apply(CharSequence paramCharSequence)
    {
      return this.pattern.matcher(paramCharSequence).find();
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.pattern.pattern(), Integer.valueOf(this.pattern.flags()) });
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ContainsPatternPredicate))
      {
        ContainsPatternPredicate localContainsPatternPredicate = (ContainsPatternPredicate)paramObject;
        return (Objects.equal(this.pattern.pattern(), localContainsPatternPredicate.pattern.pattern())) && (Objects.equal(Integer.valueOf(this.pattern.flags()), Integer.valueOf(localContainsPatternPredicate.pattern.flags())));
      }
      return false;
    }
    
    public String toString()
    {
      return Objects.toStringHelper(this).add("pattern", this.pattern).add("pattern.flags", Integer.toHexString(this.pattern.flags())).toString();
    }
  }
  
  private static class CompositionPredicate
    implements Predicate, Serializable
  {
    final Predicate p;
    final Function f;
    private static final long serialVersionUID = 0L;
    
    private CompositionPredicate(Predicate paramPredicate, Function paramFunction)
    {
      this.p = ((Predicate)Preconditions.checkNotNull(paramPredicate));
      this.f = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public boolean apply(Object paramObject)
    {
      return this.p.apply(this.f.apply(paramObject));
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof CompositionPredicate))
      {
        CompositionPredicate localCompositionPredicate = (CompositionPredicate)paramObject;
        return (this.f.equals(localCompositionPredicate.f)) && (this.p.equals(localCompositionPredicate.p));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.f.hashCode() ^ this.p.hashCode();
    }
    
    public String toString()
    {
      return this.p.toString() + "(" + this.f.toString() + ")";
    }
  }
  
  private static class InPredicate
    implements Predicate, Serializable
  {
    private final Collection target;
    private static final long serialVersionUID = 0L;
    
    private InPredicate(Collection paramCollection)
    {
      this.target = ((Collection)Preconditions.checkNotNull(paramCollection));
    }
    
    public boolean apply(Object paramObject)
    {
      try
      {
        return this.target.contains(paramObject);
      }
      catch (NullPointerException localNullPointerException)
      {
        return false;
      }
      catch (ClassCastException localClassCastException) {}
      return false;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof InPredicate))
      {
        InPredicate localInPredicate = (InPredicate)paramObject;
        return this.target.equals(localInPredicate.target);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.target.hashCode();
    }
    
    public String toString()
    {
      return "In(" + this.target + ")";
    }
  }
  
  @GwtIncompatible("Class.isAssignableFrom")
  private static class AssignableFromPredicate
    implements Predicate, Serializable
  {
    private final Class clazz;
    private static final long serialVersionUID = 0L;
    
    private AssignableFromPredicate(Class paramClass)
    {
      this.clazz = ((Class)Preconditions.checkNotNull(paramClass));
    }
    
    public boolean apply(Class paramClass)
    {
      return this.clazz.isAssignableFrom(paramClass);
    }
    
    public int hashCode()
    {
      return this.clazz.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof AssignableFromPredicate))
      {
        AssignableFromPredicate localAssignableFromPredicate = (AssignableFromPredicate)paramObject;
        return this.clazz == localAssignableFromPredicate.clazz;
      }
      return false;
    }
    
    public String toString()
    {
      return "IsAssignableFrom(" + this.clazz.getName() + ")";
    }
  }
  
  @GwtIncompatible("Class.isInstance")
  private static class InstanceOfPredicate
    implements Predicate, Serializable
  {
    private final Class clazz;
    private static final long serialVersionUID = 0L;
    
    private InstanceOfPredicate(Class paramClass)
    {
      this.clazz = ((Class)Preconditions.checkNotNull(paramClass));
    }
    
    public boolean apply(Object paramObject)
    {
      return this.clazz.isInstance(paramObject);
    }
    
    public int hashCode()
    {
      return this.clazz.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof InstanceOfPredicate))
      {
        InstanceOfPredicate localInstanceOfPredicate = (InstanceOfPredicate)paramObject;
        return this.clazz == localInstanceOfPredicate.clazz;
      }
      return false;
    }
    
    public String toString()
    {
      return "IsInstanceOf(" + this.clazz.getName() + ")";
    }
  }
  
  private static class IsEqualToPredicate
    implements Predicate, Serializable
  {
    private final Object target;
    private static final long serialVersionUID = 0L;
    
    private IsEqualToPredicate(Object paramObject)
    {
      this.target = paramObject;
    }
    
    public boolean apply(Object paramObject)
    {
      return this.target.equals(paramObject);
    }
    
    public int hashCode()
    {
      return this.target.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof IsEqualToPredicate))
      {
        IsEqualToPredicate localIsEqualToPredicate = (IsEqualToPredicate)paramObject;
        return this.target.equals(localIsEqualToPredicate.target);
      }
      return false;
    }
    
    public String toString()
    {
      return "IsEqualTo(" + this.target + ")";
    }
  }
  
  private static class OrPredicate
    implements Predicate, Serializable
  {
    private final List components;
    private static final long serialVersionUID = 0L;
    
    private OrPredicate(List paramList)
    {
      this.components = paramList;
    }
    
    public boolean apply(Object paramObject)
    {
      for (int i = 0; i < this.components.size(); i++) {
        if (((Predicate)this.components.get(i)).apply(paramObject)) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.components.hashCode() + 87855567;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof OrPredicate))
      {
        OrPredicate localOrPredicate = (OrPredicate)paramObject;
        return this.components.equals(localOrPredicate.components);
      }
      return false;
    }
    
    public String toString()
    {
      return "Or(" + Predicates.COMMA_JOINER.join(this.components) + ")";
    }
  }
  
  private static class AndPredicate
    implements Predicate, Serializable
  {
    private final List components;
    private static final long serialVersionUID = 0L;
    
    private AndPredicate(List paramList)
    {
      this.components = paramList;
    }
    
    public boolean apply(Object paramObject)
    {
      for (int i = 0; i < this.components.size(); i++) {
        if (!((Predicate)this.components.get(i)).apply(paramObject)) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      return this.components.hashCode() + 306654252;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof AndPredicate))
      {
        AndPredicate localAndPredicate = (AndPredicate)paramObject;
        return this.components.equals(localAndPredicate.components);
      }
      return false;
    }
    
    public String toString()
    {
      return "And(" + Predicates.COMMA_JOINER.join(this.components) + ")";
    }
  }
  
  private static class NotPredicate
    implements Predicate, Serializable
  {
    final Predicate predicate;
    private static final long serialVersionUID = 0L;
    
    NotPredicate(Predicate paramPredicate)
    {
      this.predicate = ((Predicate)Preconditions.checkNotNull(paramPredicate));
    }
    
    public boolean apply(Object paramObject)
    {
      return !this.predicate.apply(paramObject);
    }
    
    public int hashCode()
    {
      return this.predicate.hashCode() ^ 0xFFFFFFFF;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof NotPredicate))
      {
        NotPredicate localNotPredicate = (NotPredicate)paramObject;
        return this.predicate.equals(localNotPredicate.predicate);
      }
      return false;
    }
    
    public String toString()
    {
      return "Not(" + this.predicate.toString() + ")";
    }
  }
  
  static abstract enum ObjectPredicate
    implements Predicate
  {
    ALWAYS_TRUE,  ALWAYS_FALSE,  IS_NULL,  NOT_NULL;
    
    Predicate withNarrowedType()
    {
      return this;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Predicates.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */