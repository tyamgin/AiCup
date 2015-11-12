package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.internal.AbstractBindingBuilder;
import com.google.inject.internal.BindingBuilder;
import com.google.inject.internal.ConstantBindingBuilderImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ExposureBuilder;
import com.google.inject.internal.InternalFlags;
import com.google.inject.internal.InternalFlags.IncludeStackTraceOption;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.PrivateElementsImpl;
import com.google.inject.internal.ProviderMethodsModule;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.matcher.Matcher;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;

public final class Elements
{
  private static final BindingTargetVisitor GET_INSTANCE_VISITOR = new DefaultBindingTargetVisitor()
  {
    public Object visit(InstanceBinding paramAnonymousInstanceBinding)
    {
      return paramAnonymousInstanceBinding.getInstance();
    }
    
    protected Object visitOther(Binding paramAnonymousBinding)
    {
      throw new IllegalArgumentException();
    }
  };
  
  public static List getElements(Module... paramVarArgs)
  {
    return getElements(Stage.DEVELOPMENT, Arrays.asList(paramVarArgs));
  }
  
  public static List getElements(Stage paramStage, Module... paramVarArgs)
  {
    return getElements(paramStage, Arrays.asList(paramVarArgs));
  }
  
  public static List getElements(Iterable paramIterable)
  {
    return getElements(Stage.DEVELOPMENT, paramIterable);
  }
  
  public static List getElements(Stage paramStage, Iterable paramIterable)
  {
    RecordingBinder localRecordingBinder = new RecordingBinder(paramStage, null);
    Iterator localIterator = paramIterable.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Module)localIterator.next();
      localRecordingBinder.install((Module)localObject);
    }
    localRecordingBinder.scanForAnnotatedMethods();
    localIterator = localRecordingBinder.privateBinders.iterator();
    while (localIterator.hasNext())
    {
      localObject = (RecordingBinder)localIterator.next();
      ((RecordingBinder)localObject).scanForAnnotatedMethods();
    }
    StackTraceElements.clearCache();
    return Collections.unmodifiableList(localRecordingBinder.elements);
  }
  
  public static Module getModule(Iterable paramIterable)
  {
    return new ElementsAsModule(paramIterable);
  }
  
  static BindingTargetVisitor getInstanceVisitor()
  {
    return GET_INSTANCE_VISITOR;
  }
  
  private static class RecordingBinder
    implements Binder, PrivateBinder
  {
    private final Stage stage;
    private final Map modules;
    private final List elements;
    private final Object source;
    private ModuleSource moduleSource = null;
    private final SourceProvider sourceProvider;
    private final Set scanners;
    private final RecordingBinder parent;
    private final PrivateElementsImpl privateElements;
    private final List privateBinders;
    
    private RecordingBinder(Stage paramStage)
    {
      this.stage = paramStage;
      this.modules = Maps.newLinkedHashMap();
      this.scanners = Sets.newLinkedHashSet();
      this.elements = Lists.newArrayList();
      this.source = null;
      this.sourceProvider = SourceProvider.DEFAULT_INSTANCE.plusSkippedClasses(new Class[] { Elements.class, RecordingBinder.class, AbstractModule.class, ConstantBindingBuilderImpl.class, AbstractBindingBuilder.class, BindingBuilder.class });
      this.parent = null;
      this.privateElements = null;
      this.privateBinders = Lists.newArrayList();
    }
    
    private RecordingBinder(RecordingBinder paramRecordingBinder, Object paramObject, SourceProvider paramSourceProvider)
    {
      Preconditions.checkArgument((paramObject == null ? 1 : 0) ^ (paramSourceProvider == null ? 1 : 0));
      this.stage = paramRecordingBinder.stage;
      this.modules = paramRecordingBinder.modules;
      this.elements = paramRecordingBinder.elements;
      this.scanners = paramRecordingBinder.scanners;
      this.source = paramObject;
      this.moduleSource = paramRecordingBinder.moduleSource;
      this.sourceProvider = paramSourceProvider;
      this.parent = paramRecordingBinder.parent;
      this.privateElements = paramRecordingBinder.privateElements;
      this.privateBinders = paramRecordingBinder.privateBinders;
    }
    
    private RecordingBinder(RecordingBinder paramRecordingBinder, PrivateElementsImpl paramPrivateElementsImpl)
    {
      this.stage = paramRecordingBinder.stage;
      this.modules = Maps.newLinkedHashMap();
      this.scanners = Sets.newLinkedHashSet(paramRecordingBinder.scanners);
      this.elements = paramPrivateElementsImpl.getElementsMutable();
      this.source = paramRecordingBinder.source;
      this.moduleSource = paramRecordingBinder.moduleSource;
      this.sourceProvider = paramRecordingBinder.sourceProvider;
      this.parent = paramRecordingBinder;
      this.privateElements = paramPrivateElementsImpl;
      this.privateBinders = paramRecordingBinder.privateBinders;
    }
    
    public void bindInterceptor(Matcher paramMatcher1, Matcher paramMatcher2, MethodInterceptor... paramVarArgs)
    {
      this.elements.add(new InterceptorBinding(getElementSource(), paramMatcher1, paramMatcher2, paramVarArgs));
    }
    
    public void bindScope(Class paramClass, Scope paramScope)
    {
      this.elements.add(new ScopeBinding(getElementSource(), paramClass, paramScope));
    }
    
    public void requestInjection(Object paramObject)
    {
      requestInjection(TypeLiteral.get(paramObject.getClass()), paramObject);
    }
    
    public void requestInjection(TypeLiteral paramTypeLiteral, Object paramObject)
    {
      this.elements.add(new InjectionRequest(getElementSource(), MoreTypes.canonicalizeForKey(paramTypeLiteral), paramObject));
    }
    
    public MembersInjector getMembersInjector(TypeLiteral paramTypeLiteral)
    {
      MembersInjectorLookup localMembersInjectorLookup = new MembersInjectorLookup(getElementSource(), MoreTypes.canonicalizeForKey(paramTypeLiteral));
      this.elements.add(localMembersInjectorLookup);
      return localMembersInjectorLookup.getMembersInjector();
    }
    
    public MembersInjector getMembersInjector(Class paramClass)
    {
      return getMembersInjector(TypeLiteral.get(paramClass));
    }
    
    public void bindListener(Matcher paramMatcher, TypeListener paramTypeListener)
    {
      this.elements.add(new TypeListenerBinding(getElementSource(), paramTypeListener, paramMatcher));
    }
    
    public void bindListener(Matcher paramMatcher, ProvisionListener... paramVarArgs)
    {
      this.elements.add(new ProvisionListenerBinding(getElementSource(), paramMatcher, paramVarArgs));
    }
    
    public void requestStaticInjection(Class... paramVarArgs)
    {
      for (Class localClass : paramVarArgs) {
        this.elements.add(new StaticInjectionRequest(getElementSource(), localClass));
      }
    }
    
    void scanForAnnotatedMethods()
    {
      Iterator localIterator1 = this.scanners.iterator();
      while (localIterator1.hasNext())
      {
        ModuleAnnotatedMethodScanner localModuleAnnotatedMethodScanner = (ModuleAnnotatedMethodScanner)localIterator1.next();
        Iterator localIterator2 = Maps.newLinkedHashMap(this.modules).entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          Module localModule = (Module)localEntry.getKey();
          Elements.ModuleInfo localModuleInfo = (Elements.ModuleInfo)localEntry.getValue();
          if (!Elements.ModuleInfo.access$300(localModuleInfo))
          {
            this.moduleSource = Elements.ModuleInfo.access$400((Elements.ModuleInfo)localEntry.getValue());
            try
            {
              Elements.ModuleInfo.access$500(localModuleInfo).install(ProviderMethodsModule.forModule(localModule, localModuleAnnotatedMethodScanner));
            }
            catch (RuntimeException localRuntimeException)
            {
              Collection localCollection = Errors.getMessagesFromThrowable(localRuntimeException);
              if (!localCollection.isEmpty()) {
                this.elements.addAll(localCollection);
              } else {
                addError(localRuntimeException);
              }
            }
          }
        }
      }
      this.moduleSource = null;
    }
    
    public void install(Module paramModule)
    {
      if (!this.modules.containsKey(paramModule))
      {
        RecordingBinder localRecordingBinder = this;
        int i = 0;
        if ((paramModule instanceof ProviderMethodsModule))
        {
          Object localObject = ((ProviderMethodsModule)paramModule).getDelegateModule();
          if ((this.moduleSource == null) || (!this.moduleSource.getModuleClassName().equals(localObject.getClass().getName())))
          {
            this.moduleSource = getModuleSource(localObject);
            i = 1;
          }
        }
        else
        {
          this.moduleSource = getModuleSource(paramModule);
          i = 1;
        }
        boolean bool = false;
        if ((paramModule instanceof PrivateModule))
        {
          localRecordingBinder = (RecordingBinder)localRecordingBinder.newPrivateBinder();
          localRecordingBinder.modules.put(paramModule, new Elements.ModuleInfo(localRecordingBinder, this.moduleSource, false, null));
          bool = true;
        }
        this.modules.put(paramModule, new Elements.ModuleInfo(localRecordingBinder, this.moduleSource, bool, null));
        try
        {
          paramModule.configure(localRecordingBinder);
        }
        catch (RuntimeException localRuntimeException)
        {
          Collection localCollection = Errors.getMessagesFromThrowable(localRuntimeException);
          if (!localCollection.isEmpty()) {
            this.elements.addAll(localCollection);
          } else {
            addError(localRuntimeException);
          }
        }
        localRecordingBinder.install(ProviderMethodsModule.forModule(paramModule));
        if (i != 0) {
          this.moduleSource = this.moduleSource.getParent();
        }
      }
    }
    
    public Stage currentStage()
    {
      return this.stage;
    }
    
    public void addError(String paramString, Object... paramVarArgs)
    {
      this.elements.add(new Message(getElementSource(), Errors.format(paramString, paramVarArgs)));
    }
    
    /* Error */
    public void addError(Throwable arg1)
    {
      // Byte code:
      //   0: ldc 1
      //   2: aload_1
      //   3: invokevirtual 167	java/lang/Throwable:getMessage	()Ljava/lang/String;
      //   6: invokestatic 164	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   9: dup
      //   10: invokevirtual 163	java/lang/String:length	()I
      //   13: ifeq +9 -> 22
      //   16: invokevirtual 161	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
      //   19: goto +12 -> 31
      //   22: pop
      //   23: new 57	java/lang/String
      //   26: dup_x1
      //   27: swap
      //   28: invokespecial 160	java/lang/String:<init>	(Ljava/lang/String;)V
      //   31: astore_2
      //   32: aload_0
      //   33: getfield 70	com/google/inject/spi/Elements$RecordingBinder:elements	Ljava/util/List;
      //   36: new 40	com/google/inject/spi/Message
      //   39: dup
      //   40: aload_0
      //   41: invokespecial 122	com/google/inject/spi/Elements$RecordingBinder:getElementSource	()Lcom/google/inject/spi/ElementSource;
      //   44: invokestatic 81	com/google/common/collect/ImmutableList:of	(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;
      //   47: aload_2
      //   48: aload_1
      //   49: invokespecial 139	com/google/inject/spi/Message:<init>	(Ljava/util/List;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   52: invokeinterface 175 2 0
      //   57: pop
      //   58: return
    }
    
    public void addError(Message paramMessage)
    {
      this.elements.add(paramMessage);
    }
    
    public AnnotatedBindingBuilder bind(Key paramKey)
    {
      BindingBuilder localBindingBuilder = new BindingBuilder(this, this.elements, getElementSource(), MoreTypes.canonicalizeKey(paramKey));
      return localBindingBuilder;
    }
    
    public AnnotatedBindingBuilder bind(TypeLiteral paramTypeLiteral)
    {
      return bind(Key.get(paramTypeLiteral));
    }
    
    public AnnotatedBindingBuilder bind(Class paramClass)
    {
      return bind(Key.get(paramClass));
    }
    
    public AnnotatedConstantBindingBuilder bindConstant()
    {
      return new ConstantBindingBuilderImpl(this, this.elements, getElementSource());
    }
    
    public Provider getProvider(Key paramKey)
    {
      return getProvider(Dependency.get(paramKey));
    }
    
    public Provider getProvider(Dependency paramDependency)
    {
      ProviderLookup localProviderLookup = new ProviderLookup(getElementSource(), paramDependency);
      this.elements.add(localProviderLookup);
      return localProviderLookup.getProvider();
    }
    
    public Provider getProvider(Class paramClass)
    {
      return getProvider(Key.get(paramClass));
    }
    
    public void convertToTypes(Matcher paramMatcher, TypeConverter paramTypeConverter)
    {
      this.elements.add(new TypeConverterBinding(getElementSource(), paramMatcher, paramTypeConverter));
    }
    
    public RecordingBinder withSource(Object paramObject)
    {
      return paramObject == this.source ? this : new RecordingBinder(this, paramObject, null);
    }
    
    public RecordingBinder skipSources(Class... paramVarArgs)
    {
      if (this.source != null) {
        return this;
      }
      SourceProvider localSourceProvider = this.sourceProvider.plusSkippedClasses(paramVarArgs);
      return new RecordingBinder(this, null, localSourceProvider);
    }
    
    public PrivateBinder newPrivateBinder()
    {
      PrivateElementsImpl localPrivateElementsImpl = new PrivateElementsImpl(getElementSource());
      RecordingBinder localRecordingBinder = new RecordingBinder(this, localPrivateElementsImpl);
      this.privateBinders.add(localRecordingBinder);
      this.elements.add(localPrivateElementsImpl);
      return localRecordingBinder;
    }
    
    public void disableCircularProxies()
    {
      this.elements.add(new DisableCircularProxiesOption(getElementSource()));
    }
    
    public void requireExplicitBindings()
    {
      this.elements.add(new RequireExplicitBindingsOption(getElementSource()));
    }
    
    public void requireAtInjectOnConstructors()
    {
      this.elements.add(new RequireAtInjectOnConstructorsOption(getElementSource()));
    }
    
    public void requireExactBindingAnnotations()
    {
      this.elements.add(new RequireExactBindingAnnotationsOption(getElementSource()));
    }
    
    public void scanModulesForAnnotatedMethods(ModuleAnnotatedMethodScanner paramModuleAnnotatedMethodScanner)
    {
      this.scanners.add(paramModuleAnnotatedMethodScanner);
      this.elements.add(new ModuleAnnotatedMethodScannerBinding(getElementSource(), paramModuleAnnotatedMethodScanner));
    }
    
    public void expose(Key paramKey)
    {
      exposeInternal(paramKey);
    }
    
    public AnnotatedElementBuilder expose(Class paramClass)
    {
      return exposeInternal(Key.get(paramClass));
    }
    
    public AnnotatedElementBuilder expose(TypeLiteral paramTypeLiteral)
    {
      return exposeInternal(Key.get(paramTypeLiteral));
    }
    
    private AnnotatedElementBuilder exposeInternal(Key paramKey)
    {
      if (this.privateElements == null)
      {
        addError("Cannot expose %s on a standard binder. Exposed bindings are only applicable to private binders.", new Object[] { paramKey });
        new AnnotatedElementBuilder()
        {
          public void annotatedWith(Class paramAnonymousClass) {}
          
          public void annotatedWith(Annotation paramAnonymousAnnotation) {}
        };
      }
      ExposureBuilder localExposureBuilder = new ExposureBuilder(this, getElementSource(), MoreTypes.canonicalizeKey(paramKey));
      this.privateElements.addExposureBuilder(localExposureBuilder);
      return localExposureBuilder;
    }
    
    private ModuleSource getModuleSource(Object paramObject)
    {
      StackTraceElement[] arrayOfStackTraceElement;
      if (InternalFlags.getIncludeStackTraceOption() == InternalFlags.IncludeStackTraceOption.COMPLETE) {
        arrayOfStackTraceElement = getPartialCallStack(new Throwable().getStackTrace());
      } else {
        arrayOfStackTraceElement = new StackTraceElement[0];
      }
      if (this.moduleSource == null) {
        return new ModuleSource(paramObject, arrayOfStackTraceElement);
      }
      return this.moduleSource.createChild(paramObject, arrayOfStackTraceElement);
    }
    
    private ElementSource getElementSource()
    {
      StackTraceElement[] arrayOfStackTraceElement1 = null;
      StackTraceElement[] arrayOfStackTraceElement2 = new StackTraceElement[0];
      ElementSource localElementSource = null;
      Object localObject = this.source;
      if ((localObject instanceof ElementSource))
      {
        localElementSource = (ElementSource)localObject;
        localObject = localElementSource.getDeclaringSource();
      }
      InternalFlags.IncludeStackTraceOption localIncludeStackTraceOption = InternalFlags.getIncludeStackTraceOption();
      if ((localIncludeStackTraceOption == InternalFlags.IncludeStackTraceOption.COMPLETE) || ((localIncludeStackTraceOption == InternalFlags.IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE) && (localObject == null))) {
        arrayOfStackTraceElement1 = new Throwable().getStackTrace();
      }
      if (localIncludeStackTraceOption == InternalFlags.IncludeStackTraceOption.COMPLETE) {
        arrayOfStackTraceElement2 = getPartialCallStack(arrayOfStackTraceElement1);
      }
      if (localObject == null) {
        if ((localIncludeStackTraceOption == InternalFlags.IncludeStackTraceOption.COMPLETE) || (localIncludeStackTraceOption == InternalFlags.IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE)) {
          localObject = this.sourceProvider.get(arrayOfStackTraceElement1);
        } else {
          localObject = this.sourceProvider.getFromClassNames(this.moduleSource.getModuleClassNames());
        }
      }
      return new ElementSource(localElementSource, localObject, this.moduleSource, arrayOfStackTraceElement2);
    }
    
    private StackTraceElement[] getPartialCallStack(StackTraceElement[] paramArrayOfStackTraceElement)
    {
      int i = 0;
      if (this.moduleSource != null) {
        i = this.moduleSource.getStackTraceSize();
      }
      int j = paramArrayOfStackTraceElement.length - i - 1;
      StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[j];
      System.arraycopy(paramArrayOfStackTraceElement, 1, arrayOfStackTraceElement, 0, j);
      return arrayOfStackTraceElement;
    }
    
    public String toString()
    {
      return "Binder";
    }
  }
  
  private static class ModuleInfo
  {
    private final Binder binder;
    private final ModuleSource moduleSource;
    private final boolean skipScanning;
    
    private ModuleInfo(Binder paramBinder, ModuleSource paramModuleSource, boolean paramBoolean)
    {
      this.binder = paramBinder;
      this.moduleSource = paramModuleSource;
      this.skipScanning = paramBoolean;
    }
  }
  
  private static class ElementsAsModule
    implements Module
  {
    private final Iterable elements;
    
    ElementsAsModule(Iterable paramIterable)
    {
      this.elements = paramIterable;
    }
    
    public void configure(Binder paramBinder)
    {
      Iterator localIterator = this.elements.iterator();
      while (localIterator.hasNext())
      {
        Element localElement = (Element)localIterator.next();
        localElement.applyTo(paramBinder);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\Elements.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */