package org.apache.commons.math3.exception.util;

import java.io.Serializable;
import java.util.Locale;

public abstract interface Localizable
  extends Serializable
{
  public abstract String getLocalizedString(Locale paramLocale);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\exception\util\Localizable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */