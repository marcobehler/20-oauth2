package com.marcobehler.oauthdemo;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Thanks for watching this episode! Send any feedback to info@marcobehler.com!
 */
public class ChargeDemo {

    public static void main(String[] args) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        Stripe.apiKey = "sk_test_Pr6bVfOcibyGLGbjoKYxbfx3";

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", 2000);
        chargeParams.put("currency", "eur");
        chargeParams.put("description", "Charge for huber@hansi.de");
        chargeParams.put("source", "tok_amex"); // ^ obtained with Stripe.js


        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount("acct_15fCszIkrDQBKQfO").build();

        Charge charge = Charge.create(chargeParams, requestOptions);
        System.out.println("charge.getStatus() = " + charge.getStatus());


    }
}
