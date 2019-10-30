package module6;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 *
 * @author UC San Diego Intermediate Software Development MOOC team
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {

	public OceanQuakeMarker(PointFeature quake) {
		super(quake);

		// setting field in earthquake marker
		isOnLand = false;
	}


	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.pushStyle();

		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
		if (EarthquakeCityMap.getLastClicked() == this) {
			pg.stroke(0);
		} else {
			pg.noStroke();
		}
		for (Marker marker : EarthquakeCityMap.getCityMarkers()) {
			if (this.getDistanceTo(marker.getLocation()) <= this.threatCircle()) {
				SimplePointMarker simplePointMarker = new SimplePointMarker(marker.getLocation());
				ScreenPosition position = simplePointMarker.getScreenPosition(EarthquakeCityMap.getMap());
				// (200; 50) is the upper left point of the map actually
				pg.line(position.x - 200, position.y - 50, x, y);
			}
		}

		pg.popStyle();
	}




}