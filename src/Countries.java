import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Countries {
        private List<Country> list;
        private final String FILENAME = "vat-eu.csv";
        private String[] recordSplit;
        private double highTarif;
        private	double lowTarif;
        private boolean specialTarif;
        private double defaultLimit = 20.0;


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
        public boolean limitInputAboveTwenty(double limit){
            boolean result = false;
            if(limit>=defaultLimit){
                result=true;
            }
            return result;
        }

        public void getList(List<Country> list){
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
