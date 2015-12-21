package com.google.inject.util;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderWithDependencies;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Providers
{
  public static com.google.inject.Provider of(Object paramObject)
  {
    return new ConstantProvider(paramObject, null);
  }
  
  public static com.google.inject.Provider guicify(javax.inject.Provider paramProvider)
  {
    if ((paramProvider instanceof com.google.inject.Provider)) {
      return (com.google.inject.Provider)paramProvider;
    }
    javax.inject.Provider localProvider = (javax.inject.Provider)Preconditions.checkNotNull(paramProvider, "provider");
    Set localSet = InjectionPoint.forInstanceMethodsAndFields(paramProvider.getClass());
    if (localSet.isEmpty()) {
      return new GuicifiedProvider(localProvider, null);
    }
    HashSet localHashSet = Sets.newHashSet();
    Object localObject = localSet.iterator();
    while (((Iterator)localObject).hasNext())
    {
      InjectionPoint localInjectionPoint = (InjectionPoint)((Iterator)localObject).next();
      localHashSet.addAll(localInjectionPoint.getDependencies());
    }
    localObject = ImmutableSet.copyOf(localHashSet);
    return new GuicifiedProviderWithDependencies((Set)localObject, localProvider, null);
  }
  
  private static final class GuicifiedProviderWithDependencies
    extends Providers.GuicifiedProvider
    implements ProviderWithDependencies
  {
    private final Set dependencies;
    
    private GuicifiedProviderWithDependencies(Set paramSet, javax.inject.Provider paramProvider)
    {
      super(null);
      this.dependencies = paramSet;
    }
    
    @Inject
    void initialize(Injector paramInjector)
    {
      paramInjector.injectMembers(this.delegate);
    }
    
    public Set getDependencies()
    {
      return this.dependencies;
    }
  }
  
  private static class GuicifiedProvider
    implements com.google.inject.Provider
  {
    protected final javax.inject.Provider delegate;
    
    private GuicifiedProvider(javax.inject.Provider paramProvider)
    {
      this.delegate = paramProvider;
    }
    
    public Object get()
    {
      return this.delegate.get();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.delegate));
      return 11 + str.length() + "guicified(" + str + ")";
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof GuicifiedProvider)) && (Objects.equal(this.delegate, ((GuicifiedProvider)paramObject).delegate));
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.delegate });
    }
  }
  
  private static final class ConstantProvider
    implements com.google.inject.Provider
  {
    private final Object instance;
    
    private ConstantProvider(Object paramObject)
    {
      this.instance = paramObject;
    }
    
    public Object get()
    {
      return this.instance;
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.instance));
      return 4 + str.length() + "of(" + str + ")";
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof ConstantProvider)) && (Objects.equal(this.instance, ((ConstantProvider)paramObject).instance));
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.instance });
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\util\Providers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */