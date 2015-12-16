/*
 * Md. Momin Al Aziz momin.aziz.cse @ gmail.com	
 * http://www.mominalaziz.com
 */
package Experiments;

import CSP.CSP;
import Database.QueryDB;
import Utilities.ChatClientEndpoint;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import util.ConfigParser;
import java.util.Date;

/**
 *
 * @author shad942
 */
public class RunTest {

    static int iterations = 20;

    public static JsonObject getTestCase() {
        QueryDB queryDB = new QueryDB();
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("type", "q");
        JsonObjectBuilder msg = Json.createObjectBuilder();
        msg.add("operation", "count");
        msg.add("count", "10");
        msg.add("secure", "1");
        msg.add("text", "CC");
        msg.add("snip", queryDB.getRandomSnip().get(0).getSnip());
        msg.add("type", "all");
        jsonObjectBuilder.add("msg", msg.build());
        return jsonObjectBuilder.build();
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        final ConfigParser config = new ConfigParser("Config.conf");
        String destUri = "ws://" + config.getString("Host") + ":" + config.getString("SocketPort") + "/" + config.getString("SocketEndpoint");
        Date d1 = new Date();

//        System.out.println("Endpoint " + destUri);
        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI(destUri));
        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
            @Override
            public void handleMessage(String message) {
//                Map<String,String> test = new HashMap<>();
//                System.out.println("Message from server " + message);
                JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
                if (jsonObject.getString("type").equals("result")) {
                    System.out.println(iterations+" Message from server " + message);
                    iterations--;
                    try {
                        clientEndPoint.sendMessage(getTestCase().toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });

        try {
            clientEndPoint.sendMessage(getTestCase().toString());

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (iterations > -1) {
//            System.out.println("iteration "+iterations);
            Thread.sleep(500);
        }
        Date d2 = new Date();
        System.out.println("Running time " + (double)((d2.getTime() - d1.getTime())/1000 ));
    }
}
