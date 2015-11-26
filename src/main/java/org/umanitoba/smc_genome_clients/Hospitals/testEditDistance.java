/*
 * Md. Momin Al Aziz momin.aziz.cse @ gmail.com	
 * http://www.mominalaziz.com
 */
package org.umanitoba.smc_genome_clients.Hospitals;

import Functions.EditDistance;
import java.math.BigDecimal;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author shad942
 */
public class testEditDistance {

    public static void main(String[] args) {
        JsonObject json = Json.createObjectBuilder()
                .add("text", "test")
                .add("count", "10")
                .build();
        JsonObject res = new EditDistance().executeEditDistance(json, "1");
        System.out.println(" res " + res.toString());
    }
}
