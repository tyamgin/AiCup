package de.schlichtherle.truezip.file;

import de.schlichtherle.truezip.fs.FsAbstractCompositeDriver;
import de.schlichtherle.truezip.fs.FsDriver;
import de.schlichtherle.truezip.fs.FsDriverProvider;
import de.schlichtherle.truezip.fs.FsScheme;
import de.schlichtherle.truezip.fs.sl.FsDriverLocator;
import de.schlichtherle.truezip.util.SuffixSet;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class TArchiveDetector
  extends FsAbstractCompositeDriver
{
  public static final TArchiveDetector NULL = new TArchiveDetector("");
  public static final TArchiveDetector ALL = new TArchiveDetector(null);
  private final SuffixSet suffixes;
  private final Map drivers;
  
  private static SuffixSet extensions(FsDriverProvider paramFsDriverProvider)
  {
    if ((paramFsDriverProvider instanceof TArchiveDetector)) {
      return new SuffixSet(((TArchiveDetector)paramFsDriverProvider).suffixes);
    }
    Map localMap = paramFsDriverProvider.get();
    SuffixSet localSuffixSet = new SuffixSet();
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (((FsDriver)localEntry.getValue()).isFederated()) {
        localSuffixSet.add(((FsScheme)localEntry.getKey()).toString());
      }
    }
    return localSuffixSet;
  }
  
  public TArchiveDetector(String paramString)
  {
    this(FsDriverLocator.SINGLETON, paramString);
  }
  
  public TArchiveDetector(FsDriverProvider paramFsDriverProvider, String paramString)
  {
    SuffixSet localSuffixSet1 = extensions(paramFsDriverProvider);
    SuffixSet localSuffixSet2;
    if (null == paramString)
    {
      localSuffixSet2 = localSuffixSet1;
    }
    else
    {
      localSuffixSet2 = new SuffixSet(paramString);
      if (localSuffixSet2.retainAll(localSuffixSet1))
      {
        localSuffixSet2 = new SuffixSet(paramString);
        localSuffixSet2.removeAll(localSuffixSet1);
        assert (!localSuffixSet2.isEmpty());
        throw new IllegalArgumentException("\"" + localSuffixSet2 + "\" (no archive driver installed for these extensions)");
      }
    }
    this.suffixes = localSuffixSet2;
    this.drivers = paramFsDriverProvider.get();
  }
  
  public Map get()
  {
    return this.drivers;
  }
  
  public FsScheme getScheme(String paramString)
  {
    paramString = paramString.replace('/', File.separatorChar);
    int i = paramString.lastIndexOf(File.separatorChar) + 1;
    paramString = paramString.substring(i);
    int j = paramString.length();
    i = 0;
    while ((0 < (i = paramString.indexOf('.', i) + 1)) && (i < j))
    {
      String str = paramString.substring(i);
      if (this.suffixes.contains(str)) {
        try
        {
          return new FsScheme(str);
        }
        catch (URISyntaxException localURISyntaxException) {}
      }
    }
    return null;
  }
  
  public String toString()
  {
    return this.suffixes.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\file\TArchiveDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */