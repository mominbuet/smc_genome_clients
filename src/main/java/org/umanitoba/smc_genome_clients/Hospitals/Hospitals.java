/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.umanitoba.smc_genome_clients.Hospitals;

import Utilities.ChatClientEndpoint;
import Functions.CountQuery;
import Functions.EditDistance;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import util.Utils;

/**
 *
 * @author shad942
 */
public class Hospitals {

    final static int epoch = 4 * 1000;
    static int serverNo = 0;
    static Map<BigInteger, List<String>> editDistResult = new HashMap<>();

    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        String destUri = "ws://130.179.30.133:8080/smc_genome/endpoint_smc_genome";
        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI(destUri));
        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println("Message from server " + message);
                
                JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
                
                switch (jsonObject.getString("type")) {
                    case "wl":
                        serverNo =Integer.parseInt( jsonObject.getString("msg"));
                        break;
                    case "resultEditDistanceCSP":
                        String distances[] = jsonObject.getString("msg").split(",");
//                        System.out.println(distances[0]);
                        JsonObjectBuilder jsonObjectBuilder_dist = Json.createObjectBuilder();
                        boolean flagToSend = false;
                        for (String s : distances) {
                            if (!"".equals(s)) {
                                if (editDistResult.get(new BigInteger(s)) != null) {
                                    flagToSend = true;
                                    System.out.println(editDistResult.get(new BigInteger(s)));
                                    jsonObjectBuilder_dist.add(s, editDistResult.get(new BigInteger(s)).toString());
                                }
                            }
                        }
                        if (flagToSend) {
                            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                            jsonObjectBuilder.add("type", "resultEditDistanceHospital")
                                    .add("queryID", jsonObject.getString("queryID"))
                                    .add("result", jsonObjectBuilder_dist.build().toString());
                            try {
                                clientEndPoint.sendMessage(jsonObjectBuilder.build().toString());
                            } catch (IOException ex) {
                                Logger.getLogger(Hospitals.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    case "q":
//                    String queryID = jsonObject.getString("queryID");
                        JsonObject msg = Json.createReader(new StringReader(jsonObject.getString("msg"))).readObject();
                        System.out.println("Operation " + msg.toString());
                        JsonObject ret = null;
                        switch (msg.getString("operation")) {
                            case "count":
                                ret = CountQuery.executeCount(msg, jsonObject.getString("queryID"));
                                 {
                                    try {
                                        clientEndPoint.sendMessage(ret.toString());
                                    } catch (IOException ex) {
                                        Logger.getLogger(Hospitals.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;

                            case "editdist":
//                            ret = new EditDistance().executeEditDistance(msg, jsonObject.getString("queryID"));
                                System.out.println("Executing " + msg.toString());
                                editDistResult = new EditDistance().executeEditDistance(msg, jsonObject.getString("queryID"));
                                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                                jsonObjectBuilder.add("type", "result");
                                jsonObjectBuilder.add("queryID", jsonObject.getString("queryID"));
                                String result = "";
                                for (BigInteger bigInteger : editDistResult.keySet()) {
                                    result += bigInteger + ",";
                                }
                                jsonObjectBuilder.add("result", result);
                                JsonObject jsonObject1 = jsonObjectBuilder.build();
                                System.out.println("result " + jsonObject1.toString());
//                            editDistResult.put(jsonObject.getString("queryID"), ret);
                                //only  encrypted value do here
                                 {
                                    try {
                                        clientEndPoint.sendMessage(jsonObject1.toString());
                                    } catch (IOException ex) {
                                        Logger.getLogger(Hospitals.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;
                        }
                        break;
                }
            }
        });
        try {
            clientEndPoint.sendMessage(Utils.getMessage("ih", Inet4Address.getLocalHost().getHostAddress()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            clientEndPoint.sendMessage(Utils.getMessage("ping", ""));
                            Thread.sleep(Hospitals.epoch);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Hospitals.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        } catch (Exception ex) {
                            Logger.getLogger(Hospitals.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }).start();
        } catch (UnknownHostException ex) {
            System.out.println("exception ");
        }

    }

    /**
     * Create a json representation.
     *
     * @param message
     * @return
     */
}
