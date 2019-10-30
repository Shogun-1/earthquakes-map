package module6;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 *
 * @author UC San Diego Intermediate Software Development MOOC team
 *
 */
public class CityMarker extends CommonMarker {

	public static int TRI_SIZE = 5;  // The size of the triangle marker

	public CityMarker(Location location) {
		super(location);
	}


	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}


	// pg is the graphics object on which you call the graphics
	// methods.  e.g. pg.fill(255, 0, 0) will set the color to red
	// x and y are the center of the object to draw. 
	// They will be used to calculate the coordinates to pass
	// into any shape drawing methods.  
	// e.g. pg.rect(x, y, 10, 10) will draw a 10x10 square
	// whose upper left corner is at position x, y
	/**
	 * Implementation of method to draw marker on the map.
	 */
	public void drawMarker(PGraphics pg, float x, float y) {
		//System.out.println("Drawing a city");
		// Save previous drawing style
		pg.pushStyle();

		// IMPLEMENT: drawing triangle for each city
		pg.fill(255, 0, 0);
		pg.triangle(x - TRI_SIZE, y - TRI_SIZE, x + TRI_SIZE, y - TRI_SIZE, x, y);


		// Restore previous drawing style
		pg.popStyle();
	}

	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		String city = "City: " + getCity();
		String country = "Country: " + getCountry();
		String population = "Population: " + getPopulation() + " M";

		pg.pushStyle();

		pg.fill(255, 234, 128);
		pg.rectMode(PConstants.CORNER);
		float width = Math.max(pg.textWidth(city), pg.textWidth(country));
		width = Math.max(width, pg.textWidth(population));
		pg.rect(x, y-TRI_SIZE-50, width + 5, 45);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text(city, x+3, y-TRI_SIZE-50);
		pg.text(country, x+3, y - TRI_SIZE -35);
		pg.text(population, x+3, y - TRI_SIZE - 20);

		pg.popStyle();
	}

	private String getCity()
	{
		return getStringProperty("name");
	}

	private String getCountry()
	{
		return getStringProperty("country");
	}

	private float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}
}