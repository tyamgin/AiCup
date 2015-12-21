package de.schlichtherle.truezip.util;

public class ThreadGroups
{
  public static ThreadGroup getThreadGroup()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    return null != localSecurityManager ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
  }
  
  public static ThreadGroup getServerThreadGroup()
  {
    ThreadGroup localThreadGroup;
    for (Object localObject = getThreadGroup(); null != (localThreadGroup = ((ThreadGroup)localObject).getParent()); localObject = localThreadGroup) {
      try
      {
        localThreadGroup.checkAccess();
      }
      catch (SecurityException localSecurityException)
      {
        break;
      }
    }
    return (ThreadGroup)localObject;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\ThreadGroups.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */