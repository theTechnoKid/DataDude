/*******************************************************************************
 *     DataDude is a data managing application designed to have many types of data in one application
 *     Copyright (C) 2015  Ahmed R. (theTechnoKid)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.datadude.datamanaging;

import java.awt.Component;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.EventListener;

/**
 * Utilities used by various classes in DataDude
 *
 * @author theTechnoKid
 */
public abstract class Utils {

	/**
	 * Joins an array af strings using the
	 * delimeter {@code d}.
	 * @param r The array to be joined
	 * @param d The delimeter
	 * @return A string that has joined {@code r} with {@code d}
	 */
	public static String join(String r[], String d) {
		if (r.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		int i;
		for (i = 0; i < r.length - 1; i++)
			sb.append(r[i] + d);
		return sb.toString() + r[i];
	}

	/**
	 * Removes listeners from a component to be garbage collected or serialized.
	 *
	 * @param comp
	 *            The component to remove all listeners from
	 */
	@SuppressWarnings("unchecked")
	public static void removeListeners(Component comp) {
		Method[] methods = comp.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String name = method.getName();
			if (name.startsWith("remove") && name.endsWith("Listener")) {

				@SuppressWarnings("rawtypes")
				Class[] params = method.getParameterTypes();
				if (params.length == 1) {
					EventListener[] listeners = null;
					try {
						listeners = comp.getListeners(params[0]);
					} catch (Exception e) {
						// It is possible that someone could create a listener
						// that doesn't extend from EventListener. If so, ignore
						// it
						System.out.println("Listener " + params[0] + " does not extend EventListener");
						continue;
					}
					for (int j = 0; j < listeners.length; j++) {
						try {
							method.invoke(comp, new Object[] { listeners[j] });
						} catch (Exception e) {
							System.out.println("Cannot invoke removeListener method " + e);
						}
					}
				} else {
					// The only Listener method that I know of that has more
					// than
					// one argument is removePropertyChangeListener. If it is
					// something other than that, flag it and move on.
					if (!name.equals("removePropertyChangeListener"))
						System.out.println("    Wrong number of Args " + name);
				}
			}
		}
	}

	/**
	 * Downloads a file from a URL
	 * @param url The URL to download from
	 * @param localFilename The local file name
	 * @throws IOException If an error occured.
	 */
	public static void downloadFromUrl(URL url, String localFilename) throws IOException {
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URLConnection urlConn = url.openConnection();// connect

			is = urlConn.getInputStream(); // get connection inputstream
			fos = new FileOutputStream(localFilename); // open outputstream to
														// local file

			byte[] buffer = new byte[4096]; // declare 4KB buffer
			int len;

			// while we have availble data, continue downloading and storing to
			// local file
			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}
}
