package flink.examples.sql._03.source_sink.table.socket;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.RowData;


public class SocketSourceFunction extends RichSourceFunction<RowData> implements ResultTypeQueryable<RowData> {

    private final String hostname;
    private final int port;
    private final byte byteDelimiter;
    private final DeserializationSchema<RowData> deserializer;

    private volatile boolean isRunning = true;
    private Socket currentSocket;

    public SocketSourceFunction(String hostname, int port, byte byteDelimiter,
            DeserializationSchema<RowData> deserializer) {
        this.hostname = hostname;
        this.port = port;
        this.byteDelimiter = byteDelimiter;
        this.deserializer = deserializer;
    }

    @Override
    public TypeInformation<RowData> getProducedType() {
        return deserializer.getProducedType();
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        deserializer.open(null);

        this.currentSocket = new Socket();

        this.currentSocket.connect(new InetSocketAddress(hostname, port), 0);
    }

    @Override
    public void run(SourceContext<RowData> ctx) throws Exception {

        InputStream stream = this.currentSocket.getInputStream();

        while (isRunning) {
            // open and consume from socket

            byte[] b = new byte[46];

            stream.read(b, 0, 46);

            RowData rowData = deserializer.deserialize(b);

            ctx.collect(rowData);
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
        try {
            currentSocket.close();
        } catch (Throwable t) {
            // ignore
        }
    }
}