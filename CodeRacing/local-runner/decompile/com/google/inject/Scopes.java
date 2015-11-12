package com.google.inject;

import com.google.inject.internal.CircularDependencyProxy;
import com.google.inject.internal.LinkedBindingImpl;
import com.google.inject.internal.SingletonScope;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.PrivateElements;

public class Scopes
{
  public static final Scope SINGLETON = new SingletonScope();
  public static final Scope NO_SCOPE = new Scope()
  {
    public Provider scope(Key paramAnonymousKey, Provider paramAnonymousProvider)
    {
      return paramAnonymousProvider;
    }
    
    public String toString()
    {
      return "Scopes.NO_SCOPE";
    }
  };
  private static final BindingScopingVisitor IS_SINGLETON_VISITOR = new BindingScopingVisitor()
  {
    public Boolean visitNoScoping()
    {
      return Boolean.valueOf(false);
    }
    
    public Boolean visitScopeAnnotation(Class paramAnonymousClass)
    {
      return Boolean.valueOf((paramAnonymousClass == Singleton.class) || (paramAnonymousClass == javax.inject.Singleton.class));
    }
    
    public Boolean visitScope(Scope paramAnonymousScope)
    {
      return Boolean.valueOf(paramAnonymousScope == Scopes.SINGLETON);
    }
    
    public Boolean visitEagerSingleton()
    {
      return Boolean.valueOf(true);
    }
  };
  
  public static boolean isSingleton(Binding paramBinding)
  {
    for (;;)
    {
      boolean bool = ((Boolean)paramBinding.acceptScopingVisitor(IS_SINGLETON_VISITOR)).booleanValue();
      if (bool) {
        return true;
      }
      Object localObject1;
      Object localObject2;
      if ((paramBinding instanceof LinkedBindingImpl))
      {
        localObject1 = (LinkedBindingImpl)paramBinding;
        localObject2 = ((LinkedBindingImpl)localObject1).getInjector();
        if (localObject2 != null) {
          paramBinding = ((Injector)localObject2).getBinding(((LinkedBindingImpl)localObject1).getLinkedKey());
        } else {
          break;
        }
      }
      else
      {
        if (!(paramBinding instanceof ExposedBinding)) {
          break;
        }
        localObject1 = (ExposedBinding)paramBinding;
        localObject2 = ((ExposedBinding)localObject1).getPrivateElements().getInjector();
        if (localObject2 == null) {
          break;
        }
        paramBinding = ((Injector)localObject2).getBinding(((ExposedBinding)localObject1).getKey());
      }
    }
    return false;
  }
  
  public static boolean isScoped(Binding paramBinding, final Scope paramScope, Class paramClass)
  {
    for (;;)
    {
      boolean bool = ((Boolean)paramBinding.acceptScopingVisitor(new BindingScopingVisitor()
      {
        public Boolean visitNoScoping()
        {
          return Boolean.valueOf(false);
        }
        
        public Boolean visitScopeAnnotation(Class paramAnonymousClass)
        {
          return Boolean.valueOf(paramAnonymousClass == this.val$scopeAnnotation);
        }
        
        public Boolean visitScope(Scope paramAnonymousScope)
        {
          return Boolean.valueOf(paramAnonymousScope == paramScope);
        }
        
        public Boolean visitEagerSingleton()
        {
          return Boolean.valueOf(false);
        }
      })).booleanValue();
      if (bool) {
        return true;
      }
      Object localObject1;
      Object localObject2;
      if ((paramBinding instanceof LinkedBindingImpl))
      {
        localObject1 = (LinkedBindingImpl)paramBinding;
        localObject2 = ((LinkedBindingImpl)localObject1).getInjector();
        if (localObject2 != null) {
          paramBinding = ((Injector)localObject2).getBinding(((LinkedBindingImpl)localObject1).getLinkedKey());
        } else {
          break;
        }
      }
      else
      {
        if (!(paramBinding instanceof ExposedBinding)) {
          break;
        }
        localObject1 = (ExposedBinding)paramBinding;
        localObject2 = ((ExposedBinding)localObject1).getPrivateElements().getInjector();
        if (localObject2 == null) {
          break;
        }
        paramBinding = ((Injector)localObject2).getBinding(((ExposedBinding)localObject1).getKey());
      }
    }
    return false;
  }
  
  public static boolean isCircularProxy(Object paramObject)
  {
    return paramObject instanceof CircularDependencyProxy;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\Scopes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */