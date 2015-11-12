package net.lingala.zip4j.unzip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.zip.CRC32;
import net.lingala.zip4j.core.HeaderReader;
import net.lingala.zip4j.crypto.AESDecrypter;
import net.lingala.zip4j.crypto.IDecrypter;
import net.lingala.zip4j.crypto.StandardDecrypter;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.InflaterInputStream;
import net.lingala.zip4j.io.PartInputStream;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.AESExtraDataRecord;
import net.lingala.zip4j.model.EndCentralDirRecord;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Raw;
import net.lingala.zip4j.util.Zip4jUtil;

public class UnzipEngine
{
  private ZipModel zipModel;
  private FileHeader fileHeader;
  private int currSplitFileCounter = 0;
  private LocalFileHeader localFileHeader;
  private IDecrypter decrypter;
  private CRC32 crc;
  
  public UnzipEngine(ZipModel paramZipModel, FileHeader paramFileHeader)
    throws ZipException
  {
    if ((paramZipModel == null) || (paramFileHeader == null)) {
      throw new ZipException("Invalid parameters passed to StoreUnzip. One or more of the parameters were null");
    }
    this.zipModel = paramZipModel;
    this.fileHeader = paramFileHeader;
    this.crc = new CRC32();
  }
  
  public void unzipFile(ProgressMonitor paramProgressMonitor, String paramString1, String paramString2, UnzipParameters paramUnzipParameters)
    throws ZipException
  {
    if ((this.zipModel == null) || (this.fileHeader == null) || (!Zip4jUtil.isStringNotNullAndNotEmpty(paramString1))) {
      throw new ZipException("Invalid parameters passed during unzipping file. One or more of the parameters were null");
    }
    ZipInputStream localZipInputStream = null;
    FileOutputStream localFileOutputStream = null;
    try
    {
      byte[] arrayOfByte = new byte['က'];
      int i = -1;
      localZipInputStream = getInputStream();
      localFileOutputStream = getOutputStream(paramString1, paramString2);
      while ((i = localZipInputStream.read(arrayOfByte)) != -1)
      {
        localFileOutputStream.write(arrayOfByte, 0, i);
        paramProgressMonitor.updateWorkCompleted(i);
        if (paramProgressMonitor.isCancelAllTasks())
        {
          paramProgressMonitor.setResult(3);
          paramProgressMonitor.setState(0);
          return;
        }
      }
      closeStreams(localZipInputStream, localFileOutputStream);
      UnzipUtil.applyFileAttributes(this.fileHeader, new File(getOutputFileNameWithPath(paramString1, paramString2)), paramUnzipParameters);
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
    finally
    {
      closeStreams(localZipInputStream, localFileOutputStream);
    }
  }
  
  public ZipInputStream getInputStream()
    throws ZipException
  {
    if (this.fileHeader == null) {
      throw new ZipException("file header is null, cannot get inputstream");
    }
    RandomAccessFile localRandomAccessFile = null;
    try
    {
      localRandomAccessFile = createFileHandler("r");
      String str = "local header and file header do not match";
      if (!checkLocalHeader()) {
        throw new ZipException(str);
      }
      init(localRandomAccessFile);
      long l1 = this.localFileHeader.getCompressedSize();
      long l2 = this.localFileHeader.getOffsetStartOfData();
      if (this.localFileHeader.isEncrypted()) {
        if (this.localFileHeader.getEncryptionMethod() == 99)
        {
          if ((this.decrypter instanceof AESDecrypter))
          {
            l1 -= ((AESDecrypter)this.decrypter).getSaltLength() + ((AESDecrypter)this.decrypter).getPasswordVerifierLength() + 10;
            l2 += ((AESDecrypter)this.decrypter).getSaltLength() + ((AESDecrypter)this.decrypter).getPasswordVerifierLength();
          }
          else
          {
            throw new ZipException("invalid decryptor when trying to calculate compressed size for AES encrypted file: " + this.fileHeader.getFileName());
          }
        }
        else if (this.localFileHeader.getEncryptionMethod() == 0)
        {
          l1 -= 12L;
          l2 += 12L;
        }
      }
      int i = this.fileHeader.getCompressionMethod();
      if (this.fileHeader.getEncryptionMethod() == 99) {
        if (this.fileHeader.getAesExtraDataRecord() != null) {
          i = this.fileHeader.getAesExtraDataRecord().getCompressionMethod();
        } else {
          throw new ZipException("AESExtraDataRecord does not exist for AES encrypted file: " + this.fileHeader.getFileName());
        }
      }
      localRandomAccessFile.seek(l2);
      switch (i)
      {
      case 0: 
        return new ZipInputStream(new PartInputStream(localRandomAccessFile, l2, l1, this));
      case 8: 
        return new ZipInputStream(new InflaterInputStream(localRandomAccessFile, l2, l1, this));
      }
      throw new ZipException("compression type not supported");
    }
    catch (ZipException localZipException)
    {
      if (localRandomAccessFile != null) {
        try
        {
          localRandomAccessFile.close();
        }
        catch (IOException localIOException1) {}
      }
      throw localZipException;
    }
    catch (Exception localException)
    {
      if (localRandomAccessFile != null) {
        try
        {
          localRandomAccessFile.close();
        }
        catch (IOException localIOException2) {}
      }
      throw new ZipException(localException);
    }
  }
  
  private void init(RandomAccessFile paramRandomAccessFile)
    throws ZipException
  {
    if (this.localFileHeader == null) {
      throw new ZipException("local file header is null, cannot initialize input stream");
    }
    try
    {
      initDecrypter(paramRandomAccessFile);
    }
    catch (ZipException localZipException)
    {
      throw localZipException;
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
  
  private void initDecrypter(RandomAccessFile paramRandomAccessFile)
    throws ZipException
  {
    if (this.localFileHeader == null) {
      throw new ZipException("local file header is null, cannot init decrypter");
    }
    if (this.localFileHeader.isEncrypted()) {
      if (this.localFileHeader.getEncryptionMethod() == 0) {
        this.decrypter = new StandardDecrypter(this.fileHeader, getStandardDecrypterHeaderBytes(paramRandomAccessFile));
      } else if (this.localFileHeader.getEncryptionMethod() == 99) {
        this.decrypter = new AESDecrypter(this.localFileHeader, getAESSalt(paramRandomAccessFile), getAESPasswordVerifier(paramRandomAccessFile));
      } else {
        throw new ZipException("unsupported encryption method");
      }
    }
  }
  
  private byte[] getStandardDecrypterHeaderBytes(RandomAccessFile paramRandomAccessFile)
    throws ZipException
  {
    try
    {
      byte[] arrayOfByte = new byte[12];
      paramRandomAccessFile.seek(this.localFileHeader.getOffsetStartOfData());
      paramRandomAccessFile.read(arrayOfByte, 0, 12);
      return arrayOfByte;
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
  
  private byte[] getAESSalt(RandomAccessFile paramRandomAccessFile)
    throws ZipException
  {
    if (this.localFileHeader.getAesExtraDataRecord() == null) {
      return null;
    }
    try
    {
      AESExtraDataRecord localAESExtraDataRecord = this.localFileHeader.getAesExtraDataRecord();
      byte[] arrayOfByte = new byte[calculateAESSaltLength(localAESExtraDataRecord)];
      paramRandomAccessFile.seek(this.localFileHeader.getOffsetStartOfData());
      paramRandomAccessFile.read(arrayOfByte);
      return arrayOfByte;
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
  }
  
  private byte[] getAESPasswordVerifier(RandomAccessFile paramRandomAccessFile)
    throws ZipException
  {
    try
    {
      byte[] arrayOfByte = new byte[2];
      paramRandomAccessFile.read(arrayOfByte);
      return arrayOfByte;
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
  }
  
  private int calculateAESSaltLength(AESExtraDataRecord paramAESExtraDataRecord)
    throws ZipException
  {
    if (paramAESExtraDataRecord == null) {
      throw new ZipException("unable to determine salt length: AESExtraDataRecord is null");
    }
    switch (paramAESExtraDataRecord.getAesStrength())
    {
    case 1: 
      return 8;
    case 2: 
      return 12;
    case 3: 
      return 16;
    }
    throw new ZipException("unable to determine salt length: invalid aes key strength");
  }
  
  public void checkCRC()
    throws ZipException
  {
    if (this.fileHeader != null)
    {
      Object localObject;
      if (this.fileHeader.getEncryptionMethod() == 99)
      {
        if ((this.decrypter != null) && ((this.decrypter instanceof AESDecrypter)))
        {
          byte[] arrayOfByte1 = ((AESDecrypter)this.decrypter).getCalculatedAuthenticationBytes();
          byte[] arrayOfByte2 = ((AESDecrypter)this.decrypter).getStoredMac();
          localObject = new byte[10];
          if ((localObject == null) || (arrayOfByte2 == null)) {
            throw new ZipException("CRC (MAC) check failed for " + this.fileHeader.getFileName());
          }
          System.arraycopy(arrayOfByte1, 0, localObject, 0, 10);
          if (!Arrays.equals((byte[])localObject, arrayOfByte2)) {
            throw new ZipException("invalid CRC (MAC) for file: " + this.fileHeader.getFileName());
          }
        }
      }
      else
      {
        long l = this.crc.getValue() & 0xFFFFFFFF;
        if (l != this.fileHeader.getCrc32())
        {
          localObject = "invalid CRC for file: " + this.fileHeader.getFileName();
          if ((this.localFileHeader.isEncrypted()) && (this.localFileHeader.getEncryptionMethod() == 0)) {
            localObject = (String)localObject + " - Wrong Password?";
          }
          throw new ZipException((String)localObject);
        }
      }
    }
  }
  
  private boolean checkLocalHeader()
    throws ZipException
  {
    RandomAccessFile localRandomAccessFile = null;
    try
    {
      localRandomAccessFile = checkSplitFile();
      if (localRandomAccessFile == null) {
        localRandomAccessFile = new RandomAccessFile(new File(this.zipModel.getZipFile()), "r");
      }
      HeaderReader localHeaderReader = new HeaderReader(localRandomAccessFile);
      this.localFileHeader = localHeaderReader.readLocalFileHeader(this.fileHeader);
      if (this.localFileHeader == null) {
        throw new ZipException("error reading local file header. Is this a valid zip file?");
      }
      if (this.localFileHeader.getCompressionMethod() != this.fileHeader.getCompressionMethod())
      {
        bool = false;
        return bool;
      }
      boolean bool = true;
      return bool;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw new ZipException(localFileNotFoundException);
    }
    finally
    {
      if (localRandomAccessFile != null) {
        try
        {
          localRandomAccessFile.close();
        }
        catch (IOException localIOException3) {}catch (Exception localException3) {}
      }
    }
  }
  
  private RandomAccessFile checkSplitFile()
    throws ZipException
  {
    if (this.zipModel.isSplitArchive())
    {
      int i = this.fileHeader.getDiskNumberStart();
      this.currSplitFileCounter = (i + 1);
      String str1 = this.zipModel.getZipFile();
      String str2 = null;
      if (i == this.zipModel.getEndCentralDirRecord().getNoOfThisDisk()) {
        str2 = this.zipModel.getZipFile();
      } else if (i >= 9) {
        str2 = str1.substring(0, str1.lastIndexOf(".")) + ".z" + (i + 1);
      } else {
        str2 = str1.substring(0, str1.lastIndexOf(".")) + ".z0" + (i + 1);
      }
      try
      {
        RandomAccessFile localRandomAccessFile = new RandomAccessFile(str2, "r");
        if (this.currSplitFileCounter == 1)
        {
          byte[] arrayOfByte = new byte[4];
          localRandomAccessFile.read(arrayOfByte);
          if (Raw.readIntLittleEndian(arrayOfByte, 0) != 134695760L) {
            throw new ZipException("invalid first part split file signature");
          }
        }
        return localRandomAccessFile;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        throw new ZipException(localFileNotFoundException);
      }
      catch (IOException localIOException)
      {
        throw new ZipException(localIOException);
      }
    }
    return null;
  }
  
  private RandomAccessFile createFileHandler(String paramString)
    throws ZipException
  {
    if ((this.zipModel == null) || (!Zip4jUtil.isStringNotNullAndNotEmpty(this.zipModel.getZipFile()))) {
      throw new ZipException("input parameter is null in getFilePointer");
    }
    try
    {
      RandomAccessFile localRandomAccessFile = null;
      if (this.zipModel.isSplitArchive()) {
        localRandomAccessFile = checkSplitFile();
      } else {
        localRandomAccessFile = new RandomAccessFile(new File(this.zipModel.getZipFile()), paramString);
      }
      return localRandomAccessFile;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw new ZipException(localFileNotFoundException);
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
  
  private FileOutputStream getOutputStream(String paramString1, String paramString2)
    throws ZipException
  {
    if (!Zip4jUtil.isStringNotNullAndNotEmpty(paramString1)) {
      throw new ZipException("invalid output path");
    }
    try
    {
      File localFile = new File(getOutputFileNameWithPath(paramString1, paramString2));
      if (!localFile.getParentFile().exists()) {
        localFile.getParentFile().mkdirs();
      }
      if (localFile.exists()) {
        localFile.delete();
      }
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      return localFileOutputStream;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw new ZipException(localFileNotFoundException);
    }
  }
  
  private String getOutputFileNameWithPath(String paramString1, String paramString2)
    throws ZipException
  {
    String str = null;
    if (Zip4jUtil.isStringNotNullAndNotEmpty(paramString2)) {
      str = paramString2;
    } else {
      str = this.fileHeader.getFileName();
    }
    return paramString1 + System.getProperty("file.separator") + str;
  }
  
  public RandomAccessFile startNextSplitFile()
    throws IOException, FileNotFoundException
  {
    String str1 = this.zipModel.getZipFile();
    String str2 = null;
    if (this.currSplitFileCounter == this.zipModel.getEndCentralDirRecord().getNoOfThisDisk()) {
      str2 = this.zipModel.getZipFile();
    } else if (this.currSplitFileCounter >= 9) {
      str2 = str1.substring(0, str1.lastIndexOf(".")) + ".z" + (this.currSplitFileCounter + 1);
    } else {
      str2 = str1.substring(0, str1.lastIndexOf(".")) + ".z0" + (this.currSplitFileCounter + 1);
    }
    this.currSplitFileCounter += 1;
    try
    {
      if (!Zip4jUtil.checkFileExists(str2)) {
        throw new IOException("zip split file does not exist: " + str2);
      }
    }
    catch (ZipException localZipException)
    {
      throw new IOException(localZipException.getMessage());
    }
    return new RandomAccessFile(str2, "r");
  }
  
  private void closeStreams(InputStream paramInputStream, OutputStream paramOutputStream)
    throws ZipException
  {
    try
    {
      if (paramInputStream != null)
      {
        paramInputStream.close();
        paramInputStream = null;
      }
      return;
    }
    catch (IOException localIOException2)
    {
      if ((localIOException2 != null) && (Zip4jUtil.isStringNotNullAndNotEmpty(localIOException2.getMessage())) && (localIOException2.getMessage().indexOf(" - Wrong Password?") >= 0)) {
        throw new ZipException(localIOException2.getMessage());
      }
    }
    finally
    {
      try
      {
        if (paramOutputStream != null)
        {
          paramOutputStream.close();
          paramOutputStream = null;
        }
      }
      catch (IOException localIOException4) {}
    }
  }
  
  public void updateCRC(int paramInt)
  {
    this.crc.update(paramInt);
  }
  
  public void updateCRC(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte != null) {
      this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  public FileHeader getFileHeader()
  {
    return this.fileHeader;
  }
  
  public IDecrypter getDecrypter()
  {
    return this.decrypter;
  }
  
  public ZipModel getZipModel()
  {
    return this.zipModel;
  }
  
  public LocalFileHeader getLocalFileHeader()
  {
    return this.localFileHeader;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\unzip\UnzipEngine.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */