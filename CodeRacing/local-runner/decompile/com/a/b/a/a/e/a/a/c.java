package com.a.b.a.a.e.a.a;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

final class c
  implements Runnable
{
  c(InputStream paramInputStream, File paramFile, Process paramProcess) {}
  
  public void run()
  {
    try
    {
      FileUtils.copyInputStreamToFile(this.a, this.b);
    }
    catch (IOException localIOException) {}
    a.c().debug("Completed to write stream from " + this.c + " to '" + this.b + "'.");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\a\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */