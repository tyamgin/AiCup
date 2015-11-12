package net.lingala.zip4j.core;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.AESExtraDataRecord;
import net.lingala.zip4j.model.CentralDirectory;
import net.lingala.zip4j.model.DigitalSignature;
import net.lingala.zip4j.model.EndCentralDirRecord;
import net.lingala.zip4j.model.ExtraDataRecord;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.model.Zip64EndCentralDirLocator;
import net.lingala.zip4j.model.Zip64EndCentralDirRecord;
import net.lingala.zip4j.model.Zip64ExtendedInfo;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.util.Raw;
import net.lingala.zip4j.util.Zip4jUtil;

public class HeaderReader
{
  private RandomAccessFile zip4jRaf = null;
  private ZipModel zipModel;
  
  public HeaderReader(RandomAccessFile paramRandomAccessFile)
  {
    this.zip4jRaf = paramRandomAccessFile;
  }
  
  public ZipModel readAllHeaders(String paramString)
    throws ZipException
  {
    this.zipModel = new ZipModel();
    this.zipModel.setFileNameCharset(paramString);
    this.zipModel.setEndCentralDirRecord(readEndOfCentralDirectoryRecord());
    this.zipModel.setZip64EndCentralDirLocator(readZip64EndCentralDirLocator());
    if (this.zipModel.isZip64Format())
    {
      this.zipModel.setZip64EndCentralDirRecord(readZip64EndCentralDirRec());
      if ((this.zipModel.getZip64EndCentralDirRecord() != null) && (this.zipModel.getZip64EndCentralDirRecord().getNoOfThisDisk() > 0)) {
        this.zipModel.setSplitArchive(true);
      } else {
        this.zipModel.setSplitArchive(false);
      }
    }
    this.zipModel.setCentralDirectory(readCentralDirectory());
    return this.zipModel;
  }
  
  private EndCentralDirRecord readEndOfCentralDirectoryRecord()
    throws ZipException
  {
    if (this.zip4jRaf == null) {
      throw new ZipException("random access file was null", 3);
    }
    try
    {
      byte[] arrayOfByte1 = new byte[4];
      long l = this.zip4jRaf.length() - 22L;
      EndCentralDirRecord localEndCentralDirRecord = new EndCentralDirRecord();
      int i = 0;
      do
      {
        this.zip4jRaf.seek(l--);
        i++;
      } while ((Raw.readLeInt(this.zip4jRaf, arrayOfByte1) != 101010256L) && (i <= 3000));
      if (Raw.readIntLittleEndian(arrayOfByte1, 0) != 101010256L) {
        throw new ZipException("zip headers not found. probably not a zip file");
      }
      byte[] arrayOfByte2 = new byte[4];
      byte[] arrayOfByte3 = new byte[2];
      localEndCentralDirRecord.setSignature(101010256L);
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localEndCentralDirRecord.setNoOfThisDisk(Raw.readShortLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localEndCentralDirRecord.setNoOfThisDiskStartOfCentralDir(Raw.readShortLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localEndCentralDirRecord.setTotNoOfEntriesInCentralDirOnThisDisk(Raw.readShortLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localEndCentralDirRecord.setTotNoOfEntriesInCentralDir(Raw.readShortLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      localEndCentralDirRecord.setSizeOfCentralDir(Raw.readIntLittleEndian(arrayOfByte2, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      byte[] arrayOfByte4 = getLongByteFromIntByte(arrayOfByte2);
      localEndCentralDirRecord.setOffsetOfStartOfCentralDir(Raw.readLongLittleEndian(arrayOfByte4, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      int j = Raw.readShortLittleEndian(arrayOfByte3, 0);
      localEndCentralDirRecord.setCommentLength(j);
      if (j > 0)
      {
        byte[] arrayOfByte5 = new byte[j];
        readIntoBuff(this.zip4jRaf, arrayOfByte5);
        localEndCentralDirRecord.setComment(new String(arrayOfByte5));
        localEndCentralDirRecord.setCommentBytes(arrayOfByte5);
      }
      else
      {
        localEndCentralDirRecord.setComment(null);
      }
      int k = localEndCentralDirRecord.getNoOfThisDisk();
      if (k > 0) {
        this.zipModel.setSplitArchive(true);
      } else {
        this.zipModel.setSplitArchive(false);
      }
      return localEndCentralDirRecord;
    }
    catch (IOException localIOException)
    {
      throw new ZipException("Probably not a zip file or a corrupted zip file", localIOException, 4);
    }
  }
  
  private CentralDirectory readCentralDirectory()
    throws ZipException
  {
    if (this.zip4jRaf == null) {
      throw new ZipException("random access file was null", 3);
    }
    if (this.zipModel.getEndCentralDirRecord() == null) {
      throw new ZipException("EndCentralRecord was null, maybe a corrupt zip file");
    }
    try
    {
      CentralDirectory localCentralDirectory = new CentralDirectory();
      ArrayList localArrayList = new ArrayList();
      EndCentralDirRecord localEndCentralDirRecord = this.zipModel.getEndCentralDirRecord();
      long l = localEndCentralDirRecord.getOffsetOfStartOfCentralDir();
      int i = localEndCentralDirRecord.getTotNoOfEntriesInCentralDir();
      if (this.zipModel.isZip64Format())
      {
        l = this.zipModel.getZip64EndCentralDirRecord().getOffsetStartCenDirWRTStartDiskNo();
        i = (int)this.zipModel.getZip64EndCentralDirRecord().getTotNoOfEntriesInCentralDir();
      }
      this.zip4jRaf.seek(l);
      byte[] arrayOfByte1 = new byte[4];
      byte[] arrayOfByte2 = new byte[2];
      byte[] arrayOfByte3 = new byte[8];
      for (int j = 0; j < i; j++)
      {
        FileHeader localFileHeader = new FileHeader();
        readIntoBuff(this.zip4jRaf, arrayOfByte1);
        m = Raw.readIntLittleEndian(arrayOfByte1, 0);
        if (m != 33639248L) {
          throw new ZipException("Expected central directory entry not found (#" + (j + 1) + ")");
        }
        localFileHeader.setSignature(m);
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        localFileHeader.setVersionMadeBy(Raw.readShortLittleEndian(arrayOfByte2, 0));
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        localFileHeader.setVersionNeededToExtract(Raw.readShortLittleEndian(arrayOfByte2, 0));
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        localFileHeader.setFileNameUTF8Encoded((Raw.readShortLittleEndian(arrayOfByte2, 0) & 0x800) != 0);
        int n = arrayOfByte2[0];
        int i1 = n & 0x1;
        if (i1 != 0) {
          localFileHeader.setEncrypted(true);
        }
        localFileHeader.setGeneralPurposeFlag((byte[])arrayOfByte2.clone());
        localFileHeader.setDataDescriptorExists(n >> 3 == 1);
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        localFileHeader.setCompressionMethod(Raw.readShortLittleEndian(arrayOfByte2, 0));
        readIntoBuff(this.zip4jRaf, arrayOfByte1);
        localFileHeader.setLastModFileTime(Raw.readIntLittleEndian(arrayOfByte1, 0));
        readIntoBuff(this.zip4jRaf, arrayOfByte1);
        localFileHeader.setCrc32(Raw.readIntLittleEndian(arrayOfByte1, 0));
        localFileHeader.setCrcBuff((byte[])arrayOfByte1.clone());
        readIntoBuff(this.zip4jRaf, arrayOfByte1);
        arrayOfByte3 = getLongByteFromIntByte(arrayOfByte1);
        localFileHeader.setCompressedSize(Raw.readLongLittleEndian(arrayOfByte3, 0));
        readIntoBuff(this.zip4jRaf, arrayOfByte1);
        arrayOfByte3 = getLongByteFromIntByte(arrayOfByte1);
        localFileHeader.setUncompressedSize(Raw.readLongLittleEndian(arrayOfByte3, 0));
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        int i2 = Raw.readShortLittleEndian(arrayOfByte2, 0);
        localFileHeader.setFileNameLength(i2);
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        int i3 = Raw.readShortLittleEndian(arrayOfByte2, 0);
        localFileHeader.setExtraFieldLength(i3);
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        int i4 = Raw.readShortLittleEndian(arrayOfByte2, 0);
        localFileHeader.setFileComment(new String(arrayOfByte2));
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        localFileHeader.setDiskNumberStart(Raw.readShortLittleEndian(arrayOfByte2, 0));
        readIntoBuff(this.zip4jRaf, arrayOfByte2);
        localFileHeader.setInternalFileAttr((byte[])arrayOfByte2.clone());
        readIntoBuff(this.zip4jRaf, arrayOfByte1);
        localFileHeader.setExternalFileAttr((byte[])arrayOfByte1.clone());
        readIntoBuff(this.zip4jRaf, arrayOfByte1);
        arrayOfByte3 = getLongByteFromIntByte(arrayOfByte1);
        localFileHeader.setOffsetLocalHeader(Raw.readLongLittleEndian(arrayOfByte3, 0) & 0xFFFFFFFF);
        byte[] arrayOfByte5;
        if (i2 > 0)
        {
          arrayOfByte5 = new byte[i2];
          readIntoBuff(this.zip4jRaf, arrayOfByte5);
          String str = null;
          if (Zip4jUtil.isStringNotNullAndNotEmpty(this.zipModel.getFileNameCharset())) {
            str = new String(arrayOfByte5, this.zipModel.getFileNameCharset());
          } else {
            str = Zip4jUtil.decodeFileName(arrayOfByte5, localFileHeader.isFileNameUTF8Encoded());
          }
          if (str == null) {
            throw new ZipException("fileName is null when reading central directory");
          }
          if (str.indexOf(":" + System.getProperty("file.separator")) >= 0) {
            str = str.substring(str.indexOf(":" + System.getProperty("file.separator")) + 2);
          }
          localFileHeader.setFileName(str);
          localFileHeader.setDirectory((str.endsWith("/")) || (str.endsWith("\\")));
        }
        else
        {
          localFileHeader.setFileName(null);
        }
        readAndSaveExtraDataRecord(localFileHeader);
        readAndSaveZip64ExtendedInfo(localFileHeader);
        readAndSaveAESExtraDataRecord(localFileHeader);
        if (i4 > 0)
        {
          arrayOfByte5 = new byte[i4];
          readIntoBuff(this.zip4jRaf, arrayOfByte5);
          localFileHeader.setFileComment(new String(arrayOfByte5));
        }
        localArrayList.add(localFileHeader);
      }
      localCentralDirectory.setFileHeaders(localArrayList);
      DigitalSignature localDigitalSignature = new DigitalSignature();
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      int k = Raw.readIntLittleEndian(arrayOfByte1, 0);
      if (k != 84233040L) {
        return localCentralDirectory;
      }
      localDigitalSignature.setHeaderSignature(k);
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      int m = Raw.readShortLittleEndian(arrayOfByte2, 0);
      localDigitalSignature.setSizeOfData(m);
      if (m > 0)
      {
        byte[] arrayOfByte4 = new byte[m];
        readIntoBuff(this.zip4jRaf, arrayOfByte4);
        localDigitalSignature.setSignatureData(new String(arrayOfByte4));
      }
      return localCentralDirectory;
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
  }
  
  private void readAndSaveExtraDataRecord(FileHeader paramFileHeader)
    throws ZipException
  {
    if (this.zip4jRaf == null) {
      throw new ZipException("invalid file handler when trying to read extra data record");
    }
    if (paramFileHeader == null) {
      throw new ZipException("file header is null");
    }
    int i = paramFileHeader.getExtraFieldLength();
    if (i <= 0) {
      return;
    }
    paramFileHeader.setExtraDataRecords(readExtraDataRecords(i));
  }
  
  private void readAndSaveExtraDataRecord(LocalFileHeader paramLocalFileHeader)
    throws ZipException
  {
    if (this.zip4jRaf == null) {
      throw new ZipException("invalid file handler when trying to read extra data record");
    }
    if (paramLocalFileHeader == null) {
      throw new ZipException("file header is null");
    }
    int i = paramLocalFileHeader.getExtraFieldLength();
    if (i <= 0) {
      return;
    }
    paramLocalFileHeader.setExtraDataRecords(readExtraDataRecords(i));
  }
  
  private ArrayList readExtraDataRecords(int paramInt)
    throws ZipException
  {
    if (paramInt <= 0) {
      return null;
    }
    try
    {
      byte[] arrayOfByte1 = new byte[paramInt];
      this.zip4jRaf.read(arrayOfByte1);
      int i = 0;
      ArrayList localArrayList = new ArrayList();
      while (i < paramInt)
      {
        ExtraDataRecord localExtraDataRecord = new ExtraDataRecord();
        int j = Raw.readShortLittleEndian(arrayOfByte1, i);
        localExtraDataRecord.setHeader(j);
        i += 2;
        int k = Raw.readShortLittleEndian(arrayOfByte1, i);
        if (2 + k > paramInt)
        {
          k = Raw.readShortBigEndian(arrayOfByte1, i);
          if (2 + k > paramInt) {
            break;
          }
        }
        localExtraDataRecord.setSizeOfData(k);
        i += 2;
        if (k > 0)
        {
          byte[] arrayOfByte2 = new byte[k];
          System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, k);
          localExtraDataRecord.setData(arrayOfByte2);
        }
        i += k;
        localArrayList.add(localExtraDataRecord);
      }
      if (localArrayList.size() > 0) {
        return localArrayList;
      }
      return null;
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
  }
  
  private Zip64EndCentralDirLocator readZip64EndCentralDirLocator()
    throws ZipException
  {
    if (this.zip4jRaf == null) {
      throw new ZipException("invalid file handler when trying to read Zip64EndCentralDirLocator");
    }
    try
    {
      Zip64EndCentralDirLocator localZip64EndCentralDirLocator = new Zip64EndCentralDirLocator();
      setFilePointerToReadZip64EndCentralDirLoc();
      byte[] arrayOfByte1 = new byte[4];
      byte[] arrayOfByte2 = new byte[8];
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      int i = Raw.readIntLittleEndian(arrayOfByte1, 0);
      if (i == 117853008L)
      {
        this.zipModel.setZip64Format(true);
        localZip64EndCentralDirLocator.setSignature(i);
      }
      else
      {
        this.zipModel.setZip64Format(false);
        return null;
      }
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      localZip64EndCentralDirLocator.setNoOfDiskStartOfZip64EndOfCentralDirRec(Raw.readIntLittleEndian(arrayOfByte1, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      localZip64EndCentralDirLocator.setOffsetZip64EndOfCentralDirRec(Raw.readLongLittleEndian(arrayOfByte2, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      localZip64EndCentralDirLocator.setTotNumberOfDiscs(Raw.readIntLittleEndian(arrayOfByte1, 0));
      return localZip64EndCentralDirLocator;
    }
    catch (Exception localException)
    {
      throw new ZipException(localException);
    }
  }
  
  private Zip64EndCentralDirRecord readZip64EndCentralDirRec()
    throws ZipException
  {
    if (this.zipModel.getZip64EndCentralDirLocator() == null) {
      throw new ZipException("invalid zip64 end of central directory locator");
    }
    long l1 = this.zipModel.getZip64EndCentralDirLocator().getOffsetZip64EndOfCentralDirRec();
    if (l1 < 0L) {
      throw new ZipException("invalid offset for start of end of central directory record");
    }
    try
    {
      this.zip4jRaf.seek(l1);
      Zip64EndCentralDirRecord localZip64EndCentralDirRecord = new Zip64EndCentralDirRecord();
      byte[] arrayOfByte1 = new byte[2];
      byte[] arrayOfByte2 = new byte[4];
      byte[] arrayOfByte3 = new byte[8];
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      int i = Raw.readIntLittleEndian(arrayOfByte2, 0);
      if (i != 101075792L) {
        throw new ZipException("invalid signature for zip64 end of central directory record");
      }
      localZip64EndCentralDirRecord.setSignature(i);
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localZip64EndCentralDirRecord.setSizeOfZip64EndCentralDirRec(Raw.readLongLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      localZip64EndCentralDirRecord.setVersionMadeBy(Raw.readShortLittleEndian(arrayOfByte1, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      localZip64EndCentralDirRecord.setVersionNeededToExtract(Raw.readShortLittleEndian(arrayOfByte1, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      localZip64EndCentralDirRecord.setNoOfThisDisk(Raw.readIntLittleEndian(arrayOfByte2, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      localZip64EndCentralDirRecord.setNoOfThisDiskStartOfCentralDir(Raw.readIntLittleEndian(arrayOfByte2, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localZip64EndCentralDirRecord.setTotNoOfEntriesInCentralDirOnThisDisk(Raw.readLongLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localZip64EndCentralDirRecord.setTotNoOfEntriesInCentralDir(Raw.readLongLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localZip64EndCentralDirRecord.setSizeOfCentralDir(Raw.readLongLittleEndian(arrayOfByte3, 0));
      readIntoBuff(this.zip4jRaf, arrayOfByte3);
      localZip64EndCentralDirRecord.setOffsetStartCenDirWRTStartDiskNo(Raw.readLongLittleEndian(arrayOfByte3, 0));
      long l2 = localZip64EndCentralDirRecord.getSizeOfZip64EndCentralDirRec() - 44L;
      if (l2 > 0L)
      {
        byte[] arrayOfByte4 = new byte[(int)l2];
        readIntoBuff(this.zip4jRaf, arrayOfByte4);
        localZip64EndCentralDirRecord.setExtensibleDataSector(arrayOfByte4);
      }
      return localZip64EndCentralDirRecord;
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
  }
  
  private void readAndSaveZip64ExtendedInfo(FileHeader paramFileHeader)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("file header is null in reading Zip64 Extended Info");
    }
    if ((paramFileHeader.getExtraDataRecords() == null) || (paramFileHeader.getExtraDataRecords().size() <= 0)) {
      return;
    }
    Zip64ExtendedInfo localZip64ExtendedInfo = readZip64ExtendedInfo(paramFileHeader.getExtraDataRecords(), paramFileHeader.getUncompressedSize(), paramFileHeader.getCompressedSize(), paramFileHeader.getOffsetLocalHeader(), paramFileHeader.getDiskNumberStart());
    if (localZip64ExtendedInfo != null)
    {
      paramFileHeader.setZip64ExtendedInfo(localZip64ExtendedInfo);
      if (localZip64ExtendedInfo.getUnCompressedSize() != -1L) {
        paramFileHeader.setUncompressedSize(localZip64ExtendedInfo.getUnCompressedSize());
      }
      if (localZip64ExtendedInfo.getCompressedSize() != -1L) {
        paramFileHeader.setCompressedSize(localZip64ExtendedInfo.getCompressedSize());
      }
      if (localZip64ExtendedInfo.getOffsetLocalHeader() != -1L) {
        paramFileHeader.setOffsetLocalHeader(localZip64ExtendedInfo.getOffsetLocalHeader());
      }
      if (localZip64ExtendedInfo.getDiskNumberStart() != -1) {
        paramFileHeader.setDiskNumberStart(localZip64ExtendedInfo.getDiskNumberStart());
      }
    }
  }
  
  private void readAndSaveZip64ExtendedInfo(LocalFileHeader paramLocalFileHeader)
    throws ZipException
  {
    if (paramLocalFileHeader == null) {
      throw new ZipException("file header is null in reading Zip64 Extended Info");
    }
    if ((paramLocalFileHeader.getExtraDataRecords() == null) || (paramLocalFileHeader.getExtraDataRecords().size() <= 0)) {
      return;
    }
    Zip64ExtendedInfo localZip64ExtendedInfo = readZip64ExtendedInfo(paramLocalFileHeader.getExtraDataRecords(), paramLocalFileHeader.getUncompressedSize(), paramLocalFileHeader.getCompressedSize(), -1L, -1);
    if (localZip64ExtendedInfo != null)
    {
      paramLocalFileHeader.setZip64ExtendedInfo(localZip64ExtendedInfo);
      if (localZip64ExtendedInfo.getUnCompressedSize() != -1L) {
        paramLocalFileHeader.setUncompressedSize(localZip64ExtendedInfo.getUnCompressedSize());
      }
      if (localZip64ExtendedInfo.getCompressedSize() != -1L) {
        paramLocalFileHeader.setCompressedSize(localZip64ExtendedInfo.getCompressedSize());
      }
    }
  }
  
  private Zip64ExtendedInfo readZip64ExtendedInfo(ArrayList paramArrayList, long paramLong1, long paramLong2, long paramLong3, int paramInt)
    throws ZipException
  {
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      ExtraDataRecord localExtraDataRecord = (ExtraDataRecord)paramArrayList.get(i);
      if ((localExtraDataRecord != null) && (localExtraDataRecord.getHeader() == 1L))
      {
        Zip64ExtendedInfo localZip64ExtendedInfo = new Zip64ExtendedInfo();
        byte[] arrayOfByte1 = localExtraDataRecord.getData();
        if (localExtraDataRecord.getSizeOfData() <= 0) {
          break;
        }
        byte[] arrayOfByte2 = new byte[8];
        byte[] arrayOfByte3 = new byte[4];
        int j = 0;
        int k = 0;
        long l;
        if (((paramLong1 & 0xFFFF) == 65535L) && (j < localExtraDataRecord.getSizeOfData()))
        {
          System.arraycopy(arrayOfByte1, j, arrayOfByte2, 0, 8);
          l = Raw.readLongLittleEndian(arrayOfByte2, 0);
          localZip64ExtendedInfo.setUnCompressedSize(l);
          j += 8;
          k = 1;
        }
        if (((paramLong2 & 0xFFFF) == 65535L) && (j < localExtraDataRecord.getSizeOfData()))
        {
          System.arraycopy(arrayOfByte1, j, arrayOfByte2, 0, 8);
          l = Raw.readLongLittleEndian(arrayOfByte2, 0);
          localZip64ExtendedInfo.setCompressedSize(l);
          j += 8;
          k = 1;
        }
        if (((paramLong3 & 0xFFFF) == 65535L) && (j < localExtraDataRecord.getSizeOfData()))
        {
          System.arraycopy(arrayOfByte1, j, arrayOfByte2, 0, 8);
          l = Raw.readLongLittleEndian(arrayOfByte2, 0);
          localZip64ExtendedInfo.setOffsetLocalHeader(l);
          j += 8;
          k = 1;
        }
        if (((paramInt & 0xFFFF) == 65535) && (j < localExtraDataRecord.getSizeOfData()))
        {
          System.arraycopy(arrayOfByte1, j, arrayOfByte3, 0, 4);
          int m = Raw.readIntLittleEndian(arrayOfByte3, 0);
          localZip64ExtendedInfo.setDiskNumberStart(m);
          j += 8;
          k = 1;
        }
        if (k == 0) {
          break;
        }
        return localZip64ExtendedInfo;
      }
    }
    return null;
  }
  
  private void setFilePointerToReadZip64EndCentralDirLoc()
    throws ZipException
  {
    try
    {
      byte[] arrayOfByte = new byte[4];
      long l = this.zip4jRaf.length() - 22L;
      do
      {
        this.zip4jRaf.seek(l--);
      } while (Raw.readLeInt(this.zip4jRaf, arrayOfByte) != 101010256L);
      this.zip4jRaf.seek(this.zip4jRaf.getFilePointer() - 4L - 4L - 8L - 4L - 4L);
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
  }
  
  public LocalFileHeader readLocalFileHeader(FileHeader paramFileHeader)
    throws ZipException
  {
    if ((paramFileHeader == null) || (this.zip4jRaf == null)) {
      throw new ZipException("invalid read parameters for local header");
    }
    long l = paramFileHeader.getOffsetLocalHeader();
    if (paramFileHeader.getZip64ExtendedInfo() != null)
    {
      Zip64ExtendedInfo localZip64ExtendedInfo = paramFileHeader.getZip64ExtendedInfo();
      if (localZip64ExtendedInfo.getOffsetLocalHeader() > 0L) {
        l = paramFileHeader.getOffsetLocalHeader();
      }
    }
    if (l < 0L) {
      throw new ZipException("invalid local header offset");
    }
    try
    {
      this.zip4jRaf.seek(l);
      int i = 0;
      LocalFileHeader localLocalFileHeader = new LocalFileHeader();
      byte[] arrayOfByte1 = new byte[2];
      byte[] arrayOfByte2 = new byte[4];
      byte[] arrayOfByte3 = new byte[8];
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      int j = Raw.readIntLittleEndian(arrayOfByte2, 0);
      if (j != 67324752L) {
        throw new ZipException("invalid local header signature for file: " + paramFileHeader.getFileName());
      }
      localLocalFileHeader.setSignature(j);
      i += 4;
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      localLocalFileHeader.setVersionNeededToExtract(Raw.readShortLittleEndian(arrayOfByte1, 0));
      i += 2;
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      localLocalFileHeader.setFileNameUTF8Encoded((Raw.readShortLittleEndian(arrayOfByte1, 0) & 0x800) != 0);
      int k = arrayOfByte1[0];
      int m = k & 0x1;
      if (m != 0) {
        localLocalFileHeader.setEncrypted(true);
      }
      localLocalFileHeader.setGeneralPurposeFlag(arrayOfByte1);
      i += 2;
      String str1 = Integer.toBinaryString(k);
      if (str1.length() >= 4) {
        localLocalFileHeader.setDataDescriptorExists(str1.charAt(3) == '1');
      }
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      localLocalFileHeader.setCompressionMethod(Raw.readShortLittleEndian(arrayOfByte1, 0));
      i += 2;
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      localLocalFileHeader.setLastModFileTime(Raw.readIntLittleEndian(arrayOfByte2, 0));
      i += 4;
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      localLocalFileHeader.setCrc32(Raw.readIntLittleEndian(arrayOfByte2, 0));
      localLocalFileHeader.setCrcBuff((byte[])arrayOfByte2.clone());
      i += 4;
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      arrayOfByte3 = getLongByteFromIntByte(arrayOfByte2);
      localLocalFileHeader.setCompressedSize(Raw.readLongLittleEndian(arrayOfByte3, 0));
      i += 4;
      readIntoBuff(this.zip4jRaf, arrayOfByte2);
      arrayOfByte3 = getLongByteFromIntByte(arrayOfByte2);
      localLocalFileHeader.setUncompressedSize(Raw.readLongLittleEndian(arrayOfByte3, 0));
      i += 4;
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      int n = Raw.readShortLittleEndian(arrayOfByte1, 0);
      localLocalFileHeader.setFileNameLength(n);
      i += 2;
      readIntoBuff(this.zip4jRaf, arrayOfByte1);
      int i1 = Raw.readShortLittleEndian(arrayOfByte1, 0);
      localLocalFileHeader.setExtraFieldLength(i1);
      i += 2;
      if (n > 0)
      {
        byte[] arrayOfByte4 = new byte[n];
        readIntoBuff(this.zip4jRaf, arrayOfByte4);
        String str2 = Zip4jUtil.decodeFileName(arrayOfByte4, localLocalFileHeader.isFileNameUTF8Encoded());
        if (str2 == null) {
          throw new ZipException("file name is null, cannot assign file name to local file header");
        }
        if (str2.indexOf(":" + System.getProperty("file.separator")) >= 0) {
          str2 = str2.substring(str2.indexOf(":" + System.getProperty("file.separator")) + 2);
        }
        localLocalFileHeader.setFileName(str2);
        i += n;
      }
      else
      {
        localLocalFileHeader.setFileName(null);
      }
      readAndSaveExtraDataRecord(localLocalFileHeader);
      i += i1;
      localLocalFileHeader.setOffsetStartOfData(l + i);
      localLocalFileHeader.setPassword(paramFileHeader.getPassword());
      readAndSaveZip64ExtendedInfo(localLocalFileHeader);
      readAndSaveAESExtraDataRecord(localLocalFileHeader);
      if ((localLocalFileHeader.isEncrypted()) && (localLocalFileHeader.getEncryptionMethod() != 99)) {
        if ((k & 0x40) == 64) {
          localLocalFileHeader.setEncryptionMethod(1);
        } else {
          localLocalFileHeader.setEncryptionMethod(0);
        }
      }
      if (localLocalFileHeader.getCrc32() <= 0L)
      {
        localLocalFileHeader.setCrc32(paramFileHeader.getCrc32());
        localLocalFileHeader.setCrcBuff(paramFileHeader.getCrcBuff());
      }
      if (localLocalFileHeader.getCompressedSize() <= 0L) {
        localLocalFileHeader.setCompressedSize(paramFileHeader.getCompressedSize());
      }
      if (localLocalFileHeader.getUncompressedSize() <= 0L) {
        localLocalFileHeader.setUncompressedSize(paramFileHeader.getUncompressedSize());
      }
      return localLocalFileHeader;
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
  }
  
  private void readAndSaveAESExtraDataRecord(FileHeader paramFileHeader)
    throws ZipException
  {
    if (paramFileHeader == null) {
      throw new ZipException("file header is null in reading Zip64 Extended Info");
    }
    if ((paramFileHeader.getExtraDataRecords() == null) || (paramFileHeader.getExtraDataRecords().size() <= 0)) {
      return;
    }
    AESExtraDataRecord localAESExtraDataRecord = readAESExtraDataRecord(paramFileHeader.getExtraDataRecords());
    if (localAESExtraDataRecord != null)
    {
      paramFileHeader.setAesExtraDataRecord(localAESExtraDataRecord);
      paramFileHeader.setEncryptionMethod(99);
    }
  }
  
  private void readAndSaveAESExtraDataRecord(LocalFileHeader paramLocalFileHeader)
    throws ZipException
  {
    if (paramLocalFileHeader == null) {
      throw new ZipException("file header is null in reading Zip64 Extended Info");
    }
    if ((paramLocalFileHeader.getExtraDataRecords() == null) || (paramLocalFileHeader.getExtraDataRecords().size() <= 0)) {
      return;
    }
    AESExtraDataRecord localAESExtraDataRecord = readAESExtraDataRecord(paramLocalFileHeader.getExtraDataRecords());
    if (localAESExtraDataRecord != null)
    {
      paramLocalFileHeader.setAesExtraDataRecord(localAESExtraDataRecord);
      paramLocalFileHeader.setEncryptionMethod(99);
    }
  }
  
  private AESExtraDataRecord readAESExtraDataRecord(ArrayList paramArrayList)
    throws ZipException
  {
    if (paramArrayList == null) {
      return null;
    }
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      ExtraDataRecord localExtraDataRecord = (ExtraDataRecord)paramArrayList.get(i);
      if ((localExtraDataRecord != null) && (localExtraDataRecord.getHeader() == 39169L))
      {
        if (localExtraDataRecord.getData() == null) {
          throw new ZipException("corrput AES extra data records");
        }
        AESExtraDataRecord localAESExtraDataRecord = new AESExtraDataRecord();
        localAESExtraDataRecord.setSignature(39169L);
        localAESExtraDataRecord.setDataSize(localExtraDataRecord.getSizeOfData());
        byte[] arrayOfByte1 = localExtraDataRecord.getData();
        localAESExtraDataRecord.setVersionNumber(Raw.readShortLittleEndian(arrayOfByte1, 0));
        byte[] arrayOfByte2 = new byte[2];
        System.arraycopy(arrayOfByte1, 2, arrayOfByte2, 0, 2);
        localAESExtraDataRecord.setVendorID(new String(arrayOfByte2));
        localAESExtraDataRecord.setAesStrength(arrayOfByte1[4] & 0xFF);
        localAESExtraDataRecord.setCompressionMethod(Raw.readShortLittleEndian(arrayOfByte1, 5));
        return localAESExtraDataRecord;
      }
    }
    return null;
  }
  
  private byte[] readIntoBuff(RandomAccessFile paramRandomAccessFile, byte[] paramArrayOfByte)
    throws ZipException
  {
    try
    {
      if (paramRandomAccessFile.read(paramArrayOfByte, 0, paramArrayOfByte.length) != -1) {
        return paramArrayOfByte;
      }
      throw new ZipException("unexpected end of file when reading short buff");
    }
    catch (IOException localIOException)
    {
      throw new ZipException("IOException when reading short buff", localIOException);
    }
  }
  
  private byte[] getLongByteFromIntByte(byte[] paramArrayOfByte)
    throws ZipException
  {
    if (paramArrayOfByte == null) {
      throw new ZipException("input parameter is null, cannot expand to 8 bytes");
    }
    if (paramArrayOfByte.length != 4) {
      throw new ZipException("invalid byte length, cannot expand to 8 bytes");
    }
    byte[] arrayOfByte = { paramArrayOfByte[0], paramArrayOfByte[1], paramArrayOfByte[2], paramArrayOfByte[3], 0, 0, 0, 0 };
    return arrayOfByte;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\core\HeaderReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */