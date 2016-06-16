import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SearchExample 
{
    public static void main(String[] args) 
    {
        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://bingapis.azure-api.net/api/v5/images/search");

            builder.setParameter("q", "cats");
            builder.setParameter("count", "10");
            builder.setParameter("offset", "0");
            builder.setParameter("mkt", "en-us");
            builder.setParameter("safeSearch", "Moderate");

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", "08b669df660c40ed8135ac6403d859be");

            // what is body here?
            // and why it wants to cast setEntity?
            // Request body
//            StringEntity reqEntity = new StringEntity("{body}");
//            request.setEntity(reqEntity);
//
//            HttpResponse response = httpclient.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) 
//            {
//                System.out.println(EntityUtils.toString(entity));
//            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}