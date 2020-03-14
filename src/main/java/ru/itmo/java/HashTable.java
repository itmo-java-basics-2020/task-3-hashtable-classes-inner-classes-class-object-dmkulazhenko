package ru.itmo.java;

@SuppressWarnings("FieldCanBeLocal")
public class HashTable {
    private static class Entry {
        private Object key, value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int MAX_INIT_SIZE = 1_000_007;
    private final int INIT_SIZE = 4096;
    private final int RESIZE_FACTOR = 2;
    private final int HASH_STEP = 911;
    private final float INIT_LOAD_FACTOR = 0.5f;
    private final float THRESHOLD = 0.75f;

    private int size = 0;
    private int realSize = 0;  // Including deleted entries
    private Entry[] table;
    private float loadFactor;

    public HashTable() {
        this.loadFactor = this.INIT_LOAD_FACTOR;
        this.table = new Entry[this.INIT_SIZE];
    }

    public HashTable(int initSize) {
        validateInitParams(initSize, null);
        this.loadFactor = this.INIT_LOAD_FACTOR;
        this.table = new Entry[initSize];
    }

    public HashTable(int initSize, float loadFactor) {
        validateInitParams(initSize, loadFactor);
        this.loadFactor = loadFactor;
        this.table = new Entry[initSize];
    }

    private void validateInitParams(Integer initSize, Float loadFactor) {
        if (initSize != null && (initSize <= 0 || initSize > this.MAX_INIT_SIZE)) {
            throw new IllegalArgumentException("Initial size must be in (0; " + this.MAX_INIT_SIZE + "]");
        }
        if (loadFactor != null && (loadFactor <= 0 || loadFactor > 1)) {
            throw new IllegalArgumentException("Load factor must be in (0; 1]");
        }
    }

    private int getHash(Object key) {
        int hash = key.hashCode() % this.table.length;
        if (hash < 0) {
            hash += this.table.length;
        }
        return hash;
    }

    private int nextHash(int hash) {
        return (hash + this.HASH_STEP) % this.table.length;
    }

    private void resize(int factor) {
        Entry[] oldTable = this.table;
        this.table = new Entry[oldTable.length * factor];
        this.size = 0;
        this.realSize = 0;

        for (Entry entry : oldTable) {
            if (entry != null  && entry.key != null && entry.value != null) {
                this.put(entry.key, entry.value);
            }
        }
    }


    Object put(Object key, Object value) {
        int hash = this.getHash(key);

        while (this.table[hash] != null) {
            if (key.equals(this.table[hash].key)) {
                Object prevValue = this.table[hash].value;
                this.table[hash].value = value;
                return prevValue;
            }
            hash = nextHash(hash);
        }

        this.table[hash] = new Entry(key, value);
        ++this.realSize;
        ++this.size;

        if (this.table.length * this.loadFactor < this.size) {
            this.resize(this.RESIZE_FACTOR);
        }
        if (this.THRESHOLD > this.loadFactor && this.table.length * this.THRESHOLD < this.realSize) {
            this.resize(1);
        }

        return null;
    }

    Object get(Object key) {
        int hash = this.getHash(key);
        while (this.table[hash] != null) {
            if (key.equals(this.table[hash].key)) {
                return this.table[hash].value;
            }
            hash = this.nextHash(hash);
        }
        return null;
    }

    Object remove(Object key) {
        int hash = this.getHash(key);
        while (this.table[hash] != null) {
            if (key.equals(this.table[hash].key)) {
                Object value = this.table[hash].value;
                this.table[hash].key = null;
                this.table[hash].value = null;
                --size;
                return value;
            }
            hash = this.nextHash(hash);
        }
        return null;
    }

    int size() {
        return this.size;
    }
}
