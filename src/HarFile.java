import org.apache.wink.json4j.*;
import java.io.*;
import java.nio.charset.UnsupportedCharsetException;

public class HarFile extends TextFile {
	private JSONObject json;
	private Character csvDelimiter;
	final private Character CSV_DEFAULT_DELIMITER = '\t';
	final private String timingNames[] = { "blocked", "dns", "connect", "send", "wait", "receive", "ssl" };

	public HarFile(String filename, String charset)
			throws FileNotFoundException, IOException, JSONException, UnsupportedCharsetException {
		super(filename, charset);
		init();
	}

	public HarFile(String filename)
			throws FileNotFoundException, IOException, JSONException {
		super(filename);
		init();
	}

	/**
	 * Constructor helper
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JSONException
	 */
	private void init()
			throws FileNotFoundException, IOException, JSONException {
		readFile();
		json = new JSONObject(getText());
		setDelimiter(CSV_DEFAULT_DELIMITER);
	}

	/**
	 * Crawl JSONObject and generate a CSV String for entries
	 *
	 * @return
	 * 		CSV String
	 * @throws JSONException
	 */
	public String entriesToCsv(String filter)
			throws JSONException {
		StringBuilder csvBuffer = new StringBuilder();

		// CSV headers
		if(filter.equals("domains")) {
			csvBuffer.append("Domains" + csvDelimiter);
		} else {
			csvBuffer.append("Status Code" + csvDelimiter
				+ "Methode"+ csvDelimiter
				+ "HTTP Version rec" + csvDelimiter
				+ "Domain" + csvDelimiter
				+ "File" + csvDelimiter
				+ "Content type" + csvDelimiter
				+ "Accept language" + csvDelimiter
				+ "Referer" + csvDelimiter
				+ "Cookie" + csvDelimiter
				+ "Connexion" + csvDelimiter
				+ "Cache control" + csvDelimiter
				+ "HTTP Version resp" + csvDelimiter
				+ "Set Cookie" + csvDelimiter
				+ "Content Language" + csvDelimiter
				+ "Keep-Alive" + csvDelimiter
				+ "Connexion" + csvDelimiter);
		}

		csvBuffer.append(LINE_ENDING);

		JSONArray entries = ((JSONObject)json.get("log")).getJSONArray("entries");

		for(int i = 0; i < entries.length(); i++) {
			csvBuffer.append(singleEntryToCsvLine(entries.getJSONObject(i), filter));
			if(i < csvBuffer.length() - 1) {
				csvBuffer.append(LINE_ENDING);
			}
		}

		return csvBuffer.toString();
	}

	/**
	 * Process a single entry
	 *
	 * @param jsonObject
	 * @return
	 * 		CSV String line
	 * @throws JSONException
	 */
	private String singleEntryToCsvLine(JSONObject jsonObject, String filter)
			throws JSONException {
		StringBuilder csvLineBuffer = new StringBuilder();

		JSONObject requestObject = jsonObject.getJSONObject("request");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject contentObject = responseObject.getJSONObject("content");

		// General keys
		if(filter.equals("all")) {
			csvLineBuffer.append(processKey(responseObject, "status") + csvDelimiter);
			csvLineBuffer.append(processKey(requestObject, "method") + csvDelimiter);
			csvLineBuffer.append(processKey(requestObject, "httpVersion") + csvDelimiter);
			csvLineBuffer.append(processKey(requestObject.getJSONArray("headers"), "Host") + csvDelimiter);
			csvLineBuffer.append(truncate(processKey(requestObject.getJSONArray("headers"), "url")) + csvDelimiter);
			csvLineBuffer.append(processKey(responseObject.getJSONArray("headers"), "Content-Type") + csvDelimiter);
			csvLineBuffer.append(processKey(requestObject.getJSONArray("headers"), "Accept-Language") + csvDelimiter);
			csvLineBuffer.append(truncate(processKey(requestObject.getJSONArray("headers"), "Referer")) + csvDelimiter);
			csvLineBuffer.append(processKey(requestObject.getJSONArray("headers"), "Cookie") + csvDelimiter);
			csvLineBuffer.append(processKey(requestObject.getJSONArray("headers"), "Connection") + csvDelimiter);
			csvLineBuffer.append(processKey(requestObject.getJSONArray("headers"), "Cache-Control") + csvDelimiter);
			csvLineBuffer.append(processKey(responseObject.getJSONArray("headers"), "httpVersion") + csvDelimiter);
			csvLineBuffer.append(processKey(responseObject.getJSONArray("headers"), "Set-Cookie") + csvDelimiter);
			csvLineBuffer.append(processKey(responseObject.getJSONArray("headers"), "Content-Language") + csvDelimiter);
			csvLineBuffer.append(processKey(responseObject.getJSONArray("headers"), "Keep-Alive") + csvDelimiter);
			csvLineBuffer.append(processKey(responseObject.getJSONArray("headers"), "Connection") + csvDelimiter);
		} else if(filter.equals("domains")) {
			csvLineBuffer.append(processKey(requestObject.getJSONArray("headers"), "Host") + csvDelimiter);
		}

		return csvLineBuffer.toString();
	}


	public String truncate(String s){
		if(s.length()>50){
			return s.substring(0,50)+" (trunc.)"; //truncated
		} else {
			return s;
		}
	}

	/**
	 * Process key and return value as String
	 * (empty String, if not applicable or null)
	 *
	 * @param object
	 * 		JSONObject
	 * @param key
	 * 		Name of the key
	 * @return
	 * 		Value of the key, or an empty String
	 * @throws JSONException
	 */
	private String processKey(JSONObject object, String key)
			throws JSONException {
		String returnValue = new String();

		if(object.has(key)) {
			if(!object.isNull(key)) {
				Object value = object.get(key);
				if(String.class.isInstance(value)) {
					if(value != null) {
						returnValue = (String)value;
					}
				} else if(Integer.class.isInstance(value)) {
					if((Integer)value >= 0) {
						returnValue = String.valueOf(value);
					}
				} else if(Double.class.isInstance(value)) {
					if((Double)value >= 0) {
						returnValue = String.valueOf(value);
					}
				}
			}
		}

		return returnValue;
	}

	/**
	 * Process key from JSONArray, and return value as String
	 * @param jsonArray
	 * 		JSONArray
	 * @param key
	 * 		Name of the JSONObject whose value we want to retrieve
	 * @return
	 * 		Value as String, or an empty string if the
	 * 		key was not found
	 * @throws JSONException
	 */
	private String processKey(JSONArray jsonArray, String key)
			throws JSONException {
		String returnValue = new String();

		for(Object entry : jsonArray.toArray()) {
			if(((JSONObject)entry).getString("name").equalsIgnoreCase(key)) {
				returnValue = processKey((JSONObject)entry, "value");
				break;
			}
		}

		return returnValue;
	}

	/**
	 * Set CSV delimiter
	 *
	 * @param delimiter
	 */
	public void setDelimiter(Character delimiter) {
		if(delimiter != null)
			csvDelimiter = delimiter;
	}
}
