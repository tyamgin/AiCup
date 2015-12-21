package de.schlichtherle.truezip.fs;

public abstract class FsModel
{
  private final FsMountPoint mountPoint;
  private final FsModel parent;
  
  protected FsModel(FsMountPoint paramFsMountPoint, FsModel paramFsModel)
  {
    if (!equals(paramFsMountPoint.getParent(), null == paramFsModel ? null : paramFsModel.getMountPoint())) {
      throw new IllegalArgumentException("Parent/Member mismatch!");
    }
    this.mountPoint = paramFsMountPoint;
    this.parent = paramFsModel;
  }
  
  private static boolean equals(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((null != paramObject1) && (paramObject1.equals(paramObject2)));
  }
  
  public final FsMountPoint getMountPoint()
  {
    return this.mountPoint;
  }
  
  public final FsModel getParent()
  {
    return this.parent;
  }
  
  public abstract boolean isMounted();
  
  public final boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString()
  {
    return String.format("%s[mountPoint=%s, parent=%s, mounted=%b]", new Object[] { getClass().getName(), getMountPoint(), getParent(), Boolean.valueOf(isMounted()) });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */