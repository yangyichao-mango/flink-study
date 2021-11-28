package flink.examples.sql._03.source_sink.table.redis.options;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;


public class RedisWriteOptions {

    protected final String hostname;
    protected final int port;

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    private int writeTtl;

    private final String writeMode;

    private final boolean isBatchMode;

    private final int batchSize;

    public static final ConfigOption<Integer> WRITE_TTL = ConfigOptions
            .key("write.ttl")
            .intType()
            .defaultValue(24 * 3600)
            .withDescription("Optional ttl for insert to redis");

    public static final ConfigOption<String> WRITE_MODE = ConfigOptions
            .key("write.mode")
            .stringType()
            .defaultValue("string")
            .withDescription("mode for insert to redis");

    public static final ConfigOption<Boolean> IS_BATCH_MODE = ConfigOptions
            .key("is.batch.mode")
            .booleanType()
            .defaultValue(false)
            .withDescription("if is.batch.mode is ture, means it can cache records and hit redis using jedis pipeline.");

    public static final ConfigOption<Integer> BATCH_SIZE = ConfigOptions
            .key("batch.size")
            .intType()
            .defaultValue(30)
            .withDescription("jedis pipeline batch size.");

    public RedisWriteOptions(int writeTtl, String hostname, int port, String writeMode, boolean isBatchMode, int batchSize) {
        this.writeTtl = writeTtl;
        this.hostname = hostname;
        this.port = port;
        this.writeMode = writeMode;
        this.isBatchMode = isBatchMode;
        this.batchSize = batchSize;
    }

    public int getWriteTtl() {
        return writeTtl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getWriteMode() {
        return writeMode;
    }

    public boolean isBatchMode() {
        return isBatchMode;
    }

    public int getBatchSize() {
        return batchSize;
    }

    /** Builder of {@link RedisWriteOptions}. */
    public static class Builder {
        private int writeTtl = 24 * 3600;

        /** optional, max retry times for Redis connector. */
        public Builder setWriteTtl(int writeTtl) {
            this.writeTtl = writeTtl;
            return this;
        }

        protected String hostname = "localhost";

        protected int port = 6379;

        private String writeMode = "string";

        private boolean isBatchMode = false;

        private int batchSize = 30;

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

        public Builder setWriteMode(String writeMode) {
            this.writeMode = writeMode;
            return this;
        }

        public RedisWriteOptions build() {
            return new RedisWriteOptions(writeTtl, hostname, port, writeMode, isBatchMode, batchSize);
        }
    }
}
