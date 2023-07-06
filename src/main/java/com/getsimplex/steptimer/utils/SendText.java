//© 2021 Sean Murdock

package com.getsimplex.steptimer.utils;

import com.getsimplex.steptimer.model.TextMessage;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class SendText {
    private static String ACCOUNT_SID ="";
    private static String AUTH_TOKEN = "";
    private static String TWILIO_PHONE= "";

    // This is a slightly tweaked version of this program  that doesn't call Twilio Directly - so we don't need to give out Twilio Tokens

    static {

        ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
        AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
        TWILIO_PHONE = System.getenv("TWILIO_PHONE");
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void send(String destinationPhone, String text) throws Exception{

        Gson gson = new Gson();
        String formattedPhone = getFormattedPhone(destinationPhone);
        PhoneNumber destination = new PhoneNumber(formattedPhone);
        PhoneNumber origin = new PhoneNumber(TWILIO_PHONE);

        Message message =Message.creator(destination, origin, text).create();
TextMessage textMessage = new TextMessage();
        textMessage.setMessage(text);
        textMessage.setPhoneNumber(formattedPhone);

//        HttpClient httpClient = new HttpClient();
//
//        Request request = httpClient.POST("https://dev.stedi.me/sendtext");//this url only allows a user to text themselves using their own token
//        request.header(HttpHeader.ACCEPT,"application/json");
//        request.header(HttpHeader.CONTENT_TYPE,"application/json");
//        request.header("suresteps.session.token",AUTH_TOKEN);
//

//
//        request.content(new StringContentProvider(gson.toJson(textMessage)),"application/json");
//        request.send();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://dev.stedi.me/sendtext"))
                .headers("Content-Type", "application/json;charset=UTF-8")
                .headers("suresteps.session.token",AUTH_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(textMessage)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("**** Sent Text: ID "+message.getSid());
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