package de.schlichtherle.truezip.socket;

import de.schlichtherle.truezip.io.InputException;
import de.schlichtherle.truezip.io.Streams;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class IOSocket
{
  public abstract Object getLocalTarget()
    throws IOException;
  
  public abstract Object getPeerTarget()
    throws IOException;
  
  public static void copy(InputSocket paramInputSocket, OutputSocket paramOutputSocket)
    throws IOException
  {
    if (null == paramOutputSocket) {
      throw new NullPointerException();
    }
    OutputStream localOutputStream = null;
    InputStream localInputStream;
    try
    {
      localInputStream = paramInputSocket.connect(paramOutputSocket).newInputStream();
    }
    catch (IOException localIOException1)
    {
      throw new InputException(localIOException1);
    }
    try
    {
      localOutputStream = paramOutputSocket.newOutputStream();
      if (null == localOutputStream) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException2)
        {
          throw new InputException(localIOException2);
        }
      }
      Streams.copy(localInputStream, localOutputStream);
    }
    finally
    {
      if (null == localOutputStream) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException3)
        {
          throw new InputException(localIOException3);
        }
      }
    }
    paramInputSocket.connect(null);
  }
  
  public String toString()
  {
    Object localObject1;
    try
    {
      localObject1 = getLocalTarget();
    }
    catch (IOException localIOException1)
    {
      localObject1 = localIOException1;
    }
    Object localObject2;
    try
    {
      localObject2 = getPeerTarget();
    }
    catch (IOException localIOException2)
    {
      localObject2 = localIOException2;
    }
    return String.format("%s[localTarget=%s, peerTarget=%s]", new Object[] { getClass().getName(), localObject1, localObject2 });
  }
  
  public final boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\socket\IOSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */