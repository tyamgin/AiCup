package net.lingala.zip4j.model;

public class AESExtraDataRecord
{
  private long signature = -1L;
  private int dataSize = -1;
  private int versionNumber = -1;
  private String vendorID = null;
  private int aesStrength = -1;
  private int compressionMethod = -1;
  
  public void setSignature(long paramLong)
  {
    this.signature = paramLong;
  }
  
  public void setDataSize(int paramInt)
  {
    this.dataSize = paramInt;
  }
  
  public void setVersionNumber(int paramInt)
  {
    this.versionNumber = paramInt;
  }
  
  public void setVendorID(String paramString)
  {
    this.vendorID = paramString;
  }
  
  public int getAesStrength()
  {
    return this.aesStrength;
  }
  
  public void setAesStrength(int paramInt)
  {
    this.aesStrength = paramInt;
  }
  
  public int getCompressionMethod()
  {
    return this.compressionMethod;
  }
  
  public void setCompressionMethod(int paramInt)
  {
    this.compressionMethod = paramInt;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\AESExtraDataRecord.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */