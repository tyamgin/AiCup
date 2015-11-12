package net.lingala.zip4j.model;

public class EndCentralDirRecord
{
  private long signature;
  private int noOfThisDisk;
  private int noOfThisDiskStartOfCentralDir;
  private int totNoOfEntriesInCentralDirOnThisDisk;
  private int totNoOfEntriesInCentralDir;
  private int sizeOfCentralDir;
  private long offsetOfStartOfCentralDir;
  private int commentLength;
  private String comment;
  private byte[] commentBytes;
  
  public void setSignature(long paramLong)
  {
    this.signature = paramLong;
  }
  
  public int getNoOfThisDisk()
  {
    return this.noOfThisDisk;
  }
  
  public void setNoOfThisDisk(int paramInt)
  {
    this.noOfThisDisk = paramInt;
  }
  
  public void setNoOfThisDiskStartOfCentralDir(int paramInt)
  {
    this.noOfThisDiskStartOfCentralDir = paramInt;
  }
  
  public void setTotNoOfEntriesInCentralDirOnThisDisk(int paramInt)
  {
    this.totNoOfEntriesInCentralDirOnThisDisk = paramInt;
  }
  
  public int getTotNoOfEntriesInCentralDir()
  {
    return this.totNoOfEntriesInCentralDir;
  }
  
  public void setTotNoOfEntriesInCentralDir(int paramInt)
  {
    this.totNoOfEntriesInCentralDir = paramInt;
  }
  
  public void setSizeOfCentralDir(int paramInt)
  {
    this.sizeOfCentralDir = paramInt;
  }
  
  public long getOffsetOfStartOfCentralDir()
  {
    return this.offsetOfStartOfCentralDir;
  }
  
  public void setOffsetOfStartOfCentralDir(long paramLong)
  {
    this.offsetOfStartOfCentralDir = paramLong;
  }
  
  public void setCommentLength(int paramInt)
  {
    this.commentLength = paramInt;
  }
  
  public void setComment(String paramString)
  {
    this.comment = paramString;
  }
  
  public void setCommentBytes(byte[] paramArrayOfByte)
  {
    this.commentBytes = paramArrayOfByte;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\EndCentralDirRecord.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */