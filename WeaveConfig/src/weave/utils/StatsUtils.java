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

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.io.IOException;

import weave.Globals;
import weave.Settings;
import weave.async.AsyncFunction;
import weave.managers.ConfigManager;

public class StatsUtils extends Globals
{
	public static void logUpdate()
	{
		logUpdate( false );
	}
	
	public static void logUpdate( boolean forced )
	{
		if( Settings.isOfflineMode() )
			return;
		
		final URLRequestParams params = new URLRequestParams();
		params.add("action", 	"UPDATE");
		params.add("uniqueID",	Settings.UNIQUE_ID);
		params.add("forced", 	(forced ? "1" : "0"));
		
		AsyncFunction task = new AsyncFunction() {
			@Override
			public Object doInBackground() {
				try {
					return URLRequestUtils.request(URLRequestUtils.GET, Settings.API_STATS_LOG, params);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return null;
			}
		};
		task.call();
	}

	public static void noop()
	{
		if( Settings.isOfflineMode() )
			return;
		
		try {
			final URLRequestParams params = new URLRequestParams();
			params.add("uniqueID", 	Settings.UNIQUE_ID);
			params.add("os", 		Settings.getExactOS());
			params.add("server", 	(String) ObjectUtils.ternary(ConfigManager.getConfigManager().getActiveContainer(), "getConfigName", "NONE"));
			params.add("database", 	(String) ObjectUtils.ternary(ConfigManager.getConfigManager().getActiveDatabase(), "getConfigName", "NONE"));
			
			AsyncFunction task = new AsyncFunction() {
				@Override
				public Object doInBackground() {
					try {
						return URLRequestUtils.request(URLRequestUtils.POST, Settings.API_STATS_LIVE, params);
					} catch (IOException e) {
						// Do nothing here, might be a bug
						// In the purpose of this function, I would rather ignore
						// bugs than to alert the user every time it hits one.
					} catch (Exception e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					}
					return null;
				}
			};
			task.call();
			
		} catch (Exception e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
}
