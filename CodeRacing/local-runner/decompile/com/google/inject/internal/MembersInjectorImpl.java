package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.InjectionPoint;
import java.util.Iterator;

final class MembersInjectorImpl
  implements MembersInjector
{
  private final TypeLiteral typeLiteral;
  private final InjectorImpl injector;
  private final ImmutableList memberInjectors;
  private final ImmutableSet userMembersInjectors;
  private final ImmutableSet injectionListeners;
  private final ImmutableList addedAspects;
  
  MembersInjectorImpl(InjectorImpl paramInjectorImpl, TypeLiteral paramTypeLiteral, EncounterImpl paramEncounterImpl, ImmutableList paramImmutableList)
  {
    this.injector = paramInjectorImpl;
    this.typeLiteral = paramTypeLiteral;
    this.memberInjectors = paramImmutableList;
    this.userMembersInjectors = paramEncounterImpl.getMembersInjectors();
    this.injectionListeners = paramEncounterImpl.getInjectionListeners();
    this.addedAspects = paramEncounterImpl.getAspects();
  }
  
  public ImmutableList getMemberInjectors()
  {
    return this.memberInjectors;
  }
  
  public void injectMembers(Object paramObject)
  {
    Errors localErrors = new Errors(this.typeLiteral);
    try
    {
      injectAndNotify(paramObject, localErrors, null, null, this.typeLiteral, false);
    }
    catch (ErrorsException localErrorsException)
    {
      localErrors.merge(localErrorsException.getErrors());
    }
    localErrors.throwProvisionExceptionIfErrorsExist();
  }
  
  void injectAndNotify(final Object paramObject1, final Errors paramErrors, final Key paramKey, final ProvisionListenerStackCallback paramProvisionListenerStackCallback, final Object paramObject2, final boolean paramBoolean)
    throws ErrorsException
  {
    if (paramObject1 == null) {
      return;
    }
    this.injector.callInContext(new ContextualCallable()
    {
      public Void call(final InternalContext paramAnonymousInternalContext)
        throws ErrorsException
      {
        paramAnonymousInternalContext.pushState(paramKey, paramObject2);
        try
        {
          if ((paramProvisionListenerStackCallback != null) && (paramProvisionListenerStackCallback.hasListeners())) {
            paramProvisionListenerStackCallback.provision(paramErrors, paramAnonymousInternalContext, new ProvisionListenerStackCallback.ProvisionCallback()
            {
              public Object call()
              {
                MembersInjectorImpl.this.injectMembers(MembersInjectorImpl.1.this.val$instance, MembersInjectorImpl.1.this.val$errors, paramAnonymousInternalContext, MembersInjectorImpl.1.this.val$toolableOnly);
                return MembersInjectorImpl.1.this.val$instance;
              }
            });
          } else {
            MembersInjectorImpl.this.injectMembers(paramObject1, paramErrors, paramAnonymousInternalContext, paramBoolean);
          }
        }
        finally
        {
          paramAnonymousInternalContext.popState();
        }
        return null;
      }
    });
    if (!paramBoolean) {
      notifyListeners(paramObject1, paramErrors);
    }
  }
  
  void notifyListeners(Object paramObject, Errors paramErrors)
    throws ErrorsException
  {
    int i = paramErrors.size();
    Iterator localIterator = this.injectionListeners.iterator();
    while (localIterator.hasNext())
    {
      InjectionListener localInjectionListener = (InjectionListener)localIterator.next();
      try
      {
        localInjectionListener.afterInjection(paramObject);
      }
      catch (RuntimeException localRuntimeException)
      {
        paramErrors.errorNotifyingInjectionListener(localInjectionListener, this.typeLiteral, localRuntimeException);
      }
    }
    paramErrors.throwIfNewErrors(i);
  }
  
  void injectMembers(Object paramObject, Errors paramErrors, InternalContext paramInternalContext, boolean paramBoolean)
  {
    int i = 0;
    int j = this.memberInjectors.size();
    while (i < j)
    {
      SingleMemberInjector localSingleMemberInjector = (SingleMemberInjector)this.memberInjectors.get(i);
      if ((!paramBoolean) || (localSingleMemberInjector.getInjectionPoint().isToolable())) {
        localSingleMemberInjector.inject(paramErrors, paramInternalContext, paramObject);
      }
      i++;
    }
    if (!paramBoolean)
    {
      Iterator localIterator = this.userMembersInjectors.iterator();
      while (localIterator.hasNext())
      {
        MembersInjector localMembersInjector = (MembersInjector)localIterator.next();
        try
        {
          localMembersInjector.injectMembers(paramObject);
        }
        catch (RuntimeException localRuntimeException)
        {
          paramErrors.errorInUserInjector(localMembersInjector, this.typeLiteral, localRuntimeException);
        }
      }
    }
  }
  
  public String toString()
  {
    String str = String.valueOf(String.valueOf(this.typeLiteral));
    return 17 + str.length() + "MembersInjector<" + str + ">";
  }
  
  public ImmutableSet getInjectionPoints()
  {
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    Iterator localIterator = this.memberInjectors.iterator();
    while (localIterator.hasNext())
    {
      SingleMemberInjector localSingleMemberInjector = (SingleMemberInjector)localIterator.next();
      localBuilder.add(localSingleMemberInjector.getInjectionPoint());
    }
    return localBuilder.build();
  }
  
  public ImmutableList getAddedAspects()
  {
    return this.addedAspects;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\MembersInjectorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */