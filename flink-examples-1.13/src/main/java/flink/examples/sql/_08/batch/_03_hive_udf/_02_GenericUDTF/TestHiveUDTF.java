package flink.examples.sql._08.batch._03_hive_udf._02_GenericUDTF;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class TestHiveUDTF extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {
        ArrayList<String> fieldNames = new ArrayList<String>() {{
            add("column1");
        }};
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>() {{
            add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        }};

        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    @Override
    public void process(Object[] objects) throws HiveException {

        forward(objects[0]);
        forward(objects[0]);

    }

    @Override
    public void close() throws HiveException {

    }


}
