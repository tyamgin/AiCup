package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.QuotedUriSyntaxException;
import de.schlichtherle.truezip.util.UriBuilder;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public final class FsPath
  implements Serializable, Comparable
{
  private static final URI DOT = URI.create(".");
  private URI uri;
  private transient FsMountPoint mountPoint;
  private transient FsEntryName entryName;
  private volatile transient URI hierarchical;
  
  public FsPath(File paramFile)
  {
    try
    {
      parse(paramFile.toURI(), FsUriModifier.CANONICALIZE);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new AssertionError(localURISyntaxException);
    }
  }
  
  public FsPath(URI paramURI, FsUriModifier paramFsUriModifier)
    throws URISyntaxException
  {
    parse(paramURI, paramFsUriModifier);
  }
  
  public FsPath(FsMountPoint paramFsMountPoint, FsEntryName paramFsEntryName)
  {
    if (null == paramFsMountPoint)
    {
      this.uri = paramFsEntryName.toUri();
    }
    else if (paramFsEntryName.isRoot())
    {
      this.uri = paramFsMountPoint.toUri();
    }
    else
    {
      URI localURI1;
      if ((localURI1 = paramFsMountPoint.toUri()).isOpaque()) {
        try
        {
          String str1 = localURI1.getRawSchemeSpecificPart();
          int i = str1.length();
          URI localURI2 = paramFsEntryName.toUri();
          String str2 = localURI2.getRawPath();
          int j = str2.length();
          String str3 = localURI2.getRawQuery();
          int k = null == str3 ? 0 : str3.length() + 1;
          StringBuilder localStringBuilder = new StringBuilder(i + j + k).append(str1).append(str2);
          if (null != str3) {
            localStringBuilder.append('?').append(str3);
          }
          this.uri = new UriBuilder(true).scheme(localURI1.getScheme()).path(localStringBuilder.toString()).fragment(localURI2.getRawFragment()).getUri();
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw new AssertionError(localURISyntaxException);
        }
      } else {
        this.uri = localURI1.resolve(paramFsEntryName.toUri());
      }
    }
    this.mountPoint = paramFsMountPoint;
    this.entryName = paramFsEntryName;
    assert (invariants());
  }
  
  private void parse(URI paramURI, FsUriModifier paramFsUriModifier)
    throws URISyntaxException
  {
    paramURI = paramFsUriModifier.modify(paramURI, FsUriModifier.PostFix.PATH);
    if (null != paramURI.getRawFragment()) {
      throw new QuotedUriSyntaxException(paramURI, "Fragment not allowed");
    }
    if (paramURI.isOpaque())
    {
      String str = paramURI.getRawSchemeSpecificPart();
      int i = str.lastIndexOf("!/");
      if (0 > i) {
        throw new QuotedUriSyntaxException(paramURI, "Missing mount point separator \"!/\"");
      }
      UriBuilder localUriBuilder = new UriBuilder(true);
      this.mountPoint = new FsMountPoint(localUriBuilder.scheme(paramURI.getScheme()).path(str.substring(0, i + 2)).toUri(), paramFsUriModifier);
      this.entryName = new FsEntryName(localUriBuilder.clear().pathQuery(str.substring(i + 2)).fragment(paramURI.getRawFragment()).toUri(), paramFsUriModifier);
      if (FsUriModifier.NULL != paramFsUriModifier)
      {
        URI localURI1 = this.mountPoint.toUri();
        URI localURI2 = new URI(localURI1.getScheme() + ':' + localURI1.getRawSchemeSpecificPart() + this.entryName.toUri());
        if (!paramURI.equals(localURI2)) {
          paramURI = localURI2;
        }
      }
    }
    else if (paramURI.isAbsolute())
    {
      this.mountPoint = new FsMountPoint(paramURI.resolve(DOT), paramFsUriModifier);
      this.entryName = new FsEntryName(this.mountPoint.toUri().relativize(paramURI), paramFsUriModifier);
    }
    else
    {
      this.mountPoint = null;
      this.entryName = new FsEntryName(paramURI, paramFsUriModifier);
      if (FsUriModifier.NULL != paramFsUriModifier) {
        paramURI = this.entryName.toUri();
      }
    }
    this.uri = paramURI;
    assert (invariants());
  }
  
  private boolean invariants()
  {
    assert (null != toUri());
    assert (null == toUri().getRawFragment());
    if (!$assertionsDisabled) {
      if ((null != getMountPoint()) != toUri().isAbsolute()) {
        throw new AssertionError();
      }
    }
    assert (null != getEntryName());
    if (toUri().isOpaque())
    {
      if ((!$assertionsDisabled) && (!toUri().getRawSchemeSpecificPart().contains("!/"))) {
        throw new AssertionError();
      }
    }
    else if (toUri().isAbsolute())
    {
      assert (toUri().normalize() == toUri());
      if ((!$assertionsDisabled) && (!toUri().equals(getMountPoint().toUri().resolve(getEntryName().toUri())))) {
        throw new AssertionError();
      }
    }
    else
    {
      assert (toUri().normalize() == toUri());
      assert (getEntryName().toUri() == toUri());
    }
    return true;
  }
  
  public URI toUri()
  {
    return this.uri;
  }
  
  public URI toHierarchicalUri()
  {
    URI localURI1 = this.hierarchical;
    if (null != localURI1) {
      return localURI1;
    }
    if (this.uri.isOpaque())
    {
      URI localURI2 = this.mountPoint.toHierarchicalUri();
      URI localURI3 = this.entryName.toUri();
      try
      {
        return this.hierarchical = localURI3.toString().isEmpty() ? localURI2 : new UriBuilder(localURI2, true).path(localURI2.getRawPath() + "/").getUri().resolve(localURI3);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new AssertionError(localURISyntaxException);
      }
    }
    return this.hierarchical = this.uri;
  }
  
  public FsMountPoint getMountPoint()
  {
    return this.mountPoint;
  }
  
  public FsEntryName getEntryName()
  {
    return this.entryName;
  }
  
  public boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof FsPath)) && (this.uri.equals(((FsPath)paramObject).uri)));
  }
  
  public int compareTo(FsPath paramFsPath)
  {
    return this.uri.compareTo(paramFsPath.uri);
  }
  
  public int hashCode()
  {
    return this.uri.hashCode();
  }
  
  public String toString()
  {
    return this.uri.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */