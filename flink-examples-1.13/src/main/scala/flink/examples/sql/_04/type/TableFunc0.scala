package flink.examples.sql._04.`type`

import org.apache.flink.table.functions.TableFunction


case class SimpleUser(name: String, age: Int)

class TableFunc0 extends TableFunction[SimpleUser] {

  // make sure input element's format is "<string&gt#<int>"

  def eval(user: String): Unit = {

    if (user.contains("#")) {

      val splits = user.split("#")

      collect(SimpleUser(splits(0), splits(1).toInt))

    }
  }

}