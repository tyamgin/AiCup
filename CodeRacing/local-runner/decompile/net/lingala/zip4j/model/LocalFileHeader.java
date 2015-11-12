package net.lingala.zip4j.model;

import java.util.ArrayList;

public class LocalFileHeader
{
  private int signature;
  private int versionNeededToExtract;
  private byte[] generalPurposeFlag;
  private int compressionMethod;
  private int lastModFileTime;
  private long crc32 = 0L;
  private byte[] crcBuff;
  private long compressedSize;
  private long uncompressedSize = 0L;
  private int fileNameLength;
  private int extraFieldLength;
  private String fileName;
  private long offsetStartOfData;
  private boolean isEncrypted;
  private int encryptionMethod = -1;
  private char[] password;
  private ArrayList extraDataRecords;
  private Zip64ExtendedInfo zip64ExtendedInfo;
  private AESExtraDataRecord aesExtraDataRecord;
  private boolean dataDescriptorExists;
  private boolean writeComprSizeInZip64ExtraRecord = false;
  private boolean fileNameUTF8Encoded;
  
  public void setSignature(int paramInt)
  {
    this.signature = paramInt;
  }
  
  public void setVersionNeededToExtract(int paramInt)
  {
    this.versionNeededToExtract = paramInt;
  }
  
  public void setGeneralPurposeFlag(byte[] paramArrayOfByte)
  {
    this.generalPurposeFlag = paramArrayOfByte;
  }
  
  public int getCompressionMethod()
  {
    return this.compressionMethod;
  }
  
  public void setCompressionMethod(int paramInt)
  {
    this.compressionMethod = paramInt;
  }
  
  public void setLastModFileTime(int paramInt)
  {
    this.lastModFileTime = paramInt;
  }
  
  public long getCrc32()
  {
    return this.crc32;
  }
  
  public void setCrc32(long paramLong)
  {
    this.crc32 = paramLong;
  }
  
  public long getCompressedSize()
  {
    return this.compressedSize;
  }
  
  public void setCompressedSize(long paramLong)
  {
    this.compressedSize = paramLong;
  }
  
  public long getUncompressedSize()
  {
    return this.uncompressedSize;
  }
  
  public void setUncompressedSize(long paramLong)
  {
    this.uncompressedSize = paramLong;
  }
  
  public void setFileNameLength(int paramInt)
  {
    this.fileNameLength = paramInt;
  }
  
  public int getExtraFieldLength()
  {
    return this.extraFieldLength;
  }
  
  public void setExtraFieldLength(int paramInt)
  {
    this.extraFieldLength = paramInt;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
  
  public void setFileName(String paramString)
  {
    this.fileName = paramString;
  }
  
  public long getOffsetStartOfData()
  {
    return this.offsetStartOfData;
  }
  
  public void setOffsetStartOfData(long paramLong)
  {
    this.offsetStartOfData = paramLong;
  }
  
  public boolean isEncrypted()
  {
    return this.isEncrypted;
  }
  
  public void setEncrypted(boolean paramBoolean)
  {
    this.isEncrypted = paramBoolean;
  }
  
  public int getEncryptionMethod()
  {
    return this.encryptionMethod;
  }
  
  public void setEncryptionMethod(int paramInt)
  {
    this.encryptionMethod = paramInt;
  }
  
  public void setCrcBuff(byte[] paramArrayOfByte)
  {
    this.crcBuff = paramArrayOfByte;
  }
  
  public char[] getPassword()
  {
    return this.password;
  }
  
  public void setPassword(char[] paramArrayOfChar)
  {
    this.password = paramArrayOfChar;
  }
  
  public ArrayList getExtraDataRecords()
  {
    return this.extraDataRecords;
  }
  
  public void setExtraDataRecords(ArrayList paramArrayList)
  {
    this.extraDataRecords = paramArrayList;
  }
  
  public void setDataDescriptorExists(boolean paramBoolean)
  {
    this.dataDescriptorExists = paramBoolean;
  }
  
  public void setZip64ExtendedInfo(Zip64ExtendedInfo paramZip64ExtendedInfo)
  {
    this.zip64ExtendedInfo = paramZip64ExtendedInfo;
  }
  
  public AESExtraDataRecord getAesExtraDataRecord()
  {
    return this.aesExtraDataRecord;
  }
  
  public void setAesExtraDataRecord(AESExtraDataRecord paramAESExtraDataRecord)
  {
    this.aesExtraDataRecord = paramAESExtraDataRecord;
  }
  
  public boolean isFileNameUTF8Encoded()
  {
    return this.fileNameUTF8Encoded;
  }
  
  public void setFileNameUTF8Encoded(boolean paramBoolean)
  {
    this.fileNameUTF8Encoded = paramBoolean;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\LocalFileHeader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */