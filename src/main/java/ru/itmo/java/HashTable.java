package ru.itmo.java;

@SuppressWarnings("FieldCanBeLocal")
public class HashTable {
    private final static int INIT_SIZE = 4096;
    private final static int RESIZE_FACTOR = 2;
    private final static int HASH_STEP = 911;
    private final static float INIT_LOAD_FACTOR = 0.5f;
    private final static float THRESHOLD = 0.75f;
    private static final Entry DELETED = new Entry(null, null);

    private int size = 0;
    private int realSize = 0;  // Including deleted entries
    private Entry[] table;
    private float loadFactor;

    public HashTable() {
        this(INIT_SIZE, INIT_LOAD_FACTOR);
    }

    public HashTable(int initSize) {
        this(initSize, INIT_LOAD_FACTOR);
    }

    public HashTable(int initSize, float loadFactor) {
        validateInitParams(initSize, loadFactor);
        this.loadFactor = loadFactor;
        this.table = new Entry[initSize];
    }

    public Object put(Object key, Object value) {
        if (this.table.length * this.loadFactor < this.size) {
            this.resize(RESIZE_FACTOR);
        }
        if (THRESHOLD > this.loadFactor && this.table.length * THRESHOLD < this.realSize) {
            this.resize(1);
        }

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

        return null;
    }

    public Object get(Object key) {
        int hash = this.getHash(key);
        while (this.table[hash] != null) {
            if (key.equals(this.table[hash].key)) {
                return this.table[hash].value;
            }
            hash = this.nextHash(hash);
        }
        return null;
    }

    public Object remove(Object key) {
        int hash = this.getHash(key);
        while (this.table[hash] != null) {
            if (key.equals(this.table[hash].key)) {
                Object value = this.table[hash].value;
                this.table[hash] = DELETED;
                --size;
                return value;
            }
            hash = this.nextHash(hash);
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    private void validateInitParams(Integer initSize, Float loadFactor) {
        if (initSize != null && initSize <= 0) {
            throw new IllegalArgumentException("Initial size must be positive");
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
        return (hash + HASH_STEP) % this.table.length;
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

    private static class Entry {
        private Object key;
        private Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
