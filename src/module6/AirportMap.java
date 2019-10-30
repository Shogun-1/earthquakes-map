package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	private UnfoldingMap map;
	private List<Marker> airportList;
	private List<Marker> routeList;

	// Colors
	private int yellow = color(255, 255, 0);
	private int blue = color(0, 0, 255);
	private int red = color(255, 0, 0);
	private int green = color(0, 255, 0);
	private int purple = color(179, 0, 179);

	private int xbase = 15;
	private int ybase = 170;

	private CommonMarker lastSelected;

	public void setup() {
		// setting up PAppler
		size(900,700, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 200, 50, 650, 600, new Microsoft.RoadProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<>();
		HashMap<Integer, Location> airports = new HashMap<>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());

		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(102, 0, 17);
		map.draw();
		addKey();
		
	}

	private void addKey() {
		fill(255, 234, 128);
		rect(10, 50, 180, 400);

		stroke(0);
		fill(0);
		textAlign(CENTER, CENTER);
		textSize(15);
		text("Click on square to display located in selected region airports only", 20, 55, 150, 100);

		// Squares
		fill(red);
		rect(xbase, ybase, 30, 30);
		fill(yellow);
		rect(xbase, ybase + 50, 30, 30);
		fill(blue);
		rect(xbase, ybase + 100, 30, 30);
		fill(green);
		rect(xbase, ybase + 150, 30, 30);
		fill(purple);
		rect(xbase, ybase + 200, 30, 30);


		// Regions
		fill(0);
		textAlign(LEFT, CENTER);
		text("Europe", xbase + 40, ybase + 12.5f);
		text("Asia", xbase + 40, ybase + 62.5f);
		text("Americas", xbase + 40, ybase + 112.5f);
		text("Africa", xbase + 40, ybase + 162.5f);
		text("Australia", xbase + 40, ybase + 212.5f);
	}

	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;

		}
		selectMarkerIfHover(airportList);
	}

	// If there is a marker under the cursor, and lastSelected is null
	// set the lastSelected to be the first marker found under the cursor
	// Make sure you do not select two markers.
	//
	private void selectMarkerIfHover(List<Marker> markers)
	{
		for (Marker airport : markers) {
			if (airport.isInside(map, mouseX, mouseY)) {
				lastSelected = (CommonMarker) airport;
				lastSelected.setSelected(true);
				break;
			}
		}
	}

	public void mouseReleased() {
		unhideAll();
		if (mouseX > xbase && mouseX < xbase + 30 && mouseY > ybase && mouseY < ybase + 30) {
			displaySelectedAirports("Europe");
		} else if (mouseX > xbase && mouseX < xbase + 30 && mouseY > ybase + 50 && mouseY < ybase + 80) {
			displaySelectedAirports("Asia");
		} else if (mouseX > xbase && mouseX < xbase + 30 && mouseY > ybase + 100 && mouseY < ybase + 130) {
			displaySelectedAirports("America");
		} else if (mouseX > xbase && mouseX < xbase + 30 && mouseY > ybase + 150 && mouseY < ybase + 180) {
			displaySelectedAirports("Africa");
		} else if (mouseX > xbase && mouseX < xbase + 30 && mouseY > ybase + 200 && mouseY < ybase + 230) {
			displaySelectedAirports("Australia");
		}
	}

	private void displaySelectedAirports(String region) {
		for (Marker airport : airportList) {
			try {
				if (!airport.getStringProperty("region").equals(region)) {
					airport.setHidden(true);
				}
			} catch (NullPointerException ex) {
				airport.setHidden(true);
			}
		}
	}

	private void unhideAll() {
		for (Marker airport : airportList) {
			airport.setHidden(false);
		}
	}

}
