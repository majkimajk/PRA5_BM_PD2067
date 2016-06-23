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
  private String getParamsServ;       // mazwa serwletu pobierania parametrów

  public void init() {

    context = getServletContext();

    presentationServ = context.getInitParameter("presentationServ");
    getParamsServ = context.getInitParameter("getParamsServ");
    String commandClassName = context.getInitParameter("commandClassName");
    String dbName = context.getInitParameter("dbName");

    // Za³adowanie klasy Command i utworzenie jej egzemplarza
    // który bêdzie wykonywa³ pracê
    try {
      Class commandClass = Class.forName(commandClassName);
      command = (Command) commandClass.newInstance();
      // ustalamy, na jakiej bazie ma dzia³aæ Command i inicjujemy obiekt
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

// Wywolanie serwletu pobierania parametrów
RequestDispatcher disp = context.getRequestDispatcher(getParamsServ);
disp.include(req,resp);

// Pobranie bie¿¹cej sesji
// i z jej atrybutów - wartoœci parametrów
// ustalonych przez servlet pobierania parametrów
// Ró¿ne informacje o aplikacji (np. nazwy parametrów)
// s¹ wygodnie dostêpne poprzez w³asn¹ klasê BundleInfo

HttpSession ses = req.getSession();

String[] pnames = BundleInfo.getCommandParamNames();
for (int i=0; i<pnames.length; i++) {

String pval = (String) ses.getAttribute("param_"+pnames[i]);

if (pval == null) return;  // jeszcze nie ma parametrów

// Ustalenie tych parametrów dla Command
command.setParameter(pnames[i], pval);
}

// Wykonanie dzia³añ definiowanych przez Command
// i pobranie wyników
// Poniewa¿ do serwletu mo¿e naraz odwo³ywaæ sie wielu klientów
// (wiele watków) - potrzebna jest synchronizacja
// przy czym rrygiel zamkniemy tutaj, a otworzymy w innym fragmnencie kodu
// - w serwlecie przentacji (ca³y cykl od wykonania cmd do poazania wyników jest sekcj¹ krytyczn¹) 

Lock mainLock = new ReentrantLock();

mainLock.lock();
// wykonanie
command.execute();

// pobranie wyników
List results = (List) command.getResults();

// Pobranie i zapamiêtanie kodu wyniku (dla servletu prezentacji)
ses.setAttribute("StatusCode", new Integer(command.getStatusCode()));

// Wyniki - bêd¹ dostêpne jako atrybut sesji
ses.setAttribute("Results", results);
ses.setAttribute("Lock", mainLock);    // zapiszmy lock, aby mozna go by³o otworzyæ póŸniej


// Wywo³anie serwletu prezentacji
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