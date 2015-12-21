package net.lingala.zip4j.model;

public class ExtraDataRecord
{
  private long header;
  private int sizeOfData;
  private byte[] data;
  
  public long getHeader()
  {
    return this.header;
  }
  
  public void setHeader(long paramLong)
  {
    this.header = paramLong;
  }
  
  public int getSizeOfData()
  {
    return this.sizeOfData;
  }
  
  public void setSizeOfData(int paramInt)
  {
    this.sizeOfData = paramInt;
  }
  
  public byte[] getData()
  {
    return this.data;
  }
  
  public void setData(byte[] paramArrayOfByte)
  {
    this.data = paramArrayOfByte;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\ExtraDataRecord.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */