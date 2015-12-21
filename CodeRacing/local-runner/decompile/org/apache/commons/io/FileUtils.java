package org.apache.commons.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

public class FileUtils
{
  public static final BigInteger ONE_KB_BI = BigInteger.valueOf(1024L);
  public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);
  public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
  public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
  public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);
  public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);
  public static final BigInteger ONE_ZB = BigInteger.valueOf(1024L).multiply(BigInteger.valueOf(1152921504606846976L));
  public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);
  public static final File[] EMPTY_FILE_ARRAY = new File[0];
  private static final Charset UTF8 = Charset.forName("UTF-8");
  
  public static FileInputStream openInputStream(File paramFile)
    throws IOException
  {
    if (paramFile.exists())
    {
      if (paramFile.isDirectory()) {
        throw new IOException("File '" + paramFile + "' exists but is a directory");
      }
      if (!paramFile.canRead()) {
        throw new IOException("File '" + paramFile + "' cannot be read");
      }
    }
    else
    {
      throw new FileNotFoundException("File '" + paramFile + "' does not exist");
    }
    return new FileInputStream(paramFile);
  }
  
  public static FileOutputStream openOutputStream(File paramFile)
    throws IOException
  {
    return openOutputStream(paramFile, false);
  }
  
  public static FileOutputStream openOutputStream(File paramFile, boolean paramBoolean)
    throws IOException
  {
    if (paramFile.exists())
    {
      if (paramFile.isDirectory()) {
        throw new IOException("File '" + paramFile + "' exists but is a directory");
      }
      if (!paramFile.canWrite()) {
        throw new IOException("File '" + paramFile + "' cannot be written to");
      }
    }
    else
    {
      File localFile = paramFile.getParentFile();
      if ((localFile != null) && (!localFile.mkdirs()) && (!localFile.isDirectory())) {
        throw new IOException("Directory '" + localFile + "' could not be created");
      }
    }
    return new FileOutputStream(paramFile, paramBoolean);
  }
  
  public static void copyInputStreamToFile(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    try
    {
      FileOutputStream localFileOutputStream = openOutputStream(paramFile);
      try
      {
        IOUtils.copy(paramInputStream, localFileOutputStream);
        localFileOutputStream.close();
      }
      finally {}
    }
    finally
    {
      IOUtils.closeQuietly(paramInputStream);
    }
  }
  
  public static byte[] readFileToByteArray(File paramFile)
    throws IOException
  {
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = openInputStream(paramFile);
      byte[] arrayOfByte = IOUtils.toByteArray(localFileInputStream, paramFile.length());
      return arrayOfByte;
    }
    finally
    {
      IOUtils.closeQuietly(localFileInputStream);
    }
  }
  
  public static List readLines(File paramFile, Charset paramCharset)
    throws IOException
  {
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = openInputStream(paramFile);
      List localList = IOUtils.readLines(localFileInputStream, Charsets.toCharset(paramCharset));
      return localList;
    }
    finally
    {
      IOUtils.closeQuietly(localFileInputStream);
    }
  }
  
  public static void writeByteArrayToFile(File paramFile, byte[] paramArrayOfByte)
    throws IOException
  {
    writeByteArrayToFile(paramFile, paramArrayOfByte, false);
  }
  
  public static void writeByteArrayToFile(File paramFile, byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException
  {
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = openOutputStream(paramFile, paramBoolean);
      localFileOutputStream.write(paramArrayOfByte);
      localFileOutputStream.close();
    }
    finally
    {
      IOUtils.closeQuietly(localFileOutputStream);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\io\FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */