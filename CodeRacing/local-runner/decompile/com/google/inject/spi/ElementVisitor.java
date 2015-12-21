package com.google.inject.spi;

import com.google.inject.Binding;

public abstract interface ElementVisitor
{
  public abstract Object visit(Binding paramBinding);
  
  public abstract Object visit(InterceptorBinding paramInterceptorBinding);
  
  public abstract Object visit(ScopeBinding paramScopeBinding);
  
  public abstract Object visit(TypeConverterBinding paramTypeConverterBinding);
  
  public abstract Object visit(InjectionRequest paramInjectionRequest);
  
  public abstract Object visit(StaticInjectionRequest paramStaticInjectionRequest);
  
  public abstract Object visit(ProviderLookup paramProviderLookup);
  
  public abstract Object visit(MembersInjectorLookup paramMembersInjectorLookup);
  
  public abstract Object visit(Message paramMessage);
  
  public abstract Object visit(PrivateElements paramPrivateElements);
  
  public abstract Object visit(TypeListenerBinding paramTypeListenerBinding);
  
  public abstract Object visit(ProvisionListenerBinding paramProvisionListenerBinding);
  
  public abstract Object visit(RequireExplicitBindingsOption paramRequireExplicitBindingsOption);
  
  public abstract Object visit(DisableCircularProxiesOption paramDisableCircularProxiesOption);
  
  public abstract Object visit(RequireAtInjectOnConstructorsOption paramRequireAtInjectOnConstructorsOption);
  
  public abstract Object visit(RequireExactBindingAnnotationsOption paramRequireExactBindingAnnotationsOption);
  
  public abstract Object visit(ModuleAnnotatedMethodScannerBinding paramModuleAnnotatedMethodScannerBinding);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ElementVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */