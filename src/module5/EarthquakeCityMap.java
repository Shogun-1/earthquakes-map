package module5;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;


/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setup and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private static UnfoldingMap map;

	static UnfoldingMap getMap() {
		return map;
	}

    // Markers for each city
	private static List<Marker> cityMarkers;

	static List<Marker> getCityMarkers() {
		return cityMarkers;
	}

	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;

	// Colors
	private int backgroundColor = color(102, 0, 17);
	private int yellow = color(255, 255, 0);
	private int blue = color(0, 0, 255);
	private int red = color(255, 0, 0);
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Microsoft.RoadProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	}  // End setup
	
	
	public void draw() {
		background(backgroundColor);
		map.draw();
		addKey();
	}

	public void mouseReleased() {
		if (mouseX > 30 && mouseX < 50 && mouseY > 480 && mouseY < 500) {
			backgroundColor = color(102, 0, 17);
		} else if (mouseX > 30 && mouseX < 50 && mouseY > 520 && mouseY < 540) {
			backgroundColor = color(204, 255, 204);
		}
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
	}
	
	// If there is a marker under the cursor, and lastSelected is null 
	// set the lastSelected to be the first marker found under the cursor
	// Make sure you do not select two markers.
	// 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// TODO: Implement this method
		for (Marker marker : markers) {
			if (marker.isInside(map, mouseX, mouseY)) {
				lastSelected = (CommonMarker) marker;
				lastSelected.setSelected(true);
				break;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		// TODO: Implement this method
		// Hint: You probably want a helper method or two to keep this code
		// from getting too long/disorganized
        if (lastClicked != null) {
            lastClicked.setClicked(false);
            lastClicked = null;
            unhideMarkers();
        } else {
            selectMarkerIfClicked(quakeMarkers);
            selectMarkerIfClicked(cityMarkers);
        }
	}
    private void selectMarkerIfClicked(List<Marker> markers)
    {
        // TODO: Implement this method
        for (Marker marker : markers) {
            if (marker.isInside(map, mouseX, mouseY)) {
                lastClicked = (CommonMarker) marker;
                lastClicked.setClicked(true);
                if (lastClicked instanceof EarthquakeMarker) {
                    double circle = ((EarthquakeMarker) lastClicked).threatCircle();
                    hideMarkersIfQuakeSelected(circle);
                } else {
                    hideMarkersIfCitySelected();
                }
                break;
            }
        }
    }
	
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}

	private void hideMarkersIfQuakeSelected(double circle) {
        for(Marker marker : quakeMarkers) {
            if (marker != lastClicked) {
                marker.setHidden(true);
            }
        }

        for(Marker marker : cityMarkers) {
            if (marker.getDistanceTo(lastClicked.getLocation()) > circle) {
                marker.setHidden(true);
            }
        }
    }

    private void hideMarkersIfCitySelected() {
        for(Marker marker : quakeMarkers) {
            if (marker instanceof EarthquakeMarker) {
                double circle = ((EarthquakeMarker) marker).threatCircle();
                if (marker.getDistanceTo(lastClicked.getLocation()) > circle) {
                    marker.setHidden(true);
                }
            }
        }

        for(Marker marker : cityMarkers) {
            if (marker != lastClicked) {
                marker.setHidden(true);
            }
        }
    }
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 234, 128);
		rect(20, 50, 175, 600);


		// Markers
		fill(255, 0, 0);
		triangle(30, 100, 50, 100, 40, 110);
		fill(255);
		ellipse(40, 150, 20, 20);
		rect(30, 190, 20, 20);
		// Texting
		fill(0);
		textSize(15);
		text("Earthquake Keys", 50, 70);
		text("City", 65, 110);
		text("Land Quake", 65, 155);
		text("Ocean Quake", 65, 205);
		text("Size ~ magnitude", 50, 250);

		// Markers
		fill(yellow);
		ellipse(40, 290, 20, 20);
		fill(blue);
		ellipse(40, 340, 20, 20);
		fill(red);
		ellipse(40, 390, 20, 20);
		// Texting
		fill(0);
		text("Shallow", 65, 295);
		text("Intermediate", 65, 345);
		text("Deep", 65, 395);

		// X on Past Day Quakes
		fill(255);
		ellipse(40, 440, 20, 20);
		fill(0);
		line(30, 430, 50, 450);
		line (50, 430, 30, 450);
		text("Past Day Quake", 65, 445);

		// Adding background color setters
		fill(102, 0, 17);
		rect(30, 480, 20, 20);
		fill (204, 255, 204);
		rect(30, 520, 20, 20);
		fill(0);
		text("Background color", 65, 515);
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.	
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	private void printQuakes() {
		int oceanQuakes = 0;
		boolean isNumberOfOceanQuakesFetched = false;
		int landQuakes = 0;
		boolean isNumberOfLandQuakesFetched = false;
		for (Marker marker : countryMarkers) {
			String countryName = marker.getStringProperty("name");
			int quakeCounter = 0;
			for (Marker earthquakeMarker : quakeMarkers) {
				if (earthquakeMarker instanceof LandQuakeMarker) {
					if (!isNumberOfLandQuakesFetched) {
						landQuakes++;
					}
					String earthquakeCountry = earthquakeMarker.getStringProperty("country");
					if (countryName.equals(earthquakeCountry)) {
						quakeCounter++;
					}
				} else if (!isNumberOfOceanQuakesFetched) {
					oceanQuakes++;
				}
			}
			isNumberOfOceanQuakesFetched = true;
			isNumberOfLandQuakesFetched = true;
			if (quakeCounter > 0) {
				System.out.println(countryName + ": " + quakeCounter);
			}
		}
		System.out.println("LAND QUAKES TOTAL: " + landQuakes);
		System.out.println("OCEAN QUAKES: " + oceanQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}

