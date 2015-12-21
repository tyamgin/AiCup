package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.FilteringIterator;
import java.net.URI;
import java.util.Iterator;

public final class FsFilteringManager
  extends FsDecoratingManager
{
  private final URI prefix;
  
  public FsFilteringManager(FsManager paramFsManager, FsMountPoint paramFsMountPoint)
  {
    super(paramFsManager);
    this.prefix = paramFsMountPoint.toHierarchicalUri();
  }
  
  public int getSize()
  {
    int i = 0;
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      FsController localFsController = (FsController)localIterator.next();
      i++;
    }
    return i;
  }
  
  public Iterator iterator()
  {
    return new FilteredControllerIterator();
  }
  
  private final class FilteredControllerIterator
    extends FilteringIterator
  {
    final String ps = FsFilteringManager.this.prefix.getScheme();
    final String pp = FsFilteringManager.this.prefix.getPath();
    final int ppl = this.pp.length();
    final boolean pps = '/' == this.pp.charAt(this.ppl - 1);
    
    FilteredControllerIterator()
    {
      super();
    }
    
    protected boolean accept(FsController paramFsController)
    {
      assert (null != paramFsController) : "null elements are not allowed in this collection!";
      URI localURI = paramFsController.getModel().getMountPoint().toHierarchicalUri();
      String str;
      return (localURI.getScheme().equals(this.ps)) && ((str = localURI.getPath()).startsWith(this.pp)) && ((this.pps) || (str.length() == this.ppl) || ('/' == str.charAt(this.ppl)));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsFilteringManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */