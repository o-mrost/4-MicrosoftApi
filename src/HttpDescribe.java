
// // This sample uses the Apache HTTP client from HTTP Components (http://hc.apache.org/httpcomponents-client-ga/)
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpDescribe {

	public String describeImageFromLink(String url, String token) {

		HttpClient httpclient = HttpClients.createDefault();
		String reply = null;
		StringBuffer body = null;

		try {

			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/vision/v1.0/describe");

			builder.setParameter("maxCandidates", "1");

			builder.setParameter("visualFeatures", "Categories");
			builder.setParameter("details", "{string}");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", token);

			// Request body
			StringEntity reqEntity = new StringEntity(url);
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			body = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}

			// it was in the original code from Microsoft, if I let it, then I
			// receive attempted read from closed stream message
			// if (entity != null) {
			// System.out.println(EntityUtils.toString(entity));
			//
			// }
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return body.toString();
	}
}
