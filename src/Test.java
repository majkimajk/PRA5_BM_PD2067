import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {

    public static void main(String[] args) {


        String driverName = "org.apache.derby.jdbc.EmbeddedDriver";
        String url = "jdbc:derby:E:/DerbyDbs/ksidb";
        String query = "SELECT DISTINCT AUTOR.NAME, POZYCJE.TYTUL, POZYCJE.CENA, POZYCJE.ISBN FROM AUTOR, POZYCJE";
        try {
            Class.forName(driverName);
            Connection con = DriverManager.getConnection(url);
            java.sql.Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(query);
            rs.afterLast();
            while (rs.previous()) {
                System.out.println(rs.getString(1) + ". " + rs.getString(2) + " -- " + rs.getString(3) + "          " + rs.getString(4));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
