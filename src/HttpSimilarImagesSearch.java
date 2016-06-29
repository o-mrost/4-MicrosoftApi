import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpSimilarImagesSearch {

	public String GetUrlContentAsString(String searchToken, String tagsString) {

		StringBuffer body = null;

		try {

			// here comes null pointer exception
			// why the hell?

			// String license = "&license=public";
			String license = "";

			// or we can leave the license and use just image size to filter

			String url = "https://bingapis.azure-api.net/api/v5/images/search?q=" + tagsString + "&count=4&mkt=en-us"
					+ license;

			System.out.println("url to search: " + url);

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			request.setHeader("Ocp-Apim-Subscription-Key", searchToken);

			HttpResponse response = client.execute(request);

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			body = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}

//			System.out.println(body);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body.toString();

	}
}
