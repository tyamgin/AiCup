package com.google.inject.spi;

import com.google.inject.Binding;
import java.util.List;

public abstract interface ProvisionListener
{
  public abstract void onProvision(ProvisionInvocation paramProvisionInvocation);
  
  public static abstract class ProvisionInvocation
  {
    public abstract Binding getBinding();
    
    public abstract Object provision();
    
    public abstract List getDependencyChain();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProvisionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */