//© 2021 Sean Murdock

package com.getsimplex.steptimer.utils;

import com.getsimplex.steptimer.model.TextMessage;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class SendText {
    private static String AUTH_TOKEN = "";

    // This is a slightly tweaked version of this program  that doesn't call Twilio Directly - so we don't need to give out Twilio Tokens

    static {

        AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    }

    public static void send(String destinationPhone, String text) throws Exception{

        Gson gson = new Gson();
        String formattedPhone = getFormattedPhone(destinationPhone);

        TextMessage textMessage = new TextMessage();
        textMessage.setMessage(text);
        textMessage.setPhoneNumber(formattedPhone);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://dev.stedi.me/sendtext"))
                .headers("Content-Type", "application/json;charset=UTF-8")
                .headers("suresteps.session.token",AUTH_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(textMessage)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Send Text response: "+response.statusCode());

    }

    public static String getFormattedPhone(String inputPhone) throws Exception{
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(inputPhone, "US");
        String formattedPhone = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        formattedPhone = formattedPhone.replace(" ","");
        return formattedPhone;
    }

}