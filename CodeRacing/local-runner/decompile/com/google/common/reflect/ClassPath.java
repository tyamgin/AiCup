package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

@Beta
public final class ClassPath
{
  private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
  private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
  private static final String CLASS_FILE_NAME_EXTENSION = ".class";
  private final ImmutableSet resources;
  
  private ClassPath(ImmutableSet paramImmutableSet)
  {
    this.resources = paramImmutableSet;
  }
  
  public static ClassPath from(ClassLoader paramClassLoader)
    throws IOException
  {
    ImmutableSortedSet.Builder localBuilder = new ImmutableSortedSet.Builder(Ordering.usingToString());
    Iterator localIterator = getClassPathEntries(paramClassLoader).entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      browse((URI)localEntry.getKey(), (ClassLoader)localEntry.getValue(), localBuilder);
    }
    return new ClassPath(localBuilder.build());
  }
  
  public ImmutableSet getResources()
  {
    return this.resources;
  }
  
  public ImmutableSet getTopLevelClasses()
  {
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    Iterator localIterator = this.resources.iterator();
    while (localIterator.hasNext())
    {
      ResourceInfo localResourceInfo = (ResourceInfo)localIterator.next();
      if ((localResourceInfo instanceof ClassInfo)) {
        localBuilder.add((ClassInfo)localResourceInfo);
      }
    }
    return localBuilder.build();
  }
  
  public ImmutableSet getTopLevelClasses(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    Iterator localIterator = getTopLevelClasses().iterator();
    while (localIterator.hasNext())
    {
      ClassInfo localClassInfo = (ClassInfo)localIterator.next();
      if (localClassInfo.getPackageName().equals(paramString)) {
        localBuilder.add(localClassInfo);
      }
    }
    return localBuilder.build();
  }
  
  public ImmutableSet getTopLevelClassesRecursive(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    String str = paramString + '.';
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    Iterator localIterator = getTopLevelClasses().iterator();
    while (localIterator.hasNext())
    {
      ClassInfo localClassInfo = (ClassInfo)localIterator.next();
      if (localClassInfo.getName().startsWith(str)) {
        localBuilder.add(localClassInfo);
      }
    }
    return localBuilder.build();
  }
  
  @VisibleForTesting
  static ImmutableMap getClassPathEntries(ClassLoader paramClassLoader)
  {
    LinkedHashMap localLinkedHashMap = Maps.newLinkedHashMap();
    ClassLoader localClassLoader = paramClassLoader.getParent();
    if (localClassLoader != null) {
      localLinkedHashMap.putAll(getClassPathEntries(localClassLoader));
    }
    if ((paramClassLoader instanceof URLClassLoader))
    {
      URLClassLoader localURLClassLoader = (URLClassLoader)paramClassLoader;
      for (URL localURL : localURLClassLoader.getURLs())
      {
        URI localURI;
        try
        {
          localURI = localURL.toURI();
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw new IllegalArgumentException(localURISyntaxException);
        }
        if (!localLinkedHashMap.containsKey(localURI)) {
          localLinkedHashMap.put(localURI, paramClassLoader);
        }
      }
    }
    return ImmutableMap.copyOf(localLinkedHashMap);
  }
  
  private static void browse(URI paramURI, ClassLoader paramClassLoader, ImmutableSet.Builder paramBuilder)
    throws IOException
  {
    if (paramURI.getScheme().equals("file")) {
      browseFrom(new File(paramURI), paramClassLoader, paramBuilder);
    }
  }
  
  @VisibleForTesting
  static void browseFrom(File paramFile, ClassLoader paramClassLoader, ImmutableSet.Builder paramBuilder)
    throws IOException
  {
    if (!paramFile.exists()) {
      return;
    }
    if (paramFile.isDirectory()) {
      browseDirectory(paramFile, paramClassLoader, paramBuilder);
    } else {
      browseJar(paramFile, paramClassLoader, paramBuilder);
    }
  }
  
  private static void browseDirectory(File paramFile, ClassLoader paramClassLoader, ImmutableSet.Builder paramBuilder)
  {
    browseDirectory(paramFile, paramClassLoader, "", paramBuilder);
  }
  
  private static void browseDirectory(File paramFile, ClassLoader paramClassLoader, String paramString, ImmutableSet.Builder paramBuilder)
  {
    for (File localFile : paramFile.listFiles())
    {
      String str1 = localFile.getName();
      if (localFile.isDirectory())
      {
        browseDirectory(localFile, paramClassLoader, paramString + str1 + "/", paramBuilder);
      }
      else
      {
        String str2 = paramString + str1;
        paramBuilder.add(ResourceInfo.of(str2, paramClassLoader));
      }
    }
  }
  
  private static void browseJar(File paramFile, ClassLoader paramClassLoader, ImmutableSet.Builder paramBuilder)
    throws IOException
  {
    JarFile localJarFile;
    try
    {
      localJarFile = new JarFile(paramFile);
    }
    catch (IOException localIOException1)
    {
      return;
    }
    try
    {
      Object localObject1 = getClassPathFromManifest(paramFile, localJarFile.getManifest()).iterator();
      Object localObject2;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (URI)((Iterator)localObject1).next();
        browse((URI)localObject2, paramClassLoader, paramBuilder);
      }
      localObject1 = localJarFile.entries();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject2 = (JarEntry)((Enumeration)localObject1).nextElement();
        if ((!((JarEntry)localObject2).isDirectory()) && (!((JarEntry)localObject2).getName().startsWith("META-INF/"))) {
          paramBuilder.add(ResourceInfo.of(((JarEntry)localObject2).getName(), paramClassLoader));
        }
      }
      return;
    }
    finally
    {
      try
      {
        localJarFile.close();
      }
      catch (IOException localIOException3) {}
    }
  }
  
  @VisibleForTesting
  static ImmutableSet getClassPathFromManifest(File paramFile, Manifest paramManifest)
  {
    if (paramManifest == null) {
      return ImmutableSet.of();
    }
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    String str1 = paramManifest.getMainAttributes().getValue("Class-Path");
    if (str1 != null)
    {
      Iterator localIterator = CLASS_PATH_ATTRIBUTE_SEPARATOR.split(str1).iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        URI localURI;
        try
        {
          localURI = getClassPathEntry(paramFile, str2);
        }
        catch (URISyntaxException localURISyntaxException)
        {
          logger.warning("Invalid Class-Path entry: " + str2);
        }
        continue;
        localBuilder.add(localURI);
      }
    }
    return localBuilder.build();
  }
  
  @VisibleForTesting
  static URI getClassPathEntry(File paramFile, String paramString)
    throws URISyntaxException
  {
    URI localURI = new URI(paramString);
    if (localURI.isAbsolute()) {
      return localURI;
    }
    return new File(paramFile.getParentFile(), paramString.replace('/', File.separatorChar)).toURI();
  }
  
  @VisibleForTesting
  static String getClassName(String paramString)
  {
    int i = paramString.length() - ".class".length();
    return paramString.substring(0, i).replace('/', '.');
  }
  
  @Beta
  public static final class ClassInfo
    extends ClassPath.ResourceInfo
  {
    private final String className;
    
    ClassInfo(String paramString, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      this.className = ClassPath.getClassName(paramString);
    }
    
    public String getPackageName()
    {
      return Reflection.getPackageName(this.className);
    }
    
    public String getSimpleName()
    {
      String str = getPackageName();
      if (str.isEmpty()) {
        return this.className;
      }
      return this.className.substring(str.length() + 1);
    }
    
    public String getName()
    {
      return this.className;
    }
    
    public Class load()
    {
      try
      {
        return this.loader.loadClass(this.className);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new IllegalStateException(localClassNotFoundException);
      }
    }
    
    public String toString()
    {
      return this.className;
    }
  }
  
  @Beta
  public static class ResourceInfo
  {
    private final String resourceName;
    final ClassLoader loader;
    
    static ResourceInfo of(String paramString, ClassLoader paramClassLoader)
    {
      if ((paramString.endsWith(".class")) && (!paramString.contains("$"))) {
        return new ClassPath.ClassInfo(paramString, paramClassLoader);
      }
      return new ResourceInfo(paramString, paramClassLoader);
    }
    
    ResourceInfo(String paramString, ClassLoader paramClassLoader)
    {
      this.resourceName = ((String)Preconditions.checkNotNull(paramString));
      this.loader = ((ClassLoader)Preconditions.checkNotNull(paramClassLoader));
    }
    
    public final URL url()
    {
      return (URL)Preconditions.checkNotNull(this.loader.getResource(this.resourceName), "Failed to load resource: %s", new Object[] { this.resourceName });
    }
    
    public final String getResourceName()
    {
      return this.resourceName;
    }
    
    public int hashCode()
    {
      return this.resourceName.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ResourceInfo))
      {
        ResourceInfo localResourceInfo = (ResourceInfo)paramObject;
        return (this.resourceName.equals(localResourceInfo.resourceName)) && (this.loader == localResourceInfo.loader);
      }
      return false;
    }
    
    public String toString()
    {
      return this.resourceName;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\ClassPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */