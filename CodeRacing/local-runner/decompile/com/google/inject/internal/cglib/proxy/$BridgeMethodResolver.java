package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..ClassReader;
import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..MethodVisitor;
import com.google.inject.internal.cglib.core..Signature;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class $BridgeMethodResolver
{
  private final Map declToBridge;
  
  public $BridgeMethodResolver(Map paramMap)
  {
    this.declToBridge = paramMap;
  }
  
  public Map resolveAll()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.declToBridge.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Class localClass = (Class)localEntry.getKey();
      Set localSet = (Set)localEntry.getValue();
      try
      {
        new .ClassReader(localClass.getName()).accept(new BridgedFinder(localSet, localHashMap), 6);
      }
      catch (IOException localIOException) {}
    }
    return localHashMap;
  }
  
  private static class BridgedFinder
    extends .ClassVisitor
  {
    private Map resolved;
    private Set eligableMethods;
    private .Signature currentMethod = null;
    
    BridgedFinder(Set paramSet, Map paramMap)
    {
      super();
      this.resolved = paramMap;
      this.eligableMethods = paramSet;
    }
    
    public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {}
    
    public .MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
    {
      .Signature localSignature = new .Signature(paramString1, paramString2);
      if (this.eligableMethods.remove(localSignature))
      {
        this.currentMethod = localSignature;
        new .MethodVisitor(262144)
        {
          public void visitMethodInsn(int paramAnonymousInt, String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
          {
            if ((paramAnonymousInt == 183) && (.BridgeMethodResolver.BridgedFinder.this.currentMethod != null))
            {
              .Signature localSignature = new .Signature(paramAnonymousString2, paramAnonymousString3);
              if (!localSignature.equals(.BridgeMethodResolver.BridgedFinder.this.currentMethod)) {
                .BridgeMethodResolver.BridgedFinder.this.resolved.put(.BridgeMethodResolver.BridgedFinder.this.currentMethod, localSignature);
              }
              .BridgeMethodResolver.BridgedFinder.this.currentMethod = null;
            }
          }
        };
      }
      return null;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$BridgeMethodResolver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */