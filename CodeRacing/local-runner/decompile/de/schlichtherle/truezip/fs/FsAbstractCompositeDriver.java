package de.schlichtherle.truezip.fs;

import java.util.Map;
import java.util.ServiceConfigurationError;

public abstract class FsAbstractCompositeDriver
  implements FsCompositeDriver, FsDriverProvider
{
  public final FsController newController(FsManager paramFsManager, FsModel paramFsModel, FsController paramFsController)
  {
    assert (null == paramFsModel.getParent() ? null == paramFsController : paramFsModel.getParent().equals(paramFsController.getModel()));
    FsScheme localFsScheme = paramFsModel.getMountPoint().getScheme();
    FsDriver localFsDriver = (FsDriver)get().get(localFsScheme);
    if (null == localFsDriver) {
      throw new ServiceConfigurationError(localFsScheme + " (Unknown file system scheme! May be the class path doesn't contain the respective driver module or it isn't set up correctly?)");
    }
    return localFsDriver.newController(paramFsManager, paramFsModel, paramFsController);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsAbstractCompositeDriver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */