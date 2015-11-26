/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.umanitoba.smc_genome_clients.Hospitals;

import Utilities.ChatClientEndpoint;
import Functions.CountQuery;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import util.Utils;

/**
 *
 * @author shad942
 */
public class Hospitals {

    final static int epoch = 5 * 1000;
    static Map<String, JsonObject> editDistResult = new HashMap<>();

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        String destUri = "ws://130.179.30.133:8080/smc_genome/endpoint_smc_genome";
        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI(destUri));
        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println("Message from server " + message);
                JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
                if (jsonObject.getString("type").equals("q")) {
//                    String queryID = jsonObject.getString("queryID");
                    JsonObject msg = Json.createReader(new StringReader(jsonObject.getString("msg"))).readObject();
                    System.out.println("Operation " + msg.toString());
                    JsonObject ret = null;
                    switch (msg.getString("operation")) {
                        case "count":
                            ret = CountQuery.executeCount(msg, jsonObject.getString("queryID"));
                            clientEndPoint.sendMessage(ret.toString());
                            break;
                        case "editdist":
                            ret = CountQuery.executeCount(msg, jsonObject.getString("queryID"));
                            editDistResult.put(jsonObject.getString("queryID"), ret);
                            clientEndPoint.sendMessage(ret.toString());
                            break;
                    }
                }
            }
        });
        try {
            clientEndPoint.sendMessage(Utils.getMessage("ih", Inet4Address.getLocalHost().getHostAddress()));
        } catch (UnknownHostException ex) {

        }
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

    }

    /**
     * Create a json representation.
     *
     * @param message
     * @return
     */
}
