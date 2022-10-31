/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hom4u;

import static java.lang.Math.*;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author pwightman
 */
public class Hom4u {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try{  
        File f = new File("foursquare.csv");
        
        String outputFile="geohash.csv";
                
        File f2 = new File(outputFile);
        String[] data;
        String dataLine;
        
        
        String id;
        double lat,lon;
        Point p; 
        
        Scanner myReader = new Scanner(f);
        FileWriter myWriter = new FileWriter(f2);
        int i=0;
        dataLine = myReader.nextLine();
        
        while (myReader.hasNextLine()) {
          dataLine = myReader.nextLine();
          
          data = dataLine.split(",");
        
          id = data[0];
          lat = Double.parseDouble(data[4]);
          lon = Double.parseDouble(data[5]);
          
          p = new Point(id, lat, lon);
          System.out.println("Processing point "+(i++)+": "+p);
          
          
          p.setNumVal(""+Geohash.calcGeoHashNum(p)); 
          p.setHashVal(""+Geohash.calcGeoHash(p));
          myWriter.append(p+"\n");
          
          //Experiment for Geohash queries based on AHG
          Approximation app = Approximation.ROUND;
           for (int scale = 4; scale < 8; scale++) {
            
            
            for (Point po : Geohash.calculatePoints(p, app, Relocation.AHG,scale,false)) {
                
                po.calcSetNumVal();
                po.calcSetHashVal(); 
                po.setName(id);
                po.setRel(Relocation.AHG);
                myWriter.append(po+"\n");
            }
            
            for (Point po : Geohash.calculatePoints(p, app, Relocation.MIRRORING,scale,false)) {
                
                po.calcSetNumVal();
                po.calcSetHashVal(); 
                
                po.setName(id);
                po.setRel(Relocation.MIRRORING);
                myWriter.append(po+"\n");
            }
            
            for (Point po : Geohash.calculatePoints(p, app, Relocation.SCALING,scale,false)) {
                
                po.calcSetNumVal();
                po.calcSetHashVal();
                po.setName(id);
                po.setRel(Relocation.SCALING);
                myWriter.append(po+"\n");
            }
            
            for (Point po : Geohash.calculatePoints(p, app, Relocation.BORDER_MIRRORING,scale,false)) {
               
                po.calcSetNumVal();
                po.calcSetHashVal(); 
                po.setName(id);
                po.setRel(Relocation.BORDER_MIRRORING);
                myWriter.append(po+"\n");
            }
        
        }
           
        }
        myReader.close();
        myWriter.close();
      } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
        
    }

    
    
}
