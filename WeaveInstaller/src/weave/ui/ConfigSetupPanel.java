/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2014 University of Massachusetts Lowell

    This file is a part of Weave.

    Weave is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License, Version 3,
    as published by the Free Software Foundation.

    Weave is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Weave.  If not, see <http://www.gnu.org/licenses/>.
*/

package weave.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import weave.Settings;
import weave.configs.IConfig;
import weave.configs.Jetty;
import weave.configs.SQLite;
import weave.inc.SetupPanel;
import weave.managers.ConfigManager;
import weave.utils.BugReportUtils;
import weave.utils.ImageUtils;
import weave.utils.TraceUtils;

@SuppressWarnings("serial")
public class ConfigSetupPanel extends SetupPanel
{
	public JComboBox<String>	servletCombo,	databaseCombo;
	public JLabel				servletImage, 	databaseImage;
	public JLabel				servletLevel,	databaseLevel;
	public JEditorPane			servletDesc, 	databaseDesc;
	public JEditorPane			servletWarning,	databaseWarning;

	public JLabel				servletWebappsLabel,	servletPortLabel,	databasePortLabel;
	public JTextField			servletBrowserPath,		servletPortInput,	databasePortInput;
	public JFileChooser			servletFileChooser;
	public JButton				servletBrowserButton;
	
	public ConfigSetupPanel()
	{
		maxPanels = 3;

		setLayout(null);
		setBounds(0, 0, 350, 325);
		
		JPanel panel = null;
		for (int i = 0; i < maxPanels; i++) {
			switch (i) {
				case 0: panel = createServletPanel(); 	break;
				case 1: panel = createDatabasePanel();	break;
				case 2: panel = createReviewPanel();	break;
			}
			panels.add(panel);
			add(panel);
		}
		hidePanels();
	}
	
	public JPanel createServletPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		
		// Title
		JLabel title = new JLabel("Select Servlet");
		title.setFont(new Font(Settings.FONT, Font.BOLD, 15));
		title.setBounds(20, 20, 140, 30);
		panel.add(title);
		
		// Servlet Combobox
		List<IConfig> servlets = ConfigManager.getConfigManager().getServletConfigs();
		servletCombo = new JComboBox<String>();
		servletCombo.setBounds(160, 22, 170, 22);
		servletCombo.setVisible(true);
		servletCombo.setEnabled(true);
		servletCombo.setFont(new Font(Settings.FONT, Font.PLAIN, 13));
		
		for( int i = 0; i < servlets.size(); i++ ) 
			servletCombo.addItem(servlets.get(i).getConfigName());
		
		servletCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateServletInfo(
						ConfigManager
						.getConfigManager()
						.getConfigByName(servletCombo.getSelectedItem()));
			}
		});
		panel.add(servletCombo);

		// Experience label
		servletLevel = new JLabel("Experience Level: ");
		servletLevel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		servletLevel.setBounds(20, 60, 300, 25);
		panel.add(servletLevel);
		
		// Image
		servletImage = new JLabel((ImageIcon)null, JLabel.CENTER);
		servletImage.setBounds(20, 90, 80, 80);
		servletImage.setVerticalAlignment(JLabel.TOP);
		panel.add(servletImage);

		
		// Description
		servletDesc = new JEditorPane();
		servletDesc.setBounds(110, 90, 210, 100);
		servletDesc.setEditable(false);
		servletDesc.setContentType("text/html");
		servletDesc.setFont(new Font(Settings.FONT, Font.PLAIN, 10));
		String styleDesc =  "body { font-family: " + servletDesc.getFont().getFamily() + "; " + 
						"font-size: " + servletDesc.getFont().getSize() + "px; }";
		((HTMLDocument)servletDesc.getDocument()).getStyleSheet().addRule(styleDesc);
		servletDesc.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
				{
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					} catch (URISyntaxException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					}
				}
			}
		});
		panel.add(servletDesc);
		
		
		// Warning
		servletWarning = new JEditorPane();
		servletWarning.setBounds(20, 190, 310, 60);
		servletWarning.setEditable(false);
		servletWarning.setContentType("text/html");
		servletWarning.setFont(new Font(Settings.FONT, Font.PLAIN, 10));
		String styleWarning = 	"body { font-family: " + servletWarning.getFont().getFamily() + "; " + 
								"font-size: " + servletWarning.getFont().getSize() + "px; }";
		((HTMLDocument)servletWarning.getDocument()).getStyleSheet().addRule(styleWarning);
		servletWarning.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
				{
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					} catch (URISyntaxException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					}
				}
			}
		});
		panel.add(servletWarning);

		
		// Servlet directory label
		servletWebappsLabel = new JLabel("Webapps:");
		servletWebappsLabel.setBounds(20, 280, 70, 25);
		servletWebappsLabel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		panel.add(servletWebappsLabel);
		
		
		// Servlet directory chooser
		servletFileChooser = new JFileChooser();
		servletFileChooser.setMultiSelectionEnabled(false);
		servletFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		servletFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		
		servletBrowserPath = new JTextField();
		servletBrowserPath.setBounds(100, 280, 150, 25);
		servletBrowserPath.setEditable(false);
		servletBrowserPath.setFont(new Font(Settings.FONT, Font.PLAIN, 13));
		servletBrowserPath.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		servletBrowserPath.setVisible(true);
		panel.add(servletBrowserPath);
		
		servletBrowserButton = new JButton("Browse");
		servletBrowserButton.setBounds(260, 280, 70, 25);
		servletBrowserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int retVal = servletFileChooser.showOpenDialog(ConfigSetupPanel.this);
				if( retVal == JFileChooser.APPROVE_OPTION )
				{
					IConfig config = ConfigManager
										.getConfigManager()
										.getConfigByName(servletCombo.getSelectedItem());
					File selectedFolder = servletFileChooser.getSelectedFile();
					if( (new File(selectedFolder, "ROOT")).exists() )
					{
						servletBrowserPath.setText(selectedFolder.getAbsolutePath());
						config.setWebappsDirectory(selectedFolder);
					}
					else
					{
						JOptionPane.showMessageDialog(null, 
								"This does not appear to be a valid webapps directory.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		servletBrowserButton.setVisible(true);
		panel.add(servletBrowserButton);
		
		
		// Servlet Port
		servletPortLabel = new JLabel("Port:");
		servletPortLabel.setBounds(20, 250, 70, 25);
		servletPortLabel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		servletPortLabel.setVisible(true);
		panel.add(servletPortLabel);

		
		// Servlet Port Input
		servletPortInput = new JTextField();
		servletPortInput.setBounds(100, 250, 150, 25);
		servletPortInput.setEditable(true);
		servletPortInput.setFont(new Font(Settings.FONT, Font.PLAIN, 14));
		servletPortInput.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		servletPortInput.setVisible(true);
		panel.add(servletPortInput);
		
		
		// Apply default values
		servletCombo.setSelectedIndex(0);
		
		
		return panel;
	}
	
	public JPanel createDatabasePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		// Title
		JLabel title = new JLabel("Select Database");
		title.setFont(new Font(Settings.FONT, Font.BOLD, 15));
		title.setBounds(20, 20, 140, 30);
		panel.add(title);
		
		// Database Combobox
		List<IConfig> databases = ConfigManager.getConfigManager().getDatabaseConfigs();
		databaseCombo = new JComboBox<String>();
		databaseCombo.setBounds(160, 22, 170, 22);
		databaseCombo.setVisible(true);
		databaseCombo.setEnabled(true);
		databaseCombo.setFont(new Font(Settings.FONT, Font.PLAIN, 13));
		
		for( int i = 0; i < databases.size(); i++ )
			databaseCombo.addItem(databases.get(i).getConfigName());
		
		databaseCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDatabaseInfo(
						ConfigManager
							.getConfigManager()
							.getConfigByName(databaseCombo.getSelectedItem()));
			}
		});
		panel.add(databaseCombo);

		// Experience label
		databaseLevel = new JLabel("Experience Level: ");
		databaseLevel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		databaseLevel.setBounds(20, 60, 300, 25);
		panel.add(databaseLevel);
		
		// Image
		databaseImage = new JLabel((ImageIcon)null, JLabel.CENTER);
		databaseImage.setBounds(20, 90, 80, 80);
		databaseImage.setVerticalAlignment(JLabel.TOP);
		panel.add(databaseImage);

		
		// Description
		databaseDesc = new JEditorPane();
		databaseDesc.setBounds(110, 90, 210, 100);
		databaseDesc.setEditable(false);
		databaseDesc.setContentType("text/html");
		databaseDesc.setFont(new Font(Settings.FONT, Font.PLAIN, 10));
		String styleDesc =  "body { font-family: " + databaseDesc.getFont().getFamily() + "; " + 
						"font-size: " + databaseDesc.getFont().getSize() + "px; }";
		((HTMLDocument)databaseDesc.getDocument()).getStyleSheet().addRule(styleDesc);
		databaseDesc.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
				{
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					} catch (URISyntaxException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					}
				}
			}
		});
		panel.add(databaseDesc);
		
		
		// Warning
		databaseWarning = new JEditorPane();
		databaseWarning.setBounds(20, 190, 310, 60);
		databaseWarning.setEditable(false);
		databaseWarning.setContentType("text/html");
		databaseWarning.setFont(new Font(Settings.FONT, Font.PLAIN, 10));
		String styleWarning=  "body { font-family: " + databaseWarning.getFont().getFamily() + "; " + 
							"font-size: " + databaseWarning.getFont().getSize() + "px; }";
		((HTMLDocument)databaseWarning.getDocument()).getStyleSheet().addRule(styleWarning);
		databaseWarning.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
				{
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					} catch (URISyntaxException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
						BugReportUtils.showBugReportDialog(ex);
					}
				}
			}
		});
		panel.add(databaseWarning);

		
		// Database port label
		databasePortLabel = new JLabel("Port:");
		databasePortLabel.setBounds(20, 250, 70, 25);
		databasePortLabel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		databasePortLabel.setVisible(true);
		panel.add(databasePortLabel);
		
		// Database port input
		databasePortInput = new JTextField();
		databasePortInput.setBounds(100, 250, 150, 25);
		databasePortInput.setFont(new Font(Settings.FONT, Font.PLAIN, 13));
		databasePortInput.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		databasePortInput.setMargin(new Insets(2, 2, 2, 2));
		databasePortInput.setVisible(true);
		panel.add(databasePortInput);
		
		// Apply default values
		databaseCombo.setSelectedIndex(0);
		
		
		return panel;
	}

	public JPanel createReviewPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		return panel;
	}
	
	
	
	private void updateServletInfo(IConfig servlet)
	{
		if( servlet == null )
			return;
		
		if( servletLevel != null )
			servletLevel.setText("Experience Level: " + servlet.getTechLevel());
		if( servletImage != null )
			servletImage.setIcon(
					new ImageIcon(ImageUtils.scale(
									servlet.getImage(), 
									servletImage.getWidth(), 
									ImageUtils.SCALE_WIDTH)));
		if( servletDesc != null )
			servletDesc.setText(servlet.getDescription());
		if( servletWarning != null )
			servletWarning.setText(servlet.getWarning());
		if( servletBrowserPath != null )
			servletBrowserPath.setText(
					( servlet.getWebappsDirectory() != null ) ?
							servlet.getWebappsDirectory().getAbsolutePath() :
							"" );
		if( servletPortInput != null )
			servletPortInput.setText(Integer.toString(servlet.getPort()));
		
		boolean visible = !servlet.getConfigName().equals(Jetty.NAME);
		servletWebappsLabel.setVisible(visible);
		servletBrowserPath.setVisible(visible);
		servletBrowserButton.setVisible(visible);
	}
	private void updateDatabaseInfo(IConfig database)
	{
		if( database == null )
			return;
		
		if( databaseLevel != null )
			databaseLevel.setText("Experience Level: " + database.getTechLevel());
		if( databaseImage != null )
			databaseImage.setIcon(
					new ImageIcon(ImageUtils.scale(
									database.getImage(), 
									databaseImage.getWidth(), 
									ImageUtils.SCALE_WIDTH)));
		if( databaseDesc != null )
			databaseDesc.setText(database.getDescription());
		if( databaseWarning != null )
			databaseWarning.setText(database.getWarning());
		if( databasePortInput != null )
			databasePortInput.setText(Integer.toString(database.getPort()));
		
		boolean visible = !database.getConfigName().equals(SQLite.NAME);
		databasePortLabel.setVisible(visible);
		databasePortInput.setVisible(visible);
	}
}
