package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

@GwtCompatible
abstract class FilteredMultimap
  extends AbstractMultimap
{
  final Multimap unfiltered;
  
  FilteredMultimap(Multimap paramMultimap)
  {
    this.unfiltered = ((Multimap)Preconditions.checkNotNull(paramMultimap));
  }
  
  abstract Predicate entryPredicate();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\FilteredMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */