package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.RoundingMode;
import java.util.Arrays;

@Beta
@GwtCompatible(emulated=true)
public abstract class BaseEncoding
{
  private static final BaseEncoding BASE64 = new StandardBaseEncoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", Character.valueOf('='));
  private static final BaseEncoding BASE64_URL = new StandardBaseEncoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", Character.valueOf('='));
  private static final BaseEncoding BASE32 = new StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", Character.valueOf('='));
  private static final BaseEncoding BASE32_HEX = new StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", Character.valueOf('='));
  private static final BaseEncoding BASE16 = new StandardBaseEncoding("base16()", "0123456789ABCDEF", null);
  
  public String encode(byte[] paramArrayOfByte)
  {
    return encode((byte[])Preconditions.checkNotNull(paramArrayOfByte), 0, paramArrayOfByte.length);
  }
  
  public final String encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    Preconditions.checkNotNull(paramArrayOfByte);
    Preconditions.checkPositionIndexes(paramInt1, paramInt1 + paramInt2, paramArrayOfByte.length);
    GwtWorkarounds.CharOutput localCharOutput = GwtWorkarounds.stringBuilderOutput(maxEncodedSize(paramInt2));
    GwtWorkarounds.ByteOutput localByteOutput = encodingStream(localCharOutput);
    try
    {
      for (int i = 0; i < paramInt2; i++) {
        localByteOutput.write(paramArrayOfByte[(paramInt1 + i)]);
      }
      localByteOutput.close();
    }
    catch (IOException localIOException)
    {
      throw new AssertionError("impossible");
    }
    return localCharOutput.toString();
  }
  
  @GwtIncompatible("Writer,OutputStream")
  public final OutputStream encodingStream(Writer paramWriter)
  {
    return GwtWorkarounds.asOutputStream(encodingStream(GwtWorkarounds.asCharOutput(paramWriter)));
  }
  
  @GwtIncompatible("Writer,OutputStream")
  public final OutputSupplier encodingStream(final OutputSupplier paramOutputSupplier)
  {
    Preconditions.checkNotNull(paramOutputSupplier);
    new OutputSupplier()
    {
      public OutputStream getOutput()
        throws IOException
      {
        return BaseEncoding.this.encodingStream((Writer)paramOutputSupplier.getOutput());
      }
    };
  }
  
  @GwtIncompatible("ByteSink,CharSink")
  public final ByteSink encodingSink(final CharSink paramCharSink)
  {
    Preconditions.checkNotNull(paramCharSink);
    new ByteSink()
    {
      public OutputStream openStream()
        throws IOException
      {
        return BaseEncoding.this.encodingStream(paramCharSink.openStream());
      }
    };
  }
  
  private static byte[] extract(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt == paramArrayOfByte.length) {
      return paramArrayOfByte;
    }
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramInt);
    return arrayOfByte;
  }
  
  public final byte[] decode(CharSequence paramCharSequence)
  {
    paramCharSequence = padding().trimTrailingFrom(paramCharSequence);
    GwtWorkarounds.ByteInput localByteInput = decodingStream(GwtWorkarounds.asCharInput(paramCharSequence));
    byte[] arrayOfByte = new byte[maxDecodedSize(paramCharSequence.length())];
    int i = 0;
    try
    {
      for (int j = localByteInput.read(); j != -1; j = localByteInput.read()) {
        arrayOfByte[(i++)] = ((byte)j);
      }
    }
    catch (IOException localIOException)
    {
      throw new IllegalArgumentException(localIOException);
    }
    return extract(arrayOfByte, i);
  }
  
  @GwtIncompatible("Reader,InputStream")
  public final InputStream decodingStream(Reader paramReader)
  {
    return GwtWorkarounds.asInputStream(decodingStream(GwtWorkarounds.asCharInput(paramReader)));
  }
  
  @GwtIncompatible("Reader,InputStream")
  public final InputSupplier decodingStream(final InputSupplier paramInputSupplier)
  {
    Preconditions.checkNotNull(paramInputSupplier);
    new InputSupplier()
    {
      public InputStream getInput()
        throws IOException
      {
        return BaseEncoding.this.decodingStream((Reader)paramInputSupplier.getInput());
      }
    };
  }
  
  @GwtIncompatible("ByteSource,CharSource")
  public final ByteSource decodingSource(final CharSource paramCharSource)
  {
    Preconditions.checkNotNull(paramCharSource);
    new ByteSource()
    {
      public InputStream openStream()
        throws IOException
      {
        return BaseEncoding.this.decodingStream(paramCharSource.openStream());
      }
    };
  }
  
  abstract int maxEncodedSize(int paramInt);
  
  abstract GwtWorkarounds.ByteOutput encodingStream(GwtWorkarounds.CharOutput paramCharOutput);
  
  abstract int maxDecodedSize(int paramInt);
  
  abstract GwtWorkarounds.ByteInput decodingStream(GwtWorkarounds.CharInput paramCharInput);
  
  abstract CharMatcher padding();
  
  public abstract BaseEncoding omitPadding();
  
  public abstract BaseEncoding withPadChar(char paramChar);
  
  public abstract BaseEncoding withSeparator(String paramString, int paramInt);
  
  public abstract BaseEncoding upperCase();
  
  public abstract BaseEncoding lowerCase();
  
  public static BaseEncoding base64()
  {
    return BASE64;
  }
  
  public static BaseEncoding base64Url()
  {
    return BASE64_URL;
  }
  
  public static BaseEncoding base32()
  {
    return BASE32;
  }
  
  public static BaseEncoding base32Hex()
  {
    return BASE32_HEX;
  }
  
  public static BaseEncoding base16()
  {
    return BASE16;
  }
  
  static GwtWorkarounds.CharInput ignoringInput(GwtWorkarounds.CharInput paramCharInput, final CharMatcher paramCharMatcher)
  {
    Preconditions.checkNotNull(paramCharInput);
    Preconditions.checkNotNull(paramCharMatcher);
    new GwtWorkarounds.CharInput()
    {
      public int read()
        throws IOException
      {
        int i;
        do
        {
          i = this.val$delegate.read();
        } while ((i != -1) && (paramCharMatcher.matches((char)i)));
        return i;
      }
      
      public void close()
        throws IOException
      {
        this.val$delegate.close();
      }
    };
  }
  
  static GwtWorkarounds.CharOutput separatingOutput(final GwtWorkarounds.CharOutput paramCharOutput, final String paramString, int paramInt)
  {
    Preconditions.checkNotNull(paramCharOutput);
    Preconditions.checkNotNull(paramString);
    Preconditions.checkArgument(paramInt > 0);
    new GwtWorkarounds.CharOutput()
    {
      int charsUntilSeparator = this.val$afterEveryChars;
      
      public void write(char paramAnonymousChar)
        throws IOException
      {
        if (this.charsUntilSeparator == 0)
        {
          for (int i = 0; i < paramString.length(); i++) {
            paramCharOutput.write(paramString.charAt(i));
          }
          this.charsUntilSeparator = this.val$afterEveryChars;
        }
        paramCharOutput.write(paramAnonymousChar);
        this.charsUntilSeparator -= 1;
      }
      
      public void flush()
        throws IOException
      {
        paramCharOutput.flush();
      }
      
      public void close()
        throws IOException
      {
        paramCharOutput.close();
      }
    };
  }
  
  static final class SeparatedBaseEncoding
    extends BaseEncoding
  {
    private final BaseEncoding delegate;
    private final String separator;
    private final int afterEveryChars;
    private final CharMatcher separatorChars;
    
    SeparatedBaseEncoding(BaseEncoding paramBaseEncoding, String paramString, int paramInt)
    {
      this.delegate = ((BaseEncoding)Preconditions.checkNotNull(paramBaseEncoding));
      this.separator = ((String)Preconditions.checkNotNull(paramString));
      this.afterEveryChars = paramInt;
      Preconditions.checkArgument(paramInt > 0, "Cannot add a separator after every %s chars", new Object[] { Integer.valueOf(paramInt) });
      this.separatorChars = CharMatcher.anyOf(paramString).precomputed();
    }
    
    CharMatcher padding()
    {
      return this.delegate.padding();
    }
    
    int maxEncodedSize(int paramInt)
    {
      int i = this.delegate.maxEncodedSize(paramInt);
      return i + this.separator.length() * IntMath.divide(Math.max(0, i - 1), this.afterEveryChars, RoundingMode.FLOOR);
    }
    
    GwtWorkarounds.ByteOutput encodingStream(GwtWorkarounds.CharOutput paramCharOutput)
    {
      return this.delegate.encodingStream(separatingOutput(paramCharOutput, this.separator, this.afterEveryChars));
    }
    
    int maxDecodedSize(int paramInt)
    {
      return this.delegate.maxDecodedSize(paramInt);
    }
    
    GwtWorkarounds.ByteInput decodingStream(GwtWorkarounds.CharInput paramCharInput)
    {
      return this.delegate.decodingStream(ignoringInput(paramCharInput, this.separatorChars));
    }
    
    public BaseEncoding omitPadding()
    {
      return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
    }
    
    public BaseEncoding withPadChar(char paramChar)
    {
      return this.delegate.withPadChar(paramChar).withSeparator(this.separator, this.afterEveryChars);
    }
    
    public BaseEncoding withSeparator(String paramString, int paramInt)
    {
      throw new UnsupportedOperationException("Already have a separator");
    }
    
    public BaseEncoding upperCase()
    {
      return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
    }
    
    public BaseEncoding lowerCase()
    {
      return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
    }
    
    public String toString()
    {
      return this.delegate.toString() + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
    }
  }
  
  static final class StandardBaseEncoding
    extends BaseEncoding
  {
    private final BaseEncoding.Alphabet alphabet;
    private final Character paddingChar;
    private transient BaseEncoding upperCase;
    private transient BaseEncoding lowerCase;
    
    StandardBaseEncoding(String paramString1, String paramString2, Character paramCharacter)
    {
      this(new BaseEncoding.Alphabet(paramString1, paramString2.toCharArray()), paramCharacter);
    }
    
    StandardBaseEncoding(BaseEncoding.Alphabet paramAlphabet, Character paramCharacter)
    {
      this.alphabet = ((BaseEncoding.Alphabet)Preconditions.checkNotNull(paramAlphabet));
      Preconditions.checkArgument((paramCharacter == null) || (!paramAlphabet.matches(paramCharacter.charValue())), "Padding character %s was already in alphabet", new Object[] { paramCharacter });
      this.paddingChar = paramCharacter;
    }
    
    CharMatcher padding()
    {
      return this.paddingChar == null ? CharMatcher.NONE : CharMatcher.is(this.paddingChar.charValue());
    }
    
    int maxEncodedSize(int paramInt)
    {
      return this.alphabet.charsPerChunk * IntMath.divide(paramInt, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
    }
    
    GwtWorkarounds.ByteOutput encodingStream(final GwtWorkarounds.CharOutput paramCharOutput)
    {
      Preconditions.checkNotNull(paramCharOutput);
      new GwtWorkarounds.ByteOutput()
      {
        int bitBuffer = 0;
        int bitBufferLength = 0;
        int writtenChars = 0;
        
        public void write(byte paramAnonymousByte)
          throws IOException
        {
          this.bitBuffer <<= 8;
          this.bitBuffer |= paramAnonymousByte & 0xFF;
          for (this.bitBufferLength += 8; this.bitBufferLength >= BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar; this.bitBufferLength -= BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar)
          {
            int i = this.bitBuffer >> this.bitBufferLength - BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar & BaseEncoding.StandardBaseEncoding.this.alphabet.mask;
            paramCharOutput.write(BaseEncoding.StandardBaseEncoding.this.alphabet.encode(i));
            this.writtenChars += 1;
          }
        }
        
        public void flush()
          throws IOException
        {
          paramCharOutput.flush();
        }
        
        public void close()
          throws IOException
        {
          if (this.bitBufferLength > 0)
          {
            int i = this.bitBuffer << BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength & BaseEncoding.StandardBaseEncoding.this.alphabet.mask;
            paramCharOutput.write(BaseEncoding.StandardBaseEncoding.this.alphabet.encode(i));
            this.writtenChars += 1;
            if (BaseEncoding.StandardBaseEncoding.this.paddingChar != null) {
              while (this.writtenChars % BaseEncoding.StandardBaseEncoding.this.alphabet.charsPerChunk != 0)
              {
                paramCharOutput.write(BaseEncoding.StandardBaseEncoding.this.paddingChar.charValue());
                this.writtenChars += 1;
              }
            }
          }
          paramCharOutput.close();
        }
      };
    }
    
    int maxDecodedSize(int paramInt)
    {
      return (int)((this.alphabet.bitsPerChar * paramInt + 7L) / 8L);
    }
    
    GwtWorkarounds.ByteInput decodingStream(final GwtWorkarounds.CharInput paramCharInput)
    {
      Preconditions.checkNotNull(paramCharInput);
      new GwtWorkarounds.ByteInput()
      {
        int bitBuffer = 0;
        int bitBufferLength = 0;
        int readChars = 0;
        boolean hitPadding = false;
        final CharMatcher paddingMatcher = BaseEncoding.StandardBaseEncoding.this.padding();
        
        public int read()
          throws IOException
        {
          for (;;)
          {
            int i = paramCharInput.read();
            if (i == -1)
            {
              if ((!this.hitPadding) && (!BaseEncoding.StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars))) {
                throw new IOException("Invalid input length " + this.readChars);
              }
              return -1;
            }
            this.readChars += 1;
            char c = (char)i;
            if (this.paddingMatcher.matches(c))
            {
              if ((!this.hitPadding) && ((this.readChars == 1) || (!BaseEncoding.StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1)))) {
                throw new IOException("Padding cannot start at index " + this.readChars);
              }
              this.hitPadding = true;
            }
            else
            {
              if (this.hitPadding) {
                throw new IOException("Expected padding character but found '" + c + "' at index " + this.readChars);
              }
              this.bitBuffer <<= BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar;
              this.bitBuffer |= BaseEncoding.StandardBaseEncoding.this.alphabet.decode(c);
              this.bitBufferLength += BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar;
              if (this.bitBufferLength >= 8)
              {
                this.bitBufferLength -= 8;
                return this.bitBuffer >> this.bitBufferLength & 0xFF;
              }
            }
          }
        }
        
        public void close()
          throws IOException
        {
          paramCharInput.close();
        }
      };
    }
    
    public BaseEncoding omitPadding()
    {
      return this.paddingChar == null ? this : new StandardBaseEncoding(this.alphabet, null);
    }
    
    public BaseEncoding withPadChar(char paramChar)
    {
      if ((8 % this.alphabet.bitsPerChar == 0) || ((this.paddingChar != null) && (this.paddingChar.charValue() == paramChar))) {
        return this;
      }
      return new StandardBaseEncoding(this.alphabet, Character.valueOf(paramChar));
    }
    
    public BaseEncoding withSeparator(String paramString, int paramInt)
    {
      Preconditions.checkNotNull(paramString);
      Preconditions.checkArgument(padding().or(this.alphabet).matchesNoneOf(paramString), "Separator cannot contain alphabet or padding characters");
      return new BaseEncoding.SeparatedBaseEncoding(this, paramString, paramInt);
    }
    
    public BaseEncoding upperCase()
    {
      Object localObject = this.upperCase;
      if (localObject == null)
      {
        BaseEncoding.Alphabet localAlphabet = this.alphabet.upperCase();
        localObject = this.upperCase = localAlphabet == this.alphabet ? this : new StandardBaseEncoding(localAlphabet, this.paddingChar);
      }
      return (BaseEncoding)localObject;
    }
    
    public BaseEncoding lowerCase()
    {
      Object localObject = this.lowerCase;
      if (localObject == null)
      {
        BaseEncoding.Alphabet localAlphabet = this.alphabet.lowerCase();
        localObject = this.lowerCase = localAlphabet == this.alphabet ? this : new StandardBaseEncoding(localAlphabet, this.paddingChar);
      }
      return (BaseEncoding)localObject;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("BaseEncoding.");
      localStringBuilder.append(this.alphabet.toString());
      if (8 % this.alphabet.bitsPerChar != 0) {
        if (this.paddingChar == null) {
          localStringBuilder.append(".omitPadding()");
        } else {
          localStringBuilder.append(".withPadChar(").append(this.paddingChar).append(')');
        }
      }
      return localStringBuilder.toString();
    }
  }
  
  private static final class Alphabet
    extends CharMatcher
  {
    private final String name;
    private final char[] chars;
    final int mask;
    final int bitsPerChar;
    final int charsPerChunk;
    final int bytesPerChunk;
    private final byte[] decodabet;
    private final boolean[] validPadding;
    
    Alphabet(String paramString, char[] paramArrayOfChar)
    {
      this.name = ((String)Preconditions.checkNotNull(paramString));
      this.chars = ((char[])Preconditions.checkNotNull(paramArrayOfChar));
      try
      {
        this.bitsPerChar = IntMath.log2(paramArrayOfChar.length, RoundingMode.UNNECESSARY);
      }
      catch (ArithmeticException localArithmeticException)
      {
        throw new IllegalArgumentException("Illegal alphabet length " + paramArrayOfChar.length, localArithmeticException);
      }
      int i = Math.min(8, Integer.lowestOneBit(this.bitsPerChar));
      this.charsPerChunk = (8 / i);
      this.bytesPerChunk = (this.bitsPerChar / i);
      this.mask = (paramArrayOfChar.length - 1);
      byte[] arrayOfByte = new byte[''];
      Arrays.fill(arrayOfByte, (byte)-1);
      for (int j = 0; j < paramArrayOfChar.length; j++)
      {
        k = paramArrayOfChar[j];
        Preconditions.checkArgument(CharMatcher.ASCII.matches(k), "Non-ASCII character: %s", new Object[] { Character.valueOf(k) });
        Preconditions.checkArgument(arrayOfByte[k] == -1, "Duplicate character: %s", new Object[] { Character.valueOf(k) });
        arrayOfByte[k] = ((byte)j);
      }
      this.decodabet = arrayOfByte;
      boolean[] arrayOfBoolean = new boolean[this.charsPerChunk];
      for (int k = 0; k < this.bytesPerChunk; k++) {
        arrayOfBoolean[IntMath.divide(k * 8, this.bitsPerChar, RoundingMode.CEILING)] = true;
      }
      this.validPadding = arrayOfBoolean;
    }
    
    char encode(int paramInt)
    {
      return this.chars[paramInt];
    }
    
    boolean isValidPaddingStartPosition(int paramInt)
    {
      return this.validPadding[(paramInt % this.charsPerChunk)];
    }
    
    int decode(char paramChar)
      throws IOException
    {
      if ((paramChar > '') || (this.decodabet[paramChar] == -1)) {
        throw new IOException("Unrecognized character: " + paramChar);
      }
      return this.decodabet[paramChar];
    }
    
    private boolean hasLowerCase()
    {
      for (char c : this.chars) {
        if (Ascii.isLowerCase(c)) {
          return true;
        }
      }
      return false;
    }
    
    private boolean hasUpperCase()
    {
      for (char c : this.chars) {
        if (Ascii.isUpperCase(c)) {
          return true;
        }
      }
      return false;
    }
    
    Alphabet upperCase()
    {
      if (!hasLowerCase()) {
        return this;
      }
      Preconditions.checkState(!hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
      char[] arrayOfChar = new char[this.chars.length];
      for (int i = 0; i < this.chars.length; i++) {
        arrayOfChar[i] = Ascii.toUpperCase(this.chars[i]);
      }
      return new Alphabet(this.name + ".upperCase()", arrayOfChar);
    }
    
    Alphabet lowerCase()
    {
      if (!hasUpperCase()) {
        return this;
      }
      Preconditions.checkState(!hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
      char[] arrayOfChar = new char[this.chars.length];
      for (int i = 0; i < this.chars.length; i++) {
        arrayOfChar[i] = Ascii.toLowerCase(this.chars[i]);
      }
      return new Alphabet(this.name + ".lowerCase()", arrayOfChar);
    }
    
    public boolean matches(char paramChar)
    {
      return (CharMatcher.ASCII.matches(paramChar)) && (this.decodabet[paramChar] != -1);
    }
    
    public String toString()
    {
      return this.name;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\BaseEncoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */