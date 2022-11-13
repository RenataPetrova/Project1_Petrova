import java.util.ArrayList;
import java.util.List;

public class Main{
    static Countries countries;
    public static void main (String []args){
        List<Country> list = new ArrayList<>(); //vytvoreni seznamu, ktery budu chtit naplnit
        countries = new Countries(list);    //vytvoreni instance, diky ktere mam pristup, ke vsem funkciim z tridy countries
        try{
            countries.openFile(list);   //zapis dat z csv
        }catch(Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
