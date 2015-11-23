import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.wink.json4j.JSONException;
import java.util.HashMap;
import java.nio.charset.UnsupportedCharsetException;

public class har2csv {

	public static void main(String[] args) {
		HashMap<Character, String> argsTrans = new HashMap<Character, String>();
		argsTrans.put('d', "delimiter");
		argsTrans.put('e', "encoding");
		argsTrans.put('h', "help");
		argsTrans.put('i', "in");
		argsTrans.put('o', "out");
		argsTrans.put('v', "version");
		argsTrans.put('f', "filter");

		CliArgs cliArgs = new CliArgs(args, argsTrans);

		if(cliArgs.hasArg("help")) {
			System.out.println("Usage: har2csv options\n"
				+ "where options include:\n"
			 	+ "    -d, --delimiter <char>    CSV column delimiter\n"
				+ "                              (default delimiter is \\t)\n"
				+ "    -e, --encoding <name>     Input HAR file encoding\n"
				+ "                              (default encoding is UTF-8)\n"
				+ "    -h, --help                This help\n"
				+ "    -i, --in <filename>       Source HAR filename\n"
				+ "    -o, --out <filename>      Destination CSV filename\n"
				+ "                              (if omitted, outputs to stdout)\n"
				+ "    -v, --version             Version number\n"
				+ "	   -f, --filter              Filter output (all, domains)\n");
		} else if(cliArgs.hasArg("version")) {
			System.out.println("har2csv $build:20140311_2332$\n"
				+ "HTTP Archive(har) to CSV converter\n"
				+ "http://www.yamamoto.com.ar/hartools\n");
		} else if(cliArgs.hasArg("in") && String.class.isInstance(cliArgs.getValue("in"))) {
			HarFile harFile;

			try {
				// Get encoding from arguments, if exists, and call the appropiate constructor
				if(cliArgs.hasArg("encoding") && String.class.isInstance(cliArgs.getValue("encoding"))) {
					harFile = new HarFile((String)cliArgs.getValue("in"), (String)cliArgs.getValue("encoding"));
				} else {
					harFile = new HarFile((String)cliArgs.getValue("in"));
				}

				// Get delimiter from arguments, if exists
				if(cliArgs.hasArg("delimiter") && String.class.isInstance(cliArgs.getValue("delimiter"))) {
					harFile.setDelimiter(((String)cliArgs.getValue("delimiter")).charAt(0));
				}

				// The actual conversion
				String csvString;
				if(cliArgs.hasArg("filter") && String.class.isInstance(cliArgs.getValue("filter"))) {
					csvString = harFile.entriesToCsv((String)cliArgs.getValue("filter"));
				} else {
					csvString = harFile.entriesToCsv("all");
				}


				// Get destination filename and save content, or print to stdout
				if(cliArgs.hasArg("out") && String.class.isInstance(cliArgs.getValue("out"))) {
					TextFile csvFile = new TextFile((String)cliArgs.getValue("out"), "utf-8", true);
					csvFile.writeToFile(csvString);
				} else {
					System.out.print(csvString);
				}
			} catch(FileNotFoundException Ex) {
				System.out.println("[ERROR] File not found (\""
					+ (String)cliArgs.getValue("in") + "\")\n");
			} catch(UnsupportedCharsetException Ex) {
				System.out.println("[ERROR] Unknown encoding (\""
					+ (String)cliArgs.getValue("encoding") + "\")\n");
			} catch(IOException Ex) {
				System.out.println("[ERROR] " + Ex.toString() + "\n");
			} catch(JSONException Ex) {
				System.out.println("[ERROR] Malformed HAR file\n");
			} catch(Exception Ex) {
				Ex.printStackTrace();
			}
		} else {
			System.out.println("Enjoying my time, nothing to do... :)\n");
		}
	}
}
