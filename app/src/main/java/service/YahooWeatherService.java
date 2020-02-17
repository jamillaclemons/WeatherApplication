package service;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import data.Channel;

// Copyright 2019 Oath Inc. Licensed under the terms of the zLib license see https://opensource.org/licenses/Zlib for terms.


@SuppressWarnings("unused")
public class YahooWeatherService {
    private final WeatherServiceCallback callback;
    private String location;
    private Exception error;

    public YahooWeatherService(WeatherServiceCallback callback) {
        this.callback = callback;
    }

    public String getLocation() {
        return location;
    }

    @SuppressLint("StaticFieldLeak")
    public void refreshWeather(String l) {
        //noinspection SillyAssignment
        this.location = location;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {

                //   String YQL = format("https://www.yahoo.com/news/weather/united-states/texas/austin-2357536)", strings[0]);

             //   String endpoint = format("https://weather-ydn-yql.media.yahoo.com/forecastrss?%s&format=json", Uri.encode(YQL));

                /*

                  <pre>
                  % java --version
                  % java 11.0.1 2018-10-16 LTS

                  % javac WeatherYdnJava.java && java -ea WeatherYdnJava
                  </pre>

                 */
                class WeatherYdnJava {
                    public static void main(String[] args) throws Exception {

                        final String appId = "hSQmAa54";
                        final String consumerKey = "dj0yJmk9dUtLS3NkMWd4WndiJmQ9WVdrOWFGTlJiVUZoTlRRbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTI4";
                        final String consumerSecret = "01707a6914bdfaca0adb32f4bce73505520ea595";
                        final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";

                        long timestamp = new Date().getTime() / 1000;
                        byte[] nonce = new byte[32];
                        Random rand = new Random();
                        rand.nextBytes(nonce);
                        String oauthNonce = new String(nonce).replaceAll("\\W", "");

                        List<String> parameters = new ArrayList<>();
                        parameters.add("oauth_consumer_key=" + consumerKey);
                        parameters.add("oauth_nonce=" + oauthNonce);
                        parameters.add("oauth_signature_method=HMAC-SHA1");
                        parameters.add("oauth_timestamp=" + timestamp);
                        parameters.add("oauth_version=1.0");
                        // Make sure value is encoded
                        parameters.add("location=" + URLEncoder.encode("sunnyvale,ca", "UTF-8"));
                        parameters.add("format=json");
                        Collections.sort(parameters);

                        StringBuilder parametersList = new StringBuilder();
                        for (int i = 0; i < parameters.size(); i++) {
                            parametersList.append((i > 0) ? "&" : "").append(parameters.get(i));
                        }

                        String signatureString = "GET&" +
                                URLEncoder.encode(url, "UTF-8") + "&" +
                                URLEncoder.encode(parametersList.toString(), "UTF-8");

                        String signature = null;
                        try {
                            SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
                            Mac mac = Mac.getInstance("HmacSHA1");
                            mac.init(signingKey);
                            byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
                            Encoder encoder = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                encoder = Base64.getEncoder();
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                signature = encoder.encodeToString(rawHMAC);
                            }
                        } catch (Exception e) {
                            System.err.println("Unable to append signature");
                            System.exit(0);
                        }

                        String authorizationLine = "OAuth " +
                                "oauth_consumer_key=\"" + consumerKey + "\", " +
                                "oauth_nonce=\"" + oauthNonce + "\", " +
                                "oauth_timestamp=\"" + timestamp + "\", " +
                                "oauth_signature_method=\"HMAC-SHA1\", " +
                                "oauth_signature=\"" + signature + "\", " +
                                "oauth_version=\"1.0\"";

                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url + "?location=sunnyvale,ca&format=json"))
                                .header("Authorization", authorizationLine)
                                .header("X-Yahoo-App-Id", appId)
                                .header("Content-Type", "application/json")
                                .build();

                        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                        System.out.println(response.body());
                    }
                }
                try {
                    URL url = new URL(endpoint);

                    URLConnection connection = url.openConnection();

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }
                    return result.toString();

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("http://www.mysite.se/index.asp?data=99");

                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        System.out.print(current);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            @Override
            protected void onPostExecute(String s) {

                if (s == null && error != null) {
                    callback.serviceFailure(error);
                    return;
                }

                try {
                    assert s != null;
                    JSONObject data = new JSONObject(s);

                    JSONObject queryResults = data.optJSONObject("query");

                    int count = Objects.requireNonNull(queryResults).optInt("count");
                    if(count == 0) {
                        callback.serviceFailure(new LocationWeatherException("No weather information found for " + location));
                        return;
                    }

                    Channel channel = new Channel();
                    channel.populate(Objects.requireNonNull(Objects.requireNonNull(queryResults.optJSONObject("results")).optJSONObject("channel")));

                    callback.serviceSuccess(channel);

                } catch (JSONException e) {
                    callback.serviceFailure(e);
                }
            }
        }.execute(location);

    }

    class LocationWeatherException extends Exception {
        LocationWeatherException(String message) {
            super(message);
        }
    }
}
