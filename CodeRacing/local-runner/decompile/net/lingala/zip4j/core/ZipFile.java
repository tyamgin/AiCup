package net.lingala.zip4j.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.CentralDirectory;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.unzip.Unzip;
import net.lingala.zip4j.util.Zip4jUtil;

public class ZipFile
{
  private String file;
  private int mode;
  private ZipModel zipModel;
  private ProgressMonitor progressMonitor;
  private boolean runInThread;
  private String fileNameCharset;
  
  public ZipFile(File paramFile)
    throws ZipException
  {
    if (paramFile == null) {
      throw new ZipException("Input zip file parameter is not null", 1);
    }
    this.file = paramFile.getPath();
    this.mode = 2;
    this.progressMonitor = new ProgressMonitor();
    this.runInThread = false;
  }
  
  private void readZipInfo()
    throws ZipException
  {
    if (!Zip4jUtil.checkFileExists(this.file)) {
      throw new ZipException("zip file does not exist");
    }
    if (!Zip4jUtil.checkFileReadAccess(this.file)) {
      throw new ZipException("no read access for the input zip file");
    }
    if (this.mode != 2) {
      throw new ZipException("Invalid mode");
    }
    RandomAccessFile localRandomAccessFile = null;
    try
    {
      localRandomAccessFile = new RandomAccessFile(new File(this.file), "r");
      if (this.zipModel == null)
      {
        HeaderReader localHeaderReader = new HeaderReader(localRandomAccessFile);
        this.zipModel = localHeaderReader.readAllHeaders(this.fileNameCharset);
        if (this.zipModel != null) {
          this.zipModel.setZipFile(this.file);
        }
      }
      return;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw new ZipException(localFileNotFoundException);
    }
    finally
    {
      if (localRandomAccessFile != null) {
        try
        {
          localRandomAccessFile.close();
        }
        catch (IOException localIOException2) {}
      }
    }
  }
  
  public void extractFile(FileHeader paramFileHeader, String paramString)
    throws ZipException
  {
    extractFile(paramFileHeader, paramString, null);
  }
  
  public void extractFile(FileHeader paramFileHeader, String paramString, UnzipParameters paramUnzipParameters)
    throws ZipException
  {
    extractFile(paramFileHeader, paramString, paramUnzipParameters, null);
  }
  
  public void extractFile(FileHeader paramFileHeader, String paramString1, UnzipParameters paramUnzipParameters, String paramString2)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("input file header is null, cannot extract file");
    }
    if (!Zip4jUtil.isStringNotNullAndNotEmpty(paramString1)) {
      throw new ZipException("destination path is empty or null, cannot extract file");
    }
    readZipInfo();
    if (this.progressMonitor.getState() == 1) {
      throw new ZipException("invalid operation - Zip4j is in busy state");
    }
    paramFileHeader.extractFile(this.zipModel, paramString1, paramUnzipParameters, paramString2, this.progressMonitor, this.runInThread);
  }
  
  public List getFileHeaders()
    throws ZipException
  {
    readZipInfo();
    if ((this.zipModel == null) || (this.zipModel.getCentralDirectory() == null)) {
      return null;
    }
    return this.zipModel.getCentralDirectory().getFileHeaders();
  }
  
  public FileHeader getFileHeader(String paramString)
    throws ZipException
  {
    if (!Zip4jUtil.isStringNotNullAndNotEmpty(paramString)) {
      throw new ZipException("input file name is emtpy or null, cannot get FileHeader");
    }
    readZipInfo();
    if ((this.zipModel == null) || (this.zipModel.getCentralDirectory() == null)) {
      return null;
    }
    return Zip4jUtil.getFileHeader(this.zipModel, paramString);
  }
  
  private void checkZipModel()
    throws ZipException
  {
    if (this.zipModel == null) {
      if (Zip4jUtil.checkFileExists(this.file)) {
        readZipInfo();
      } else {
        createNewZipModel();
      }
    }
  }
  
  private void createNewZipModel()
  {
    this.zipModel = new ZipModel();
    this.zipModel.setZipFile(this.file);
    this.zipModel.setFileNameCharset(this.fileNameCharset);
  }
  
  public ZipInputStream getInputStream(FileHeader paramFileHeader)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("FileHeader is null, cannot get InputStream");
    }
    checkZipModel();
    if (this.zipModel == null) {
      throw new ZipException("zip model is null, cannot get inputstream");
    }
    Unzip localUnzip = new Unzip(this.zipModel);
    return localUnzip.getInputStream(paramFileHeader);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\core\ZipFile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */