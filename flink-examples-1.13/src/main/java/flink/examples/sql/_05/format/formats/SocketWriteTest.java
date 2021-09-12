package flink.examples.sql._05.format.formats;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import flink.examples.JacksonUtils;
import flink.examples.sql._05.format.formats.protobuf.Test;


public class SocketWriteTest {


    public static void main(String[] args) throws IOException, InterruptedException {

        ServerSocket serversocket = new ServerSocket(9999);

        final Socket socket = serversocket.accept();

        int i = 0;

        while (true) {

            Map<String, Integer> map = ImmutableMap.of("key1", 1, "地图", i);

            Test test = Test.newBuilder()
                    .setName("姓名" + i)
                    .addNames("姓名列表" + i)
                    .putAllSiMap(map)
                    .build();

            System.out.println(JacksonUtils.bean2Json(test));
            byte[] b = test.toByteArray();

            socket.getOutputStream().write(b);

            socket.getOutputStream().flush();
            i++;

            if (i == 10) {
                break;
            }

            Thread.sleep(500);
        }

        socket.close();
        serversocket.close();

    }
}
