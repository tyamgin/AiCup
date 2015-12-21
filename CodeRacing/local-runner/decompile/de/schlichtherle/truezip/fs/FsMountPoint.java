package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.QuotedUriSyntaxException;
import de.schlichtherle.truezip.util.UriBuilder;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public final class FsMountPoint
  implements Serializable, Comparable
{
  private URI uri;
  private transient FsPath path;
  private volatile transient FsScheme scheme;
  private volatile transient URI hierarchical;
  
  public FsMountPoint(URI paramURI, FsUriModifier paramFsUriModifier)
    throws URISyntaxException
  {
    parse(paramURI, paramFsUriModifier);
  }
  
  public FsMountPoint(FsScheme paramFsScheme, FsPath paramFsPath)
    throws URISyntaxException
  {
    URI localURI = paramFsPath.toUri();
    if (!localURI.isAbsolute()) {
      throw new QuotedUriSyntaxException(localURI, "Path not absolute");
    }
    String str = paramFsPath.getEntryName().toUri().getPath();
    if (0 == str.length()) {
      throw new QuotedUriSyntaxException(localURI, "Empty entry name");
    }
    this.uri = new UriBuilder(true).scheme(paramFsScheme.toString()).path(localURI.getScheme() + ':' + localURI.getRawSchemeSpecificPart() + "!/").toUri();
    this.scheme = paramFsScheme;
    this.path = paramFsPath;
    assert (invariants());
  }
  
  private void parse(URI paramURI, FsUriModifier paramFsUriModifier)
    throws URISyntaxException
  {
    paramURI = paramFsUriModifier.modify(paramURI, FsUriModifier.PostFix.MOUNT_POINT);
    if (null != paramURI.getRawQuery()) {
      throw new QuotedUriSyntaxException(paramURI, "Query not allowed");
    }
    if (null != paramURI.getRawFragment()) {
      throw new QuotedUriSyntaxException(paramURI, "Fragment not allowed");
    }
    if (paramURI.isOpaque())
    {
      String str = paramURI.getRawSchemeSpecificPart();
      int i = str.lastIndexOf("!/");
      if (str.length() - 2 != i) {
        throw new QuotedUriSyntaxException(paramURI, "Doesn't end with mount point separator \"!/\"");
      }
      this.path = new FsPath(new URI(str.substring(0, i)), paramFsUriModifier);
      URI localURI1 = this.path.toUri();
      if (!localURI1.isAbsolute()) {
        throw new QuotedUriSyntaxException(paramURI, "Path not absolute");
      }
      if (0 == this.path.getEntryName().getPath().length()) {
        throw new QuotedUriSyntaxException(paramURI, "Empty URI path of entry name of path");
      }
      if (FsUriModifier.NULL != paramFsUriModifier)
      {
        URI localURI2 = new UriBuilder(true).scheme(paramURI.getScheme()).path(localURI1.getScheme() + ':' + localURI1.getRawSchemeSpecificPart() + "!/").toUri();
        if (!paramURI.equals(localURI2)) {
          paramURI = localURI2;
        }
      }
    }
    else
    {
      if (!paramURI.isAbsolute()) {
        throw new QuotedUriSyntaxException(paramURI, "Not absolute");
      }
      if (!paramURI.getRawPath().endsWith("/")) {
        throw new QuotedUriSyntaxException(paramURI, "URI path doesn't end with separator \"/\"");
      }
      this.path = null;
    }
    this.uri = paramURI;
    assert (invariants());
  }
  
  private boolean invariants()
  {
    assert (null != toUri());
    assert (toUri().isAbsolute());
    assert (null == toUri().getRawQuery());
    assert (null == toUri().getRawFragment());
    if (toUri().isOpaque())
    {
      assert (toUri().getRawSchemeSpecificPart().endsWith("!/"));
      assert (null != getPath());
      assert (getPath().toUri().isAbsolute());
      assert (null == getPath().toUri().getRawFragment());
      if ((!$assertionsDisabled) && (0 == getPath().getEntryName().toUri().getRawPath().length())) {
        throw new AssertionError();
      }
    }
    else
    {
      assert (toUri().normalize() == toUri());
      assert (toUri().getRawPath().endsWith("/"));
      assert (null == getPath());
    }
    return true;
  }
  
  public URI toUri()
  {
    return this.uri;
  }
  
  public URI toHierarchicalUri()
  {
    URI localURI = this.hierarchical;
    return null != localURI ? localURI : (this.hierarchical = this.uri.isOpaque() ? this.path.toHierarchicalUri() : this.uri);
  }
  
  public FsScheme getScheme()
  {
    FsScheme localFsScheme = this.scheme;
    return null != localFsScheme ? localFsScheme : (this.scheme = FsScheme.create(this.uri.getScheme()));
  }
  
  public FsPath getPath()
  {
    return this.path;
  }
  
  public FsMountPoint getParent()
  {
    assert ((null == this.path) || (null != this.path.getMountPoint()));
    return null == this.path ? null : this.path.getMountPoint();
  }
  
  public boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof FsMountPoint)) && (this.uri.equals(((FsMountPoint)paramObject).uri)));
  }
  
  public int compareTo(FsMountPoint paramFsMountPoint)
  {
    return this.uri.compareTo(paramFsMountPoint.uri);
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsMountPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */