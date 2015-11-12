package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.internal.util.Stopwatch;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;
import com.google.inject.spi.PrivateElements;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

final class InjectorShell
{
  private final List elements;
  private final InjectorImpl injector;
  
  private InjectorShell(Builder paramBuilder, List paramList, InjectorImpl paramInjectorImpl)
  {
    this.elements = paramList;
    this.injector = paramInjectorImpl;
  }
  
  InjectorImpl getInjector()
  {
    return this.injector;
  }
  
  List getElements()
  {
    return this.elements;
  }
  
  private static void bindInjector(InjectorImpl paramInjectorImpl)
  {
    Key localKey = Key.get(Injector.class);
    InjectorFactory localInjectorFactory = new InjectorFactory(paramInjectorImpl, null);
    paramInjectorImpl.state.putBinding(localKey, new ProviderInstanceBindingImpl(paramInjectorImpl, localKey, SourceProvider.UNKNOWN_SOURCE, localInjectorFactory, Scoping.UNSCOPED, localInjectorFactory, ImmutableSet.of()));
  }
  
  private static void bindLogger(InjectorImpl paramInjectorImpl)
  {
    Key localKey = Key.get(Logger.class);
    LoggerFactory localLoggerFactory = new LoggerFactory(null);
    paramInjectorImpl.state.putBinding(localKey, new ProviderInstanceBindingImpl(paramInjectorImpl, localKey, SourceProvider.UNKNOWN_SOURCE, localLoggerFactory, Scoping.UNSCOPED, localLoggerFactory, ImmutableSet.of()));
  }
  
  private static void bindStage(InjectorImpl paramInjectorImpl, Stage paramStage)
  {
    Key localKey = Key.get(Stage.class);
    InstanceBindingImpl localInstanceBindingImpl = new InstanceBindingImpl(paramInjectorImpl, localKey, SourceProvider.UNKNOWN_SOURCE, new ConstantFactory(Initializables.of(paramStage)), ImmutableSet.of(), paramStage);
    paramInjectorImpl.state.putBinding(localKey, localInstanceBindingImpl);
  }
  
  private static class InheritedScannersModule
    implements Module
  {
    private final State state;
    
    InheritedScannersModule(State paramState)
    {
      this.state = paramState;
    }
    
    public void configure(Binder paramBinder)
    {
      Iterator localIterator = this.state.getScannerBindings().iterator();
      while (localIterator.hasNext())
      {
        ModuleAnnotatedMethodScannerBinding localModuleAnnotatedMethodScannerBinding = (ModuleAnnotatedMethodScannerBinding)localIterator.next();
        localModuleAnnotatedMethodScannerBinding.applyTo(paramBinder);
      }
    }
  }
  
  private static class RootModule
    implements Module
  {
    public void configure(Binder paramBinder)
    {
      paramBinder = paramBinder.withSource(SourceProvider.UNKNOWN_SOURCE);
      paramBinder.bindScope(com.google.inject.Singleton.class, Scopes.SINGLETON);
      paramBinder.bindScope(javax.inject.Singleton.class, Scopes.SINGLETON);
    }
  }
  
  private static class LoggerFactory
    implements Provider, InternalFactory
  {
    public Logger get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    {
      InjectionPoint localInjectionPoint = paramDependency.getInjectionPoint();
      return localInjectionPoint == null ? Logger.getAnonymousLogger() : Logger.getLogger(localInjectionPoint.getMember().getDeclaringClass().getName());
    }
    
    public Logger get()
    {
      return Logger.getAnonymousLogger();
    }
    
    public String toString()
    {
      return "Provider<Logger>";
    }
  }
  
  private static class InjectorFactory
    implements Provider, InternalFactory
  {
    private final Injector injector;
    
    private InjectorFactory(Injector paramInjector)
    {
      this.injector = paramInjector;
    }
    
    public Injector get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
      throws ErrorsException
    {
      return this.injector;
    }
    
    public Injector get()
    {
      return this.injector;
    }
    
    public String toString()
    {
      return "Provider<Injector>";
    }
  }
  
  static class Builder
  {
    private final List elements = Lists.newArrayList();
    private final List modules = Lists.newArrayList();
    private State state;
    private InjectorImpl parent;
    private InjectorImpl.InjectorOptions options;
    private Stage stage;
    private PrivateElementsImpl privateElements;
    
    Builder stage(Stage paramStage)
    {
      this.stage = paramStage;
      return this;
    }
    
    Builder parent(InjectorImpl paramInjectorImpl)
    {
      this.parent = paramInjectorImpl;
      this.state = new InheritingState(paramInjectorImpl.state);
      this.options = paramInjectorImpl.options;
      this.stage = this.options.stage;
      return this;
    }
    
