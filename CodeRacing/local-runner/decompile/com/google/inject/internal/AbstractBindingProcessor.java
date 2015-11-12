package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.PrivateElements;
import java.util.Map;
import java.util.Set;

abstract class AbstractBindingProcessor
  extends AbstractProcessor
{
  private static final Set FORBIDDEN_TYPES = ImmutableSet.of(AbstractModule.class, Binder.class, Binding.class, Injector.class, Key.class, MembersInjector.class, new Class[] { Module.class, Provider.class, Scope.class, Stage.class, TypeLiteral.class });
  protected final ProcessedBindingData bindingData;
  
  AbstractBindingProcessor(Errors paramErrors, ProcessedBindingData paramProcessedBindingData)
  {
    super(paramErrors);
    this.bindingData = paramProcessedBindingData;
  }
  
  protected UntargettedBindingImpl invalidBinding(InjectorImpl paramInjectorImpl, Key paramKey, Object paramObject)
  {
    return new UntargettedBindingImpl(paramInjectorImpl, paramKey, paramObject);
  }
  
  protected void putBinding(BindingImpl paramBindingImpl)
  {
    Key localKey = paramBindingImpl.getKey();
    Class localClass = localKey.getTypeLiteral().getRawType();
    if (FORBIDDEN_TYPES.contains(localClass))
    {
      this.errors.cannotBindToGuiceType(localClass.getSimpleName());
      return;
    }
    BindingImpl localBindingImpl = this.injector.getExistingBinding(localKey);
    if (localBindingImpl != null)
    {
      if (this.injector.state.getExplicitBinding(localKey) != null) {
        try
        {
          if (!isOkayDuplicate(localBindingImpl, paramBindingImpl, this.injector.state))
          {
            this.errors.bindingAlreadySet(localKey, localBindingImpl.getSource());
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          this.errors.errorCheckingDuplicateBinding(localKey, localBindingImpl.getSource(), localThrowable);
          return;
        }
      }
      this.errors.jitBindingAlreadySet(localKey);
      return;
    }
    this.injector.state.parent().blacklist(localKey, this.injector.state, paramBindingImpl.getSource());
    this.injector.state.putBinding(localKey, paramBindingImpl);
  }
  
  private boolean isOkayDuplicate(BindingImpl paramBindingImpl1, BindingImpl paramBindingImpl2, State paramState)
  {
    if ((paramBindingImpl1 instanceof ExposedBindingImpl))
    {
      ExposedBindingImpl localExposedBindingImpl = (ExposedBindingImpl)paramBindingImpl1;
      InjectorImpl localInjectorImpl = (InjectorImpl)localExposedBindingImpl.getPrivateElements().getInjector();
      return localInjectorImpl == paramBindingImpl2.getInjector();
    }
    paramBindingImpl1 = (BindingImpl)paramState.getExplicitBindingsThisLevel().get(paramBindingImpl2.getKey());
    if (paramBindingImpl1 == null) {
      return false;
    }
    return paramBindingImpl1.equals(paramBindingImpl2);
  }
  
  private void validateKey(Object paramObject, Key paramKey)
  {
    Annotations.checkForMisplacedScopeAnnotations(paramKey.getTypeLiteral().getRawType(), paramObject, this.errors);
  }
  
  abstract class Processor
    extends DefaultBindingTargetVisitor
  {
    final Object source;
    final Key key;
    final Class rawType;
    Scoping scoping;
    
    Processor(BindingImpl paramBindingImpl)
    {
      this.source = paramBindingImpl.getSource();
      this.key = paramBindingImpl.getKey();
      this.rawType = this.key.getTypeLiteral().getRawType();
      this.scoping = paramBindingImpl.getScoping();
    }
    
    protected void prepareBinding()
    {
      AbstractBindingProcessor.this.validateKey(this.source, this.key);
      this.scoping = Scoping.makeInjectable(this.scoping, AbstractBindingProcessor.this.injector, AbstractBindingProcessor.this.errors);
    }
    
    protected void scheduleInitialization(final BindingImpl paramBindingImpl)
    {
      AbstractBindingProcessor.this.bindingData.addUninitializedBinding(new Runnable()
      {
        public void run()
        {
          try
          {
            paramBindingImpl.getInjector().initializeBinding(paramBindingImpl, AbstractBindingProcessor.this.errors.withSource(AbstractBindingProcessor.Processor.this.source));
          }
          catch (ErrorsException localErrorsException)
          {
            AbstractBindingProcessor.this.errors.merge(localErrorsException.getErrors());
          }
        }
      });
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\AbstractBindingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */