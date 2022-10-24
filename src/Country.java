public class Country {
        private String shortcut;
        private String country;
        private double highTarif; //percentage
        private	double lowTarif; //percentage
        private boolean specialTarif;

        public Country(String shortcut,String country,double highTarif,double lowTarif,boolean specialTarif){
            this.shortcut = shortcut;
            this.country = country;
            this.highTarif = highTarif;
            this.lowTarif = lowTarif;
            this.specialTarif = specialTarif;
        }

        public String getInfo() {
            String record;
            record = getCountry() + " (" +
                    getShortcut() + "): " +
                    gethighTarif() + " %";

            if (getSpecialTarif() == true) {
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

	    public String getCountry(){
            return country;
        }

        public void setCountry(){
            this.country = country;
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
}
