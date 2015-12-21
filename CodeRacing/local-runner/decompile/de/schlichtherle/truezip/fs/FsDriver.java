package de.schlichtherle.truezip.fs;

public abstract class FsDriver
{
  public boolean isFederated()
  {
    return false;
  }
  
  public int getPriority()
  {
    return 0;
  }
  
  public FsController newController(FsManager paramFsManager, FsModel paramFsModel, FsController paramFsController)
  {
    return newController(paramFsModel, paramFsController);
  }
  
  public abstract FsController newController(FsModel paramFsModel, FsController paramFsController);
  
  public String toString()
  {
    return String.format("%s[federated=%b, priority=%d]", new Object[] { getClass().getName(), Boolean.valueOf(isFederated()), Integer.valueOf(getPriority()) });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsDriver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */