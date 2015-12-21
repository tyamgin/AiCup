package de.schlichtherle.truezip.file;

import de.schlichtherle.truezip.fs.FsController;
import de.schlichtherle.truezip.fs.FsFilteringManager;
import de.schlichtherle.truezip.fs.FsModel;
import de.schlichtherle.truezip.fs.FsMountPoint;
import de.schlichtherle.truezip.fs.FsSyncException;
import de.schlichtherle.truezip.fs.FsSyncOptions;
import de.schlichtherle.truezip.fs.FsUriModifier;
import de.schlichtherle.truezip.util.BitField;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class TVFS
{
  public static void umount(TFile paramTFile)
    throws FsSyncException
  {
    sync(paramTFile, FsSyncOptions.UMOUNT);
  }
  
  static FsMountPoint mountPoint(TFile paramTFile)
  {
    if (paramTFile.isArchive()) {
      return paramTFile.getController().getModel().getMountPoint();
    }
    try
    {
      return new FsMountPoint(new URI(paramTFile.getFile().toURI() + "/"), FsUriModifier.CANONICALIZE);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new AssertionError(localURISyntaxException);
    }
  }
  
  public static void sync(TFile paramTFile, BitField paramBitField)
    throws FsSyncException
  {
    sync(mountPoint(paramTFile), paramBitField);
  }
  
  public static void sync(FsMountPoint paramFsMountPoint, BitField paramBitField)
    throws FsSyncException
  {
    new FsFilteringManager(TConfig.get().getFsManager(), paramFsMountPoint).sync(paramBitField);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\file\TVFS.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */