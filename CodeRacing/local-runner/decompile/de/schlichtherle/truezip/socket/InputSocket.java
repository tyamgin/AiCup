package de.schlichtherle.truezip.socket;

import de.schlichtherle.truezip.entry.Entry;
import de.schlichtherle.truezip.rof.ReadOnlyFile;
import de.schlichtherle.truezip.rof.ReadOnlyFileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class InputSocket
  extends IOSocket
{
  private OutputSocket peer;
  
  public final Entry getPeerTarget()
    throws IOException
  {
    return null == this.peer ? null : (Entry)this.peer.getLocalTarget();
  }
  
  final InputSocket connect(OutputSocket paramOutputSocket)
  {
    OutputSocket localOutputSocket = this.peer;
    if (localOutputSocket != paramOutputSocket)
    {
      if (null != localOutputSocket)
      {
        this.peer = null;
        localOutputSocket.connect(null);
      }
      if (null != paramOutputSocket)
      {
        this.peer = paramOutputSocket;
        paramOutputSocket.connect(this);
      }
    }
    return this;
  }
  
  public abstract ReadOnlyFile newReadOnlyFile()
    throws IOException;
  
  public InputStream newInputStream()
    throws IOException
  {
    return new ReadOnlyFileInputStream(newReadOnlyFile());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\socket\InputSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */