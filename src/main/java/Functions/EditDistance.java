/*
 * Md. Momin Al Aziz momin.aziz.cse @ gmail.com	
 * http://www.mominalaziz.com
 */
package Functions;

import Database.QueryDB;
import Database.Words;
import OPE.RegularOPE;
import Utilities.Paillier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author shad942
 */
public class EditDistance {

    final static int limit = 500;
    static int highestDistance = Integer.MAX_VALUE;

    public int getDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 2);//ThreadLocalRandom.current().nextInt(2, 5)
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public void insertMapDistance(Map<String, Integer> map) {
        Integer[] keys = new Integer[map.size()];
        keys = map.values().toArray(keys);
        Arrays.sort(keys);
//        System.out.println("keys "+Arrays.toString(keys));
//        ValueComparator valueComparator = new ValueComparator(map);
//        map = new TreeMap<>(valueComparator);
//        System.out.println("map "+map);
        while (map.keySet().iterator().hasNext()) {
            highestDistance = map.get(map.keySet().iterator().next());
        }

//        List<String> list = new ArrayList<>(map.keySet());
//        System.out.println("list size "+list.size());
        map.remove(keys[keys.length - 1]);

    }

    private void addToMap(TreeMap< Integer, List<String>> mapStringDistance, int distance, String word) {
        if (mapStringDistance.get(distance) == null) {
            List<String> tmp = new ArrayList<>();
            tmp.add(word);
            mapStringDistance.put(distance, tmp);

        } else {
            if (mapStringDistance.get(distance).size() < 10) {
                mapStringDistance.get(distance).add(word);
            }
        }
    }

    public Map<BigInteger, List<String>> executeEditDistanceOrderPreserving(JsonObject msg, String qID, int serverNo, boolean secure) {
//        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        String text = msg.getString("text");
        text = text.replace(" ", "");
        int count = Integer.parseInt(msg.getString("count"));

        QueryDB queryDB = new QueryDB();
        int offset = 0, server1 = ThreadLocalRandom.current().nextInt(0, 4), server2 = new Random().nextInt(7);
        System.out.println("server1 " + server1 + "server2 " + server2 + " count " + count + " text " + text);

        List<Words> words = queryDB.getFromWords(limit, offset, serverNo);
//        Map<String, Integer> mapDistance = new HashMap<>();
        TreeMap< Integer, List<String>> mapStringDistance = new TreeMap<>();
        while (!words.isEmpty()) {
            for (Words word : words) {
                int distance = getDistance(text, word.getWords());
                if (mapStringDistance.size() < count) {
                    addToMap(mapStringDistance, distance, word.getWords());
                    highestDistance = (highestDistance > distance) ? distance : highestDistance;
                } else if (distance < highestDistance) {
                    addToMap(mapStringDistance, distance, word.getWords());
                    Iterator it = mapStringDistance.keySet().iterator();
                    while (it.hasNext()) {
                        highestDistance = Integer.parseInt(it.next() + "");
                    }

                }
                if (mapStringDistance.size() > count) {
                    mapStringDistance.remove(mapStringDistance.pollLastEntry().getKey());
                }
            }
            offset += 500;
            words = queryDB.getFromWords(limit, offset, serverNo);

        }
        System.out.println("size of the map " + mapStringDistance.size());
//        jsonObjectBuilder.add("type", "result");
//        jsonObjectBuilder.add("queryID", qID);

        /**
         * change this to multiple time later
         */
//        JsonObjectBuilder jsonObjectBuilder1 = Json.createObjectBuilder();
        Map< BigInteger, List<String>> ret = new TreeMap<>();
        for (Map.Entry<Integer, List<String>> entrySet : mapStringDistance.entrySet()) {
            ret.put(secure ? (new BigInteger(new RegularOPE().getOPE(entrySet.getKey()) + "")) : new BigInteger(entrySet.getKey().toString()), entrySet.getValue());
        }
//        jsonObjectBuilder.add("result", jsonObjectBuilder1.build().toString());
//        JsonObject ret = jsonObjectBuilder.build();
        return ret;
    }

    public Map<BigInteger, List<String>> executeEditDistance(JsonObject msg, String qID, int serverNo, boolean secure) {
//        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        String text = msg.getString("text");
        text = text.replace(" ", "");
        int count = Integer.parseInt(msg.getString("count"));

        QueryDB queryDB = new QueryDB();
        int offset = 0, server1 = ThreadLocalRandom.current().nextInt(0, 4), server2 = new Random().nextInt(7);
        System.out.println("server1 " + server1 + "server2 " + server2 + " count " + count + " text " + text);

        List<Words> words = queryDB.getFromWords(limit, offset, serverNo);
//        Map<String, Integer> mapDistance = new HashMap<>();
        TreeMap< Integer, List<String>> mapStringDistance = new TreeMap<>();
        while (!words.isEmpty()) {
            for (Words word : words) {
                int distance = getDistance(text, word.getWords());
                if (mapStringDistance.size() < count) {
                    addToMap(mapStringDistance, distance, word.getWords());
                    highestDistance = (highestDistance > distance) ? distance : highestDistance;
                } else if (distance < highestDistance) {
                    addToMap(mapStringDistance, distance, word.getWords());
                    Iterator it = mapStringDistance.keySet().iterator();
                    while (it.hasNext()) {
                        highestDistance = Integer.parseInt(it.next() + "");
                    }

                }
                if (mapStringDistance.size() > count) {
                    mapStringDistance.remove(mapStringDistance.pollLastEntry().getKey());
                }
            }
            offset += 500;
            words = queryDB.getFromWords(limit, offset, serverNo);

        }
        System.out.println("size of the map " + mapStringDistance.size());
//        jsonObjectBuilder.add("type", "result");
//        jsonObjectBuilder.add("queryID", qID);

        /**
         * change this to multiple time later
         */
//        JsonObjectBuilder jsonObjectBuilder1 = Json.createObjectBuilder();
        Paillier paillier = new Paillier(true);
        Map< BigInteger, List<String>> ret = new TreeMap<>();
        for (Map.Entry<Integer, List<String>> entrySet : mapStringDistance.entrySet()) {
            ret.put(secure ? paillier.Encryption(new BigInteger(entrySet.getKey().toString())) : new BigInteger(entrySet.getKey().toString()), entrySet.getValue());
        }
//        jsonObjectBuilder.add("result", jsonObjectBuilder1.build().toString());
//        JsonObject ret = jsonObjectBuilder.build();
        return ret;
    }

    class ValueComparator implements Comparator {

        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        @Override
        public int compare(Object a, Object b) {
            if (base.get(a.toString()) >= base.get(b.toString())) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }

    }
}
