import org.apache.flink.api.java.tuple.Tuple3
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api.{DataTypes, Schema}
import org.apache.flink.types.Row

// https://ci.apache.org/projects/flink/flink-docs-release-1.8/dev/table/udfs.html

/**
 * https://blog.csdn.net/fct2001140269/article/details/84066274
 *
 * https://ci.apache.org/projects/flink/flink-docs-release-1.8/dev/table/
 *
 * https://blog.csdn.net/qq_35338741/article/details/108645832
 */

object ScalaEnv {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // create a TableEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    val source = env.fromCollection(scala.Iterator.apply(Tuple3.of(new String("2"), 1L, 1627218000000L), Tuple3.of(new String("2"), 101L, 1627218000000L + 6000L), Tuple3.of(new String("2"), 201L, 1627218000000L + 7000L), Tuple3.of(new String("2"), 301L, 1627218000000L + 7000L)))

    tableEnv.createTemporaryView("source_db.source_table"
      , source
      , Schema
        .newBuilder()
        .column("f0", DataTypes.STRING())
        .column("f1", DataTypes.BIGINT())
        .column("f2", DataTypes.BIGINT())
        .build())

    tableEnv.createFunction("hashCode"
      , classOf[TableFunc0])

    val sql = "select * from source_db.source_table as a LEFT JOIN LATERAL TABLE(table1(a.f1)) AS DIM(status_new) ON TRUE"

    tableEnv.toDataStream(tableEnv.sqlQuery(sql), classOf[Row]).print()

    // execute
    env.execute()
  }

}