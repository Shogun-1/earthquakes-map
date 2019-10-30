package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	
	AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(255, 234, 128);
		pg.ellipse(x, y, 5, 5);
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		pg.pushStyle();

		pg.fill(255, 234, 128);
		String title = "Airport: " + this.getStringProperty("name");
		String location = "Location: " + this.getStringProperty("city") + ", " + this.getStringProperty("country");
		pg.rect(x + 10, y + 10, Math.max(pg.textWidth(title), pg.textWidth(location)) + 15, 35, 90);

		pg.fill(0);
		pg.text(title, x + 17.5f, y + 25);
		pg.text(location, x + 17.5f, y + 40);
		pg.popStyle();
	}
	
}
