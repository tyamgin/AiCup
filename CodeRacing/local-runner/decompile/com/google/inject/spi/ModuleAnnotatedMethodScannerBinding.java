package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.internal.Errors;

public final class ModuleAnnotatedMethodScannerBinding
  implements Element
{
  private final Object source;
  private final ModuleAnnotatedMethodScanner scanner;
  
  public ModuleAnnotatedMethodScannerBinding(Object paramObject, ModuleAnnotatedMethodScanner paramModuleAnnotatedMethodScanner)
  {
    this.source = Preconditions.checkNotNull(paramObject, "source");
    this.scanner = ((ModuleAnnotatedMethodScanner)Preconditions.checkNotNull(paramModuleAnnotatedMethodScanner, "scanner"));
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public ModuleAnnotatedMethodScanner getScanner()
  {
    return this.scanner;
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).scanModulesForAnnotatedMethods(this.scanner);
  }
  
  public String toString()
  {
    String str1 = String.valueOf(String.valueOf(this.scanner));
    String str2 = String.valueOf(String.valueOf(this.scanner.annotationClasses()));
    String str3 = String.valueOf(String.valueOf(Errors.convert(this.source)));
    return 29 + str1.length() + str2.length() + str3.length() + str1 + " which scans for " + str2 + " (bound at " + str3 + ")";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ModuleAnnotatedMethodScannerBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */