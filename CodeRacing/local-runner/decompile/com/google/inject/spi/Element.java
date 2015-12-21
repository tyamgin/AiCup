package com.google.inject.spi;

import com.google.inject.Binder;

public abstract interface Element
{
  public abstract Object getSource();
  
  public abstract Object acceptVisitor(ElementVisitor paramElementVisitor);
  
  public abstract void applyTo(Binder paramBinder);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\Element.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */