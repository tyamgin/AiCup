package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.DependencyAndSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class InternalContext
{
  private final InjectorImpl.InjectorOptions options;
  private Map constructionContexts = Maps.newHashMap();
  private Dependency dependency;
  private final DependencyStack state = new DependencyStack(null);
  
  InternalContext(InjectorImpl.InjectorOptions paramInjectorOptions)
  {
    this.options = paramInjectorOptions;
  }
  
  public InjectorImpl.InjectorOptions getInjectorOptions()
  {
    return this.options;
  }
  
  public ConstructionContext getConstructionContext(Object paramObject)
  {
    ConstructionContext localConstructionContext = (ConstructionContext)this.constructionContexts.get(paramObject);
    if (localConstructionContext == null)
    {
      localConstructionContext = new ConstructionContext();
      this.constructionContexts.put(paramObject, localConstructionContext);
    }
    return localConstructionContext;
  }
  
  public Dependency getDependency()
  {
    return this.dependency;
  }
  
  public Dependency pushDependency(Dependency paramDependency, Object paramObject)
  {
    Dependency localDependency = this.dependency;
    this.dependency = paramDependency;
    this.state.add(paramDependency, paramObject);
    return localDependency;
  }
  
  public void popStateAndSetDependency(Dependency paramDependency)
  {
    this.state.pop();
    this.dependency = paramDependency;
  }
  
  public void pushState(Key paramKey, Object paramObject)
  {
    this.state.add(paramKey, paramObject);
  }
  
  public void popState()
  {
    this.state.pop();
  }
  
  public List getDependencyChain()
  {
    ImmutableList.Builder localBuilder = ImmutableList.builder();
    for (int i = 0; i < this.state.size(); i += 2)
    {
      Object localObject = this.state.get(i);
      Dependency localDependency;
      if ((localObject instanceof Key)) {
        localDependency = Dependency.get((Key)localObject);
      } else {
        localDependency = (Dependency)localObject;
      }
      localBuilder.add(new DependencyAndSource(localDependency, this.state.get(i + 1)));
    }
    return localBuilder.build();
  }
  
  private static final class DependencyStack
  {
    private Object[] elements = new Object[16];
    private int size = 0;
    
    public void add(Object paramObject1, Object paramObject2)
    {
      if (this.elements.length < this.size + 2) {
        this.elements = Arrays.copyOf(this.elements, this.elements.length * 3 / 2 + 2);
      }
      this.elements[(this.size++)] = paramObject1;
      this.elements[(this.size++)] = paramObject2;
    }
    
    public void pop()
    {
      this.elements[(--this.size)] = null;
      this.elements[(--this.size)] = null;
    }
    
    public Object get(int paramInt)
    {
      return this.elements[paramInt];
    }
    
    public int size()
    {
      return this.size;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InternalContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */