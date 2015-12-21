package de.schlichtherle.truezip.fs;

final class FsSyncShutdownHook
{
  private static final Runtime RUNTIME = ;
  private static final Hook hook = new Hook();
  
  static void cancel()
  {
    Hook localHook = hook;
    if (localHook.manager != null) {
      synchronized (localHook)
      {
        if (localHook.manager != null)
        {
          RUNTIME.removeShutdownHook(localHook);
          localHook.manager = null;
        }
      }
    }
  }
  
  private static final class Hook
    extends Thread
  {
    volatile FsManager manager;
    
    Hook()
    {
      setPriority(10);
    }
    
    public void run()
    {
      FsManager localFsManager = this.manager;
      if (localFsManager != null)
      {
        this.manager = null;
        try
        {
          localFsManager.sync(FsSyncOptions.UMOUNT);
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsSyncShutdownHook.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */