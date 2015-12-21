package com.google.inject.internal;

import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Field;
import java.util.List;

final class SingleFieldInjector
  implements SingleMemberInjector
{
  final Field field;
  final InjectionPoint injectionPoint;
  final Dependency dependency;
  final BindingImpl binding;
  
  public SingleFieldInjector(InjectorImpl paramInjectorImpl, InjectionPoint paramInjectionPoint, Errors paramErrors)
    throws ErrorsException
  {
    this.injectionPoint = paramInjectionPoint;
    this.field = ((Field)paramInjectionPoint.getMember());
    this.dependency = ((Dependency)paramInjectionPoint.getDependencies().get(0));
    this.field.setAccessible(true);
    this.binding = paramInjectorImpl.getBindingOrThrow(this.dependency.getKey(), paramErrors, InjectorImpl.JitLimitation.NO_JIT);
  }
  
  public InjectionPoint getInjectionPoint()
  {
    return this.injectionPoint;
  }
  
  public void inject(Errors paramErrors, InternalContext paramInternalContext, Object paramObject)
  {
    paramErrors = paramErrors.withSource(this.dependency);
    Dependency localDependency = paramInternalContext.pushDependency(this.dependency, this.binding.getSource());
    try
    {
      Object localObject1 = this.binding.getInternalFactory().get(paramErrors, paramInternalContext, this.dependency, false);
      this.field.set(paramObject, localObject1);
    }
    catch (ErrorsException localErrorsException)
    {
      paramErrors.withSource(this.injectionPoint).merge(localErrorsException.getErrors());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new AssertionError(localIllegalAccessException);
    }
    finally
    {
      paramInternalContext.popStateAndSetDependency(localDependency);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\SingleFieldInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */