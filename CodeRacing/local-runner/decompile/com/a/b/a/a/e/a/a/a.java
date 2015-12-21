package com.a.b.a.a.e.a.a;

import com.codeforces.commons.compress.ZipUtil;
import com.codeforces.commons.io.FileUtil;
import com.codeforces.commons.io.IoUtil;
import com.codeforces.commons.resource.ResourceUtil;
import com.codeforces.commons.text.StringUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class a
{
  private static final Logger a = LoggerFactory.getLogger(a.class);
  private final String b;
  private final Process c;
  private final File d;
  private final AtomicBoolean e = new AtomicBoolean();
  
  private a(String paramString, Process paramProcess, File paramFile)
  {
    this.b = paramString;
    this.c = paramProcess;
    this.d = paramFile;
  }
  
  public File a()
  {
    return this.d;
  }
  
  public void a(long paramLong)
  {
    Thread localThread = new Thread(new b(this));
    localThread.start();
    try
    {
      localThread.join(paramLong);
    }
    catch (InterruptedException localInterruptedException)
    {
      localThread.interrupt();
    }
  }
  
  public void b()
  {
    if (!this.e.compareAndSet(false, true)) {
      return;
    }
    this.c.destroy();
    try
    {
      this.c.waitFor();
      a.info("Process finished with exit code '" + this.c.exitValue() + "'.");
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  protected void finalize()
    throws Throwable
  {
    if (!this.e.get()) {
      a.error(String.format("Process '%s' in directory '%s' has not been destroyed.", new Object[] { this.b, this.d.getAbsolutePath() }));
    }
    b();
    super.finalize();
  }
  
  public static a a(String paramString, Map paramMap, File paramFile, String... paramVarArgs)
    throws IOException
  {
    File localFile1 = new File(paramString);
    File localFile2 = a(localFile1.getParentFile());
    String str1 = FilenameUtils.getExtension(localFile1.getName());
    d locald;
    if ("zip".equalsIgnoreCase(str1))
    {
      ZipUtil.unzip(localFile1, localFile2);
      locald = m.a(localFile1.getName().substring(0, localFile1.getName().length() - ".zip".length()));
    }
    else
    {
      localObject = new File(localFile2, localFile1.getName());
      Files.createSymbolicLink(FileSystems.getDefault().getPath(((File)localObject).getAbsolutePath(), new String[0]), FileSystems.getDefault().getPath(localFile1.getAbsolutePath(), new String[0]), new FileAttribute[0]);
      locald = m.a(paramString);
    }
    a(locald, localFile2, paramFile, paramMap);
    Object localObject = locald.a(FilenameUtils.getName(paramString), paramMap);
    ArrayList localArrayList = new ArrayList(Arrays.asList(a(localFile2, (String)localObject)));
    Collections.addAll(localArrayList, paramVarArgs);
    if (!new File((String)localArrayList.get(0)).isAbsolute()) {
      localArrayList.set(0, new File(localFile2, (String)localArrayList.get(0)).getAbsolutePath());
    }
    String str2 = a(localArrayList);
    File localFile3 = new File(localFile2, "run.bat");
    if (!FileUtil.isFile(localFile3)) {
      FileUtil.writeFile(localFile3, str2.getBytes(StandardCharsets.UTF_8));
    }
    Process localProcess = new ProcessBuilder(localArrayList).directory(localFile2).start();
    a(localProcess, localProcess.getInputStream(), new File(localFile2, "runexe.output"));
    a(localProcess, localProcess.getErrorStream(), new File(localFile2, "runexe.error"));
    a.info("Running '" + str2 + "' in the '" + localFile2 + "'.");
    return new a((String)localObject, localProcess, localFile2);
  }
  
  private static void a(Process paramProcess, InputStream paramInputStream, File paramFile)
  {
    new Thread(new c(paramInputStream, paramFile, paramProcess)).start();
  }
  
  private static String a(List paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(' ');
      }
      localStringBuilder.append('"').append(str).append('"');
    }
    return localStringBuilder.toString();
  }
  
  private static void a(d paramd, File paramFile1, File paramFile2, Map paramMap)
    throws IOException
  {
    Iterator localIterator = paramd.a().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if ((paramMap != null) && (paramd.a(str1)))
      {
        InputStream localInputStream = null;
        String str2;
        try
        {
          localInputStream = a.class.getResourceAsStream(str1);
          str2 = new String(IoUtil.toByteArray(localInputStream), StandardCharsets.UTF_8);
          localObject1 = paramMap.entrySet().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject1).next();
            str2 = StringUtil.replace(str2, "${" + (String)localEntry.getKey() + '}', (String)localEntry.getValue());
          }
        }
        finally
        {
          IoUtil.closeQuietly(localInputStream);
        }
        Object localObject1 = new File(paramFile1, new File(str1).getName());
        ResourceUtil.saveResourceToFile((File)localObject1, null, str2.getBytes(StandardCharsets.UTF_8), null);
      }
      else
      {
        ResourceUtil.copyResourceToDir(paramFile1, paramFile2, str1, null, a.class, false);
      }
    }
  }
  
  private static File a(File paramFile)
    throws IOException
  {
    File localFile;
    do
    {
      localFile = new File(paramFile, String.format("%s-%s", new Object[] { new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new Date()), RandomStringUtils.randomAlphanumeric(4) }));
    } while (localFile.exists());
    if (!localFile.mkdirs()) {
      throw new IOException("Can't create temporary directory '" + localFile + "'.");
    }
    return localFile;
  }
  
  private static String[] a(File paramFile, String paramString)
  {
    if (new File(paramFile, paramString).exists()) {
      return new String[] { paramString };
    }
    paramString = paramString + " ";
    int i = 0;
    int j = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    ArrayList localArrayList = new ArrayList();
    for (int k = 0; k < paramString.length(); k++)
    {
      char c1 = paramString.charAt(k);
      if (c1 == '\\')
      {
        i ^= 0x1;
        if (i == 0) {
          localStringBuilder.append('\\');
        }
      }
      else
      {
        if (c1 == '"')
        {
          if (i == 0) {
            j = j == 0 ? 1 : 0;
          } else {
            localStringBuilder.append('"');
          }
        }
        else
        {
          if (i == 1) {
            localStringBuilder.append('\\');
          }
          if ((c1 <= ' ') && (j == 0))
          {
            if (localStringBuilder.length() > 0)
            {
              localArrayList.add(localStringBuilder.toString());
              localStringBuilder.setLength(0);
            }
          }
          else {
            localStringBuilder.append(c1);
          }
        }
        i = 0;
      }
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\a\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */