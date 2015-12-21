package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.BitField;

public final class FsInputOptions
{
  public static final BitField NONE = BitField.noneOf(FsInputOption.class);
  @Deprecated
  public static final BitField NO_INPUT_OPTIONS = NONE;
  @Deprecated
  public static final BitField NO_INPUT_OPTION = NONE;
  public static final BitField INPUT_PREFERENCES_MASK = BitField.of(FsInputOption.CACHE);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsInputOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */