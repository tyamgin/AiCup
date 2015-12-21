package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.entry.Entry;
import de.schlichtherle.truezip.entry.Entry.Access;
import de.schlichtherle.truezip.entry.Entry.Size;
import de.schlichtherle.truezip.entry.Entry.Type;
import de.schlichtherle.truezip.util.BitField;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Set;

public abstract class FsEntry
  implements Entry
{
  public abstract String getName();
  
  public abstract Set getTypes();
  
  public boolean isType(Entry.Type paramType)
  {
    return getTypes().contains(paramType);
  }
  
  public abstract Set getMembers();
  
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
    Set localSet = getTypes();
    BitField localBitField = localSet.isEmpty() ? BitField.noneOf(Entry.Type.class) : BitField.copyOf(localSet);
    StringBuilder localStringBuilder = new StringBuilder(256);
    Formatter localFormatter = new Formatter(localStringBuilder).format("%s[name=%s, types=%s", new Object[] { getClass().getName(), getName(), localBitField });
    Iterator localIterator = ALL_SIZE_SET.iterator();
    Object localObject;
    long l;
    while (localIterator.hasNext())
    {
      localObject = (Entry.Size)localIterator.next();
      l = getSize((Entry.Size)localObject);
      if (-1L != l) {
        localFormatter.format(", size(%s)=%d", new Object[] { localObject, Long.valueOf(l) });
      }
    }
    localIterator = ALL_ACCESS_SET.iterator();
    while (localIterator.hasNext())
    {
      localObject = (Entry.Access)localIterator.next();
      l = getTime((Entry.Access)localObject);
      if (-1L != l) {
        localFormatter.format(", time(%s)=%tc", new Object[] { localObject, Long.valueOf(l) });
      }
    }
    return localFormatter.format(", members=%s]", new Object[] { getMembers() }).toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */