package flink.examples.datastream._04.keyed_co_process;


import java.util.HashMap;
import java.util.Map.Entry;

public class HashMapTest {

    public static void main(String[] args) {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("1", "2");
        hashMap.put("2", "2");
        hashMap.put("3", "2");
        hashMap.put("4", "2");
        hashMap.put("5", "2");

        for (Entry<String, String> e : hashMap.entrySet()) {
            hashMap.remove(e.getKey());
        }
    }

}
