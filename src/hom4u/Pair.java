/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hom4u;

/**
 *
 * @author hp
 */
public class Pair {
    
    double lat,lon;

    public Pair(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    
    public String toString(){
    
        return ""+lat+" "+lon;
        
    }
    
    
    
}