    Builder privateElements(PrivateElements paramPrivateElements)
    {
      this.privateElements = ((PrivateElementsImpl)paramPrivateElements);
      this.elements.addAll(paramPrivateElements.getElements());
      return this;
    }
    
    void addModules(Iterable paramIterable)
    {
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        Module localModule = (Module)localIterator.next();
        this.modules.add(localModule);
      }
    }
    
    Stage getStage()
    {
      return this.options.stage;
    }
    
    Object lock()
    {
      return getState().lock();
    }
    
    List build(Initializer paramInitializer, ProcessedBindingData paramProcessedBindingData, Stopwatch paramStopwatch, Errors paramErrors)
    {
      Preconditions.checkState(this.stage != null, "Stage not initialized");
      Preconditions.checkState((this.privateElements == null) || (this.parent != null), "PrivateElements with no parent");
      Preconditions.checkState(this.state != null, "no state. Did you remember to lock() ?");
      if (this.parent == null) {
        this.modules.add(0, new InjectorShell.RootModule(null));
      } else {
        this.modules.add(0, new InjectorShell.InheritedScannersModule(this.parent.state));
      }
      this.elements.addAll(Elements.getElements(this.stage, this.modules));
      InjectorOptionsProcessor localInjectorOptionsProcessor = new InjectorOptionsProcessor(paramErrors);
      localInjectorOptionsProcessor.process(null, this.elements);
      this.options = localInjectorOptionsProcessor.getOptions(this.stage, this.options);
      InjectorImpl localInjectorImpl = new InjectorImpl(this.parent, this.state, this.options);
      if (this.privateElements != null) {
        this.privateElements.initInjector(localInjectorImpl);
      }
      if (this.parent == null) {
        TypeConverterBindingProcessor.prepareBuiltInConverters(localInjectorImpl);
      }
      paramStopwatch.resetAndLog("Module execution");
      new MessageProcessor(paramErrors).process(localInjectorImpl, this.elements);
      new InterceptorBindingProcessor(paramErrors).process(localInjectorImpl, this.elements);
      paramStopwatch.resetAndLog("Interceptors creation");
      new ListenerBindingProcessor(paramErrors).process(localInjectorImpl, this.elements);
      List localList1 = localInjectorImpl.state.getTypeListenerBindings();
      localInjectorImpl.membersInjectorStore = new MembersInjectorStore(localInjectorImpl, localList1);
      List localList2 = localInjectorImpl.state.getProvisionListenerBindings();
      localInjectorImpl.provisionListenerStore = new ProvisionListenerCallbackStore(localList2);
      paramStopwatch.resetAndLog("TypeListeners & ProvisionListener creation");
      new ScopeBindingProcessor(paramErrors).process(localInjectorImpl, this.elements);
      paramStopwatch.resetAndLog("Scopes creation");
      new TypeConverterBindingProcessor(paramErrors).process(localInjectorImpl, this.elements);
      paramStopwatch.resetAndLog("Converters creation");
      InjectorShell.bindStage(localInjectorImpl, this.stage);
      InjectorShell.bindInjector(localInjectorImpl);
      InjectorShell.bindLogger(localInjectorImpl);
      new BindingProcessor(paramErrors, paramInitializer, paramProcessedBindingData).process(localInjectorImpl, this.elements);
      new UntargettedBindingProcessor(paramErrors, paramProcessedBindingData).process(localInjectorImpl, this.elements);
      paramStopwatch.resetAndLog("Binding creation");
      new ModuleAnnotatedMethodScannerProcessor(paramErrors).process(localInjectorImpl, this.elements);
      paramStopwatch.resetAndLog("Module annotated method scanners creation");
      ArrayList localArrayList = Lists.newArrayList();
      localArrayList.add(new InjectorShell(this, this.elements, localInjectorImpl, null));
      PrivateElementProcessor localPrivateElementProcessor = new PrivateElementProcessor(paramErrors);
      localPrivateElementProcessor.process(localInjectorImpl, this.elements);
      Iterator localIterator = localPrivateElementProcessor.getInjectorShellBuilders().iterator();
      while (localIterator.hasNext())
      {
        Builder localBuilder = (Builder)localIterator.next();
        localArrayList.addAll(localBuilder.build(paramInitializer, paramProcessedBindingData, paramStopwatch, paramErrors));
      }
      paramStopwatch.resetAndLog("Private environment creation");
      return localArrayList;
    }
    
    private State getState()
    {
      if (this.state == null) {
        this.state = new InheritingState(State.NONE);
      }
      return this.state;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InjectorShell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */