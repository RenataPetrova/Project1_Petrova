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

        private double evaluateLimit(String limit) throws CountryException{
            double myLimit;
            if (limit.isEmpty()){
                myLimit = defaultLimit;
            }else{
                try {
                    myLimit = Double.parseDouble(limit);
                }catch(Exception ex){
                    throw new CountryException("Vlozena hodnota neni cislo.");
                }
            }
            return myLimit;
        }

        private void getOutputFileName(double limit){


            OUTPUTFILE = OUTPUTFILE + limit + suffix;

        }

        private List<Country> chooseList(boolean firstListing){
            List<Country> tempList = new ArrayList<>();
            if (firstListing==false){
                tempList = limitList;
            }else{
                tempList = list;

            }
            return tempList;
        }
        public List<Country> writeList(List<Country> list,boolean firstListing){
            String recordToWrite;
            List<Country> tempList = new ArrayList<>();
                        tempList = chooseList(firstListing);
            double limit = setLimit();

            if (firstListing == true) {
                countriesUnderLimit = countriesUnderLimit + limit + " % nebo nižší nebo používají speciální sazbu: ";
                getOutputFileName(limit);
            }
       //     try (PrintWriter outputWriter = new PrintWriter(new FileWriter(OUTPUTFILE))){

                for (Country country : tempList) {

                    System.out.println(country.getInfo(firstListing));
                    if (firstListing == true) {
                        recordToWrite = fillLimitList(country, limit);
     //                   outputWriter.print(recordToWrite + "\n");
                        //outputWriter.print("ahoj" + "\n");
                    }
                }
         //   }catch(Exception ex){
         //           ex.printStackTrace();
         //   }
            String cutted = countriesUnderLimit.substring(0,countriesUnderLimit.length() - 2);   //cut last 2 digits

            if (firstListing==true){
                System.out.println();
                countriesUnderLimit = cutted;
            }else{
                for(int i = 1;i<35;i++){
                    System.out.print("=");
                }
                System.out.println();
                System.out.println(cutted);
            }
            return tempList;
        }

            public void getSorted(){
                Collections.sort(limitList,Collections.reverseOrder());

            }
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
    public void writeNewData(List<Country> list){
        String recordToWrite;
        try (PrintWriter outputWriter =
                     new PrintWriter(new FileWriter(OUTPUTFILE))) {

            for (Country country : list) {
                recordToWrite = country.getInfo(false);
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
        public void openFile(List<Country> list) throws CountryException{
            File file = new File(FILENAME);
            try{
                Scanner scanner = new Scanner(file);    //muze nastat chyba pri nacitani
                readFileData(list,scanner);
                //return scanner;
            }catch(Exception ex) {
                throw new CountryException("Chyba nacitani " + ex.getLocalizedMessage());
            }

        }
        private List<Country> readFileData(List<Country> list, Scanner scanner) throws CountryException{
            String shortcut;
            String countryName;
            String highTarifString;
            String lowTarifString;
            String specialTarifString;

            int line = 1;		//line of current record

            while (scanner.hasNextLine()) {
                String record = scanner.nextLine();
                String[] recordSplit = record.split("\t");
                shortcut = recordSplit[0];
                countryName = recordSplit[1];
                highTarifString = recordSplit[2];
                lowTarifString = recordSplit[3];
                specialTarifString = recordSplit[4];
                resolveWrongInput(highTarifString,lowTarifString,specialTarifString,line);

                Country country = new Country(shortcut, countryName, highTarif, lowTarif, specialTarif);
                list.add(country);
                line++;
            }

            return list;
        }
        private String replaceToDot(String temp){
            String replaced = temp.replaceAll(",",".");
            return replaced;
        }
        private void resolveWrongInput(String high,String low,String special,int line) throws CountryException{
            try{
                highTarif = Double.valueOf(replaceToDot(high));
            }catch(Exception ex){
                throw new CountryException("Plná sazba daně pro řádek " + line + " nemá formát desetinného čísla.");
            }

            try{
                lowTarif = Double.valueOf(replaceToDot(low));
            }catch(Exception ex){
                throw new CountryException("Snížená sazba daně pro řádek " + line + " nemá formát desetinného čísla.");
            }

            if (special.toUpperCase().equals("TRUE") || special.toUpperCase().equals("FALSE")) {
                specialTarif = Boolean.parseBoolean(special);
            } else {
                throw new CountryException("Speciální sazba daně pro řádek " + line + " nemá povolenou hodnotu(true, false).");
            }
        }
}
