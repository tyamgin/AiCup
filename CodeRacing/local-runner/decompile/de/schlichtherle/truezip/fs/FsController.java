package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.entry.Entry;
import de.schlichtherle.truezip.entry.Entry.Type;
import de.schlichtherle.truezip.socket.InputSocket;
import de.schlichtherle.truezip.socket.OutputSocket;
import de.schlichtherle.truezip.util.BitField;
import java.io.IOException;

public abstract class FsController
{
  public abstract FsModel getModel();
  
  public abstract FsEntry getEntry(FsEntryName paramFsEntryName)
    throws IOException;
  
  public abstract boolean isReadable(FsEntryName paramFsEntryName)
    throws IOException;
  
  public abstract boolean isWritable(FsEntryName paramFsEntryName)
    throws IOException;
  
  public boolean isExecutable(FsEntryName paramFsEntryName)
    throws IOException
  {
    return false;
  }
  
  public abstract void setReadOnly(FsEntryName paramFsEntryName)
    throws IOException;
  
  public abstract boolean setTime(FsEntryName paramFsEntryName, BitField paramBitField1, long paramLong, BitField paramBitField2)
    throws IOException;
  
  public abstract InputSocket getInputSocket(FsEntryName paramFsEntryName, BitField paramBitField);
  
  public abstract OutputSocket getOutputSocket(FsEntryName paramFsEntryName, BitField paramBitField, Entry paramEntry);
  
  public abstract void mknod(FsEntryName paramFsEntryName, Entry.Type paramType, BitField paramBitField, Entry paramEntry)
    throws IOException;
  
  public abstract void unlink(FsEntryName paramFsEntryName, BitField paramBitField)
    throws IOException;
  
  public abstract void sync(BitField paramBitField)
    throws FsSyncWarningException, FsSyncException;
  
  public final boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString()
  {
    return String.format("%s[model=%s]", new Object[] { getClass().getName(), getModel() });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */