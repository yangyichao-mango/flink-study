package flink.examples.sql._06.calcite;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;


public class CalciteTest {

    public static void main(String[] args) throws SqlParseException {
        SqlParser parser = SqlParser.create("select c,d from source where a = '6'", SqlParser.Config.DEFAULT);
        SqlNode sqlNode = parser.parseStmt();

        System.out.println();
    }

}
