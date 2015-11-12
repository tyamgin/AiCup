package com.google.inject.spi;

public abstract interface ProvidesMethodTargetVisitor
  extends BindingTargetVisitor
{
  public abstract Object visit(ProvidesMethodBinding paramProvidesMethodBinding);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProvidesMethodTargetVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */