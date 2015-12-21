package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.BitField;

public final class FsOutputOptions
{
  public static final BitField NONE = BitField.noneOf(FsOutputOption.class);
  @Deprecated
  public static final BitField NO_OUTPUT_OPTIONS = NONE;
  @Deprecated
  public static final BitField NO_OUTPUT_OPTION = NONE;
  public static final BitField OUTPUT_PREFERENCES_MASK = BitField.of(FsOutputOption.CACHE, new FsOutputOption[] { FsOutputOption.CREATE_PARENTS, FsOutputOption.STORE, FsOutputOption.COMPRESS, FsOutputOption.GROW, FsOutputOption.ENCRYPT });
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsOutputOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */