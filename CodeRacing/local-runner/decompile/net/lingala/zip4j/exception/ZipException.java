package net.lingala.zip4j.exception;

public class ZipException
  extends Exception
{
  private int code = -1;
  
  public ZipException() {}
  
  public ZipException(String paramString)
  {
    super(paramString);
  }
  
  public ZipException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public ZipException(String paramString, int paramInt)
  {
    super(paramString);
    this.code = paramInt;
  }
  
  public ZipException(String paramString, Throwable paramThrowable, int paramInt)
  {
    super(paramString, paramThrowable);
    this.code = paramInt;
  }
  
  public ZipException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\exception\ZipException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */