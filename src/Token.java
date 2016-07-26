import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Token {
	/**
	 * No arg Constructor
	 */
	public Token(){
	}

	private String token = null;

	/**
	 * get filname passed
	 * if variable where token is stored is null execute loadFromFile() and pass filename
	 * if not null return token
	 * @param filename
	 * @return
	 */
	public String getApiToken(String filename) {
		if (token == null) {
			token = loadFromFile(filename);
		}
		return token;
	}

	/**get filename passed
	 * read token from file and return it
	 * @param nameOfFile
	 * @return
	 */
	private String loadFromFile(String nameOfFile) {

		BufferedReader br = null;
		String apiToken = null;
		try {
			br = new BufferedReader(new FileReader(nameOfFile));
			apiToken = br.readLine();

		} catch (FileNotFoundException e) {
			apiToken = null;
		} catch (IOException e) {
			apiToken = null;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return apiToken;
	}
}