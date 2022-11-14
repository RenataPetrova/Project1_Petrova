public class Country implements Comparable<Country> {
        private String shortcut;
        private String countryName;
        private double highTarif; //percentage
        private	double lowTarif; //percentage
        private boolean specialTarif;

        public Country(String shortcut,String countryName,double highTarif,double lowTarif,boolean specialTarif){
            this.shortcut = shortcut;
            this.countryName = countryName;
            this.highTarif = highTarif;
            this.lowTarif = lowTarif;
            this.specialTarif = specialTarif;
        }

        public String getInfo(boolean firstListing) {
            String record;
            record = getCountryName() + " (" +
                    getShortcut() + "): " +
                    gethighTarif() + " %";

            if (firstListing == false) {
                record = record
                        + " (" + getLowTarif()
                        + " %)";
            }
            return record;
        }

        public boolean evaluateLimit(double limitValue){
            boolean result = false;
                if (gethighTarif()>=limitValue){
                    result = true;
                }
                return result;
        }

		public String getShortcut(){
            return shortcut;
        }

        public void setShortcut(){
            this.shortcut = shortcut;
        }

	    public String getCountryName(){
            return countryName;
        }

        public void setCountryName(){
            this.countryName = countryName;
        }

        public double gethighTarif(){
            return highTarif;
        }

        public void setHighTarif(){
            this.highTarif = highTarif;
        }

	    public double getLowTarif(){
            return lowTarif;
        }

        public void setLowTarif(){
            this.lowTarif = lowTarif;
        }

	    public boolean getSpecialTarif(){
            return specialTarif;
        }

        public void setSpecialTarif() {
            this.specialTarif = specialTarif;
        }


    @Override
    public int compareTo(Country secondCountry) {
        return Double.compare(this.highTarif,secondCountry.gethighTarif());
    }
}
