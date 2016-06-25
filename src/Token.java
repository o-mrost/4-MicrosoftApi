import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Token {
	
	public Token(){
	}

	private String token = null;

	public String getApiToken(String filename) {
		if (token == null) {
			token = loadFromFile(filename);
		}
		return token;
	}

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