package lifeexpectancy;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifeExpectancy extends PApplet {
    private UnfoldingMap map;

    private final AbstractMapProvider google = new Google.GoogleMapProvider();
    private final AbstractMapProvider microsoft = new Microsoft.RoadProvider();

    private Map<String, Float> lifeExpByCountry;

    private List<Feature> countries;
    private List<Marker> countryMarkers;

    public void setup() {
        size(800, 600, OPENGL);
        background(200);
        map = new UnfoldingMap(this, 50, 50, 500, 500, microsoft);
        map.zoomTo(1.3f);
        MapUtils.createDefaultEventDispatcher(this, map);

        lifeExpByCountry = loadLifeExpectancyFromCSV("LifeExpectancyWorldBankModule3.csv");

        countries = GeoJSONReader.loadData(this, "countries.geo.json");
        countryMarkers = MapUtils.createSimpleMarkers(countries);

        map.addMarkers(countryMarkers);
        shadeCountries();

    }
    public void draw() {
        map.draw();
    }

    private Map<String, Float> loadLifeExpectancyFromCSV (String fileName) {
        Map <String, Float> lifeExpMap = new HashMap<>();
        String[] rows = loadStrings(fileName);

        for (int i = 1; i <= 214; i++) {
            String[] columns = rows[i].split(",");
            try {
                if (!columns[5].equals("..")) {
                    float value = Float.parseFloat(columns[5]);
                    lifeExpMap.put(columns[4], value);
                }
            } catch (NumberFormatException ex) {
                if (!columns[6].equals("..")) {
                    float value = Float.parseFloat(columns[6]);
                    lifeExpMap.put(columns[5], value);
                }
            }
        }

        return lifeExpMap;
    }

    private void shadeCountries () {
        for (Marker marker : countryMarkers) {
            String countryID = marker.getId();
            if (lifeExpByCountry.containsKey(countryID)) {
                float lifeExp = lifeExpByCountry.get(countryID);
                int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);
                marker.setColor(color(255 - colorLevel, 100, colorLevel));
            } else {
                marker.setColor(color(150, 150, 150));
            }
        }
    }
}
