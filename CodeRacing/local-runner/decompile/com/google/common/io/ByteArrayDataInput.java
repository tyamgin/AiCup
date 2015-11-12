package com.google.common.io;

import java.io.DataInput;

public abstract interface ByteArrayDataInput
  extends DataInput
{
  public abstract void readFully(byte[] paramArrayOfByte);
  
  public abstract void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract int skipBytes(int paramInt);
  
  public abstract boolean readBoolean();
  
  public abstract byte readByte();
  
  public abstract int readUnsignedByte();
  
  public abstract short readShort();
  
  public abstract int readUnsignedShort();
  
  public abstract char readChar();
  
  public abstract int readInt();
  
  public abstract long readLong();
  
  public abstract float readFloat();
  
  public abstract double readDouble();
  
  public abstract String readLine();
  
  public abstract String readUTF();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\ByteArrayDataInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */