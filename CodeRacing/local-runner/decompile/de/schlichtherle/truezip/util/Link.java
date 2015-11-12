package de.schlichtherle.truezip.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public abstract interface Link
{
  public abstract Object getTarget();
  
  public static abstract enum Type
  {
    STRONG,  SOFT,  WEAK,  PHANTOM;
    
    public Link newLink(Object paramObject)
    {
      return newLink(paramObject, null);
    }
    
    abstract Link newLink(Object paramObject, ReferenceQueue paramReferenceQueue);
    
    private static final class Phantom
      extends PhantomReference
      implements Link
    {
      Phantom(Object paramObject, ReferenceQueue paramReferenceQueue)
      {
        super(paramReferenceQueue);
      }
      
      public Object getTarget()
      {
        return super.get();
      }
      
      public String toString()
      {
        return String.format("%s[target=%s]", new Object[] { getClass().getName(), getTarget() });
      }
    }
    
    private static final class Weak
      extends WeakReference
      implements Link
    {
      Weak(Object paramObject, ReferenceQueue paramReferenceQueue)
      {
        super(paramReferenceQueue);
      }
      
      public Object getTarget()
      {
        return super.get();
      }
      
      public String toString()
      {
        return String.format("%s[target=%s]", new Object[] { getClass().getName(), getTarget() });
      }
    }
    
    private static final class Soft
      extends SoftReference
      implements Link
    {
      Soft(Object paramObject, ReferenceQueue paramReferenceQueue)
      {
        super(paramReferenceQueue);
      }
      
      public Object getTarget()
      {
        return super.get();
      }
      
      public String toString()
      {
        return String.format("%s[target=%s]", new Object[] { getClass().getName(), getTarget() });
      }
    }
    
    private static final class Strong
      implements Link
    {
      private final Object target;
      
      Strong(Object paramObject)
      {
        this.target = paramObject;
      }
      
      public Object getTarget()
      {
        return this.target;
      }
      
      public String toString()
      {
        return String.format("%s[target=%s]", new Object[] { getClass().getName(), getTarget() });
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\Link.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */