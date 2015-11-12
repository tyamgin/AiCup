package net.lingala.zip4j.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import net.lingala.zip4j.crypto.AESDecrypter;
import net.lingala.zip4j.crypto.IDecrypter;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.unzip.UnzipEngine;

public class PartInputStream
  extends BaseInputStream
{
  private RandomAccessFile raf;
  private long bytesRead;
  private long length;
  private UnzipEngine unzipEngine;
  private IDecrypter decrypter;
  private byte[] oneByteBuff = new byte[1];
  private byte[] aesBlockByte = new byte[16];
  private int aesBytesReturned = 0;
  private boolean isAESEncryptedFile = false;
  private int count = -1;
  
  public PartInputStream(RandomAccessFile paramRandomAccessFile, long paramLong1, long paramLong2, UnzipEngine paramUnzipEngine)
  {
    this.raf = paramRandomAccessFile;
    this.unzipEngine = paramUnzipEngine;
    this.decrypter = paramUnzipEngine.getDecrypter();
    this.bytesRead = 0L;
    this.length = paramLong2;
    this.isAESEncryptedFile = ((paramUnzipEngine.getFileHeader().isEncrypted()) && (paramUnzipEngine.getFileHeader().getEncryptionMethod() == 99));
  }
  
  public int available()
  {
    long l = this.length - this.bytesRead;
    if (l > 2147483647L) {
      return Integer.MAX_VALUE;
    }
    return (int)l;
  }
  
  public int read()
    throws IOException
  {
    if (this.bytesRead >= this.length) {
      return -1;
    }
    if (this.isAESEncryptedFile)
    {
      if ((this.aesBytesReturned == 0) || (this.aesBytesReturned == 16))
      {
        if (read(this.aesBlockByte) == -1) {
          return -1;
        }
        this.aesBytesReturned = 0;
      }
      return this.aesBlockByte[(this.aesBytesReturned++)] & 0xFF;
    }
    return read(this.oneByteBuff, 0, 1) == -1 ? -1 : this.oneByteBuff[0] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 > this.length - this.bytesRead)
    {
      paramInt2 = (int)(this.length - this.bytesRead);
      if (paramInt2 == 0)
      {
        checkAndReadAESMacBytes();
        return -1;
      }
    }
    if (((this.unzipEngine.getDecrypter() instanceof AESDecrypter)) && (this.bytesRead + paramInt2 < this.length) && (paramInt2 % 16 != 0)) {
      paramInt2 -= paramInt2 % 16;
    }
    synchronized (this.raf)
    {
      this.count = this.raf.read(paramArrayOfByte, paramInt1, paramInt2);
      if ((this.count < paramInt2) && (this.unzipEngine.getZipModel().isSplitArchive()))
      {
        this.raf.close();
        this.raf = this.unzipEngine.startNextSplitFile();
        if (this.count < 0) {
          this.count = 0;
        }
        int i = this.raf.read(paramArrayOfByte, this.count, paramInt2 - this.count);
        if (i > 0) {
          this.count += i;
        }
      }
    }
    if (this.count > 0)
    {
      if (this.decrypter != null) {
        try
        {
          this.decrypter.decryptData(paramArrayOfByte, paramInt1, this.count);
        }
        catch (ZipException localZipException)
        {
          throw new IOException(localZipException.getMessage());
        }
      }
      this.bytesRead += this.count;
    }
    if (this.bytesRead >= this.length) {
      checkAndReadAESMacBytes();
    }
    return this.count;
  }
  
  protected void checkAndReadAESMacBytes()
    throws IOException
  {
    if ((this.isAESEncryptedFile) && (this.decrypter != null) && ((this.decrypter instanceof AESDecrypter)))
    {
      if (((AESDecrypter)this.decrypter).getStoredMac() != null) {
        return;
      }
      byte[] arrayOfByte = new byte[10];
      int i = -1;
      i = this.raf.read(arrayOfByte);
      if (i != 10) {
        if (this.unzipEngine.getZipModel().isSplitArchive())
        {
          this.raf.close();
          this.raf = this.unzipEngine.startNextSplitFile();
          int j = this.raf.read(arrayOfByte, i, 10 - i);
          i += j;
        }
        else
        {
          throw new IOException("Error occured while reading stored AES authentication bytes");
        }
      }
      ((AESDecrypter)this.unzipEngine.getDecrypter()).setStoredMac(arrayOfByte);
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException();
    }
    if (paramLong > this.length - this.bytesRead) {
      paramLong = this.length - this.bytesRead;
    }
    this.bytesRead += paramLong;
    return paramLong;
  }
  
  public void close()
    throws IOException
  {
    this.raf.close();
  }
  
  public UnzipEngine getUnzipEngine()
  {
    return this.unzipEngine;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\io\PartInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */