package sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces;

import org.jdeferred.Promise;

/**
 * Created by Keng Kiat Lim on 12/29/14.
 */
public interface Pollable {
    public Promise getSensorValues();
}
