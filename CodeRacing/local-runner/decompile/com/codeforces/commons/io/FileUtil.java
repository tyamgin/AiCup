package com.codeforces.commons.io;

import com.codeforces.commons.io.internal.UnsafeFileUtil;
import com.codeforces.commons.process.ThreadUtil;
import com.codeforces.commons.process.ThreadUtil.ExecutionStrategy;
import com.codeforces.commons.process.ThreadUtil.ExecutionStrategy.Type;
import com.codeforces.commons.process.ThreadUtil.Operation;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

public class FileUtil
{
  private FileUtil()
  {
    throw new UnsupportedOperationException();
  }
  
  public static Object executeIoOperation(ThreadUtil.Operation paramOperation)
    throws IOException
  {
    return executeIoOperation(paramOperation, 9);
  }
  
  public static Object executeIoOperation(ThreadUtil.Operation paramOperation, int paramInt)
    throws IOException
  {
    return executeIoOperation(paramOperation, paramInt, 50L, ThreadUtil.ExecutionStrategy.Type.SQUARE);
  }
  
  public static Object executeIoOperation(ThreadUtil.Operation paramOperation, int paramInt, long paramLong, ThreadUtil.ExecutionStrategy.Type paramType)
    throws IOException
  {
    try
    {
      return ThreadUtil.execute(paramOperation, paramInt, new ThreadUtil.ExecutionStrategy(paramLong, paramType));
    }
    catch (RuntimeException|Error localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Throwable localThrowable)
    {
      throw new IOException(localThrowable);
    }
  }
  
  public static File ensureDirectoryExists(File paramFile)
    throws IOException
  {
    (File)executeIoOperation(new ThreadUtil.Operation()
    {
      public File run()
        throws IOException
      {
        return UnsafeFileUtil.ensureDirectoryExists(this.val$directory);
      }
    });
  }
  
  public static File ensureParentDirectoryExists(File paramFile)
    throws IOException
  {
    File localFile = paramFile.getParentFile();
    if (localFile == null) {
      return null;
    }
    (File)executeIoOperation(new ThreadUtil.Operation()
    {
      public File run()
        throws IOException
      {
        return UnsafeFileUtil.ensureDirectoryExists(this.val$directory);
      }
    });
  }
  
  public static void deleteTotally(File paramFile)
    throws IOException
  {
    executeIoOperation(new ThreadUtil.Operation()
    {
      public Void run()
        throws IOException
      {
        UnsafeFileUtil.deleteTotally(this.val$file);
        return null;
      }
    });
  }
  
  public static void writeFile(File paramFile, final byte[] paramArrayOfByte)
    throws IOException
  {
    executeIoOperation(new ThreadUtil.Operation()
    {
      public Void run()
        throws IOException
      {
        UnsafeFileUtil.writeFile(this.val$file, paramArrayOfByte);
        return null;
      }
    });
  }
  
  public static boolean isFile(File paramFile)
  {
    return (paramFile != null) && (paramFile.isFile());
  }
  
  public static boolean isDirectory(File paramFile)
  {
    return (paramFile != null) && (paramFile.isDirectory());
  }
  
  public static void createSymbolicLinkOrCopy(File paramFile1, File paramFile2)
    throws IOException
  {
    if (!paramFile1.exists()) {
      throw new IOException("Source '" + paramFile1 + "' doesn't exist.");
    }
    deleteTotally(paramFile2);
    ensureParentDirectoryExists(paramFile2);
    try
    {
      Files.createSymbolicLink(FileSystems.getDefault().getPath(paramFile2.getAbsolutePath(), new String[0]), FileSystems.getDefault().getPath(paramFile1.getAbsolutePath(), new String[0]), new FileAttribute[0]);
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      if (isFile(paramFile1)) {
        UnsafeFileUtil.copyFile(paramFile1, paramFile2);
      } else if (isDirectory(paramFile1)) {
        UnsafeFileUtil.copyDirectory(paramFile1, paramFile2);
      } else {
        throw new IOException("Unexpected source '" + paramFile1 + "'.");
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\FileUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */