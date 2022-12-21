import javax.naming.LimitExceededException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Countries {
    private List<Country> list;
    private List<Country> limitList = new ArrayList<>();
    private final String FILENAME = "vat-eu.csv";
    private String OUTPUTFILE = "vat-over-";
    private final String suffix = ".txt";
    private String[] recordSplit;
    private double highTarif;
    private	double lowTarif;
    private boolean specialTarif;
    private double defaultLimit = 20.0;
    private boolean rightInput = false;
    // private PrintWriter outputWriter;

    private String countriesUnderLimit = "Sazba VAT ";


    public Countries(List<Country> list){
        this.list = list;
    }

    public void addCountry(Country country){
        list.add(country);
    }

    public void removeCountry(Country country){
        list.remove(country);
    }

    public Country getCountry(int index){
        return list.get(index);
    }

    /*
    **********************************************************
    -nastavi limit podle ktereho se maji data vyfiltrovat
    -vyzyva uzivate k nekonecnemu zadavani, dokud nezada v spravnem formatu, program neukonci pri nespravnem zadani
    -vola metodu evaluateLimit, aby pripustil i zadani defaultniho limitu, nebo overil jestli je zadana hodnota double
    **********************************************************
    */
    private double setLimit(){
        double finalLimit = 0;


        while(rightInput == false) {
            System.out.println("Prosim, nastav požadovaný limit ve formatu cisla:");
            Scanner sc = new Scanner(System.in);
            String limit = sc.nextLine();
            try {
                finalLimit = evaluateLimit(limit);
                rightInput = true;
            }catch(Exception ex){

            }
        }
        return finalLimit;
    }

    /*
    **********************************************************
    -pokusi se konvertovat zadanou hodnotu na double
    - nebo pripousti defaultni limit u zmacknuti enteru
    **********************************************************
    */
    private double evaluateLimit(String limit) throws CountryException{
        double myLimit;
        if (limit.isEmpty()){
            myLimit = defaultLimit;
        }else{
            try {
                limit=replaceToDot(limit);
                myLimit = Double.parseDouble(limit);
            }catch(Exception ex){
                throw new CountryException("Vlozena hodnota neni cislo.");
            }
        }
        return myLimit;
    }
    /*
    **********************************************************
    -upravi output file name v zavislosti na tom jaky byl zvolenej limit
    **********************************************************
    */
    private void getOutputFileName(double limit){
        OUTPUTFILE = OUTPUTFILE + limit + suffix;
    }
    /*
    **********************************************************
    -vybere spravny list
    list: vsechny country
    limitList: krajiny nad limit, podlimitne jsou v jednom radku
    **********************************************************
    */
    private List<Country> chooseList(boolean firstListing){
        List<Country> tempList = new ArrayList<>();
        if (firstListing==false){
            tempList = limitList;
        }else{
            tempList = list;
        }

        return tempList;
    }

    /*
    **********************************************************
    -vstup: list - vsechny nactene hodnoty, uz s osetrenym formatem
    -vstup: firstListing - chci nacitat data jenom jednou, ale vystup ma byt jiny
    **********************************************************
    */
    public List<Country> writeList(List<Country> list,boolean firstListing){
        String recordToWrite;
        List<Country> tempList = new ArrayList<>();
        tempList = chooseList(firstListing);
        double limit = setLimit(); //nastaveni limitu uzivatelem, resp. nastaveni defaultniho

        //u prvniho nacteni, tj. u pouziti nezosortovaneho seznamu se vytvari uz textovy retezec s countries pod stanovenym limitem.
        if (firstListing == true) {
            countriesUnderLimit = countriesUnderLimit + limit + " % nebo nižší nebo používají speciální sazbu: ";
            getOutputFileName(limit);
        }

        //cyklus bude projizdet jednotlive krajiny ze seznamu nezosortovaneho prvnikrat a uz tehdy je rozdtridi na NAD a POD limit.
        //nebude to tedy vyhodnocovat znovu u druheho nacteni
        //puvodni zamer byl rovnou naplnit obe listy uz u prniho projizdeni cyklu, to se mi ale nepovedlo. Jde to nejak udelat??
        //takze druhe projizdeni cyklu je uz urceno jenom pro zapis dat, ktere uz ale byly vyhodnocene v prvnim projizdeni
        for (Country country : tempList) {

            System.out.println(country.getInfo(firstListing));		//zapis dat v pozadovanem formatu, format se lisi u prvniho a druheho zapisu. Osetreno vstupnim parametrem "firstListing"
            if (firstListing == true) {
                recordToWrite = fillLimitList(country, limit);		//vyhodnoceni jestli jsou NAD nebo POD limit
            }
        }

        String cutted = countriesUnderLimit.substring(0,countriesUnderLimit.length() - 2);   //cut last 2 digits, nechci koncit carkou a mezerou

        if (firstListing==true){
            System.out.println();
            countriesUnderLimit = cutted;	//jenom pripraveno pro druhy vypis, u prvniho vypisu se jenom odrazdkuje
        }else{
            for(int i = 1;i<35;i++){
                System.out.print("=");		//u druheho vypisu se pouzije cutted pripraveno z prvniho vypisu, oddeleno rovnitky
            }
            System.out.println();
            System.out.println(cutted);
        }
        return tempList;
    }

    public void getSorted(){
        Collections.sort(limitList,Collections.reverseOrder());
    }

    /*
    **********************************************************
    - vyhodnoti jestli je krajina POD nebo NAD limitem a soucasne pouziva special tarif
    - NAD jdou na seznam krajin nad a vypisou se u druhyho projizdeni cyklu
    - POD jdou do retezce(na jeden radek) a jsou pripravene taky pro druhy vypis
    **********************************************************
    */
    public String fillLimitList(Country country, double limit){
        String blank = "";

        if (country.gethighTarif()>= limit && country.getSpecialTarif()==false){

            limitList.add(country);
            String recordToWrite = country.getInfo(false);
            return recordToWrite;


        }else{

            countriesUnderLimit = countriesUnderLimit + country.getShortcut() + ", ";
            return blank;
        }
    }
    /*
    **********************************************************
    - vypise data do souboru
    **********************************************************
    */
    public void writeNewData(List<Country> list){
        String recordToWrite;
        try (PrintWriter outputWriter = new PrintWriter(new FileWriter(OUTPUTFILE))) {
            for (Country country : list) {
                recordToWrite = country.getInfo(false); //chceme vypsat ve formatu jaky ma druhy vypis
                recordToWrite = recordToWrite + "\n";
                outputWriter.print(recordToWrite);
            }

            for(int i = 1;i<35;i++){
                outputWriter.print("=");
            }
            outputWriter.print("\n");
            outputWriter.print(countriesUnderLimit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
**********************************************************
- pokusi se otevrit soubor, pokud ho najde. Pokud ho nenajde, nebo dojde k jinemu problemu, tak vyhodi chybu

**********************************************************
*/
    public void openFile(List<Country> list) throws CountryException{
        File file = new File(FILENAME);
        try{
            Scanner scanner = new Scanner(file);    //muze nastat chyba pri nacitani
            readFileData(list,scanner);	//samotne nacitani filu radek po radku
        }catch(Exception ex) {
            throw new CountryException("Chyba nacitani " + ex.getLocalizedMessage());
        }

    }


    /*
    **********************************************************
    - pokusi se nacist data z filu, radek po radku
    - nevim jestli vubec nekdy vyhodi chybu, jestli je osetreno => osetreno, chybu zdedim z metody "resolveWrongInput"
    - vsechno nacitam jako string, abych predesla nacitaci chybe a pak pomoci metody "resolveWrongInput" resim pripadne chyby v konverzi
    - nevim jestli vubec potrebuji pocitat line =>> overeno potrebuji
    - neosetreno pokud je soubor prazdny => osetreno, potrebuji overit jestli funguje dle ocekavani
    - vraci private promennou "list" - vsechno nactene uz v spravnem formatu
    **********************************************************
    */
    private List<Country> readFileData(List<Country> list, Scanner scanner) throws CountryException{
        String shortcut;
        String countryName;
        String highTarifString;
        String lowTarifString;
        String specialTarifString;

        int line = 1;		//line of current record

        while (scanner.hasNextLine()) {
            String record = scanner.nextLine();
            String[] recordSplit = record.split("\t");	//sloupecky rozdeleny tabulatorem
            shortcut = recordSplit[0];
            countryName = recordSplit[1];
            highTarifString = recordSplit[2];
            lowTarifString = recordSplit[3];
            specialTarifString = recordSplit[4];
            resolveWrongInput(highTarifString,lowTarifString,specialTarifString,line);		//vstupuji jenom ty parametry, ktere nechci ponechat stringy, protoze ty nebudou mit problem s konverzi

            Country country = new Country(shortcut, countryName, highTarif, lowTarif, specialTarif);
            list.add(country);
            line++;
        }

        //vyhod chybu pokud je soubor prazdny
        if (line == 1){
            throw new CountryException("Vstupni soubor je prazdny.");
        }

        return list;
    }
    /*
    **********************************************************
    - pokud je v desetinnem cisle pouzita carka tak ji nahradi teckou
    **********************************************************
    */
    private String replaceToDot(String temp){
        String replaced = temp.replaceAll(",",".");
        return replaced;
    }
    /*
    **********************************************************
    - vyhodnoti jestli maji vstupy pozadovany format
    - pokud dojde k chybe vypise hlasku(moji) a vyhodi chybu nadrizene tride, tj. readFileData
    **********************************************************
    */
    private void resolveWrongInput(String high,String low,String special,int line) throws CountryException{

        //osetreni formatu high tarif = double
        try{
            highTarif = Double.valueOf(replaceToDot(high));
        }catch(Exception ex){
            throw new CountryException("Plná sazba daně pro řádek " + line + " nemá formát desetinného čísla.");
        }

        //osetreni formatu low tarif = double
        try{
            lowTarif = Double.valueOf(replaceToDot(low));
        }catch(Exception ex){
            throw new CountryException("Snížená sazba daně pro řádek " + line + " nemá formát desetinného čísla.");
        }

        //osetreni formatu special tarif = boolean a soucasne povoluji, ze to muze byt napsany malym pismem, nebo kombinaci maleho a velkeho
        if (special.toUpperCase().equals("TRUE") || special.toUpperCase().equals("FALSE")) {
            specialTarif = Boolean.parseBoolean(special);
        } else {
            throw new CountryException("Speciální sazba daně pro řádek " + line + " nemá povolenou hodnotu(true, false).");
        }
    }
}