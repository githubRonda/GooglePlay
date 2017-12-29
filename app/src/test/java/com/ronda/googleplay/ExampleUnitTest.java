package com.ronda.googleplay;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        // assertEquals(4, 2 + 2);

        Map<String, String> map = new HashMap<>();
        map.put("key3", "value3");
        map.put("key2", "value2");
        map.put("key1", "value1");

        Set<Map.Entry<String, String>> entries = map.entrySet();

        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();

            System.out.println("key: " + key + ", value: " + value);
        }
    }

    @Test
    public void fun1() throws Exception {

        System.out.println(-4 % 5);
    }
}