package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

final class MessageDigestHashFunction
  extends AbstractStreamingHashFunction
  implements Serializable
{
  private final MessageDigest prototype;
  private final int bytes;
  private final boolean supportsClone;
  private final String toString;
  
  MessageDigestHashFunction(String paramString1, String paramString2)
  {
    this.prototype = getMessageDigest(paramString1);
    this.bytes = this.prototype.getDigestLength();
    this.toString = ((String)Preconditions.checkNotNull(paramString2));
    this.supportsClone = supportsClone();
  }
  
  MessageDigestHashFunction(String paramString1, int paramInt, String paramString2)
  {
    this.toString = ((String)Preconditions.checkNotNull(paramString2));
    this.prototype = getMessageDigest(paramString1);
    int i = this.prototype.getDigestLength();
    Preconditions.checkArgument((paramInt >= 4) && (paramInt <= i), "bytes (%s) must be >= 4 and < %s", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) });
    this.bytes = paramInt;
    this.supportsClone = supportsClone();
  }
  
  private boolean supportsClone()
  {
    try
    {
      this.prototype.clone();
      return true;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return false;
  }
  
  public int bits()
  {
    return this.bytes * 8;
  }
  
  public String toString()
  {
    return this.toString;
  }
  
  private static MessageDigest getMessageDigest(String paramString)
  {
    try
    {
      return MessageDigest.getInstance(paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new AssertionError(localNoSuchAlgorithmException);
    }
  }
  
  public Hasher newHasher()
  {
    if (this.supportsClone) {
      try
      {
        return new MessageDigestHasher((MessageDigest)this.prototype.clone(), this.bytes, null);
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
    }
    return new MessageDigestHasher(getMessageDigest(this.prototype.getAlgorithm()), this.bytes, null);
  }
  
  Object writeReplace()
  {
    return new SerializedForm(this.prototype.getAlgorithm(), this.bytes, this.toString, null);
  }
  
  private static final class MessageDigestHasher
    extends AbstractByteHasher
  {
    private final MessageDigest digest;
    private final int bytes;
    private boolean done;
    
    private MessageDigestHasher(MessageDigest paramMessageDigest, int paramInt)
    {
      this.digest = paramMessageDigest;
      this.bytes = paramInt;
    }
    
    protected void update(byte paramByte)
    {
      checkNotDone();
      this.digest.update(paramByte);
    }
    
    protected void update(byte[] paramArrayOfByte)
    {
      checkNotDone();
      this.digest.update(paramArrayOfByte);
    }
    
    protected void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      checkNotDone();
      this.digest.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    private void checkNotDone()
    {
      Preconditions.checkState(!this.done, "Cannot use Hasher after calling #hash() on it");
    }
    
    public HashCode hash()
    {
      this.done = true;
      return this.bytes == this.digest.getDigestLength() ? HashCodes.fromBytesNoCopy(this.digest.digest()) : HashCodes.fromBytesNoCopy(Arrays.copyOf(this.digest.digest(), this.bytes));
    }
  }
  
  private static final class SerializedForm
    implements Serializable
  {
    private final String algorithmName;
    private final int bytes;
    private final String toString;
    private static final long serialVersionUID = 0L;
    
    private SerializedForm(String paramString1, int paramInt, String paramString2)
    {
      this.algorithmName = paramString1;
      this.bytes = paramInt;
      this.toString = paramString2;
    }
    
    private Object readResolve()
    {
      return new MessageDigestHashFunction(this.algorithmName, this.bytes, this.toString);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\MessageDigestHashFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */