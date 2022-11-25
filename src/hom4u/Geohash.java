/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hom4u;

import java.util.ArrayList;


/**
 *
 * @author hp
 */
public class Geohash {
     
    public static final int PRECISION = 9;
    public static final int APROXIMATION_PRECISION = 8;
    
        
    public static double calcDiffGridCell(int geoHashScale, boolean isLat){
        
        
        double numCellsLat = 180/Math.pow(2,Geohash.getNumDigitsByPrecision(geoHashScale, true));
        double numCellsLon = 360/Math.pow(2,Geohash.getNumDigitsByPrecision(geoHashScale, false));
        
        
        if(isLat)
            return numCellsLat;
        
        return numCellsLon;
        
    }
    
    
    public static String calcGeoHash(Point p){
    
        return calcGeoHash(p, p.getScale());
    }
        
    public static String calcGeoHash(Point p, int scale){
        
        StringBuilder sbBin = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        int numDigits, precision;
       
        String sLat = calcGeoHash(p.getLat(),scale, true);
        String sLon = calcGeoHash(p.getLon(),scale,false);
        
        
        //System.out.println("SLat "+sLat);
        //System.out.println("SLon "+sLon);
        
        numDigits = Math.max(sLat.length(),sLon.length());
        
        for (int i = 0; i < numDigits; i++) {
            if(i < sLon.length())
                sbBin.append(sLon.charAt(i));
            if(i < sLat.length())
                sbBin.append(sLat.charAt(i));
            
        }
        
        //The precision will be obtained only based on the default value
        precision = Math.max(scale, PRECISION);
        
        for (int i = 0; i < precision; i++) {
            sb.append(get32ghs(sbBin.toString().substring(i*5, (i+1)*5)));
        }
        
        
        return sb.toString();
        
    }
    
    public static String getBinCoordFromGeoHash(String s, boolean isLat){
    
        StringBuilder coord = new StringBuilder();
        
        int init=0;
        
        if(isLat){
            init=1;
        }
        
        for (int j = init; j < s.length(); j++) {
            if(j%2 == init)
                coord.append(s.charAt(j));
        }
        
        return coord.toString();
        
    }
    
    public static double getCoordFromBinCoord(String s, boolean isLat){
    
        double coord = 0;
        double coordMid = 90;
        
        if(isLat){
            coordMid = 45;
        }
        
        for (int j = 0; j < s.length(); j++) {
            if(s.charAt(j) == '0'){
                coord -= coordMid;
            }else{
                coord += coordMid;
            }
            coordMid/=2;
        }
        
        return coord;
        
    }
    
    //String needs to hace the GeoHash in coded Base32 format
    public static Point calcPointFromGeoHash(String s){
        return calcPointFromGeoHash(s, PRECISION);
    }
        
    
    public static Point calcPointFromGeoHash(String s, int sc){
        Point p;
        StringBuilder binary = new StringBuilder();
        String lats;
        String lons;
        double lat;
        double lon;
        
        for (int i = 0; i < s.length(); i++) {
            binary.append(getReverse32ghsBinary(s.charAt(i)));
        }
        
        //System.out.println("Binary "+binary);
        
        lats = getBinCoordFromGeoHash(binary.toString(),true);
        lons = getBinCoordFromGeoHash(binary.toString(),false);
        
        //System.out.println("SLat "+lats);
        //System.out.println("SLat "+lons);
        
        lat = getCoordFromBinCoord(lats, true);
        lon = getCoordFromBinCoord(lons, false);
        
        p = new Point(s, lat, lon, Approximation.NONE, Relocation.NONE, s.length());
        p.setHashVal(s);
        return p;
    }
    
        
    public static long calcGeoHashNum(Point p){
        return calcGeoHashNum(calcGeoHash(p), p.getScale());
    }
    
    public static long calcGeoHashNum(String s){
        return calcGeoHashNum(s, 0);
    }
    
    public static long calcGeoHashNum(String s,  int scale){
    
        long sum=0;
        
        int precision = 1;
        
        
        //The precision will be obtained only based on the default value
        precision = Math.max(scale, PRECISION);
        
        
        /*If scale is larger that PRECISION it is possible that there will be points with different 
        numerical length.. make sure that the PRECISION is always the largest number in the 
        experiment*/
        s = standardizeGeoHash(s, precision); //Unify extension to the length of precision
        
        for (int i = precision-1; i >= 0; i--) {
            sum += getReverse32ghs(s.charAt(i))*Math.pow(32, precision-i-1);
        }
        
        return sum;
        
    }
    
    public static String standardizeGeoHash(String s){
        return standardizeGeoHash(s,PRECISION);
    }
    
    public static String standardizeGeoHash(String s, int precision){
        StringBuilder sb = new StringBuilder(s);
        
        for (int i = s.length(); i < precision; i++) {
            sb.append("0");
        }
        
        return sb.toString();
    }
    
    public static String reduceGeoHashPrecision(String s, int precision){
        StringBuilder sb = new StringBuilder(s);
        
        sb.setCharAt(precision,'s'); //Locate the point at the center of the quadrant
        for (int i = precision+1; i < s.length(); i++) {
            sb.setCharAt(i,'0');
        }
        
        return sb.toString();
    }
    
    public static Point reducePrecision(Point p, int precision){
        
        String geohashCode = standardizeGeoHash(p.getHashVal(), PRECISION);
        String geohashCodeRed = reduceGeoHashPrecision(geohashCode, precision);
        Point temp = calcPointFromGeoHash(geohashCodeRed);
        temp.calcSetNumVal();
        
        temp.setApp(p.getApp());
        temp.setRel(p.getRel());
        temp.setRadius(p.getRadius());
        temp.setScale(p.getScale());
        
        return temp;
    }
    
    
    public static char get32ghs(String s){
    
        char c='0';
        
        switch(s){
            case "00000":
                c='0';
                break;    
            case "00001":
                c='1';
                break;
            case "00010":
                c='2';
                break;
            case "00011":
                c='3';
                break;
            case "00100":
                c='4';
                break;    
            case "00101":
                c='5';
                break;
            case "00110":
                c='6';
                break;
            case "00111":
                c='7';
                break;
            case "01000":
                c='8';
                break;    
            case "01001":
                c='9';
                break;
            case "01010":
                c='b';
                break;
            case "01011":
                c='c';
                break;
            case "01100":
                c='d';
                break;    
            case "01101":
                c='e';
                break;
            case "01110":
                c='f';
                break;
            case "01111":
                c='g';
                break;
            case "10000":
                c='h';
                break;    
            case "10001":
                c='j';
                break;
            case "10010":
                c='k';
                break;
            case "10011":
                c='m';
                break;
            case "10100":
                c='n';
                break;    
            case "10101":
                c='p';
                break;
            case "10110":
                c='q';
                break;
            case "10111":
                c='r';
                break;
            case "11000":
                c='s';
                break;    
            case "11001":
                c='t';
                break;
            case "11010":
                c='u';
                break;
            case "11011":
                c='v';
                break;
            case "11100":
                c='w';
                break;    
            case "11101":
                c='x';
                break;
            case "11110":
                c='y';
                break;
            case "11111":
                c='z';
                break;
        }
        
        return c;
        
    }
    
    public static char get16ghs(String s){
    
        char c='0';
        
        switch(s){
            case "0000":
                c='0';
                break;    
            case "0001":
                c='1';
                break;
            case "0010":
                c='2';
                break;
            case "0011":
                c='3';
                break;
            case "0100":
                c='4';
                break;    
            case "0101":
                c='5';
                break;
            case "0110":
                c='6';
                break;
            case "0111":
                c='7';
                break;
            case "1000":
                c='8';
                break;    
            case "1001":
                c='9';
                break;
            case "1010":
                c='a';
                break;
            case "1011":
                c='b';
                break;
            case "1100":
                c='c';
                break;    
            case "1101":
                c='d';
                break;
            case "1110":
                c='e';
                break;
            case "1111":
                c='f';
                break;
        }
        
        return c;
        
    }
    
    public static String getBinaryFromCode(String code){
        StringBuilder s = new StringBuilder();
        
        for (int i = 0; i < code.length(); i++) {
            s.append(getReverse32ghsBinary(code.charAt(i)));
        }
        
        return s.toString();
    }
    
    public static String getReverse32ghsBinary(char s){
    
        String c="";
        
        switch(s){
            case '0':
                c="00000";
                break;    
            case '1':
                c="00001";
                break;
            case '2':
                c="00010";
                break;
            case '3':
                c="00011";
                break;
            case '4':
                c="00100";
                break;    
            case '5':
                c="00101";
                break;
            case '6':
                c="00110";
                break;
            case '7':
                c="00111";
                break;
            case '8':
                c="01000";
                break;    
            case '9':
                c="01001";
                break;
            case 'b':
                c="01010";
                break;
            case 'c':
                c="01011";
                break;
            case 'd':
                c="01100";
                break;    
            case 'e':
                c="01101";
                break;
            case 'f':
                c="01110";
                break;
            case 'g':
                c="01111";
                break;    
            case 'h':
                c="10000";
                break;    
            case 'j':
                c="10001";
                break;
            case 'k':
                c="10010";
                break;
            case 'm':
                c="10011";
                break;
            case 'n':
                c="10100";
                break;    
            case 'p':
                c="10101";
                break;
            case 'q':
                c="10110";
                break;
            case 'r':
                c="10111";
                break;
            case 's':
                c="11000";
                break;    
            case 't':
                c="11001";
                break;
            case 'u':
                c="11010";
                break;
            case 'v':
                c="11011";
                break;
            case 'w':
                c="11100";
                break;    
            case 'x':
                c="11101";
                break;
            case 'y':
                c="11110";
                break;
            case 'z':
                c="11111";
                break;
        }
        
        return c;
        
    }
    
    
    public static int getReverse32ghs(char c){
    
        int n=0;
        
        switch(c){
            case '0':
                n=0;
                break;    
            case '1':
                n=1;
                break;
            case '2':
                n=2;
                break;
            case '3':
                n=3;
                break;
            case '4':
                n=4;
                break;    
            case '5':
                n=5;
                break;
            case '6':
                n=6;
                break;
            case '7':
                n=7;
                break;
            case '8':
                n=8;
                break;    
            case '9':
                n=9;
                break;
            case 'b':
                n=10;
                break;
            case 'c':
                n=11;
                break;
            case 'd':
                n=12;
                break;    
            case 'e':
                n=13;
                break;
            case 'f':
                n=14;
                break;
            case 'g':
                n=15;
                break;
            case 'h':
                n=16;
                break;    
            case 'j':
                n=17;
                break;
            case 'k':
                n=18;
                break;
            case 'm':
                n=19;
                break;
            case 'n':
                n=20;
                break;    
            case 'p':
                n=21;
                break;
            case 'q':
                n=22;
                break;
            case 'r':
                n=23;
                break;
            case 's':
                n=24;
                break;    
            case 't':
                n=25;
                break;
            case 'u':
                n=26;
                break;
            case 'v':
                n=27;
                break;
            case 'w':
                n=28;
                break;    
            case 'x':
                n=29;
                break;
            case 'y':
                n=30;
                break;
            case 'z':
                n=31;
                break;
        }
        
        return n;
        
    }
        
    public static int getNumDigitsByPrecision(boolean isLat){
        return getNumDigitsByPrecision(PRECISION, isLat);
    }
    
    public static int getNumDigitsByPrecision(int precision, boolean isLat){
    
        int n=0;
        
        if(isLat){
            switch(precision){
                case 1:
                    n = 2;
                    break;
                case 2:
                    n = 5;
                    break;
                case 3:
                    n = 7;
                    break;
                case 4:
                    n = 10;
                    break;
                case 5:
                    n = 12;
                    break;
                case 6:
                    n = 15;
                    break;
                case 7:
                    n = 17;
                    break;
                case 8:
                    n = 20;
                    break;
                case 9:
                    n = 22;
                    break;
                case 10:
                    n = 25;
                    break;
                case 11:
                    n = 27;
                    break;
                case 12:
                    n = 30;
                    break;
            }
        }else{
            switch(precision){
                case 1:
                    n = 3;
                    break;
                case 2:
                    n = 5;
                    break;
                case 3:
                    n = 8;
                    break;
                case 4:
                    n = 10;
                    break;
                case 5:
                    n = 13;
                    break;
                case 6:
                    n = 15;
                    break;
                case 7:
                    n = 18;
                    break;
                case 8:
                    n = 20;
                    break;
                case 9:
                    n = 23;
                    break;
                case 10:
                    n = 25;
                    break;
                case 11:
                    n = 28;
                    break;
                case 12:
                    n = 30;
                    break;
            }
        }
        
        return n;
    }
    
    
    public static int getPrecisionByNumDigits(int numDigits, boolean isLat){
    
        int n=0;
        
        if(isLat){
            switch(numDigits){
                case 2:
                    n = 1;
                    break;
                case 5:
                    n = 2;
                    break;
                case 7:
                    n = 3;
                    break;
                case 10:
                    n = 4;
                    break;
                case 12:
                    n = 5;
                    break;
                case 15:
                    n = 16;
                    break;
                case 17:
                    n = 7;
                    break;
                case 20:
                    n = 8;
                    break;
                case 22:
                    n = 9;
                    break;
                case 25:
                    n = 10;
                    break;
                case 27:
                    n = 11;
                    break;
                case 30:
                    n = 12;
                    break;
            }
        }else{
            switch(numDigits){
                case 3:
                    n = 1;
                    break;
                case 5:
                    n = 2;
                    break;
                case 8:
                    n = 3;
                    break;
                case 10:
                    n = 4;
                    break;
                case 13:
                    n = 5;
                    break;
                case 15:
                    n = 6;
                    break;
                case 18:
                    n = 17;
                    break;
                case 20:
                    n = 8;
                    break;
                case 23:
                    n = 9;
                    break;
                case 25:
                    n = 10;
                    break;
                case 28:
                    n = 11;
                    break;
                case 30:
                    n = 12;
                    break;
            }
        }
        
        return n;
    }
    
    
    
    
    public static String calcGeoHash(double lat, boolean isLat){
        return calcGeoHash(lat, getNumDigitsByPrecision(isLat), isLat);
    }
    
   
    
    public static String calcGeoHash(double lat, int scale, boolean isLat){
    
        double max = 90;
        double min = -90;
        double mid = 0;
        int numDigits;
        
        //The num of digits will be obtained only based on the desired precision of Geohash
        numDigits = getNumDigitsByPrecision(Math.max(scale, PRECISION), isLat);
        
        
        if(!isLat){
            max = 180;
            min = -180;
        }
        
        
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < numDigits; i++){
            
            if(lat > mid){
                sb.append("1");
                min = mid;
                mid = (mid+max)/2;
            }else{
                sb.append("0");
                max = mid;
                mid = (mid+min)/2;
            }
            
        }
        
        return sb.toString();
        
    }
    
    
    public static Point calculateMainCentroide(Point point, int scale){
                
        String s = calcGeoHash(point, scale);

        s = s.substring(0,scale);
        
        Point centroideMain = Geohash.calcPointFromGeoHash(s, scale);
        
        return centroideMain;
    
    }
    
    private static Point calculatePoint(Point point, Approximation app, Relocation rel, int scale) {
        
        return calculatePoint(point,app, rel, scale, calculateMainCentroide(point, scale), false);
    }
    
    private static Point calculatePoint(Point point, Approximation app, Relocation rel, int scale, boolean isCentroid) {
        
        return calculatePoint(point,app, rel, scale, calculateMainCentroide(point, scale), isCentroid);
    }
    
    
    private static Point calculatePoint(Point point, Approximation app, Relocation rel, int scale, Point mainCentroide) {
        return calculatePoint(point,app, rel, scale, mainCentroide, false);
    }
    
    static Point calculatePoint(Point point, Approximation app, Relocation rel, int scale, Point mainCentroide, boolean isCentroid) {
        
        Point p = new Point(point, app, rel, scale);
        
        Point refPoint = null; 
        
        double diffLat=0, diffLon=0;
        
        if(!isCentroid){
            //If approximation is none, the point will be stored as original
            if(app != Approximation.NONE){

                switch(rel){

                    case AHG:
                        //Scaling calcula la diferencia entre el punto original y el centroide más cercano,
                        // le suma la diferencia al punto dividida entre 2, lo que sirve como una proyección
                        // del punto como si los cuadrantes fueran de tamaño 2x2, y fueran escalados nuevamente a 1x1
                        //p = calculateApproxCentroide(point, app, scale);
                        p = calcNextQuadrant(point, app, scale);

                        break;
                    
                    
                    case MIRRORING:
                        //Scaling calcula la diferencia entre el punto original y el centroide más cercano,
                        // le suma la diferencia al punto dividida entre 2, lo que sirve como una proyección
                        // del punto como si los cuadrantes fueran de tamaño 2x2, y fueran escalados nuevamente a 1x1
                        
                        p = calculateCentroidMirror(point, mainCentroide, app, scale);
                        
                        
                        break;
                        
                    case SCALING:
                        //Scaling calcula la diferencia entre el punto original y el centroide más cercano,
                        // le suma la diferencia al punto dividida entre 2, lo que sirve como una proyección
                        // del punto como si los cuadrantes fueran de tamaño 2x2, y fueran escalados nuevamente a 1x1
                        //refPoint = calculateApproxCentroide(mainCentroide, app, scale);
                        
                        refPoint = calcNextQuadrant(mainCentroide, app, scale);

                        diffLat = (refPoint.getLat() - point.getLat())/1.4; 
                        diffLon = (refPoint.getLon() - point.getLon())/1.4;
                        
                        p.addLat(diffLat);
                        p.addLon(diffLon);

                        break;
                        
                   

                    case BORDER_MIRRORING:
                        //Mirroring calcula la diferencia entre el punto original y el punto de contacto al cuadrande más cercano,
                        // le suma la diferencia absoluta al punto, lo que sirve como una proyección espejo
                        // del punto en el cuadrante vecino
                        
                        refPoint = calculateContactPoint(point, mainCentroide, app, scale);
                        
                        p = calculateMirrorPoint(point, refPoint, app);

                        break;
                        
                    case HOM2:
                        //HOM2 opera como AHG pero proyecta los puntos sobre el borde y define radios diferentes para cada cuadrante
                        //para reducir el número de entradas de cada query
                        
                        p = calculateContactPoint(point, mainCentroide, app, scale);
                        
                        break;

                }


                p.cleanPoint(PRECISION);
            }
        }else{
            //Calculate centroid 
            Point p2 = calculateApproxCentroide(mainCentroide, app, scale);
            p2.setHashVal(Geohash.calcGeoHash(p2));
            p2.setNumVal(""+Geohash.calcGeoHashNum(p2));
            return p2;
            
        }
        
        /*p.setHashVal(Geohash.calcGeoHash(p));
        p.setNumVal(""+Geohash.calcGeoHashNum(p));*/
        p.calcSetHashVal();
        p.calcSetNumVal();
        p.setApp(app);
        return p;
    }
        
    static ArrayList<Point> calculatePointsHOM2Radii_1_5_1_5(Point p, int scale){
    
        //Point mc = Geohash.calculateMainCentroide(p, scale);
        
        ArrayList<Point> puntos = calculatePoints(p, Approximation.KING, Relocation.HOM2, scale, false);
        
        //double dist = Geohash.calcDiffGridCell(scale, false);
        
        for (Point punto : puntos) {
            
            if(punto.app == Approximation.DOWN || punto.app == Approximation.LEFT
               || punto.app == Approximation.RIGHT || punto.app == Approximation.UP){
                punto.setRadius(0.619656837);
            }else{
                punto.setRadius(0.366025404);
            }
        }
        
        return puntos;
        
    }
    
    static ArrayList<Point> calculatePointsHOM2Radii_1_75_1_75(Point p, int scale){
    
        //Point mc = Geohash.calculateMainCentroide(p, scale);
        
        ArrayList<Point> puntos = calculatePoints(p, Approximation.KING, Relocation.HOM2, scale, false);
        
        //double dist = Geohash.calcDiffGridCell(scale, false);
        
        for (Point punto : puntos) {
            
            if(punto.app == Approximation.UP || punto.app == Approximation.RIGHT){
                punto.setRadius(0.866025404);
            }else if(punto.app == Approximation.UPLEFT || punto.app == Approximation.DOWNRIGHT){
                punto.setRadius(0.411437828);
            }else if(punto.app == Approximation.UPRIGHT){
                punto.setRadius(0.718245837);
            }else if(punto.app == Approximation.DOWN || punto.app == Approximation.LEFT){
                punto.setRadius(0.661437828);
            }else if(punto.app == Approximation.DOWNLEFT){
                punto.setRadius(0);
            } 
        }
        
        return puntos;
        
    }
    
    static ArrayList<Point> calculatePointsHOM2Radii_1_125_1_5(Point p, int scale){
    
        //Point mc = Geohash.calculateMainCentroide(p, scale);
        
        ArrayList<Point> puntos = calculatePoints(p, Approximation.KING, Relocation.HOM2, scale, false);
        
        //double dist = Geohash.calcDiffGridCell(scale, false);
        
        for (Point punto : puntos) {
            
            if(punto.app == Approximation.UP || punto.app == Approximation.DOWN){
                punto.setRadius(0.855442041);
            }else if(punto.app == Approximation.UPLEFT || punto.app == Approximation.DOWNLEFT){
                punto.setRadius(0.741025404);
            }else if(punto.app == Approximation.RIGHT){
                punto.setRadius(0.484122918);
            }else if(punto.app == Approximation.LEFT){
                punto.setRadius(0.893934365);
            }else if(punto.app == Approximation.UPRIGHT || punto.app == Approximation.DOWNRIGHT){
                punto.setRadius(0);
            } 
        }
        
        return puntos;
        
    }

static ArrayList<Point> calculatePointsHOM2Radii_1_625_1_875(Point p, int scale){
    
        //Point mc = Geohash.calculateMainCentroide(p, scale);
        
        ArrayList<Point> puntos = calculatePoints(p, Approximation.KING, Relocation.HOM2, scale, false);
        
        //double dist = Geohash.calcDiffGridCell(scale, false);
        
        for (Point punto : puntos) {
            
            if(punto.app == Approximation.UP){
                punto.setRadius(0.905797335);
            }else if(punto.app == Approximation.UPLEFT){
                punto.setRadius(0.65562475);
            }else if(punto.app == Approximation.RIGHT){
                punto.setRadius(0.88177821);
            }else if(punto.app == Approximation.LEFT){
                punto.setRadius(0.78062475);
            }else if(punto.app == Approximation.UPRIGHT){
                punto.setRadius(0.802024811);
            }else if(punto.app == Approximation.DOWN){
                punto.setRadius(0.484122918);
            }else if(punto.app == Approximation.DOWNLEFT){
                punto.setRadius(0);
            }else if(punto.app == Approximation.DOWNRIGHT){
                punto.setRadius(0.109122918);
            }
        }
        
        return puntos;
        
    }    
    
    
    public static Point calculateContactPoint(Point point, Point mainCentroide, Approximation app, int scale) {
        
        Point contactPoint = calcNextQuadrant(mainCentroide, app, scale, 0.5);
        
        switch(app){
        
            case UP:
            case DOWN:
                //Longitud of the original point remains, only latitude will change
                contactPoint.setLon(point.getLon());
                
                break;
            
            case RIGHT:
            case LEFT:
                //Latitude of the original point remains, only longitude will change
                contactPoint.setLat(point.getLat());
                break;
                
        }

        
        return contactPoint;
        
        
    }
    
    
    public static ArrayList<Point> calculatePoints(Point point, Approximation app, Relocation rel, int scale, boolean isCentroid) {
        
        double dist = Geohash.calcDiffGridCell(scale, false);
        return calculatePoints(point, app, rel, scale, isCentroid, dist);
    
    }
    
    
    
    public static ArrayList<Point> calculatePoints(Point point, Approximation app, Relocation rel, int scale, boolean isCentroid, double distance) {
    
        ArrayList<Point> puntos = new ArrayList();
        Point mc;
        
        switch(app){
        
            case KING:
                puntos.add(calculatePoint(point,Approximation.DOWNLEFT, rel, scale, isCentroid));
                puntos.add(calculatePoint(point,Approximation.DOWNRIGHT, rel, scale, isCentroid));
                puntos.add(calculatePoint(point,Approximation.UPLEFT, rel, scale, isCentroid));
                puntos.add(calculatePoint(point,Approximation.UPRIGHT, rel, scale, isCentroid));
                
            case TOWER:
                puntos.add(calculatePoint(point,Approximation.DOWN, rel, scale, isCentroid));
                puntos.add(calculatePoint(point,Approximation.RIGHT, rel, scale, isCentroid));
                puntos.add(calculatePoint(point,Approximation.LEFT, rel, scale, isCentroid));
                puntos.add(calculatePoint(point,Approximation.UP, rel, scale, isCentroid));                
                break;
            
                
            case ROUND:
                mc = calculateMainCentroide(point, scale);
                for (Approximation a : getAllApprox(point, scale)) {
                    puntos.add(calculatePoint(point,a, rel, scale, mc));                
                }
                
                break;
            
            case ROUND2:
                mc = calculateMainCentroide(point, scale);
                for (Approximation a : getAllApprox2(point, scale, distance)) {
                    puntos.add(calculatePoint(point,a , rel, scale, mc));                
                }
                
                break;
                
        }
        
        
        
        return puntos;
    }
    
    public static ArrayList<Point> calculateAproxCentroids(Point point, Approximation app, int scale) {
    
        ArrayList<Point> puntos = new ArrayList();
        
        switch(app){
        
            case KING:
                puntos.add(calculateApproxCentroide(point,Approximation.DOWNLEFT, scale));
                puntos.add(calculateApproxCentroide(point,Approximation.DOWNRIGHT, scale));
                puntos.add(calculateApproxCentroide(point,Approximation.UPLEFT, scale));
                puntos.add(calculateApproxCentroide(point,Approximation.UPRIGHT, scale));
                
            case TOWER:
                puntos.add(calculateApproxCentroide(point,Approximation.DOWN, scale));
                puntos.add(calculateApproxCentroide(point,Approximation.RIGHT, scale));
                puntos.add(calculateApproxCentroide(point,Approximation.LEFT, scale));
                puntos.add(calculateApproxCentroide(point,Approximation.UP, scale));               
                break;
            
                
            case ROUND:
                for (Approximation a : getAllApprox(point, scale)) {
                    puntos.add(calculateApproxCentroide(point,a, scale));                
                }
                
                break;
                
        }
        
        
        
        return puntos;
    }
    
    public static ArrayList<Approximation> getAllApprox(Point point, int scale){
    
        Approximation v;
        Approximation h;
        Approximation d;
        ArrayList<Approximation> apps = new ArrayList();
        
        Point centroide = Geohash.calculateMainCentroide(point, scale);
        boolean latPos = true;
        boolean lonPos = true;
        
        double diffLat = Geohash.calcDiffGridCell(scale, true);
        double diffLon = Geohash.calcDiffGridCell(scale, false);
        
        if(Math.abs(point.getLat()-centroide.getLat()) < diffLat){
            v = Approximation.UP;
        }else{
            v = Approximation.DOWN;
        }
        
        if(Math.abs(point.getLon()-centroide.getLon()) < diffLon){
            h = Approximation.RIGHT;
        }else{
            h = Approximation.LEFT;
        }
        
        
        if(v == Approximation.UP){
            if(h == Approximation.LEFT){
                d = Approximation.UPLEFT;
            }else{
                d = Approximation.UPRIGHT;
            }
        }else{
            if(h == Approximation.LEFT){
                d = Approximation.DOWNLEFT;
            }else{
                d = Approximation.DOWNRIGHT;
            }
        }
        
        apps.add(v);
        apps.add(h);
        apps.add(d);
        
        
        return apps;
    }
    
    public static ArrayList<Approximation> getAllApprox2(Point point, int scale, double distance){
    
        ArrayList<Approximation> apps = new ArrayList();
        
        ArrayList<Approximation> appsTest = new ArrayList();
        
        appsTest.add(Approximation.DOWN);
        appsTest.add(Approximation.UP);
        appsTest.add(Approximation.LEFT);
        appsTest.add(Approximation.RIGHT);
        appsTest.add(Approximation.UPLEFT);
        appsTest.add(Approximation.UPRIGHT);
        appsTest.add(Approximation.DOWNLEFT);
        appsTest.add(Approximation.DOWNRIGHT);
        
        Point contactPoint;
        Point centroide = Geohash.calculateMainCentroide(point, scale);
        
        for(Approximation a: appsTest){
            contactPoint = calculateContactPoint(point, centroide, a, scale);
            
            if(contactPoint.calcDist(point) < distance){
                apps.add(a);
            }
            
        }
        
        return apps;
    }
    
    //Scale debe tener el número del dígito decimal con el que se quiere trabajar
    // debe tener valores entre 0 y 5 para los dígitos decimales, y de -1 a -3 para los números enteros
    public static Approximation getVerticalApprox(Point point, int scale){
    
        String sLat = Point.getStardadizeCoord(point.getLat(), PRECISION);
        scale--;
        //Ajuste de la escala para no incluir el punto
        if(scale <= 0)
            scale--;
        
        boolean signPos = false;
        if(point.getLat() >= 0)
            signPos = true;
        
        if(signPos){
            if(sLat.charAt(scale+4) > '4'){
                return Approximation.UP;
            }else{
                return Approximation.DOWN;
            }
        }else{
            if(sLat.charAt(scale+4) < '5'){
                return Approximation.UP;
            }else{
                return Approximation.DOWN;
            }
        }
        
    }
    
    public static Approximation getHorizontalApprox(Point point, int scale){
    
        String sLon = Point.getStardadizeCoord(point.getLon(), PRECISION);
        scale--;
        //Ajuste de la escala para no incluir el punto
        if(scale <= 0)
            scale--;
        
         boolean signPos = false;
        if(point.getLon() >= 0)
            signPos = true;
        
        if(signPos){
            if(sLon.charAt(scale+4) > '4'){
                return Approximation.RIGHT;
            }else{
                return Approximation.LEFT;
            }
        }else{
            if(sLon.charAt(scale+4) < '5'){
                return Approximation.RIGHT;
            }else{
                return Approximation.LEFT;
            }
        }


    }
    
    private static Point calculateApproxCentroide(Point mainCentroide, Approximation app, int scale){
 
        Point centroide = new Point(mainCentroide, app, mainCentroide.getRel(), scale);
      
              
        double factLat = calcDiffGridCell(scale, true);
        double factLon = calcDiffGridCell(scale, false);
        
        
        switch(app){
        
            case UP:
                centroide.addLat(factLat);
                break;
                
            case DOWN:
                centroide.addLat(-factLat);
                break;
            
            case RIGHT:
                centroide.addLon(factLon);
                break;
                
            case LEFT:
                centroide.addLon(-factLon);
                break;
        
            case UPRIGHT:
                centroide.addLat(factLat);
                centroide.addLon(factLon);
                break;
                
            case DOWNRIGHT:
                centroide.addLat(-factLat);
                centroide.addLon(factLon);
                break;
            
            case UPLEFT:
                centroide.addLat(factLat);
                centroide.addLon(-factLon);
                break;
                
            case DOWNLEFT:
                centroide.addLat(-factLat);
                centroide.addLon(-factLon);
                break;
                
        }
        
        centroide.calcSetHashVal();
        centroide.calcSetNumVal();
        
        centroide = Geohash.calculateMainCentroide(centroide, scale);
        
        return centroide;
    
    }
    
    private static Point calculateCentroidMirror(Point point, Point mainCentroide, Approximation app, int scale){
 
        
        Point p = Geohash.calcNextQuadrant(mainCentroide, app, scale);

        
        double diffLat = Math.abs(mainCentroide.getLat() - point.getLat()); 
        double diffLon = Math.abs(mainCentroide.getLon() - point.getLon());


        switch(app){

            case UP:
                p.addLat(-diffLat);
                break;

            case DOWN:
                p.addLat(diffLat);
                break;

            case RIGHT:
                p.addLon(-diffLon);
                break;

            case LEFT:
                p.addLon(diffLon);
                break;

            case UPRIGHT:
                p.addLat(-diffLat);
                p.addLon(-diffLon);
                break;

            case DOWNRIGHT:
                p.addLat(diffLat);
                p.addLon(-diffLon);
                break;

            case UPLEFT:
                p.addLat(-diffLat);
                p.addLon(diffLon);
                break;

            case DOWNLEFT:
                p.addLat(diffLat);
                p.addLon(diffLon);
                break;

        }
        
        
        return p;
    
    }
    
    private static Point calculateMirrorPoint(Point point, Point refPoint, Approximation app) {
        
        Point p = new Point(refPoint);
        
        double factLat = Math.abs(point.getLat() - refPoint.getLat());
        double factLon = Math.abs(point.getLon() - refPoint.getLon());
        
        
        
        switch(app){
        
            case UP:
                p.addLat(factLat);
                break;
                
            case DOWN:
                p.addLat(-factLat);
                break;
            
            case RIGHT:
                p.addLon(factLon);
                break;
                
            case LEFT:
                p.addLon(-factLon);
                break;
        
            case UPRIGHT:
                p.addLat(factLat);
                p.addLon(factLon);
                break;
                
            case DOWNRIGHT:
                p.addLat(-factLat);
                p.addLon(factLon);
                break;
            
            case UPLEFT:
                p.addLat(factLat);
                p.addLon(-factLon);
                break;
                
            case DOWNLEFT:
                p.addLat(-factLat);
                p.addLon(-factLon);
                break;
                
        }
        
        
        return p;
    }
    
    
    
    static ArrayList<Point> calculatePointsHOM2Radii(Point p, int scale){
    
        Point mc = Geohash.calculateMainCentroide(p, scale);
        
        ArrayList<Point> puntos = calculatePoints(p, Approximation.KING, Relocation.HOM2, scale, false);
                
        Point origin = Geohash.calculateContactPoint(p, mc, Approximation.DOWNLEFT, scale);
        
        double dist = Geohash.calcDiffGridCell(scale, true);
        
        double tempLat = (p.getLat()-origin.getLat())/dist;
               
        dist = Geohash.calcDiffGridCell(scale, false);
        
        double tempLon = (p.getLon()-origin.getLon())/dist; 
        
        Pair normPoint = new Pair(tempLat, tempLon);
        double temp;
        for (Point punto : puntos) {
            if(null != punto.app){ 
                temp = calcRadius(normPoint, punto.app);
                if(temp > 0) {
                    temp += 0.5;
                }
                punto.setRadius(Math.min(temp,1));
            }
        }
        
        return puntos;
        
    }

    static double calcRadius(Pair p, Approximation quadrant){
        double rad = 0;

        /*
        
Cuadrante 1:
'31.3297212742473 x^{3} y^{3} - 38.0711107973306 x^{3} y^{2} + 
 5.79301276246633 x^{3} y - 0.237865280406682 x^{3} - 
 55.9180530254119 x^{2} y^{3} + 64.3356345882686 x^{2} y^{2} - 
 8.68951914369937 x^{2} y + 0.606797920610023 x^{2} + 
 23.6399549905477 x y^{3} - 23.1474559282443 x y^{2} - 
 2.41473507855972 \\cdot 10^{-13} x y - 0.5 x + 
 1.18624204102366 y^{3} - 3.22386578330366 y^{2} + 
 2.89650638123329 y + 0.131067359796659'
Cuadrante 2:
'- 1.32249766693349 \\cdot 10^{-12} x^{3} y^{3} + 
 1.30651045537888 \\cdot 10^{-12} x^{3} y^{2} - 
 1.51319901969932 x^{2} y^{3} + 1.5422120051426 x^{2} y^{2} + 
 1.51319901970063 x y^{3} - 1.54221200514389 x y^{2} + 
 1.24452387129493 y^{3} - 3.28345921290091 y^{2} +
 2.89650638123329 y + 0.141067359796659'
Cuadrante 3:
'- 31.3297212742515 x^{3} y^{3} + 38.071110797336 x^{3} y^{2} - 
 5.79301276246757 x^{3} y + 0.237865280406692 x^{3} + 
 38.071110797336 x^{2} y^{3} - 49.8776978037304 x^{2} y^{2} + 
 8.68951914370083 x^{2} y - 0.106797920610033 x^{2} -
 5.79301276246757 x y^{3} + 8.68951914370084 x y^{2} +
 0.237865280406692 y^{3} - 0.106797920610033 y^{2}'
Cuadrante 4:
'- 1.32205357772364 \\cdot 10^{-12} x^{3} y^{3} -
 1.51319901969932 x^{3} y^{2} + 1.51319901970063 x^{3} y +
 1.24452387129493 x^{3} + 1.30584432156411 \\cdot 10^{-12} x^{2} y^{3} +
 1.5422120051426 x^{2} y^{2} - 1.54221200514389 x^{2} y -
 3.28345921290091 x^{2} + 2.89650638123329 x + 0.141067359796659'
Cuadrante 5:
'31.3297212742473 x^{3} y^{3} - 55.9180530254119 x^{3} y^{2} +
 23.6399549905477 x^{3} y + 1.18624204102366 x^{3} -
 38.0711107973306 x^{2} y^{3} + 64.3356345882686 x^{2} y^{2} -
 23.1474559282443 x^{2} y - 3.22386578330366 x^{2} +
 5.79301276246633 x y^{3} - 8.68951914369937 x y^{2} -
 2.44249065417534 \\cdot 10^{-13} x y + 2.89650638123329 x -
 0.237865280406682 y^{3} + 0.606797920610023 y^{2} - 0.5 y +
 0.131067359796659'
Cuadrante 6:
'1.06670228205985 \\cdot 10^{-12} x^{3} y^{3} -
 2.1440627051561 \\cdot 10^{-12} x^{3} y^{2} +
 1.08801856413265 \\cdot 10^{-12} x^{3} y -
 1.06581410364015 \\cdot 10^{-14} x^{3} +
 1.51319901969984 x^{2} y^{3} - 2.99738505395641 x^{2} y^{2} +
 1.4551730488133 x^{2} y + 0.0290129854432715 x^{2} -
 1.51319901970089 x y^{3} + 2.99738505395853 x y^{2} -
 1.45517304881437 x y - 0.0290129854432608 x -
 1.24452387129495 y^{3} + 0.450112400983889 y^{2} -
 0.0631595693162512 y + 0.998638399423969'
Cuadrante 7:
'- 31.3297212742477 x^{3} y^{3} + 55.9180530254119 x^{3} y^{2} -
 23.6399549905472 x^{3} y - 1.18624204102369 x^{3} +
 55.9180530254119 x^{2} y^{3} - 103.418524487967 x^{2} y^{2} +
 47.7724090433976 x^{2} y + 0.334860339767345 x^{2} -
 23.6399549905472 x y^{3} + 47.7724090433976 x y^{2} -
 24.6249531151535 x y - 0.00750093769693039 x -
 1.18624204102369 y^{3} + 0.334860339767345 y^{2} -
 0.00750093769693039 y + 0.989949998749938'
Cuadrante 8:
'1.06670228205985 \\cdot 10^{-12} x^{3} y^{3} +
 1.51319901969984 x^{3} y^{2} - 1.51319901970089 x^{3} y -
 1.24452387129495 x^{3} - 2.1440627051561 \\cdot 10^{-12} x^{2} y^{3} -
 2.99738505395641 x^{2} y^{2} + 2.99738505395853 x^{2} y +
 0.450112400983889 x^{2} + 1.08801856413265 \\cdot 10^{-12} x y^{3} +
 1.4551730488133 x y^{2} - 1.45517304881437 x y -
 0.0631595693162512 x - 1.06581410364015 \\cdot 10^{-14} y^{3} +
 0.0290129854432715 y^{2} - 0.0290129854432608 y + 0.998638399423969'
        */
        double x = p.getLon(); // between 0 and 1
        double y = p.getLat(); // between 0 and 1
        
        switch(quadrant){
            case UPLEFT: // 1
                /*rad = 31.3297212742473*Math.pow(x,3)*Math.pow(y,3) - 38.0711107973306*Math.pow(x,3)*Math.pow(y,2) + 
 5.79301276246633*Math.pow(x,3)*y - 0.237865280406682*Math.pow(x,3) - 
 55.9180530254119*Math.pow(x,2)*Math.pow(y,3) + 64.3356345882686*Math.pow(x,2)*Math.pow(y,2) - 
 8.68951914369937*Math.pow(x,2)*y + 0.606797920610023*Math.pow(x,2) + 
 23.6399549905477*x*Math.pow(y,3) - 23.1474559282443*x*Math.pow(y,2) - 
 - 0.5*x + 1.18624204102366*Math.pow(y,3) - 3.22386578330366*Math.pow(y,2) + 
 2.89650638123329*y + 0.131067359796659;*/
                
                /*rad = 31.3297212742473*Math.pow(x,3)*Math.pow(y,3) - 55.9180530254119*Math.pow(x,3)*Math.pow(y,2) +
 23.6399549905477*Math.pow(x,3)*y + 1.18624204102366*Math.pow(x,3) -
 38.0711107973306*Math.pow(x,2)*Math.pow(y,3) + 64.3356345882686*Math.pow(x,2)*Math.pow(y,2) -
 23.1474559282443*Math.pow(x,2)*y - 3.22386578330366*Math.pow(x,2) +
 5.79301276246633*x*Math.pow(y,3) - 8.68951914369937*x*Math.pow(y,2) -
 + 2.89650638123329*x - 0.237865280406682*Math.pow(y,3) + 0.606797920610023*Math.pow(y,2) - 0.5*y +
 0.131067359796659;
                */
                
                rad = 31.3297212742473*Math.pow(x, 3)*Math.pow(y, 3) - 38.0711107973306*Math.pow(x, 3)*Math.pow(y, 2) + 
 5.79301276246633*Math.pow(x, 3)*y - 0.237865280406682 *Math.pow(x, 3) - 
 55.9180530254119*Math.pow(x, 2)*Math.pow(y, 3) + 64.3356345882686*Math.pow(x, 2)*Math.pow(y, 2) - 
 8.68951914369937*Math.pow(x, 2)*y + 0.606797920610023*Math.pow(x, 2) + 
 23.6399549905477*x*Math.pow(y, 3) - 23.1474559282443*x*Math.pow(y, 2) - 0.5*x + 1.18624204102366*Math.pow(y, 3) - 
 3.22386578330366*Math.pow(y, 2) + 2.89650638123329*y + 0.131067359796659;
                
                break;
                
            case UP: // 2
                rad = -1.51319901969932*Math.pow(x,2)*Math.pow(y,3) + 1.5422120051426*Math.pow(x,2)*Math.pow(y,2) + 
 1.51319901970063*x*Math.pow(y,3) - 1.54221200514389*x*Math.pow(y,2) + 
 1.24452387129493*Math.pow(y,3) - 3.28345921290091*Math.pow(y,2) +
 2.89650638123329*y + 0.141067359796659;
                break;
                
            case UPRIGHT: // 3
                rad = - 31.3297212742515*Math.pow(x,3)*Math.pow(y,3) + 38.071110797336*Math.pow(x,3)*Math.pow(y,2) - 
 5.79301276246757*Math.pow(x,3)*y + 0.237865280406692*Math.pow(x,3) + 
 38.071110797336*Math.pow(x,2)*Math.pow(y,3) - 49.8776978037304*Math.pow(x,2)*Math.pow(y,2) + 
 8.68951914370083*Math.pow(x,2)*y - 0.106797920610033*Math.pow(x,2) -
 5.79301276246757*x*Math.pow(y,3) + 8.68951914370084*x*Math.pow(y,2) +
 0.237865280406692*Math.pow(y,3) - 0.106797920610033*Math.pow(y,2);
                break;
                
            case RIGHT: // 4
                rad = - 1.51319901969932*Math.pow(x,3)*Math.pow(y,2) + 1.51319901970063*Math.pow(x,3)*y +
 1.24452387129493*Math.pow(x,3) + 1.5422120051426*Math.pow(x,2)*Math.pow(y,2) - 1.54221200514389*Math.pow(x,2)*y -
 3.28345921290091*Math.pow(x,2) + 2.89650638123329*x + 0.141067359796659;
                break;
                
            case DOWNRIGHT: // 5
                /*rad = 31.3297212742473*Math.pow(x,3)*Math.pow(y,3) - 55.9180530254119*Math.pow(x,3)*Math.pow(y,2) +
 23.6399549905477*Math.pow(x,3)*y + 1.18624204102366*Math.pow(x,3) -
 38.0711107973306*Math.pow(x,2)*Math.pow(y,3) + 64.3356345882686*Math.pow(x,2)*Math.pow(y,2) -
 23.1474559282443*Math.pow(x,2)*y - 3.22386578330366*Math.pow(x,2) +
 5.79301276246633*x*Math.pow(y,3) - 8.68951914369937*x*Math.pow(y,2) -
 + 2.89650638123329*x -
 0.237865280406682*Math.pow(y,3) + 0.606797920610023*Math.pow(y,2) - 0.5*y +
 0.131067359796659;*/
                
                rad = 31.3297212742473*Math.pow(x,3)*Math.pow(y,3) - 38.0711107973306*Math.pow(x,3)*Math.pow(y,2) + 
 5.79301276246633*Math.pow(x,3)*y - 0.237865280406682*Math.pow(x,3) - 
 55.9180530254119*Math.pow(x,2)*Math.pow(y,3) + 64.3356345882686*Math.pow(x,2)*Math.pow(y,2) - 
 8.68951914369937*Math.pow(x,2)*y + 0.606797920610023*Math.pow(x,2) + 
 23.6399549905477*x*Math.pow(y,3) - 23.1474559282443*x*Math.pow(y,2) - 
 - 0.5*x + 1.18624204102366*Math.pow(y,3) - 3.22386578330366*Math.pow(y,2) + 
 2.89650638123329*y + 0.131067359796659;
                break;
                
            case DOWN: // 6
                rad = 1.51319901969984*Math.pow(x,2)*Math.pow(y,3) - 2.99738505395641*Math.pow(x,2)*Math.pow(y,2) +
 1.4551730488133*Math.pow(x,2)*y + 0.0290129854432715*Math.pow(x,2) -
 1.51319901970089*x*Math.pow(y,3) + 2.99738505395853*x*Math.pow(y,2) -
 1.45517304881437*x*y - 0.0290129854432608*x -
 1.24452387129495*Math.pow(y,3) + 0.450112400983889*Math.pow(y,2) -
 0.0631595693162512*y + 0.998638399423969;
                break;
                
            case DOWNLEFT: // 7
                rad = - 31.3297212742477*Math.pow(x,3)*Math.pow(y,3) + 55.9180530254119*Math.pow(x,3)*Math.pow(y,2) -
 23.6399549905472*Math.pow(x,3)*y - 1.18624204102369*Math.pow(x,3) +
 55.9180530254119*Math.pow(x,2)*Math.pow(y,3) - 103.418524487967*Math.pow(x,2)*Math.pow(y,2) +
 47.7724090433976*Math.pow(x,2)* + 0.334860339767345*Math.pow(x,2) -
 23.6399549905472*x*Math.pow(y,3) + 47.7724090433976*x*Math.pow(y,2) -
 24.6249531151535*x*y - 0.00750093769693039*x -
 1.18624204102369*Math.pow(y,3) + 0.334860339767345*Math.pow(y,2) -
 0.00750093769693039*y + 0.989949998749938;
                
                break;
            
            case LEFT: // 8
                rad = 1.51319901969984*Math.pow(x,3)*Math.pow(y,2) - 1.51319901970089*Math.pow(x,3)*y -
 1.24452387129495*Math.pow(x,3) - 2.99738505395641*Math.pow(x,2)*Math.pow(y,2) + 2.99738505395853*Math.pow(x,2)*y +
 0.450112400983889*Math.pow(x,2) + 1.4551730488133*x*Math.pow(y,2) - 1.45517304881437*x*y -
 0.0631595693162512*x + 0.0290129854432715*Math.pow(y,2) - 0.0290129854432608*y + 0.998638399423969;
                break;
        }
        
        //Validando el valor de salida
        if(rad < 0 || rad > 1)
            rad = 0;
        
        return rad;
    }
    
    
    static double calcRadiusCross(Pair p){
    /*
        - 1.32249766693349 \\cdot 10^{-12} x^{3} y^{3} + 
 1.30651045537888 \\cdot 10^{-12} x^{3} y^{2} - 
 1.51319901969932 x^{2} y^{3} + 1.5422120051426 x^{2} y^{2} + 
 1.51319901970063 x y^{3} - 1.54221200514389 x y^{2} + 
 1.24452387129493 y^{3} - 3.28345921290091 y^{2} + 
 2.89650638123329 y + 0.141067359796659
        
        - 1.32249766693349 \\cdot 10^{-12} x^{3} y^{3} + 
 1.30651045537888 \\cdot 10^{-12} x^{3} y^{2} se asumen 0
        
        */
        
        double x = p.getLon();
        double y = p.getLat();
        
        double r = 1.51319901969932*Math.pow(x, 2)*Math.pow(y, 3) + 1.5422120051426*Math.pow(x, 2)*Math.pow(y, 2)+ 
 1.51319901970063*x*Math.pow(y, 3) - 1.54221200514389*x*Math.pow(y, 2) + 
 1.24452387129493*Math.pow(y, 3) - 3.28345921290091*Math.pow(y, 2) + 
 2.89650638123329*y + 0.141067359796659;
        
        return r;
    }
    
    static double calcRadiusX(Pair p){
    
        /*
        31.3297212742473 x^{3} y^{3} - 38.0711107973306 x^{3} y^{2} + 
 5.79301276246633 x^{3} y - 0.237865280406682 x^{3} - 
 55.9180530254119 x^{2} y^{3} + 64.3356345882686 x^{2} y^{2} - 
 8.68951914369937 x^{2} y + 0.606797920610023 x^{2} + 
 23.6399549905477 x y^{3} - 23.1474559282443 x y^{2} - 
 2.41473507855972 \\cdot 10^{-13} x y - 0.5 x + 1.18624204102366 y^{3} - 
 3.22386578330366 y^{2} + 2.89650638123329 y + 0.131067359796659
        
        2.41473507855972 \\cdot 10^{-13} x y se asume 0
        
        */
        
        double x = p.getLon();
        double y = p.getLat();
        
        double r = 31.3297212742473*Math.pow(x, 3)*Math.pow(y, 3) - 38.0711107973306*Math.pow(x, 3)*Math.pow(y, 2) + 
 5.79301276246633*Math.pow(x, 3)*y - 0.237865280406682 *Math.pow(x, 3) - 
 55.9180530254119*Math.pow(x, 2)*Math.pow(y, 3) + 64.3356345882686*Math.pow(x, 2)*Math.pow(y, 2) - 
 8.68951914369937*Math.pow(x, 2)*y + 0.606797920610023*Math.pow(x, 2) + 
 23.6399549905477*x*Math.pow(y, 3) - 23.1474559282443*x*Math.pow(y, 2) - 0.5*x + 1.18624204102366*Math.pow(y, 3) - 
 3.22386578330366*Math.pow(y, 2) + 2.89650638123329*y + 0.131067359796659;
        
        
        return r;
    }
    
    private static ArrayList<Pair> calcNormProjectedPoints(Point p, Point mc, Point origin, int scale) {
        ArrayList<Pair> points = new ArrayList();
        ArrayList<Pair> pointsNorm = new ArrayList();
        double normX=0, normY=0;
        
        //Calculate the distance with the centroid
        double distLat = Math.abs(p.getLat()-mc.getLat());
        double distLon = Math.abs(p.getLon()-mc.getLon());
        
        
        //Calculate the 4 projections of the point in the quadrants. The quadrants are based on the order of the calculation 12, 34, 56, 78
        points.add(new Pair(mc.getLat()+distLat,mc.getLon()-distLon)); //Q12 - NW N
        points.add(new Pair(mc.getLat()+distLat,mc.getLon()+distLon)); //Q34 - NE E
        points.add(new Pair(mc.getLat()-distLat,mc.getLon()+distLon)); //Q56 - SE S
        points.add(new Pair(mc.getLat()-distLat,mc.getLon()-distLon)); //Q78 - SW W
        
        //Obtain the normalized points, based on the origin
        for (Pair point : points) {
            pointsNorm.add(new Pair((point.getLat()-origin.getLat())/(Geohash.calcDiffGridCell(scale, false)), (point.getLon()-origin.getLon())/Geohash.calcDiffGridCell(scale, false)));
        }
        
        int i=0;
        /*double normX = Math.abs(p.getLon()-origin.getLon())/Geohash.calcDiffGridCell(scale, false);
        
        double normY = Math.abs(p.getLat()-origin.getLat())/Geohash.calcDiffGridCell(scale, true);
        */
        
        return pointsNorm;
    }
    
    
    public static Point calcNextQuadrant(Point p, Approximation app, int scale){
        return calcNextQuadrant(p, app, scale, 1);
    }
    
    
    public static Point calcNextQuadrant(Point p, Approximation app, int scale, double d2){

        double d = d2+0.001; //Include very small difference to guarantee that the projection is on the quandrant
        double numCellsLat = 180/Math.pow(2,Geohash.getNumDigitsByPrecision(scale, true));
        double numCellsLon = 360/Math.pow(2,Geohash.getNumDigitsByPrecision(scale, false));
        
        
        double latCell = p.getLat()/numCellsLat;
        double lonCell = p.getLon()/numCellsLon;
        
        
        switch(app){
            case UP:
                latCell+=d;
                break;
            case UPLEFT:
                latCell+=d;
                lonCell-=d;
                break;
            case UPRIGHT:
                latCell+=d;
                lonCell+=d;
                break;
            case DOWN:
                latCell-=d;
                break;
            case DOWNLEFT:
                latCell-=d;
                lonCell-=d;
                break;
            case DOWNRIGHT:
                latCell-=d;
                lonCell+=d;
                break;
            case LEFT:
                lonCell-=d;
                break;
            case RIGHT:
                lonCell+=d;
                break;
                
        }
        
        latCell*=numCellsLat;
        lonCell*=numCellsLon;
        
        Point newP = new Point(p);
        
        newP.setLat(latCell);
        newP.setLon(lonCell);
        newP.calcSetHashVal();
        newP.calcSetNumVal();
        
        return newP;
        
    }
    
    
}
