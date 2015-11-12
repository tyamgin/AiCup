package net.lingala.zip4j.crypto;

import java.util.Arrays;
import net.lingala.zip4j.crypto.PBKDF2.MacBasedPRF;
import net.lingala.zip4j.crypto.PBKDF2.PBKDF2Engine;
import net.lingala.zip4j.crypto.PBKDF2.PBKDF2Parameters;
import net.lingala.zip4j.crypto.engine.AESEngine;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.AESExtraDataRecord;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.util.Raw;

public class AESDecrypter
  implements IDecrypter
{
  private LocalFileHeader localFileHeader;
  private AESEngine aesEngine;
  private MacBasedPRF mac;
  private final int PASSWORD_VERIFIER_LENGTH = 2;
  private int KEY_LENGTH;
  private int MAC_LENGTH;
  private int SALT_LENGTH;
  private byte[] aesKey;
  private byte[] macKey;
  private byte[] derivedPasswordVerifier;
  private byte[] storedMac;
  private int nonce = 1;
  private byte[] iv;
  private byte[] counterBlock;
  private int loopCount = 0;
  
  public AESDecrypter(LocalFileHeader paramLocalFileHeader, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws ZipException
  {
    if (paramLocalFileHeader == null) {
      throw new ZipException("one of the input parameters is null in AESDecryptor Constructor");
    }
    this.localFileHeader = paramLocalFileHeader;
    this.storedMac = null;
    this.iv = new byte[16];
    this.counterBlock = new byte[16];
    init(paramArrayOfByte1, paramArrayOfByte2);
  }
  
  private void init(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws ZipException
  {
    if (this.localFileHeader == null) {
      throw new ZipException("invalid file header in init method of AESDecryptor");
    }
    AESExtraDataRecord localAESExtraDataRecord = this.localFileHeader.getAesExtraDataRecord();
    if (localAESExtraDataRecord == null) {
      throw new ZipException("invalid aes extra data record - in init method of AESDecryptor");
    }
    switch (localAESExtraDataRecord.getAesStrength())
    {
    case 1: 
      this.KEY_LENGTH = 16;
      this.MAC_LENGTH = 16;
      this.SALT_LENGTH = 8;
      break;
    case 2: 
      this.KEY_LENGTH = 24;
      this.MAC_LENGTH = 24;
      this.SALT_LENGTH = 12;
      break;
    case 3: 
      this.KEY_LENGTH = 32;
      this.MAC_LENGTH = 32;
      this.SALT_LENGTH = 16;
      break;
    default: 
      throw new ZipException("invalid aes key strength for file: " + this.localFileHeader.getFileName());
    }
    if ((this.localFileHeader.getPassword() == null) || (this.localFileHeader.getPassword().length <= 0)) {
      throw new ZipException("empty or null password provided for AES Decryptor");
    }
    byte[] arrayOfByte = deriveKey(paramArrayOfByte1, this.localFileHeader.getPassword());
    if ((arrayOfByte == null) || (arrayOfByte.length != this.KEY_LENGTH + this.MAC_LENGTH + 2)) {
      throw new ZipException("invalid derived key");
    }
    this.aesKey = new byte[this.KEY_LENGTH];
    this.macKey = new byte[this.MAC_LENGTH];
    this.derivedPasswordVerifier = new byte[2];
    System.arraycopy(arrayOfByte, 0, this.aesKey, 0, this.KEY_LENGTH);
    System.arraycopy(arrayOfByte, this.KEY_LENGTH, this.macKey, 0, this.MAC_LENGTH);
    System.arraycopy(arrayOfByte, this.KEY_LENGTH + this.MAC_LENGTH, this.derivedPasswordVerifier, 0, 2);
    if (this.derivedPasswordVerifier == null) {
      throw new ZipException("invalid derived password verifier for AES");
    }
    if (!Arrays.equals(paramArrayOfByte2, this.derivedPasswordVerifier)) {
      throw new ZipException("Wrong Password for file: " + this.localFileHeader.getFileName(), 5);
    }
    this.aesEngine = new AESEngine(this.aesKey);
    this.mac = new MacBasedPRF("HmacSHA1");
    this.mac.init(this.macKey);
  }
  
  public int decryptData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ZipException
  {
    if (this.aesEngine == null) {
      throw new ZipException("AES not initialized properly");
    }
    try
    {
      for (int i = paramInt1; i < paramInt1 + paramInt2; i += 16)
      {
        this.loopCount = (i + 16 <= paramInt1 + paramInt2 ? 16 : paramInt1 + paramInt2 - i);
        this.mac.update(paramArrayOfByte, i, this.loopCount);
        Raw.prepareBuffAESIVBytes(this.iv, this.nonce, 16);
        this.aesEngine.processBlock(this.iv, this.counterBlock);
        for (int j = 0; j < this.loopCount; j++) {
          paramArrayOfByte[(i + j)] = ((byte)(paramArrayOfByte[(i + j)] ^ this.counterBlock[j]));
        }
        this.nonce += 1;
      }
      return paramInt2;
    }
    catch (ZipException localZipException)
    {
      throw localZipException;
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
  
  private byte[] deriveKey(byte[] paramArrayOfByte, char[] paramArrayOfChar)
    throws ZipException
  {
    try
    {
      PBKDF2Parameters localPBKDF2Parameters = new PBKDF2Parameters("HmacSHA1", "ISO-8859-1", paramArrayOfByte, 1000);
      PBKDF2Engine localPBKDF2Engine = new PBKDF2Engine(localPBKDF2Parameters);
      byte[] arrayOfByte = localPBKDF2Engine.deriveKey(paramArrayOfChar, this.KEY_LENGTH + this.MAC_LENGTH + 2);
      return arrayOfByte;
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
  
  public int getPasswordVerifierLength()
  {
    return 2;
  }
  
  public int getSaltLength()
  {
    return this.SALT_LENGTH;
  }
  
  public byte[] getCalculatedAuthenticationBytes()
  {
    return this.mac.doFinal();
  }
  
  public void setStoredMac(byte[] paramArrayOfByte)
  {
    this.storedMac = paramArrayOfByte;
  }
  
  public byte[] getStoredMac()
  {
    return this.storedMac;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\crypto\AESDecrypter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */