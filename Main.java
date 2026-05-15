import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main{
    private static String json;
    public static void generatePayload() throws IOException{
        List<String> lines = Files.readAllLines(Paths.get("request.json"), Charset.forName("ISO-8859-1"));
        lines.forEach((l)->{
            Main.json += l;
        });
    }
    public static void main(String[] args) {
        json = new String();

        try {
            generatePayload();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://malware-analysis-lfckm.eastus.inference.ml.azure.com/score"))
                    .header("Content-Type", "application/json")
                    .header("Authorization","Bearer ")
                    .header("azureml-model-deployment", "mwa-1")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept((h)->{
                System.out.println(Integer.parseInt(h.body().split("[\\[\\]]")[1]) == 1? "Classification: Legitimate" : "Classification: Malware");
            }).join();

        } catch (Exception e) {
           System.err.println(e);
        }

    }
}