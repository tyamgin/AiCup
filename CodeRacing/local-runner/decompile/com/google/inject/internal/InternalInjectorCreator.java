package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.Stopwatch;
import com.google.inject.spi.Dependency;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InternalInjectorCreator
{
  private final Stopwatch stopwatch = new Stopwatch();
  private final Errors errors = new Errors();
  private final Initializer initializer = new Initializer();
  private final ProcessedBindingData bindingData = new ProcessedBindingData();
  private final InjectionRequestProcessor injectionRequestProcessor = new InjectionRequestProcessor(this.errors, this.initializer);
  private final InjectorShell.Builder shellBuilder = new InjectorShell.Builder();
  private List shells;
  
  public InternalInjectorCreator stage(Stage paramStage)
  {
    this.shellBuilder.stage(paramStage);
    return this;
  }
  
  public InternalInjectorCreator parentInjector(InjectorImpl paramInjectorImpl)
  {
    this.shellBuilder.parent(paramInjectorImpl);
    return this;
  }
  
  public InternalInjectorCreator addModules(Iterable paramIterable)
  {
    this.shellBuilder.addModules(paramIterable);
    return this;
  }
  
  public Injector build()
  {
    if (this.shellBuilder == null) {
      throw new AssertionError("Already built, builders are not reusable.");
    }
    synchronized (this.shellBuilder.lock())
    {
      this.shells = this.shellBuilder.build(this.initializer, this.bindingData, this.stopwatch, this.errors);
      this.stopwatch.resetAndLog("Injector construction");
      initializeStatically();
    }
    injectDynamically();
    if (this.shellBuilder.getStage() == Stage.TOOL) {
      return new ToolStageInjector(primaryInjector());
    }
    return primaryInjector();
  }
  
  private void initializeStatically()
  {
    this.bindingData.initializeBindings();
    this.stopwatch.resetAndLog("Binding initialization");
    Iterator localIterator = this.shells.iterator();
    InjectorShell localInjectorShell;
    while (localIterator.hasNext())
    {
      localInjectorShell = (InjectorShell)localIterator.next();
      localInjectorShell.getInjector().index();
    }
    this.stopwatch.resetAndLog("Binding indexing");
    this.injectionRequestProcessor.process(this.shells);
    this.stopwatch.resetAndLog("Collecting injection requests");
    this.bindingData.runCreationListeners(this.errors);
    this.stopwatch.resetAndLog("Binding validation");
    this.injectionRequestProcessor.validate();
    this.stopwatch.resetAndLog("Static validation");
    this.initializer.validateOustandingInjections(this.errors);
    this.stopwatch.resetAndLog("Instance member validation");
    new LookupProcessor(this.errors).process(this.shells);
    localIterator = this.shells.iterator();
    while (localIterator.hasNext())
    {
      localInjectorShell = (InjectorShell)localIterator.next();
      ((DeferredLookups)localInjectorShell.getInjector().lookups).initialize(this.errors);
    }
    this.stopwatch.resetAndLog("Provider verification");
    localIterator = this.shells.iterator();
    while (localIterator.hasNext())
    {
      localInjectorShell = (InjectorShell)localIterator.next();
      if (!localInjectorShell.getElements().isEmpty())
      {
        String str = String.valueOf(String.valueOf(localInjectorShell.getElements()));
        throw new AssertionError(18 + str.length() + "Failed to execute " + str);
      }
    }
    this.errors.throwCreationExceptionIfErrorsExist();
  }
  
  private Injector primaryInjector()
  {
    return ((InjectorShell)this.shells.get(0)).getInjector();
  }
  
  private void injectDynamically()
  {
    this.injectionRequestProcessor.injectMembers();
    this.stopwatch.resetAndLog("Static member injection");
    this.initializer.injectAll(this.errors);
    this.stopwatch.resetAndLog("Instance injection");
    this.errors.throwCreationExceptionIfErrorsExist();
    if (this.shellBuilder.getStage() != Stage.TOOL)
    {
      Iterator localIterator = this.shells.iterator();
      while (localIterator.hasNext())
      {
        InjectorShell localInjectorShell = (InjectorShell)localIterator.next();
        loadEagerSingletons(localInjectorShell.getInjector(), this.shellBuilder.getStage(), this.errors);
      }
      this.stopwatch.resetAndLog("Preloading singletons");
    }
    this.errors.throwCreationExceptionIfErrorsExist();
  }
  
  void loadEagerSingletons(InjectorImpl paramInjectorImpl, Stage paramStage, final Errors paramErrors)
  {
    ImmutableList localImmutableList = ImmutableList.copyOf(Iterables.concat(paramInjectorImpl.state.getExplicitBindingsThisLevel().values(), paramInjectorImpl.jitBindings.values()));
    Iterator localIterator = localImmutableList.iterator();
    while (localIterator.hasNext())
    {
      final BindingImpl localBindingImpl = (BindingImpl)localIterator.next();
      if (isEagerSingleton(paramInjectorImpl, localBindingImpl, paramStage)) {
        try
        {
          paramInjectorImpl.callInContext(new ContextualCallable()
          {
            Dependency dependency = Dependency.get(localBindingImpl.getKey());
            
            public Void call(InternalContext paramAnonymousInternalContext)
            {
              Dependency localDependency = paramAnonymousInternalContext.pushDependency(this.dependency, localBindingImpl.getSource());
              Errors localErrors = paramErrors.withSource(this.dependency);
              try
              {
                localBindingImpl.getInternalFactory().get(localErrors, paramAnonymousInternalContext, this.dependency, false);
              }
              catch (ErrorsException localErrorsException)
              {
                localErrors.merge(localErrorsException.getErrors());
              }
              finally
              {
                paramAnonymousInternalContext.popStateAndSetDependency(localDependency);
              }
              return null;
            }
          });
        }
        catch (ErrorsException localErrorsException)
        {
          throw new AssertionError();
        }
      }
    }
  }
  
  private boolean isEagerSingleton(InjectorImpl paramInjectorImpl, BindingImpl paramBindingImpl, Stage paramStage)
  {
    if (paramBindingImpl.getScoping().isEagerSingleton(paramStage)) {
      return true;
    }
    if ((paramBindingImpl instanceof LinkedBindingImpl))
    {
      Key localKey = ((LinkedBindingImpl)paramBindingImpl).getLinkedKey();
      return isEagerSingleton(paramInjectorImpl, paramInjectorImpl.getBinding(localKey), paramStage);
    }
    return false;
  }
  
  static class ToolStageInjector
    implements Injector
  {
    private final Injector delegateInjector;
    
    ToolStageInjector(Injector paramInjector)
    {
      this.delegateInjector = paramInjector;
    }
    
    public void injectMembers(Object paramObject)
    {
      throw new UnsupportedOperationException("Injector.injectMembers(Object) is not supported in Stage.TOOL");
    }
    
    public Map getBindings()
    {
      return this.delegateInjector.getBindings();
    }
    
    public Map getAllBindings()
    {
      return this.delegateInjector.getAllBindings();
    }
    
    public Binding getBinding(Key paramKey)
    {
      return this.delegateInjector.getBinding(paramKey);
    }
    
    public Binding getBinding(Class paramClass)
    {
      return this.delegateInjector.getBinding(paramClass);
    }
    
    public Binding getExistingBinding(Key paramKey)
    {
      return this.delegateInjector.getExistingBinding(paramKey);
    }
    
    public List findBindingsByType(TypeLiteral paramTypeLiteral)
    {
      return this.delegateInjector.findBindingsByType(paramTypeLiteral);
    }
    
    public Injector getParent()
    {
      return this.delegateInjector.getParent();
    }
    
    public Injector createChildInjector(Iterable paramIterable)
    {
      return this.delegateInjector.createChildInjector(paramIterable);
    }
    
    public Injector createChildInjector(Module... paramVarArgs)
    {
      return this.delegateInjector.createChildInjector(paramVarArgs);
    }
    
    public Map getScopeBindings()
    {
      return this.delegateInjector.getScopeBindings();
    }
    
    public Set getTypeConverterBindings()
    {
      return this.delegateInjector.getTypeConverterBindings();
    }
    
    public Provider getProvider(Key paramKey)
    {
      throw new UnsupportedOperationException("Injector.getProvider(Key<T>) is not supported in Stage.TOOL");
    }
    
    public Provider getProvider(Class paramClass)
    {
      throw new UnsupportedOperationException("Injector.getProvider(Class<T>) is not supported in Stage.TOOL");
    }
    
    public MembersInjector getMembersInjector(TypeLiteral paramTypeLiteral)
    {
      throw new UnsupportedOperationException("Injector.getMembersInjector(TypeLiteral<T>) is not supported in Stage.TOOL");
    }
    
    public MembersInjector getMembersInjector(Class paramClass)
    {
      throw new UnsupportedOperationException("Injector.getMembersInjector(Class<T>) is not supported in Stage.TOOL");
    }
    
    public Object getInstance(Key paramKey)
    {
      throw new UnsupportedOperationException("Injector.getInstance(Key<T>) is not supported in Stage.TOOL");
    }
    
    public Object getInstance(Class paramClass)
    {
      throw new UnsupportedOperationException("Injector.getInstance(Class<T>) is not supported in Stage.TOOL");
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InternalInjectorCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */