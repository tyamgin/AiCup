package de.schlichtherle.truezip.file;

import de.schlichtherle.truezip.entry.Entry.Access;
import de.schlichtherle.truezip.entry.Entry.Size;
import de.schlichtherle.truezip.entry.Entry.Type;
import de.schlichtherle.truezip.fs.FsController;
import de.schlichtherle.truezip.fs.FsEntry;
import de.schlichtherle.truezip.fs.FsEntryName;
import de.schlichtherle.truezip.fs.FsManager;
import de.schlichtherle.truezip.fs.FsModel;
import de.schlichtherle.truezip.fs.FsMountPoint;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.FsPath;
import de.schlichtherle.truezip.fs.FsScheme;
import de.schlichtherle.truezip.fs.FsUriModifier;
import de.schlichtherle.truezip.io.Paths;
import de.schlichtherle.truezip.io.Paths.Splitter;
import de.schlichtherle.truezip.util.BitField;
import de.schlichtherle.truezip.util.UriBuilder;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.Set;
import java.util.TreeSet;

public final class TFile
  extends File
{
  private static final String UNC_PREFIX = separator + separator;
  private static final Set ROOTS = Collections.unmodifiableSet(new TreeSet(Arrays.asList(listRoots())));
  private static final File CURRENT_DIRECTORY = new File(".");
  private transient File file;
  private transient TArchiveDetector detector;
  private transient TFile innerArchive;
  private transient TFile enclArchive;
  private transient FsEntryName enclEntryName;
  private volatile transient FsController controller;
  
  public TFile(File paramFile)
  {
    this(paramFile, (TArchiveDetector)null);
  }
  
  public TFile(File paramFile, TArchiveDetector paramTArchiveDetector)
  {
    super(paramFile.getPath());
    if ((paramFile instanceof TFile))
    {
      TFile localTFile = (TFile)paramFile;
      this.file = localTFile.file;
      this.detector = localTFile.detector;
      this.enclArchive = localTFile.enclArchive;
      this.enclEntryName = localTFile.enclEntryName;
      this.innerArchive = (localTFile.isArchive() ? this : localTFile.innerArchive);
      this.controller = localTFile.controller;
    }
    else
    {
      this.file = paramFile;
      this.detector = (null != paramTArchiveDetector ? paramTArchiveDetector : TConfig.get().getArchiveDetector());
      scan(null);
    }
    assert (invariants());
  }
  
  public TFile(String paramString, TArchiveDetector paramTArchiveDetector)
  {
    super(paramString);
    this.file = new File(paramString);
    this.detector = (null != paramTArchiveDetector ? paramTArchiveDetector : TConfig.get().getArchiveDetector());
    scan(null);
    assert (invariants());
  }
  
  public TFile(File paramFile, String paramString)
  {
    this(paramFile, paramString, null);
  }
  
  public TFile(File paramFile, String paramString, TArchiveDetector paramTArchiveDetector)
  {
    super(paramFile, paramString);
    this.file = new File(paramFile, paramString);
    if ((paramFile instanceof TFile))
    {
      TFile localTFile = (TFile)paramFile;
      this.detector = (null != paramTArchiveDetector ? paramTArchiveDetector : localTFile.detector);
      scan(localTFile);
    }
    else
    {
      this.detector = (null != paramTArchiveDetector ? paramTArchiveDetector : TConfig.get().getArchiveDetector());
      scan(null);
    }
    assert (invariants());
  }
  
  private TFile(File paramFile, TFile paramTFile, TArchiveDetector paramTArchiveDetector)
  {
    super(paramFile.getPath());
    this.file = paramFile;
    String str = paramFile.getPath();
    if (null != paramTFile)
    {
      int i = paramTFile.getPath().length();
      if (str.length() == i)
      {
        this.detector = paramTFile.detector;
        this.enclArchive = paramTFile.enclArchive;
        this.enclEntryName = paramTFile.enclEntryName;
        this.innerArchive = this;
        this.controller = paramTFile.controller;
      }
      else
      {
        this.detector = paramTArchiveDetector;
        this.innerArchive = (this.enclArchive = paramTFile);
        try
        {
          this.enclEntryName = new FsEntryName(new UriBuilder().path(str.substring(i + 1).replace(separatorChar, '/')).getUri(), FsUriModifier.CANONICALIZE);
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw new AssertionError(localURISyntaxException);
        }
      }
    }
    else
    {
      this.detector = paramTArchiveDetector;
    }
    assert (invariants());
  }
  
  private void scan(TFile paramTFile)
  {
    String str = super.getPath();
    assert ((paramTFile == null) || (str.startsWith(paramTFile.getPath())));
    assert (this.file.getPath().equals(str));
    assert (null != this.detector);
    StringBuilder localStringBuilder = new StringBuilder(str.length());
    scan(paramTFile, this.detector, 0, str, localStringBuilder, new Paths.Splitter(separatorChar, false));
    try
    {
      this.enclEntryName = (0 >= localStringBuilder.length() ? null : new FsEntryName(new UriBuilder().path(localStringBuilder.toString()).getUri(), FsUriModifier.CANONICALIZE));
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new AssertionError(localURISyntaxException);
    }
  }
  
  private void scan(TFile paramTFile, TArchiveDetector paramTArchiveDetector, int paramInt, String paramString, StringBuilder paramStringBuilder, Paths.Splitter paramSplitter)
  {
    if (paramString == null)
    {
      assert (null == this.enclArchive);
      paramStringBuilder.setLength(0);
      return;
    }
    paramSplitter.split(paramString);
    String str1 = paramSplitter.getParentPath();
    String str2 = paramSplitter.getMemberName();
    if ((0 != str2.length()) && (!".".equals(str2))) {
      if ("..".equals(str2))
      {
        paramInt++;
      }
      else if (0 < paramInt)
      {
        paramInt--;
      }
      else
      {
        if (null != paramTFile)
        {
          i = paramString.length();
          int j = paramTFile.getPath().length();
          if (i == j)
          {
            this.enclArchive = paramTFile.innerArchive;
            if (!paramTFile.isArchive())
            {
              if (paramTFile.isEntry())
              {
                assert (null != paramTFile.enclEntryName);
                if (0 < paramStringBuilder.length())
                {
                  paramStringBuilder.insert(0, '/');
                  paramStringBuilder.insert(0, paramTFile.enclEntryName.getPath());
                }
                else
                {
                  assert (this.enclArchive == paramTFile.enclArchive);
                  paramStringBuilder.append(paramTFile.enclEntryName.getPath());
                }
              }
              else
              {
                assert (null == this.enclArchive);
                paramStringBuilder.setLength(0);
              }
            }
            else if (0 >= paramStringBuilder.length())
            {
              assert (this.enclArchive == paramTFile);
              this.innerArchive = this;
              this.enclArchive = paramTFile.enclArchive;
              if (paramTFile.enclEntryName != null) {
                paramStringBuilder.append(paramTFile.enclEntryName.getPath());
              }
            }
            if (this != this.innerArchive) {
              this.innerArchive = this.enclArchive;
            }
            return;
          }
          if (i < j)
          {
            paramTArchiveDetector = paramTFile.detector;
            paramTFile = paramTFile.enclArchive;
          }
        }
        int i = null != paramTArchiveDetector.getScheme(paramString) ? 1 : 0;
        if (0 < paramStringBuilder.length())
        {
          if (i != 0)
          {
            this.enclArchive = new TFile(paramString, paramTArchiveDetector);
            if (this.innerArchive != this) {
              this.innerArchive = this.enclArchive;
            }
            return;
          }
          paramStringBuilder.insert(0, '/');
          paramStringBuilder.insert(0, str2);
        }
        else
        {
          if (i != 0) {
            this.innerArchive = this;
          }
          paramStringBuilder.append(str2);
        }
      }
    }
    scan(paramTFile, paramTArchiveDetector, paramInt, str1, paramStringBuilder, paramSplitter);
  }
  
  private boolean invariants()
  {
    File localFile = this.file;
    TFile localTFile1 = this.innerArchive;
    TFile localTFile2 = this.enclArchive;
    FsEntryName localFsEntryName = this.enclEntryName;
    assert (null != localFile);
    assert (!(localFile instanceof TFile));
    assert (localFile.getPath().equals(super.getPath()));
    assert (null != this.detector);
    if (!$assertionsDisabled) {
      if ((null != localTFile1 ? 1 : 0) != (getInnerEntryName() != null ? 1 : 0)) {
        throw new AssertionError();
      }
    }
    if (!$assertionsDisabled) {
      if ((null != localTFile2 ? 1 : 0) != (localFsEntryName != null ? 1 : 0)) {
        throw new AssertionError();
      }
    }
    assert (this != localTFile2);
    if (!$assertionsDisabled) {
      if (((this == localTFile1 ? 1 : 0) ^ ((localTFile1 == localTFile2) && (null == this.controller) ? 1 : 0)) == 0) {
        throw new AssertionError();
      }
    }
    assert ((null == localTFile2) || ((Paths.contains(localTFile2.getPath(), localFile.getParentFile().getPath(), separatorChar)) && (!localFsEntryName.toString().isEmpty())));
    return true;
  }
  
  public String getParent()
  {
    return this.file.getParent();
  }
  
  public TFile getParentFile()
  {
    File localFile = this.file.getParentFile();
    if (localFile == null) {
      return null;
    }
    TFile localTFile = this.enclArchive;
    if ((null != localTFile) && (localTFile.getPath().length() == localFile.getPath().length()))
    {
      assert (localTFile.getPath().equals(localFile.getPath()));
      return localTFile;
    }
    return new TFile(localFile, localTFile, this.detector);
  }
  
  public TFile getAbsoluteFile()
  {
    String str = getAbsolutePath();
    return str.equals(getPath()) ? this : new TFile(str, this.detector);
  }
  
  public String getAbsolutePath()
  {
    return this.file.getAbsolutePath();
  }
  
  public TFile getCanonicalFile()
    throws IOException
  {
    String str = getCanonicalPath();
    return str.equals(getPath()) ? this : new TFile(str, this.detector);
  }
  
  public String getCanonicalPath()
    throws IOException
  {
    return this.file.getCanonicalPath();
  }
  
  public String getPath()
  {
    return this.file.getPath();
  }
  
  public String getName()
  {
    return this.file.getName();
  }
  
  public TArchiveDetector getArchiveDetector()
  {
    return this.detector;
  }
  
  public boolean isArchive()
  {
    return this == this.innerArchive;
  }
  
  public boolean isEntry()
  {
    return this.enclEntryName != null;
  }
  
  public TFile getInnerArchive()
  {
    return this.innerArchive;
  }
  
  public String getInnerEntryName()
  {
    FsEntryName localFsEntryName;
    return null == (localFsEntryName = this.enclEntryName) ? null : this == this.innerArchive ? FsEntryName.ROOT.getPath() : localFsEntryName.getPath();
  }
  
  FsEntryName getInnerFsEntryName()
  {
    return this == this.innerArchive ? FsEntryName.ROOT : this.enclEntryName;
  }
  
  public TFile getEnclArchive()
  {
    return this.enclArchive;
  }
  
  public String getEnclEntryName()
  {
    return null == this.enclEntryName ? null : this.enclEntryName.getPath();
  }
  
  public TFile getTopLevelArchive()
  {
    TFile localTFile = this.enclArchive;
    return null != localTFile ? localTFile.getTopLevelArchive() : this.innerArchive;
  }
  
  @Deprecated
  public File getFile()
  {
    return this.file;
  }
  
  FsController getController()
  {
    FsController localFsController = this.controller;
    if ((this != this.innerArchive) || (null != localFsController)) {
      return localFsController;
    }
    File localFile = this.file;
    String str = Paths.normalize(localFile.getPath(), separatorChar);
    FsScheme localFsScheme = this.detector.getScheme(str);
    if (null == localFsScheme) {
      throw new ServiceConfigurationError("Unknown file system scheme for path \"" + str + "\"! Check run-time class path configuration.");
    }
    FsMountPoint localFsMountPoint;
    try
    {
      TFile localTFile = this.enclArchive;
      FsEntryName localFsEntryName = this.enclEntryName;
      if (!$assertionsDisabled) {
        if ((null != localTFile ? 1 : 0) != (null != localFsEntryName ? 1 : 0)) {
          throw new AssertionError();
        }
      }
      localFsMountPoint = new FsMountPoint(localFsScheme, null == localTFile ? new FsPath(localFile) : new FsPath(localTFile.getController().getModel().getMountPoint(), localFsEntryName));
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new AssertionError(localURISyntaxException);
    }
    return this.controller = getController(localFsMountPoint);
  }
  
  private FsController getController(FsMountPoint paramFsMountPoint)
  {
    return TConfig.get().getFsManager().getController(paramFsMountPoint, this.detector);
  }
  
  public boolean isAbsolute()
  {
    return this.file.isAbsolute();
  }
  
  public boolean isHidden()
  {
    return this.file.isHidden();
  }
  
  public int hashCode()
  {
    return this.file.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return this.file.equals(paramObject);
  }
  
  public int compareTo(File paramFile)
  {
    return this.file.compareTo(paramFile);
  }
  
  public String toString()
  {
    return this.file.toString();
  }
  
  @Deprecated
  public URL toURL()
    throws MalformedURLException
  {
    return null != this.innerArchive ? toURI().toURL() : this.file.toURL();
  }
  
  public URI toURI()
  {
    try
    {
      if (this == this.innerArchive)
      {
        FsScheme localFsScheme = getScheme();
        if (null != this.enclArchive)
        {
          assert (null != this.enclEntryName);
          return new FsMountPoint(localFsScheme, new FsPath(new FsMountPoint(this.enclArchive.toURI(), FsUriModifier.CANONICALIZE), this.enclEntryName)).toUri();
        }
        return new FsMountPoint(localFsScheme, new FsPath(this.file)).toUri();
      }
      if (null != this.enclArchive)
      {
        assert (null != this.enclEntryName);
        return new FsPath(new FsMountPoint(this.enclArchive.toURI(), FsUriModifier.CANONICALIZE), this.enclEntryName).toUri();
      }
      return this.file.toURI();
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new AssertionError(localURISyntaxException);
    }
  }
  
  private FsScheme getScheme()
  {
    if (this != this.innerArchive) {
      return null;
    }
    FsController localFsController = this.controller;
    if (null != localFsController) {
      return localFsController.getModel().getMountPoint().getScheme();
    }
    return this.detector.getScheme(this.file.getPath());
  }
  
  @Deprecated
  public Path toPath()
  {
    throw new UnsupportedOperationException("Use a Path constructor or method instead!");
  }
  
  public boolean exists()
  {
    if (null != this.innerArchive) {
      try
      {
        FsEntry localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
        return null != localFsEntry;
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.exists();
  }
  
  public boolean isFile()
  {
    if (null != this.innerArchive) {
      try
      {
        FsEntry localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
        return (null != localFsEntry) && (localFsEntry.isType(Entry.Type.FILE));
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.isFile();
  }
  
  public boolean isDirectory()
  {
    if (null != this.innerArchive) {
      try
      {
        FsEntry localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
        return (null != localFsEntry) && (localFsEntry.isType(Entry.Type.DIRECTORY));
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.isDirectory();
  }
  
  public boolean canRead()
  {
    if (null != this.innerArchive) {
      try
      {
        return this.innerArchive.getController().isReadable(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.canRead();
  }
  
  public boolean canWrite()
  {
    if (null != this.innerArchive) {
      try
      {
        return this.innerArchive.getController().isWritable(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.canWrite();
  }
  
  public boolean canExecute()
  {
    if (null != this.innerArchive) {
      try
      {
        return this.innerArchive.getController().isExecutable(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.canExecute();
  }
  
  public boolean setReadOnly()
  {
    if (null != this.innerArchive) {
      try
      {
        this.innerArchive.getController().setReadOnly(getInnerFsEntryName());
        return true;
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.setReadOnly();
  }
  
  public long length()
  {
    if (null != this.innerArchive)
    {
      FsEntry localFsEntry;
      try
      {
        localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return 0L;
      }
      if (null == localFsEntry) {
        return 0L;
      }
      long l = localFsEntry.getSize(Entry.Size.DATA);
      return -1L != l ? l : 0L;
    }
    return this.file.length();
  }
  
  public long lastModified()
  {
    if (null != this.innerArchive)
    {
      FsEntry localFsEntry;
      try
      {
        localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return 0L;
      }
      if (null == localFsEntry) {
        return 0L;
      }
      long l = localFsEntry.getTime(Entry.Access.WRITE);
      return -1L != l ? l : 0L;
    }
    return this.file.lastModified();
  }
  
  public boolean setLastModified(long paramLong)
  {
    if (null != this.innerArchive) {
      try
      {
        this.innerArchive.getController().setTime(getInnerFsEntryName(), BitField.of(Entry.Access.WRITE), paramLong, TConfig.get().getOutputPreferences());
        return true;
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.setLastModified(paramLong);
  }
  
  public String[] list()
  {
    if (null != this.innerArchive)
    {
      FsEntry localFsEntry;
      try
      {
        localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return null;
      }
      if (null == localFsEntry) {
        return null;
      }
      Set localSet = localFsEntry.getMembers();
      return null == localSet ? null : (String[])localSet.toArray(new String[localSet.size()]);
    }
    return this.file.list();
  }
  
  public String[] list(FilenameFilter paramFilenameFilter)
  {
    if (null != this.innerArchive)
    {
      FsEntry localFsEntry;
      try
      {
        localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return null;
      }
      Set localSet = members(localFsEntry);
      if (null == localSet) {
        return null;
      }
      if (null == paramFilenameFilter) {
        return (String[])localSet.toArray(new String[localSet.size()]);
      }
      ArrayList localArrayList = new ArrayList(localSet.size());
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (paramFilenameFilter.accept(this, str)) {
          localArrayList.add(str);
        }
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    return this.file.list(paramFilenameFilter);
  }
  
  public TFile[] listFiles()
  {
    return listFiles((FilenameFilter)null, this.detector);
  }
  
  public TFile[] listFiles(FilenameFilter paramFilenameFilter)
  {
    return listFiles(paramFilenameFilter, this.detector);
  }
  
  public TFile[] listFiles(FilenameFilter paramFilenameFilter, TArchiveDetector paramTArchiveDetector)
  {
    if (null != this.innerArchive)
    {
      FsEntry localFsEntry;
      try
      {
        localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return null;
      }
      return filter(members(localFsEntry), paramFilenameFilter, paramTArchiveDetector);
    }
    return filter(list(this.file.list(paramFilenameFilter)), (FilenameFilter)null, paramTArchiveDetector);
  }
  
  private static Set members(FsEntry paramFsEntry)
  {
    return null == paramFsEntry ? null : paramFsEntry.getMembers();
  }
  
  private static List list(String[] paramArrayOfString)
  {
    return null == paramArrayOfString ? null : Arrays.asList(paramArrayOfString);
  }
  
  private TFile[] filter(Collection paramCollection, FilenameFilter paramFilenameFilter, TArchiveDetector paramTArchiveDetector)
  {
    if (null == paramCollection) {
      return null;
    }
    if (null != paramFilenameFilter)
    {
      localObject1 = new ArrayList(paramCollection.size());
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        if (paramFilenameFilter.accept(this, (String)localObject2)) {
          ((Collection)localObject1).add(new TFile(this, (String)localObject2, paramTArchiveDetector));
        }
      }
      return (TFile[])((Collection)localObject1).toArray(new TFile[((Collection)localObject1).size()]);
    }
    Object localObject1 = new TFile[paramCollection.size()];
    int i = 0;
    Object localObject2 = paramCollection.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      String str = (String)((Iterator)localObject2).next();
      localObject1[(i++)] = new TFile(this, str, paramTArchiveDetector);
    }
    return (TFile[])localObject1;
  }
  
  public TFile[] listFiles(FileFilter paramFileFilter)
  {
    return listFiles(paramFileFilter, this.detector);
  }
  
  public TFile[] listFiles(FileFilter paramFileFilter, TArchiveDetector paramTArchiveDetector)
  {
    if (null != this.innerArchive)
    {
      FsEntry localFsEntry;
      try
      {
        localFsEntry = this.innerArchive.getController().getEntry(getInnerFsEntryName());
      }
      catch (IOException localIOException)
      {
        return null;
      }
      return filter(members(localFsEntry), paramFileFilter, paramTArchiveDetector);
    }
    return filter(list(this.file.list()), paramFileFilter, paramTArchiveDetector);
  }
  
  private TFile[] filter(Collection paramCollection, FileFilter paramFileFilter, TArchiveDetector paramTArchiveDetector)
  {
    if (null == paramCollection) {
      return null;
    }
    Object localObject3;
    if (null != paramFileFilter)
    {
      localObject1 = new ArrayList(paramCollection.size());
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        localObject3 = new TFile(this, (String)localObject2, paramTArchiveDetector);
        if (paramFileFilter.accept((File)localObject3)) {
          ((Collection)localObject1).add(localObject3);
        }
      }
      return (TFile[])((Collection)localObject1).toArray(new TFile[((Collection)localObject1).size()]);
    }
    Object localObject1 = new TFile[paramCollection.size()];
    int i = 0;
    Object localObject2 = paramCollection.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (String)((Iterator)localObject2).next();
      localObject1[(i++)] = new TFile(this, (String)localObject3, paramTArchiveDetector);
    }
    return (TFile[])localObject1;
  }
  
  public boolean createNewFile()
    throws IOException
  {
    if (null != this.innerArchive)
    {
      FsController localFsController = this.innerArchive.getController();
      FsEntryName localFsEntryName = getInnerFsEntryName();
      if (null != localFsController.getEntry(localFsEntryName)) {
        return false;
      }
      localFsController.mknod(localFsEntryName, Entry.Type.FILE, TConfig.get().getOutputPreferences().set(FsOutputOption.EXCLUSIVE), null);
      return true;
    }
    return this.file.createNewFile();
  }
  
  public boolean mkdirs()
  {
    if (null == this.innerArchive) {
      return this.file.mkdirs();
    }
    TFile localTFile = getParentFile();
    if ((null != localTFile) && (!localTFile.exists())) {
      localTFile.mkdirs();
    }
    return mkdir();
  }
  
  public boolean mkdir()
  {
    if (null != this.innerArchive) {
      try
      {
        this.innerArchive.getController().mknod(getInnerFsEntryName(), Entry.Type.DIRECTORY, TConfig.get().getOutputPreferences(), null);
        return true;
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return this.file.mkdir();
  }
  
  @Deprecated
  public boolean delete()
  {
    try
    {
      rm(this);
      return true;
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  public static void rm(File paramFile)
    throws IOException
  {
    if ((paramFile instanceof TFile))
    {
      TFile localTFile = (TFile)paramFile;
      if (null != localTFile.innerArchive)
      {
        localTFile.innerArchive.getController().unlink(localTFile.getInnerFsEntryName(), TConfig.get().getOutputPreferences());
        return;
      }
      paramFile = localTFile.file;
    }
    if (!paramFile.delete()) {
      throw new IOException(paramFile + " (cannot delete)");
    }
  }
  
  public void deleteOnExit()
  {
    if (this.innerArchive != null) {
      throw new UnsupportedOperationException();
    }
    this.file.deleteOnExit();
  }
  
  @Deprecated
  public boolean renameTo(File paramFile)
  {
    try
    {
      mv(this, paramFile, this.detector);
      return true;
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  public static void mv(File paramFile1, File paramFile2, TArchiveDetector paramTArchiveDetector)
    throws IOException
  {
    int i;
    File localFile1;
    if ((paramFile1 instanceof TFile))
    {
      TFile localTFile1 = (TFile)paramFile1;
      i = null != localTFile1.getInnerArchive() ? 1 : 0;
      localFile1 = localTFile1.getFile();
    }
    else
    {
      i = 0;
      localFile1 = paramFile1;
    }
    int j;
    File localFile2;
    if ((paramFile2 instanceof TFile))
    {
      TFile localTFile2 = (TFile)paramFile2;
      j = null != localTFile2.getInnerArchive() ? 1 : 0;
      localFile2 = localTFile2.getFile();
    }
    else
    {
      j = 0;
      localFile2 = paramFile2;
    }
    if ((i == 0) && (j == 0))
    {
      if (localFile1.renameTo(localFile2)) {
        return;
      }
      throw new IOException(paramFile1 + " (cannot rename to " + paramFile2 + ")");
    }
    TBIO.mv(paramFile1, paramFile2, paramTArchiveDetector);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\file\TFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */