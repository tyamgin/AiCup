package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InstanceBinding;
import java.lang.annotation.Annotation;
import java.util.List;

public abstract class AbstractBindingBuilder
{
  public static final String IMPLEMENTATION_ALREADY_SET = "Implementation is set more than once.";
  public static final String SINGLE_INSTANCE_AND_SCOPE = "Setting the scope is not permitted when binding to a single instance.";
  public static final String SCOPE_ALREADY_SET = "Scope is set more than once.";
  public static final String BINDING_TO_NULL = "Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.";
  public static final String CONSTANT_VALUE_ALREADY_SET = "Constant value is set more than once.";
  public static final String ANNOTATION_ALREADY_SPECIFIED = "More than one annotation is specified for this binding.";
  protected static final Key NULL_KEY = Key.get(Void.class);
  protected List elements;
  protected int position;
  protected final Binder binder;
  private BindingImpl binding;
  
  public AbstractBindingBuilder(Binder paramBinder, List paramList, Object paramObject, Key paramKey)
  {
    this.binder = paramBinder;
    this.elements = paramList;
    this.position = paramList.size();
    this.binding = new UntargettedBindingImpl(paramObject, paramKey, Scoping.UNSCOPED);
    paramList.add(this.position, this.binding);
  }
  
  protected BindingImpl getBinding()
  {
    return this.binding;
  }
  
  protected BindingImpl setBinding(BindingImpl paramBindingImpl)
  {
    this.binding = paramBindingImpl;
    this.elements.set(this.position, paramBindingImpl);
    return paramBindingImpl;
  }
  
  protected BindingImpl annotatedWithInternal(Class paramClass)
  {
    Preconditions.checkNotNull(paramClass, "annotationType");
    checkNotAnnotated();
    return setBinding(this.binding.withKey(Key.get(this.binding.getKey().getTypeLiteral(), paramClass)));
  }
  
  protected BindingImpl annotatedWithInternal(Annotation paramAnnotation)
  {
    Preconditions.checkNotNull(paramAnnotation, "annotation");
    checkNotAnnotated();
    return setBinding(this.binding.withKey(Key.get(this.binding.getKey().getTypeLiteral(), paramAnnotation)));
  }
  
  public void in(Class paramClass)
  {
    Preconditions.checkNotNull(paramClass, "scopeAnnotation");
    checkNotScoped();
    setBinding(getBinding().withScoping(Scoping.forAnnotation(paramClass)));
  }
  
  public void in(Scope paramScope)
  {
    Preconditions.checkNotNull(paramScope, "scope");
    checkNotScoped();
    setBinding(getBinding().withScoping(Scoping.forInstance(paramScope)));
  }
  
  public void asEagerSingleton()
  {
    checkNotScoped();
    setBinding(getBinding().withScoping(Scoping.EAGER_SINGLETON));
  }
  
  protected boolean keyTypeIsSet()
  {
    return !Void.class.equals(this.binding.getKey().getTypeLiteral().getType());
  }
  
  protected void checkNotTargetted()
  {
    if (!(this.binding instanceof UntargettedBindingImpl)) {
      this.binder.addError("Implementation is set more than once.", new Object[0]);
    }
  }
  
  protected void checkNotAnnotated()
  {
    if (this.binding.getKey().getAnnotationType() != null) {
      this.binder.addError("More than one annotation is specified for this binding.", new Object[0]);
    }
  }
  
  protected void checkNotScoped()
  {
    if ((this.binding instanceof InstanceBinding))
    {
      this.binder.addError("Setting the scope is not permitted when binding to a single instance.", new Object[0]);
      return;
    }
    if (this.binding.getScoping().isExplicitlyScoped()) {
      this.binder.addError("Scope is set more than once.", new Object[0]);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\AbstractBindingBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */