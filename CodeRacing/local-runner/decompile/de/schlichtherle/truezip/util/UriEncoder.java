package de.schlichtherle.truezip.util;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public final class UriEncoder
{
  public static final Charset UTF8 = Charset.forName("UTF-8");
  private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  private final CharsetEncoder encoder;
  private final boolean encode;
  private final boolean raw;
  private StringBuilder stringBuilder;
  
  public UriEncoder()
  {
    this(UTF8, false);
  }
  
  public UriEncoder(Charset paramCharset, boolean paramBoolean)
  {
    if ((this.encode = null != paramCharset ? 1 : 0) == 0) {
      paramCharset = UTF8;
    }
    this.encoder = paramCharset.newEncoder();
    this.raw = paramBoolean;
  }
  
  boolean isRaw()
  {
    return this.raw;
  }
  
  private static void quote(char paramChar, StringBuilder paramStringBuilder)
  {
    quote(UTF8.encode(CharBuffer.wrap(Character.toString(paramChar))), paramStringBuilder);
  }
  
  private static void quote(ByteBuffer paramByteBuffer, StringBuilder paramStringBuilder)
  {
    while (paramByteBuffer.hasRemaining())
    {
      int i = paramByteBuffer.get();
      paramStringBuilder.append('%');
      paramStringBuilder.append(HEX[(i >> 4 & 0xF)]);
      paramStringBuilder.append(HEX[(i & 0xF)]);
    }
  }
  
  public StringBuilder encode(String paramString, Encoding paramEncoding, StringBuilder paramStringBuilder)
    throws URISyntaxException
  {
    String[] arrayOfString = paramEncoding.escapes;
    CharBuffer localCharBuffer = CharBuffer.wrap(paramString);
    ByteBuffer localByteBuffer = null;
    CharsetEncoder localCharsetEncoder = this.encoder;
    boolean bool = this.encode;
    while (localCharBuffer.hasRemaining())
    {
      localCharBuffer.mark();
      char c = localCharBuffer.get();
      if (c < '')
      {
        String str = arrayOfString[c];
        if ((null != str) && (('%' != c) || (!this.raw)))
        {
          if (null == localByteBuffer)
          {
            if (null == paramStringBuilder)
            {
              if (null == (paramStringBuilder = this.stringBuilder)) {
                paramStringBuilder = this.stringBuilder = new StringBuilder();
              } else {
                paramStringBuilder.setLength(0);
              }
              paramStringBuilder.append(paramString, 0, localCharBuffer.position() - 1);
            }
            localByteBuffer = ByteBuffer.allocate(3);
          }
          paramStringBuilder.append(str);
        }
        else if (null != paramStringBuilder)
        {
          paramStringBuilder.append(c);
        }
      }
      else if ((Character.isISOControl(c)) || (Character.isSpaceChar(c)) || (bool))
      {
        if (null == localByteBuffer)
        {
          if (null == paramStringBuilder)
          {
            if (null == (paramStringBuilder = this.stringBuilder)) {
              paramStringBuilder = this.stringBuilder = new StringBuilder();
            } else {
              paramStringBuilder.setLength(0);
            }
            paramStringBuilder.append(paramString, 0, localCharBuffer.position() - 1);
          }
          localByteBuffer = ByteBuffer.allocate(3);
        }
        int i = localCharBuffer.position();
        localCharBuffer.reset();
        localCharBuffer.limit(i);
        CoderResult localCoderResult;
        if ((CoderResult.UNDERFLOW != (localCoderResult = localCharsetEncoder.reset().encode(localCharBuffer, localByteBuffer, true))) || (CoderResult.UNDERFLOW != (localCoderResult = localCharsetEncoder.flush(localByteBuffer))))
        {
          assert (CoderResult.OVERFLOW != localCoderResult);
          throw new QuotedUriSyntaxException(paramString, localCoderResult.toString());
        }
        localByteBuffer.flip();
        quote(localByteBuffer, paramStringBuilder);
        localByteBuffer.clear();
        localCharBuffer.limit(localCharBuffer.capacity());
      }
      else if (null != paramStringBuilder)
      {
        paramStringBuilder.append(c);
      }
    }
    return null == localByteBuffer ? null : paramStringBuilder;
  }
  
  public static enum Encoding
  {
    ANY("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'(),;$&+=@"),  AUTHORITY("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'(),;$&+=@:[]"),  PATH("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'(),;$&+=@/"),  ABSOLUTE_PATH("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'(),;$&+=@:/"),  QUERY("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'(),;$&+=@:/?"),  FRAGMENT("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'(),;$&+=@:/?");
    
    private final String[] escapes = new String[''];
    
    private Encoding(String paramString1)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      for (char c = '\000'; c < ''; c = (char)(c + '\001')) {
        if (paramString1.indexOf(c) < 0)
        {
          localStringBuilder.setLength(0);
          UriEncoder.quote(c, localStringBuilder);
          this.escapes[c] = localStringBuilder.toString();
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\UriEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */