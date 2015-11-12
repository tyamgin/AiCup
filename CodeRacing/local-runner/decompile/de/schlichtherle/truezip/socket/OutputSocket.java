package de.schlichtherle.truezip.socket;

import de.schlichtherle.truezip.entry.Entry;
import java.io.IOException;
import java.io.OutputStream;

public abstract class OutputSocket
  extends IOSocket
{
  private InputSocket peer;
  
  public final Entry getPeerTarget()
    throws IOException
  {
    return null == this.peer ? null : (Entry)this.peer.getLocalTarget();
  }
  
  final OutputSocket connect(InputSocket paramInputSocket)
  {
    InputSocket localInputSocket = this.peer;
    if (localInputSocket != paramInputSocket)
    {
      if (null != localInputSocket)
      {
        this.peer = null;
        localInputSocket.connect(null);
      }
      if (null != paramInputSocket)
      {
        this.peer = paramInputSocket;
        paramInputSocket.connect(this);
      }
    }
    return this;
  }
  
  public abstract OutputStream newOutputStream()
    throws IOException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\socket\OutputSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */