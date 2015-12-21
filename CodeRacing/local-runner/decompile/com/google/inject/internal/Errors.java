package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.inject.ConfigurationException;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.Classes;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ElementSource;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Message;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Errors
  implements Serializable
{
  private static final Logger logger = Logger.getLogger(Guice.class.getName());
  private static final Set warnedDependencies = Sets.newSetFromMap(new ConcurrentHashMap());
  private final Errors root;
  private final Errors parent;
  private final Object source;
  private List errors;
  private static final String CONSTRUCTOR_RULES = "Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.";
  private static final Collection converters = ImmutableList.of(new Converter(Class.class)new Converter
  {
    public String toString(Class paramAnonymousClass)
    {
      return paramAnonymousClass.getName();
    }
  }, new Converter(Member.class)new Converter
  {
    public String toString(Member paramAnonymousMember)
    {
      return Classes.toString(paramAnonymousMember);
    }
  }, new Converter(Key.class)
  {
    public String toString(Key paramAnonymousKey)
    {
      if (paramAnonymousKey.getAnnotationType() != null)
      {
        String str1 = String.valueOf(String.valueOf(paramAnonymousKey.getTypeLiteral()));
        String str2 = String.valueOf(String.valueOf(paramAnonymousKey.getAnnotation() != null ? paramAnonymousKey.getAnnotation() : paramAnonymousKey.getAnnotationType()));
        return 16 + str1.length() + str2.length() + str1 + " annotated with " + str2;
      }
      return paramAnonymousKey.getTypeLiteral().toString();
    }
  });
  
  public Errors()
  {
    this.root = this;
    this.parent = null;
    this.source = SourceProvider.UNKNOWN_SOURCE;
  }
  
  public Errors(Object paramObject)
  {
    this.root = this;
    this.parent = null;
    this.source = paramObject;
  }
  
  private Errors(Errors paramErrors, Object paramObject)
  {
    this.root = paramErrors.root;
    this.parent = paramErrors;
    this.source = paramObject;
  }
  
  public Errors withSource(Object paramObject)
  {
    return (paramObject == this.source) || (paramObject == SourceProvider.UNKNOWN_SOURCE) ? this : new Errors(this, paramObject);
  }
  
  public Errors missingImplementation(Key paramKey)
  {
    return addMessage("No implementation for %s was bound.", new Object[] { paramKey });
  }
  
  public Errors jitDisabled(Key paramKey)
  {
    return addMessage("Explicit bindings are required and %s is not explicitly bound.", new Object[] { paramKey });
  }
  
  public Errors jitDisabledInParent(Key paramKey)
  {
    return addMessage("Explicit bindings are required and %s would be bound in a parent injector.%nPlease add an explicit binding for it, either in the child or the parent.", new Object[] { paramKey });
  }
  
  public Errors atInjectRequired(Class paramClass)
  {
    return addMessage("Explicit @Inject annotations are required on constructors, but %s has no constructors annotated with @Inject.", new Object[] { paramClass });
  }
  
  public Errors converterReturnedNull(String paramString, Object paramObject, TypeLiteral paramTypeLiteral, TypeConverterBinding paramTypeConverterBinding)
  {
    return addMessage("Received null converting '%s' (bound at %s) to %s%n using %s.", new Object[] { paramString, convert(paramObject), paramTypeLiteral, paramTypeConverterBinding });
  }
  
  public Errors conversionTypeError(String paramString, Object paramObject1, TypeLiteral paramTypeLiteral, TypeConverterBinding paramTypeConverterBinding, Object paramObject2)
  {
    return addMessage("Type mismatch converting '%s' (bound at %s) to %s%n using %s.%n Converter returned %s.", new Object[] { paramString, convert(paramObject1), paramTypeLiteral, paramTypeConverterBinding, paramObject2 });
  }
  
  public Errors conversionError(String paramString, Object paramObject, TypeLiteral paramTypeLiteral, TypeConverterBinding paramTypeConverterBinding, RuntimeException paramRuntimeException)
  {
    return errorInUserCode(paramRuntimeException, "Error converting '%s' (bound at %s) to %s%n using %s.%n Reason: %s", new Object[] { paramString, convert(paramObject), paramTypeLiteral, paramTypeConverterBinding, paramRuntimeException });
  }
  
  public Errors ambiguousTypeConversion(String paramString, Object paramObject, TypeLiteral paramTypeLiteral, TypeConverterBinding paramTypeConverterBinding1, TypeConverterBinding paramTypeConverterBinding2)
  {
    return addMessage("Multiple converters can convert '%s' (bound at %s) to %s:%n %s and%n %s.%n Please adjust your type converter configuration to avoid overlapping matches.", new Object[] { paramString, convert(paramObject), paramTypeLiteral, paramTypeConverterBinding1, paramTypeConverterBinding2 });
  }
  
  public Errors bindingToProvider()
  {
    return addMessage("Binding to Provider is not allowed.", new Object[0]);
  }
  
  public Errors subtypeNotProvided(Class paramClass1, Class paramClass2)
  {
    return addMessage("%s doesn't provide instances of %s.", new Object[] { paramClass1, paramClass2 });
  }
  
  public Errors notASubtype(Class paramClass1, Class paramClass2)
  {
    return addMessage("%s doesn't extend %s.", new Object[] { paramClass1, paramClass2 });
  }
  
  public Errors recursiveImplementationType()
  {
    return addMessage("@ImplementedBy points to the same class it annotates.", new Object[0]);
  }
  
  public Errors recursiveProviderType()
  {
    return addMessage("@ProvidedBy points to the same class it annotates.", new Object[0]);
  }
  
  public Errors missingRuntimeRetention(Class paramClass)
  {
    return addMessage(format("Please annotate %s with @Retention(RUNTIME).", new Object[] { paramClass }), new Object[0]);
  }
  
  public Errors missingScopeAnnotation(Class paramClass)
  {
    return addMessage(format("Please annotate %s with @ScopeAnnotation.", new Object[] { paramClass }), new Object[0]);
  }
  
  public Errors optionalConstructor(Constructor paramConstructor)
  {
    return addMessage("%s is annotated @Inject(optional=true), but constructors cannot be optional.", new Object[] { paramConstructor });
  }
  
  public Errors cannotBindToGuiceType(String paramString)
  {
    return addMessage("Binding to core guice framework type is not allowed: %s.", new Object[] { paramString });
  }
  
  public Errors scopeNotFound(Class paramClass)
  {
    return addMessage("No scope is bound to %s.", new Object[] { paramClass });
  }
  
  public Errors scopeAnnotationOnAbstractType(Class paramClass1, Class paramClass2, Object paramObject)
  {
    return addMessage("%s is annotated with %s, but scope annotations are not supported for abstract types.%n Bound at %s.", new Object[] { paramClass2, paramClass1, convert(paramObject) });
  }
  
  public Errors misplacedBindingAnnotation(Member paramMember, Annotation paramAnnotation)
  {
    return addMessage("%s is annotated with %s, but binding annotations should be applied to its parameters instead.", new Object[] { paramMember, paramAnnotation });
  }
  
  public Errors missingConstructor(Class paramClass)
  {
    return addMessage("Could not find a suitable constructor in %s. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.", new Object[] { paramClass });
  }
  
  public Errors tooManyConstructors(Class paramClass)
  {
    return addMessage("%s has more than one constructor annotated with @Inject. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.", new Object[] { paramClass });
  }
  
  public Errors constructorNotDefinedByType(Constructor paramConstructor, TypeLiteral paramTypeLiteral)
  {
    return addMessage("%s does not define %s", new Object[] { paramTypeLiteral, paramConstructor });
  }
  
  public Errors duplicateScopes(ScopeBinding paramScopeBinding, Class paramClass, Scope paramScope)
  {
    return addMessage("Scope %s is already bound to %s at %s.%n Cannot bind %s.", new Object[] { paramScopeBinding.getScope(), paramClass, paramScopeBinding.getSource(), paramScope });
  }
  
  public Errors voidProviderMethod()
  {
    return addMessage("Provider methods must return a value. Do not return void.", new Object[0]);
  }
  
  public Errors missingConstantValues()
  {
    return addMessage("Missing constant value. Please call to(...).", new Object[0]);
  }
  
  public Errors cannotInjectInnerClass(Class paramClass)
  {
    return addMessage("Injecting into inner classes is not supported.  Please use a 'static' class (top-level or nested) instead of %s.", new Object[] { paramClass });
  }
  
  public Errors duplicateBindingAnnotations(Member paramMember, Class paramClass1, Class paramClass2)
  {
    return addMessage("%s has more than one annotation annotated with @BindingAnnotation: %s and %s", new Object[] { paramMember, paramClass1, paramClass2 });
  }
  
  public Errors staticInjectionOnInterface(Class paramClass)
  {
    return addMessage("%s is an interface, but interfaces have no static injection points.", new Object[] { paramClass });
  }
  
  public Errors cannotInjectFinalField(Field paramField)
  {
    return addMessage("Injected field %s cannot be final.", new Object[] { paramField });
  }
  
  public Errors cannotInjectAbstractMethod(Method paramMethod)
  {
    return addMessage("Injected method %s cannot be abstract.", new Object[] { paramMethod });
  }
  
  public Errors cannotInjectNonVoidMethod(Method paramMethod)
  {
    return addMessage("Injected method %s must return void.", new Object[] { paramMethod });
  }
  
  public Errors cannotInjectMethodWithTypeParameters(Method paramMethod)
  {
    return addMessage("Injected method %s cannot declare type parameters of its own.", new Object[] { paramMethod });
  }
  
  public Errors duplicateScopeAnnotations(Class paramClass1, Class paramClass2)
  {
    return addMessage("More than one scope annotation was found: %s and %s.", new Object[] { paramClass1, paramClass2 });
  }
  
  public Errors recursiveBinding()
  {
    return addMessage("Binding points to itself.", new Object[0]);
  }
  
  public Errors bindingAlreadySet(Key paramKey, Object paramObject)
  {
    return addMessage("A binding to %s was already configured at %s.", new Object[] { paramKey, convert(paramObject) });
  }
  
  public Errors jitBindingAlreadySet(Key paramKey)
  {
    return addMessage("A just-in-time binding to %s was already configured on a parent injector.", new Object[] { paramKey });
  }
  
  public Errors childBindingAlreadySet(Key paramKey, Set paramSet)
  {
    Formatter localFormatter = new Formatter();
    Object localObject1 = paramSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = ((Iterator)localObject1).next();
      if (localObject2 == null) {
        localFormatter.format("%n    (bound by a just-in-time binding)", new Object[0]);
      } else {
        localFormatter.format("%n    bound at %s", new Object[] { localObject2 });
      }
    }
    localObject1 = addMessage("Unable to create binding for %s. It was already configured on one or more child injectors or private modules%s%n  If it was in a PrivateModule, did you forget to expose the binding?", new Object[] { paramKey, localFormatter.out() });
    return (Errors)localObject1;
  }
  
  public Errors errorCheckingDuplicateBinding(Key paramKey, Object paramObject, Throwable paramThrowable)
  {
    return addMessage("A binding to %s was already configured at %s and an error was thrown while checking duplicate bindings.  Error: %s", new Object[] { paramKey, convert(paramObject), paramThrowable });
  }
  
  public Errors errorInjectingMethod(Throwable paramThrowable)
  {
    return errorInUserCode(paramThrowable, "Error injecting method, %s", new Object[] { paramThrowable });
  }
  
  public Errors errorNotifyingTypeListener(TypeListenerBinding paramTypeListenerBinding, TypeLiteral paramTypeLiteral, Throwable paramThrowable)
  {
    return errorInUserCode(paramThrowable, "Error notifying TypeListener %s (bound at %s) of %s.%n Reason: %s", new Object[] { paramTypeListenerBinding.getListener(), convert(paramTypeListenerBinding.getSource()), paramTypeLiteral, paramThrowable });
  }
  
  public Errors errorInjectingConstructor(Throwable paramThrowable)
  {
    return errorInUserCode(paramThrowable, "Error injecting constructor, %s", new Object[] { paramThrowable });
  }
  
  public Errors errorInProvider(RuntimeException paramRuntimeException)
  {
    Throwable localThrowable = unwrap(paramRuntimeException);
    return errorInUserCode(localThrowable, "Error in custom provider, %s", new Object[] { localThrowable });
  }
  
  public Errors errorInUserInjector(MembersInjector paramMembersInjector, TypeLiteral paramTypeLiteral, RuntimeException paramRuntimeException)
  {
    return errorInUserCode(paramRuntimeException, "Error injecting %s using %s.%n Reason: %s", new Object[] { paramTypeLiteral, paramMembersInjector, paramRuntimeException });
  }
  
  public Errors errorNotifyingInjectionListener(InjectionListener paramInjectionListener, TypeLiteral paramTypeLiteral, RuntimeException paramRuntimeException)
  {
    return errorInUserCode(paramRuntimeException, "Error notifying InjectionListener %s of %s.%n Reason: %s", new Object[] { paramInjectionListener, paramTypeLiteral, paramRuntimeException });
  }
  
  public Errors exposedButNotBound(Key paramKey)
  {
    return addMessage("Could not expose() %s, it must be explicitly bound.", new Object[] { paramKey });
  }
  
  public Errors keyNotFullySpecified(TypeLiteral paramTypeLiteral)
  {
    return addMessage("%s cannot be used as a key; It is not fully specified.", new Object[] { paramTypeLiteral });
  }
  
  public Errors errorEnhancingClass(Class paramClass, Throwable paramThrowable)
  {
    return errorInUserCode(paramThrowable, "Unable to method intercept: %s", new Object[] { paramClass });
  }
  
  public static Collection getMessagesFromThrowable(Throwable paramThrowable)
  {
    if ((paramThrowable instanceof ProvisionException)) {
      return ((ProvisionException)paramThrowable).getErrorMessages();
    }
    if ((paramThrowable instanceof ConfigurationException)) {
      return ((ConfigurationException)paramThrowable).getErrorMessages();
    }
    if ((paramThrowable instanceof CreationException)) {
      return ((CreationException)paramThrowable).getErrorMessages();
    }
    return ImmutableSet.of();
  }
  
  public Errors errorInUserCode(Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    Collection localCollection = getMessagesFromThrowable(paramThrowable);
    if (!localCollection.isEmpty()) {
      return merge(localCollection);
    }
    return addMessage(paramThrowable, paramString, paramVarArgs);
  }
  
  private Throwable unwrap(RuntimeException paramRuntimeException)
  {
    if ((paramRuntimeException instanceof Exceptions.UnhandledCheckedUserException)) {
      return paramRuntimeException.getCause();
    }
    return paramRuntimeException;
  }
  
  public Errors cannotInjectRawProvider()
  {
    return addMessage("Cannot inject a Provider that has no type parameter", new Object[0]);
  }
  
  public Errors cannotInjectRawMembersInjector()
  {
    return addMessage("Cannot inject a MembersInjector that has no type parameter", new Object[0]);
  }
  
  public Errors cannotInjectTypeLiteralOf(Type paramType)
  {
    return addMessage("Cannot inject a TypeLiteral of %s", new Object[] { paramType });
  }
  
  public Errors cannotInjectRawTypeLiteral()
  {
    return addMessage("Cannot inject a TypeLiteral that has no type parameter", new Object[0]);
  }
  
  public Errors cannotSatisfyCircularDependency(Class paramClass)
  {
    return addMessage("Tried proxying %s to support a circular dependency, but it is not an interface.", new Object[] { paramClass });
  }
  
  public Errors circularProxiesDisabled(Class paramClass)
  {
    return addMessage("Tried proxying %s to support a circular dependency, but circular proxies are disabled.", new Object[] { paramClass });
  }
  
  public void throwCreationExceptionIfErrorsExist()
  {
    if (!hasErrors()) {
      return;
    }
    throw new CreationException(getMessages());
  }
  
  public void throwConfigurationExceptionIfErrorsExist()
  {
    if (!hasErrors()) {
      return;
    }
    throw new ConfigurationException(getMessages());
  }
  
  public void throwProvisionExceptionIfErrorsExist()
  {
    if (!hasErrors()) {
      return;
    }
    throw new ProvisionException(getMessages());
  }
  
  private Message merge(Message paramMessage)
  {
    ArrayList localArrayList = Lists.newArrayList();
    localArrayList.addAll(getSources());
    localArrayList.addAll(paramMessage.getSources());
    return new Message(localArrayList, paramMessage.getMessage(), paramMessage.getCause());
  }
  
  public Errors merge(Collection paramCollection)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Message localMessage = (Message)localIterator.next();
      addMessage(merge(localMessage));
    }
    return this;
  }
  
  public Errors merge(Errors paramErrors)
  {
    if ((paramErrors.root == this.root) || (paramErrors.root.errors == null)) {
      return this;
    }
    merge(paramErrors.root.errors);
    return this;
  }
  
  public List getSources()
  {
    ArrayList localArrayList = Lists.newArrayList();
    for (Errors localErrors = this; localErrors != null; localErrors = localErrors.parent) {
      if (localErrors.source != SourceProvider.UNKNOWN_SOURCE) {
        localArrayList.add(0, localErrors.source);
      }
    }
    return localArrayList;
  }
  
  public void throwIfNewErrors(int paramInt)
    throws ErrorsException
  {
    if (size() == paramInt) {
      return;
    }
    throw toException();
  }
  
  public ErrorsException toException()
  {
    return new ErrorsException(this);
  }
  
  public boolean hasErrors()
  {
    return this.root.errors != null;
  }
  
  public Errors addMessage(String paramString, Object... paramVarArgs)
  {
    return addMessage(null, paramString, paramVarArgs);
  }
  
  private Errors addMessage(Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    String str = format(paramString, paramVarArgs);
    addMessage(new Message(getSources(), str, paramThrowable));
    return this;
  }
  
  public Errors addMessage(Message paramMessage)
  {
    if (this.root.errors == null) {
      this.root.errors = Lists.newArrayList();
    }
    this.root.errors.add(paramMessage);
    return this;
  }
  
  public static String format(String paramString, Object... paramVarArgs)
  {
    for (int i = 0; i < paramVarArgs.length; i++) {
      paramVarArgs[i] = convert(paramVarArgs[i]);
    }
    return String.format(paramString, paramVarArgs);
  }
  
  public List getMessages()
  {
    if (this.root.errors == null) {
      return ImmutableList.of();
    }
    new Ordering()
    {
      public int compare(Message paramAnonymousMessage1, Message paramAnonymousMessage2)
      {
        return paramAnonymousMessage1.getSource().compareTo(paramAnonymousMessage2.getSource());
      }
    }.sortedCopy(this.root.errors);
  }
  
  public static String format(String paramString, Collection paramCollection)
  {
    Formatter localFormatter = new Formatter().format(paramString, new Object[0]).format(":%n%n", new Object[0]);
    int i = 1;
    int j = getOnlyCause(paramCollection) == null ? 1 : 0;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Message localMessage = (Message)localIterator.next();
      localFormatter.format("%s) %s%n", new Object[] { Integer.valueOf(i++), localMessage.getMessage() });
      List localList = localMessage.getSources();
      Object localObject;
      for (int k = localList.size() - 1; k >= 0; k--)
      {
        localObject = localList.get(k);
        formatSource(localFormatter, localObject);
      }
      Throwable localThrowable = localMessage.getCause();
      if ((j != 0) && (localThrowable != null))
      {
        localObject = new StringWriter();
        localThrowable.printStackTrace(new PrintWriter((Writer)localObject));
        localFormatter.format("Caused by: %s", new Object[] { ((StringWriter)localObject).getBuffer() });
      }
      localFormatter.format("%n", new Object[0]);
    }
    if (paramCollection.size() == 1) {
      localFormatter.format("1 error", new Object[0]);
    } else {
      localFormatter.format("%s errors", new Object[] { Integer.valueOf(paramCollection.size()) });
    }
    return localFormatter.toString();
  }
  
  public Object checkForNull(Object paramObject1, Object paramObject2, Dependency paramDependency)
    throws ErrorsException
  {
    if ((paramObject1 != null) || (paramDependency.isNullable())) {
      return paramObject1;
    }
    if ((paramDependency.getInjectionPoint().getMember() instanceof Method))
    {
      Method localMethod = (Method)paramDependency.getInjectionPoint().getMember();
      if (localMethod.isAnnotationPresent(Provides.class)) {
        switch (InternalFlags.getNullableProvidesOption())
        {
        case ERROR: 
          break;
        case IGNORE: 
          return paramObject1;
        case WARN: 
          if (!warnedDependencies.add(paramDependency)) {
            return paramObject1;
          }
          logger.log(Level.WARNING, "Guice injected null into parameter {0} of {1} (a {2}), please mark it @Nullable. Use -Dguice_check_nullable_provides_params=ERROR to turn this into an error.", new Object[] { Integer.valueOf(paramDependency.getParameterIndex()), convert(paramDependency.getInjectionPoint().getMember()), convert(paramDependency.getKey()) });
          return null;
        }
      }
    }
    int i = paramDependency.getParameterIndex();
    int j = i;
    String str = i != -1 ? 25 + "parameter " + j + " of " : "";
    addMessage("null returned by binding at %s%n but %s%s is not @Nullable", new Object[] { paramObject2, str, paramDependency.getInjectionPoint().getMember() });
    throw toException();
  }
  
  public static Throwable getOnlyCause(Collection paramCollection)
  {
    Object localObject = null;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Message localMessage = (Message)localIterator.next();
      Throwable localThrowable = localMessage.getCause();
      if (localThrowable != null)
      {
        if (localObject != null) {
          return null;
        }
        localObject = localThrowable;
      }
    }
    return (Throwable)localObject;
  }
  
  public int size()
  {
    return this.root.errors == null ? 0 : this.root.errors.size();
  }
  
  public static Object convert(Object paramObject)
  {
    ElementSource localElementSource = null;
    if ((paramObject instanceof ElementSource))
    {
      localElementSource = (ElementSource)paramObject;
      paramObject = localElementSource.getDeclaringSource();
    }
    return convert(paramObject, localElementSource);
  }
  
  public static Object convert(Object paramObject, ElementSource paramElementSource)
  {
    Iterator localIterator = converters.iterator();
    while (localIterator.hasNext())
    {
      Converter localConverter = (Converter)localIterator.next();
      if (localConverter.appliesTo(paramObject)) {
        return appendModules(localConverter.convert(paramObject), paramElementSource);
      }
    }
    return appendModules(paramObject, paramElementSource);
  }
  
  private static Object appendModules(Object paramObject, ElementSource paramElementSource)
  {
    String str1 = moduleSourceString(paramElementSource);
    if (str1.length() == 0) {
      return paramObject;
    }
    String str2 = String.valueOf(String.valueOf(paramObject));
    String str3 = String.valueOf(String.valueOf(str1));
    return 0 + str2.length() + str3.length() + str2 + str3;
  }
  
  private static String moduleSourceString(ElementSource paramElementSource)
  {
    if (paramElementSource == null) {
      return "";
    }
    ArrayList localArrayList = Lists.newArrayList(paramElementSource.getModuleClassNames());
    while (paramElementSource.getOriginalElementSource() != null)
    {
      paramElementSource = paramElementSource.getOriginalElementSource();
      localArrayList.addAll(0, paramElementSource.getModuleClassNames());
    }
    if (localArrayList.size() <= 1) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(" (via modules: ");
    for (int i = localArrayList.size() - 1; i >= 0; i--)
    {
      localStringBuilder.append((String)localArrayList.get(i));
      if (i != 0) {
        localStringBuilder.append(" -> ");
      }
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public static void formatSource(Formatter paramFormatter, Object paramObject)
  {
    ElementSource localElementSource = null;
    if ((paramObject instanceof ElementSource))
    {
      localElementSource = (ElementSource)paramObject;
      paramObject = localElementSource.getDeclaringSource();
    }
    formatSource(paramFormatter, paramObject, localElementSource);
  }
  
  public static void formatSource(Formatter paramFormatter, Object paramObject, ElementSource paramElementSource)
  {
    String str = moduleSourceString(paramElementSource);
    Object localObject;
    if ((paramObject instanceof Dependency))
    {
      localObject = (Dependency)paramObject;
      InjectionPoint localInjectionPoint = ((Dependency)localObject).getInjectionPoint();
      if (localInjectionPoint != null) {
        formatInjectionPoint(paramFormatter, (Dependency)localObject, localInjectionPoint, paramElementSource);
      } else {
        formatSource(paramFormatter, ((Dependency)localObject).getKey(), paramElementSource);
      }
    }
    else if ((paramObject instanceof InjectionPoint))
    {
      formatInjectionPoint(paramFormatter, null, (InjectionPoint)paramObject, paramElementSource);
    }
    else if ((paramObject instanceof Class))
    {
      paramFormatter.format("  at %s%s%n", new Object[] { StackTraceElements.forType((Class)paramObject), str });
    }
    else if ((paramObject instanceof Member))
    {
      paramFormatter.format("  at %s%s%n", new Object[] { StackTraceElements.forMember((Member)paramObject), str });
    }
    else if ((paramObject instanceof TypeLiteral))
    {
      paramFormatter.format("  while locating %s%s%n", new Object[] { paramObject, str });
    }
    else if ((paramObject instanceof Key))
    {
      localObject = (Key)paramObject;
      paramFormatter.format("  while locating %s%n", new Object[] { convert(localObject, paramElementSource) });
    }
    else if ((paramObject instanceof Thread))
    {
      paramFormatter.format("  in thread %s%n", new Object[] { paramObject });
    }
    else
    {
      paramFormatter.format("  at %s%s%n", new Object[] { paramObject, str });
    }
  }
  
  public static void formatInjectionPoint(Formatter paramFormatter, Dependency paramDependency, InjectionPoint paramInjectionPoint, ElementSource paramElementSource)
  {
    Member localMember = paramInjectionPoint.getMember();
    Class localClass = Classes.memberType(localMember);
    if (localClass == Field.class)
    {
      paramDependency = (Dependency)paramInjectionPoint.getDependencies().get(0);
      paramFormatter.format("  while locating %s%n", new Object[] { convert(paramDependency.getKey(), paramElementSource) });
      paramFormatter.format("    for field at %s%n", new Object[] { StackTraceElements.forMember(localMember) });
    }
    else if (paramDependency != null)
    {
      paramFormatter.format("  while locating %s%n", new Object[] { convert(paramDependency.getKey(), paramElementSource) });
      paramFormatter.format("    for parameter %s at %s%n", new Object[] { Integer.valueOf(paramDependency.getParameterIndex()), StackTraceElements.forMember(localMember) });
    }
    else
    {
      formatSource(paramFormatter, paramInjectionPoint.getMember());
    }
  }
  
  private static abstract class Converter
  {
    final Class type;
    
    Converter(Class paramClass)
    {
      this.type = paramClass;
    }
    
    boolean appliesTo(Object paramObject)
    {
      return (paramObject != null) && (this.type.isAssignableFrom(paramObject.getClass()));
    }
    
    String convert(Object paramObject)
    {
      return toString(this.type.cast(paramObject));
    }
    
    abstract String toString(Object paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\Errors.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */