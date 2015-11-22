package CSP;

import Utilities.ChatClientEndpoint;
import util.*;

import org.apache.commons.cli.ParseException;

import flexsc.Mode;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import org.umanitoba.smc_genome_clients.Hospitals.Hospitals;

public abstract class CSP<T> {

    final static int epoch = 5 * 1000;

//    public abstract void prepareInput(CompEnv<T> gen) throws Exception;
//
//    public abstract void secureCompute(CompEnv<T> gen) throws Exception;
//
//    public abstract void prepareOutput(CompEnv<T> gen) throws Exception;
    Mode m;
//    int port;
//    String host;
//    protected String[] args;
//    public boolean verbose = true;
//    public ConfigParser config;
//
//    public void setParameter(ConfigParser config, String[] args) {
//        this.m = Mode.getMode(config.getString("Mode"));
//        this.port = config.getInt("Port");
//        host = config.getString("Host");
//        this.args = args;
//        this.config = config;
//    }
//
//    public void setParameter(Mode m, String host, int port) {
//        this.m = m;
//        this.port = port;
//        this.host = host;
//    }
//
//    public void run() {
//        try {
//            if (verbose) {
//                System.out.println("connecting EVA");
//            }
//            connect(host, port);
//            if (verbose) {
//                System.out.println("connected");
//            }
//
//            @SuppressWarnings("unchecked")
//            CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, this);
//
//            double s = System.nanoTime();
//            Flag.sw.startTotal();
//            prepareInput(env);
//            os.flush();
//            secureCompute(env);
//            os.flush();
//            prepareOutput(env);
//            os.flush();
//            Flag.sw.stopTotal();
//            double e = System.nanoTime();
//            disconnect();
//            if (verbose) {
//                System.out.println("Eva running time:" + (e - s) / 1e9);
//                System.out.println("Number Of AND Gates:" + env.numOfAnds);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }

    //@SuppressWarnings("rawtypes")
    public static void main(String[] args) throws ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, URISyntaxException {
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
                        ex.printStackTrace();
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
                }
            }
        });
        try {
            clientEndPoint.sendMessage(Utils.getMessage("ic", Inet4Address.getLocalHost().getHostAddress()));
        } catch (UnknownHostException ex) {

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

    }
}
