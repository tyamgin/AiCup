package net.lingala.zip4j.io;

import java.io.IOException;
import java.io.InputStream;
import net.lingala.zip4j.unzip.UnzipEngine;

public abstract class BaseInputStream
  extends InputStream
{
  public int read()
    throws IOException
  {
    return 0;
  }
  
  public int available()
    throws IOException
  {
    return 0;
  }
  
  public UnzipEngine getUnzipEngine()
  {
    return null;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\io\BaseInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */