package net.lingala.zip4j.crypto.PBKDF2;

abstract interface PRF
{
  public abstract void init(byte[] paramArrayOfByte);
  
  public abstract byte[] doFinal(byte[] paramArrayOfByte);
  
  public abstract int getHLen();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\crypto\PBKDF2\PRF.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */