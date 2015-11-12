package net.lingala.zip4j.model;

public class Zip64EndCentralDirRecord
{
  private long signature;
  private long sizeOfZip64EndCentralDirRec;
  private int versionMadeBy;
  private int versionNeededToExtract;
  private int noOfThisDisk;
  private int noOfThisDiskStartOfCentralDir;
  private long totNoOfEntriesInCentralDirOnThisDisk;
  private long totNoOfEntriesInCentralDir;
  private long sizeOfCentralDir;
  private long offsetStartCenDirWRTStartDiskNo;
  private byte[] extensibleDataSector;
  
  public void setSignature(long paramLong)
  {
    this.signature = paramLong;
  }
  
  public long getSizeOfZip64EndCentralDirRec()
  {
    return this.sizeOfZip64EndCentralDirRec;
  }
  
  public void setSizeOfZip64EndCentralDirRec(long paramLong)
  {
    this.sizeOfZip64EndCentralDirRec = paramLong;
  }
  
  public void setVersionMadeBy(int paramInt)
  {
    this.versionMadeBy = paramInt;
  }
  
  public void setVersionNeededToExtract(int paramInt)
  {
    this.versionNeededToExtract = paramInt;
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
  
  public void setTotNoOfEntriesInCentralDirOnThisDisk(long paramLong)
  {
    this.totNoOfEntriesInCentralDirOnThisDisk = paramLong;
  }
  
  public long getTotNoOfEntriesInCentralDir()
  {
    return this.totNoOfEntriesInCentralDir;
  }
  
  public void setTotNoOfEntriesInCentralDir(long paramLong)
  {
    this.totNoOfEntriesInCentralDir = paramLong;
  }
  
  public void setSizeOfCentralDir(long paramLong)
  {
    this.sizeOfCentralDir = paramLong;
  }
  
  public long getOffsetStartCenDirWRTStartDiskNo()
  {
    return this.offsetStartCenDirWRTStartDiskNo;
  }
  
  public void setOffsetStartCenDirWRTStartDiskNo(long paramLong)
  {
    this.offsetStartCenDirWRTStartDiskNo = paramLong;
  }
  
  public void setExtensibleDataSector(byte[] paramArrayOfByte)
  {
    this.extensibleDataSector = paramArrayOfByte;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\Zip64EndCentralDirRecord.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */