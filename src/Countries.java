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
        private String[] recordSplit;
        private double highTarif;
        private	double lowTarif;
        private boolean specialTarif;
        private double defaultLimit = 20.0;


        private String countriesUnderLimit = "Sazba VAT 20 % nebo nižší nebo používají speciální sazbu: ";


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

        private void setLimit(){
            Scanner sc = new Scanner(System.in);
            System.out.print("Prosim, nastav požadovaný limit:");
            try {
                String limit = sc.nextLine();

            }catch(Exception ex){
                System.err.println(ex.getLocalizedMessage());
            }
        }

        private double evaluateLimit(String limit){
            double myLimit;
            if (limit.isEmpty()){
                myLimit = defaultLimit;
            }else{
                myLimit = Double.parseDouble(limit);
            }
            return myLimit;
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
        public void writeList(List<Country> list,boolean firstListing){
            List<Country> tempList = new ArrayList<>();
                        tempList = chooseList(firstListing);

            for (Country country : tempList) {

                System.out.println(country.getInfo(firstListing));
                if (firstListing==true){
                    fillLimitList(country);
                }
            }
            String cutted = countriesUnderLimit.substring(0,countriesUnderLimit.length() - 2);   //cut last 2 digits
            if (firstListing==true){
                System.out.println();
            }else{
                for(int i = 1;i<35;i++){
                    System.out.print("=");
                }
                System.out.println();
                System.out.println(cutted);
            }
        }

            public void getSorted(){
                Collections.sort(limitList,Collections.reverseOrder());

            }
            public void fillLimitList(Country country){

                if (country.gethighTarif()>= defaultLimit && country.getSpecialTarif()==false){
                    limitList.add(country);
                }else{
                    countriesUnderLimit = countriesUnderLimit + country.getShortcut() + ", ";
                }
            }


        public void createOutputList(List<Country> list){
            String recordToWrite;


            try (PrintWriter outputWriter =
                         new PrintWriter(new FileWriter(FILENAME))) {

                for (Country country : list) {
                    recordToWrite = country.getInfo(true);
                    outputWriter.print(recordToWrite);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //public void createListAboveLimit(List<Country> list){
        //    list.sort();

        //}
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
