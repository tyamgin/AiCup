package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.BitField;
import de.schlichtherle.truezip.util.Link;
import de.schlichtherle.truezip.util.Link.Type;
import de.schlichtherle.truezip.util.Links;
import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public final class FsDefaultManager
  extends FsManager
{
  private final Map controllers = new WeakHashMap();
  private final Link.Type optionalScheduleType;
  private final ReentrantReadWriteLock.ReadLock readLock;
  private final ReentrantReadWriteLock.WriteLock writeLock;
  
  public FsDefaultManager()
  {
    this(Link.Type.WEAK);
  }
  
  FsDefaultManager(Link.Type paramType)
  {
    assert (null != paramType);
    this.optionalScheduleType = paramType;
    ReentrantReadWriteLock localReentrantReadWriteLock = new ReentrantReadWriteLock();
    this.readLock = localReentrantReadWriteLock.readLock();
    this.writeLock = localReentrantReadWriteLock.writeLock();
  }
  
  /* Error */
  public FsController getController(FsMountPoint paramFsMountPoint, FsCompositeDriver paramFsCompositeDriver)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 30	de/schlichtherle/truezip/fs/FsDefaultManager:readLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   4: invokevirtual 53	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:lock	()V
    //   7: aload_0
    //   8: aload_1
    //   9: aload_2
    //   10: invokespecial 36	de/schlichtherle/truezip/fs/FsDefaultManager:getController0	(Lde/schlichtherle/truezip/fs/FsMountPoint;Lde/schlichtherle/truezip/fs/FsCompositeDriver;)Lde/schlichtherle/truezip/fs/FsController;
    //   13: astore_3
    //   14: aload_0
    //   15: getfield 30	de/schlichtherle/truezip/fs/FsDefaultManager:readLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   18: invokevirtual 54	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   21: aload_3
    //   22: areturn
    //   23: astore 4
    //   25: aload_0
    //   26: getfield 30	de/schlichtherle/truezip/fs/FsDefaultManager:readLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
    //   29: invokevirtual 54	java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock:unlock	()V
    //   32: aload 4
    //   34: athrow
    //   35: astore_3
    //   36: aload_0
    //   37: getfield 31	de/schlichtherle/truezip/fs/FsDefaultManager:writeLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
    //   40: invokevirtual 56	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:lock	()V
    //   43: aload_0
    //   44: aload_1
    //   45: aload_2
    //   46: invokespecial 36	de/schlichtherle/truezip/fs/FsDefaultManager:getController0	(Lde/schlichtherle/truezip/fs/FsMountPoint;Lde/schlichtherle/truezip/fs/FsCompositeDriver;)Lde/schlichtherle/truezip/fs/FsController;
    //   49: astore 4
    //   51: aload_0
    //   52: getfield 31	de/schlichtherle/truezip/fs/FsDefaultManager:writeLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
    //   55: invokevirtual 57	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
    //   58: aload 4
    //   60: areturn
    //   61: astore 5
    //   63: aload_0
    //   64: getfield 31	de/schlichtherle/truezip/fs/FsDefaultManager:writeLock	Ljava/util/concurrent/locks/ReentrantReadWriteLock$WriteLock;
    //   67: invokevirtual 57	java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock:unlock	()V
    //   70: aload 5
    //   72: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	FsDefaultManager
    //   0	73	1	paramFsMountPoint	FsMountPoint
    //   0	73	2	paramFsCompositeDriver	FsCompositeDriver
    //   13	9	3	localFsController1	FsController
    //   35	1	3	localFsNeedsWriteLockException	FsNeedsWriteLockException
    //   23	10	4	localObject1	Object
    //   49	10	4	localFsController2	FsController
    //   61	10	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	14	23	finally
    //   23	25	23	finally
    //   0	21	35	de/schlichtherle/truezip/fs/FsNeedsWriteLockException
    //   23	35	35	de/schlichtherle/truezip/fs/FsNeedsWriteLockException
    //   43	51	61	finally
    //   61	63	61	finally
  }
  
  private FsController getController0(FsMountPoint paramFsMountPoint, FsCompositeDriver paramFsCompositeDriver)
  {
    FsController localFsController1 = (FsController)Links.getTarget((Link)this.controllers.get(paramFsMountPoint));
    if (null != localFsController1) {
      return localFsController1;
    }
    if (!this.writeLock.isHeldByCurrentThread()) {
      throw FsNeedsWriteLockException.get();
    }
    FsMountPoint localFsMountPoint = paramFsMountPoint.getParent();
    FsController localFsController2 = null == localFsMountPoint ? null : getController0(localFsMountPoint, paramFsCompositeDriver);
    ManagedModel localManagedModel = new ManagedModel(paramFsMountPoint, null == localFsController2 ? null : localFsController2.getModel());
    localFsController1 = paramFsCompositeDriver.newController(this, localManagedModel, localFsController2);
    localManagedModel.init(localFsController1);
    return localFsController1;
  }
  
  public int getSize()
  {
    this.readLock.lock();
    try
    {
      int i = this.controllers.size();
      return i;
    }
    finally
    {
      this.readLock.unlock();
    }
  }
  
  public Iterator iterator()
  {
    return sortedControllers().iterator();
  }
  
  private Set sortedControllers()
  {
    this.readLock.lock();
    try
    {
      TreeSet localTreeSet = new TreeSet(ReverseControllerComparator.INSTANCE);
      Object localObject1 = this.controllers.values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Link localLink = (Link)((Iterator)localObject1).next();
        FsController localFsController = (FsController)Links.getTarget(localLink);
        if (null != localFsController) {
          localTreeSet.add(localFsController);
        }
      }
      localObject1 = localTreeSet;
      return (Set)localObject1;
    }
    finally
    {
      this.readLock.unlock();
    }
  }
  
  public void sync(BitField paramBitField)
    throws FsSyncException
  {
    FsSyncShutdownHook.cancel();
    super.sync(paramBitField);
  }
  
  private static final class ReverseControllerComparator
    implements Comparator
  {
    static final ReverseControllerComparator INSTANCE = new ReverseControllerComparator();
    
    public int compare(FsController paramFsController1, FsController paramFsController2)
    {
      return paramFsController2.getModel().getMountPoint().toHierarchicalUri().compareTo(paramFsController1.getModel().getMountPoint().toHierarchicalUri());
    }
  }
  
  private final class ManagedModel
    extends FsModel
  {
    FsController controller;
    volatile boolean mounted;
    
    ManagedModel(FsMountPoint paramFsMountPoint, FsModel paramFsModel)
    {
      super(paramFsModel);
    }
    
    void init(FsController paramFsController)
    {
      assert (null != paramFsController);
      assert (!this.mounted);
      this.controller = paramFsController;
      schedule(false);
    }
    
    public boolean isMounted()
    {
      return this.mounted;
    }
    
    void schedule(boolean paramBoolean)
    {
      assert (FsDefaultManager.this.writeLock.isHeldByCurrentThread());
      Link.Type localType = paramBoolean ? Link.Type.STRONG : FsDefaultManager.this.optionalScheduleType;
      FsDefaultManager.this.controllers.put(getMountPoint(), localType.newLink(this.controller));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsDefaultManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */