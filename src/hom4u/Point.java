/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hom4u;

//import java.time.LocalTime;
import static java.lang.Math.abs;
import java.util.Random;

/**
 *
 * @author pwightman
 */
public class Point {
    
    String name;
    double lat;
    double lon;
    String numVal;
    String hashVal;
    Approximation app;
    Relocation rel;
    int scale;
    double radius;
    
    static final double DEGRESS_TO_MT=+360.0/40400000;
    
    public Point(){
        Random r = new Random();
        lon = -180+r.nextDouble()*360;
        lat = -90+r.nextDouble()*180;
        name = "";
        numVal="";
        hashVal="";
    }
    
    public Point(Point p){
        this(new String(p.name), p.lat, p.lon, p.app, p.rel, p.scale);
    }
    
    public Point(double lat, double lon){
        this("", lat, lon);
    }
    
    public Point(String lat, String lon){
        this("", Double.parseDouble(lat), Double.parseDouble(lon));
    }
    
    public Point(String name, double lat, double lon){
        this(name, lat, lon, Approximation.NONE, Relocation.NONE, 0);
    }
    
    public Point(String name, double lat, double lon, Approximation app, Relocation rel, int scale){
        this(name, lat, lon, app, rel, scale, 1);
    }
    
    public Point(String name, double lat, double lon, Approximation app, Relocation rel, int scale, long radius){
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.numVal="";
        this.hashVal="";
        this.app = app;
        this.rel = rel;
        this.scale = scale;
        this.radius = radius;
    }

    Point(Point point, Approximation app, Relocation rel, int scale) {
        this(point);
        this.app = app;
        this.rel = rel;
        this.scale = scale;
    }

    public void setApp(Approximation app) {
        this.app = app;
    }

    public void setRel(Relocation rel) {
        this.rel = rel;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
    
    public void setScale(int scale){
        this.scale = scale;
    }
    
    public int getScale(){
        return this.scale;
    }
    
    
    public String toString(){
        if(radius != 0){
            if(name != "")
                return name+","+lat+","+lon+","+numVal+","+hashVal+","+app+","+rel+","+scale+","+radius;

            return ""+lat+","+lon+","+numVal+","+hashVal+","+app+","+rel+","+scale+","+radius;
        }else{
            if(name != "")
                return name+","+lat+","+lon+","+numVal+","+hashVal+","+app+","+rel+","+scale;
        
            return ""+lat+","+lon+","+numVal+","+hashVal+","+app+","+rel+","+scale;
        }
    }
    
    public void setNumVal(String val){
        this.numVal = val;
    }
    
    public String getNumVal(){
        return this.numVal;
    }
    
    public void calcSetHashVal(){
        this.setHashVal(Geohash.calcGeoHash(this));
    }
    
    public void calcSetNumVal(){
        if(this.hashVal.equals("") || this.hashVal.length()<9)
            calcSetHashVal();
        this.setNumVal(""+Geohash.calcGeoHashNum(this.hashVal));
    }
    
    public void setHashVal(String val){
        this.hashVal = val;
    }
    
    public String getHashVal(){
        return this.hashVal;
    }
    
    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public Approximation getApp() {
        return app;
    }

    public Relocation getRel() {
        return rel;
    }

    public void addLat(double val) {
        lat = lat+val;
    }

    public void addLon(double val) {
        lon = lon+val;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
    
    
    public static String getStardadizeCoord(Double coord, int roundby){
    
        int i, j;
        
        
        //Estandarización de los valores de la coordenada
        String s = ""+abs(coord);
        j = s.indexOf(".");
        
        int max = Math.min(s.length(), roundby+j);
        
        s = s.substring(0, max);
        
        //Rellenar con 0s antes del punto
        for(i=0; i<3-j;i++)
            s= "0"+s;
        
        int t = s.length();
        //Relenar con 0s después del último decimal hasta tener 6 dígitos decimales, 3 enteros y el punto
        for(i=0; i<10-t;i++)
           s = s+"0";
        
        return s;
    }
    
    public double calcDist(Point p){
    
        return Math.sqrt(Math.pow(this.getLat() - p.getLat(), 2)+Math.pow(this.getLon() - p.getLon(), 2))/DEGRESS_TO_MT;
        
    }
    
    public void cleanPoint(int roundby){
        
        String sLat = Point.getStardadizeCoord(getLat(), roundby);        
        String sLon = Point.getStardadizeCoord(getLon(), roundby);
        int factLat = 1;
        int factLon = 1;
        
        if(getLat() < 0){
            factLat = -1; 
        }
        
        if(getLon() < 0){
            factLon = -1;
        }
        
        lat = factLat*Double.parseDouble(sLat);
        lon = factLon*Double.parseDouble(sLon);
        
        
    }
    
    public double calcDist(double lat, double lon){
    
        double dist=0;
        
        dist = Math.sqrt(Math.pow(lat - this.lat, 2) + Math.pow(lon - this.lon, 2));
        
        return dist;
        
    }
    
}

