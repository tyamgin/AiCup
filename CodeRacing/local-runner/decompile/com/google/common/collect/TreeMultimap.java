package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

@GwtCompatible(serializable=true, emulated=true)
public class TreeMultimap
  extends AbstractSortedKeySortedSetMultimap
{
  private transient Comparator keyComparator;
  private transient Comparator valueComparator;
  @GwtIncompatible("not needed in emulated source")
  private static final long serialVersionUID = 0L;
  
  public static TreeMultimap create()
  {
    return new TreeMultimap(Ordering.natural(), Ordering.natural());
  }
  
  public static TreeMultimap create(Comparator paramComparator1, Comparator paramComparator2)
  {
    return new TreeMultimap((Comparator)Preconditions.checkNotNull(paramComparator1), (Comparator)Preconditions.checkNotNull(paramComparator2));
  }
  
  public static TreeMultimap create(Multimap paramMultimap)
  {
    return new TreeMultimap(Ordering.natural(), Ordering.natural(), paramMultimap);
  }
  
  TreeMultimap(Comparator paramComparator1, Comparator paramComparator2)
  {
    super(new TreeMap(paramComparator1));
    this.keyComparator = paramComparator1;
    this.valueComparator = paramComparator2;
  }
  
  private TreeMultimap(Comparator paramComparator1, Comparator paramComparator2, Multimap paramMultimap)
  {
    this(paramComparator1, paramComparator2);
    putAll(paramMultimap);
  }
  
  SortedSet createCollection()
  {
    return new TreeSet(this.valueComparator);
  }
  
  Collection createCollection(Object paramObject)
  {
    if (paramObject == null) {
      keyComparator().compare(paramObject, paramObject);
    }
    return super.createCollection(paramObject);
  }
  
  public Comparator keyComparator()
  {
    return this.keyComparator;
  }
  
  public Comparator valueComparator()
  {
    return this.valueComparator;
  }
  
  @GwtIncompatible("NavigableMap")
  NavigableMap backingMap()
  {
    return (NavigableMap)super.backingMap();
  }
  
  @GwtIncompatible("NavigableSet")
  public NavigableSet get(Object paramObject)
  {
    return (NavigableSet)super.get(paramObject);
  }
  
  @GwtIncompatible("NavigableSet")
  Collection unmodifiableCollectionSubclass(Collection paramCollection)
  {
    return Sets.unmodifiableNavigableSet((NavigableSet)paramCollection);
  }
  
  @GwtIncompatible("NavigableSet")
  Collection wrapCollection(Object paramObject, Collection paramCollection)
  {
    return new AbstractMapBasedMultimap.WrappedNavigableSet(this, paramObject, (NavigableSet)paramCollection, null);
  }
  
  @GwtIncompatible("NavigableSet")
  public NavigableSet keySet()
  {
    return (NavigableSet)super.keySet();
  }
  
  @GwtIncompatible("NavigableSet")
  NavigableSet createKeySet()
  {
    return new AbstractMapBasedMultimap.NavigableKeySet(this, backingMap());
  }
  
  @GwtIncompatible("NavigableMap")
  public NavigableMap asMap()
  {
    return (NavigableMap)super.asMap();
  }
  
  @GwtIncompatible("NavigableMap")
  NavigableMap createAsMap()
  {
    return new AbstractMapBasedMultimap.NavigableAsMap(this, backingMap());
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(keyComparator());
    paramObjectOutputStream.writeObject(valueComparator());
    Serialization.writeMultimap(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.keyComparator = ((Comparator)Preconditions.checkNotNull((Comparator)paramObjectInputStream.readObject()));
    this.valueComparator = ((Comparator)Preconditions.checkNotNull((Comparator)paramObjectInputStream.readObject()));
    setMap(new TreeMap(this.keyComparator));
    Serialization.populateMultimap(this, paramObjectInputStream);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\TreeMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */