import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class SimilarImagesSearch {

	public static void main(String[] args) {

		try {

			String url = "https://bingapis.azure-api.net/api/v5/images/search?q=sailing+dinghies&mkt=en-us";

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			request.setHeader("Ocp-Apim-Subscription-Key", "08b669df660c40ed8135ac6403d859be");

			HttpResponse response = client.execute(request);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer body = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}
			System.out.println(body);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
