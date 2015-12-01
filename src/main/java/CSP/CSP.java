package CSP;

import Utilities.ChatClientEndpoint;
import Utilities.Paillier;
import util.*;

import org.apache.commons.cli.ParseException;

import flexsc.Mode;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.umanitoba.smc_genome_clients.Hospitals.Hospitals;

public abstract class CSP<T> {

    final static int epoch = 4 * 1000;
    Mode m;
    static int timesSending = 0;
    static String tmpDistances = "";

    //@SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        try {
            final ConfigParser config = new ConfigParser("Config.conf");
//        System.out.println("host "+config.getString("Host"));
            String destUri = "ws://" + config.getString("Host") + ":" + config.getString("SocketPort") + "/" + config.getString("SocketEndpoint");

//        System.out.println("Endpoint " + destUri);
            final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI(destUri));
            clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    System.out.println("Message from server " + message);
                    JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
                    System.out.println("type " + jsonObject.getString("type"));
                    if (jsonObject.getString("type").equals("decryption")) {

                        try {
                            String[] args = new String[2];
                            args[0] = "example.DecryptHE";
                            args[1] = "0";
//                        args[2] = args[1].length() + "";
                            EvaRunnable.runDecryption(args);
                            System.out.println(jsonObject.getString("queryID"));
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ParseException ex) {
                            Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
                        }
//                    JsonObject msg = Json.createReader(new StringReader(jsonObject.getString("msg"))).readObject();
//                    System.out.println("Operation " + msg.toString());
//                    if (msg.getString("operation").equals("decryption")) {
//
//                    }

//                    JsonObject msg = Json.createReader(new StringReader(jsonObject.getString("msg"))).readObject();
//                    System.out.println("Operation " + msg.toString());
//                    if (msg.getString("operation").equals("decryption")) {
//                        
//                    }
                    } else if (jsonObject.getString("type").equals("editdist")) {
                        tmpDistances += jsonObject.getString("distances") + ",";
                        if (jsonObject.getInt("times") == jsonObject.getInt("total")) {

                            JsonObject query = jsonObject.getJsonObject("query");
                            int count = Integer.parseInt(query.getJsonObject("msg").getString("count", "10"));
                            if (tmpDistances.equals("")) {
                                tmpDistances = jsonObject.getString("distances");
                            }

                            String[] distances = tmpDistances.split(",");
                            System.out.println("distance size " + distances.length);
                            Map<Integer, List<String>> numericDistances = new TreeMap<>();
                            Paillier paillier = new Paillier(true);
                            for (int i = 0; i < distances.length; i++) {
                                if (!"".equals(distances[i])) {
                                    int decrypted = Integer.valueOf(paillier.Decryption(new BigInteger(distances[i])).toString());
                                    if (numericDistances.get(decrypted) != null) {
                                        numericDistances.get(decrypted).add(distances[i]);
                                    } else {
                                        List<String> tmp = new ArrayList<>();
                                        tmp.add(distances[i]);
                                        numericDistances.put(decrypted, tmp);
                                    }
                                }
                            }
//                        System.out.println("count " + numericDistances.size());
                            String result = jsonObject.getInt("queryID") + ";";
                            List<Integer> keys = new ArrayList<>(numericDistances.keySet());
                            for (int i = 0; i < count; i++) {
                                for (String s : numericDistances.get(keys.get(i))) {
                                    result += s + ",";
                                }
                            }
                            System.out.println("result " + result);
                            System.out.println("edit distance sorted " + Utils.getMessage("resultEditDistanceCSP", result));
                            try {
                                clientEndPoint.sendMessage(Utils.getMessage("resultEditDistanceCSP", result));
                            } catch (IOException ex) {
                                Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            tmpDistances = "";
//                    System.out.println("found " + jsonArray.getString(1));
//                    System.out.println("distances " + jsonObject.getJsonArray("distances").toString());
//                    JsonObject distances = Json.createReader(new StringReader(jsonObject.getJsonArray("distances").toString())).readObject();
//                    System.out.println("d "+distances.toString());
                        }
                    }
                }
            });

            try {
                clientEndPoint.sendMessage(Utils.getMessage("ic", Inet4Address.getLocalHost().getHostAddress()));
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            clientEndPoint.sendMessage(Utils.getMessage("ping", ""));
                            Thread.sleep(epoch);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Hospitals.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        } catch (Exception ex) {
                            Logger.getLogger(Hospitals.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }).start();
        } catch (Exception ex) {
            System.out.println("exception " + ex.getMessage());

        }
    }
}
