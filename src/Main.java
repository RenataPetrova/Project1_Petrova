import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main{
    static Countries countries;

    public static void main (String []args){
        List<Country> list = new ArrayList<>(); //vytvoreni seznamu, ktery budu chtit naplnit
        countries = new Countries(list);    //vytvoreni instance, diky ktere mam pristup, ke vsem funkciim z tridy countries

        try{
            countries.openFile(list);   //nacteni dat z csv
        }catch(Exception ex) {
            System.err.println(ex.getLocalizedMessage()); //neni nutne delat dalsi vlastni komentare, protoze ty jsou uz implementovany primo ve funkci, kde k chybe dojde.
        }												 // Je ale pro me dobry mit i hlasku javy, pokud sem neco nezohlednila a muj komentar neni uplne presny

        countries.writeList(list,true);
        countries.getSorted();
        countries.writeNewData(countries.writeList(list,false));//druhy vypis spojeny se zapisem do souboru


    }
}
