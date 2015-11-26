/*
 * Md. Momin Al Aziz momin.aziz.cse @ gmail.com	
 * http://www.mominalaziz.com
 */
package Functions;

import Database.QueryDB;
import Database.Snps;
import Utilities.Paillier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author shad942
 */
public class CountQuery {

//    static BigInteger r = new BigInteger("5714881131644890452843952028302231497460408850954597362377631094638418487691915771506028794235772572047404493606460583590826549316115776900483370456735234");

    public static JsonObject executeCount(JsonObject msg, String qID) {
        JsonObject ret = null;
        String query = msg.getString("text");
        String snip = msg.getString("snip");
        List<Snps> snps = new ArrayList<>();
        if (msg.getString("type").equals("all")) {
            snps = new QueryDB().getFromSnip(snip);
        } else {
            snps = new QueryDB().getFromSnip(snip, msg.getString("type"));
        }
        int count = 0;
        for (Snps snp : snps) {
            if (snps != null) {
                String[] tokens = snp.getDescription().trim().split("\\s+");
                for (String token : tokens) {
                    if (token.equals(query)) {
                        count++;
                    }
                }
            }
        }
        System.out.println("Count " + count);
        String encrypted = new Paillier(true).Encryption(new BigInteger(count + "")).toString();
        System.out.println("Encrypted " + encrypted);
        ret = Json.createObjectBuilder()
                .add("type", "result")
                .add("result", (msg.getString("secure").equals("1")) ? encrypted : count + "")
                .add("queryID", qID)
                .build();
        return ret;
    }
}
