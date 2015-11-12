package net.lingala.zip4j.unzip;

import java.io.File;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.InternalZipConstants;
import net.lingala.zip4j.util.Zip4jUtil;

public class Unzip
{
  private ZipModel zipModel;
  
  public Unzip(ZipModel paramZipModel)
    throws ZipException
  {
    if (paramZipModel == null) {
      throw new ZipException("ZipModel is null");
    }
    this.zipModel = paramZipModel;
  }
  
  public void extractFile(final FileHeader paramFileHeader, final String paramString1, final UnzipParameters paramUnzipParameters, final String paramString2, final ProgressMonitor paramProgressMonitor, boolean paramBoolean)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("fileHeader is null");
    }
    paramProgressMonitor.setCurrentOperation(1);
    paramProgressMonitor.setTotalWork(paramFileHeader.getCompressedSize());
    paramProgressMonitor.setState(1);
    paramProgressMonitor.setPercentDone(0);
    paramProgressMonitor.setFileName(paramFileHeader.getFileName());
    if (paramBoolean)
    {
      Thread local2 = new Thread("Zip4j")
      {
        public void run()
        {
          try
          {
            Unzip.this.initExtractFile(paramFileHeader, paramString1, paramUnzipParameters, paramString2, paramProgressMonitor);
            paramProgressMonitor.endProgressMonitorSuccess();
          }
          catch (ZipException localZipException) {}
        }
      };
      local2.start();
    }
    else
    {
      initExtractFile(paramFileHeader, paramString1, paramUnzipParameters, paramString2, paramProgressMonitor);
      paramProgressMonitor.endProgressMonitorSuccess();
    }
  }
  
  private void initExtractFile(FileHeader paramFileHeader, String paramString1, UnzipParameters paramUnzipParameters, String paramString2, ProgressMonitor paramProgressMonitor)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("fileHeader is null");
    }
    try
    {
      paramProgressMonitor.setFileName(paramFileHeader.getFileName());
      if (!paramString1.endsWith(InternalZipConstants.FILE_SEPARATOR)) {
        paramString1 = paramString1 + InternalZipConstants.FILE_SEPARATOR;
      }
      if (paramFileHeader.isDirectory()) {
        try
        {
          String str1 = paramFileHeader.getFileName();
          if (!Zip4jUtil.isStringNotNullAndNotEmpty(str1)) {
            return;
          }
          String str2 = paramString1 + str1;
          File localFile = new File(str2);
          if (!localFile.exists()) {
            localFile.mkdirs();
          }
        }
        catch (Exception localException1)
        {
          paramProgressMonitor.endProgressMonitorError(localException1);
          throw new ZipException(localException1);
        }
      }
      checkOutputDirectoryStructure(paramFileHeader, paramString1, paramString2);
      UnzipEngine localUnzipEngine = new UnzipEngine(this.zipModel, paramFileHeader);
      try
      {
        localUnzipEngine.unzipFile(paramProgressMonitor, paramString1, paramString2, paramUnzipParameters);
      }
      catch (Exception localException3)
      {
        paramProgressMonitor.endProgressMonitorError(localException3);
        throw new ZipException(localException3);
      }
    }
    catch (ZipException localZipException)
    {
      paramProgressMonitor.endProgressMonitorError(localZipException);
      throw localZipException;
    }
    catch (Exception localException2)
    {
      paramProgressMonitor.endProgressMonitorError(localException2);
      throw new ZipException(localException2);
    }
  }
  
  public ZipInputStream getInputStream(FileHeader paramFileHeader)
    throws ZipException
  {
    UnzipEngine localUnzipEngine = new UnzipEngine(this.zipModel, paramFileHeader);
    return localUnzipEngine.getInputStream();
  }
  
  private void checkOutputDirectoryStructure(FileHeader paramFileHeader, String paramString1, String paramString2)
    throws ZipException
  {
    if ((paramFileHeader == null) || (!Zip4jUtil.isStringNotNullAndNotEmpty(paramString1))) {
      throw new ZipException("Cannot check output directory structure...one of the parameters was null");
    }
    String str1 = paramFileHeader.getFileName();
    if (Zip4jUtil.isStringNotNullAndNotEmpty(paramString2)) {
      str1 = paramString2;
    }
    if (!Zip4jUtil.isStringNotNullAndNotEmpty(str1)) {
      return;
    }
    String str2 = paramString1 + str1;
    try
    {
      File localFile1 = new File(str2);
      String str3 = localFile1.getParent();
      File localFile2 = new File(str3);
      if (!localFile2.exists()) {
        localFile2.mkdirs();
      }
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\unzip\Unzip.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */