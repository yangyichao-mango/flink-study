package flink.examples.sql._08.batch._03_hive_udf._01_GenericUDAFResolver2;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFResolver2;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.Text;

public class TestHiveUDAF implements GenericUDAFResolver2 {

    public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters) throws SemanticException {
        return new InneGenericUDAFEvaluatorr();
    }


    public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo paramInfo) throws SemanticException {

        return new InneGenericUDAFEvaluatorr();
    }


    public static class InneGenericUDAFEvaluatorr extends GenericUDAFEvaluator {
        private PrimitiveObjectInspector inputOI;

        @Override
        public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
            super.init(m, parameters);
            this.inputOI = (PrimitiveObjectInspector) parameters[0];
            return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
        }

        static class StringAgg implements AggregationBuffer {
            String all = "";
        }

        @Override
        public AggregationBuffer getNewAggregationBuffer() throws HiveException {
            StringAgg stringAgg = new StringAgg();
            return stringAgg;
        }

        @Override
        public void reset(AggregationBuffer agg) throws HiveException {
            StringAgg stringAgg = (StringAgg) agg;
            stringAgg.all = "";
        }

        @Override
        public void iterate(AggregationBuffer agg, Object[] parameters) throws HiveException {
            StringAgg myagg = (StringAgg) agg;

            String inputStr = PrimitiveObjectInspectorUtils.getString(parameters[0], inputOI);

            myagg.all += inputStr;
        }

        @Override
        public Object terminatePartial(AggregationBuffer agg) throws HiveException {
            return this.terminate(agg);
        }

        @Override
        public void merge(AggregationBuffer agg, Object partial) throws HiveException {
            if (partial != null) {
                StringAgg stringAgg = (StringAgg) agg;

                stringAgg.all += partial;
            }
        }

        @Override
        public Object terminate(AggregationBuffer agg) throws HiveException {

            return new Text(((StringAgg) agg).all);
        }


    }
}
