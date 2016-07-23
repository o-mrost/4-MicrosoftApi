import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class SearchForSimilarImages {

	public String GetUrlContentAsString(String searchToken, String tagsString, String imageTypeAsString,
			String sizeAsString, String licenseAsString, String safeSearchAsString) {

		StringBuffer body = null;
		String imageType = null, sizeType = null, licenseType = null, safeSearchType = null;
		try {

			if (!imageTypeAsString.equals("unspecified")) {
				imageType = "&imagetype=" + imageTypeAsString;
			} else {
				imageType = "";
			}

			if (!sizeAsString.equals("unspecified")) {
				sizeType = "&size=" + sizeAsString;
			} else {
				sizeType = "";
			}

			if (!licenseAsString.equals("unspecified")) {
				licenseType = "&license=" + licenseAsString;
			} else {
				licenseType = "";
			}

			if (!safeSearchAsString.equals("unspecified")) {
				safeSearchType = "&safesearch=" + safeSearchAsString;
			} else {
				safeSearchType = "";
			}

			String url = "https://bingapis.azure-api.net/api/v5/images/search?q=" + tagsString + "&count=10&mkt=en-us"
					+ imageType + sizeType + licenseType + safeSearchType;

			System.out.println("url to search " + url);

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

			// System.out.println(body);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body.toString();

	}
}