package hibskyi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRateLoader {

    public static Map<String, Double> loadRates(String currency) throws Exception {
        String url = "https://v6.exchangerate-api.com/v6/9191b39bd21f91d8fd4ad530/latest/"+currency;
        return getExchangeRates(url);
    }

    private static Map<String, Double> getExchangeRates(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == 404)
            throw new IllegalArgumentException("Unknown currency");
        if (response.getStatusLine().getStatusCode() != 200)
            throw new RuntimeException("Server error");

        String jsonResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonResponse);
        JsonNode ratesNode = jsonNode.get("conversion_rates");

        Map<String, Double> ratesMap = new HashMap<>();
        ratesNode.fields().forEachRemaining(entry -> ratesMap.put(entry.getKey(), entry.getValue().asDouble()));
        httpClient.close();

        return ratesMap;
    }
}
