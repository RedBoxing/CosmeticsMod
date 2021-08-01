package fr.redboxing.cosmeticsmod.api.utils;

import com.google.gson.*;
import fr.redboxing.cosmeticsmod.CosmeticsMod;
import okhttp3.*;

import java.io.IOException;

public class RequestUtils {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static JsonElement getJSON(String url) throws IOException {
        JsonElement jsonElement = new JsonParser().parse(getRaw(url));
        return jsonElement.getAsJsonObject();
    }

    public static JsonElement postJSON(String url, JsonObject body) throws IOException {
        JsonElement jsonElement = new JsonParser().parse(postRaw(url, body));
        return jsonElement.getAsJsonObject();
    }

    public static <T> T get(String url, Class<T> classOfT) throws IOException {
        return GSON.fromJson(getRaw(url), classOfT);
    }

    public static <T> T post(String url, JsonObject body, Class<T> classOfT) throws IOException {
        return GSON.fromJson(postRaw(url, body), classOfT);
    }

    private static String getRaw(String url) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).get();

        if(CosmeticsMod.getApi().getToken() != null && !CosmeticsMod.getApi().getToken().isEmpty()) {
            builder.header("Authorization", "Bearer " + CosmeticsMod.getApi().getToken());
        }

        try (Response response = CLIENT.newCall(builder.build()).execute()) {
            return response.body().string();
        }
    }

    private static String postRaw(String url, JsonObject body) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).post(RequestBody.create(body.toString(), JSON));

        if(CosmeticsMod.getApi().getToken() != null && !CosmeticsMod.getApi().getToken().isEmpty()) {
            builder.header("Authorization", "Bearer " + CosmeticsMod.getApi().getToken());
        }

        try (Response response = CLIENT.newCall(builder.build()).execute()) {
            return response.body().string();
        }
    }
}
