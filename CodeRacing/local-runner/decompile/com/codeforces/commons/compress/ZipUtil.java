package com.codeforces.commons.compress;

import com.codeforces.commons.io.FileUtil;
import com.codeforces.commons.io.IoUtil;
import com.codeforces.commons.math.Math;
import com.google.common.primitives.Ints;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsSyncException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Deflater;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public final class ZipUtil
{
  private static final int DEFAULT_BUFFER_SIZE = Ints.checkedCast(1048576L);
  
  public static byte[] compress(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte.length == 0) {
      return paramArrayOfByte;
    }
    Deflater localDeflater = new Deflater();
    localDeflater.setLevel(paramInt);
    localDeflater.setInput(paramArrayOfByte);
    localDeflater.finish();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(paramArrayOfByte.length);
    byte[] arrayOfByte = new byte[DEFAULT_BUFFER_SIZE];
    while (!localDeflater.finished()) {
      localByteArrayOutputStream.write(arrayOfByte, 0, localDeflater.deflate(arrayOfByte));
    }
    IoUtil.closeQuietly(localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static void unzip(File paramFile1, File paramFile2)
    throws IOException
  {
    unzip(paramFile1, paramFile2, null);
  }
  
  public static void unzip(File paramFile1, File paramFile2, FileFilter paramFileFilter)
    throws IOException
  {
    try
    {
      FileUtil.ensureDirectoryExists(paramFile2);
      ZipFile localZipFile = new ZipFile(paramFile1);
      int i = 0;
      Iterator localIterator = localZipFile.getFileHeaders().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (i >= 50000L) {
          break;
        }
        FileHeader localFileHeader = (FileHeader)localObject;
        File localFile = new File(paramFile2, localFileHeader.getFileName());
        if ((paramFileFilter == null) || (!paramFileFilter.accept(localFile)))
        {
          if (localFileHeader.isDirectory())
          {
            FileUtil.ensureDirectoryExists(localFile);
          }
          else if ((localFileHeader.getUncompressedSize() <= 536870912L) && (localFileHeader.getCompressedSize() <= 536870912L))
          {
            FileUtil.ensureDirectoryExists(localFile.getParentFile());
            localZipFile.extractFile(localFileHeader, paramFile2.getAbsolutePath());
          }
          else
          {
            long l = Math.max(localFileHeader.getUncompressedSize(), localFileHeader.getCompressedSize());
            throw new IOException("Entry '" + localFileHeader.getFileName() + "' is larger than " + l + " B.");
          }
          i++;
        }
      }
    }
    catch (ZipException localZipException)
    {
      throw new IOException("Can't extract ZIP-file to directory.", localZipException);
    }
  }
  
  public static byte[] getZipEntryBytes(File paramFile, String paramString)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    writeZipEntryBytes(paramFile, paramString, localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static void writeZipEntryBytes(File paramFile, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    TFile localTFile = new TFile(new File(paramFile, paramString));
    try
    {
      try
      {
        Object localObject1;
        if (localTFile.isArchive())
        {
          synchronizeQuietly(localTFile);
          ZipFile localZipFile = new ZipFile(paramFile);
          localObject1 = localZipFile.getInputStream(localZipFile.getFileHeader(paramString));
        }
        else
        {
          localObject1 = new TFileInputStream(localTFile);
        }
        IoUtil.copy((InputStream)localObject1, paramOutputStream);
      }
      catch (ZipException localZipException)
      {
        throw new IOException("Can't write ZIP-entry bytes.", localZipException);
      }
    }
    finally
    {
      IoUtil.closeQuietly(paramOutputStream);
      synchronizeQuietly(localTFile);
    }
  }
  
  public static void synchronizeQuietly(TFile paramTFile)
  {
    if (paramTFile != null)
    {
      TFile localTFile = paramTFile.getTopLevelArchive();
      try
      {
        if (localTFile == null) {
          TVFS.umount(paramTFile);
        } else {
          TVFS.umount(localTFile);
        }
      }
      catch (FsSyncException localFsSyncException) {}
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\compress\ZipUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */