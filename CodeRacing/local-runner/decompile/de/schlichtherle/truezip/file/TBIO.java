package de.schlichtherle.truezip.file;

import de.schlichtherle.truezip.entry.Entry;
import de.schlichtherle.truezip.fs.FsController;
import de.schlichtherle.truezip.fs.FsManager;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.FsPath;
import de.schlichtherle.truezip.io.Paths;
import de.schlichtherle.truezip.socket.IOSocket;
import de.schlichtherle.truezip.socket.InputSocket;
import de.schlichtherle.truezip.socket.OutputSocket;
import de.schlichtherle.truezip.util.BitField;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

final class TBIO
{
  static void mv(File paramFile1, File paramFile2, TArchiveDetector paramTArchiveDetector)
    throws IOException
  {
    checkContains(paramFile1, paramFile2);
    if (paramFile2.exists()) {
      throw new IOException(paramFile2 + " (destination exists already)");
    }
    mv0(paramFile1, paramFile2, paramTArchiveDetector);
  }
  
  private static void mv0(File paramFile1, File paramFile2, TArchiveDetector paramTArchiveDetector)
    throws IOException
  {
    if (paramFile1.isDirectory())
    {
      long l = paramFile1.lastModified();
      int i = ((paramFile1 instanceof TFile)) && (null != ((TFile)paramFile1).getInnerArchive()) ? 1 : 0;
      int j = ((paramFile2 instanceof TFile)) && (null != ((TFile)paramFile2).getInnerArchive()) ? 1 : 0;
      int k = (i != 0) && (0L >= l) ? 1 : 0;
      if (((k == 0) || (j == 0) || (!TConfig.get().isLenient())) && (!paramFile2.mkdir()) && (!paramFile2.isDirectory())) {
        throw new IOException(paramFile2 + " (not a directory)");
      }
      String[] arrayOfString1 = paramFile1.list();
      if (null == arrayOfString1) {
        throw new IOException(paramFile2 + " (cannot list directory)");
      }
      if ((i == 0) && (j != 0)) {
        Arrays.sort(arrayOfString1);
      }
      for (String str : arrayOfString1) {
        mv0(new TFile(paramFile1, str, paramTArchiveDetector), new TFile(paramFile2, str, paramTArchiveDetector), paramTArchiveDetector);
      }
      if ((k == 0) && (!paramFile2.setLastModified(l))) {
        throw new IOException(paramFile2 + " (cannot set last modification time)");
      }
    }
    else if (paramFile1.isFile())
    {
      if ((paramFile2.exists()) && (!paramFile2.isFile())) {
        throw new IOException(paramFile2 + " (not a file)");
      }
      cp0(true, paramFile1, paramFile2);
    }
    else
    {
      if (paramFile1.exists()) {
        throw new IOException(paramFile1 + " (cannot move special file)");
      }
      throw new IOException(paramFile1 + " (missing file)");
    }
    if (!paramFile1.delete()) {
      throw new IOException(paramFile1 + " (cannot delete)");
    }
  }
  
  private static void cp0(boolean paramBoolean, File paramFile1, File paramFile2)
    throws IOException
  {
    TConfig localTConfig = TConfig.get();
    InputSocket localInputSocket = getInputSocket(paramFile1, localTConfig.getInputPreferences());
    OutputSocket localOutputSocket = getOutputSocket(paramFile2, localTConfig.getOutputPreferences(), paramBoolean ? (Entry)localInputSocket.getLocalTarget() : null);
    IOSocket.copy(localInputSocket, localOutputSocket);
  }
  
  private static void checkContains(File paramFile1, File paramFile2)
    throws IOException
  {
    if (Paths.contains(paramFile1.getAbsolutePath(), paramFile2.getAbsolutePath(), File.separatorChar)) {
      throw new IOException(paramFile2 + " (contained in " + paramFile1 + ")");
    }
  }
  
  static InputSocket getInputSocket(File paramFile, BitField paramBitField)
  {
    if ((paramFile instanceof TFile))
    {
      localObject = (TFile)paramFile;
      TFile localTFile = ((TFile)localObject).getInnerArchive();
      if (null != localTFile) {
        return localTFile.getController().getInputSocket(((TFile)localObject).getInnerFsEntryName(), paramBitField);
      }
    }
    Object localObject = new FsPath(paramFile);
    return TConfig.get().getFsManager().getController(((FsPath)localObject).getMountPoint(), getDetector(paramFile)).getInputSocket(((FsPath)localObject).getEntryName(), paramBitField);
  }
  
  static OutputSocket getOutputSocket(File paramFile, BitField paramBitField, Entry paramEntry)
  {
    if ((paramFile instanceof TFile))
    {
      localObject = (TFile)paramFile;
      TFile localTFile = ((TFile)localObject).getInnerArchive();
      if (null != localTFile) {
        return localTFile.getController().getOutputSocket(((TFile)localObject).getInnerFsEntryName(), paramBitField, paramEntry);
      }
    }
    Object localObject = new FsPath(paramFile);
    return TConfig.get().getFsManager().getController(((FsPath)localObject).getMountPoint(), getDetector(paramFile)).getOutputSocket(((FsPath)localObject).getEntryName(), paramBitField.clear(FsOutputOption.CREATE_PARENTS), paramEntry);
  }
  
  private static TArchiveDetector getDetector(File paramFile)
  {
    return (paramFile instanceof TFile) ? ((TFile)paramFile).getArchiveDetector() : TConfig.get().getArchiveDetector();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\file\TBIO.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */