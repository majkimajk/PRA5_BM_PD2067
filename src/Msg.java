

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.portable.InputStream;

/**
 * Servlet implementation class Msg
 */
@WebServlet("/Msg")
public class Msg extends HttpServlet {
	private static final long serialVersionUID = 1L;

	  private String prolog =
              "<html><title>Przyk³ad</title>" +
              "<body background=\"images/os2.jpg\" text=\"antiquewhite\"" +
              "link=\"white\" vlink=\"white\">";
	  
	  private String epilog = "</body></html>";
	  private ServletContext context;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        
        response.setContentType("text/html; charset=ISO-8859-2");
        
        String formFile = getInitParameter("control");
        PrintWriter out = response.getWriter();
        context = this.getServletContext();
        java.io.InputStream in = context.getResourceAsStream("/WEB-INF/control.html");
        BufferedReader br = new BufferedReader( new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) out.println(line);
		

		

	}

}
