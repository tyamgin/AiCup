package de.schlichtherle.truezip.fs.sl;

import de.schlichtherle.truezip.fs.FsDefaultManager;
import de.schlichtherle.truezip.fs.FsManager;
import de.schlichtherle.truezip.fs.FsManagerProvider;
import de.schlichtherle.truezip.fs.spi.FsManagerDecorator;
import de.schlichtherle.truezip.fs.spi.FsManagerService;
import de.schlichtherle.truezip.util.ServiceLocator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FsManagerLocator
  implements FsManagerProvider
{
  public static final FsManagerLocator SINGLETON = new FsManagerLocator();
  
  public FsManager get()
  {
    return Boot.manager;
  }
  
  private static final class DefaultManagerService
    extends FsManagerService
  {
    public FsManager get()
    {
      return new FsDefaultManager();
    }
  }
  
  private static final class Boot
  {
    static final FsManager manager;
    
    private static FsManager create(ServiceLocator paramServiceLocator, Logger paramLogger)
    {
      Object localObject1 = (FsManagerService)paramServiceLocator.getService(FsManagerService.class, null);
      if (null == localObject1)
      {
        localObject2 = paramServiceLocator.getServices(FsManagerService.class);
        while (((Iterator)localObject2).hasNext())
        {
          FsManagerService localFsManagerService = (FsManagerService)((Iterator)localObject2).next();
          paramLogger.log(Level.CONFIG, "located", localFsManagerService);
          if (null == localObject1)
          {
            localObject1 = localFsManagerService;
          }
          else
          {
            int i = ((FsManagerService)localObject1).getPriority();
            int j = localFsManagerService.getPriority();
            if (i < j) {
              localObject1 = localFsManagerService;
            } else if (i == j) {
              paramLogger.log(Level.WARNING, "collision", new Object[] { Integer.valueOf(i), localObject1, localFsManagerService });
            }
          }
        }
      }
      if (null == localObject1) {
        localObject1 = new FsManagerLocator.DefaultManagerService(null);
      }
      paramLogger.log(Level.CONFIG, "using", localObject1);
      Object localObject2 = ((FsManagerService)localObject1).get();
      paramLogger.log(Level.CONFIG, "result", localObject2);
      return (FsManager)localObject2;
    }
    
    private static FsManager decorate(FsManager paramFsManager, ServiceLocator paramServiceLocator, Logger paramLogger)
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject1 = paramServiceLocator.getServices(FsManagerDecorator.class);
      while (((Iterator)localObject1).hasNext()) {
        localArrayList.add(((Iterator)localObject1).next());
      }
      localObject1 = (FsManagerDecorator[])localArrayList.toArray(new FsManagerDecorator[localArrayList.size()]);
      Arrays.sort((Object[])localObject1, new Comparator()
      {
        public int compare(FsManagerDecorator paramAnonymousFsManagerDecorator1, FsManagerDecorator paramAnonymousFsManagerDecorator2)
        {
          return paramAnonymousFsManagerDecorator1.getPriority() - paramAnonymousFsManagerDecorator2.getPriority();
        }
      });
      for (Object localObject3 : localObject1)
      {
        paramLogger.log(Level.CONFIG, "decorating", localObject3);
        paramFsManager = ((FsManagerDecorator)localObject3).decorate(paramFsManager);
        paramLogger.log(Level.CONFIG, "result", paramFsManager);
      }
      return paramFsManager;
    }
    
    static
    {
      Class localClass = FsManagerLocator.class;
      Logger localLogger = Logger.getLogger(localClass.getName(), localClass.getName());
      ServiceLocator localServiceLocator = new ServiceLocator(localClass.getClassLoader());
      manager = decorate(create(localServiceLocator, localLogger), localServiceLocator, localLogger);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\sl\FsManagerLocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */