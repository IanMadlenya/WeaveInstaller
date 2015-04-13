package weave.plugins;

import javax.swing.JLabel;
import javax.swing.JPanel;

import weave.configs.JettyConfig;
import weave.utils.EnvironmentUtils;

public class JettyPlugin extends Plugin
{
	private static JettyPlugin _instance = null;
	public static JettyPlugin getPlugin()
	{
		if( _instance == null )
			_instance = new JettyPlugin();
		return _instance;
	}
	
	public JettyPlugin()
	{
		super(JettyConfig.NAME);
		
		String filename = "";
		String url = JettyConfig.getConfig().getDownloadURL();
		if( url != null )
			filename = url.substring(url.lastIndexOf("/") + 1);
		
		setPluginVersion(JettyConfig.getConfig().getInstallVersion());
		setPluginHomepageURL(JettyConfig.getConfig().getHomepageURL());
		setPluginDownloadURL(url);
		setPluginDescription(JettyConfig.getConfig().getDescription());
		setPluginDownloadFile("${" + EnvironmentUtils.DOWNLOAD_DIR + "}/" + filename);
		setPluginBaseDirectory("${" + EnvironmentUtils.PLUGINS_DIR + "}/" + getPluginName());
	}
	
	@Override
	public JPanel getPluginPanel()
	{
		JPanel panel = super.getPluginPanel();
		
		JLabel l = new JLabel(getPluginName());
		l.setBounds(40, 40, 150, 25);
		panel.add(l);
		
		return panel;
	}
}
