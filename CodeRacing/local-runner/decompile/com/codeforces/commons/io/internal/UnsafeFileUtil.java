package com.codeforces.commons.io.internal;

import com.codeforces.commons.compress.ZipUtil;
import com.google.common.primitives.Ints;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TVFS;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class UnsafeFileUtil
{
  private static final Logger logger = Logger.getLogger(UnsafeFileUtil.class);
  
  private UnsafeFileUtil()
  {
    throw new UnsupportedOperationException();
  }
  
  public static void copyFile(File paramFile1, File paramFile2)
    throws IOException
  {
    internalCopyFile(paramFile1, paramFile2, true);
  }
  
  /* Error */
  private static void internalCopyFile(File paramFile1, File paramFile2, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: instanceof 22
    //   4: ifeq +13 -> 17
    //   7: new 38	java/lang/UnsupportedOperationException
    //   10: dup
    //   11: ldc 8
    //   13: invokespecial 101	java/lang/UnsupportedOperationException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_1
    //   18: invokestatic 55	com/codeforces/commons/io/internal/UnsafeFileUtil:deleteTotally	(Ljava/io/File;)V
    //   21: aload_1
    //   22: invokevirtual 77	java/io/File:getParentFile	()Ljava/io/File;
    //   25: astore_3
    //   26: aload_3
    //   27: ifnull +8 -> 35
    //   30: aload_3
    //   31: invokestatic 56	com/codeforces/commons/io/internal/UnsafeFileUtil:ensureDirectoryExists	(Ljava/io/File;)Ljava/io/File;
    //   34: pop
    //   35: aload_0
    //   36: instanceof 22
    //   39: ifeq +44 -> 83
    //   42: aload_1
    //   43: aload_0
    //   44: invokestatic 59	com/codeforces/commons/io/internal/UnsafeFileUtil:getBytes	(Ljava/io/File;)[B
    //   47: invokestatic 62	com/codeforces/commons/io/internal/UnsafeFileUtil:writeFile	(Ljava/io/File;[B)V
    //   50: iload_2
    //   51: ifeq +29 -> 80
    //   54: aload_0
    //   55: checkcast 22	de/schlichtherle/truezip/file/TFile
    //   58: invokestatic 50	com/codeforces/commons/compress/ZipUtil:synchronizeQuietly	(Lde/schlichtherle/truezip/file/TFile;)V
    //   61: goto +19 -> 80
    //   64: astore 4
    //   66: iload_2
    //   67: ifeq +10 -> 77
    //   70: aload_0
    //   71: checkcast 22	de/schlichtherle/truezip/file/TFile
    //   74: invokestatic 50	com/codeforces/commons/compress/ZipUtil:synchronizeQuietly	(Lde/schlichtherle/truezip/file/TFile;)V
    //   77: aload 4
    //   79: athrow
    //   80: goto +138 -> 218
    //   83: aload_0
    //   84: invokevirtual 79	java/io/File:isFile	()Z
    //   87: ifne +35 -> 122
    //   90: new 30	java/io/IOException
    //   93: dup
    //   94: new 36	java/lang/StringBuilder
    //   97: dup
    //   98: invokespecial 96	java/lang/StringBuilder:<init>	()V
    //   101: ldc 1
    //   103: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: aload_0
    //   107: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   110: ldc 4
    //   112: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: invokespecial 92	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   121: athrow
    //   122: aconst_null
    //   123: astore 4
    //   125: aconst_null
    //   126: astore 5
    //   128: new 27	java/io/FileInputStream
    //   131: dup
    //   132: aload_0
    //   133: invokespecial 85	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   136: astore 4
    //   138: new 29	java/io/FileOutputStream
    //   141: dup
    //   142: aload_1
    //   143: invokespecial 89	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   146: astore 5
    //   148: aload 4
    //   150: invokevirtual 87	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   153: astore 6
    //   155: aload 5
    //   157: invokevirtual 90	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   160: astore 7
    //   162: aload 6
    //   164: lconst_0
    //   165: aload 6
    //   167: invokevirtual 106	java/nio/channels/FileChannel:size	()J
    //   170: aload 7
    //   172: invokevirtual 107	java/nio/channels/FileChannel:transferTo	(JJLjava/nio/channels/WritableByteChannel;)J
    //   175: pop2
    //   176: iconst_2
    //   177: anewarray 32	java/lang/AutoCloseable
    //   180: dup
    //   181: iconst_0
    //   182: aload 4
    //   184: aastore
    //   185: dup
    //   186: iconst_1
    //   187: aload 5
    //   189: aastore
    //   190: invokestatic 53	com/codeforces/commons/io/IoUtil:closeQuietly	([Ljava/lang/AutoCloseable;)V
    //   193: goto +25 -> 218
    //   196: astore 8
    //   198: iconst_2
    //   199: anewarray 32	java/lang/AutoCloseable
    //   202: dup
    //   203: iconst_0
    //   204: aload 4
    //   206: aastore
    //   207: dup
    //   208: iconst_1
    //   209: aload 5
    //   211: aastore
    //   212: invokestatic 53	com/codeforces/commons/io/IoUtil:closeQuietly	([Ljava/lang/AutoCloseable;)V
    //   215: aload 8
    //   217: athrow
    //   218: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	219	0	paramFile1	File
    //   0	219	1	paramFile2	File
    //   0	219	2	paramBoolean	boolean
    //   25	6	3	localFile	File
    //   64	14	4	localObject1	Object
    //   123	82	4	localFileInputStream	FileInputStream
    //   126	84	5	localFileOutputStream	FileOutputStream
    //   153	13	6	localFileChannel1	FileChannel
    //   160	11	7	localFileChannel2	FileChannel
    //   196	20	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   42	50	64	finally
    //   64	66	64	finally
    //   128	176	196	finally
    //   196	198	196	finally
  }
  
  public static void copyDirectory(File paramFile1, File paramFile2)
    throws IOException
  {
    internalCopyDirectory(paramFile1, paramFile2, true);
  }
  
  private static void internalCopyDirectory(File paramFile1, File paramFile2, boolean paramBoolean)
    throws IOException
  {
    if ((paramFile2 instanceof TFile)) {
      throw new UnsupportedOperationException("Can't copy directory into archive file.");
    }
    if (!paramFile1.isDirectory()) {
      throw new IOException("'" + paramFile1 + "' is not a directory.");
    }
    if (paramFile2.isFile()) {
      throw new IOException("'" + paramFile2 + "' is a file.");
    }
    ensureDirectoryExists(paramFile2);
    try
    {
      for (String str : paramFile1.list())
      {
        File localFile1 = (paramFile1 instanceof TFile) ? new TFile(paramFile1, str) : new File(paramFile1, str);
        File localFile2 = new File(paramFile2, str);
        if (localFile1.isDirectory())
        {
          TFile localTFile1;
          TFile localTFile2;
          if (((localFile1 instanceof TFile)) && ((localTFile1 = (TFile)localFile1).isArchive()) && ((localTFile2 = localTFile1.getEnclArchive()) != null) && (new File(localTFile2.getAbsolutePath()).isFile()))
          {
            deleteTotally(localFile2);
            ZipUtil.synchronizeQuietly(localTFile1);
            ZipUtil.writeZipEntryBytes(localTFile2, localTFile1.getEnclEntryName(), new FileOutputStream(localFile2));
          }
          else
          {
            internalCopyDirectory(localFile1, localFile2, false);
          }
        }
        else
        {
          internalCopyFile(localFile1, localFile2, false);
        }
      }
    }
    finally
    {
      if ((paramBoolean) && ((paramFile1 instanceof TFile))) {
        ZipUtil.synchronizeQuietly((TFile)paramFile1);
      }
    }
  }
  
  public static File ensureDirectoryExists(File paramFile)
    throws IOException
  {
    if ((paramFile.isDirectory()) || (paramFile.mkdirs()) || (paramFile.isDirectory())) {
      return paramFile;
    }
    throw new IOException("Can't create directory '" + paramFile + "'.");
  }
  
  public static void deleteTotally(File paramFile)
    throws IOException
  {
    if (paramFile == null) {
      return;
    }
    Path localPath = Paths.get(paramFile.toURI());
    if (Files.exists(localPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
      if (Files.isSymbolicLink(localPath))
      {
        if (!paramFile.delete()) {
          if (Files.exists(localPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
            throw new IOException("Can't delete symbolic link '" + paramFile + "'.");
          }
        }
      }
      else if (paramFile.isFile())
      {
        if ((!paramFile.delete()) && (paramFile.exists())) {
          throw new IOException("Can't delete file '" + paramFile + "'.");
        }
      }
      else if (paramFile.isDirectory())
      {
        cleanDirectory(paramFile, null);
        if ((!paramFile.delete()) && (paramFile.exists())) {
          throw new IOException("Can't delete directory '" + paramFile + "'.");
        }
      }
      else if (Files.exists(localPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
      {
        throw new IllegalArgumentException("Unsupported file system item '" + paramFile + "'.");
      }
    }
  }
  
  private static void ensureParentDirectoryExists(File paramFile)
    throws IOException
  {
    File localFile = paramFile.getParentFile();
    if (localFile != null) {
      ensureDirectoryExists(localFile);
    }
  }
  
  public static void cleanDirectory(File paramFile, FileFilter paramFileFilter)
    throws IOException
  {
    if (!paramFile.isDirectory()) {
      throw new IllegalArgumentException("'" + paramFile + "' is not a directory.");
    }
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile == null) {
      throw new IOException("Failed to list files of '" + paramFile + "'.");
    }
    int i = 0;
    int j = arrayOfFile.length;
    while (i < j)
    {
      File localFile = arrayOfFile[i];
      if ((paramFileFilter == null) || (paramFileFilter.accept(localFile))) {
        deleteTotally(localFile);
      } else if ((localFile.isDirectory()) && (!Files.isSymbolicLink(Paths.get(localFile.toURI())))) {
        cleanDirectory(localFile, paramFileFilter);
      }
      i++;
    }
  }
  
  /* Error */
  public static void writeFile(File paramFile, byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 57	com/codeforces/commons/io/internal/UnsafeFileUtil:ensureParentDirectoryExists	(Ljava/io/File;)V
    //   4: aconst_null
    //   5: astore_2
    //   6: new 29	java/io/FileOutputStream
    //   9: dup
    //   10: aload_0
    //   11: invokespecial 89	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   14: astore_2
    //   15: aload_2
    //   16: aload_1
    //   17: invokevirtual 91	java/io/FileOutputStream:write	([B)V
    //   20: aload_2
    //   21: invokestatic 52	com/codeforces/commons/io/IoUtil:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   24: goto +10 -> 34
    //   27: astore_3
    //   28: aload_2
    //   29: invokestatic 52	com/codeforces/commons/io/IoUtil:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   32: aload_3
    //   33: athrow
    //   34: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	paramFile	File
    //   0	35	1	paramArrayOfByte	byte[]
    //   5	24	2	localFileOutputStream	FileOutputStream
    //   27	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   6	20	27	finally
  }
  
  public static byte[] getBytes(File paramFile)
    throws IOException
  {
    if ((paramFile instanceof TFile))
    {
      TFile localTFile = (TFile)paramFile;
      try
      {
        if (localTFile.isFile())
        {
          long l = paramFile.length();
          TFileInputStream localTFileInputStream = new TFileInputStream(paramFile);
          byte[] arrayOfByte2 = new byte[Ints.checkedCast(l)];
          IOUtils.read(localTFileInputStream, arrayOfByte2);
          localTFileInputStream.close();
          byte[] arrayOfByte3 = arrayOfByte2;
          return arrayOfByte3;
        }
        if (localTFile.isArchive())
        {
          TVFS.umount(localTFile);
          paramFile = new File(paramFile.getAbsolutePath());
          if (paramFile.isFile())
          {
            localObject1 = forceGetBytesFromExistingRegularFile(paramFile);
            return (byte[])localObject1;
          }
          Object localObject1 = localTFile.getEnclArchive();
          if ((localObject1 != null) && (new File(((TFile)localObject1).getAbsolutePath()).isFile()))
          {
            byte[] arrayOfByte1 = ZipUtil.getZipEntryBytes((File)localObject1, localTFile.getEnclEntryName());
            return arrayOfByte1;
          }
        }
      }
      finally
      {
        TVFS.umount(localTFile);
      }
    }
    else if (paramFile.isFile())
    {
      return forceGetBytesFromExistingRegularFile(paramFile);
    }
    throw new FileNotFoundException("'" + paramFile + "' is not file.");
  }
  
  private static byte[] forceGetBytesFromExistingRegularFile(File paramFile)
    throws IOException
  {
    long l = paramFile.length();
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    FileChannel localFileChannel = localFileInputStream.getChannel();
    ByteBuffer localByteBuffer = ByteBuffer.allocate(Ints.checkedCast(l));
    localFileChannel.read(localByteBuffer);
    localFileChannel.close();
    localFileInputStream.close();
    return localByteBuffer.array();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\internal\UnsafeFileUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */