package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Stage;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.RequireAtInjectOnConstructorsOption;
import com.google.inject.spi.RequireExactBindingAnnotationsOption;
import com.google.inject.spi.RequireExplicitBindingsOption;

class InjectorOptionsProcessor
  extends AbstractProcessor
{
  private boolean disableCircularProxies = false;
  private boolean jitDisabled = false;
  private boolean atInjectRequired = false;
  private boolean exactBindingAnnotationsRequired = false;
  
  InjectorOptionsProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  public Boolean visit(DisableCircularProxiesOption paramDisableCircularProxiesOption)
  {
    this.disableCircularProxies = true;
    return Boolean.valueOf(true);
  }
  
  public Boolean visit(RequireExplicitBindingsOption paramRequireExplicitBindingsOption)
  {
    this.jitDisabled = true;
    return Boolean.valueOf(true);
  }
  
  public Boolean visit(RequireAtInjectOnConstructorsOption paramRequireAtInjectOnConstructorsOption)
  {
    this.atInjectRequired = true;
    return Boolean.valueOf(true);
  }
  
  public Boolean visit(RequireExactBindingAnnotationsOption paramRequireExactBindingAnnotationsOption)
  {
    this.exactBindingAnnotationsRequired = true;
    return Boolean.valueOf(true);
  }
  
  InjectorImpl.InjectorOptions getOptions(Stage paramStage, InjectorImpl.InjectorOptions paramInjectorOptions)
  {
    Preconditions.checkNotNull(paramStage, "stage must be set");
    if (paramInjectorOptions == null) {
      return new InjectorImpl.InjectorOptions(paramStage, this.jitDisabled, this.disableCircularProxies, this.atInjectRequired, this.exactBindingAnnotationsRequired);
    }
    Preconditions.checkState(paramStage == paramInjectorOptions.stage, "child & parent stage don't match");
    return new InjectorImpl.InjectorOptions(paramStage, (this.jitDisabled) || (paramInjectorOptions.jitDisabled), (this.disableCircularProxies) || (paramInjectorOptions.disableCircularProxies), (this.atInjectRequired) || (paramInjectorOptions.atInjectRequired), (this.exactBindingAnnotationsRequired) || (paramInjectorOptions.exactBindingAnnotationsRequired));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InjectorOptionsProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */