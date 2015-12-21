package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..ClassWriter;

public class $DefaultGeneratorStrategy
  implements .GeneratorStrategy
{
  public static final DefaultGeneratorStrategy INSTANCE = new DefaultGeneratorStrategy();
  
  public byte[] generate(.ClassGenerator paramClassGenerator)
    throws Exception
  {
    .DebuggingClassWriter localDebuggingClassWriter = getClassVisitor();
    transform(paramClassGenerator).generateClass(localDebuggingClassWriter);
    return transform(localDebuggingClassWriter.toByteArray());
  }
  
  protected .DebuggingClassWriter getClassVisitor()
    throws Exception
  {
    return new .DebuggingClassWriter(1);
  }
  
  protected final .ClassWriter getClassWriter()
  {
    throw new UnsupportedOperationException("You are calling getClassWriter, which no longer exists in this cglib version.");
  }
  
  protected byte[] transform(byte[] paramArrayOfByte)
    throws Exception
  {
    return paramArrayOfByte;
  }
  
  protected .ClassGenerator transform(.ClassGenerator paramClassGenerator)
    throws Exception
  {
    return paramClassGenerator;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$DefaultGeneratorStrategy.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */