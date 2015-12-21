package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public final class LinkedHashMultimap
  extends AbstractSetMultimap
{
  private static final int DEFAULT_KEY_CAPACITY = 16;
  private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
  @VisibleForTesting
  static final double VALUE_SET_LOAD_FACTOR = 1.0D;
  @VisibleForTesting
  transient int valueSetCapacity = 2;
  private transient ValueEntry multimapHeaderEntry;
  @GwtIncompatible("java serialization not supported")
  private static final long serialVersionUID = 1L;
  
  public static LinkedHashMultimap create()
  {
    return new LinkedHashMultimap(16, 2);
  }
  
  public static LinkedHashMultimap create(int paramInt1, int paramInt2)
  {
    return new LinkedHashMultimap(Maps.capacity(paramInt1), Maps.capacity(paramInt2));
  }
  
  public static LinkedHashMultimap create(Multimap paramMultimap)
  {
    LinkedHashMultimap localLinkedHashMultimap = create(paramMultimap.keySet().size(), 2);
    localLinkedHashMultimap.putAll(paramMultimap);
    return localLinkedHashMultimap;
  }
  
  private static void succeedsInValueSet(ValueSetLink paramValueSetLink1, ValueSetLink paramValueSetLink2)
  {
    paramValueSetLink1.setSuccessorInValueSet(paramValueSetLink2);
    paramValueSetLink2.setPredecessorInValueSet(paramValueSetLink1);
  }
  
  private static void succeedsInMultimap(ValueEntry paramValueEntry1, ValueEntry paramValueEntry2)
  {
    paramValueEntry1.setSuccessorInMultimap(paramValueEntry2);
    paramValueEntry2.setPredecessorInMultimap(paramValueEntry1);
  }
  
  private static void deleteFromValueSet(ValueSetLink paramValueSetLink)
  {
    succeedsInValueSet(paramValueSetLink.getPredecessorInValueSet(), paramValueSetLink.getSuccessorInValueSet());
  }
  
  private static void deleteFromMultimap(ValueEntry paramValueEntry)
  {
    succeedsInMultimap(paramValueEntry.getPredecessorInMultimap(), paramValueEntry.getSuccessorInMultimap());
  }
  
  private LinkedHashMultimap(int paramInt1, int paramInt2)
  {
    super(new LinkedHashMap(paramInt1));
    Preconditions.checkArgument(paramInt2 >= 0, "expectedValuesPerKey must be >= 0 but was %s", new Object[] { Integer.valueOf(paramInt2) });
    this.valueSetCapacity = paramInt2;
    this.multimapHeaderEntry = new ValueEntry(null, null, 0, null);
    succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
  }
  
  Set createCollection()
  {
    return new LinkedHashSet(this.valueSetCapacity);
  }
  
  Collection createCollection(Object paramObject)
  {
    return new ValueSet(paramObject, this.valueSetCapacity);
  }
  
  public Set replaceValues(Object paramObject, Iterable paramIterable)
  {
    return super.replaceValues(paramObject, paramIterable);
  }
  
  public Set entries()
  {
    return super.entries();
  }
  
  public Collection values()
  {
    return super.values();
  }
  
  Iterator entryIterator()
  {
    new Iterator()
    {
      LinkedHashMultimap.ValueEntry nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.successorInMultimap;
      LinkedHashMultimap.ValueEntry toRemove;
      
      public boolean hasNext()
      {
        return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
      }
      
      public Map.Entry next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        LinkedHashMultimap.ValueEntry localValueEntry = this.nextEntry;
        this.toRemove = localValueEntry;
        this.nextEntry = this.nextEntry.successorInMultimap;
        return localValueEntry;
      }
      
      public void remove()
      {
        Iterators.checkRemove(this.toRemove != null);
        LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
        this.toRemove = null;
      }
    };
  }
  
  public void clear()
  {
    super.clear();
    succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.valueSetCapacity);
    paramObjectOutputStream.writeInt(keySet().size());
    Iterator localIterator = keySet().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = localIterator.next();
      paramObjectOutputStream.writeObject(localObject);
    }
    paramObjectOutputStream.writeInt(size());
    localIterator = entries().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      paramObjectOutputStream.writeObject(((Map.Entry)localObject).getKey());
      paramObjectOutputStream.writeObject(((Map.Entry)localObject).getValue());
    }
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.multimapHeaderEntry = new ValueEntry(null, null, 0, null);
    succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
    this.valueSetCapacity = paramObjectInputStream.readInt();
    int i = paramObjectInputStream.readInt();
    LinkedHashMap localLinkedHashMap = new LinkedHashMap(Maps.capacity(i));
    for (int j = 0; j < i; j++)
    {
      Object localObject1 = paramObjectInputStream.readObject();
      localLinkedHashMap.put(localObject1, createCollection(localObject1));
    }
    j = paramObjectInputStream.readInt();
    for (int k = 0; k < j; k++)
    {
      Object localObject2 = paramObjectInputStream.readObject();
      Object localObject3 = paramObjectInputStream.readObject();
      ((Collection)localLinkedHashMap.get(localObject2)).add(localObject3);
    }
    setMap(localLinkedHashMap);
  }
  
  @VisibleForTesting
  final class ValueSet
    extends Sets.ImprovedAbstractSet
    implements LinkedHashMultimap.ValueSetLink
  {
    private final Object key;
    @VisibleForTesting
    LinkedHashMultimap.ValueEntry[] hashTable;
    private int size = 0;
    private int modCount = 0;
    private LinkedHashMultimap.ValueSetLink firstEntry;
    private LinkedHashMultimap.ValueSetLink lastEntry;
    
    ValueSet(Object paramObject, int paramInt)
    {
      this.key = paramObject;
      this.firstEntry = this;
      this.lastEntry = this;
      int i = Hashing.closedTableSize(paramInt, 1.0D);
      LinkedHashMultimap.ValueEntry[] arrayOfValueEntry = new LinkedHashMultimap.ValueEntry[i];
      this.hashTable = arrayOfValueEntry;
    }
    
    public LinkedHashMultimap.ValueSetLink getPredecessorInValueSet()
    {
      return this.lastEntry;
    }
    
    public LinkedHashMultimap.ValueSetLink getSuccessorInValueSet()
    {
      return this.firstEntry;
    }
    
    public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink paramValueSetLink)
    {
      this.lastEntry = paramValueSetLink;
    }
    
    public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink paramValueSetLink)
    {
      this.firstEntry = paramValueSetLink;
    }
    
    public Iterator iterator()
    {
      new Iterator()
      {
        LinkedHashMultimap.ValueSetLink nextEntry = LinkedHashMultimap.ValueSet.this.firstEntry;
        LinkedHashMultimap.ValueEntry toRemove;
        int expectedModCount = LinkedHashMultimap.ValueSet.this.modCount;
        
        private void checkForComodification()
        {
          if (LinkedHashMultimap.ValueSet.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
          }
        }
        
        public boolean hasNext()
        {
          checkForComodification();
          return this.nextEntry != LinkedHashMultimap.ValueSet.this;
        }
        
        public Object next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          LinkedHashMultimap.ValueEntry localValueEntry = (LinkedHashMultimap.ValueEntry)this.nextEntry;
          Object localObject = localValueEntry.getValue();
          this.toRemove = localValueEntry;
          this.nextEntry = localValueEntry.getSuccessorInValueSet();
          return localObject;
        }
        
        public void remove()
        {
          checkForComodification();
          Iterators.checkRemove(this.toRemove != null);
          Object localObject1 = this.toRemove.getValue();
          int i = localObject1 == null ? 0 : localObject1.hashCode();
          int j = Hashing.smear(i) & LinkedHashMultimap.ValueSet.this.hashTable.length - 1;
          Object localObject2 = null;
          for (LinkedHashMultimap.ValueEntry localValueEntry = LinkedHashMultimap.ValueSet.this.hashTable[j]; localValueEntry != null; localValueEntry = localValueEntry.nextInValueSetHashRow)
          {
            if (localValueEntry == this.toRemove)
            {
              if (localObject2 == null) {
                LinkedHashMultimap.ValueSet.this.hashTable[j] = localValueEntry.nextInValueSetHashRow;
              } else {
                ((LinkedHashMultimap.ValueEntry)localObject2).nextInValueSetHashRow = localValueEntry.nextInValueSetHashRow;
              }
              LinkedHashMultimap.deleteFromValueSet(this.toRemove);
              LinkedHashMultimap.deleteFromMultimap(this.toRemove);
              LinkedHashMultimap.ValueSet.access$410(LinkedHashMultimap.ValueSet.this);
              this.expectedModCount = LinkedHashMultimap.ValueSet.access$104(LinkedHashMultimap.ValueSet.this);
              break;
            }
            localObject2 = localValueEntry;
          }
          this.toRemove = null;
        }
      };
    }
    
    public int size()
    {
      return this.size;
    }
    
    public boolean contains(Object paramObject)
    {
      int i = paramObject == null ? 0 : paramObject.hashCode();
      int j = Hashing.smear(i) & this.hashTable.length - 1;
      for (LinkedHashMultimap.ValueEntry localValueEntry = this.hashTable[j]; localValueEntry != null; localValueEntry = localValueEntry.nextInValueSetHashRow) {
        if ((i == localValueEntry.valueHash) && (Objects.equal(paramObject, localValueEntry.getValue()))) {
          return true;
        }
      }
      return false;
    }
    
    public boolean add(Object paramObject)
    {
      int i = paramObject == null ? 0 : paramObject.hashCode();
      int j = Hashing.smear(i) & this.hashTable.length - 1;
      LinkedHashMultimap.ValueEntry localValueEntry1 = this.hashTable[j];
      for (LinkedHashMultimap.ValueEntry localValueEntry2 = localValueEntry1; localValueEntry2 != null; localValueEntry2 = localValueEntry2.nextInValueSetHashRow) {
        if ((i == localValueEntry2.valueHash) && (Objects.equal(paramObject, localValueEntry2.getValue()))) {
          return false;
        }
      }
      localValueEntry2 = new LinkedHashMultimap.ValueEntry(this.key, paramObject, i, localValueEntry1);
      LinkedHashMultimap.succeedsInValueSet(this.lastEntry, localValueEntry2);
      LinkedHashMultimap.succeedsInValueSet(localValueEntry2, this);
      LinkedHashMultimap.succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), localValueEntry2);
      LinkedHashMultimap.succeedsInMultimap(localValueEntry2, LinkedHashMultimap.this.multimapHeaderEntry);
      this.hashTable[j] = localValueEntry2;
      this.size += 1;
      this.modCount += 1;
      rehashIfNecessary();
      return true;
    }
    
    private void rehashIfNecessary()
    {
      if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0D))
      {
        LinkedHashMultimap.ValueEntry[] arrayOfValueEntry = new LinkedHashMultimap.ValueEntry[this.hashTable.length * 2];
        this.hashTable = arrayOfValueEntry;
        int i = arrayOfValueEntry.length - 1;
        for (LinkedHashMultimap.ValueSetLink localValueSetLink = this.firstEntry; localValueSetLink != this; localValueSetLink = localValueSetLink.getSuccessorInValueSet())
        {
          LinkedHashMultimap.ValueEntry localValueEntry = (LinkedHashMultimap.ValueEntry)localValueSetLink;
          int j = Hashing.smear(localValueEntry.valueHash) & i;
          localValueEntry.nextInValueSetHashRow = arrayOfValueEntry[j];
          arrayOfValueEntry[j] = localValueEntry;
        }
      }
    }
    
    public boolean remove(Object paramObject)
    {
      int i = paramObject == null ? 0 : paramObject.hashCode();
      int j = Hashing.smear(i) & this.hashTable.length - 1;
      Object localObject = null;
      for (LinkedHashMultimap.ValueEntry localValueEntry = this.hashTable[j]; localValueEntry != null; localValueEntry = localValueEntry.nextInValueSetHashRow)
      {
        if ((i == localValueEntry.valueHash) && (Objects.equal(paramObject, localValueEntry.getValue())))
        {
          if (localObject == null) {
            this.hashTable[j] = localValueEntry.nextInValueSetHashRow;
          } else {
            ((LinkedHashMultimap.ValueEntry)localObject).nextInValueSetHashRow = localValueEntry.nextInValueSetHashRow;
          }
          LinkedHashMultimap.deleteFromValueSet(localValueEntry);
          LinkedHashMultimap.deleteFromMultimap(localValueEntry);
          this.size -= 1;
          this.modCount += 1;
          return true;
        }
        localObject = localValueEntry;
      }
      return false;
    }
    
    public void clear()
    {
      Arrays.fill(this.hashTable, null);
      this.size = 0;
      for (LinkedHashMultimap.ValueSetLink localValueSetLink = this.firstEntry; localValueSetLink != this; localValueSetLink = localValueSetLink.getSuccessorInValueSet())
      {
        LinkedHashMultimap.ValueEntry localValueEntry = (LinkedHashMultimap.ValueEntry)localValueSetLink;
        LinkedHashMultimap.deleteFromMultimap(localValueEntry);
      }
      LinkedHashMultimap.succeedsInValueSet(this, this);
      this.modCount += 1;
    }
  }
  
  @VisibleForTesting
  static final class ValueEntry
    extends AbstractMapEntry
    implements LinkedHashMultimap.ValueSetLink
  {
    final Object key;
    final Object value;
    final int valueHash;
    ValueEntry nextInValueSetHashRow;
    LinkedHashMultimap.ValueSetLink predecessorInValueSet;
    LinkedHashMultimap.ValueSetLink successorInValueSet;
    ValueEntry predecessorInMultimap;
    ValueEntry successorInMultimap;
    
    ValueEntry(Object paramObject1, Object paramObject2, int paramInt, ValueEntry paramValueEntry)
    {
      this.key = paramObject1;
      this.value = paramObject2;
      this.valueHash = paramInt;
      this.nextInValueSetHashRow = paramValueEntry;
    }
    
    public Object getKey()
    {
      return this.key;
    }
    
    public Object getValue()
    {
      return this.value;
    }
    
    public LinkedHashMultimap.ValueSetLink getPredecessorInValueSet()
    {
      return this.predecessorInValueSet;
    }
    
    public LinkedHashMultimap.ValueSetLink getSuccessorInValueSet()
    {
      return this.successorInValueSet;
    }
    
    public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink paramValueSetLink)
    {
      this.predecessorInValueSet = paramValueSetLink;
    }
    
    public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink paramValueSetLink)
    {
      this.successorInValueSet = paramValueSetLink;
    }
    
    public ValueEntry getPredecessorInMultimap()
    {
      return this.predecessorInMultimap;
    }
    
    public ValueEntry getSuccessorInMultimap()
    {
      return this.successorInMultimap;
    }
    
    public void setSuccessorInMultimap(ValueEntry paramValueEntry)
    {
      this.successorInMultimap = paramValueEntry;
    }
    
    public void setPredecessorInMultimap(ValueEntry paramValueEntry)
    {
      this.predecessorInMultimap = paramValueEntry;
    }
  }
  
  private static abstract interface ValueSetLink
  {
    public abstract ValueSetLink getPredecessorInValueSet();
    
    public abstract ValueSetLink getSuccessorInValueSet();
    
    public abstract void setPredecessorInValueSet(ValueSetLink paramValueSetLink);
    
    public abstract void setSuccessorInValueSet(ValueSetLink paramValueSetLink);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\LinkedHashMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */