/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hom4u;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static hom4u.Geohash.standardizeGeoHash;

/**
 *
 * @author hp
 */
public class ExperimentDB {
    
    public static void main(String[] args) {

        int cont=0;
        String url = "jdbc:postgresql://localhost:5432/pruebamapir";
        String user = "postgres";
        String password = "password";

        int scale = 5;
        int correction = 0;
        long distance = 200_000; //Scale 5
        if(scale == 6)
            distance = 100_000; //Scale 6
        
        ArrayList<Experiment> experiments = new ArrayList();
        
        ArrayList<Integer> distances = new ArrayList();
        distances.add(1000);
        distances.add(3000);
        distances.add(6000);
        distances.add(10000);
        distances.add(30000);
        distances.add(60000);
        distances.add(100000);
        distances.add(300000);
        distances.add(600000);
        distances.add(1000000);
        distances.add(3000000);
        distances.add(60000000);
        
        ArrayList<String> tables = new ArrayList();
        tables.add("geohashval2");//ALL+SCALING+ROUND new border
        tables.add("geohashvalk2");//ALL+SCALING+KING new border
        
        
        int initScale = 5;
        int finalScale = 6;
        
        boolean summarized = true;
              
        Point location = new Point("",4.655659,-74.061467,Approximation.NONE, Relocation.NONE,scale); //Original
        //Point location = new Point("",4.6776598,-74.0527141,Approximation.NONE, Relocation.NONE,scale); //Parque 93
        //Point location = new Point("",4.707942,-74.1073024,Approximation.NONE, Relocation.NONE,scale); //Portal 80
        //Point location = new Point("",4.6591177,-74.0641296,Approximation.NONE, Relocation.NONE,scale); //Calle 72, Kra 7a
        //Point location = new Point("",4.617481,-74.137144,Approximation.NONE, Relocation.NONE,scale); //Plaza de las Américas, Kennedy
        
        

        //Locate the point in the middle of the centroid 1.5-1.5
        //location = Geohash.calculateMainCentroide(location, scale);
        
        
        /*
        //Locate the point in the middle of the centroid 1.75-1.75
        location = Geohash.calculateMainCentroide(location, scale);
        double disttemp = Geohash.calcDiffGridCell(scale, false);
        location.addLat(disttemp*0.25);
        location.addLon(disttemp*0.25);
        //Point mc = Geohash.calculateMainCentroide(location, scale);       
        //location = Geohash.calcNextQuadrant(mc, Approximation.UPRIGHT, scale, 0.25);
        //calculatePointsHOM2Radii_1_75_1_75
        */
        
        
        //Locate the point in the middle of the centroid 1.125-1.5
        //location = Geohash.calculateMainCentroide(location, scale);
        //double disttemp = Geohash.calcDiffGridCell(scale, false);
        //location.addLon(-disttemp*0.375);
        //calculatePointsHOM2Radii_1_125_1_5
        
        
        /*
        //Locate the point in the middle of the centroid 1.625-1.875
        location = Geohash.calculateMainCentroide(location, scale);
        double disttemp = Geohash.calcDiffGridCell(scale, false);
        location.addLat(disttemp*0.125);
        location.addLon(disttemp*0.375);
        //calculatePointsHOM2Radii_1_625_1_875
        */
        
        location.calcSetHashVal();
        location.calcSetNumVal();
        
        ArrayList<Point> points = new ArrayList();
        ArrayList<String> hashValues = new ArrayList();
        ArrayList<Long> hashNumValues = new ArrayList();
        
        
        ArrayList<Point> pointsHOM2 = new ArrayList();
        ArrayList<String> hashValuesHOM2 = new ArrayList();
        ArrayList<Long> hashNumValuesHOM2 = new ArrayList();
        ArrayList<Double> radiiHOM2 = new ArrayList();
        
        Point centroide;
        ArrayList<Point> points_centroids = new ArrayList();
        ArrayList<String> hashValues_centroids = new ArrayList();
        ArrayList<Long> hashNumValues_centroids = new ArrayList();
        
        
        Approximation app;
        for (String table : tables) {
            if(table.equals("geohashval2")){
                app = Approximation.ROUND;
            }else{
                app = Approximation.KING;
            }
            for (int i = initScale; i <= finalScale; i++) {
                points.clear();
                hashValues.clear();
                hashNumValues.clear();
                
                points.add(location);
                points.addAll(Geohash.calculatePoints(location, app, Relocation.AHG, i, false));
        
                //System.out.println("Puntos");
                for (Point point : points) {
                    point.calcSetHashVal();
                    point.calcSetNumVal();
                    hashValues.add(point.getHashVal());
                    hashNumValues.add(Long.parseLong(point.getNumVal()));
                }
                
                /*
                HOM2
                */
                pointsHOM2.clear();
                hashValuesHOM2.clear();
                hashNumValuesHOM2.clear();
                radiiHOM2.clear();
                
                pointsHOM2.add(location);
                pointsHOM2.addAll(Geohash.calculatePointsHOM2Radii_1_75_1_75(location, i));
                //pointsHOM2.addAll(Geohash.calculatePointsHOM2Radii(location, i));
                
                for (Point point : pointsHOM2) {
                    point.calcSetHashVal();
                    point.calcSetNumVal();
                    hashValuesHOM2.add(point.getHashVal());
                    hashNumValuesHOM2.add(Long.parseLong(point.getNumVal()));
                    radiiHOM2.add(point.getRadius());
                }
                 
                points_centroids.clear();
                hashValues_centroids.clear();
                hashNumValues_centroids.clear();
                
                
                /*This are used for the AHGSubstring*/
                //centroide = Geohash.calculateMainCentroide(location, i);
                points_centroids.add(location);
                points_centroids.addAll(Geohash.calculatePoints(location, app, Relocation.AHG, i, true));
        
                //System.out.println("Centroides");
                for (Point point : points_centroids) {
                    point.calcSetHashVal();
                    point.calcSetNumVal();
                    hashValues_centroids.add(point.getHashVal());
                    hashNumValues_centroids.add(Long.parseLong(point.getNumVal()));
                    //System.out.println(point);
                }
                //System.out.println("***************");
                
                
                experiments.add(new Experiment2(table, i, 0, 0, TypeExperiment.GEOGRAPHICAL, location, hashValues, hashNumValues));
                experiments.add(new Experiment2(table, i, correction, 0, TypeExperiment.AHGSUBSTR, location, hashValues_centroids, hashNumValues_centroids));
                experiments.add(new Experiment2(table, i, correction, 0, TypeExperiment.HOMAHGSUBSTR, location, hashValues, hashNumValues));
                experiments.add(new Experiment2(table, i, correction, 0, TypeExperiment.HOMMIRRORINGSUBSTR, location, hashValues, hashNumValues));
                experiments.add(new Experiment2(table, i, correction, 0, TypeExperiment.HOMBMIRRORINGSUBSTR, location, hashValues, hashNumValues));
                experiments.add(new Experiment2(table, i, correction, 0, TypeExperiment.HOMSCALINGSUBSTR, location, hashValues, hashNumValues));
                for (Integer dist : distances) {
                    experiments.add(new Experiment2(table, i, correction, dist, TypeExperiment.AHGDISTANCE, location, hashValues, hashNumValues));
                    experiments.add(new Experiment2(table, i, correction, dist, TypeExperiment.HOMAHGDISTANCE, location, hashValues, hashNumValues));
                    experiments.add(new Experiment2(table, i, correction, dist, TypeExperiment.HOMMIRRORINGDISTANCE, location, hashValues, hashNumValues));
                    experiments.add(new Experiment2(table, i, correction, dist, TypeExperiment.HOMBMIRRORINGDISTANCE, location, hashValues, hashNumValues));
                    experiments.add(new Experiment2(table, i, correction, dist, TypeExperiment.HOMSCALINGDISTANCE, location, hashValues, hashNumValues));
                    experiments.add(new Experiment2(table, i, correction, dist, TypeExperiment.HOM2DISTANCE, location, hashValuesHOM2, hashNumValuesHOM2, radiiHOM2));
                }
            }
        }
        
        
        
        long lStartTime, lEndTime, output;
        double latTemp, lonTemp, distTemp, distSum=0, distAVG;
        ArrayList<Double> tempDistances = new ArrayList();
 
        try{
            
        Connection con = DriverManager.getConnection(url, user, password);
        Statement st = con.createStatement();
                
        for (Experiment experiment : experiments) {
            
                lStartTime = System.nanoTime();

                ResultSet rs = st.executeQuery(experiment.getStatement());

                //end
                lEndTime = System.nanoTime();

                //time elapsed
                output = (lEndTime - lStartTime)/ 1000000;
                experiment.setOutputTime(output);
                
                tempDistances.clear();
                distSum=0;
                cont=0;
                
                
                
                System.out.println(experiment.getType()+" - "+experiment.getScale()+" - "+experiment.getDistance());
                while (rs.next()) {       
                    experiment.addValues(rs.getInt(1));
                    if(!summarized){
                        System.out.print(rs.getString(1)+",");
                        System.out.print(rs.getString(2)+",");
                        System.out.print(rs.getString(3)+",");
                        System.out.print(rs.getString(4)+",");
                        if(experiment.getType() != TypeExperiment.GEOGRAPHICAL2){
                            System.out.print(rs.getString(5)+",");
                            if(experiment.getType() == TypeExperiment.HOMAHGDISTANCEALL || experiment.getType() == TypeExperiment.GEOGRAPHICAL)
                                System.out.print(rs.getString(6)+",");
                        }
                        System.out.println("");
                    }
                    cont++;
                    
                    latTemp = Double.parseDouble(rs.getString(3));
                    lonTemp = Double.parseDouble(rs.getString(4));
                    
                    distTemp = location.calcDist(latTemp, lonTemp);
                    
                    tempDistances.add(distTemp);
                    
                    distSum += distTemp;
                
                }
                
                distAVG = distSum/cont;
                distSum = 0;
                for (Double tempDistance : tempDistances) {
                    distSum += Math.pow(tempDistance - distAVG, 2);
                }
                distSum = Math.sqrt(distSum/cont);

                System.out.println("Total de resultados: "+cont);
                System.out.println(experiment.getValuesString());
                System.out.println("Elapsed time in milliseconds: " + output);
                System.out.println("Distancia promedio de puntos: "+ distAVG);
                System.out.println("DevEst de la Distancia de puntos: "+ distSum);
                System.out.println("***********************");
            
                experiment.setDistanceAVG(distAVG);
                experiment.setDistanceSTDDEV(distSum);
                
        }
        
        } catch (SQLException ex) {

                ex.printStackTrace();
            }
            
        int geographicalIndex = 0;
        int tempScale=0;
        for (int i = 0; i < experiments.size(); i++) {
            tempScale = experiments.get(i).getScale();
            geographicalIndex = getGeographicalExperimentIndexByScale(experiments, tempScale);
            if(!summarized){
                System.out.println(experiments.get(i).getType()+","+experiments.get(i).getOutputTime()+" Values en Geo: "+experiments.get(i).getCommonValuesString(experiments.get(geographicalIndex).getValues(),true, summarized)+" Values not in Geo: "+experiments.get(i).getNotCommonValuesString(experiments.get(geographicalIndex).getValues(),true, summarized));
            }else{
                System.out.println(experiments.get(i).getVariables()+","+experiments.get(i).getCommonValuesString(experiments.get(geographicalIndex).getValues(),true, summarized)+",,"+experiments.get(i).getNotCommonValuesString(experiments.get(geographicalIndex).getValues(),true, summarized));
            }
        }
        
        double dist = Geohash.calcDiffGridCell(5, false);
        
        System.out.println("Geographical distance: "+dist);
        
        dist = Geohash.calcDiffGridCell(6, false);
        System.out.println("Geographical distance: "+dist);
        
        
    }

    private static int getGeographicalExperimentIndexByScale(ArrayList<Experiment> exps, int tempScale) {
        int val = 0;
        
        for (Experiment e : exps) {
            if(e.getType() == TypeExperiment.GEOGRAPHICAL && e.getScale() == tempScale)
                return val;
            val++;
        }
        
        return val;
    }
    
    
}
