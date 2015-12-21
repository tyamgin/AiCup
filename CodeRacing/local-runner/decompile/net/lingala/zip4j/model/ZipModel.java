package net.lingala.zip4j.model;

public class ZipModel
  implements Cloneable
{
  private CentralDirectory centralDirectory;
  private EndCentralDirRecord endCentralDirRecord;
  private Zip64EndCentralDirLocator zip64EndCentralDirLocator;
  private Zip64EndCentralDirRecord zip64EndCentralDirRecord;
  private boolean splitArchive;
  private long splitLength = -1L;
  private String zipFile;
  private boolean isZip64Format;
  private String fileNameCharset;
  
  public CentralDirectory getCentralDirectory()
  {
    return this.centralDirectory;
  }
  
  public void setCentralDirectory(CentralDirectory paramCentralDirectory)
  {
    this.centralDirectory = paramCentralDirectory;
  }
  
  public EndCentralDirRecord getEndCentralDirRecord()
  {
    return this.endCentralDirRecord;
  }
  
  public void setEndCentralDirRecord(EndCentralDirRecord paramEndCentralDirRecord)
  {
    this.endCentralDirRecord = paramEndCentralDirRecord;
  }
  
  public boolean isSplitArchive()
  {
    return this.splitArchive;
  }
  
  public void setSplitArchive(boolean paramBoolean)
  {
    this.splitArchive = paramBoolean;
  }
  
  public String getZipFile()
  {
    return this.zipFile;
  }
  
  public void setZipFile(String paramString)
  {
    this.zipFile = paramString;
  }
  
  public Zip64EndCentralDirLocator getZip64EndCentralDirLocator()
  {
    return this.zip64EndCentralDirLocator;
  }
  
  public void setZip64EndCentralDirLocator(Zip64EndCentralDirLocator paramZip64EndCentralDirLocator)
  {
    this.zip64EndCentralDirLocator = paramZip64EndCentralDirLocator;
  }
  
  public Zip64EndCentralDirRecord getZip64EndCentralDirRecord()
  {
    return this.zip64EndCentralDirRecord;
  }
  
  public void setZip64EndCentralDirRecord(Zip64EndCentralDirRecord paramZip64EndCentralDirRecord)
  {
    this.zip64EndCentralDirRecord = paramZip64EndCentralDirRecord;
  }
  
  public boolean isZip64Format()
  {
    return this.isZip64Format;
  }
  
  public void setZip64Format(boolean paramBoolean)
  {
    this.isZip64Format = paramBoolean;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
  
  public String getFileNameCharset()
  {
    return this.fileNameCharset;
  }
  
  public void setFileNameCharset(String paramString)
  {
    this.fileNameCharset = paramString;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\ZipModel.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */