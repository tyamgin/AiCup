package com.google.inject.internal;

import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;

final class ModuleAnnotatedMethodScannerProcessor
  extends AbstractProcessor
{
  ModuleAnnotatedMethodScannerProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  public Boolean visit(ModuleAnnotatedMethodScannerBinding paramModuleAnnotatedMethodScannerBinding)
  {
    this.injector.state.addScanner(paramModuleAnnotatedMethodScannerBinding);
    return Boolean.valueOf(true);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ModuleAnnotatedMethodScannerProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */