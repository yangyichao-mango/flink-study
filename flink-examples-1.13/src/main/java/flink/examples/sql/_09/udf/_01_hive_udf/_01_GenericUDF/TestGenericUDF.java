package flink.examples.sql._09.udf._01_hive_udf._01_GenericUDF;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.io.Text;

public class TestGenericUDF extends GenericUDF {

    private transient StringObjectInspector soi = null;

    private transient StringObjectInspector soi1 = null;

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        PrimitiveObjectInspector primitiveObjectInspector = (PrimitiveObjectInspector) arguments[0];
        soi = (StringObjectInspector) primitiveObjectInspector;
        return PrimitiveObjectInspectorFactory
                .getPrimitiveWritableObjectInspector(PrimitiveObjectInspector.PrimitiveCategory.STRING);
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        return new Text("UNKNOWN");
    }

    @Override
    public String getDisplayString(String[] children) {
        return "test";
    }
}
