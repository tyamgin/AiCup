package de.schlichtherle.truezip.file;

import de.schlichtherle.truezip.io.DecoratingInputStream;
import de.schlichtherle.truezip.socket.InputSocket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class TFileInputStream
  extends DecoratingInputStream
{
  public TFileInputStream(File paramFile)
    throws FileNotFoundException
  {
    super(newInputStream(paramFile));
  }
  
  private static InputStream newInputStream(File paramFile)
    throws FileNotFoundException
  {
    InputSocket localInputSocket = TBIO.getInputSocket(paramFile, TConfig.get().getInputPreferences());
    try
    {
      return localInputSocket.newInputStream();
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw localFileNotFoundException;
    }
    catch (IOException localIOException)
    {
      throw ((FileNotFoundException)new FileNotFoundException(paramFile.toString()).initCause(localIOException));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\file\TFileInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */