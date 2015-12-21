package net.lingala.zip4j.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.CentralDirectory;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipModel;

public class Zip4jUtil
{
  public static boolean isStringNotNullAndNotEmpty(String paramString)
  {
    return (paramString != null) && (paramString.trim().length() > 0);
  }
  
  public static boolean checkOutputFolder(String paramString)
    throws ZipException
  {
    if (!isStringNotNullAndNotEmpty(paramString)) {
      throw new ZipException(new NullPointerException("output path is null"));
    }
    File localFile = new File(paramString);
    if (localFile.exists())
    {
      if (!localFile.isDirectory()) {
        throw new ZipException("output folder is not valid");
      }
      if (!localFile.canWrite()) {
        throw new ZipException("no write access to output folder");
      }
    }
    else
    {
      try
      {
        localFile.mkdirs();
        if (!localFile.isDirectory()) {
          throw new ZipException("output folder is not valid");
        }
        if (!localFile.canWrite()) {
          throw new ZipException("no write access to destination folder");
        }
      }
      catch (Exception localException)
      {
        throw new ZipException("Cannot create destination folder");
      }
    }
    return true;
  }
  
  public static boolean checkFileReadAccess(String paramString)
    throws ZipException
  {
    if (!isStringNotNullAndNotEmpty(paramString)) {
      throw new ZipException("path is null");
    }
    if (!checkFileExists(paramString)) {
      throw new ZipException("file does not exist: " + paramString);
    }
    try
    {
      File localFile = new File(paramString);
      return localFile.canRead();
    }
    catch (Exception localException)
    {
      throw new ZipException("cannot read zip file");
    }
  }
  
  public static boolean checkFileExists(String paramString)
    throws ZipException
  {
    if (!isStringNotNullAndNotEmpty(paramString)) {
      throw new ZipException("path is null");
    }
    File localFile = new File(paramString);
    return checkFileExists(localFile);
  }
  
  public static boolean checkFileExists(File paramFile)
    throws ZipException
  {
    if (paramFile == null) {
      throw new ZipException("cannot check if file exists: input file is null");
    }
    return paramFile.exists();
  }
  
  public static void setFileReadOnly(File paramFile)
    throws ZipException
  {
    if (paramFile == null) {
      throw new ZipException("input file is null. cannot set read only file attribute");
    }
    if (paramFile.exists()) {
      paramFile.setReadOnly();
    }
  }
  
  public static void setFileHidden(File paramFile)
    throws ZipException
  {}
  
  public static void setFileArchive(File paramFile)
    throws ZipException
  {}
  
  public static void setFileSystemMode(File paramFile)
    throws ZipException
  {}
  
  public static long dosToJavaTme(int paramInt)
  {
    int i = 2 * (paramInt & 0x1F);
    int j = paramInt >> 5 & 0x3F;
    int k = paramInt >> 11 & 0x1F;
    int m = paramInt >> 16 & 0x1F;
    int n = (paramInt >> 21 & 0xF) - 1;
    int i1 = (paramInt >> 25 & 0x7F) + 1980;
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(i1, n, m, k, j, i);
    localCalendar.set(14, 0);
    return localCalendar.getTime().getTime();
  }
  
  public static FileHeader getFileHeader(ZipModel paramZipModel, String paramString)
    throws ZipException
  {
    if (paramZipModel == null) {
      throw new ZipException("zip model is null, cannot determine file header for fileName: " + paramString);
    }
    if (!isStringNotNullAndNotEmpty(paramString)) {
      throw new ZipException("file name is null, cannot determine file header for fileName: " + paramString);
    }
    FileHeader localFileHeader = null;
    localFileHeader = getFileHeaderWithExactMatch(paramZipModel, paramString);
    if (localFileHeader == null)
    {
      paramString = paramString.replaceAll("\\\\", "/");
      localFileHeader = getFileHeaderWithExactMatch(paramZipModel, paramString);
      if (localFileHeader == null)
      {
        paramString = paramString.replaceAll("/", "\\\\");
        localFileHeader = getFileHeaderWithExactMatch(paramZipModel, paramString);
      }
    }
    return localFileHeader;
  }
  
  public static FileHeader getFileHeaderWithExactMatch(ZipModel paramZipModel, String paramString)
    throws ZipException
  {
    if (paramZipModel == null) {
      throw new ZipException("zip model is null, cannot determine file header with exact match for fileName: " + paramString);
    }
    if (!isStringNotNullAndNotEmpty(paramString)) {
      throw new ZipException("file name is null, cannot determine file header with exact match for fileName: " + paramString);
    }
    if (paramZipModel.getCentralDirectory() == null) {
      throw new ZipException("central directory is null, cannot determine file header with exact match for fileName: " + paramString);
    }
    if (paramZipModel.getCentralDirectory().getFileHeaders() == null) {
      throw new ZipException("file Headers are null, cannot determine file header with exact match for fileName: " + paramString);
    }
    if (paramZipModel.getCentralDirectory().getFileHeaders().size() <= 0) {
      return null;
    }
    ArrayList localArrayList = paramZipModel.getCentralDirectory().getFileHeaders();
    for (int i = 0; i < localArrayList.size(); i++)
    {
      FileHeader localFileHeader = (FileHeader)localArrayList.get(i);
      String str = localFileHeader.getFileName();
      if ((isStringNotNullAndNotEmpty(str)) && (paramString.equalsIgnoreCase(str))) {
        return localFileHeader;
      }
    }
    return null;
  }
  
  public static String decodeFileName(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramBoolean) {
      try
      {
        return new String(paramArrayOfByte, "UTF8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        return new String(paramArrayOfByte);
      }
    }
    return getCp850EncodedString(paramArrayOfByte);
  }
  
  public static String getCp850EncodedString(byte[] paramArrayOfByte)
  {
    try
    {
      String str = new String(paramArrayOfByte, "Cp850");
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return new String(paramArrayOfByte);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\util\Zip4jUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */