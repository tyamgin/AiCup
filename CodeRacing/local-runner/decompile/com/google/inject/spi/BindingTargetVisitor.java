package com.google.inject.spi;

public abstract interface BindingTargetVisitor
{
  public abstract Object visit(InstanceBinding paramInstanceBinding);
  
  public abstract Object visit(ProviderInstanceBinding paramProviderInstanceBinding);
  
  public abstract Object visit(ProviderKeyBinding paramProviderKeyBinding);
  
  public abstract Object visit(LinkedKeyBinding paramLinkedKeyBinding);
  
  public abstract Object visit(ExposedBinding paramExposedBinding);
  
  public abstract Object visit(UntargettedBinding paramUntargettedBinding);
  
  public abstract Object visit(ConstructorBinding paramConstructorBinding);
  
  public abstract Object visit(ConvertedConstantBinding paramConvertedConstantBinding);
  
  public abstract Object visit(ProviderBinding paramProviderBinding);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\BindingTargetVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */