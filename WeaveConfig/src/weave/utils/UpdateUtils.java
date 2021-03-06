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

package weave.utils;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import weave.Globals;
import weave.Settings;
import weave.Settings.INSTALL_ENUM;
import weave.managers.TrayManager;

public class UpdateUtils extends Globals
{
	public static final int FROM_USER = 1;
	public static final int FROM_EVENT = 2;
	
	public static final int NO_UPDATE_AVAILABLE = 0;
	public static final int UPDATE_AVAILABLE = 1;
	public static final int UPDATE_OFFLINE = 2;
	public static final int UPDATE_ERROR = 3;
	
	public static List<String> entriesToCheck	= null;
	public static List<String> lookupEntries	= null;
	
	/**
	 * Assign new values to lookupEntries.
	 */
	public static void refreshLookupValues()
	{
		entriesToCheck = new ArrayList<String>( Arrays.asList( 	RemoteUtils.WEAVE_UPDATER_VERSION,
																RemoteUtils.WEAVE_SERVER_VERSION,
																RemoteUtils.WEAVE_LAUNCHER_VERSION));
		lookupEntries = new ArrayList<String>( Arrays.asList( 	Settings.UPDATER_VER,
																Settings.SERVER_VER,
																Settings.LAUNCHER_VER));
	}
	
	public static boolean isServerUpdateAvailable() 
	{
		if( Settings.isOfflineMode() )
			return false;
	
		File tempFile;
		boolean missingFile = false;
		boolean outOfDateFile = false;
		
		refreshLookupValues();
		
		// Loop over local and remote application versions
		// If there is a difference in the version, there is an update
		for( int i = 0; i < entriesToCheck.size(); i++ )
		{
			String value = RemoteUtils.getConfigEntry( entriesToCheck.get(i) );
			if( value == null ) return false;
			
			outOfDateFile |= !(value).equals( lookupEntries.get(i) );
		}
		
		// Loop over the remote required files.
		// If there is a missing file locally, there is an update
		String[] files = RemoteUtils.getRemoteFiles();
		for( String f : files ) {
			tempFile = new File(Settings.WEAVE_ROOT_DIRECTORY, f.trim());
			if( !tempFile.exists() ) {
				missingFile = true;
				break;
			}
		}
		return ( missingFile || outOfDateFile );
	}
	
	public static void checkForServerUpdate(int from)
	{
		boolean isUpdate = isServerUpdateAvailable();
		
		if( isUpdate )
		{
			Settings.UPDATE_OVERRIDE = true;
			Settings.save();
			
			TrayManager.displayUpdateMessage(
					Settings.PROJECT_NAME + " Update Available!", 
					"Click on this message to recieve the update.",
					MessageType.INFO);
		}
		else
		{
			if( from == FROM_USER )
				TrayManager.displayTrayMessage(
						Settings.UPDATER_NAME,
						"No new updates available",
						MessageType.INFO);
		}
	}

	public static int isWeaveUpdateAvailable() throws InterruptedException, IOException
	{
		if( Settings.isOfflineMode() )
			return UPDATE_OFFLINE; 
		
		String urlStr = (Settings.INSTALL_MODE == INSTALL_ENUM.NIGHTLY ? getLatestNightlyURL() : getLatestMilestoneURL());
		if( urlStr == null )
			return UPDATE_ERROR;
		
		String fileName = getWeaveUpdateFileName(urlStr);
		if( fileName == null )
			return UPDATE_ERROR;
		
		return ( new File(Settings.WEAVE_BINARIES_DIRECTORY, fileName).exists() ? NO_UPDATE_AVAILABLE : UPDATE_AVAILABLE );
	}

	public static int checkForWeaveUpdate(boolean save) throws InterruptedException, IOException
	{
		int isUpdate = isWeaveUpdateAvailable();

		if( save && isUpdate != UPDATE_ERROR ) {
			Settings.LAST_UPDATE_CHECK = new SimpleDateFormat("M/d/yyyy h:mm a").format(new Date());
			Settings.save();
		}
		
		return isUpdate;
	}
	
	public static String getWeaveUpdateFileName(String url) throws InterruptedException, IOException
	{
		if( Settings.isOfflineMode() )
			return null;
		
		String search = "filename=";
		String header = URLRequestUtils.getContentHeader(url, "Content-Disposition");
		int index = ((header != null ) ? header.indexOf(search) : -1 );
		if( index == -1 )
			return null;
		
		return header.substring(index + search.length());
	}
	
	public static String getLatestNightlyURL()
	{
		return RemoteUtils.getConfigEntry(RemoteUtils.WEAVE_BINARIES_URL);
	}

	public static String getLatestMilestoneURL() throws IOException, InterruptedException
	{
		String urlStr = RemoteUtils.getConfigEntry(RemoteUtils.WEAVE_TAGS_URL);
		URLRequestResult result = URLRequestUtils.request(URLRequestUtils.GET, urlStr);
		if( result == null )
			return null;
		
		JSONArray tags = new JSONArray(result.getResponseContent());
		String url = tags.getJSONObject(0).getString("zipball_url");
		return url;
	}
}
