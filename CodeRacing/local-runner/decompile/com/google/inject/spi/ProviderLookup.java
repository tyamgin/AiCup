package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import java.util.Set;

public final class ProviderLookup
  implements Element
{
  private final Object source;
  private final Dependency dependency;
  private Provider delegate;
  
  public ProviderLookup(Object paramObject, Key paramKey)
  {
    this(paramObject, Dependency.get((Key)Preconditions.checkNotNull(paramKey, "key")));
  }
  
  public ProviderLookup(Object paramObject, Dependency paramDependency)
  {
    this.source = Preconditions.checkNotNull(paramObject, "source");
    this.dependency = ((Dependency)Preconditions.checkNotNull(paramDependency, "dependency"));
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Key getKey()
  {
    return this.dependency.getKey();
  }
  
  public Dependency getDependency()
  {
    return this.dependency;
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public void initializeDelegate(Provider paramProvider)
  {
    Preconditions.checkState(this.delegate == null, "delegate already initialized");
    this.delegate = ((Provider)Preconditions.checkNotNull(paramProvider, "delegate"));
  }
  
  public void applyTo(Binder paramBinder)
  {
    initializeDelegate(paramBinder.withSource(getSource()).getProvider(this.dependency));
  }
  
  public Provider getDelegate()
  {
    return this.delegate;
  }
  
  public Provider getProvider()
  {
    new ProviderWithDependencies()
    {
      public Object get()
      {
        Preconditions.checkState(ProviderLookup.this.delegate != null, "This Provider cannot be used until the Injector has been created.");
        return ProviderLookup.this.delegate.get();
      }
      
      public Set getDependencies()
      {
        Key localKey = ProviderLookup.this.getKey().ofType(Types.providerOf(ProviderLookup.this.getKey().getTypeLiteral().getType()));
        return ImmutableSet.of(Dependency.get(localKey));
      }
      
      public String toString()
      {
        String str = String.valueOf(String.valueOf(ProviderLookup.this.getKey().getTypeLiteral()));
        return 10 + str.length() + "Provider<" + str + ">";
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProviderLookup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */