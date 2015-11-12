package net.lingala.zip4j.model;

public class Zip64EndCentralDirLocator
{
  private long signature;
  private int noOfDiskStartOfZip64EndOfCentralDirRec;
  private long offsetZip64EndOfCentralDirRec;
  private int totNumberOfDiscs;
  
  public void setSignature(long paramLong)
  {
    this.signature = paramLong;
  }
  
  public void setNoOfDiskStartOfZip64EndOfCentralDirRec(int paramInt)
  {
    this.noOfDiskStartOfZip64EndOfCentralDirRec = paramInt;
  }
  
  public long getOffsetZip64EndOfCentralDirRec()
  {
    return this.offsetZip64EndOfCentralDirRec;
  }
  
  public void setOffsetZip64EndOfCentralDirRec(long paramLong)
  {
    this.offsetZip64EndOfCentralDirRec = paramLong;
  }
  
  public void setTotNumberOfDiscs(int paramInt)
  {
    this.totNumberOfDiscs = paramInt;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\Zip64EndCentralDirLocator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */