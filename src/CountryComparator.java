import java.util.Comparator;

public class CountryComparator implements Comparator<Country> {

        @Override
        public int compare(Country first, Country second) {
            return first.gethighTarif().compareTo(second.gethighTarif());
        }
}
