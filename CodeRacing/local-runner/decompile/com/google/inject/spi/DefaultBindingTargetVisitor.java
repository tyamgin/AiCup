package com.google.inject.spi;

import com.google.inject.Binding;

public abstract class DefaultBindingTargetVisitor
  implements BindingTargetVisitor
{
  protected Object visitOther(Binding paramBinding)
  {
    return null;
  }
  
  public Object visit(InstanceBinding paramInstanceBinding)
  {
    return visitOther(paramInstanceBinding);
  }
  
  public Object visit(ProviderInstanceBinding paramProviderInstanceBinding)
  {
    return visitOther(paramProviderInstanceBinding);
  }
  
  public Object visit(ProviderKeyBinding paramProviderKeyBinding)
  {
    return visitOther(paramProviderKeyBinding);
  }
  
  public Object visit(LinkedKeyBinding paramLinkedKeyBinding)
  {
    return visitOther(paramLinkedKeyBinding);
  }
  
  public Object visit(ExposedBinding paramExposedBinding)
  {
    return visitOther(paramExposedBinding);
  }
  
  public Object visit(UntargettedBinding paramUntargettedBinding)
  {
    return visitOther(paramUntargettedBinding);
  }
  
  public Object visit(ConstructorBinding paramConstructorBinding)
  {
    return visitOther(paramConstructorBinding);
  }
  
  public Object visit(ConvertedConstantBinding paramConvertedConstantBinding)
  {
    return visitOther(paramConvertedConstantBinding);
  }
  
  public Object visit(ProviderBinding paramProviderBinding)
  {
    return visitOther(paramProviderBinding);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\DefaultBindingTargetVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */