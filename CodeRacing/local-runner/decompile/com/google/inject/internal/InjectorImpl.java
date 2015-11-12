package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.util.Providers;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

final class InjectorImpl
  implements Injector, Lookups
{
  public static final TypeLiteral STRING_TYPE = TypeLiteral.get(String.class);
  final State state;
  final InjectorImpl parent;
  final BindingsMultimap bindingsMultimap = new BindingsMultimap(null);
  final InjectorOptions options;
  final Map jitBindings = Maps.newHashMap();
  final Set failedJitBindings = Sets.newHashSet();
  Lookups lookups = new DeferredLookups(this);
  final ConstructorInjectorStore constructors = new ConstructorInjectorStore(this);
  MembersInjectorStore membersInjectorStore;
  ProvisionListenerCallbackStore provisionListenerStore;
  private final ThreadLocal localContext;
  private static final ConcurrentMap globalInternalContext = Maps.newConcurrentMap();
  
  InjectorImpl(InjectorImpl paramInjectorImpl, State paramState, InjectorOptions paramInjectorOptions)
  {
    this.parent = paramInjectorImpl;
    this.state = paramState;
    this.options = paramInjectorOptions;
    if (paramInjectorImpl != null) {
      this.localContext = paramInjectorImpl.localContext;
    } else {
      this.localContext = new ThreadLocal();
    }
  }
  
  void index()
  {
    Iterator localIterator = this.state.getExplicitBindingsThisLevel().values().iterator();
    while (localIterator.hasNext())
    {
      Binding localBinding = (Binding)localIterator.next();
      index(localBinding);
    }
  }
  
  void index(Binding paramBinding)
  {
    this.bindingsMultimap.put(paramBinding.getKey().getTypeLiteral(), paramBinding);
  }
  
  public List findBindingsByType(TypeLiteral paramTypeLiteral)
  {
    return this.bindingsMultimap.getAll(paramTypeLiteral);
  }
  
  public BindingImpl getBinding(Key paramKey)
  {
    Errors localErrors = new Errors(paramKey);
    try
    {
      BindingImpl localBindingImpl = getBindingOrThrow(paramKey, localErrors, JitLimitation.EXISTING_JIT);
      localErrors.throwConfigurationExceptionIfErrorsExist();
      return localBindingImpl;
    }
    catch (ErrorsException localErrorsException)
    {
      throw new ConfigurationException(localErrors.merge(localErrorsException.getErrors()).getMessages());
    }
  }
  
  public BindingImpl getExistingBinding(Key paramKey)
  {
    BindingImpl localBindingImpl1 = this.state.getExplicitBinding(paramKey);
    if (localBindingImpl1 != null) {
      return localBindingImpl1;
    }
    synchronized (this.state.lock())
    {
      for (InjectorImpl localInjectorImpl = this; localInjectorImpl != null; localInjectorImpl = localInjectorImpl.parent)
      {
        BindingImpl localBindingImpl2 = (BindingImpl)localInjectorImpl.jitBindings.get(paramKey);
        if (localBindingImpl2 != null) {
          return localBindingImpl2;
        }
      }
    }
    if (isProvider(paramKey)) {
      try
      {
        ??? = getProvidedKey(paramKey, new Errors());
        if (getExistingBinding((Key)???) != null) {
          return getBinding(paramKey);
        }
      }
      catch (ErrorsException localErrorsException)
      {
        throw new ConfigurationException(localErrorsException.getErrors().getMessages());
      }
    }
    return null;
  }
  
  BindingImpl getBindingOrThrow(Key paramKey, Errors paramErrors, JitLimitation paramJitLimitation)
    throws ErrorsException
  {
    BindingImpl localBindingImpl = this.state.getExplicitBinding(paramKey);
    if (localBindingImpl != null) {
      return localBindingImpl;
    }
    return getJustInTimeBinding(paramKey, paramErrors, paramJitLimitation);
  }
  
  public Binding getBinding(Class paramClass)
  {
    return getBinding(Key.get(paramClass));
  }
  
  public Injector getParent()
  {
    return this.parent;
  }
  
  public Injector createChildInjector(Iterable paramIterable)
  {
    return new InternalInjectorCreator().parentInjector(this).addModules(paramIterable).build();
  }
  
  public Injector createChildInjector(Module... paramVarArgs)
  {
    return createChildInjector(ImmutableList.copyOf(paramVarArgs));
  }
  
  private BindingImpl getJustInTimeBinding(Key paramKey, Errors paramErrors, JitLimitation paramJitLimitation)
    throws ErrorsException
  {
    int i = (isProvider(paramKey)) || (isTypeLiteral(paramKey)) || (isMembersInjector(paramKey)) ? 1 : 0;
    synchronized (this.state.lock())
    {
      for (InjectorImpl localInjectorImpl = this; localInjectorImpl != null; localInjectorImpl = localInjectorImpl.parent)
      {
        BindingImpl localBindingImpl = (BindingImpl)localInjectorImpl.jitBindings.get(paramKey);
        if (localBindingImpl != null)
        {
          if ((this.options.jitDisabled) && (paramJitLimitation == JitLimitation.NO_JIT) && (i == 0) && (!(localBindingImpl instanceof ConvertedConstantBindingImpl))) {
            throw paramErrors.jitDisabled(paramKey).toException();
          }
          return localBindingImpl;
        }
      }
      if ((this.failedJitBindings.contains(paramKey)) && (paramErrors.hasErrors())) {
        throw paramErrors.toException();
      }
      return createJustInTimeBindingRecursive(paramKey, paramErrors, this.options.jitDisabled, paramJitLimitation);
    }
  }
  
  private static boolean isProvider(Key paramKey)
  {
    return paramKey.getTypeLiteral().getRawType().equals(Provider.class);
  }
  
  private static boolean isTypeLiteral(Key paramKey)
  {
    return paramKey.getTypeLiteral().getRawType().equals(TypeLiteral.class);
  }
  
  private static Key getProvidedKey(Key paramKey, Errors paramErrors)
    throws ErrorsException
  {
    Type localType1 = paramKey.getTypeLiteral().getType();
    if (!(localType1 instanceof ParameterizedType)) {
      throw paramErrors.cannotInjectRawProvider().toException();
    }
    Type localType2 = ((ParameterizedType)localType1).getActualTypeArguments()[0];
    Key localKey = paramKey.ofType(localType2);
    return localKey;
  }
  
  private static boolean isMembersInjector(Key paramKey)
  {
    return (paramKey.getTypeLiteral().getRawType().equals(MembersInjector.class)) && (paramKey.getAnnotationType() == null);
  }
  
  private BindingImpl createMembersInjectorBinding(Key paramKey, Errors paramErrors)
    throws ErrorsException
  {
    Type localType = paramKey.getTypeLiteral().getType();
    if (!(localType instanceof ParameterizedType)) {
      throw paramErrors.cannotInjectRawMembersInjector().toException();
    }
    TypeLiteral localTypeLiteral = TypeLiteral.get(((ParameterizedType)localType).getActualTypeArguments()[0]);
    MembersInjectorImpl localMembersInjectorImpl = this.membersInjectorStore.get(localTypeLiteral, paramErrors);
    ConstantFactory localConstantFactory = new ConstantFactory(Initializables.of(localMembersInjectorImpl));
    return new InstanceBindingImpl(this, paramKey, SourceProvider.UNKNOWN_SOURCE, localConstantFactory, ImmutableSet.of(), localMembersInjectorImpl);
  }
  
  private BindingImpl createProviderBinding(Key paramKey, Errors paramErrors)
    throws ErrorsException
  {
    Key localKey = getProvidedKey(paramKey, paramErrors);
    BindingImpl localBindingImpl = getBindingOrThrow(localKey, paramErrors, JitLimitation.NO_JIT);
    return new ProviderBindingImpl(this, paramKey, localBindingImpl);
  }
  
  private BindingImpl convertConstantStringBinding(Key paramKey, Errors paramErrors)
    throws ErrorsException
  {
    Key localKey = paramKey.ofType(STRING_TYPE);
    BindingImpl localBindingImpl = this.state.getExplicitBinding(localKey);
    if ((localBindingImpl == null) || (!localBindingImpl.isConstant())) {
      return null;
    }
    String str = (String)localBindingImpl.getProvider().get();
    Object localObject1 = localBindingImpl.getSource();
    TypeLiteral localTypeLiteral = paramKey.getTypeLiteral();
    TypeConverterBinding localTypeConverterBinding = this.state.getConverter(str, localTypeLiteral, paramErrors, localObject1);
    if (localTypeConverterBinding == null) {
      return null;
    }
    try
    {
      Object localObject2 = localTypeConverterBinding.getTypeConverter().convert(str, localTypeLiteral);
      if (localObject2 == null) {
        throw paramErrors.converterReturnedNull(str, localObject1, localTypeLiteral, localTypeConverterBinding).toException();
      }
      if (!localTypeLiteral.getRawType().isInstance(localObject2)) {
        throw paramErrors.conversionTypeError(str, localObject1, localTypeLiteral, localTypeConverterBinding, localObject2).toException();
      }
      return new ConvertedConstantBindingImpl(this, paramKey, localObject2, localBindingImpl, localTypeConverterBinding);
    }
    catch (ErrorsException localErrorsException)
    {
      throw localErrorsException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw paramErrors.conversionError(str, localObject1, localTypeLiteral, localTypeConverterBinding, localRuntimeException).toException();
    }
  }
  
  void initializeBinding(BindingImpl paramBindingImpl, Errors paramErrors)
    throws ErrorsException
  {
    if ((paramBindingImpl instanceof DelayedInitialize)) {
      ((DelayedInitialize)paramBindingImpl).initialize(this, paramErrors);
    }
  }
  
  void initializeJitBinding(BindingImpl paramBindingImpl, Errors paramErrors)
    throws ErrorsException
  {
    if ((paramBindingImpl instanceof DelayedInitialize))
    {
      Key localKey = paramBindingImpl.getKey();
      this.jitBindings.put(localKey, paramBindingImpl);
      int i = 0;
      DelayedInitialize localDelayedInitialize = (DelayedInitialize)paramBindingImpl;
      try
      {
        localDelayedInitialize.initialize(this, paramErrors);
        i = 1;
      }
      finally
      {
        if (i == 0)
        {
          removeFailedJitBinding(paramBindingImpl, null);
          cleanup(paramBindingImpl, new HashSet());
        }
      }
    }
  }
  
  private boolean cleanup(BindingImpl paramBindingImpl, Set paramSet)
  {
    boolean bool1 = false;
    Set localSet = getInternalDependencies(paramBindingImpl);
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Dependency localDependency = (Dependency)localIterator.next();
      Key localKey = localDependency.getKey();
      InjectionPoint localInjectionPoint = localDependency.getInjectionPoint();
      if (paramSet.add(localKey))
      {
        BindingImpl localBindingImpl = (BindingImpl)this.jitBindings.get(localKey);
        if (localBindingImpl != null)
        {
          boolean bool2 = cleanup(localBindingImpl, paramSet);
          if ((localBindingImpl instanceof ConstructorBindingImpl))
          {
            ConstructorBindingImpl localConstructorBindingImpl = (ConstructorBindingImpl)localBindingImpl;
            localInjectionPoint = localConstructorBindingImpl.getInternalConstructor();
            if (!localConstructorBindingImpl.isInitialized()) {
              bool2 = true;
            }
          }
          if (bool2)
          {
            removeFailedJitBinding(localBindingImpl, localInjectionPoint);
            bool1 = true;
          }
        }
        else if (this.state.getExplicitBinding(localKey) == null)
        {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private void removeFailedJitBinding(Binding paramBinding, InjectionPoint paramInjectionPoint)
  {
    this.failedJitBindings.add(paramBinding.getKey());
    this.jitBindings.remove(paramBinding.getKey());
    this.membersInjectorStore.remove(paramBinding.getKey().getTypeLiteral());
    this.provisionListenerStore.remove(paramBinding);
    if (paramInjectionPoint != null) {
      this.constructors.remove(paramInjectionPoint);
    }
  }
  
  private Set getInternalDependencies(BindingImpl paramBindingImpl)
  {
    if ((paramBindingImpl instanceof ConstructorBindingImpl)) {
      return ((ConstructorBindingImpl)paramBindingImpl).getInternalDependencies();
    }
    if ((paramBindingImpl instanceof HasDependencies)) {
      return ((HasDependencies)paramBindingImpl).getDependencies();
    }
    return ImmutableSet.of();
  }
  
  BindingImpl createUninitializedBinding(Key paramKey, Scoping paramScoping, Object paramObject, Errors paramErrors, boolean paramBoolean)
    throws ErrorsException
  {
    Class localClass = paramKey.getTypeLiteral().getRawType();
    ImplementedBy localImplementedBy = (ImplementedBy)localClass.getAnnotation(ImplementedBy.class);
    if ((localClass.isArray()) || ((localClass.isEnum()) && (localImplementedBy != null))) {
      throw paramErrors.missingImplementation(paramKey).toException();
    }
    if (localClass == TypeLiteral.class)
    {
      localObject = createTypeLiteralBinding(paramKey, paramErrors);
      return (BindingImpl)localObject;
    }
    if (localImplementedBy != null)
    {
      Annotations.checkForMisplacedScopeAnnotations(localClass, paramObject, paramErrors);
      return createImplementedByBinding(paramKey, paramScoping, localImplementedBy, paramErrors);
    }
    Object localObject = (ProvidedBy)localClass.getAnnotation(ProvidedBy.class);
    if (localObject != null)
    {
      Annotations.checkForMisplacedScopeAnnotations(localClass, paramObject, paramErrors);
      return createProvidedByBinding(paramKey, paramScoping, (ProvidedBy)localObject, paramErrors);
    }
    return ConstructorBindingImpl.create(this, paramKey, null, paramObject, paramScoping, paramErrors, (paramBoolean) && (this.options.jitDisabled), this.options.atInjectRequired);
  }
  
  private BindingImpl createTypeLiteralBinding(Key paramKey, Errors paramErrors)
    throws ErrorsException
  {
    Type localType1 = paramKey.getTypeLiteral().getType();
    if (!(localType1 instanceof ParameterizedType)) {
      throw paramErrors.cannotInjectRawTypeLiteral().toException();
    }
    ParameterizedType localParameterizedType = (ParameterizedType)localType1;
    Type localType2 = localParameterizedType.getActualTypeArguments()[0];
    if ((!(localType2 instanceof Class)) && (!(localType2 instanceof GenericArrayType)) && (!(localType2 instanceof ParameterizedType))) {
      throw paramErrors.cannotInjectTypeLiteralOf(localType2).toException();
    }
    TypeLiteral localTypeLiteral = TypeLiteral.get(localType2);
    ConstantFactory localConstantFactory = new ConstantFactory(Initializables.of(localTypeLiteral));
    return new InstanceBindingImpl(this, paramKey, SourceProvider.UNKNOWN_SOURCE, localConstantFactory, ImmutableSet.of(), localTypeLiteral);
  }
  
  BindingImpl createProvidedByBinding(Key paramKey, Scoping paramScoping, ProvidedBy paramProvidedBy, Errors paramErrors)
    throws ErrorsException
  {
    Class localClass1 = paramKey.getTypeLiteral().getRawType();
    Class localClass2 = paramProvidedBy.value();
    if (localClass2 == localClass1) {
      throw paramErrors.recursiveProviderType().toException();
    }
    Key localKey = Key.get(localClass2);
    ProvidedByInternalFactory localProvidedByInternalFactory = new ProvidedByInternalFactory(localClass1, localClass2, localKey);
    Class localClass3 = localClass1;
    LinkedProviderBindingImpl localLinkedProviderBindingImpl = LinkedProviderBindingImpl.createWithInitializer(this, paramKey, localClass3, Scoping.scope(paramKey, this, localProvidedByInternalFactory, localClass3, paramScoping), paramScoping, localKey, localProvidedByInternalFactory);
    localProvidedByInternalFactory.setProvisionListenerCallback(this.provisionListenerStore.get(localLinkedProviderBindingImpl));
    return localLinkedProviderBindingImpl;
  }
  
  private BindingImpl createImplementedByBinding(Key paramKey, Scoping paramScoping, ImplementedBy paramImplementedBy, Errors paramErrors)
    throws ErrorsException
  {
    Class localClass1 = paramKey.getTypeLiteral().getRawType();
    Class localClass2 = paramImplementedBy.value();
    if (localClass2 == localClass1) {
      throw paramErrors.recursiveImplementationType().toException();
    }
    if (!localClass1.isAssignableFrom(localClass2)) {
      throw paramErrors.notASubtype(localClass2, localClass1).toException();
    }
    Class localClass3 = localClass2;
    final Key localKey = Key.get(localClass3);
    final BindingImpl localBindingImpl = getBindingOrThrow(localKey, paramErrors, JitLimitation.NEW_OR_EXISTING_JIT);
    InternalFactory local1 = new InternalFactory()
    {
      public Object get(Errors paramAnonymousErrors, InternalContext paramAnonymousInternalContext, Dependency paramAnonymousDependency, boolean paramAnonymousBoolean)
        throws ErrorsException
      {
        paramAnonymousInternalContext.pushState(localKey, localBindingImpl.getSource());
        try
        {
          Object localObject1 = localBindingImpl.getInternalFactory().get(paramAnonymousErrors.withSource(localKey), paramAnonymousInternalContext, paramAnonymousDependency, true);
          return localObject1;
        }
        finally
        {
          paramAnonymousInternalContext.popState();
        }
      }
    };
    Class localClass4 = localClass1;
    return new LinkedBindingImpl(this, paramKey, localClass4, Scoping.scope(paramKey, this, local1, localClass4, paramScoping), paramScoping, localKey);
  }
  
  private BindingImpl createJustInTimeBindingRecursive(Key paramKey, Errors paramErrors, boolean paramBoolean, JitLimitation paramJitLimitation)
    throws ErrorsException
  {
    if (this.parent != null)
    {
      if ((paramJitLimitation == JitLimitation.NEW_OR_EXISTING_JIT) && (paramBoolean) && (!this.parent.options.jitDisabled)) {
        throw paramErrors.jitDisabledInParent(paramKey).toException();
      }
      try
      {
        return this.parent.createJustInTimeBindingRecursive(paramKey, new Errors(), paramBoolean, this.parent.options.jitDisabled ? JitLimitation.NO_JIT : paramJitLimitation);
      }
      catch (ErrorsException localErrorsException) {}
    }
    Set localSet = this.state.getSourcesForBlacklistedKey(paramKey);
    if (this.state.isBlacklisted(paramKey)) {
      throw paramErrors.childBindingAlreadySet(paramKey, localSet).toException();
    }
    paramKey = MoreTypes.canonicalizeKey(paramKey);
    BindingImpl localBindingImpl = createJustInTimeBinding(paramKey, paramErrors, paramBoolean, paramJitLimitation);
    this.state.parent().blacklist(paramKey, this.state, localBindingImpl.getSource());
    this.jitBindings.put(paramKey, localBindingImpl);
    return localBindingImpl;
  }
  
  private BindingImpl createJustInTimeBinding(Key paramKey, Errors paramErrors, boolean paramBoolean, JitLimitation paramJitLimitation)
    throws ErrorsException
  {
    int i = paramErrors.size();
    Set localSet = this.state.getSourcesForBlacklistedKey(paramKey);
    if (this.state.isBlacklisted(paramKey)) {
      throw paramErrors.childBindingAlreadySet(paramKey, localSet).toException();
    }
    if (isProvider(paramKey))
    {
      localBindingImpl1 = createProviderBinding(paramKey, paramErrors);
      return localBindingImpl1;
    }
    if (isMembersInjector(paramKey))
    {
      localBindingImpl1 = createMembersInjectorBinding(paramKey, paramErrors);
      return localBindingImpl1;
    }
    BindingImpl localBindingImpl1 = convertConstantStringBinding(paramKey, paramErrors);
    if (localBindingImpl1 != null) {
      return localBindingImpl1;
    }
    if ((!isTypeLiteral(paramKey)) && (paramBoolean) && (paramJitLimitation != JitLimitation.NEW_OR_EXISTING_JIT)) {
      throw paramErrors.jitDisabled(paramKey).toException();
    }
    if (paramKey.getAnnotationType() != null)
    {
      if ((paramKey.hasAttributes()) && (!this.options.exactBindingAnnotationsRequired)) {
        try
        {
          Errors localErrors = new Errors();
          return getBindingOrThrow(paramKey.withoutAttributes(), localErrors, JitLimitation.NO_JIT);
        }
        catch (ErrorsException localErrorsException) {}
      }
      throw paramErrors.missingImplementation(paramKey).toException();
    }
    Class localClass = paramKey.getTypeLiteral().getRawType();
    BindingImpl localBindingImpl2 = createUninitializedBinding(paramKey, Scoping.UNSCOPED, localClass, paramErrors, true);
    paramErrors.throwIfNewErrors(i);
    initializeJitBinding(localBindingImpl2, paramErrors);
    return localBindingImpl2;
  }
  
  InternalFactory getInternalFactory(Key paramKey, Errors paramErrors, JitLimitation paramJitLimitation)
    throws ErrorsException
  {
    return getBindingOrThrow(paramKey, paramErrors, paramJitLimitation).getInternalFactory();
  }
  
  public Map getBindings()
  {
    return this.state.getExplicitBindingsThisLevel();
  }
  
  /* Error */
  public Map getAllBindings()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 90	com/google/inject/internal/InjectorImpl:state	Lcom/google/inject/internal/State;
    //   4: invokeinterface 250 1 0
    //   9: dup
    //   10: astore_1
    //   11: monitorenter
    //   12: new 8	com/google/common/collect/ImmutableMap$Builder
    //   15: dup
    //   16: invokespecial 104	com/google/common/collect/ImmutableMap$Builder:<init>	()V
    //   19: aload_0
    //   20: getfield 90	com/google/inject/internal/InjectorImpl:state	Lcom/google/inject/internal/State;
    //   23: invokeinterface 246 1 0
    //   28: invokevirtual 106	com/google/common/collect/ImmutableMap$Builder:putAll	(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap$Builder;
    //   31: aload_0
    //   32: getfield 83	com/google/inject/internal/InjectorImpl:jitBindings	Ljava/util/Map;
    //   35: invokevirtual 106	com/google/common/collect/ImmutableMap$Builder:putAll	(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap$Builder;
    //   38: invokevirtual 105	com/google/common/collect/ImmutableMap$Builder:build	()Lcom/google/common/collect/ImmutableMap;
    //   41: aload_1
    //   42: monitorexit
    //   43: areturn
    //   44: astore_2
    //   45: aload_1
    //   46: monitorexit
    //   47: aload_2
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	InjectorImpl
    //   10	36	1	Ljava/lang/Object;	Object
    //   44	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   12	43	44	finally
    //   44	47	44	finally
  }
  
  public Map getScopeBindings()
  {
    return ImmutableMap.copyOf(this.state.getScopes());
  }
  
  public Set getTypeConverterBindings()
  {
    return ImmutableSet.copyOf(this.state.getConvertersThisLevel());
  }
  
  SingleParameterInjector[] getParametersInjectors(List paramList, Errors paramErrors)
    throws ErrorsException
  {
    if (paramList.isEmpty()) {
      return null;
    }
    int i = paramErrors.size();
    SingleParameterInjector[] arrayOfSingleParameterInjector = new SingleParameterInjector[paramList.size()];
    int j = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Dependency localDependency = (Dependency)localIterator.next();
      try
      {
        arrayOfSingleParameterInjector[(j++)] = createParameterInjector(localDependency, paramErrors.withSource(localDependency));
      }
      catch (ErrorsException localErrorsException) {}
    }
    paramErrors.throwIfNewErrors(i);
    return arrayOfSingleParameterInjector;
  }
  
  SingleParameterInjector createParameterInjector(Dependency paramDependency, Errors paramErrors)
    throws ErrorsException
  {
    BindingImpl localBindingImpl = getBindingOrThrow(paramDependency.getKey(), paramErrors, JitLimitation.NO_JIT);
    return new SingleParameterInjector(paramDependency, localBindingImpl);
  }
  
  public void injectMembers(Object paramObject)
  {
    MembersInjector localMembersInjector = getMembersInjector(paramObject.getClass());
    localMembersInjector.injectMembers(paramObject);
  }
  
  public MembersInjector getMembersInjector(TypeLiteral paramTypeLiteral)
  {
    Errors localErrors = new Errors(paramTypeLiteral);
    try
    {
      return this.membersInjectorStore.get(paramTypeLiteral, localErrors);
    }
    catch (ErrorsException localErrorsException)
    {
      throw new ConfigurationException(localErrors.merge(localErrorsException.getErrors()).getMessages());
    }
  }
  
  public MembersInjector getMembersInjector(Class paramClass)
  {
    return getMembersInjector(TypeLiteral.get(paramClass));
  }
  
  public Provider getProvider(Class paramClass)
  {
    return getProvider(Key.get(paramClass));
  }
  
  Provider getProviderOrThrow(final Dependency paramDependency, Errors paramErrors)
    throws ErrorsException
  {
    Key localKey = paramDependency.getKey();
    final BindingImpl localBindingImpl = getBindingOrThrow(localKey, paramErrors, JitLimitation.NO_JIT);
    new Provider()
    {
      public Object get()
      {
        final Errors localErrors = new Errors(paramDependency);
        try
        {
          Object localObject = InjectorImpl.this.callInContext(new ContextualCallable()
          {
            public Object call(InternalContext paramAnonymous2InternalContext)
              throws ErrorsException
            {
              Dependency localDependency = paramAnonymous2InternalContext.pushDependency(InjectorImpl.2.this.val$dependency, InjectorImpl.2.this.val$binding.getSource());
              try
              {
                Object localObject1 = InjectorImpl.2.this.val$binding.getInternalFactory().get(localErrors, paramAnonymous2InternalContext, InjectorImpl.2.this.val$dependency, false);
                return localObject1;
              }
              finally
              {
                paramAnonymous2InternalContext.popStateAndSetDependency(localDependency);
              }
            }
          });
          localErrors.throwIfNewErrors(0);
          return localObject;
        }
        catch (ErrorsException localErrorsException)
        {
          throw new ProvisionException(localErrors.merge(localErrorsException.getErrors()).getMessages());
        }
      }
      
      public String toString()
      {
        return localBindingImpl.getInternalFactory().toString();
      }
    };
  }
  
  public Provider getProvider(Key paramKey)
  {
    Errors localErrors = new Errors(paramKey);
    try
    {
      Provider localProvider = getProviderOrThrow(Dependency.get(paramKey), localErrors);
      localErrors.throwIfNewErrors(0);
      return localProvider;
    }
    catch (ErrorsException localErrorsException)
    {
      throw new ConfigurationException(localErrors.merge(localErrorsException.getErrors()).getMessages());
    }
  }
  
  public Object getInstance(Key paramKey)
  {
    return getProvider(paramKey).get();
  }
  
  public Object getInstance(Class paramClass)
  {
    return getProvider(paramClass).get();
  }
  
  static Map getGlobalInternalContext()
  {
    return Collections.unmodifiableMap(globalInternalContext);
  }
  
  /* Error */
  Object callInContext(ContextualCallable paramContextualCallable)
    throws ErrorsException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 84	com/google/inject/internal/InjectorImpl:localContext	Ljava/lang/ThreadLocal;
    //   4: invokevirtual 231	java/lang/ThreadLocal:get	()Ljava/lang/Object;
    //   7: checkcast 3	[Ljava/lang/Object;
    //   10: astore_2
    //   11: aload_2
    //   12: ifnonnull +16 -> 28
    //   15: iconst_1
    //   16: anewarray 61	java/lang/Object
    //   19: astore_2
    //   20: aload_0
    //   21: getfield 84	com/google/inject/internal/InjectorImpl:localContext	Ljava/lang/ThreadLocal;
    //   24: aload_2
    //   25: invokevirtual 232	java/lang/ThreadLocal:set	(Ljava/lang/Object;)V
    //   28: invokestatic 229	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   31: astore_3
    //   32: aload_2
    //   33: iconst_0
    //   34: aaload
    //   35: ifnonnull +83 -> 118
    //   38: aload_2
    //   39: iconst_0
    //   40: new 42	com/google/inject/internal/InternalContext
    //   43: dup
    //   44: aload_0
    //   45: getfield 87	com/google/inject/internal/InjectorImpl:options	Lcom/google/inject/internal/InjectorImpl$InjectorOptions;
    //   48: invokespecial 201	com/google/inject/internal/InternalContext:<init>	(Lcom/google/inject/internal/InjectorImpl$InjectorOptions;)V
    //   51: aastore
    //   52: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   55: aload_3
    //   56: aload_2
    //   57: iconst_0
    //   58: aaload
    //   59: checkcast 42	com/google/inject/internal/InternalContext
    //   62: invokeinterface 269 3 0
    //   67: pop
    //   68: aload_1
    //   69: aload_2
    //   70: iconst_0
    //   71: aaload
    //   72: checkcast 42	com/google/inject/internal/InternalContext
    //   75: invokeinterface 240 2 0
    //   80: astore 4
    //   82: aload_2
    //   83: iconst_0
    //   84: aconst_null
    //   85: aastore
    //   86: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   89: aload_3
    //   90: invokeinterface 270 2 0
    //   95: pop
    //   96: aload 4
    //   98: areturn
    //   99: astore 5
    //   101: aload_2
    //   102: iconst_0
    //   103: aconst_null
    //   104: aastore
    //   105: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   108: aload_3
    //   109: invokeinterface 270 2 0
    //   114: pop
    //   115: aload 5
    //   117: athrow
    //   118: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   121: aload_3
    //   122: invokeinterface 268 2 0
    //   127: astore 4
    //   129: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   132: aload_3
    //   133: aload_2
    //   134: iconst_0
    //   135: aaload
    //   136: checkcast 42	com/google/inject/internal/InternalContext
    //   139: invokeinterface 269 3 0
    //   144: pop
    //   145: aload_1
    //   146: aload_2
    //   147: iconst_0
    //   148: aaload
    //   149: checkcast 42	com/google/inject/internal/InternalContext
    //   152: invokeinterface 240 2 0
    //   157: astore 5
    //   159: aload 4
    //   161: ifnull +21 -> 182
    //   164: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   167: aload_3
    //   168: aload 4
    //   170: checkcast 42	com/google/inject/internal/InternalContext
    //   173: invokeinterface 269 3 0
    //   178: pop
    //   179: goto +13 -> 192
    //   182: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   185: aload_3
    //   186: invokeinterface 270 2 0
    //   191: pop
    //   192: aload 5
    //   194: areturn
    //   195: astore 6
    //   197: aload 4
    //   199: ifnull +21 -> 220
    //   202: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   205: aload_3
    //   206: aload 4
    //   208: checkcast 42	com/google/inject/internal/InternalContext
    //   211: invokeinterface 269 3 0
    //   216: pop
    //   217: goto +13 -> 230
    //   220: getstatic 82	com/google/inject/internal/InjectorImpl:globalInternalContext	Ljava/util/concurrent/ConcurrentMap;
    //   223: aload_3
    //   224: invokeinterface 270 2 0
    //   229: pop
    //   230: aload 6
    //   232: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	233	0	this	InjectorImpl
    //   0	233	1	paramContextualCallable	ContextualCallable
    //   10	137	2	arrayOfObject	Object[]
    //   31	193	3	localThread	Thread
    //   80	127	4	localObject1	Object
    //   99	17	5	localObject2	Object
    //   157	36	5	localObject3	Object
    //   195	36	6	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   68	82	99	finally
    //   99	101	99	finally
    //   145	159	195	finally
    //   195	197	195	finally
  }
  
  public String toString()
  {
    return Objects.toStringHelper(Injector.class).add("bindings", this.state.getExplicitBindingsThisLevel().values()).toString();
  }
  
  static abstract interface MethodInvoker
  {
    public abstract Object invoke(Object paramObject, Object... paramVarArgs)
      throws IllegalAccessException, InvocationTargetException;
  }
  
  private static class BindingsMultimap
  {
    final Map multimap = Maps.newHashMap();
    
    void put(TypeLiteral paramTypeLiteral, Binding paramBinding)
    {
      Object localObject = (List)this.multimap.get(paramTypeLiteral);
      if (localObject == null)
      {
        localObject = Lists.newArrayList();
        this.multimap.put(paramTypeLiteral, localObject);
      }
      ((List)localObject).add(paramBinding);
    }
    
    List getAll(TypeLiteral paramTypeLiteral)
    {
      List localList = (List)this.multimap.get(paramTypeLiteral);
      return localList != null ? Collections.unmodifiableList((List)this.multimap.get(paramTypeLiteral)) : ImmutableList.of();
    }
  }
  
  private static class ConvertedConstantBindingImpl
    extends BindingImpl
    implements ConvertedConstantBinding
  {
    final Object value;
    final Provider provider;
    final Binding originalBinding;
    final TypeConverterBinding typeConverterBinding;
    
    ConvertedConstantBindingImpl(InjectorImpl paramInjectorImpl, Key paramKey, Object paramObject, Binding paramBinding, TypeConverterBinding paramTypeConverterBinding)
    {
      super(paramKey, paramBinding.getSource(), new ConstantFactory(Initializables.of(paramObject)), Scoping.UNSCOPED);
      this.value = paramObject;
      this.provider = Providers.of(paramObject);
      this.originalBinding = paramBinding;
      this.typeConverterBinding = paramTypeConverterBinding;
    }
    
    public Provider getProvider()
    {
      return this.provider;
    }
    
    public Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor)
    {
      return paramBindingTargetVisitor.visit(this);
    }
    
    public Object getValue()
    {
      return this.value;
    }
    
    public TypeConverterBinding getTypeConverterBinding()
    {
      return this.typeConverterBinding;
    }
    
    public Key getSourceKey()
    {
      return this.originalBinding.getKey();
    }
    
    public Set getDependencies()
    {
      return ImmutableSet.of(Dependency.get(getSourceKey()));
    }
    
    public void applyTo(Binder paramBinder)
    {
      throw new UnsupportedOperationException("This element represents a synthetic binding.");
    }
    
    public String toString()
    {
      return Objects.toStringHelper(ConvertedConstantBinding.class).add("key", getKey()).add("sourceKey", getSourceKey()).add("value", this.value).toString();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ConvertedConstantBindingImpl))
      {
        ConvertedConstantBindingImpl localConvertedConstantBindingImpl = (ConvertedConstantBindingImpl)paramObject;
        return (getKey().equals(localConvertedConstantBindingImpl.getKey())) && (getScoping().equals(localConvertedConstantBindingImpl.getScoping())) && (Objects.equal(this.value, localConvertedConstantBindingImpl.value));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { getKey(), getScoping(), this.value });
    }
  }
  
  private static class ProviderBindingImpl
    extends BindingImpl
    implements HasDependencies, ProviderBinding
  {
    final BindingImpl providedBinding;
    
    ProviderBindingImpl(InjectorImpl paramInjectorImpl, Key paramKey, Binding paramBinding)
    {
      super(paramKey, paramBinding.getSource(), createInternalFactory(paramBinding), Scoping.UNSCOPED);
      this.providedBinding = ((BindingImpl)paramBinding);
    }
    
    static InternalFactory createInternalFactory(Binding paramBinding)
    {
      Provider localProvider = paramBinding.getProvider();
      new InternalFactory()
      {
        public Provider get(Errors paramAnonymousErrors, InternalContext paramAnonymousInternalContext, Dependency paramAnonymousDependency, boolean paramAnonymousBoolean)
        {
          return this.val$provider;
        }
      };
    }
    
    public Key getProvidedKey()
    {
      return this.providedBinding.getKey();
    }
    
    public Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor)
    {
      return paramBindingTargetVisitor.visit(this);
    }
    
    public void applyTo(Binder paramBinder)
    {
      throw new UnsupportedOperationException("This element represents a synthetic binding.");
    }
    
    public String toString()
    {
      return Objects.toStringHelper(ProviderBinding.class).add("key", getKey()).add("providedKey", getProvidedKey()).toString();
    }
    
    public Set getDependencies()
    {
      return ImmutableSet.of(Dependency.get(getProvidedKey()));
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ProviderBindingImpl))
      {
        ProviderBindingImpl localProviderBindingImpl = (ProviderBindingImpl)paramObject;
        return (getKey().equals(localProviderBindingImpl.getKey())) && (getScoping().equals(localProviderBindingImpl.getScoping())) && (Objects.equal(this.providedBinding, localProviderBindingImpl.providedBinding));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { getKey(), getScoping(), this.providedBinding });
    }
  }
  
  static enum JitLimitation
  {
    NO_JIT,  EXISTING_JIT,  NEW_OR_EXISTING_JIT;
  }
  
  static class InjectorOptions
  {
    final Stage stage;
    final boolean jitDisabled;
    final boolean disableCircularProxies;
    final boolean atInjectRequired;
    final boolean exactBindingAnnotationsRequired;
    
    InjectorOptions(Stage paramStage, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      this.stage = paramStage;
      this.jitDisabled = paramBoolean1;
      this.disableCircularProxies = paramBoolean2;
      this.atInjectRequired = paramBoolean3;
      this.exactBindingAnnotationsRequired = paramBoolean4;
    }
    
    public String toString()
    {
      return Objects.toStringHelper(getClass()).add("stage", this.stage).add("jitDisabled", this.jitDisabled).add("disableCircularProxies", this.disableCircularProxies).add("atInjectRequired", this.atInjectRequired).add("exactBindingAnnotationsRequired", this.exactBindingAnnotationsRequired).toString();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InjectorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */