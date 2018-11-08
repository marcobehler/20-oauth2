package com.marcobehler.oauthdemo;

import com.squareup.moshi.Moshi;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import com.stripe.net.RequestOptions;
import okhttp3.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Thanks for watching this episode! Send any feedback to info@marcobehler.com!
 */
@Controller
public class OAuthController {

    private final OkHttpClient client = new OkHttpClient();

    private String stripeUserId;
    private String refreshToken;
    private String accessToken;

    @GetMapping("/connect")
    public String stripeConnect(@RequestParam Map<String, String> params) throws IOException {

        // 1st authorization code   TODO exercise: what happens here if error??
        String authorizationCode = params.get("code");

        // 2nd step: authorization code -> token
        RequestBody formBody = new FormBody.Builder()
                .add("client_secret", "sk_test_akKkQlPSG44mzpimrIpTR1Af")
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

            stripeUserId = (String) map.get("stripe_user_id");
            accessToken = (String) map.get("access_token");
            refreshToken = (String) map.get("refresh_token");
            System.out.println("stripeUserId = " + stripeUserId);
            System.out.println("accessToken = " + accessToken);
            System.out.println("refreshToken = " + refreshToken);
            // add that id to a database...connect it with the currently logged on user
        }

        return "redirect:/?success=true";
    }


    @PostMapping("/refresh")
    public String refresh() throws IOException {

        ;
        // 2nd step: authorization code -> token
        RequestBody formBody = new FormBody.Builder()
                .add("client_secret", "sk_test_akKkQlPSG44mzpimrIpTR1Af")
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
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

            stripeUserId = (String) map.get("stripe_user_id");
            accessToken = (String) map.get("access_token");
            refreshToken = (String) map.get("refresh_token");
            System.out.println("stripeUserId = " + stripeUserId);
            System.out.println("accessToken = " + accessToken);
            System.out.println("refreshToken = " + refreshToken);
            // add that id to a database...connect it with the currently logged on user
        }

        return "redirect:/";
    }

    @GetMapping("/transactions")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<String> transactions() throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Stripe.apiKey = accessToken;

        Map<String, Object> params = new HashMap<>();
        params.put("limit", 3);

        ChargeCollection chargesCollection = Charge.list(params);
        List<Charge> charges = chargesCollection.getData();

        return charges.stream().map(charge -> new Date(charge.getCreated() * 1000).toString()
                + " | " + charge.getDescription() + " | " + NumberFormat.getCurrencyInstance().format(charge.getAmount() / 100.00))
                .collect(Collectors.toList());
    }
}
