package net.lingala.zip4j.crypto.PBKDF2;

public class PBKDF2Parameters
{
  protected byte[] salt;
  protected int iterationCount;
  protected String hashAlgorithm;
  protected String hashCharset;
  protected byte[] derivedKey;
  
  public PBKDF2Parameters()
  {
    this.hashAlgorithm = null;
    this.hashCharset = "UTF-8";
    this.salt = null;
    this.iterationCount = 1000;
    this.derivedKey = null;
  }
  
  public PBKDF2Parameters(String paramString1, String paramString2, byte[] paramArrayOfByte, int paramInt)
  {
    this.hashAlgorithm = paramString1;
    this.hashCharset = paramString2;
    this.salt = paramArrayOfByte;
    this.iterationCount = paramInt;
    this.derivedKey = null;
  }
  
  public int getIterationCount()
  {
    return this.iterationCount;
  }
  
  public byte[] getSalt()
  {
    return this.salt;
  }
  
  public String getHashAlgorithm()
  {
    return this.hashAlgorithm;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\crypto\PBKDF2\PBKDF2Parameters.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */