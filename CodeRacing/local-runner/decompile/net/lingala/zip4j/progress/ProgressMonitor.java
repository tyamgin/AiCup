package net.lingala.zip4j.progress;

import net.lingala.zip4j.exception.ZipException;

public class ProgressMonitor
{
  private int state;
  private long totalWork;
  private long workCompleted;
  private int percentDone;
  private int currentOperation;
  private String fileName;
  private int result;
  private Throwable exception;
  private boolean cancelAllTasks;
  private boolean pause;
  
  public ProgressMonitor()
  {
    reset();
    this.percentDone = 0;
  }
  
  public int getState()
  {
    return this.state;
  }
  
  public void setState(int paramInt)
  {
    this.state = paramInt;
  }
  
  public void setTotalWork(long paramLong)
  {
    this.totalWork = paramLong;
  }
  
  public void updateWorkCompleted(long paramLong)
  {
    this.workCompleted += paramLong;
    if (this.totalWork > 0L)
    {
      this.percentDone = ((int)(this.workCompleted * 100L / this.totalWork));
      if (this.percentDone > 100) {
        this.percentDone = 100;
      }
    }
    while (this.pause) {
      try
      {
        Thread.sleep(150L);
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public void setPercentDone(int paramInt)
  {
    this.percentDone = paramInt;
  }
  
  public void setResult(int paramInt)
  {
    this.result = paramInt;
  }
  
  public void setFileName(String paramString)
  {
    this.fileName = paramString;
  }
  
  public void setCurrentOperation(int paramInt)
  {
    this.currentOperation = paramInt;
  }
  
  public void endProgressMonitorSuccess()
    throws ZipException
  {
    reset();
    this.result = 0;
  }
  
  public void endProgressMonitorError(Throwable paramThrowable)
    throws ZipException
  {
    reset();
    this.result = 2;
    this.exception = paramThrowable;
  }
  
  public void reset()
  {
    this.currentOperation = -1;
    this.state = 0;
    this.fileName = null;
    this.totalWork = 0L;
    this.workCompleted = 0L;
    this.percentDone = 0;
  }
  
  public boolean isCancelAllTasks()
  {
    return this.cancelAllTasks;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\progress\ProgressMonitor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */