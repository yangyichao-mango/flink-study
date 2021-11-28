package flink.examples.sql._03.source_sink.table.redis.options;

import java.io.Serializable;


public class RedisLookupOptions implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_MAX_RETRY_TIMES = 3;

    protected final String hostname;
    protected final int port;

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    private final long cacheMaxSize;
    private final long cacheExpireMs;
    private final int maxRetryTimes;
    private final boolean lookupAsync;

    private final boolean isBatchMode;

    private final int batchSize;

    private final int batchMinTriggerDelayMs;

    public RedisLookupOptions(
            long cacheMaxSize
            , long cacheExpireMs
            , int maxRetryTimes
            , boolean lookupAsync
            , String hostname
            , int port
            , boolean isBatchMode
            , int batchSize
            , int batchMinTriggerDelayMs) {
        this.cacheMaxSize = cacheMaxSize;
        this.cacheExpireMs = cacheExpireMs;
        this.maxRetryTimes = maxRetryTimes;
        this.lookupAsync = lookupAsync;

        this.hostname = hostname;
        this.port = port;
        this.isBatchMode = isBatchMode;
        this.batchSize = batchSize;
        this.batchMinTriggerDelayMs = batchMinTriggerDelayMs;
    }

    public long getCacheMaxSize() {
        return cacheMaxSize;
    }

    public long getCacheExpireMs() {
        return cacheExpireMs;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public boolean getLookupAsync() {
        return lookupAsync;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isBatchMode() {
        return isBatchMode;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getBatchMinTriggerDelayMs() {
        return batchMinTriggerDelayMs;
    }

    /** Builder of {@link RedisLookupOptions}. */
    public static class Builder {
        private long cacheMaxSize = -1L;
        private long cacheExpireMs = 0L;
        private int maxRetryTimes = DEFAULT_MAX_RETRY_TIMES;
        private boolean lookupAsync = false;

        private boolean isBatchMode = false;


        public Builder setIsBatchMode(boolean isBatchMode) {
            this.isBatchMode = isBatchMode;
            return this;
        }

        private int batchSize = 30;

        public Builder setBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        private int batchMinTriggerDelayMs = 1000;

        public Builder setBatchMinTriggerDelayMs(int batchMinTriggerDelayMs) {
            this.batchMinTriggerDelayMs = batchMinTriggerDelayMs;
            return this;
        }

        /** optional, lookup cache max size, over this value, the old data will be eliminated. */
        public Builder setCacheMaxSize(long cacheMaxSize) {
            this.cacheMaxSize = cacheMaxSize;
            return this;
        }

        /** optional, lookup cache expire mills, over this time, the old data will expire. */
        public Builder setCacheExpireMs(long cacheExpireMs) {
            this.cacheExpireMs = cacheExpireMs;
            return this;
        }

        /** optional, max retry times for Hbase connector. */
        public Builder setMaxRetryTimes(int maxRetryTimes) {
            this.maxRetryTimes = maxRetryTimes;
            return this;
        }

        /** optional, whether to set async lookup. */
        public Builder setLookupAsync(boolean lookupAsync) {
            this.lookupAsync = lookupAsync;
            return this;
        }

        protected String hostname = "localhost";

        protected int port = 6379;

        /**
         * optional, lookup cache max size, over this value, the old data will be eliminated.
         */
        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        /**
         * optional, lookup cache expire mills, over this time, the old data will expire.
         */
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public RedisLookupOptions build() {
            return new RedisLookupOptions(
                    cacheMaxSize
                    , cacheExpireMs
                    , maxRetryTimes
                    , lookupAsync
                    , hostname
                    , port
                    , isBatchMode
                    , batchSize
                    , batchMinTriggerDelayMs);
        }
    }
}
