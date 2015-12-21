import com.a.b.a.a.e.a;
import com.a.b.a.a.e.b;
import com.a.b.c;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.log4j.Logger;

public final class LocalTestRunner
{
  public static void main(String[] paramArrayOfString)
    throws IOException
  {
    Logger.getRootLogger().removeAllAppenders();
    Properties localProperties = new Properties();
    localProperties.load(new InputStreamReader(new FileInputStream(paramArrayOfString[0]), StandardCharsets.UTF_8));
    Long localLong = getSeed(paramArrayOfString, localProperties);
    MutableBoolean localMutableBoolean1 = new MutableBoolean(Boolean.parseBoolean(StringUtil.trimToEmpty(localProperties.getProperty("render-to-screen"))));
    MutableBoolean localMutableBoolean2 = new MutableBoolean(Boolean.parseBoolean(StringUtil.trimToEmpty(localProperties.getProperty("render-to-screen-sync"))));
    String str1 = getRenderToScreenSize(localProperties);
    String str2 = StringUtil.trimToEmpty(localProperties.getProperty("results-file"));
    String str3 = StringUtil.trimToEmpty(localProperties.getProperty("log-file"));
    String str4 = StringUtil.trimToEmpty(localProperties.getProperty("replay-file"));
    int i = getBaseAdapterPort(localProperties);
    String str5 = StringUtil.trimToEmpty(localProperties.getProperty("map"));
    str5 = Pattern.compile("[^_01-9a-zA-Z\\.\\-]+").matcher(str5).replaceAll("");
    if ((!str5.isEmpty()) && (!str5.endsWith(".map"))) {
      str5 = str5 + ".map";
    }
    int j = getTickCount(localProperties);
    int k = getTeamSize(localProperties);
    int m = getPlayerCount(localProperties);
    int n = getPsychoLevel(localProperties);
    boolean bool1 = Boolean.parseBoolean(StringUtil.trimToEmpty(localProperties.getProperty("swap-car-types")));
    boolean bool2 = Boolean.parseBoolean(StringUtil.trimToEmpty(localProperties.getProperty("loose-map-check")));
    String str6 = getPluginsDirectory(localProperties);
    String[] arrayOfString1 = new String[m];
    String[] arrayOfString2 = new String[m];
    setupPlayerNamesAndDefinitions(localProperties, localMutableBoolean1, localMutableBoolean2, m, arrayOfString1, arrayOfString2);
    ArrayList localArrayList = new ArrayList();
    if (j > 0) {
      localArrayList.add("-tick-count=" + j);
    }
    localArrayList.add("-render-to-screen=" + localMutableBoolean1.booleanValue());
    localArrayList.add("-render-to-screen-sync=" + localMutableBoolean2.booleanValue());
    localArrayList.add("-render-to-screen-size=" + str1);
    localArrayList.add("-results-file=" + str2);
    localArrayList.add("-write-to-text-file=" + str3);
    localArrayList.add("-replay-file=" + str4);
    localArrayList.add("-map=" + str5);
    localArrayList.add("-debug=true");
    localArrayList.add("-base-adapter-port=" + i);
    if (localLong != null) {
      localArrayList.add("-seed=" + localLong);
    }
    if (n > 0) {
      localArrayList.add("-psycho-level=" + n);
    }
    if (bool1) {
      localArrayList.add("-swap-car-types=true");
    }
    if (bool2) {
      localArrayList.add("-loose-map-check=true");
    }
    if (str6 != null) {
      localArrayList.add("-plugins-directory=" + str6);
    }
    for (int i1 = 0; i1 < m; i1++)
    {
      localArrayList.add("-p" + (i1 + 1) + "-name=" + arrayOfString1[i1]);
      localArrayList.add("-p" + (i1 + 1) + "-team-size=" + k);
      localArrayList.add(arrayOfString2[i1]);
    }
    new c((String[])localArrayList.toArray(new String[localArrayList.size()])).run();
  }
  
  private static void setupPlayerNamesAndDefinitions(Properties paramProperties, MutableBoolean paramMutableBoolean1, MutableBoolean paramMutableBoolean2, int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    HashMap localHashMap = new HashMap(paramInt);
    int i = 0;
    for (int j = 0; j < paramInt; j++)
    {
      String str1 = StringUtil.trimToEmpty(paramProperties.getProperty("p" + (j + 1) + "-type"));
      String str2 = StringUtil.trimToEmpty(paramProperties.getProperty("p" + (j + 1) + "-name"));
      Object localObject = str1;
      int k = -1;
      switch (((String)localObject).hashCode())
      {
      case 73592651: 
        if (((String)localObject).equals("Local")) {
          k = 0;
        }
        break;
      case 78394829: 
        if (((String)localObject).equals("Quick")) {
          k = 1;
        }
        break;
      case 79996329: 
        if (((String)localObject).equals("Smart")) {
          k = 2;
        }
        break;
      case 568383495: 
        if (((String)localObject).equals("Keyboard")) {
          k = 3;
        }
        break;
      case 67081517: 
        if (((String)localObject).equals("Empty")) {
          k = 4;
        }
        break;
      }
      switch (k)
      {
      case 0: 
        paramArrayOfString2[j] = "#LocalTestPlayer";
        if (str2.isEmpty()) {
          str2 = "MyStrategy";
        }
        break;
      case 1: 
        paramArrayOfString2[j] = (b.class.getSimpleName() + ".class");
        if (str2.isEmpty()) {
          str2 = "QuickStartGuy";
        }
        break;
      case 2: 
        paramArrayOfString2[j] = (a.class.getSimpleName() + ".class");
        str2 = str2.isEmpty() ? "EmptyPlayer" : str2;
        break;
      case 3: 
        if (i != 0) {
          throw new IllegalArgumentException("Can't add two or more keyboard players.");
        }
        i = 1;
        paramMutableBoolean1.setValue(true);
        paramMutableBoolean2.setValue(true);
        paramArrayOfString2[j] = "#KeyboardPlayer";
        if (str2.isEmpty()) {
          str2 = "KeyboardPlayer";
        }
        break;
      case 4: 
      default: 
        paramArrayOfString2[j] = (a.class.getSimpleName() + ".class");
        str2 = str2.isEmpty() ? "EmptyPlayer" : str2;
      }
      localObject = (Integer)localHashMap.get(str2);
      localObject = Integer.valueOf(localObject == null ? 1 : ((Integer)localObject).intValue() + 1);
      localHashMap.put(str2, localObject);
      paramArrayOfString1[j] = (((Integer)localObject).intValue() == 1 ? str2 : String.format("%s (%d)", new Object[] { str2, localObject }));
    }
  }
  
  private static Long getSeed(String[] paramArrayOfString, Properties paramProperties)
  {
    if (paramArrayOfString.length > 1) {
      try
      {
        return Long.valueOf(paramArrayOfString[1]);
      }
      catch (NumberFormatException localNumberFormatException1) {}
    }
    try
    {
      return Long.valueOf(StringUtil.trimToEmpty(paramProperties.getProperty("seed")));
    }
    catch (NumberFormatException localNumberFormatException2) {}
    return null;
  }
  
  private static String getRenderToScreenSize(Properties paramProperties)
  {
    return StringUtil.trimToEmpty(paramProperties.getProperty("render-to-screen-size"));
  }
  
  private static int getBaseAdapterPort(Properties paramProperties)
  {
    int i;
    try
    {
      i = Integer.parseInt(StringUtil.trimToEmpty(paramProperties.getProperty("base-adapter-port")));
      i = Math.max(Math.min(i, 65535), 1);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      i = 31001;
    }
    return i;
  }
  
  private static int getTickCount(Properties paramProperties)
  {
    int i;
    try
    {
      i = Integer.parseInt(StringUtil.trimToEmpty(paramProperties.getProperty("tick-count")));
      i = Math.max(Math.min(i, 10000000), 1000);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      i = Integer.MIN_VALUE;
    }
    return i;
  }
  
  private static int getTeamSize(Properties paramProperties)
  {
    int i;
    try
    {
      i = Integer.parseInt(StringUtil.trimToEmpty(paramProperties.getProperty("team-size")));
      i = Math.max(Math.min(i, 2), 1);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      i = 1;
    }
    return i;
  }
  
  private static int getPlayerCount(Properties paramProperties)
  {
    int i;
    try
    {
      i = Integer.parseInt(StringUtil.trimToEmpty(paramProperties.getProperty("player-count")));
      i = Math.max(Math.min(i, 4), 1);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      i = 4;
    }
    return i;
  }
  
  private static int getPsychoLevel(Properties paramProperties)
  {
    int i;
    try
    {
      i = Integer.parseInt(StringUtil.trimToEmpty(paramProperties.getProperty("psycho-level")));
      i = Math.max(Math.min(i, 255), 0);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      i = 0;
    }
    return i;
  }
  
  private static String getPluginsDirectory(Properties paramProperties)
  {
    String str = paramProperties.getProperty("plugins-directory");
    if ((StringUtil.isBlank(str)) || (!new File(str).isDirectory())) {
      str = null;
    }
    return str;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\LocalTestRunner.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */