package com.google.inject.spi;

import com.google.inject.Provider;

public abstract interface ProviderWithExtensionVisitor
  extends Provider
{
  public abstract Object acceptExtensionVisitor(BindingTargetVisitor paramBindingTargetVisitor, ProviderInstanceBinding paramProviderInstanceBinding);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProviderWithExtensionVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */