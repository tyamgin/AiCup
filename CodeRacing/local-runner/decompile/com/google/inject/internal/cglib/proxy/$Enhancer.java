package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..AbstractClassGenerator;
import com.google.inject.internal.cglib.core..AbstractClassGenerator.Source;
import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..CodeGenerationException;
import com.google.inject.internal.cglib.core..CollectionUtils;
import com.google.inject.internal.cglib.core..Constants;
import com.google.inject.internal.cglib.core..DuplicatesPredicate;
import com.google.inject.internal.cglib.core..EmitUtils;
import com.google.inject.internal.cglib.core..KeyFactory;
import com.google.inject.internal.cglib.core..Local;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..MethodInfoTransformer;
import com.google.inject.internal.cglib.core..MethodWrapper;
import com.google.inject.internal.cglib.core..ObjectSwitchCallback;
import com.google.inject.internal.cglib.core..ProcessSwitchCallback;
import com.google.inject.internal.cglib.core..ReflectUtils;
import com.google.inject.internal.cglib.core..RejectModifierPredicate;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.core..Transformer;
import com.google.inject.internal.cglib.core..TypeUtils;
import com.google.inject.internal.cglib.core..VisibilityPredicate;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class $Enhancer
  extends .AbstractClassGenerator
{
  private static final .CallbackFilter ALL_ZERO = new .CallbackFilter()
  {
    public int accept(Method paramAnonymousMethod)
    {
      return 0;
    }
  };
  private static final .AbstractClassGenerator.Source SOURCE = new .AbstractClassGenerator.Source(Enhancer.class.getName());
  private static final EnhancerKey KEY_FACTORY = (EnhancerKey).KeyFactory.create(EnhancerKey.class);
  private static final String BOUND_FIELD = "CGLIB$BOUND";
  private static final String THREAD_CALLBACKS_FIELD = "CGLIB$THREAD_CALLBACKS";
  private static final String STATIC_CALLBACKS_FIELD = "CGLIB$STATIC_CALLBACKS";
  private static final String SET_THREAD_CALLBACKS_NAME = "CGLIB$SET_THREAD_CALLBACKS";
  private static final String SET_STATIC_CALLBACKS_NAME = "CGLIB$SET_STATIC_CALLBACKS";
  private static final String CONSTRUCTED_FIELD = "CGLIB$CONSTRUCTED";
  private static final .Type FACTORY = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$Factory");
  private static final .Type ILLEGAL_STATE_EXCEPTION = .TypeUtils.parseType("IllegalStateException");
  private static final .Type ILLEGAL_ARGUMENT_EXCEPTION = .TypeUtils.parseType("IllegalArgumentException");
  private static final .Type THREAD_LOCAL = .TypeUtils.parseType("ThreadLocal");
  private static final .Type CALLBACK = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$Callback");
  private static final .Type CALLBACK_ARRAY = .Type.getType(new .Callback[0].getClass());
  private static final .Signature CSTRUCT_NULL = .TypeUtils.parseConstructor("");
  private static final .Signature SET_THREAD_CALLBACKS = new .Signature("CGLIB$SET_THREAD_CALLBACKS", .Type.VOID_TYPE, new .Type[] { CALLBACK_ARRAY });
  private static final .Signature SET_STATIC_CALLBACKS = new .Signature("CGLIB$SET_STATIC_CALLBACKS", .Type.VOID_TYPE, new .Type[] { CALLBACK_ARRAY });
  private static final .Signature NEW_INSTANCE = new .Signature("newInstance", .Constants.TYPE_OBJECT, new .Type[] { CALLBACK_ARRAY });
  private static final .Signature MULTIARG_NEW_INSTANCE = new .Signature("newInstance", .Constants.TYPE_OBJECT, new .Type[] { .Constants.TYPE_CLASS_ARRAY, .Constants.TYPE_OBJECT_ARRAY, CALLBACK_ARRAY });
  private static final .Signature SINGLE_NEW_INSTANCE = new .Signature("newInstance", .Constants.TYPE_OBJECT, new .Type[] { CALLBACK });
  private static final .Signature SET_CALLBACK = new .Signature("setCallback", .Type.VOID_TYPE, new .Type[] { .Type.INT_TYPE, CALLBACK });
  private static final .Signature GET_CALLBACK = new .Signature("getCallback", CALLBACK, new .Type[] { .Type.INT_TYPE });
  private static final .Signature SET_CALLBACKS = new .Signature("setCallbacks", .Type.VOID_TYPE, new .Type[] { CALLBACK_ARRAY });
  private static final .Signature GET_CALLBACKS = new .Signature("getCallbacks", CALLBACK_ARRAY, new .Type[0]);
  private static final .Signature THREAD_LOCAL_GET = .TypeUtils.parseSignature("Object get()");
  private static final .Signature THREAD_LOCAL_SET = .TypeUtils.parseSignature("void set(Object)");
  private static final .Signature BIND_CALLBACKS = .TypeUtils.parseSignature("void CGLIB$BIND_CALLBACKS(Object)");
  private Class[] interfaces;
  private .CallbackFilter filter;
  private .Callback[] callbacks;
  private .Type[] callbackTypes;
  private boolean classOnly;
  private Class superclass;
  private Class[] argumentTypes;
  private Object[] arguments;
  private boolean useFactory = true;
  private Long serialVersionUID;
  private boolean interceptDuringConstruction = true;
  
  public $Enhancer()
  {
    super(SOURCE);
  }
  
  public void setSuperclass(Class paramClass)
  {
    if ((paramClass != null) && (paramClass.isInterface())) {
      setInterfaces(new Class[] { paramClass });
    } else if ((paramClass != null) && (paramClass.equals(Object.class))) {
      this.superclass = null;
    } else {
      this.superclass = paramClass;
    }
  }
  
  public void setInterfaces(Class[] paramArrayOfClass)
  {
    this.interfaces = paramArrayOfClass;
  }
  
  public void setCallbackFilter(.CallbackFilter paramCallbackFilter)
  {
    this.filter = paramCallbackFilter;
  }
  
  public void setCallback(.Callback paramCallback)
  {
    setCallbacks(new .Callback[] { paramCallback });
  }
  
  public void setCallbacks(.Callback[] paramArrayOfCallback)
  {
    if ((paramArrayOfCallback != null) && (paramArrayOfCallback.length == 0)) {
      throw new IllegalArgumentException("Array cannot be empty");
    }
    this.callbacks = paramArrayOfCallback;
  }
  
  public void setUseFactory(boolean paramBoolean)
  {
    this.useFactory = paramBoolean;
  }
  
  public void setInterceptDuringConstruction(boolean paramBoolean)
  {
    this.interceptDuringConstruction = paramBoolean;
  }
  
  public void setCallbackType(Class paramClass)
  {
    setCallbackTypes(new Class[] { paramClass });
  }
  
  public void setCallbackTypes(Class[] paramArrayOfClass)
  {
    if ((paramArrayOfClass != null) && (paramArrayOfClass.length == 0)) {
      throw new IllegalArgumentException("Array cannot be empty");
    }
    this.callbackTypes = .CallbackInfo.determineTypes(paramArrayOfClass);
  }
  
  public Object create()
  {
    this.classOnly = false;
    this.argumentTypes = null;
    return createHelper();
  }
  
  public Object create(Class[] paramArrayOfClass, Object[] paramArrayOfObject)
  {
    this.classOnly = false;
    if ((paramArrayOfClass == null) || (paramArrayOfObject == null) || (paramArrayOfClass.length != paramArrayOfObject.length)) {
      throw new IllegalArgumentException("Arguments must be non-null and of equal length");
    }
    this.argumentTypes = paramArrayOfClass;
    this.arguments = paramArrayOfObject;
    return createHelper();
  }
  
  public Class createClass()
  {
    this.classOnly = true;
    return (Class)createHelper();
  }
  
  public void setSerialVersionUID(Long paramLong)
  {
    this.serialVersionUID = paramLong;
  }
  
  private void validate()
  {
    if ((this.classOnly ^ this.callbacks == null))
    {
      if (this.classOnly) {
        throw new IllegalStateException("createClass does not accept callbacks");
      }
      throw new IllegalStateException("Callbacks are required");
    }
    if ((this.classOnly) && (this.callbackTypes == null)) {
      throw new IllegalStateException("Callback types are required");
    }
    if ((this.callbacks != null) && (this.callbackTypes != null))
    {
      if (this.callbacks.length != this.callbackTypes.length) {
        throw new IllegalStateException("Lengths of callback and callback types array must be the same");
      }
      .Type[] arrayOfType = .CallbackInfo.determineTypes(this.callbacks);
      for (int j = 0; j < arrayOfType.length; j++) {
        if (!arrayOfType[j].equals(this.callbackTypes[j])) {
          throw new IllegalStateException("Callback " + arrayOfType[j] + " is not assignable to " + this.callbackTypes[j]);
        }
      }
    }
    else if (this.callbacks != null)
    {
      this.callbackTypes = .CallbackInfo.determineTypes(this.callbacks);
    }
    if (this.filter == null)
    {
      if (this.callbackTypes.length > 1) {
        throw new IllegalStateException("Multiple callback types possible but no filter specified");
      }
      this.filter = ALL_ZERO;
    }
    if (this.interfaces != null) {
      for (int i = 0; i < this.interfaces.length; i++)
      {
        if (this.interfaces[i] == null) {
          throw new IllegalStateException("Interfaces cannot be null");
        }
        if (!this.interfaces[i].isInterface()) {
          throw new IllegalStateException(this.interfaces[i] + " is not an interface");
        }
      }
    }
  }
  
  private Object createHelper()
  {
    validate();
    if (this.superclass != null) {
      setNamePrefix(this.superclass.getName());
    } else if (this.interfaces != null) {
      setNamePrefix(this.interfaces[.ReflectUtils.findPackageProtected(this.interfaces)].getName());
    }
    return super.create(KEY_FACTORY.newInstance(this.superclass != null ? this.superclass.getName() : null, .ReflectUtils.getNames(this.interfaces), this.filter, this.callbackTypes, this.useFactory, this.interceptDuringConstruction, this.serialVersionUID));
  }
  
  protected ClassLoader getDefaultClassLoader()
  {
    if (this.superclass != null) {
      return this.superclass.getClassLoader();
    }
    if (this.interfaces != null) {
      return this.interfaces[0].getClassLoader();
    }
    return null;
  }
  
  private .Signature rename(.Signature paramSignature, int paramInt)
  {
    return new .Signature("CGLIB$" + paramSignature.getName() + "$" + paramInt, paramSignature.getDescriptor());
  }
  
  public static void getMethods(Class paramClass, Class[] paramArrayOfClass, List paramList)
  {
    getMethods(paramClass, paramArrayOfClass, paramList, null, null);
  }
  
  private static void getMethods(Class paramClass, Class[] paramArrayOfClass, List paramList1, List paramList2, Set paramSet)
  {
    .ReflectUtils.addAllMethods(paramClass, paramList1);
    List localList = paramList2 != null ? paramList2 : paramList1;
    if (paramArrayOfClass != null) {
      for (int i = 0; i < paramArrayOfClass.length; i++) {
        if (paramArrayOfClass[i] != .Factory.class) {
          .ReflectUtils.addAllMethods(paramArrayOfClass[i], localList);
        }
      }
    }
    if (paramList2 != null)
    {
      if (paramSet != null) {
        paramSet.addAll(.MethodWrapper.createSet(paramList2));
      }
      paramList1.addAll(paramList2);
    }
    .CollectionUtils.filter(paramList1, new .RejectModifierPredicate(8));
    .CollectionUtils.filter(paramList1, new .VisibilityPredicate(paramClass, true));
    .CollectionUtils.filter(paramList1, new .DuplicatesPredicate());
    .CollectionUtils.filter(paramList1, new .RejectModifierPredicate(16));
  }
  
  public void generateClass(.ClassVisitor paramClassVisitor)
    throws Exception
  {
    Class localClass = this.superclass == null ? Object.class : this.superclass;
    if (.TypeUtils.isFinal(localClass.getModifiers())) {
      throw new IllegalArgumentException("Cannot subclass final class " + localClass);
    }
    ArrayList localArrayList1 = new ArrayList(Arrays.asList(localClass.getDeclaredConstructors()));
    filterConstructors(localClass, localArrayList1);
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    HashSet localHashSet = new HashSet();
    getMethods(localClass, this.interfaces, localArrayList2, localArrayList3, localHashSet);
    List localList1 = .CollectionUtils.transform(localArrayList2, new .Transformer()
    {
      private final Set val$forcePublic;
      
      public Object transform(Object paramAnonymousObject)
      {
        Method localMethod = (Method)paramAnonymousObject;
        int i = 0x10 | localMethod.getModifiers() & 0xFBFF & 0xFEFF & 0xFFFFFFDF;
        if (this.val$forcePublic.contains(.MethodWrapper.create(localMethod))) {
          i = i & 0xFFFFFFFB | 0x1;
        }
        return .ReflectUtils.getMethodInfo(localMethod, i);
      }
    });
    .ClassEmitter localClassEmitter = new .ClassEmitter(paramClassVisitor);
    localClassEmitter.begin_class(46, 1, getClassName(), .Type.getType(localClass), this.useFactory ? .TypeUtils.add(.TypeUtils.getTypes(this.interfaces), FACTORY) : .TypeUtils.getTypes(this.interfaces), "<generated>");
    List localList2 = .CollectionUtils.transform(localArrayList1, .MethodInfoTransformer.getInstance());
    localClassEmitter.declare_field(2, "CGLIB$BOUND", .Type.BOOLEAN_TYPE, null);
    if (!this.interceptDuringConstruction) {
      localClassEmitter.declare_field(2, "CGLIB$CONSTRUCTED", .Type.BOOLEAN_TYPE, null);
    }
    localClassEmitter.declare_field(26, "CGLIB$THREAD_CALLBACKS", THREAD_LOCAL, null);
    localClassEmitter.declare_field(26, "CGLIB$STATIC_CALLBACKS", CALLBACK_ARRAY, null);
    if (this.serialVersionUID != null) {
      localClassEmitter.declare_field(26, "serialVersionUID", .Type.LONG_TYPE, this.serialVersionUID);
    }
    for (int i = 0; i < this.callbackTypes.length; i++) {
      localClassEmitter.declare_field(2, getCallbackField(i), this.callbackTypes[i], null);
    }
    emitMethods(localClassEmitter, localList1, localArrayList2);
    emitConstructors(localClassEmitter, localList2);
    emitSetThreadCallbacks(localClassEmitter);
    emitSetStaticCallbacks(localClassEmitter);
    emitBindCallbacks(localClassEmitter);
    if (this.useFactory)
    {
      int[] arrayOfInt = getCallbackKeys();
      emitNewInstanceCallbacks(localClassEmitter);
      emitNewInstanceCallback(localClassEmitter);
      emitNewInstanceMultiarg(localClassEmitter, localList2);
      emitGetCallback(localClassEmitter, arrayOfInt);
      emitSetCallback(localClassEmitter, arrayOfInt);
      emitGetCallbacks(localClassEmitter);
      emitSetCallbacks(localClassEmitter);
    }
    localClassEmitter.end_class();
  }
  
  protected void filterConstructors(Class paramClass, List paramList)
  {
    .CollectionUtils.filter(paramList, new .VisibilityPredicate(paramClass, true));
    if (paramList.size() == 0) {
      throw new IllegalArgumentException("No visible constructors in " + paramClass);
    }
  }
  
  protected Object firstInstance(Class paramClass)
    throws Exception
  {
    if (this.classOnly) {
      return paramClass;
    }
    return createUsingReflection(paramClass);
  }
  
  protected Object nextInstance(Object paramObject)
  {
    Class localClass = (paramObject instanceof Class) ? (Class)paramObject : paramObject.getClass();
    if (this.classOnly) {
      return localClass;
    }
    if ((paramObject instanceof .Factory))
    {
      if (this.argumentTypes != null) {
        return ((.Factory)paramObject).newInstance(this.argumentTypes, this.arguments, this.callbacks);
      }
      return ((.Factory)paramObject).newInstance(this.callbacks);
    }
    return createUsingReflection(localClass);
  }
  
  public static void registerCallbacks(Class paramClass, .Callback[] paramArrayOfCallback)
  {
    setThreadCallbacks(paramClass, paramArrayOfCallback);
  }
  
  public static void registerStaticCallbacks(Class paramClass, .Callback[] paramArrayOfCallback)
  {
    setCallbacksHelper(paramClass, paramArrayOfCallback, "CGLIB$SET_STATIC_CALLBACKS");
  }
  
  public static boolean isEnhanced(Class paramClass)
  {
    try
    {
      getCallbacksSetter(paramClass, "CGLIB$SET_THREAD_CALLBACKS");
      return true;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return false;
  }
  
  private static void setThreadCallbacks(Class paramClass, .Callback[] paramArrayOfCallback)
  {
    setCallbacksHelper(paramClass, paramArrayOfCallback, "CGLIB$SET_THREAD_CALLBACKS");
  }
  
  private static void setCallbacksHelper(Class paramClass, .Callback[] paramArrayOfCallback, String paramString)
  {
    try
    {
      Method localMethod = getCallbacksSetter(paramClass, paramString);
      localMethod.invoke(null, new Object[] { paramArrayOfCallback });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new IllegalArgumentException(paramClass + " is not an enhanced class");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new .CodeGenerationException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new .CodeGenerationException(localInvocationTargetException);
    }
  }
  
  private static Method getCallbacksSetter(Class paramClass, String paramString)
    throws NoSuchMethodException
  {
    return paramClass.getDeclaredMethod(paramString, new Class[] { new .Callback[0].getClass() });
  }
  
  private Object createUsingReflection(Class paramClass)
  {
    setThreadCallbacks(paramClass, this.callbacks);
    try
    {
      if (this.argumentTypes != null)
      {
        localObject1 = .ReflectUtils.newInstance(paramClass, this.argumentTypes, this.arguments);
        return localObject1;
      }
      Object localObject1 = .ReflectUtils.newInstance(paramClass);
      return localObject1;
    }
    finally
    {
      setThreadCallbacks(paramClass, null);
    }
  }
  
  public static Object create(Class paramClass, .Callback paramCallback)
  {
    Enhancer localEnhancer = new Enhancer();
    localEnhancer.setSuperclass(paramClass);
    localEnhancer.setCallback(paramCallback);
    return localEnhancer.create();
  }
  
  public static Object create(Class paramClass, Class[] paramArrayOfClass, .Callback paramCallback)
  {
    Enhancer localEnhancer = new Enhancer();
    localEnhancer.setSuperclass(paramClass);
    localEnhancer.setInterfaces(paramArrayOfClass);
    localEnhancer.setCallback(paramCallback);
    return localEnhancer.create();
  }
  
  public static Object create(Class paramClass, Class[] paramArrayOfClass, .CallbackFilter paramCallbackFilter, .Callback[] paramArrayOfCallback)
  {
    Enhancer localEnhancer = new Enhancer();
    localEnhancer.setSuperclass(paramClass);
    localEnhancer.setInterfaces(paramArrayOfClass);
    localEnhancer.setCallbackFilter(paramCallbackFilter);
    localEnhancer.setCallbacks(paramArrayOfCallback);
    return localEnhancer.create();
  }
  
  private void emitConstructors(.ClassEmitter paramClassEmitter, List paramList)
  {
    int i = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      .CodeEmitter localCodeEmitter = .EmitUtils.begin_method(paramClassEmitter, localMethodInfo, 1);
      localCodeEmitter.load_this();
      localCodeEmitter.dup();
      localCodeEmitter.load_args();
      .Signature localSignature = localMethodInfo.getSignature();
      i = (i != 0) || (localSignature.getDescriptor().equals("()V")) ? 1 : 0;
      localCodeEmitter.super_invoke_constructor(localSignature);
      localCodeEmitter.invoke_static_this(BIND_CALLBACKS);
      if (!this.interceptDuringConstruction)
      {
        localCodeEmitter.load_this();
        localCodeEmitter.push(1);
        localCodeEmitter.putfield("CGLIB$CONSTRUCTED");
      }
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
    }
    if ((!this.classOnly) && (i == 0) && (this.arguments == null)) {
      throw new IllegalArgumentException("Superclass has no null constructors but no arguments were given");
    }
  }
  
  private int[] getCallbackKeys()
  {
    int[] arrayOfInt = new int[this.callbackTypes.length];
    for (int i = 0; i < this.callbackTypes.length; i++) {
      arrayOfInt[i] = i;
    }
    return arrayOfInt;
  }
  
  private void emitGetCallback(.ClassEmitter paramClassEmitter, int[] paramArrayOfInt)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, GET_CALLBACK, null);
    localCodeEmitter.load_this();
    localCodeEmitter.invoke_static_this(BIND_CALLBACKS);
    localCodeEmitter.load_this();
    localCodeEmitter.load_arg(0);
    localCodeEmitter.process_switch(paramArrayOfInt, new .ProcessSwitchCallback()
    {
      private final .CodeEmitter val$e;
      
      public void processCase(int paramAnonymousInt, .Label paramAnonymousLabel)
      {
        this.val$e.getfield(.Enhancer.getCallbackField(paramAnonymousInt));
        this.val$e.goTo(paramAnonymousLabel);
      }
      
      public void processDefault()
      {
        this.val$e.pop();
        this.val$e.aconst_null();
      }
    });
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitSetCallback(.ClassEmitter paramClassEmitter, int[] paramArrayOfInt)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, SET_CALLBACK, null);
    localCodeEmitter.load_arg(0);
    localCodeEmitter.process_switch(paramArrayOfInt, new .ProcessSwitchCallback()
    {
      private final .CodeEmitter val$e;
      
      public void processCase(int paramAnonymousInt, .Label paramAnonymousLabel)
      {
        this.val$e.load_this();
        this.val$e.load_arg(1);
        this.val$e.checkcast(.Enhancer.this.callbackTypes[paramAnonymousInt]);
        this.val$e.putfield(.Enhancer.getCallbackField(paramAnonymousInt));
        this.val$e.goTo(paramAnonymousLabel);
      }
      
      public void processDefault() {}
    });
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitSetCallbacks(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, SET_CALLBACKS, null);
    localCodeEmitter.load_this();
    localCodeEmitter.load_arg(0);
    for (int i = 0; i < this.callbackTypes.length; i++)
    {
      localCodeEmitter.dup2();
      localCodeEmitter.aaload(i);
      localCodeEmitter.checkcast(this.callbackTypes[i]);
      localCodeEmitter.putfield(getCallbackField(i));
    }
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitGetCallbacks(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, GET_CALLBACKS, null);
    localCodeEmitter.load_this();
    localCodeEmitter.invoke_static_this(BIND_CALLBACKS);
    localCodeEmitter.load_this();
    localCodeEmitter.push(this.callbackTypes.length);
    localCodeEmitter.newarray(CALLBACK);
    for (int i = 0; i < this.callbackTypes.length; i++)
    {
      localCodeEmitter.dup();
      localCodeEmitter.push(i);
      localCodeEmitter.load_this();
      localCodeEmitter.getfield(getCallbackField(i));
      localCodeEmitter.aastore();
    }
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitNewInstanceCallbacks(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, NEW_INSTANCE, null);
    localCodeEmitter.load_arg(0);
    localCodeEmitter.invoke_static_this(SET_THREAD_CALLBACKS);
    emitCommonNewInstance(localCodeEmitter);
  }
  
  private void emitCommonNewInstance(.CodeEmitter paramCodeEmitter)
  {
    paramCodeEmitter.new_instance_this();
    paramCodeEmitter.dup();
    paramCodeEmitter.invoke_constructor_this();
    paramCodeEmitter.aconst_null();
    paramCodeEmitter.invoke_static_this(SET_THREAD_CALLBACKS);
    paramCodeEmitter.return_value();
    paramCodeEmitter.end_method();
  }
  
  private void emitNewInstanceCallback(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, SINGLE_NEW_INSTANCE, null);
    switch (this.callbackTypes.length)
    {
    case 0: 
      break;
    case 1: 
      localCodeEmitter.push(1);
      localCodeEmitter.newarray(CALLBACK);
      localCodeEmitter.dup();
      localCodeEmitter.push(0);
      localCodeEmitter.load_arg(0);
      localCodeEmitter.aastore();
      localCodeEmitter.invoke_static_this(SET_THREAD_CALLBACKS);
      break;
    default: 
      localCodeEmitter.throw_exception(ILLEGAL_STATE_EXCEPTION, "More than one callback object required");
    }
    emitCommonNewInstance(localCodeEmitter);
  }
  
  private void emitNewInstanceMultiarg(.ClassEmitter paramClassEmitter, List paramList)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, MULTIARG_NEW_INSTANCE, null);
    localCodeEmitter.load_arg(2);
    localCodeEmitter.invoke_static_this(SET_THREAD_CALLBACKS);
    localCodeEmitter.new_instance_this();
    localCodeEmitter.dup();
    localCodeEmitter.load_arg(0);
    .EmitUtils.constructor_switch(localCodeEmitter, paramList, new .ObjectSwitchCallback()
    {
      private final .CodeEmitter val$e;
      
      public void processCase(Object paramAnonymousObject, .Label paramAnonymousLabel)
      {
        .MethodInfo localMethodInfo = (.MethodInfo)paramAnonymousObject;
        .Type[] arrayOfType = localMethodInfo.getSignature().getArgumentTypes();
        for (int i = 0; i < arrayOfType.length; i++)
        {
          this.val$e.load_arg(1);
          this.val$e.push(i);
          this.val$e.aaload();
          this.val$e.unbox(arrayOfType[i]);
        }
        this.val$e.invoke_constructor_this(localMethodInfo.getSignature());
        this.val$e.goTo(paramAnonymousLabel);
      }
      
      public void processDefault()
      {
        this.val$e.throw_exception(.Enhancer.ILLEGAL_ARGUMENT_EXCEPTION, "Constructor not found");
      }
    });
    localCodeEmitter.aconst_null();
    localCodeEmitter.invoke_static_this(SET_THREAD_CALLBACKS);
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitMethods(.ClassEmitter paramClassEmitter, List paramList1, List paramList2)
  {
    .CallbackGenerator[] arrayOfCallbackGenerator = .CallbackInfo.getGenerators(this.callbackTypes);
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    HashMap localHashMap3 = new HashMap();
    Map localMap = .CollectionUtils.getIndexMap(paramList1);
    HashMap localHashMap4 = new HashMap();
    Iterator localIterator = paramList1.iterator();
    Object localObject1 = paramList2 != null ? paramList2.iterator() : null;
    while (localIterator.hasNext())
    {
      localObject2 = (.MethodInfo)localIterator.next();
      localObject3 = localObject1 != null ? (Method)((Iterator)localObject1).next() : null;
      int i = this.filter.accept((Method)localObject3);
      if (i >= this.callbackTypes.length) {
        throw new IllegalArgumentException("Callback filter returned an index that is too large: " + i);
      }
      localHashMap3.put(localObject2, new Integer(localObject3 != null ? ((Method)localObject3).getModifiers() : ((.MethodInfo)localObject2).getModifiers()));
      localHashMap2.put(localObject2, new Integer(i));
      localObject4 = (List)localHashMap1.get(arrayOfCallbackGenerator[i]);
      if (localObject4 == null) {
        localHashMap1.put(arrayOfCallbackGenerator[i], localObject4 = new ArrayList(paramList1.size()));
      }
      ((List)localObject4).add(localObject2);
      if (.TypeUtils.isBridge(((Method)localObject3).getModifiers()))
      {
        localObject5 = (Set)localHashMap4.get(((Method)localObject3).getDeclaringClass());
        if (localObject5 == null)
        {
          localObject5 = new HashSet();
          localHashMap4.put(((Method)localObject3).getDeclaringClass(), localObject5);
        }
        ((Set)localObject5).add(((.MethodInfo)localObject2).getSignature());
      }
    }
    Object localObject2 = new .BridgeMethodResolver(localHashMap4).resolveAll();
    Object localObject3 = new HashSet();
    .CodeEmitter localCodeEmitter = paramClassEmitter.getStaticHook();
    localCodeEmitter.new_instance(THREAD_LOCAL);
    localCodeEmitter.dup();
    localCodeEmitter.invoke_constructor(THREAD_LOCAL, CSTRUCT_NULL);
    localCodeEmitter.putfield("CGLIB$THREAD_CALLBACKS");
    Object localObject4 = new Object[1];
    Object localObject5 = new .CallbackGenerator.Context()
    {
      private final Map val$originalModifiers;
      private final Map val$indexes;
      private final Map val$positions;
      private final Map val$bridgeToTarget;
      
      public ClassLoader getClassLoader()
      {
        return .Enhancer.this.getClassLoader();
      }
      
      public int getOriginalModifiers(.MethodInfo paramAnonymousMethodInfo)
      {
        return ((Integer)this.val$originalModifiers.get(paramAnonymousMethodInfo)).intValue();
      }
      
      public int getIndex(.MethodInfo paramAnonymousMethodInfo)
      {
        return ((Integer)this.val$indexes.get(paramAnonymousMethodInfo)).intValue();
      }
      
      public void emitCallback(.CodeEmitter paramAnonymousCodeEmitter, int paramAnonymousInt)
      {
        .Enhancer.this.emitCurrentCallback(paramAnonymousCodeEmitter, paramAnonymousInt);
      }
      
      public .Signature getImplSignature(.MethodInfo paramAnonymousMethodInfo)
      {
        return .Enhancer.this.rename(paramAnonymousMethodInfo.getSignature(), ((Integer)this.val$positions.get(paramAnonymousMethodInfo)).intValue());
      }
      
      public void emitInvoke(.CodeEmitter paramAnonymousCodeEmitter, .MethodInfo paramAnonymousMethodInfo)
      {
        .Signature localSignature = (.Signature)this.val$bridgeToTarget.get(paramAnonymousMethodInfo.getSignature());
        if (localSignature != null)
        {
          paramAnonymousCodeEmitter.invoke_virtual_this(localSignature);
          .Type localType = paramAnonymousMethodInfo.getSignature().getReturnType();
          if (!localType.equals(localSignature.getReturnType())) {
            paramAnonymousCodeEmitter.checkcast(localType);
          }
        }
        else
        {
          paramAnonymousCodeEmitter.super_invoke(paramAnonymousMethodInfo.getSignature());
        }
      }
      
      public .CodeEmitter beginMethod(.ClassEmitter paramAnonymousClassEmitter, .MethodInfo paramAnonymousMethodInfo)
      {
        .CodeEmitter localCodeEmitter = .EmitUtils.begin_method(paramAnonymousClassEmitter, paramAnonymousMethodInfo);
        if ((!.Enhancer.this.interceptDuringConstruction) && (!.TypeUtils.isAbstract(paramAnonymousMethodInfo.getModifiers())))
        {
          .Label localLabel = localCodeEmitter.make_label();
          localCodeEmitter.load_this();
          localCodeEmitter.getfield("CGLIB$CONSTRUCTED");
          localCodeEmitter.if_jump(154, localLabel);
          localCodeEmitter.load_this();
          localCodeEmitter.load_args();
          localCodeEmitter.super_invoke();
          localCodeEmitter.return_value();
          localCodeEmitter.mark(localLabel);
        }
        return localCodeEmitter;
      }
    };
    for (int j = 0; j < this.callbackTypes.length; j++)
    {
      .CallbackGenerator localCallbackGenerator = arrayOfCallbackGenerator[j];
      if (!((Set)localObject3).contains(localCallbackGenerator))
      {
        ((Set)localObject3).add(localCallbackGenerator);
        List localList = (List)localHashMap1.get(localCallbackGenerator);
        if (localList != null) {
          try
          {
            localCallbackGenerator.generate(paramClassEmitter, (.CallbackGenerator.Context)localObject5, localList);
            localCallbackGenerator.generateStatic(localCodeEmitter, (.CallbackGenerator.Context)localObject5, localList);
          }
          catch (RuntimeException localRuntimeException)
          {
            throw localRuntimeException;
          }
          catch (Exception localException)
          {
            throw new .CodeGenerationException(localException);
          }
        }
      }
    }
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitSetThreadCallbacks(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(9, SET_THREAD_CALLBACKS, null);
    localCodeEmitter.getfield("CGLIB$THREAD_CALLBACKS");
    localCodeEmitter.load_arg(0);
    localCodeEmitter.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_SET);
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitSetStaticCallbacks(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(9, SET_STATIC_CALLBACKS, null);
    localCodeEmitter.load_arg(0);
    localCodeEmitter.putfield("CGLIB$STATIC_CALLBACKS");
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private void emitCurrentCallback(.CodeEmitter paramCodeEmitter, int paramInt)
  {
    paramCodeEmitter.load_this();
    paramCodeEmitter.getfield(getCallbackField(paramInt));
    paramCodeEmitter.dup();
    .Label localLabel = paramCodeEmitter.make_label();
    paramCodeEmitter.ifnonnull(localLabel);
    paramCodeEmitter.pop();
    paramCodeEmitter.load_this();
    paramCodeEmitter.invoke_static_this(BIND_CALLBACKS);
    paramCodeEmitter.load_this();
    paramCodeEmitter.getfield(getCallbackField(paramInt));
    paramCodeEmitter.mark(localLabel);
  }
  
  private void emitBindCallbacks(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(26, BIND_CALLBACKS, null);
    .Local localLocal = localCodeEmitter.make_local();
    localCodeEmitter.load_arg(0);
    localCodeEmitter.checkcast_this();
    localCodeEmitter.store_local(localLocal);
    .Label localLabel1 = localCodeEmitter.make_label();
    localCodeEmitter.load_local(localLocal);
    localCodeEmitter.getfield("CGLIB$BOUND");
    localCodeEmitter.if_jump(154, localLabel1);
    localCodeEmitter.load_local(localLocal);
    localCodeEmitter.push(1);
    localCodeEmitter.putfield("CGLIB$BOUND");
    localCodeEmitter.getfield("CGLIB$THREAD_CALLBACKS");
    localCodeEmitter.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_GET);
    localCodeEmitter.dup();
    .Label localLabel2 = localCodeEmitter.make_label();
    localCodeEmitter.ifnonnull(localLabel2);
    localCodeEmitter.pop();
    localCodeEmitter.getfield("CGLIB$STATIC_CALLBACKS");
    localCodeEmitter.dup();
    localCodeEmitter.ifnonnull(localLabel2);
    localCodeEmitter.pop();
    localCodeEmitter.goTo(localLabel1);
    localCodeEmitter.mark(localLabel2);
    localCodeEmitter.checkcast(CALLBACK_ARRAY);
    localCodeEmitter.load_local(localLocal);
    localCodeEmitter.swap();
    for (int i = this.callbackTypes.length - 1; i >= 0; i--)
    {
      if (i != 0) {
        localCodeEmitter.dup2();
      }
      localCodeEmitter.aaload(i);
      localCodeEmitter.checkcast(this.callbackTypes[i]);
      localCodeEmitter.putfield(getCallbackField(i));
    }
    localCodeEmitter.mark(localLabel1);
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  private static String getCallbackField(int paramInt)
  {
    return "CGLIB$CALLBACK_" + paramInt;
  }
  
  public static abstract interface EnhancerKey
  {
    public abstract Object newInstance(String paramString, String[] paramArrayOfString, .CallbackFilter paramCallbackFilter, .Type[] paramArrayOfType, boolean paramBoolean1, boolean paramBoolean2, Long paramLong);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$Enhancer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */