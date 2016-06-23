import java.io.*;
import java.util.*;
import javax.naming.*;
import java.sql.*;
import javax.sql.*;

public class DbAccess extends CommandImpl {

  private DataSource dataSource;

  public void init() {
    try {
      Context init = new InitialContext();
      Context jndiCtx = (Context) init.lookup("java:comp/env");
      String dbName = (String) getParameter("dbName");
      dataSource = (DataSource) jndiCtx.lookup(dbName);
     } catch (NamingException exc) {
         setStatusCode(1);
     }
  }

  public void execute() {
    clearResult();
    setStatusCode(0);
    Connection con = null;
    try {
      synchronized(this) {
        con = dataSource.getConnection();
      }

      Statement stmt = con.createStatement();

      String cmd =  (String) getParameter("command");

      if (cmd.startsWith("select")) {
        ResultSet rs = stmt.executeQuery(cmd);

        // B�dziemy zapisywa� wynik jako skonkatenowane
        // warto�ci z kolumn ResultSetu
        // Oczywi�cie, w r�nych kwerendach b�d� r�ne kolumny
        // zatem korzystamy z ResultSetMetaData, by do nich dotrze�
 
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        while (rs.next()) {
          String wynik = "";
          for (int i=1; i<=cols; i++)
             wynik += rs.getObject(i) + " ";
          addResult(wynik);
        }
        rs.close();
      }
      else if (cmd.startsWith("insert")) {
        int upd = stmt.executeUpdate(cmd);
        addResult("Dopisano " + upd + " rekord�w");
        }
      else setStatusCode(3);
    } catch (SQLException exc) {
          setStatusCode(2);
          throw new DbAccessException("B��d w dost�pie do bazy lub w SQL", exc);
    } finally {
        try {
          con.close();
        } catch (Exception exc) {}
    }
  }

}