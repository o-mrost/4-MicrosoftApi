import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class AnalyseImage {

	public String describeImage(BufferedImage image, String token) {

		HttpClient httpclient = HttpClients.createDefault();
		HttpEntity entity;
		String answer = null;

		try {
			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/vision/v1.0/describe");

			builder.setParameter("maxCandidates", "1");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", token);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", os);

			InputStream is = new ByteArrayInputStream(os.toByteArray());

			InputStreamEntity inputStreamEntity = new InputStreamEntity(is);
			request.setEntity(inputStreamEntity);

			HttpResponse response = httpclient.execute(request);
			entity = response.getEntity();

			answer = EntityUtils.toString(entity);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return answer;
	}
}
