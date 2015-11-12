package com.codeforces.commons.resource;

import com.codeforces.commons.io.FileUtil;
import com.codeforces.commons.io.IoUtil;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class ResourceUtil
{
  private static final Logger logger = Logger.getLogger(ResourceUtil.class);
  private static final ConcurrentMap cacheLockByDirectory = new ConcurrentHashMap();
  private static final ConcurrentMap validationResultByCacheKey = new ConcurrentHashMap();
  
  public static byte[] getResource(Class paramClass, String paramString)
  {
    InputStream localInputStream = paramClass.getResourceAsStream(paramString);
    if (localInputStream == null) {
      throw new CantReadResourceException("Can't find resource '" + paramString + "' for " + paramClass + '.');
    }
    try
    {
      return IoUtil.toByteArray(localInputStream);
    }
    catch (IOException localIOException)
    {
      throw new CantReadResourceException("Can't read resource '" + paramString + "' for " + paramClass + '.', localIOException);
    }
  }
  
  public static byte[] getResourceOrNull(Class paramClass, String paramString)
  {
    try
    {
      return getResource(paramClass, paramString);
    }
    catch (CantReadResourceException localCantReadResourceException) {}
    return null;
  }
  
  public static void copyResourceToDir(File paramFile1, File paramFile2, String paramString, byte[] paramArrayOfByte, Class paramClass, boolean paramBoolean)
    throws IOException
  {
    File localFile1 = new File(paramFile1, new File(paramString).getName());
    if (paramFile2 == null)
    {
      saveResourceToFile(localFile1, paramString, paramArrayOfByte, paramClass);
    }
    else
    {
      File localFile2 = new File(paramFile2, toRelativePath(paramString));
      ReadWriteLock localReadWriteLock = (ReadWriteLock)cacheLockByDirectory.get(paramFile2);
      if (localReadWriteLock == null)
      {
        cacheLockByDirectory.putIfAbsent(paramFile2, new ReentrantReadWriteLock());
        localReadWriteLock = (ReadWriteLock)cacheLockByDirectory.get(paramFile2);
      }
      Lock localLock1 = localReadWriteLock.readLock();
      localLock1.lock();
      boolean bool;
      try
      {
        bool = isCacheEntryValid(localFile2, paramString, paramArrayOfByte, paramClass, paramBoolean);
      }
      finally
      {
        localLock1.unlock();
      }
      if (!bool)
      {
        Lock localLock2 = localReadWriteLock.writeLock();
        localLock2.lock();
        try
        {
          if (!isCacheEntryValid(localFile2, paramString, paramArrayOfByte, paramClass, paramBoolean)) {
            writeCacheEntry(localFile2, paramString, paramArrayOfByte, paramClass);
          }
        }
        finally
        {
          localLock2.unlock();
        }
      }
      try
      {
        FileUtil.createSymbolicLinkOrCopy(localFile2, localFile1);
      }
      catch (IOException localIOException)
      {
        throw new IOException(String.format("Can't create symbolic link or copy resource '%s' into the directory '%s'.", new Object[] { paramString, paramFile1 }), localIOException);
      }
    }
  }
  
  private static boolean isCacheEntryValid(File paramFile, String paramString, byte[] paramArrayOfByte, Class paramClass, boolean paramBoolean)
    throws IOException
  {
    if (!paramFile.isFile()) {
      return false;
    }
    Class localClass = paramClass == null ? ResourceUtil.class : paramClass;
    CacheKey localCacheKey = new CacheKey(paramString, paramArrayOfByte, paramClass, null);
    if (paramBoolean)
    {
      localObject = (Boolean)validationResultByCacheKey.get(localCacheKey);
      if ((localObject != null) && (((Boolean)localObject).booleanValue()))
      {
        long l = Resources.asByteSource(localClass.getResource(paramString)).size();
        if (paramFile.length() == l) {
          return true;
        }
      }
    }
    Object localObject = null;
    BufferedInputStream localBufferedInputStream = null;
    try
    {
      localObject = paramArrayOfByte == null ? new BufferedInputStream(localClass.getResourceAsStream(paramString), IoUtil.BUFFER_SIZE) : new ByteArrayInputStream(paramArrayOfByte);
      localBufferedInputStream = new BufferedInputStream(new FileInputStream(paramFile), IoUtil.BUFFER_SIZE);
      boolean bool = IOUtils.contentEquals((InputStream)localObject, localBufferedInputStream);
      if (bool) {
        validationResultByCacheKey.putIfAbsent(localCacheKey, Boolean.valueOf(true));
      }
      ((InputStream)localObject).close();
      localBufferedInputStream.close();
      return bool;
    }
    catch (IOException localIOException)
    {
      IoUtil.closeQuietly(new AutoCloseable[] { localObject, localBufferedInputStream });
      throw new IOException(String.format("Can't compare resource '%s' and cache file '%s'.", new Object[] { paramString, paramFile }), localIOException);
    }
  }
  
  private static void writeCacheEntry(File paramFile, String paramString, byte[] paramArrayOfByte, Class paramClass)
    throws IOException
  {
    logger.info(String.format("Saving resource '%s' to the cache file '%s'.", new Object[] { paramString, paramFile }));
    try
    {
      FileUtil.deleteTotally(paramFile);
    }
    catch (IOException localIOException1)
    {
      throw new IOException(String.format("Can't delete invalid cache file '%s'.", new Object[] { paramFile }), localIOException1);
    }
    try
    {
      FileUtil.ensureParentDirectoryExists(paramFile);
    }
    catch (IOException localIOException2)
    {
      throw new IOException(String.format("Can't create cache directory '%s'.", new Object[] { paramFile.getParentFile() }), localIOException2);
    }
    saveResourceToFile(paramFile, paramString, paramArrayOfByte, paramClass);
  }
  
  public static void saveResourceToFile(File paramFile, String paramString, byte[] paramArrayOfByte, Class paramClass)
    throws IOException
  {
    InputStream localInputStream = null;
    BufferedOutputStream localBufferedOutputStream = null;
    try
    {
      if (paramArrayOfByte == null)
      {
        localInputStream = (paramClass == null ? FileUtil.class : paramClass).getResourceAsStream(paramString);
        if (localInputStream == null) {
          throw new IOException("Can't find resource '" + paramString + "'.");
        }
        localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramFile));
        IoUtil.copy(localInputStream, localBufferedOutputStream);
        localInputStream.close();
        localBufferedOutputStream.close();
      }
      else
      {
        FileUtil.writeFile(paramFile, paramArrayOfByte);
      }
    }
    catch (IOException localIOException)
    {
      IoUtil.closeQuietly(new AutoCloseable[] { localInputStream, localBufferedOutputStream });
      throw new IOException(String.format("Can't save resource '%s' to the file '%s'.", new Object[] { paramString, paramFile }), localIOException);
    }
    finally
    {
      if (paramString != null) {
        validationResultByCacheKey.putIfAbsent(new CacheKey(paramString, paramArrayOfByte, paramClass, null), Boolean.valueOf(true));
      }
    }
  }
  
  private static String toRelativePath(String paramString)
  {
    while (paramString.startsWith(File.separator)) {
      paramString = paramString.substring(File.separator.length());
    }
    while ((SeparatorHolder.SEPARATOR != null) && (paramString.startsWith(SeparatorHolder.SEPARATOR))) {
      paramString = paramString.substring(SeparatorHolder.SEPARATOR.length());
    }
    while (paramString.startsWith("/")) {
      paramString = paramString.substring("/".length());
    }
    while (paramString.startsWith("\\")) {
      paramString = paramString.substring("\\".length());
    }
    return paramString;
  }
  
  private static final class SeparatorHolder
  {
    private static final String SEPARATOR;
    
    static
    {
      String str;
      try
      {
        FileSystem localFileSystem = FileSystems.getDefault();
        str = localFileSystem == null ? null : localFileSystem.getSeparator();
      }
      catch (RuntimeException localRuntimeException)
      {
        ResourceUtil.logger.fatal("Can't get path separator.", localRuntimeException);
        str = null;
      }
      SEPARATOR = str;
    }
  }
  
  private static final class CacheKey
  {
    private final String sha1;
    
    private CacheKey(String paramString, byte[] paramArrayOfByte, Class paramClass)
    {
      String str1 = paramArrayOfByte == null ? "" : DigestUtils.sha1Hex(paramArrayOfByte);
      String str2 = paramClass == null ? "" : paramClass.getCanonicalName();
      this.sha1 = DigestUtils.sha1Hex(paramString + '\001' + str1 + '\002' + str2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      CacheKey localCacheKey = (CacheKey)paramObject;
      return this.sha1.equals(localCacheKey.sha1);
    }
    
    public int hashCode()
    {
      return this.sha1.hashCode();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\resource\ResourceUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */