package de.schlichtherle.truezip.io;

import de.schlichtherle.truezip.util.ThreadGroups;
import de.schlichtherle.truezip.util.Throwables;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Streams
{
  private static final ExecutorService executor = Executors.newCachedThreadPool(new ReaderThreadFactory(null));
  
  public static void copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    try
    {
      cat(paramInputStream, paramOutputStream);
    }
    finally
    {
      try
      {
        paramInputStream.close();
      }
      catch (IOException localIOException2)
      {
        throw new InputException(localIOException2);
      }
      finally
      {
        paramOutputStream.close();
      }
    }
  }
  
  public static void cat(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    if ((null == paramInputStream) || (null == paramOutputStream)) {
      throw new NullPointerException();
    }
    final ReentrantLock localReentrantLock = new ReentrantLock();
    final Condition localCondition = localReentrantLock.newCondition();
    final Buffer[] arrayOfBuffer = Buffer.allocate();
    int i = 0;
    try
    {
      Runnable local1ReaderTask = new Runnable()
      {
        int off;
        int size;
        volatile Throwable exception;
        
        public void run()
        {
          InputStream localInputStream = Streams.this;
          Streams.Buffer[] arrayOfBuffer = arrayOfBuffer;
          int i = arrayOfBuffer.length;
          int j;
          do
          {
            localReentrantLock.lock();
            Streams.Buffer localBuffer;
            try
            {
              while (this.size >= i) {
                try
                {
                  localCondition.await();
                }
                catch (InterruptedException localInterruptedException)
                {
                  return;
                }
              }
              localBuffer = arrayOfBuffer[((this.off + this.size) % i)];
            }
            finally
            {
              localReentrantLock.unlock();
            }
            try
            {
              byte[] arrayOfByte = localBuffer.buf;
              j = localInputStream.read(arrayOfByte, 0, arrayOfByte.length);
            }
            catch (Throwable localThrowable)
            {
              this.exception = localThrowable;
              j = -1;
            }
            localBuffer.read = j;
            localReentrantLock.lock();
            try
            {
              this.size += 1;
              localCondition.signal();
            }
            finally
            {
              localReentrantLock.unlock();
            }
          } while (0 <= j);
        }
      };
      Future localFuture = executor.submit(local1ReaderTask);
      int j = arrayOfBuffer.length;
      for (;;)
      {
        localReentrantLock.lock();
        int m;
        Buffer localBuffer;
        try
        {
          while (0 >= local1ReaderTask.size) {
            try {}catch (InterruptedException localInterruptedException)
            {
              i = 1;
            }
          }
          m = local1ReaderTask.off;
          localBuffer = arrayOfBuffer[m];
        }
        finally
        {
          localReentrantLock.unlock();
        }
        int k = localBuffer.read;
        if (k == -1) {
          break;
        }
        try
        {
          byte[] arrayOfByte = localBuffer.buf;
          paramOutputStream.write(arrayOfByte, 0, k);
        }
        catch (IOException localIOException)
        {
          cancel(localFuture);
          throw localIOException;
        }
        catch (RuntimeException localRuntimeException)
        {
          cancel(localFuture);
          throw localRuntimeException;
        }
        catch (Error localError)
        {
          cancel(localFuture);
          throw localError;
        }
        localReentrantLock.lock();
        try
        {
          local1ReaderTask.off = ((m + 1) % j);
          local1ReaderTask.size -= 1;
          localCondition.signal();
        }
        finally
        {
          localReentrantLock.unlock();
        }
      }
      paramOutputStream.flush();
      Throwable localThrowable = local1ReaderTask.exception;
      if (null != localThrowable)
      {
        if ((localThrowable instanceof InputException)) {
          throw ((InputException)localThrowable);
        }
        if ((localThrowable instanceof IOException)) {
          throw new InputException((IOException)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)Throwables.wrap(localThrowable));
        }
        throw ((Error)Throwables.wrap(localThrowable));
      }
    }
    finally
    {
      if (i != 0) {
        Thread.currentThread().interrupt();
      }
      Buffer.release(arrayOfBuffer);
    }
  }
  
  private static void cancel(Future paramFuture)
  {
    paramFuture.cancel(true);
    int i = 0;
    try
    {
      try
      {
        paramFuture.get();
      }
      catch (CancellationException localCancellationException) {}catch (ExecutionException localExecutionException)
      {
        throw new AssertionError(localExecutionException);
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        i = 1;
      }
    }
    finally
    {
      if (i != 0) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  public static final class ReaderThread
    extends Thread
  {
    ReaderThread(Runnable paramRunnable)
    {
      super(paramRunnable, ReaderThread.class.getName());
      setDaemon(true);
    }
  }
  
  private static final class ReaderThreadFactory
    implements ThreadFactory
  {
    public Thread newThread(Runnable paramRunnable)
    {
      return new Streams.ReaderThread(paramRunnable);
    }
  }
  
  private static final class Buffer
  {
    static final Queue queue = new ConcurrentLinkedQueue();
    final byte[] buf = new byte[' '];
    int read;
    
    static Buffer[] allocate()
    {
      while (null != (localObject = (Reference)queue.poll()))
      {
        Buffer[] arrayOfBuffer = (Buffer[])((Reference)localObject).get();
        if (null != arrayOfBuffer) {
          return arrayOfBuffer;
        }
      }
      Object localObject = new Buffer[4];
      int i = localObject.length;
      while (0 <= --i) {
        localObject[i] = new Buffer();
      }
      return (Buffer[])localObject;
    }
    
    static void release(Buffer[] paramArrayOfBuffer)
    {
      queue.add(new SoftReference(paramArrayOfBuffer));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\io\Streams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */