/*******************************************************************************
 * Copyright 2013 Club ApplETS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ca.etsmtl.applets.etsmobile.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * Use this class to make async JSON GET/POST to signETS
 * 
 * @author Vincent Seguin, Philipp David, Micheal Bernier
 * 
 * @param <T>
 *            Type
 * @param <E>
 *            Entity
 */
public class Signets<T, E> {
	public enum FetchType {
		ARRAY, OBJECT
	}

	// private final String urlStr;
	private final String action;
	private final Object bodyParams;
	private final Class<E> typeOfClass;

	private final String liste;

	public Signets(final String action, final Object bodyParams,
			final Class<E> typeOfClass) {
		// this.urlStr = urlStr;
		this.action = action;
		this.bodyParams = bodyParams;
		this.typeOfClass = typeOfClass;
		this.liste = "liste";
	}

	@SuppressWarnings("unchecked")
	public T fetchArray() {
		ArrayList<E> array = new ArrayList<E>();

		try {
			final StringBuilder sb = buildURL();

			final Gson gson = new Gson();
			final String bodyParamsString = gson.toJson(bodyParams);

			final URLConnection conn = openConnection(sb);

			final OutputStreamWriter wr = writeBodyParams(bodyParamsString, conn);

			final String jsonString = readJSONResponse(conn, wr);

			array = getPOJOArray(gson, jsonString);
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final JSONException e) {
			e.printStackTrace();
		}

		return (T) array;
	}

	/**
	 * jsonString to pojo
	 * 
	 * @param gson
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 */
	private ArrayList<E> getPOJOArray(final Gson gson, final String jsonString)
			throws JSONException {
		ArrayList<E> array;
		final ArrayList<E> objectList = new ArrayList<E>();

		JSONObject jsonObject;
		jsonObject = new JSONObject(jsonString);

		JSONArray jsonRootArray;
		jsonRootArray = jsonObject.getJSONObject("d").getJSONArray(liste);
		android.util.Log.d("JSON", jsonRootArray.toString());
		for (int i = 0; i < jsonRootArray.length(); i++) {
			objectList.add(gson.fromJson(jsonRootArray.getJSONObject(i).toString(), typeOfClass));
		}

		array = objectList;
		return array;
	}

	/**
	 * Read from socket and return json string.
	 * 
	 * @param conn
	 * @param wr
	 * @return
	 * @throws IOException
	 */
	private String readJSONResponse(final URLConnection conn, final OutputStreamWriter wr)
			throws IOException {
		final StringWriter writer = new StringWriter();
		final InputStream in = conn.getInputStream();
		IOUtils.copy(in, writer);
		in.close();
		wr.close();

		final String jsonString = writer.toString();
		return jsonString;
	}

	/**
	 * Body params, like auth.
	 * 
	 * @param bodyParamsString
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	private OutputStreamWriter writeBodyParams(final String bodyParamsString,
			final URLConnection conn) throws IOException {
		conn.setDoOutput(true);
		final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(bodyParamsString);
		wr.flush();
		return wr;
	}

	@SuppressWarnings("unchecked")
	public T fetchObject() {
		T object = null;

		try {
			final StringBuilder sb = buildURL();

			final Gson gson = new Gson();
			final String bodyParamsString = gson.toJson(bodyParams);

			final URLConnection conn = openConnection(sb);

			final OutputStreamWriter wr = writeBodyParams(bodyParamsString, conn);

			final String jsonString = readJSONResponse(conn, wr);

			JSONObject jsonObject;
			jsonObject = new JSONObject(jsonString);

			JSONObject jsonRootArray;
			jsonRootArray = jsonObject.getJSONObject("d");
			object = (T) gson.fromJson(jsonRootArray.toString(), typeOfClass);

			// android.util.Log.d("JSON", jsonRootArray.toString());
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	private URLConnection openConnection(final StringBuilder sb) throws MalformedURLException,
			IOException {
		final URL url = new URL(sb.toString());
		final URLConnection conn = url.openConnection();
		conn.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
		return conn;
	}

	private StringBuilder buildURL() {
		final StringBuilder sb = new StringBuilder();
		sb.append("https://signets-ens.etsmtl.ca/Secure/WebServices/SignetsMobile.asmx/");
//		sb.append("/");
		sb.append(action);
		return sb;
	}
}
