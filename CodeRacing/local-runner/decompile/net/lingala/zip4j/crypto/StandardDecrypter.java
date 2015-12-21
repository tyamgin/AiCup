package net.lingala.zip4j.crypto;

import net.lingala.zip4j.crypto.engine.ZipCryptoEngine;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class StandardDecrypter
  implements IDecrypter
{
  private FileHeader fileHeader;
  private byte[] crc = new byte[4];
  private ZipCryptoEngine zipCryptoEngine;
  
  public StandardDecrypter(FileHeader paramFileHeader, byte[] paramArrayOfByte)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("one of more of the input parameters were null in StandardDecryptor");
    }
    this.fileHeader = paramFileHeader;
    this.zipCryptoEngine = new ZipCryptoEngine();
    init(paramArrayOfByte);
  }
  
  public int decryptData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ZipException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new ZipException("one of the input parameters were null in standard decrpyt data");
    }
    try
    {
      for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
      {
        int j = paramArrayOfByte[i] & 0xFF;
        j = (j ^ this.zipCryptoEngine.decryptByte()) & 0xFF;
        this.zipCryptoEngine.updateKeys((byte)j);
        paramArrayOfByte[i] = ((byte)j);
      }
      return paramInt2;
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
  
  public void init(byte[] paramArrayOfByte)
    throws ZipException
  {
    byte[] arrayOfByte = this.fileHeader.getCrcBuff();
    this.crc[3] = ((byte)(arrayOfByte[3] & 0xFF));
    this.crc[2] = ((byte)(arrayOfByte[3] >> 8 & 0xFF));
    this.crc[1] = ((byte)(arrayOfByte[3] >> 16 & 0xFF));
    this.crc[0] = ((byte)(arrayOfByte[3] >> 24 & 0xFF));
    if ((this.crc[2] > 0) || (this.crc[1] > 0) || (this.crc[0] > 0)) {
      throw new IllegalStateException("Invalid CRC in File Header");
    }
    if ((this.fileHeader.getPassword() == null) || (this.fileHeader.getPassword().length <= 0)) {
      throw new ZipException("Wrong password!", 5);
    }
    this.zipCryptoEngine.initKeys(this.fileHeader.getPassword());
    try
    {
      int i = paramArrayOfByte[0];
      for (int j = 0; j < 12; j++)
      {
        this.zipCryptoEngine.updateKeys((byte)(i ^ this.zipCryptoEngine.decryptByte()));
        if (j + 1 != 12) {
          i = paramArrayOfByte[(j + 1)];
        }
      }
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\crypto\StandardDecrypter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */