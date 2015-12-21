package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.ConfigurationException;
import com.google.inject.Stage;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.StaticInjectionRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class InjectionRequestProcessor
  extends AbstractProcessor
{
  private final List staticInjections = Lists.newArrayList();
  private final Initializer initializer;
  
  InjectionRequestProcessor(Errors paramErrors, Initializer paramInitializer)
  {
    super(paramErrors);
    this.initializer = paramInitializer;
  }
  
  public Boolean visit(StaticInjectionRequest paramStaticInjectionRequest)
  {
    this.staticInjections.add(new StaticInjection(this.injector, paramStaticInjectionRequest));
    return Boolean.valueOf(true);
  }
  
  public Boolean visit(InjectionRequest paramInjectionRequest)
  {
    Set localSet;
    try
    {
      localSet = paramInjectionRequest.getInjectionPoints();
    }
    catch (ConfigurationException localConfigurationException)
    {
      this.errors.merge(localConfigurationException.getErrorMessages());
      localSet = (Set)localConfigurationException.getPartialValue();
    }
    this.initializer.requestInjection(this.injector, paramInjectionRequest.getInstance(), null, paramInjectionRequest.getSource(), localSet);
    return Boolean.valueOf(true);
  }
  
  void validate()
  {
    Iterator localIterator = this.staticInjections.iterator();
    while (localIterator.hasNext())
    {
      StaticInjection localStaticInjection = (StaticInjection)localIterator.next();
      localStaticInjection.validate();
    }
  }
  
  void injectMembers()
  {
    Iterator localIterator = this.staticInjections.iterator();
    while (localIterator.hasNext())
    {
      StaticInjection localStaticInjection = (StaticInjection)localIterator.next();
      localStaticInjection.injectMembers();
    }
  }
  
  private class StaticInjection
  {
    final InjectorImpl injector;
    final Object source;
    final StaticInjectionRequest request;
    ImmutableList memberInjectors;
    
    public StaticInjection(InjectorImpl paramInjectorImpl, StaticInjectionRequest paramStaticInjectionRequest)
    {
      this.injector = paramInjectorImpl;
      this.source = paramStaticInjectionRequest.getSource();
      this.request = paramStaticInjectionRequest;
    }
    
    void validate()
    {
      Errors localErrors = InjectionRequestProcessor.this.errors.withSource(this.source);
      Set localSet;
      try
      {
        localSet = this.request.getInjectionPoints();
      }
      catch (ConfigurationException localConfigurationException)
      {
        localErrors.merge(localConfigurationException.getErrorMessages());
        localSet = (Set)localConfigurationException.getPartialValue();
      }
      if (localSet != null) {
        this.memberInjectors = this.injector.membersInjectorStore.getInjectors(localSet, localErrors);
      } else {
        this.memberInjectors = ImmutableList.of();
      }
      InjectionRequestProcessor.this.errors.merge(localErrors);
    }
    
    void injectMembers()
    {
      try
      {
        this.injector.callInContext(new ContextualCallable()
        {
          public Void call(InternalContext paramAnonymousInternalContext)
          {
            Iterator localIterator = InjectionRequestProcessor.StaticInjection.this.memberInjectors.iterator();
            while (localIterator.hasNext())
            {
              SingleMemberInjector localSingleMemberInjector = (SingleMemberInjector)localIterator.next();
              if ((InjectionRequestProcessor.StaticInjection.this.injector.options.stage != Stage.TOOL) || (localSingleMemberInjector.getInjectionPoint().isToolable())) {
                localSingleMemberInjector.inject(InjectionRequestProcessor.this.errors, paramAnonymousInternalContext, null);
              }
            }
            return null;
          }
        });
      }
      catch (ErrorsException localErrorsException)
      {
        throw new AssertionError();
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InjectionRequestProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */