package net.lingala.zip4j.unzip;

import java.io.File;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.util.Zip4jUtil;

public class UnzipUtil
{
  public static void applyFileAttributes(FileHeader paramFileHeader, File paramFile, UnzipParameters paramUnzipParameters)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("cannot set file properties: file header is null");
    }
    if (paramFile == null) {
      throw new ZipException("cannot set file properties: output file is null");
    }
    if (!Zip4jUtil.checkFileExists(paramFile)) {
      throw new ZipException("cannot set file properties: file doesnot exist");
    }
    if ((paramUnzipParameters == null) || (!paramUnzipParameters.isIgnoreDateTimeAttributes())) {
      setFileLastModifiedTime(paramFileHeader, paramFile);
    }
    if (paramUnzipParameters == null) {
      setFileAttributes(paramFileHeader, paramFile, true, true, true, true);
    } else if (paramUnzipParameters.isIgnoreAllFileAttributes()) {
      setFileAttributes(paramFileHeader, paramFile, false, false, false, false);
    } else {
      setFileAttributes(paramFileHeader, paramFile, !paramUnzipParameters.isIgnoreReadOnlyFileAttribute(), !paramUnzipParameters.isIgnoreHiddenFileAttribute(), !paramUnzipParameters.isIgnoreArchiveFileAttribute(), !paramUnzipParameters.isIgnoreSystemFileAttribute());
    }
  }
  
  private static void setFileAttributes(FileHeader paramFileHeader, File paramFile, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("invalid file header. cannot set file attributes");
    }
    byte[] arrayOfByte = paramFileHeader.getExternalFileAttr();
    if (arrayOfByte == null) {
      return;
    }
    int i = arrayOfByte[0];
    switch (i)
    {
    case 1: 
      if (paramBoolean1) {
        Zip4jUtil.setFileReadOnly(paramFile);
      }
      break;
    case 2: 
    case 18: 
      if (paramBoolean2) {
        Zip4jUtil.setFileHidden(paramFile);
      }
      break;
    case 32: 
    case 48: 
      if (paramBoolean3) {
        Zip4jUtil.setFileArchive(paramFile);
      }
      break;
    case 3: 
      if (paramBoolean1) {
        Zip4jUtil.setFileReadOnly(paramFile);
      }
      if (paramBoolean2) {
        Zip4jUtil.setFileHidden(paramFile);
      }
      break;
    case 33: 
      if (paramBoolean3) {
        Zip4jUtil.setFileArchive(paramFile);
      }
      if (paramBoolean1) {
        Zip4jUtil.setFileReadOnly(paramFile);
      }
      break;
    case 34: 
    case 50: 
      if (paramBoolean3) {
        Zip4jUtil.setFileArchive(paramFile);
      }
      if (paramBoolean2) {
        Zip4jUtil.setFileHidden(paramFile);
      }
      break;
    case 35: 
      if (paramBoolean3) {
        Zip4jUtil.setFileArchive(paramFile);
      }
      if (paramBoolean1) {
        Zip4jUtil.setFileReadOnly(paramFile);
      }
      if (paramBoolean2) {
        Zip4jUtil.setFileHidden(paramFile);
      }
      break;
    case 38: 
      if (paramBoolean1) {
        Zip4jUtil.setFileReadOnly(paramFile);
      }
      if (paramBoolean2) {
        Zip4jUtil.setFileHidden(paramFile);
      }
      if (paramBoolean4) {
        Zip4jUtil.setFileSystemMode(paramFile);
      }
      break;
    }
  }
  
  private static void setFileLastModifiedTime(FileHeader paramFileHeader, File paramFile)
    throws ZipException
  {
    if (paramFileHeader.getLastModFileTime() <= 0) {
      return;
    }
    if (paramFile.exists()) {
      paramFile.setLastModified(Zip4jUtil.dosToJavaTme(paramFileHeader.getLastModFileTime()));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\unzip\UnzipUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */