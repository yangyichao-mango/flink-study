package flink.examples.datastream._03.enums_state;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.base.EnumSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class EnumsStateTest {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        TypeInformation<StateTestEnums> t = TypeInformation.of(StateTestEnums.class);

        EnumSerializer<StateTestEnums> e = (EnumSerializer<StateTestEnums>) t.createSerializer(env.getConfig());

        DataOutputSerializer d = new DataOutputSerializer(10000);

        e.serialize(StateTestEnums.A, d);

        env.execute();
    }

    enum StateTestEnums {
        A,
        B,
        C
        ;
    }

}
