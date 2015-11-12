package com.google.inject;

import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Element;

public abstract interface Binding
  extends Element
{
  public abstract Key getKey();
  
  public abstract Provider getProvider();
  
  public abstract Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor);
  
  public abstract Object acceptScopingVisitor(BindingScopingVisitor paramBindingScopingVisitor);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\Binding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */