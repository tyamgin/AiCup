package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..ClassReader;
import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..ClassWriter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class $DebuggingClassWriter
  extends .ClassVisitor
{
  public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
  private static String debugLocation = System.getProperty("cglib.debugLocation");
  private static Constructor traceCtor;
  private String className;
  private String superName;
  
  public $DebuggingClassWriter(int paramInt)
  {
    super(262144, new .ClassWriter(paramInt));
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this.className = paramString1.replace('/', '.');
    this.superName = paramString3.replace('/', '.');
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public String getClassName()
  {
    return this.className;
  }
  
  public String getSuperName()
  {
    return this.superName;
  }
  
  public byte[] toByteArray()
  {
    (byte[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        byte[] arrayOfByte = ((.ClassWriter).DebuggingClassWriter.access$001(.DebuggingClassWriter.this)).toByteArray();
        if (.DebuggingClassWriter.debugLocation != null)
        {
          String str = .DebuggingClassWriter.this.className.replace('.', File.separatorChar);
          try
          {
            new File(.DebuggingClassWriter.debugLocation + File.separatorChar + str).getParentFile().mkdirs();
            File localFile = new File(new File(.DebuggingClassWriter.debugLocation), str + ".class");
            BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
            try
            {
              localBufferedOutputStream.write(arrayOfByte);
            }
            finally
            {
              localBufferedOutputStream.close();
            }
            if (.DebuggingClassWriter.traceCtor != null)
            {
              localFile = new File(new File(.DebuggingClassWriter.debugLocation), str + ".asm");
              localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
              try
              {
                .ClassReader localClassReader = new .ClassReader(arrayOfByte);
                PrintWriter localPrintWriter = new PrintWriter(new OutputStreamWriter(localBufferedOutputStream));
                .ClassVisitor localClassVisitor = (.ClassVisitor).DebuggingClassWriter.traceCtor.newInstance(new Object[] { null, localPrintWriter });
                localClassReader.accept(localClassVisitor, 0);
                localPrintWriter.flush();
              }
              finally
              {
                localBufferedOutputStream.close();
              }
            }
          }
          catch (Exception localException)
          {
            throw new .CodeGenerationException(localException);
          }
        }
        return arrayOfByte;
      }
    });
  }
  
  static
  {
    if (debugLocation != null)
    {
      System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
      try
      {
        Class localClass = Class.forName("com.google.inject.internal.asm.util.$TraceClassVisitor");
        traceCtor = localClass.getConstructor(new Class[] { .ClassVisitor.class, PrintWriter.class });
      }
      catch (Throwable localThrowable) {}
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$DebuggingClassWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */