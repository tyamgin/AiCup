package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.BitField;

public final class FsSyncOptions
{
  public static final BitField NONE = BitField.noneOf(FsSyncOption.class);
  public static final BitField UMOUNT = BitField.of(FsSyncOption.FORCE_CLOSE_INPUT, new FsSyncOption[] { FsSyncOption.FORCE_CLOSE_OUTPUT, FsSyncOption.CLEAR_CACHE });
  public static final BitField SYNC = BitField.of(FsSyncOption.WAIT_CLOSE_INPUT, new FsSyncOption[] { FsSyncOption.WAIT_CLOSE_OUTPUT });
  public static final BitField RESET = BitField.of(FsSyncOption.ABORT_CHANGES);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsSyncOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */