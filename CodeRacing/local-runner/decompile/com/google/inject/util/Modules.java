package com.google.inject.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.Scope;
import com.google.inject.internal.Errors;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ScopeBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Modules
{
  public static final Module EMPTY_MODULE = new EmptyModule(null);
  
  public static OverriddenModuleBuilder override(Module... paramVarArgs)
  {
    return new RealOverriddenModuleBuilder(Arrays.asList(paramVarArgs), null);
  }
  
  public static OverriddenModuleBuilder override(Iterable paramIterable)
  {
    return new RealOverriddenModuleBuilder(paramIterable, null);
  }
  
  public static Module combine(Module... paramVarArgs)
  {
    return combine(ImmutableSet.copyOf(paramVarArgs));
  }
  
  public static Module combine(Iterable paramIterable)
  {
    return new CombinedModule(paramIterable);
  }
  
  private static Module extractScanners(Iterable paramIterable)
  {
    ArrayList localArrayList = Lists.newArrayList();
    DefaultElementVisitor local1 = new DefaultElementVisitor()
    {
      public Void visit(ModuleAnnotatedMethodScannerBinding paramAnonymousModuleAnnotatedMethodScannerBinding)
      {
        this.val$scanners.add(paramAnonymousModuleAnnotatedMethodScannerBinding);
        return null;
      }
    };
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      localElement.acceptVisitor(local1);
    }
    new AbstractModule()
    {
      protected void configure()
      {
        Iterator localIterator = this.val$scanners.iterator();
        while (localIterator.hasNext())
        {
          ModuleAnnotatedMethodScannerBinding localModuleAnnotatedMethodScannerBinding = (ModuleAnnotatedMethodScannerBinding)localIterator.next();
          localModuleAnnotatedMethodScannerBinding.applyTo(binder());
        }
      }
    };
  }
  
  private static class ModuleWriter
    extends DefaultElementVisitor
  {
    protected final Binder binder;
    
    ModuleWriter(Binder paramBinder)
    {
      this.binder = paramBinder.skipSources(new Class[] { getClass() });
    }
    
    protected Void visitOther(Element paramElement)
    {
      paramElement.applyTo(this.binder);
      return null;
    }
    
    void writeAll(Iterable paramIterable)
    {
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        Element localElement = (Element)localIterator.next();
        localElement.acceptVisitor(this);
      }
    }
  }
  
  static class OverrideModule
    extends AbstractModule
  {
    private final ImmutableSet overrides;
    private final ImmutableSet baseModules;
    
    OverrideModule(Iterable paramIterable, ImmutableSet paramImmutableSet)
    {
      this.overrides = ImmutableSet.copyOf(paramIterable);
      this.baseModules = paramImmutableSet;
    }
    
    public void configure()
    {
      Object localObject1 = binder();
      List localList = Elements.getElements(currentStage(), this.baseModules);
      if (localList.size() == 1)
      {
        localObject2 = (Element)Iterables.getOnlyElement(localList);
        if ((localObject2 instanceof PrivateElements))
        {
          localObject3 = (PrivateElements)localObject2;
          localObject4 = ((Binder)localObject1).newPrivateBinder().withSource(((PrivateElements)localObject3).getSource());
          localObject5 = ((PrivateElements)localObject3).getExposedKeys().iterator();
          while (((Iterator)localObject5).hasNext())
          {
            localObject6 = (Key)((Iterator)localObject5).next();
            ((PrivateBinder)localObject4).withSource(((PrivateElements)localObject3).getExposedSource((Key)localObject6)).expose((Key)localObject6);
          }
          localObject1 = localObject4;
          localList = ((PrivateElements)localObject3).getElements();
        }
      }
      Object localObject2 = ((Binder)localObject1).skipSources(new Class[] { getClass() });
      Object localObject3 = new LinkedHashSet(localList);
      Object localObject4 = Modules.extractScanners((Iterable)localObject3);
      Object localObject5 = Elements.getElements(currentStage(), ImmutableList.builder().addAll(this.overrides).add(localObject4).build());
      final Object localObject6 = Sets.newHashSet();
      final HashMap localHashMap1 = Maps.newHashMap();
      new Modules.ModuleWriter((Binder)localObject2)
      {
        public Void visit(Binding paramAnonymousBinding)
        {
          localObject6.add(paramAnonymousBinding.getKey());
          return (Void)super.visit(paramAnonymousBinding);
        }
        
        public Void visit(ScopeBinding paramAnonymousScopeBinding)
        {
          localHashMap1.put(paramAnonymousScopeBinding.getAnnotationType(), paramAnonymousScopeBinding);
          return (Void)super.visit(paramAnonymousScopeBinding);
        }
        
        public Void visit(PrivateElements paramAnonymousPrivateElements)
        {
          localObject6.addAll(paramAnonymousPrivateElements.getExposedKeys());
          return (Void)super.visit(paramAnonymousPrivateElements);
        }
      }.writeAll((Iterable)localObject5);
      final HashMap localHashMap2 = Maps.newHashMap();
      final ArrayList localArrayList = Lists.newArrayList();
      new Modules.ModuleWriter((Binder)localObject2)
      {
        public Void visit(Binding paramAnonymousBinding)
        {
          if (!localObject6.remove(paramAnonymousBinding.getKey()))
          {
            super.visit(paramAnonymousBinding);
            Scope localScope = Modules.OverrideModule.this.getScopeInstanceOrNull(paramAnonymousBinding);
            if (localScope != null)
            {
              Object localObject = (List)localHashMap2.get(localScope);
              if (localObject == null)
              {
                localObject = Lists.newArrayList();
                localHashMap2.put(localScope, localObject);
              }
              ((List)localObject).add(paramAnonymousBinding.getSource());
            }
          }
          return null;
        }
        
        void rewrite(Binder paramAnonymousBinder, PrivateElements paramAnonymousPrivateElements, Set paramAnonymousSet)
        {
          PrivateBinder localPrivateBinder = paramAnonymousBinder.withSource(paramAnonymousPrivateElements.getSource()).newPrivateBinder();
          HashSet localHashSet = Sets.newHashSet();
          Iterator localIterator = paramAnonymousPrivateElements.getExposedKeys().iterator();
          Object localObject;
          while (localIterator.hasNext())
          {
            localObject = (Key)localIterator.next();
            if (paramAnonymousSet.remove(localObject)) {
              localHashSet.add(localObject);
            } else {
              localPrivateBinder.withSource(paramAnonymousPrivateElements.getExposedSource((Key)localObject)).expose((Key)localObject);
            }
          }
          localIterator = paramAnonymousPrivateElements.getElements().iterator();
          while (localIterator.hasNext())
          {
            localObject = (Element)localIterator.next();
            if ((!(localObject instanceof Binding)) || (!localHashSet.remove(((Binding)localObject).getKey()))) {
              if ((localObject instanceof PrivateElements)) {
                rewrite(localPrivateBinder, (PrivateElements)localObject, localHashSet);
              } else {
                ((Element)localObject).applyTo(localPrivateBinder);
              }
            }
          }
        }
        
        public Void visit(PrivateElements paramAnonymousPrivateElements)
        {
          rewrite(this.binder, paramAnonymousPrivateElements, localObject6);
          return null;
        }
        
        public Void visit(ScopeBinding paramAnonymousScopeBinding)
        {
          localArrayList.add(paramAnonymousScopeBinding);
          return null;
        }
      }.writeAll((Iterable)localObject3);
      new Modules.ModuleWriter((Binder)localObject2)
      {
        public Void visit(ScopeBinding paramAnonymousScopeBinding)
        {
          ScopeBinding localScopeBinding = (ScopeBinding)localHashMap1.remove(paramAnonymousScopeBinding.getAnnotationType());
          if (localScopeBinding == null)
          {
            super.visit(paramAnonymousScopeBinding);
          }
          else
          {
            List localList = (List)localHashMap2.get(paramAnonymousScopeBinding.getScope());
            if (localList != null)
            {
              StringBuilder localStringBuilder = new StringBuilder("The scope for @%s is bound directly and cannot be overridden.");
              String str1 = String.valueOf(String.valueOf(Errors.convert(paramAnonymousScopeBinding.getSource())));
              localStringBuilder.append(27 + str1.length() + "%n     original binding at " + str1);
              Iterator localIterator = localList.iterator();
              while (localIterator.hasNext())
              {
                Object localObject = localIterator.next();
                String str2 = String.valueOf(String.valueOf(Errors.convert(localObject)));
                localStringBuilder.append(25 + str2.length() + "%n     bound directly at " + str2);
              }
              this.binder.withSource(localScopeBinding.getSource()).addError(localStringBuilder.toString(), new Object[] { paramAnonymousScopeBinding.getAnnotationType().getSimpleName() });
            }
          }
          return null;
        }
      }.writeAll(localArrayList);
    }
    
    private Scope getScopeInstanceOrNull(Binding paramBinding)
    {
      (Scope)paramBinding.acceptScopingVisitor(new DefaultBindingScopingVisitor()
      {
        public Scope visitScope(Scope paramAnonymousScope)
        {
          return paramAnonymousScope;
        }
      });
    }
  }
  
  private static final class RealOverriddenModuleBuilder
    implements Modules.OverriddenModuleBuilder
  {
    private final ImmutableSet baseModules;
    
    private RealOverriddenModuleBuilder(Iterable paramIterable)
    {
      this.baseModules = ImmutableSet.copyOf(paramIterable);
    }
    
    public Module with(Module... paramVarArgs)
    {
      return with(Arrays.asList(paramVarArgs));
    }
    
    public Module with(Iterable paramIterable)
    {
      return new Modules.OverrideModule(paramIterable, this.baseModules);
    }
  }
  
  public static abstract interface OverriddenModuleBuilder
  {
    public abstract Module with(Module... paramVarArgs);
    
    public abstract Module with(Iterable paramIterable);
  }
  
  private static class CombinedModule
    implements Module
  {
    final Set modulesSet;
    
    CombinedModule(Iterable paramIterable)
    {
      this.modulesSet = ImmutableSet.copyOf(paramIterable);
    }
    
    public void configure(Binder paramBinder)
    {
      paramBinder = paramBinder.skipSources(new Class[] { getClass() });
      Iterator localIterator = this.modulesSet.iterator();
      while (localIterator.hasNext())
      {
        Module localModule = (Module)localIterator.next();
        paramBinder.install(localModule);
      }
    }
  }
  
  private static class EmptyModule
    implements Module
  {
    public void configure(Binder paramBinder) {}
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\util\Modules.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */