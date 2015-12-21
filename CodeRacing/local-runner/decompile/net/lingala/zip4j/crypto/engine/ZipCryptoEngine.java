package net.lingala.zip4j.crypto.engine;

public class ZipCryptoEngine
{
  private final int[] keys = new int[3];
  private static final int[] CRC_TABLE = new int['Ā'];
  
  public void initKeys(char[] paramArrayOfChar)
  {
    this.keys[0] = 305419896;
    this.keys[1] = 591751049;
    this.keys[2] = 878082192;
    for (int i = 0; i < paramArrayOfChar.length; i++) {
      updateKeys((byte)(paramArrayOfChar[i] & 0xFF));
    }
  }
  
  public void updateKeys(byte paramByte)
  {
    this.keys[0] = crc32(this.keys[0], paramByte);
    this.keys[1] += (this.keys[0] & 0xFF);
    this.keys[1] = (this.keys[1] * 134775813 + 1);
    this.keys[2] = crc32(this.keys[2], (byte)(this.keys[1] >> 24));
  }
  
  private int crc32(int paramInt, byte paramByte)
  {
    return paramInt >>> 8 ^ CRC_TABLE[((paramInt ^ paramByte) & 0xFF)];
  }
  
  public byte decryptByte()
  {
    int i = this.keys[2] | 0x2;
    return (byte)(i * (i ^ 0x1) >>> 8);
  }
  
  static
  {
    for (int i = 0; i < 256; i++)
    {
      int j = i;
      for (int k = 0; k < 8; k++) {
        if ((j & 0x1) == 1) {
          j = j >>> 1 ^ 0xEDB88320;
        } else {
          j >>>= 1;
        }
      }
      CRC_TABLE[i] = j;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\crypto\engine\ZipCryptoEngine.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */