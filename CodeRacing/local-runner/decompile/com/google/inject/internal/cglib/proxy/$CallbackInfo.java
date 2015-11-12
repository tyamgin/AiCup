package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..Type;

class $CallbackInfo
{
  private Class cls;
  private .CallbackGenerator generator;
  private .Type type;
  private static final CallbackInfo[] CALLBACKS = { new CallbackInfo(.NoOp.class, .NoOpGenerator.INSTANCE), new CallbackInfo(.MethodInterceptor.class, .MethodInterceptorGenerator.INSTANCE), new CallbackInfo(.InvocationHandler.class, .InvocationHandlerGenerator.INSTANCE), new CallbackInfo(.LazyLoader.class, .LazyLoaderGenerator.INSTANCE), new CallbackInfo(.Dispatcher.class, .DispatcherGenerator.INSTANCE), new CallbackInfo(.FixedValue.class, .FixedValueGenerator.INSTANCE), new CallbackInfo(.ProxyRefDispatcher.class, .DispatcherGenerator.PROXY_REF_INSTANCE) };
  
  public static .Type[] determineTypes(Class[] paramArrayOfClass)
  {
    .Type[] arrayOfType = new .Type[paramArrayOfClass.length];
    for (int i = 0; i < arrayOfType.length; i++) {
      arrayOfType[i] = determineType(paramArrayOfClass[i]);
    }
    return arrayOfType;
  }
  
  public static .Type[] determineTypes(.Callback[] paramArrayOfCallback)
  {
    .Type[] arrayOfType = new .Type[paramArrayOfCallback.length];
    for (int i = 0; i < arrayOfType.length; i++) {
      arrayOfType[i] = determineType(paramArrayOfCallback[i]);
    }
    return arrayOfType;
  }
  
  public static .CallbackGenerator[] getGenerators(.Type[] paramArrayOfType)
  {
    .CallbackGenerator[] arrayOfCallbackGenerator = new .CallbackGenerator[paramArrayOfType.length];
    for (int i = 0; i < arrayOfCallbackGenerator.length; i++) {
      arrayOfCallbackGenerator[i] = getGenerator(paramArrayOfType[i]);
    }
    return arrayOfCallbackGenerator;
  }
  
  private $CallbackInfo(Class paramClass, .CallbackGenerator paramCallbackGenerator)
  {
    this.cls = paramClass;
    this.generator = paramCallbackGenerator;
    this.type = .Type.getType(paramClass);
  }
  
  private static .Type determineType(.Callback paramCallback)
  {
    if (paramCallback == null) {
      throw new IllegalStateException("Callback is null");
    }
    return determineType(paramCallback.getClass());
  }
  
  private static .Type determineType(Class paramClass)
  {
    Object localObject = null;
    for (int i = 0; i < CALLBACKS.length; i++)
    {
      CallbackInfo localCallbackInfo = CALLBACKS[i];
      if (localCallbackInfo.cls.isAssignableFrom(paramClass))
      {
        if (localObject != null) {
          throw new IllegalStateException("Callback implements both " + localObject + " and " + localCallbackInfo.cls);
        }
        localObject = localCallbackInfo.cls;
      }
    }
    if (localObject == null) {
      throw new IllegalStateException("Unknown callback type " + paramClass);
    }
    return .Type.getType((Class)localObject);
  }
  
  private static .CallbackGenerator getGenerator(.Type paramType)
  {
    for (int i = 0; i < CALLBACKS.length; i++)
    {
      CallbackInfo localCallbackInfo = CALLBACKS[i];
      if (localCallbackInfo.type.equals(paramType)) {
        return localCallbackInfo.generator;
      }
    }
    throw new IllegalStateException("Unknown callback type " + paramType);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$CallbackInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */