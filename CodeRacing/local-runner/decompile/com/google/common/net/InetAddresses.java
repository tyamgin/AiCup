package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Beta
public final class InetAddresses
{
  private static final int IPV4_PART_COUNT = 4;
  private static final int IPV6_PART_COUNT = 8;
  private static final Inet4Address LOOPBACK4 = (Inet4Address)forString("127.0.0.1");
  private static final Inet4Address ANY4 = (Inet4Address)forString("0.0.0.0");
  
  private static Inet4Address getInet4Address(byte[] paramArrayOfByte)
  {
    Preconditions.checkArgument(paramArrayOfByte.length == 4, "Byte array has invalid length for an IPv4 address: %s != 4.", new Object[] { Integer.valueOf(paramArrayOfByte.length) });
    return (Inet4Address)bytesToInetAddress(paramArrayOfByte);
  }
  
  public static InetAddress forString(String paramString)
  {
    byte[] arrayOfByte = ipStringToBytes(paramString);
    if (arrayOfByte == null) {
      throw new IllegalArgumentException(String.format("'%s' is not an IP string literal.", new Object[] { paramString }));
    }
    return bytesToInetAddress(arrayOfByte);
  }
  
  public static boolean isInetAddress(String paramString)
  {
    return ipStringToBytes(paramString) != null;
  }
  
  private static byte[] ipStringToBytes(String paramString)
  {
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramString.length(); k++)
    {
      char c = paramString.charAt(k);
      if (c == '.')
      {
        j = 1;
      }
      else if (c == ':')
      {
        if (j != 0) {
          return null;
        }
        i = 1;
      }
      else if (Character.digit(c, 16) == -1)
      {
        return null;
      }
    }
    if (i != 0)
    {
      if (j != 0)
      {
        paramString = convertDottedQuadToHex(paramString);
        if (paramString == null) {
          return null;
        }
      }
      return textToNumericFormatV6(paramString);
    }
    if (j != 0) {
      return textToNumericFormatV4(paramString);
    }
    return null;
  }
  
  private static byte[] textToNumericFormatV4(String paramString)
  {
    String[] arrayOfString = paramString.split("\\.", 5);
    if (arrayOfString.length != 4) {
      return null;
    }
    byte[] arrayOfByte = new byte[4];
    try
    {
      for (int i = 0; i < arrayOfByte.length; i++) {
        arrayOfByte[i] = parseOctet(arrayOfString[i]);
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return null;
    }
    return arrayOfByte;
  }
  
  private static byte[] textToNumericFormatV6(String paramString)
  {
    String[] arrayOfString = paramString.split(":", 10);
    if ((arrayOfString.length < 3) || (arrayOfString.length > 9)) {
      return null;
    }
    int i = -1;
    for (int j = 1; j < arrayOfString.length - 1; j++) {
      if (arrayOfString[j].length() == 0)
      {
        if (i >= 0) {
          return null;
        }
        i = j;
      }
    }
    int k;
    if (i >= 0)
    {
      j = i;
      k = arrayOfString.length - i - 1;
      if (arrayOfString[0].length() == 0)
      {
        j--;
        if (j != 0) {
          return null;
        }
      }
      if (arrayOfString[(arrayOfString.length - 1)].length() == 0)
      {
        k--;
        if (k != 0) {
          return null;
        }
      }
    }
    else
    {
      j = arrayOfString.length;
      k = 0;
    }
    int m = 8 - (j + k);
    if (i >= 0 ? m < 1 : m != 0) {
      return null;
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(16);
    try
    {
      for (int n = 0; n < j; n++) {
        localByteBuffer.putShort(parseHextet(arrayOfString[n]));
      }
      for (n = 0; n < m; n++) {
        localByteBuffer.putShort((short)0);
      }
      for (n = k; n > 0; n--) {
        localByteBuffer.putShort(parseHextet(arrayOfString[(arrayOfString.length - n)]));
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return null;
    }
    return localByteBuffer.array();
  }
  
  private static String convertDottedQuadToHex(String paramString)
  {
    int i = paramString.lastIndexOf(':');
    String str1 = paramString.substring(0, i + 1);
    String str2 = paramString.substring(i + 1);
    byte[] arrayOfByte = textToNumericFormatV4(str2);
    if (arrayOfByte == null) {
      return null;
    }
    String str3 = Integer.toHexString((arrayOfByte[0] & 0xFF) << 8 | arrayOfByte[1] & 0xFF);
    String str4 = Integer.toHexString((arrayOfByte[2] & 0xFF) << 8 | arrayOfByte[3] & 0xFF);
    return str1 + str3 + ":" + str4;
  }
  
  private static byte parseOctet(String paramString)
  {
    int i = Integer.parseInt(paramString);
    if ((i > 255) || ((paramString.startsWith("0")) && (paramString.length() > 1))) {
      throw new NumberFormatException();
    }
    return (byte)i;
  }
  
  private static short parseHextet(String paramString)
  {
    int i = Integer.parseInt(paramString, 16);
    if (i > 65535) {
      throw new NumberFormatException();
    }
    return (short)i;
  }
  
  private static InetAddress bytesToInetAddress(byte[] paramArrayOfByte)
  {
    try
    {
      return InetAddress.getByAddress(paramArrayOfByte);
    }
    catch (UnknownHostException localUnknownHostException)
    {
      throw new AssertionError(localUnknownHostException);
    }
  }
  
  public static String toAddrString(InetAddress paramInetAddress)
  {
    Preconditions.checkNotNull(paramInetAddress);
    if ((paramInetAddress instanceof Inet4Address)) {
      return paramInetAddress.getHostAddress();
    }
    Preconditions.checkArgument(paramInetAddress instanceof Inet6Address);
    byte[] arrayOfByte = paramInetAddress.getAddress();
    int[] arrayOfInt = new int[8];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = Ints.fromBytes(0, 0, arrayOfByte[(2 * i)], arrayOfByte[(2 * i + 1)]);
    }
    compressLongestRunOfZeroes(arrayOfInt);
    return hextetsToIPv6String(arrayOfInt);
  }
  
  private static void compressLongestRunOfZeroes(int[] paramArrayOfInt)
  {
    int i = -1;
    int j = -1;
    int k = -1;
    for (int m = 0; m < paramArrayOfInt.length + 1; m++) {
      if ((m < paramArrayOfInt.length) && (paramArrayOfInt[m] == 0))
      {
        if (k < 0) {
          k = m;
        }
      }
      else if (k >= 0)
      {
        int n = m - k;
        if (n > j)
        {
          i = k;
          j = n;
        }
        k = -1;
      }
    }
    if (j >= 2) {
      Arrays.fill(paramArrayOfInt, i, i + j, -1);
    }
  }
  
  private static String hextetsToIPv6String(int[] paramArrayOfInt)
  {
    StringBuilder localStringBuilder = new StringBuilder(39);
    int i = 0;
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      int k = paramArrayOfInt[j] >= 0 ? 1 : 0;
      if (k != 0)
      {
        if (i != 0) {
          localStringBuilder.append(':');
        }
        localStringBuilder.append(Integer.toHexString(paramArrayOfInt[j]));
      }
      else if ((j == 0) || (i != 0))
      {
        localStringBuilder.append("::");
      }
      i = k;
    }
    return localStringBuilder.toString();
  }
  
  public static String toUriString(InetAddress paramInetAddress)
  {
    if ((paramInetAddress instanceof Inet6Address)) {
      return "[" + toAddrString(paramInetAddress) + "]";
    }
    return toAddrString(paramInetAddress);
  }
  
  public static InetAddress forUriString(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    String str;
    int i;
    if ((paramString.startsWith("[")) && (paramString.endsWith("]")))
    {
      str = paramString.substring(1, paramString.length() - 1);
      i = 16;
    }
    else
    {
      str = paramString;
      i = 4;
    }
    byte[] arrayOfByte = ipStringToBytes(str);
    if ((arrayOfByte == null) || (arrayOfByte.length != i)) {
      throw new IllegalArgumentException(String.format("Not a valid URI IP literal: '%s'", new Object[] { paramString }));
    }
    return bytesToInetAddress(arrayOfByte);
  }
  
  public static boolean isUriInetAddress(String paramString)
  {
    try
    {
      forUriString(paramString);
      return true;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return false;
  }
  
  public static boolean isCompatIPv4Address(Inet6Address paramInet6Address)
  {
    if (!paramInet6Address.isIPv4CompatibleAddress()) {
      return false;
    }
    byte[] arrayOfByte = paramInet6Address.getAddress();
    return (arrayOfByte[12] != 0) || (arrayOfByte[13] != 0) || (arrayOfByte[14] != 0) || ((arrayOfByte[15] != 0) && (arrayOfByte[15] != 1));
  }
  
  public static Inet4Address getCompatIPv4Address(Inet6Address paramInet6Address)
  {
    Preconditions.checkArgument(isCompatIPv4Address(paramInet6Address), "Address '%s' is not IPv4-compatible.", new Object[] { toAddrString(paramInet6Address) });
    return getInet4Address(Arrays.copyOfRange(paramInet6Address.getAddress(), 12, 16));
  }
  
  public static boolean is6to4Address(Inet6Address paramInet6Address)
  {
    byte[] arrayOfByte = paramInet6Address.getAddress();
    return (arrayOfByte[0] == 32) && (arrayOfByte[1] == 2);
  }
  
  public static Inet4Address get6to4IPv4Address(Inet6Address paramInet6Address)
  {
    Preconditions.checkArgument(is6to4Address(paramInet6Address), "Address '%s' is not a 6to4 address.", new Object[] { toAddrString(paramInet6Address) });
    return getInet4Address(Arrays.copyOfRange(paramInet6Address.getAddress(), 2, 6));
  }
  
  public static boolean isTeredoAddress(Inet6Address paramInet6Address)
  {
    byte[] arrayOfByte = paramInet6Address.getAddress();
    return (arrayOfByte[0] == 32) && (arrayOfByte[1] == 1) && (arrayOfByte[2] == 0) && (arrayOfByte[3] == 0);
  }
  
  public static TeredoInfo getTeredoInfo(Inet6Address paramInet6Address)
  {
    Preconditions.checkArgument(isTeredoAddress(paramInet6Address), "Address '%s' is not a Teredo address.", new Object[] { toAddrString(paramInet6Address) });
    byte[] arrayOfByte1 = paramInet6Address.getAddress();
    Inet4Address localInet4Address1 = getInet4Address(Arrays.copyOfRange(arrayOfByte1, 4, 8));
    int i = ByteStreams.newDataInput(arrayOfByte1, 8).readShort() & 0xFFFF;
    int j = (ByteStreams.newDataInput(arrayOfByte1, 10).readShort() ^ 0xFFFFFFFF) & 0xFFFF;
    byte[] arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, 12, 16);
    for (int k = 0; k < arrayOfByte2.length; k++) {
      arrayOfByte2[k] = ((byte)(arrayOfByte2[k] ^ 0xFFFFFFFF));
    }
    Inet4Address localInet4Address2 = getInet4Address(arrayOfByte2);
    return new TeredoInfo(localInet4Address1, localInet4Address2, j, i);
  }
  
  public static boolean isIsatapAddress(Inet6Address paramInet6Address)
  {
    if (isTeredoAddress(paramInet6Address)) {
      return false;
    }
    byte[] arrayOfByte = paramInet6Address.getAddress();
    if ((arrayOfByte[8] | 0x3) != 3) {
      return false;
    }
    return (arrayOfByte[9] == 0) && (arrayOfByte[10] == 94) && (arrayOfByte[11] == -2);
  }
  
  public static Inet4Address getIsatapIPv4Address(Inet6Address paramInet6Address)
  {
    Preconditions.checkArgument(isIsatapAddress(paramInet6Address), "Address '%s' is not an ISATAP address.", new Object[] { toAddrString(paramInet6Address) });
    return getInet4Address(Arrays.copyOfRange(paramInet6Address.getAddress(), 12, 16));
  }
  
  public static boolean hasEmbeddedIPv4ClientAddress(Inet6Address paramInet6Address)
  {
    return (isCompatIPv4Address(paramInet6Address)) || (is6to4Address(paramInet6Address)) || (isTeredoAddress(paramInet6Address));
  }
  
  public static Inet4Address getEmbeddedIPv4ClientAddress(Inet6Address paramInet6Address)
  {
    if (isCompatIPv4Address(paramInet6Address)) {
      return getCompatIPv4Address(paramInet6Address);
    }
    if (is6to4Address(paramInet6Address)) {
      return get6to4IPv4Address(paramInet6Address);
    }
    if (isTeredoAddress(paramInet6Address)) {
      return getTeredoInfo(paramInet6Address).getClient();
    }
    throw new IllegalArgumentException(String.format("'%s' has no embedded IPv4 address.", new Object[] { toAddrString(paramInet6Address) }));
  }
  
  public static boolean isMappedIPv4Address(String paramString)
  {
    byte[] arrayOfByte = ipStringToBytes(paramString);
    if ((arrayOfByte != null) && (arrayOfByte.length == 16))
    {
      for (int i = 0; i < 10; i++) {
        if (arrayOfByte[i] != 0) {
          return false;
        }
      }
      for (i = 10; i < 12; i++) {
        if (arrayOfByte[i] != -1) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public static Inet4Address getCoercedIPv4Address(InetAddress paramInetAddress)
  {
    if ((paramInetAddress instanceof Inet4Address)) {
      return (Inet4Address)paramInetAddress;
    }
    byte[] arrayOfByte = paramInetAddress.getAddress();
    int i = 1;
    for (int j = 0; j < 15; j++) {
      if (arrayOfByte[j] != 0)
      {
        i = 0;
        break;
      }
    }
    if ((i != 0) && (arrayOfByte[15] == 1)) {
      return LOOPBACK4;
    }
    if ((i != 0) && (arrayOfByte[15] == 0)) {
      return ANY4;
    }
    Inet6Address localInet6Address = (Inet6Address)paramInetAddress;
    long l = 0L;
    if (hasEmbeddedIPv4ClientAddress(localInet6Address)) {
      l = getEmbeddedIPv4ClientAddress(localInet6Address).hashCode();
    } else {
      l = ByteBuffer.wrap(localInet6Address.getAddress(), 0, 8).getLong();
    }
    int k = Hashing.murmur3_32().hashLong(l).asInt();
    k |= 0xE0000000;
    if (k == -1) {
      k = -2;
    }
    return getInet4Address(Ints.toByteArray(k));
  }
  
  public static int coerceToInteger(InetAddress paramInetAddress)
  {
    return ByteStreams.newDataInput(getCoercedIPv4Address(paramInetAddress).getAddress()).readInt();
  }
  
  public static Inet4Address fromInteger(int paramInt)
  {
    return getInet4Address(Ints.toByteArray(paramInt));
  }
  
  public static InetAddress fromLittleEndianByteArray(byte[] paramArrayOfByte)
    throws UnknownHostException
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      arrayOfByte[i] = paramArrayOfByte[(paramArrayOfByte.length - i - 1)];
    }
    return InetAddress.getByAddress(arrayOfByte);
  }
  
  public static InetAddress increment(InetAddress paramInetAddress)
  {
    byte[] arrayOfByte = paramInetAddress.getAddress();
    for (int i = arrayOfByte.length - 1; (i >= 0) && (arrayOfByte[i] == -1); i--) {
      arrayOfByte[i] = 0;
    }
    Preconditions.checkArgument(i >= 0, "Incrementing %s would wrap.", new Object[] { paramInetAddress });
    int tmp55_54 = i;
    byte[] tmp55_53 = arrayOfByte;
    tmp55_53[tmp55_54] = ((byte)(tmp55_53[tmp55_54] + 1));
    return bytesToInetAddress(arrayOfByte);
  }
  
  public static boolean isMaximum(InetAddress paramInetAddress)
  {
    byte[] arrayOfByte = paramInetAddress.getAddress();
    for (int i = 0; i < arrayOfByte.length; i++) {
      if (arrayOfByte[i] != -1) {
        return false;
      }
    }
    return true;
  }
  
  @Beta
  public static final class TeredoInfo
  {
    private final Inet4Address server;
    private final Inet4Address client;
    private final int port;
    private final int flags;
    
    public TeredoInfo(Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, int paramInt1, int paramInt2)
    {
      Preconditions.checkArgument((paramInt1 >= 0) && (paramInt1 <= 65535), "port '%s' is out of range (0 <= port <= 0xffff)", new Object[] { Integer.valueOf(paramInt1) });
      Preconditions.checkArgument((paramInt2 >= 0) && (paramInt2 <= 65535), "flags '%s' is out of range (0 <= flags <= 0xffff)", new Object[] { Integer.valueOf(paramInt2) });
      this.server = ((Inet4Address)Objects.firstNonNull(paramInet4Address1, InetAddresses.ANY4));
      this.client = ((Inet4Address)Objects.firstNonNull(paramInet4Address2, InetAddresses.ANY4));
      this.port = paramInt1;
      this.flags = paramInt2;
    }
    
    public Inet4Address getServer()
    {
      return this.server;
    }
    
    public Inet4Address getClient()
    {
      return this.client;
    }
    
    public int getPort()
    {
      return this.port;
    }
    
    public int getFlags()
    {
      return this.flags;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\net\InetAddresses.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */