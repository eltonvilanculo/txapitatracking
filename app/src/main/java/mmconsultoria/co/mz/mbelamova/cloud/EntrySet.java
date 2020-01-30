package mmconsultoria.co.mz.mbelamova.cloud;

import java.util.Map;

public class EntrySet<V> implements Map.Entry<String, V> {
    private String key;
    private V value;

    public static <T> EntrySet<T> from(String key, T value) {
        EntrySet<T> entrySet = new EntrySet<>();
        entrySet.key = key;
        entrySet.value = value;

        return entrySet;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }

    @Override
    public String toString() {
        return "EntrySet{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
