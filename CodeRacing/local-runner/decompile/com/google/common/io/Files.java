package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Checksum;

@Beta
public final class Files
{
  private static final int TEMP_DIR_ATTEMPTS = 10000;
  
  public static BufferedReader newReader(File paramFile, Charset paramCharset)
    throws FileNotFoundException
  {
    Preconditions.checkNotNull(paramFile);
    Preconditions.checkNotNull(paramCharset);
    return new BufferedReader(new InputStreamReader(new FileInputStream(paramFile), paramCharset));
  }
  
  public static BufferedWriter newWriter(File paramFile, Charset paramCharset)
    throws FileNotFoundException
  {
    Preconditions.checkNotNull(paramFile);
    Preconditions.checkNotNull(paramCharset);
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile), paramCharset));
  }
  
  public static ByteSource asByteSource(File paramFile)
  {
    return new FileByteSource(paramFile, null);
  }
  
  public static ByteSink asByteSink(File paramFile, FileWriteMode... paramVarArgs)
  {
    return new FileByteSink(paramFile, paramVarArgs, null);
  }
  
  public static CharSource asCharSource(File paramFile, Charset paramCharset)
  {
    return asByteSource(paramFile).asCharSource(paramCharset);
  }
  
  public static CharSink asCharSink(File paramFile, Charset paramCharset, FileWriteMode... paramVarArgs)
  {
    return asByteSink(paramFile, paramVarArgs).asCharSink(paramCharset);
  }
  
  public static InputSupplier newInputStreamSupplier(File paramFile)
  {
    return ByteStreams.asInputSupplier(asByteSource(paramFile));
  }
  
  public static OutputSupplier newOutputStreamSupplier(File paramFile)
  {
    return newOutputStreamSupplier(paramFile, false);
  }
  
  public static OutputSupplier newOutputStreamSupplier(File paramFile, boolean paramBoolean)
  {
    return ByteStreams.asOutputSupplier(asByteSink(paramFile, modes(paramBoolean)));
  }
  
  private static FileWriteMode[] modes(boolean paramBoolean)
  {
    return paramBoolean ? new FileWriteMode[] { FileWriteMode.APPEND } : new FileWriteMode[0];
  }
  
  public static InputSupplier newReaderSupplier(File paramFile, Charset paramCharset)
  {
    return CharStreams.asInputSupplier(asCharSource(paramFile, paramCharset));
  }
  
  public static OutputSupplier newWriterSupplier(File paramFile, Charset paramCharset)
  {
    return newWriterSupplier(paramFile, paramCharset, false);
  }
  
  public static OutputSupplier newWriterSupplier(File paramFile, Charset paramCharset, boolean paramBoolean)
  {
    return CharStreams.asOutputSupplier(asCharSink(paramFile, paramCharset, modes(paramBoolean)));
  }
  
  public static byte[] toByteArray(File paramFile)
    throws IOException
  {
    return asByteSource(paramFile).read();
  }
  
  public static String toString(File paramFile, Charset paramCharset)
    throws IOException
  {
    return asCharSource(paramFile, paramCharset).read();
  }
  
  public static void copy(InputSupplier paramInputSupplier, File paramFile)
    throws IOException
  {
    ByteStreams.asByteSource(paramInputSupplier).copyTo(asByteSink(paramFile, new FileWriteMode[0]));
  }
  
  public static void write(byte[] paramArrayOfByte, File paramFile)
    throws IOException
  {
    asByteSink(paramFile, new FileWriteMode[0]).write(paramArrayOfByte);
  }
  
  public static void copy(File paramFile, OutputSupplier paramOutputSupplier)
    throws IOException
  {
    asByteSource(paramFile).copyTo(ByteStreams.asByteSink(paramOutputSupplier));
  }
  
  public static void copy(File paramFile, OutputStream paramOutputStream)
    throws IOException
  {
    asByteSource(paramFile).copyTo(paramOutputStream);
  }
  
  public static void copy(File paramFile1, File paramFile2)
    throws IOException
  {
    Preconditions.checkArgument(!paramFile1.equals(paramFile2), "Source %s and destination %s must be different", new Object[] { paramFile1, paramFile2 });
    asByteSource(paramFile1).copyTo(asByteSink(paramFile2, new FileWriteMode[0]));
  }
  
  public static void copy(InputSupplier paramInputSupplier, File paramFile, Charset paramCharset)
    throws IOException
  {
    CharStreams.asCharSource(paramInputSupplier).copyTo(asCharSink(paramFile, paramCharset, new FileWriteMode[0]));
  }
  
  public static void write(CharSequence paramCharSequence, File paramFile, Charset paramCharset)
    throws IOException
  {
    asCharSink(paramFile, paramCharset, new FileWriteMode[0]).write(paramCharSequence);
  }
  
  public static void append(CharSequence paramCharSequence, File paramFile, Charset paramCharset)
    throws IOException
  {
    write(paramCharSequence, paramFile, paramCharset, true);
  }
  
  private static void write(CharSequence paramCharSequence, File paramFile, Charset paramCharset, boolean paramBoolean)
    throws IOException
  {
    asCharSink(paramFile, paramCharset, modes(paramBoolean)).write(paramCharSequence);
  }
  
  public static void copy(File paramFile, Charset paramCharset, OutputSupplier paramOutputSupplier)
    throws IOException
  {
    asCharSource(paramFile, paramCharset).copyTo(CharStreams.asCharSink(paramOutputSupplier));
  }
  
  public static void copy(File paramFile, Charset paramCharset, Appendable paramAppendable)
    throws IOException
  {
    asCharSource(paramFile, paramCharset).copyTo(paramAppendable);
  }
  
  public static boolean equal(File paramFile1, File paramFile2)
    throws IOException
  {
    Preconditions.checkNotNull(paramFile1);
    Preconditions.checkNotNull(paramFile2);
    if ((paramFile1 == paramFile2) || (paramFile1.equals(paramFile2))) {
      return true;
    }
    long l1 = paramFile1.length();
    long l2 = paramFile2.length();
    if ((l1 != 0L) && (l2 != 0L) && (l1 != l2)) {
      return false;
    }
    return asByteSource(paramFile1).contentEquals(asByteSource(paramFile2));
  }
  
  public static File createTempDir()
  {
    File localFile1 = new File(System.getProperty("java.io.tmpdir"));
    String str = System.currentTimeMillis() + "-";
    for (int i = 0; i < 10000; i++)
    {
      File localFile2 = new File(localFile1, str + i);
      if (localFile2.mkdir()) {
        return localFile2;
      }
    }
    throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + str + "0 to " + str + 9999 + ')');
  }
  
  public static void touch(File paramFile)
    throws IOException
  {
    Preconditions.checkNotNull(paramFile);
    if ((!paramFile.createNewFile()) && (!paramFile.setLastModified(System.currentTimeMillis()))) {
      throw new IOException("Unable to update modification time of " + paramFile);
    }
  }
  
  public static void createParentDirs(File paramFile)
    throws IOException
  {
    Preconditions.checkNotNull(paramFile);
    File localFile = paramFile.getCanonicalFile().getParentFile();
    if (localFile == null) {
      return;
    }
    localFile.mkdirs();
    if (!localFile.isDirectory()) {
      throw new IOException("Unable to create parent directories of " + paramFile);
    }
  }
  
  public static void move(File paramFile1, File paramFile2)
    throws IOException
  {
    Preconditions.checkNotNull(paramFile1);
    Preconditions.checkNotNull(paramFile2);
    Preconditions.checkArgument(!paramFile1.equals(paramFile2), "Source %s and destination %s must be different", new Object[] { paramFile1, paramFile2 });
    if (!paramFile1.renameTo(paramFile2))
    {
      copy(paramFile1, paramFile2);
      if (!paramFile1.delete())
      {
        if (!paramFile2.delete()) {
          throw new IOException("Unable to delete " + paramFile2);
        }
        throw new IOException("Unable to delete " + paramFile1);
      }
    }
  }
  
  public static String readFirstLine(File paramFile, Charset paramCharset)
    throws IOException
  {
    return asCharSource(paramFile, paramCharset).readFirstLine();
  }
  
  public static List readLines(File paramFile, Charset paramCharset)
    throws IOException
  {
    return CharStreams.readLines(newReaderSupplier(paramFile, paramCharset));
  }
  
  public static Object readLines(File paramFile, Charset paramCharset, LineProcessor paramLineProcessor)
    throws IOException
  {
    return CharStreams.readLines(newReaderSupplier(paramFile, paramCharset), paramLineProcessor);
  }
  
  public static Object readBytes(File paramFile, ByteProcessor paramByteProcessor)
    throws IOException
  {
    return ByteStreams.readBytes(newInputStreamSupplier(paramFile), paramByteProcessor);
  }
  
  @Deprecated
  public static long getChecksum(File paramFile, Checksum paramChecksum)
    throws IOException
  {
    return ByteStreams.getChecksum(newInputStreamSupplier(paramFile), paramChecksum);
  }
  
  public static HashCode hash(File paramFile, HashFunction paramHashFunction)
    throws IOException
  {
    return asByteSource(paramFile).hash(paramHashFunction);
  }
  
  public static MappedByteBuffer map(File paramFile)
    throws IOException
  {
    Preconditions.checkNotNull(paramFile);
    return map(paramFile, FileChannel.MapMode.READ_ONLY);
  }
  
  public static MappedByteBuffer map(File paramFile, FileChannel.MapMode paramMapMode)
    throws IOException
  {
    Preconditions.checkNotNull(paramFile);
    Preconditions.checkNotNull(paramMapMode);
    if (!paramFile.exists()) {
      throw new FileNotFoundException(paramFile.toString());
    }
    return map(paramFile, paramMapMode, paramFile.length());
  }
  
  public static MappedByteBuffer map(File paramFile, FileChannel.MapMode paramMapMode, long paramLong)
    throws FileNotFoundException, IOException
  {
    Preconditions.checkNotNull(paramFile);
    Preconditions.checkNotNull(paramMapMode);
    Closer localCloser = Closer.create();
    try
    {
      RandomAccessFile localRandomAccessFile = (RandomAccessFile)localCloser.register(new RandomAccessFile(paramFile, paramMapMode == FileChannel.MapMode.READ_ONLY ? "r" : "rw"));
      MappedByteBuffer localMappedByteBuffer = map(localRandomAccessFile, paramMapMode, paramLong);
      return localMappedByteBuffer;
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  private static MappedByteBuffer map(RandomAccessFile paramRandomAccessFile, FileChannel.MapMode paramMapMode, long paramLong)
    throws IOException
  {
    Closer localCloser = Closer.create();
    try
    {
      FileChannel localFileChannel = (FileChannel)localCloser.register(paramRandomAccessFile.getChannel());
      MappedByteBuffer localMappedByteBuffer = localFileChannel.map(paramMapMode, 0L, paramLong);
      return localMappedByteBuffer;
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  public static String simplifyPath(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    if (paramString.length() == 0) {
      return ".";
    }
    Iterable localIterable = Splitter.on('/').omitEmptyStrings().split(paramString);
    ArrayList localArrayList = new ArrayList();
    Object localObject = localIterable.iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      if (!str.equals(".")) {
        if (str.equals(".."))
        {
          if ((localArrayList.size() > 0) && (!((String)localArrayList.get(localArrayList.size() - 1)).equals(".."))) {
            localArrayList.remove(localArrayList.size() - 1);
          } else {
            localArrayList.add("..");
          }
        }
        else {
          localArrayList.add(str);
        }
      }
    }
    localObject = Joiner.on('/').join(localArrayList);
    if (paramString.charAt(0) == '/') {}
    for (localObject = "/" + (String)localObject; ((String)localObject).startsWith("/../"); localObject = ((String)localObject).substring(3)) {}
    if (((String)localObject).equals("/..")) {
      localObject = "/";
    } else if ("".equals(localObject)) {
      localObject = ".";
    }
    return (String)localObject;
  }
  
  public static String getFileExtension(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    String str = new File(paramString).getName();
    int i = str.lastIndexOf('.');
    return i == -1 ? "" : str.substring(i + 1);
  }
  
  public static String getNameWithoutExtension(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    String str = new File(paramString).getName();
    int i = str.lastIndexOf('.');
    return i == -1 ? str : str.substring(0, i);
  }
  
  private static final class FileByteSink
    extends ByteSink
  {
    private final File file;
    private final ImmutableSet modes;
    
    private FileByteSink(File paramFile, FileWriteMode... paramVarArgs)
    {
      this.file = ((File)Preconditions.checkNotNull(paramFile));
      this.modes = ImmutableSet.copyOf(paramVarArgs);
    }
    
    public FileOutputStream openStream()
      throws IOException
    {
      return new FileOutputStream(this.file, this.modes.contains(FileWriteMode.APPEND));
    }
    
    public String toString()
    {
      return "Files.asByteSink(" + this.file + ", " + this.modes + ")";
    }
  }
  
  private static final class FileByteSource
    extends ByteSource
  {
    private final File file;
    
    private FileByteSource(File paramFile)
    {
      this.file = ((File)Preconditions.checkNotNull(paramFile));
    }
    
    public FileInputStream openStream()
      throws IOException
    {
      return new FileInputStream(this.file);
    }
    
    public long size()
      throws IOException
    {
      if (!this.file.isFile()) {
        throw new FileNotFoundException(this.file.toString());
      }
      return this.file.length();
    }
    
    public byte[] read()
      throws IOException
    {
      long l = this.file.length();
      if (l == 0L) {
        return super.read();
      }
      if (l > 2147483647L) {
        throw new OutOfMemoryError("file is too large to fit in a byte array: " + l + " bytes");
      }
      byte[] arrayOfByte1 = new byte[(int)l];
      Closer localCloser = Closer.create();
      try
      {
        InputStream localInputStream = (InputStream)localCloser.register(openStream());
        int i = 0;
        int j = 0;
        while ((i < l) && ((j = localInputStream.read(arrayOfByte1, i, (int)l - i)) != -1)) {
          i += j;
        }
        byte[] arrayOfByte2 = arrayOfByte1;
        if (i < l)
        {
          arrayOfByte2 = Arrays.copyOf(arrayOfByte1, i);
        }
        else if (j != -1)
        {
          localObject1 = new ByteArrayOutputStream();
          ByteStreams.copy(localInputStream, (OutputStream)localObject1);
          byte[] arrayOfByte3 = ((ByteArrayOutputStream)localObject1).toByteArray();
          arrayOfByte2 = new byte[arrayOfByte1.length + arrayOfByte3.length];
          System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
          System.arraycopy(arrayOfByte3, 0, arrayOfByte2, arrayOfByte1.length, arrayOfByte3.length);
        }
        Object localObject1 = arrayOfByte2;
        return (byte[])localObject1;
      }
      catch (Throwable localThrowable)
      {
        throw localCloser.rethrow(localThrowable);
      }
      finally
      {
        localCloser.close();
      }
    }
    
    public String toString()
    {
      return "Files.asByteSource(" + this.file + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\Files.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */