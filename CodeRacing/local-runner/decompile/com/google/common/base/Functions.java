package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Map;

@GwtCompatible
public final class Functions
{
  public static Function toStringFunction()
  {
    return ToStringFunction.INSTANCE;
  }
  
  public static Function identity()
  {
    return IdentityFunction.INSTANCE;
  }
  
  public static Function forMap(Map paramMap)
  {
    return new FunctionForMapNoDefault(paramMap);
  }
  
  public static Function forMap(Map paramMap, Object paramObject)
  {
    return new ForMapWithDefault(paramMap, paramObject);
  }
  
  public static Function compose(Function paramFunction1, Function paramFunction2)
  {
    return new FunctionComposition(paramFunction1, paramFunction2);
  }
  
  public static Function forPredicate(Predicate paramPredicate)
  {
    return new PredicateFunction(paramPredicate, null);
  }
  
  public static Function constant(Object paramObject)
  {
    return new ConstantFunction(paramObject);
  }
  
  @Beta
  public static Function forSupplier(Supplier paramSupplier)
  {
    return new SupplierFunction(paramSupplier, null);
  }
  
  private static class SupplierFunction
    implements Function, Serializable
  {
    private final Supplier supplier;
    private static final long serialVersionUID = 0L;
    
    private SupplierFunction(Supplier paramSupplier)
    {
      this.supplier = ((Supplier)Preconditions.checkNotNull(paramSupplier));
    }
    
    public Object apply(Object paramObject)
    {
      return this.supplier.get();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof SupplierFunction))
      {
        SupplierFunction localSupplierFunction = (SupplierFunction)paramObject;
        return this.supplier.equals(localSupplierFunction.supplier);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.supplier.hashCode();
    }
    
    public String toString()
    {
      return "forSupplier(" + this.supplier + ")";
    }
  }
  
  private static class ConstantFunction
    implements Function, Serializable
  {
    private final Object value;
    private static final long serialVersionUID = 0L;
    
    public ConstantFunction(Object paramObject)
    {
      this.value = paramObject;
    }
    
    public Object apply(Object paramObject)
    {
      return this.value;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ConstantFunction))
      {
        ConstantFunction localConstantFunction = (ConstantFunction)paramObject;
        return Objects.equal(this.value, localConstantFunction.value);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.value == null ? 0 : this.value.hashCode();
    }
    
    public String toString()
    {
      return "constant(" + this.value + ")";
    }
  }
  
  private static class PredicateFunction
    implements Function, Serializable
  {
    private final Predicate predicate;
    private static final long serialVersionUID = 0L;
    
    private PredicateFunction(Predicate paramPredicate)
    {
      this.predicate = ((Predicate)Preconditions.checkNotNull(paramPredicate));
    }
    
    public Boolean apply(Object paramObject)
    {
      return Boolean.valueOf(this.predicate.apply(paramObject));
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof PredicateFunction))
      {
        PredicateFunction localPredicateFunction = (PredicateFunction)paramObject;
        return this.predicate.equals(localPredicateFunction.predicate);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.predicate.hashCode();
    }
    
    public String toString()
    {
      return "forPredicate(" + this.predicate + ")";
    }
  }
  
  private static class FunctionComposition
    implements Function, Serializable
  {
    private final Function g;
    private final Function f;
    private static final long serialVersionUID = 0L;
    
    public FunctionComposition(Function paramFunction1, Function paramFunction2)
    {
      this.g = ((Function)Preconditions.checkNotNull(paramFunction1));
      this.f = ((Function)Preconditions.checkNotNull(paramFunction2));
    }
    
    public Object apply(Object paramObject)
    {
      return this.g.apply(this.f.apply(paramObject));
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof FunctionComposition))
      {
        FunctionComposition localFunctionComposition = (FunctionComposition)paramObject;
        return (this.f.equals(localFunctionComposition.f)) && (this.g.equals(localFunctionComposition.g));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.f.hashCode() ^ this.g.hashCode();
    }
    
    public String toString()
    {
      return this.g.toString() + "(" + this.f.toString() + ")";
    }
  }
  
  private static class ForMapWithDefault
    implements Function, Serializable
  {
    final Map map;
    final Object defaultValue;
    private static final long serialVersionUID = 0L;
    
    ForMapWithDefault(Map paramMap, Object paramObject)
    {
      this.map = ((Map)Preconditions.checkNotNull(paramMap));
      this.defaultValue = paramObject;
    }
    
    public Object apply(Object paramObject)
    {
      Object localObject = this.map.get(paramObject);
      return (localObject != null) || (this.map.containsKey(paramObject)) ? localObject : this.defaultValue;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ForMapWithDefault))
      {
        ForMapWithDefault localForMapWithDefault = (ForMapWithDefault)paramObject;
        return (this.map.equals(localForMapWithDefault.map)) && (Objects.equal(this.defaultValue, localForMapWithDefault.defaultValue));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.map, this.defaultValue });
    }
    
    public String toString()
    {
      return "forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
    }
  }
  
  private static class FunctionForMapNoDefault
    implements Function, Serializable
  {
    final Map map;
    private static final long serialVersionUID = 0L;
    
    FunctionForMapNoDefault(Map paramMap)
    {
      this.map = ((Map)Preconditions.checkNotNull(paramMap));
    }
    
    public Object apply(Object paramObject)
    {
      Object localObject = this.map.get(paramObject);
      Preconditions.checkArgument((localObject != null) || (this.map.containsKey(paramObject)), "Key '%s' not present in map", new Object[] { paramObject });
      return localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof FunctionForMapNoDefault))
      {
        FunctionForMapNoDefault localFunctionForMapNoDefault = (FunctionForMapNoDefault)paramObject;
        return this.map.equals(localFunctionForMapNoDefault.map);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.map.hashCode();
    }
    
    public String toString()
    {
      return "forMap(" + this.map + ")";
    }
  }
  
  private static enum IdentityFunction
    implements Function
  {
    INSTANCE;
    
    public Object apply(Object paramObject)
    {
      return paramObject;
    }
    
    public String toString()
    {
      return "identity";
    }
  }
  
  private static enum ToStringFunction
    implements Function
  {
    INSTANCE;
    
    public String apply(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return paramObject.toString();
    }
    
    public String toString()
    {
      return "toString";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Functions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */