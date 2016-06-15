import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpSearch {

	public String GetUrlContentAsString(String searchToken) {

		try {

			String url = "https://bingapis.azure-api.net/api/v5/images/search?q=cats&count=4&offset=0&mkt=en-us&safeSearch=Moderate";

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);

			request.setHeader("Ocp-Apim-Subscription-Key", searchToken);

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer body = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}
			System.out.println("searching");
			System.out.println(body);
			System.out.println("end of body");
			return body.toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block e.printStackTrace();
		}

		return null;

	}
}
