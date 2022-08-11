package store;

import com.google.gson.Gson;
import lombok.Data;
import pet.petEntity.Response;
import store.storeEntity.Inventory;
import store.storeEntity.Order;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

@Data
public class StoreHttpService {
    public static final String URL = "https://petstore.swagger.io/";
    public static final Gson GSON = new Gson();
    public static final HttpClient CLIENT = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private int responseStatus;

    public Order placeOrder(Order order) throws IOException, InterruptedException {
        final String requestBody = GSON.toJson(order);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "v2/store/order"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        return GSON.fromJson(response.body(),Order.class);
    }

    public Order getOrderById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(URL + "v2/store/order/" + id))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        setResponseStatus(response.statusCode());
        if (getResponseStatus() != 200) {
            System.out.println("Order not found");
            return null;
        }
        return GSON.fromJson(response.body(), Order.class);
    }

    public Inventory getInventory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(URL + "v2/store/inventory"))
                .setHeader("accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        setResponseStatus(response.statusCode());

        return GSON.fromJson(response.body(), Inventory.class);
    }

    public Response delete(int id) throws IOException, InterruptedException {
        if(getOrderById(id)==null){
            System.out.println("Order with id "+ id + " not found");
        }
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(URL + "v2/store/order/" + id))
                .setHeader("accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        setResponseStatus(response.statusCode());
        return GSON.fromJson(response.body(), Response.class);

    }
}

