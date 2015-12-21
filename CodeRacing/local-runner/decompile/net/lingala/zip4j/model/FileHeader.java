package net.lingala.zip4j.model;

import java.util.ArrayList;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.unzip.Unzip;
import net.lingala.zip4j.util.Zip4jUtil;

public class FileHeader
{
  private int signature;
  private int versionMadeBy;
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
  private int diskNumberStart;
  private byte[] internalFileAttr;
  private byte[] externalFileAttr;
  private long offsetLocalHeader;
  private String fileName;
  private String fileComment;
  private boolean isDirectory;
  private boolean isEncrypted;
  private int encryptionMethod = -1;
  private char[] password;
  private boolean dataDescriptorExists;
  private Zip64ExtendedInfo zip64ExtendedInfo;
  private AESExtraDataRecord aesExtraDataRecord;
  private ArrayList extraDataRecords;
  private boolean fileNameUTF8Encoded;
  
  public void setSignature(int paramInt)
  {
    this.signature = paramInt;
  }
  
  public void setVersionMadeBy(int paramInt)
  {
    this.versionMadeBy = paramInt;
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
  
  public int getLastModFileTime()
  {
    return this.lastModFileTime;
  }
  
  public void setLastModFileTime(int paramInt)
  {
    this.lastModFileTime = paramInt;
  }
  
  public long getCrc32()
  {
    return this.crc32 & 0xFFFFFFFF;
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
  
  public int getDiskNumberStart()
  {
    return this.diskNumberStart;
  }
  
  public void setDiskNumberStart(int paramInt)
  {
    this.diskNumberStart = paramInt;
  }
  
  public void setInternalFileAttr(byte[] paramArrayOfByte)
  {
    this.internalFileAttr = paramArrayOfByte;
  }
  
  public byte[] getExternalFileAttr()
  {
    return this.externalFileAttr;
  }
  
  public void setExternalFileAttr(byte[] paramArrayOfByte)
  {
    this.externalFileAttr = paramArrayOfByte;
  }
  
  public long getOffsetLocalHeader()
  {
    return this.offsetLocalHeader;
  }
  
  public void setOffsetLocalHeader(long paramLong)
  {
    this.offsetLocalHeader = paramLong;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
  
  public void setFileName(String paramString)
  {
    this.fileName = paramString;
  }
  
  public void setFileComment(String paramString)
  {
    this.fileComment = paramString;
  }
  
  public boolean isDirectory()
  {
    return this.isDirectory;
  }
  
  public void setDirectory(boolean paramBoolean)
  {
    this.isDirectory = paramBoolean;
  }
  
  public void extractFile(ZipModel paramZipModel, String paramString1, UnzipParameters paramUnzipParameters, String paramString2, ProgressMonitor paramProgressMonitor, boolean paramBoolean)
    throws ZipException
  {
    if (paramZipModel == null) {
      throw new ZipException("input zipModel is null");
    }
    if (!Zip4jUtil.checkOutputFolder(paramString1)) {
      throw new ZipException("Invalid output path");
    }
    if (this == null) {
      throw new ZipException("invalid file header");
    }
    Unzip localUnzip = new Unzip(paramZipModel);
    localUnzip.extractFile(this, paramString1, paramUnzipParameters, paramString2, paramProgressMonitor, paramBoolean);
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
  
  public char[] getPassword()
  {
    return this.password;
  }
  
  public byte[] getCrcBuff()
  {
    return this.crcBuff;
  }
  
  public void setCrcBuff(byte[] paramArrayOfByte)
  {
    this.crcBuff = paramArrayOfByte;
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
  
  public Zip64ExtendedInfo getZip64ExtendedInfo()
  {
    return this.zip64ExtendedInfo;
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\FileHeader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */