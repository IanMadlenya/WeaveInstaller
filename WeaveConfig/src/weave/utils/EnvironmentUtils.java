package weave.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import weave.Globals;
import weave.Settings;
import weave.configs.IConfig;
import weave.managers.ConfigManager;

public class EnvironmentUtils extends Globals
{
	public static final String WEBAPPS = "SERVLET_WEBAPPS";
	public static final String PLUGINS_DIR = "PLUGINS_DIR";
	public static final String DOWNLOAD_DIR = "DOWNLOAD_DIR";
	
	private static Map<String, String> customEnv = null;
	
	private static void refresh()
	{
		IConfig servlet = ConfigManager.getConfigManager().getActiveContainer();
		
		if( customEnv == null )
			customEnv = new HashMap<String, String>();
		else
			customEnv.clear();
		
		if( servlet != null && servlet.getWebappsDirectory() != null )
			customEnv.put(WEBAPPS, servlet.getWebappsDirectory().getAbsolutePath());
		
		customEnv.put(PLUGINS_DIR, Settings.DEPLOYED_PLUGINS_DIRECTORY.getAbsolutePath());
		customEnv.put(DOWNLOAD_DIR, Settings.DOWNLOADS_DIRECTORY.getAbsolutePath());
	}
	
	public static String replace(String str)
	{
		Properties javaProps = System.getProperties();
		Map<String, String> sysEnv = new HashMap<String, String>(System.getenv());
		Map<String, String> javaEnv = new HashMap<String, String>();
		
		refresh();
		
		for( String key : javaProps.stringPropertyNames() )
			javaEnv.put(key, javaProps.getProperty(key));

		for( Entry<String, String> e : javaEnv.entrySet() )
			sysEnv.put(e.getKey(), e.getValue());
		
		for( Entry<String, String> e : customEnv.entrySet() )
			sysEnv.put(e.getKey(), e.getValue());
		
		for( Entry<String, String> entry : sysEnv.entrySet() )
			str = str.replaceAll("\\$\\{" + entry.getKey() + "\\}", entry.getValue().replaceAll("\\\\", "/"));
		
		return str;
	}
}
