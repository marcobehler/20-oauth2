package com.marcobehler.oauthdemo;

import com.squareup.moshi.Moshi;
import okhttp3.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

/**
 * Thanks for watching this episode! Send any feedback to info@marcobehler.com!
 */
@Controller
public class OAuthController {

    private final OkHttpClient client = new OkHttpClient();

    @GetMapping("/connect")
    public String stripeConnect(@RequestParam Map<String, String> params) throws IOException {

        // 1st authorization code   TODO exercise: what happens here if error??
        String authorizationCode = params.get("code");

        // 2nd step: authorization code -> token
        RequestBody formBody = new FormBody.Builder()
                .add("client_secret", "sk_test_pwd6RBvQAOgyY7CGcOfVUElF")
                .add("code", authorizationCode)
                .add("grant_type", "authorization_code")
                .build();
        Request request = new Request.Builder()
                .url("https://connect.stripe.com/oauth/token")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsonResponse = response.body().string();
            Moshi moshi = new Moshi.Builder().build();
            Map map = moshi.adapter(Map.class).fromJson(jsonResponse);

            String stripeUserId = (String) map.get("stripe_user_id");
            System.out.println("stripeUserId = " + stripeUserId);
            // add that id to a database...connect it with the currently logged on user
        }

        return "redirect:/?success=true";
    }
}
