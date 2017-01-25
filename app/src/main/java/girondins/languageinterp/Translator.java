package girondins.languageinterp;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Girondins on 28/10/15.
 */
public class Translator{
    private ExecuteThread thread;
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    String TAG = ((Object) this).getClass().getSimpleName();
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String headerValue;
    private boolean hasHeader = false;
    private String translatedText;
    private Controller cont;


    public Translator(Controller cont){
        thread = new ExecuteThread();
        thread.start();
        Timer time = new Timer();
        time.schedule(new RenewToken(), 0, 540000);
        this.cont = cont;

    }
    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {
        try {
            // http client
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 2000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 2000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params

                if(hasHeader == true){
                    httpPost.setHeader("Authorization", headerValue);
                    hasHeader = false;
                    Log.d("Authen",headerValue);
                }
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                Log.e("Request: ", "> " + url);
                HttpGet httpGet = new HttpGet(url);
                if(hasHeader == true){
                    httpGet.setHeader("Authorization", headerValue);
                    hasHeader = false;
                    Log.d("Authen",headerValue);
                }

                httpResponse = httpClient.execute(httpGet);

            }
            if (httpResponse != null) {
                httpEntity = httpResponse.getEntity();
            } else {
                Log.e(TAG, "httpResponse is null");
            }
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

    public void extractJsonObject(String access){
        try {
            accessToken = (String)((JSONObject) JSONValue.parse(access)).get("access_token");
            tokenType = (String) ((JSONObject) JSONValue.parse(access)).get("token_type");
            expiresIn = Long.parseLong((String)((JSONObject) JSONValue.parse(access)).get("expires_in"));
            Log.d("extracting","Access " + accessToken);
            Log.d("extracting","Type " + tokenType);
            Log.d("extracting","Expires " + expiresIn);
            headerValue = "Bearer " + accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void authorize(){
        thread.execute(new Authenticate());
    }

    public void translate(Language nativeLang, Language foreignLang, String text,String fragment){
        InitiateTranslate init = new InitiateTranslate(nativeLang,foreignLang,text,fragment);
        thread.execute(init);

    }


    private class Authenticate implements Runnable{

        @Override
        public void run() {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("client_id","talk_to_talk-language_translator"));
            nameValuePairs.add(new BasicNameValuePair("client_secret","WHzMGWHLEN9AfwksHiWQRdmo+Laz8NbdkBzHtBCWt1w="));
            nameValuePairs.add(new BasicNameValuePair("scope", "http://api.microsofttranslator.com"));
            nameValuePairs.add(new BasicNameValuePair("grant_type", "client_credentials"));
            String res = makeServiceCall("https://datamarket.accesscontrol.windows.net/v2/OAuth2-13",POST,nameValuePairs);
            Log.d("Translator", res.toString());
            extractJsonObject(res);
        }
    }

    private class InitiateTranslate implements Runnable{
        private Language nativeLang;
        private Language foreignLang;
        private String text;
        private String fragment;
        private static final String SERVICE_URL = "http://api.microsofttranslator.com/V2/Http.svc/Translate";

        public InitiateTranslate(Language nativeLang, Language foreignLang, String text,String fragment){
            this.nativeLang = nativeLang;
            this.foreignLang = foreignLang;
            this.text = text;
            this.fragment = fragment;
            hasHeader = true;
            Log.d("IntiateTrans", "Native " + nativeLang.getTransCD() + ", Foreign " + foreignLang.getTransCD() + " Text: " + text);
        }
        @Override
        public void run() {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("text",text));
            nameValuePairs.add(new BasicNameValuePair("from",nativeLang.getTransCD()));
            nameValuePairs.add(new BasicNameValuePair("to",foreignLang.getTransCD()));
            String res = makeServiceCall(SERVICE_URL,GET,nameValuePairs);
            Log.d("Translator", res.toString());
            readTranslation(res);
            cont.viewTranslation(translatedText,fragment);
        }
    }

    private class RenewToken extends TimerTask{

        @Override
        public void run() {
            authorize();
            Log.d("Timer","Token Renewed");
        }
    }

    public void readTranslation(String res) {
        try {
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(res));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("string");
            Element line = (Element) nodes.item(0);
            translatedText = getCharacterDataFromElement(line);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public static String getCharacterDataFromElement(Element e) {
            Node child = e.getFirstChild();
            if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                return cd.getData();
            }
            return "?";
        }

    }




