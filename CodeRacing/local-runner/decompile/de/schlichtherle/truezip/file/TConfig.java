package de.schlichtherle.truezip.file;

import de.schlichtherle.truezip.fs.FsInputOptions;
import de.schlichtherle.truezip.fs.FsManager;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.FsOutputOptions;
import de.schlichtherle.truezip.fs.sl.FsManagerLocator;
import de.schlichtherle.truezip.util.BitField;
import de.schlichtherle.truezip.util.InheritableThreadLocalStack;
import de.schlichtherle.truezip.util.Resource;
import java.io.Closeable;

public final class TConfig
  extends Resource
  implements Closeable
{
  public static final BitField DEFAULT_INPUT_PREFERENCES = FsInputOptions.NONE;
  private static final BitField INPUT_PREFERENCES_COMPLEMENT_MASK = FsInputOptions.INPUT_PREFERENCES_MASK.not();
  public static final BitField DEFAULT_OUTPUT_PREFERENCES = BitField.of(FsOutputOption.CREATE_PARENTS);
  private static final BitField OUTPUT_PREFERENCES_COMPLEMENT_MASK = FsOutputOptions.OUTPUT_PREFERENCES_MASK.not();
  private static final InheritableThreadLocalStack configs = new InheritableThreadLocalStack();
  private static final TConfig GLOBAL = new TConfig();
  private FsManager manager = FsManagerLocator.SINGLETON.get();
  private TArchiveDetector detector = TArchiveDetector.ALL;
  private BitField inputPreferences = DEFAULT_INPUT_PREFERENCES;
  private BitField outputPreferences = DEFAULT_OUTPUT_PREFERENCES;
  
  public static TConfig get()
  {
    return (TConfig)configs.peekOrElse(GLOBAL);
  }
  
  @Deprecated
  public FsManager getFsManager()
  {
    return this.manager;
  }
  
  public boolean isLenient()
  {
    return this.outputPreferences.get(FsOutputOption.CREATE_PARENTS);
  }
  
  public TArchiveDetector getArchiveDetector()
  {
    return this.detector;
  }
  
  public BitField getInputPreferences()
  {
    return this.inputPreferences;
  }
  
  public BitField getOutputPreferences()
  {
    return this.outputPreferences;
  }
  
  protected void onClose()
  {
    configs.popIf(this);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\file\TConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */