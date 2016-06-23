import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class HttpDescribeLocal {

	public String describeImageFromFilechooser(BufferedImage image, String token) {

		HttpClient httpclient = HttpClients.createDefault();
//        final String imageFileName = "4015623633_d2286141fa_o.jpg";
//        File file = new File(imageFileName);
		HttpEntity entity;
		String answer = null;
		
        try
        {
            URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/vision/v1.0/describe");

            builder.setParameter("maxCandidates", "1");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", token);

           
            
//            //**** HTTP Body form BufferedImage
//            //1
//            BufferedImage img = null;
//            try {
//                img = ImageIO.read(file);
//            } catch (IOException e) {
//            }
            
            //2
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            
            //3
            InputStream is = new ByteArrayInputStream(os.toByteArray());
           
            //4
            InputStreamEntity inputStreamEntity = new InputStreamEntity(is); 
            request.setEntity(inputStreamEntity);               
            //**** END HTTP Body form BufferedImage
            
            HttpResponse response = httpclient.execute(request);
            entity = response.getEntity();
             
            answer = EntityUtils.toString(entity);
			System.out.println("image uploaded");
            
			
//			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//			body = new StringBuffer();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				body.append(line);
//			}
			
			
//			if (entity != null) 
//            {
//                System.out.println(EntityUtils.toString(entity));
//            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
		
		return answer ;
	}

}
