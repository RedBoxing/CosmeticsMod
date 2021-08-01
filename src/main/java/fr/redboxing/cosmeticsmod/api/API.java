package fr.redboxing.cosmeticsmod.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.api.responses.LoginResponse;
import fr.redboxing.cosmeticsmod.api.responses.UserResponse;
import fr.redboxing.cosmeticsmod.api.utils.AES;
import fr.redboxing.cosmeticsmod.api.utils.RequestUtils;
import fr.redboxing.cosmeticsmod.user.CosmeticPack;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class API {
    public static final File TOKEN_FILE = new File("cosmeticsmod", "auth.encrypted.json");

    public static final String LOGIN_URL = "http://localhost:5000/auth/login";
    public static final String REGISTER_URL = "http://localhost:5000/auth/register";
    public static final String GET_USER_URL = "http://localhost:5000/users/";
    public static final String UPDATE_USER_URL = "http://localhost:5000/users/";
    public static final String UPLOAD_PACK_URL = "http://localhost:5000/packs";
    public static final String DOWNLOAD_PACK_URL = "http://localhost:5000/packs";

    private String API_TOKEN;

    public API() {
        readToken();
    }

    public LoginResponse login(String username, String password) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);

        LoginResponse response = RequestUtils.post(LOGIN_URL, body, LoginResponse.class);
        if(response.success && response.token != null) {
            setToken(response.token);
            saveToken();
        }

        return response;
    }

    public LoginResponse login() throws IOException {
        LoginResponse response = RequestUtils.post(LOGIN_URL, new JsonObject(), LoginResponse.class);
        if(!response.success) {
            setToken("");
            saveToken();
        }

        return response;
    }

    public void logout() {
        setToken("");
        saveToken();
    }

    public LoginResponse register(String username, String password, UUID uuid) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);
        body.addProperty("uuid", uuid.toString());

        LoginResponse response = RequestUtils.post(REGISTER_URL, body, LoginResponse.class);
        if(response.success && response.token != null) {
            setToken(response.token);
            saveToken();
        }

        return response;
    }

    public UserResponse getUser(UUID uuid) throws IOException {
        return RequestUtils.get(GET_USER_URL + uuid.toString(), UserResponse.class);
    }

    public void downloadPack(CosmeticPack pack) throws IOException {
        String url = DOWNLOAD_PACK_URL + pack.getId();
        File destination = new File("cosmetics",  DigestUtils.sha1Hex(url));
        if (destination.exists()) {
            InputStream is = Files.newInputStream(Paths.get(destination.toURI()));
            String hash = DigestUtils.sha1Hex(is);

            if (!hash.equals(pack.getHash())) {
                FileUtils.copyURLToFile(new URL(url), destination);
            }
        } else {
            FileUtils.copyURLToFile(new URL(url), destination);
        }
    }

    public String getToken() {
        return API_TOKEN;
    }

    public void setToken(String token) {
        API_TOKEN = token;
    }

    public void saveToken() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", Hex.encodeHexString(getToken().getBytes(StandardCharsets.UTF_8)));

        String json = jsonObject.toString();
        try {
            String encrypted = AES.encrypt(json);
            FileUtils.writeStringToFile(TOKEN_FILE, encrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readToken() {
        try {
            String encrypted = FileUtils.readFileToString(TOKEN_FILE, "UTF-8");
            String decrypted = AES.decrypt(encrypted);

            CosmeticsMod.LOGGER.info(decrypted);

            JsonObject jsonObject = new JsonParser().parse(decrypted).getAsJsonObject();
            setToken(new String(Hex.decodeHex(jsonObject.get("token").getAsString().toCharArray()), StandardCharsets.UTF_8));
            CosmeticsMod.LOGGER.info(getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
