package com.codeforces.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public final class CountingInputStream
  extends InputStream
{
  private static final ReadEvent EMPTY_READ_EVENT = new ReadEvent()
  {
    public void onRead(long paramAnonymousLong1, long paramAnonymousLong2) {}
  };
  private final ReentrantLock lock = new ReentrantLock();
  private final AtomicLong totalReadByteCount = new AtomicLong();
  private final InputStream inputStream;
  private final ReadEvent readEvent;
  
  public CountingInputStream(InputStream paramInputStream, ReadEvent paramReadEvent)
  {
    this.inputStream = paramInputStream;
    this.readEvent = paramReadEvent;
  }
  
  public int read()
    throws IOException
  {
    if (this.lock.isHeldByCurrentThread()) {
      return this.inputStream.read();
    }
    this.lock.lock();
    try
    {
      int i = this.inputStream.read();
      if (i != -1) {
        this.readEvent.onRead(1L, this.totalReadByteCount.incrementAndGet());
      }
      int j = i;
      return j;
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    if (this.lock.isHeldByCurrentThread()) {
      return this.inputStream.read(paramArrayOfByte);
    }
    this.lock.lock();
    try
    {
      int i = this.inputStream.read(paramArrayOfByte);
      if (i > 0) {
        this.readEvent.onRead(i, this.totalReadByteCount.addAndGet(i));
      }
      int j = i;
      return j;
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.lock.isHeldByCurrentThread()) {
      return this.inputStream.read(paramArrayOfByte, paramInt1, paramInt2);
    }
    this.lock.lock();
    try
    {
      int i = this.inputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i > 0) {
        this.readEvent.onRead(i, this.totalReadByteCount.addAndGet(i));
      }
      int j = i;
      return j;
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (this.lock.isHeldByCurrentThread()) {
      return this.inputStream.skip(paramLong);
    }
    this.lock.lock();
    try
    {
      long l1 = this.inputStream.skip(paramLong);
      if (l1 > 0L) {
        this.readEvent.onRead(l1, this.totalReadByteCount.addAndGet(paramLong));
      }
      long l2 = l1;
      return l2;
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public int available()
    throws IOException
  {
    this.lock.lock();
    try
    {
      int i = this.inputStream.available();
      return i;
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 12	com/codeforces/commons/io/CountingInputStream:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 28	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: getfield 11	com/codeforces/commons/io/CountingInputStream:inputStream	Ljava/io/InputStream;
    //   11: invokevirtual 18	java/io/InputStream:close	()V
    //   14: aload_0
    //   15: getfield 12	com/codeforces/commons/io/CountingInputStream:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   18: invokevirtual 29	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   21: goto +13 -> 34
    //   24: astore_1
    //   25: aload_0
    //   26: getfield 12	com/codeforces/commons/io/CountingInputStream:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   29: invokevirtual 29	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   32: aload_1
    //   33: athrow
    //   34: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	CountingInputStream
    //   24	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	14	24	finally
  }
  
  public static abstract interface ReadEvent
  {
    public abstract void onRead(long paramLong1, long paramLong2)
      throws IOException;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\CountingInputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */