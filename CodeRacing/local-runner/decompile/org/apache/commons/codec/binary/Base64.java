package org.apache.commons.codec.binary;

public class Base64
  extends BaseNCodec
{
  static final byte[] CHUNK_SEPARATOR = { 13, 10 };
  private static final byte[] STANDARD_ENCODE_TABLE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
  private static final byte[] URL_SAFE_ENCODE_TABLE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
  private static final byte[] DECODE_TABLE = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
  private final byte[] encodeTable;
  private final byte[] decodeTable = DECODE_TABLE;
  private final byte[] lineSeparator;
  private final int decodeSize;
  private final int encodeSize;
  
  public Base64()
  {
    this(0);
  }
  
  public Base64(boolean paramBoolean)
  {
    this(76, CHUNK_SEPARATOR, paramBoolean);
  }
  
  public Base64(int paramInt)
  {
    this(paramInt, CHUNK_SEPARATOR);
  }
  
  public Base64(int paramInt, byte[] paramArrayOfByte)
  {
    this(paramInt, paramArrayOfByte, false);
  }
  
  public Base64(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    super(3, 4, paramInt, paramArrayOfByte == null ? 0 : paramArrayOfByte.length);
    if (paramArrayOfByte != null)
    {
      if (containsAlphabetOrPad(paramArrayOfByte))
      {
        String str = StringUtils.newStringUtf8(paramArrayOfByte);
        throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + str + "]");
      }
      if (paramInt > 0)
      {
        this.encodeSize = (4 + paramArrayOfByte.length);
        this.lineSeparator = new byte[paramArrayOfByte.length];
        System.arraycopy(paramArrayOfByte, 0, this.lineSeparator, 0, paramArrayOfByte.length);
      }
      else
      {
        this.encodeSize = 4;
        this.lineSeparator = null;
      }
    }
    else
    {
      this.encodeSize = 4;
      this.lineSeparator = null;
    }
    this.decodeSize = (this.encodeSize - 1);
    this.encodeTable = (paramBoolean ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE);
  }
  
  void encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, BaseNCodec.Context paramContext)
  {
    if (paramContext.eof) {
      return;
    }
    if (paramInt2 < 0)
    {
      paramContext.eof = true;
      if ((0 == paramContext.modulus) && (this.lineLength == 0)) {
        return;
      }
      byte[] arrayOfByte1 = ensureBufferSize(this.encodeSize, paramContext);
      int j = paramContext.pos;
      switch (paramContext.modulus)
      {
      case 0: 
        break;
      case 1: 
        arrayOfByte1[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea >> 2 & 0x3F)];
        arrayOfByte1[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea << 4 & 0x3F)];
        if (this.encodeTable == STANDARD_ENCODE_TABLE)
        {
          arrayOfByte1[(paramContext.pos++)] = this.pad;
          arrayOfByte1[(paramContext.pos++)] = this.pad;
        }
        break;
      case 2: 
        arrayOfByte1[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea >> 10 & 0x3F)];
        arrayOfByte1[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea >> 4 & 0x3F)];
        arrayOfByte1[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea << 2 & 0x3F)];
        if (this.encodeTable == STANDARD_ENCODE_TABLE) {
          arrayOfByte1[(paramContext.pos++)] = this.pad;
        }
        break;
      default: 
        throw new IllegalStateException("Impossible modulus " + paramContext.modulus);
      }
      paramContext.currentLinePos += paramContext.pos - j;
      if ((this.lineLength > 0) && (paramContext.currentLinePos > 0))
      {
        System.arraycopy(this.lineSeparator, 0, arrayOfByte1, paramContext.pos, this.lineSeparator.length);
        paramContext.pos += this.lineSeparator.length;
      }
    }
    else
    {
      for (int i = 0; i < paramInt2; i++)
      {
        byte[] arrayOfByte2 = ensureBufferSize(this.encodeSize, paramContext);
        paramContext.modulus = ((paramContext.modulus + 1) % 3);
        int k = paramArrayOfByte[(paramInt1++)];
        if (k < 0) {
          k += 256;
        }
        paramContext.ibitWorkArea = ((paramContext.ibitWorkArea << 8) + k);
        if (0 == paramContext.modulus)
        {
          arrayOfByte2[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea >> 18 & 0x3F)];
          arrayOfByte2[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea >> 12 & 0x3F)];
          arrayOfByte2[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea >> 6 & 0x3F)];
          arrayOfByte2[(paramContext.pos++)] = this.encodeTable[(paramContext.ibitWorkArea & 0x3F)];
          paramContext.currentLinePos += 4;
          if ((this.lineLength > 0) && (this.lineLength <= paramContext.currentLinePos))
          {
            System.arraycopy(this.lineSeparator, 0, arrayOfByte2, paramContext.pos, this.lineSeparator.length);
            paramContext.pos += this.lineSeparator.length;
            paramContext.currentLinePos = 0;
          }
        }
      }
    }
  }
  
  public static String encodeBase64URLSafeString(byte[] paramArrayOfByte)
  {
    return StringUtils.newStringUtf8(encodeBase64(paramArrayOfByte, false, true));
  }
  
  public static byte[] encodeBase64(byte[] paramArrayOfByte, boolean paramBoolean1, boolean paramBoolean2)
  {
    return encodeBase64(paramArrayOfByte, paramBoolean1, paramBoolean2, Integer.MAX_VALUE);
  }
  
  public static byte[] encodeBase64(byte[] paramArrayOfByte, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return paramArrayOfByte;
    }
    Base64 localBase64 = paramBoolean1 ? new Base64(paramBoolean2) : new Base64(0, CHUNK_SEPARATOR, paramBoolean2);
    long l = localBase64.getEncodedLength(paramArrayOfByte);
    if (l > paramInt) {
      throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + l + ") than the specified maximum size of " + paramInt);
    }
    return localBase64.encode(paramArrayOfByte);
  }
  
  protected boolean isInAlphabet(byte paramByte)
  {
    return (paramByte >= 0) && (paramByte < this.decodeTable.length) && (this.decodeTable[paramByte] != -1);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\codec\binary\Base64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */