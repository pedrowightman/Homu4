/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hom4u;

import java.util.ArrayList;
import static hom4u.Geohash.getNumDigitsByPrecision;

/**
 * 
 * @author hp
 */
public class Experiment2 extends Experiment{

    ArrayList<Double> radii;

    public Experiment2(int scale, TypeExperiment type) {
        this(scale, 0, 0, type);
    }
    
    public Experiment2(int scale, int correction, TypeExperiment type) {
        this(scale, correction, 0, type);
    }
    
    public Experiment2(int scale, int correction, long distance, TypeExperiment type) {
         this("geohashval2", scale, correction, distance, type);
    }
    
    public Experiment2(int scale, int correction, long distance, TypeExperiment type, Point location, ArrayList<String> hashValues, ArrayList<Long> hashNumValues) {
         this("geohashval2", scale, correction, distance, type, location, hashValues, hashNumValues);
    }
    
    public Experiment2(String tableName, int scale, int correction, long distance, TypeExperiment type) {
        this(tableName, scale, correction, distance, type, null, null, null);
    }
    
    public Experiment2(String tableName, int scale, int correction, long distance, TypeExperiment type, Point location, ArrayList<String> hashValues, ArrayList<Long> hashNumValues) {
        super(tableName, scale, correction, distance, type, location, hashValues, hashNumValues);
    }

    public Experiment2(String tableName, int scale, int correction, long distance, TypeExperiment type, Point location, ArrayList<String> hashValues, ArrayList<Long> hashNumValues, ArrayList<Double> radii) {
        super(tableName, scale, correction, distance, type, location, hashValues, hashNumValues);
        this.radii = radii;
    }
    
    public String getStatement(){
    
        String s = null;
        int cont=0;
        
        switch(type){
        
            case AHGSUBSTR:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash, count("+tableName+".id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom, poi.geohash\n" +
                "	from poi\n" +
                ") as poi2, "+tableName+"\n" +
                "where poi2.id = "+tableName+".id and \n(";
                cont=0;
                for (String hashValue : hashValues) {
                    s = s+"substring("+tableName+".hashval,1,"+(scale+correction)+") = substring('"+hashValue+"',1,"+(scale+correction)+")";
                    if(cont < hashValues.size()-1)
                        s = s+"or \n";
                    cont++;
                }
                s = s +")and "+tableName+".relocation='NONE' and\n" +
                //"("+tableName+".scale = "+scale+" or "+tableName+".scale = 0)\n" +
                tableName+".scale = 0\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash;";
            break;
            
            
            case AHGDISTANCE:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lon, count(poi2.id)\n" +
                "from poi2\n" +
                "where \n(";
                cont=0;
                for (Long hashNumValue : hashNumValues) {
                    s = s+"(abs(poi2.numval-"+hashNumValue+"))< "+distance;
                    if(cont < hashValues.size()-1)
                        s = s+"or \n";
                    cont++;
                }
                s = s +")\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lon;";
                
                break;
                
           
            case HOMAHGSUBSTR:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash, count("+tableName+".id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom, poi.geohash\n" +
                "	from poi\n" +
                ") as poi2, "+tableName+"\n" +
                "where poi2.id = "+tableName+".id and \n" +
                "substring("+tableName+".hashval,1,"+(scale+correction)+") = substring('"+this.hashValues.get(0)+"',1,"+(scale+correction)+") and \n" +
                "("+tableName+".relocation='AHG' or "+tableName+".relocation='NONE') and\n" +
                "("+tableName+".scale = "+scale+" or "+tableName+".scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash;";
                
                break;
             
            
            case HOMAHGDISTANCE:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, count(homahg.id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom\n" +
                "	from poi\n" +
                ") as poi2, homahg\n" +
                "where poi2.id = homahg.id and \n" +
                "(abs(homahg.numval-"+this.hashNumValues.get(0)+"))< "+distance+" and \n" +
                "(homahg.scale = "+scale+" or homahg.scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng;";
                
                
                break;
           
                
            case GEOGRAPHICAL:
                double dist = Geohash.calcDiffGridCell(scale, false);
                
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lon, poi2.numval, poi2.hashval\n" +
                "from poi2 \n" +
                "where ST_DWithin(poi2.geom,ST_GeomFromEWKT('srid=4326;POINT("+location.getLon()+" "+location.getLat()+")'), "+dist+");";
  
                break;
                
            case HOMMIRRORINGSUBSTR:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash, count("+tableName+".id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom, poi.geohash\n" +
                "	from poi\n" +
                ") as poi2, "+tableName+"\n" +
                "where poi2.id = "+tableName+".id and \n" +
                "substring("+tableName+".hashval,1,"+(scale+correction)+") = substring('"+this.hashValues.get(0)+"',1,"+(scale+correction)+") and \n" +
                "("+tableName+".relocation='MIRRORING' or "+tableName+".relocation='NONE') and\n" +
                "("+tableName+".scale = "+scale+" or "+tableName+".scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash;";
                break;
                
            case HOMMIRRORINGDISTANCE:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, count(homcentroid.id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom\n" +
                "	from poi\n" +
                ") as poi2, homcentroid\n" +
                "where poi2.id = homcentroid.id and \n" +
                "(abs(homcentroid.numval-"+this.hashNumValues.get(0)+"))< "+distance+" and \n" +
                "(homcentroid.scale = "+scale+" or homcentroid.scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng;";
                
                break;
                
            
            case HOMSCALINGSUBSTR:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash, count("+tableName+".id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom, poi.geohash\n" +
                "	from poi\n" +
                ") as poi2, "+tableName+"\n" +
                "where poi2.id = "+tableName+".id and \n" +
                "substring("+tableName+".hashval,1,"+(scale+correction)+") = substring('"+this.hashValues.get(0)+"',1,"+(scale+correction)+") and \n" +
                "("+tableName+".relocation='SCALING' or "+tableName+".relocation='NONE') and\n" +
                "("+tableName+".scale = "+scale+" or "+tableName+".scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash;";
                break;
                
            case HOMSCALINGDISTANCE:
                                
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, count(homscale.id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom\n" +
                "	from poi\n" +
                ") as poi2, homscale\n" +
                "where poi2.id = homscale.id and \n" +
                "(abs(homscale.numval-"+this.hashNumValues.get(0)+"))< "+distance+" and \n" +
                "(homscale.scale = "+scale+" or homscale.scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng;";
                
                break;
        
                
            case HOMBMIRRORINGSUBSTR:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash, count("+tableName+".id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom, poi.geohash\n" +
                "	from poi\n" +
                ") as poi2, "+tableName+"\n" +
                "where poi2.id = "+tableName+".id and \n" +
                "substring("+tableName+".hashval,1,"+(scale+correction)+") = substring('"+this.hashValues.get(0)+"',1,"+(scale+correction)+") and \n" +
                "("+tableName+".relocation='BORDER_MIRRORING' or "+tableName+".relocation='NONE') and\n" +
                "("+tableName+".scale = "+scale+" or "+tableName+".scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng, poi2.geohash;";
                break;
                
            case HOMBMIRRORINGDISTANCE:
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lng, count(homborder.id)\n" +
                "from (\n" +
                "	select poi.id, poi.name, poi.lat, poi.lng, poi.geom\n" +
                "	from poi\n" +
                ") as poi2, homborder\n" +
                "where poi2.id = homborder.id and \n" +
                "(abs(homborder.numval-"+this.hashNumValues.get(0)+"))< "+distance+" and \n" +
                "(homborder.scale = "+scale+" or homborder.scale = 0)\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lng;";
                
                break;
                             
                
                /*
                HALL OF MIRRORS 4 U
                */
                case HOM2DISTANCE:
                
                s = "select poi2.id, poi2.name, poi2.lat, poi2.lon, count(poi2.id)\n" +
                "from poi2 \n" +
                "where \n(";
                cont=0;
                for (Long hashNumValue : hashNumValues) {
                    s = s+"(abs(poi2.numval-"+hashNumValue+"))< "+Math.round(radii.get(cont)*distance);
                    if(cont < hashValues.size()-1)
                        s = s+" or \n";
                    cont++;
                }
                s = s +")\n" +
                "group by poi2.id, poi2.name, poi2.lat, poi2.lon;";
                    
                    
                break;
            
            
                
        }
        
        return s;
        
    }
    
}
