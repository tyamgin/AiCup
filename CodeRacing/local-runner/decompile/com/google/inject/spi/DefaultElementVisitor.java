package com.google.inject.spi;

import com.google.inject.Binding;

public abstract class DefaultElementVisitor
  implements ElementVisitor
{
  protected Object visitOther(Element paramElement)
  {
    return null;
  }
  
  public Object visit(Message paramMessage)
  {
    return visitOther(paramMessage);
  }
  
  public Object visit(Binding paramBinding)
  {
    return visitOther(paramBinding);
  }
  
  public Object visit(InterceptorBinding paramInterceptorBinding)
  {
    return visitOther(paramInterceptorBinding);
  }
  
  public Object visit(ScopeBinding paramScopeBinding)
  {
    return visitOther(paramScopeBinding);
  }
  
  public Object visit(TypeConverterBinding paramTypeConverterBinding)
  {
    return visitOther(paramTypeConverterBinding);
  }
  
  public Object visit(ProviderLookup paramProviderLookup)
  {
    return visitOther(paramProviderLookup);
  }
  
  public Object visit(InjectionRequest paramInjectionRequest)
  {
    return visitOther(paramInjectionRequest);
  }
  
  public Object visit(StaticInjectionRequest paramStaticInjectionRequest)
  {
    return visitOther(paramStaticInjectionRequest);
  }
  
  public Object visit(PrivateElements paramPrivateElements)
  {
    return visitOther(paramPrivateElements);
  }
  
  public Object visit(MembersInjectorLookup paramMembersInjectorLookup)
  {
    return visitOther(paramMembersInjectorLookup);
  }
  
  public Object visit(TypeListenerBinding paramTypeListenerBinding)
  {
    return visitOther(paramTypeListenerBinding);
  }
  
  public Object visit(ProvisionListenerBinding paramProvisionListenerBinding)
  {
    return visitOther(paramProvisionListenerBinding);
  }
  
  public Object visit(DisableCircularProxiesOption paramDisableCircularProxiesOption)
  {
    return visitOther(paramDisableCircularProxiesOption);
  }
  
  public Object visit(RequireExplicitBindingsOption paramRequireExplicitBindingsOption)
  {
    return visitOther(paramRequireExplicitBindingsOption);
  }
  
  public Object visit(RequireAtInjectOnConstructorsOption paramRequireAtInjectOnConstructorsOption)
  {
    return visitOther(paramRequireAtInjectOnConstructorsOption);
  }
  
  public Object visit(RequireExactBindingAnnotationsOption paramRequireExactBindingAnnotationsOption)
  {
    return visitOther(paramRequireExactBindingAnnotationsOption);
  }
  
  public Object visit(ModuleAnnotatedMethodScannerBinding paramModuleAnnotatedMethodScannerBinding)
  {
    return visitOther(paramModuleAnnotatedMethodScannerBinding);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\DefaultElementVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */