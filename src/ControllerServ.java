import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.text.*;

@WebServlet("/x")
public class ControllerServ extends HttpServlet {

  private ServletContext context;
  private Command command;            // obiekt klasy dzialania (wykonawczej)
  private String presentationServ;    // nazwa serwlet prezentacji
  private String getParamsServ;       // mazwa serwletu pobierania parametr�w

  public void init() {

    context = getServletContext();

    presentationServ = context.getInitParameter("presentationServ");
    getParamsServ = context.getInitParameter("getParamsServ");
    String commandClassName = context.getInitParameter("commandClassName");
    String dbName = context.getInitParameter("dbName");

    // Za�adowanie klasy Command i utworzenie jej egzemplarza
    // kt�ry b�dzie wykonywa� prac�
    try {
      Class commandClass = Class.forName(commandClassName);
      command = (Command) commandClass.newInstance();
      // ustalamy, na jakiej bazie ma dzia�a� Command i inicjujemy obiekt
      command.setParameter("dbName", dbName);
      command.init();
    } catch (Exception exc) {
        throw new NoCommandException("Couldn't find or instantiate " +
                                      commandClassName);
    }
  }
  
  public void serviceRequest(HttpServletRequest req,
          HttpServletResponse resp)
          throws ServletException, IOException
{

resp.setContentType("text/html");

// Wywolanie serwletu pobierania parametr�w
RequestDispatcher disp = context.getRequestDispatcher(getParamsServ);
disp.include(req,resp);

// Pobranie bie��cej sesji
// i z jej atrybut�w - warto�ci parametr�w
// ustalonych przez servlet pobierania parametr�w
// R�ne informacje o aplikacji (np. nazwy parametr�w)
// s� wygodnie dost�pne poprzez w�asn� klas� BundleInfo

HttpSession ses = req.getSession();

String[] pnames = BundleInfo.getCommandParamNames();
for (int i=0; i<pnames.length; i++) {

String pval = (String) ses.getAttribute("param_"+pnames[i]);

if (pval == null) return;  // jeszcze nie ma parametr�w

// Ustalenie tych parametr�w dla Command
command.setParameter(pnames[i], pval);
}

// Wykonanie dzia�a� definiowanych przez Command
// i pobranie wynik�w
// Poniewa� do serwletu mo�e naraz odwo�ywa� sie wielu klient�w
// (wiele watk�w) - potrzebna jest synchronizacja
// przy czym rrygiel zamkniemy tutaj, a otworzymy w innym fragmnencie kodu
// - w serwlecie przentacji (ca�y cykl od wykonania cmd do poazania wynik�w jest sekcj� krytyczn�) 

Lock mainLock = new ReentrantLock();

mainLock.lock();
// wykonanie
command.execute();

// pobranie wynik�w
List results = (List) command.getResults();

// Pobranie i zapami�tanie kodu wyniku (dla servletu prezentacji)
ses.setAttribute("StatusCode", new Integer(command.getStatusCode()));

// Wyniki - b�d� dost�pne jako atrybut sesji
ses.setAttribute("Results", results);
ses.setAttribute("Lock", mainLock);    // zapiszmy lock, aby mozna go by�o otworzy� p�niej


// Wywo�anie serwletu prezentacji
disp = context.getRequestDispatcher(presentationServ);
disp.forward(req, resp);
}


public void doGet(HttpServletRequest request,
 HttpServletResponse response)
throws ServletException, IOException
{
serviceRequest(request, response);
}

public void doPost(HttpServletRequest request,
 HttpServletResponse response)
throws ServletException, IOException
{
serviceRequest(request, response);
}
  
  
}