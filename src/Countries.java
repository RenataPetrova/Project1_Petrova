import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Countries {
        private List<Country> list;
        private final String FILEPATH = "input.txt";
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
            list.add(country);
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

        public void writeNewData(List<Country> list){
            String recordToWrite;
            try (PrintWriter outputWriter =
                         new PrintWriter(new FileWriter(FILEPATH))) {

                for (Country country : list) {
                    recordToWrite = country.getInfo();
                    //country.getName() + "\t" +
                    //country.getNotes() + "\t" +
                    //country.getFrequencyOfWatering() + "\t" +
                    //country.getWatering() + "\t" +
                    //country.getPlanted() + "\n";

                    outputWriter.print(recordToWrite);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void openFile(List<Country> list) throws CountryException{
            File file = new File(FILEPATH);
            try{
                Scanner scanner = new Scanner(file);    //muze nastat chyba pri nacitani
                readFileData(list,scanner);
                //return scanner;
            }catch(Exception ex) {
                throw new CountryException("Soubor nenalezen. " + ex.getLocalizedMessage());
            }

        }
        private List<Country> readFileData(List<Country> list, Scanner scanner) throws CountryException{
            String shortcut;
            String country;

            int line = 1;		//line of current record

            while (scanner.hasNextLine()) {
                String record = scanner.nextLine();
                String[] recordSplit = record.split("\t");
                shortcut = recordSplit[0];
                country = recordSplit[1];
                resolveWrongInput(line);

            }
            return list;
        }
       // private String evaluateReplacement(int index){
       //     String temp;
       //     if (recordSplit[index].indexOf(",")=-1){
       //         temp = recordSplit[index];
        //    }else{
        //        temp = replaceToDot(recordSplit[index]);
        //    }
        //}
        private String replaceToDot(String temp){
            String replaced = temp.replaceAll(",",".");
            return replaced;
        }
        private void resolveWrongInput(int line) throws CountryException{
            try{
                highTarif = evaluateReplacement(2);
            }catch(Exception ex){
                throw new CountryException("Plná sazba daně pro řádek " + line + "nemá formát desetinného čísla.");
            }

            try{
                lowTarif = evaluateReplacement(3);
            }catch(Exception ex){
                throw new CountryException("Snížená sazba daně pro řádek " + line + "nemá formát desetinného čísla.");
            }

            try{
                specialTarif = evaluateReplacement(4);
            }catch(Exception ex){
                throw new CountryException("Speciální sazba daně pro řádek " + line + "nemá povolenou hodnotu(true, false).");
            }

            Country country = new Country(shortcut, country, highTarif, lowTarif, specialTarif);
            list.add(country);
            line++;
        }
}
