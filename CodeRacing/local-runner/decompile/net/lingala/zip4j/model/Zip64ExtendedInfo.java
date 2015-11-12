package net.lingala.zip4j.model;

public class Zip64ExtendedInfo
{
  private long compressedSize = -1L;
  private long unCompressedSize = -1L;
  private long offsetLocalHeader = -1L;
  private int diskNumberStart = -1;
  
  public long getCompressedSize()
  {
    return this.compressedSize;
  }
  
  public void setCompressedSize(long paramLong)
  {
    this.compressedSize = paramLong;
  }
  
  public long getUnCompressedSize()
  {
    return this.unCompressedSize;
  }
  
  public void setUnCompressedSize(long paramLong)
  {
    this.unCompressedSize = paramLong;
  }
  
  public long getOffsetLocalHeader()
  {
    return this.offsetLocalHeader;
  }
  
  public void setOffsetLocalHeader(long paramLong)
  {
    this.offsetLocalHeader = paramLong;
  }
  
  public int getDiskNumberStart()
  {
    return this.diskNumberStart;
  }
  
  public void setDiskNumberStart(int paramInt)
  {
    this.diskNumberStart = paramInt;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\Zip64ExtendedInfo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */