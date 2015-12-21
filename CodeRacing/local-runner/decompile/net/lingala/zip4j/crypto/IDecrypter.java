package net.lingala.zip4j.crypto;

import net.lingala.zip4j.exception.ZipException;

public abstract interface IDecrypter
{
  public abstract int decryptData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ZipException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\crypto\IDecrypter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */