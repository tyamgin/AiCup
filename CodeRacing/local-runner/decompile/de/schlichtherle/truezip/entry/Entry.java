package de.schlichtherle.truezip.entry;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract interface Entry
{
  public static final Set FILE_TYPE_SET = Collections.unmodifiableSet(EnumSet.of(Type.FILE));
  public static final Set DIRECTORY_TYPE_SET = Collections.unmodifiableSet(EnumSet.of(Type.DIRECTORY));
  public static final Set SYMLINK_TYPE_SET = Collections.unmodifiableSet(EnumSet.of(Type.SYMLINK));
  public static final Set SPECIAL_TYPE_SET = Collections.unmodifiableSet(EnumSet.of(Type.SPECIAL));
  public static final Set ALL_TYPE_SET = Collections.unmodifiableSet(EnumSet.allOf(Type.class));
  public static final Set ALL_SIZE_SET = Collections.unmodifiableSet(EnumSet.allOf(Size.class));
  public static final Set ALL_ACCESS_SET = Collections.unmodifiableSet(EnumSet.allOf(Access.class));
  
  public abstract long getSize(Size paramSize);
  
  public abstract long getTime(Access paramAccess);
  
  public static enum Access
  {
    WRITE,  READ,  CREATE;
  }
  
  public static enum Size
  {
    DATA,  STORAGE;
  }
  
  public static enum Type
  {
    FILE,  DIRECTORY,  SYMLINK,  SPECIAL;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\entry\Entry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */