import java.util.*;

public class DbAccessDef_pl extends ListResourceBundle {
     public Object[][] getContents() {
         return contents;
     }

    static final Object[][] contents = {
       { "charset", "ISO-8859-2" },
       { "header", new String[] { "Baza danych ksi��ek" } },
       { "param_command", "Polecenie (select lub insert):" },
       { "submit", "Wykonaj" },
       { "footer", new String[] { } },
       { "resCode", new String[]
                    { "Wynik:", "Brak bazy", "B��d SQL",
                      "Wadliwe polecenie; musi zaczyna� si� od select lub insert" }
                    },
       { "resDescr",
            new String[] { "" } },
    };
}