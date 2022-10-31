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
public abstract class Experiment {
    
    int scale;
    int correction;
    long distance;
    TypeExperiment type;
    ArrayList<Integer> values;
    String tableName;
    long outputTime;
    ArrayList<String> hashValues;
    ArrayList<Long> hashNumValues;
    Point location;
    double distanceAVG;
    double distanceSTDDEV;
    

    public Experiment(int scale, TypeExperiment type) {
        this(scale, 0, 0, type);
    }
    
    public Experiment(int scale, int correction, TypeExperiment type) {
        this(scale, correction, 0, type);
    }
    
    public Experiment(int scale, int correction, long distance, TypeExperiment type) {
         this("geohashval2", scale, correction, distance, type);
    }
    
    public Experiment(int scale, int correction, long distance, TypeExperiment type, Point location, ArrayList<String> hashValues, ArrayList<Long> hashNumValues) {
         this("geohashval2", scale, correction, distance, type, location, hashValues, hashNumValues);
    }
    
    public Experiment(String tableName, int scale, int correction, long distance, TypeExperiment type) {
        this(tableName, scale, correction, distance, type, null, null, null);
    }
    
    public Experiment(String tableName, int scale, int correction, long distance, TypeExperiment type, Point location, ArrayList<String> hashValues, ArrayList<Long> hashNumValues) {
        this.scale = scale;
        this.correction = correction;
        this.distance = distance;
        this.type = type;
        this.tableName = tableName;
        values = new ArrayList();
        
        if(location == null){
            this.location = new Point("",4.655659,-74.061467,Approximation.NONE, Relocation.NONE,scale);
        }else{
            this.location = new Point(location);
        }
        
        this.hashValues = new ArrayList();
        if(hashValues != null){
            this.hashValues.addAll(hashValues);
        }else{
            this.hashValues.add("d2g66xw14");
            this.hashValues.add("d2g66xk14");
            this.hashValues.add("d2g66z214");
            this.hashValues.add("d2g66xu14");
            this.hashValues.add("d2g66zb14");
            this.hashValues.add("d2g66xq14");
            this.hashValues.add("d2g66z814");
            this.hashValues.add("d2g66xs14");
            this.hashValues.add("d2g66xy14");
        }
            
        
        this.hashNumValues = new ArrayList();
        if(hashNumValues != null){
            this.hashNumValues.addAll(hashNumValues);
        }else{
            this.hashNumValues.add(13279173734436l);
            this.hashNumValues.add(13279173724196l);
            this.hashNumValues.add(13279173732388l);
            this.hashNumValues.add(13279173781540l);
            this.hashNumValues.add(13279173728292l);
            this.hashNumValues.add(13279173779492l);
            this.hashNumValues.add(13279173730340l);
            this.hashNumValues.add(13279173773348l);
            this.hashNumValues.add(13279173736484l);   
        }
    }

    
    
    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getOutputTime() {
        return outputTime;
    }

    public void setOutputTime(long outputTime) {
        this.outputTime = outputTime;
    }

    public double getDistanceAVG() {
        return distanceAVG;
    }

    public void setDistanceAVG(double distanceAVG) {
        this.distanceAVG = distanceAVG;
    }

    public double getDistanceSTDDEV() {
        return distanceSTDDEV;
    }

    public void setDistanceSTDDEV(double distanceSTDDEV) {
        this.distanceSTDDEV = distanceSTDDEV;
    }
    
    
    
    public int getScale(){
        return scale;
    }
    
    public String getTableName(){
        return tableName;
    }
    
    abstract public String getStatement();
    
    
    
    public void addValues(Integer i){
        values.add(i);
    }
    
    
    public ArrayList<Integer> getValues(){
        return values;
    }
    
    public int compareValues(ArrayList<Integer> vals){
        int cont=0;
        
        for (Integer value : values) {
            if(vals.contains(value))
                cont++;
        }
        
        return cont;
    }
    
    public ArrayList<Integer> getCommonValues(ArrayList<Integer> vals){
        ArrayList<Integer> valsCommon = new ArrayList();
        
        for (Integer value : vals) {
            if(values.contains(value))
                valsCommon.add(value);
        }
        
        return valsCommon;
    }
    
    public ArrayList<Integer> getNotCommonValues(ArrayList<Integer> vals){
        ArrayList<Integer> valsCommon = new ArrayList();
        
        for (Integer value : vals) {
            if(!values.contains(value))
                valsCommon.add(value);
        }
        
        return valsCommon;
    }
    
    public String getCommonValuesString(ArrayList<Integer> vals){
        return getCommonValuesString(vals, false, false);
    }
    
    
    public String getCommonValuesString(ArrayList<Integer> vals, boolean includeSize, boolean summarized){
        StringBuffer sb = new StringBuffer();
        ArrayList<Integer> valsCommon = getCommonValues(vals);
        
        if(!summarized){
            if(includeSize)
                sb.append("Total results: "+values.size()+" - Found in Geo: "+valsCommon.size()+" out of "+vals.size()+" - ");

            for (Integer value : valsCommon) {
                sb.append(value+",");
            }
        }else{
        
            if(includeSize)
                sb.append(""+values.size()+","+valsCommon.size()+","+vals.size()+"");
        }
        
        return sb.toString();
    }
    
    public String getNotCommonValuesString(ArrayList<Integer> vals){
        return getNotCommonValuesString(vals, false, false);
    }
    
    public String getNotCommonValuesString(ArrayList<Integer> vals, boolean includeSize, boolean summarized){
        StringBuffer sb = new StringBuffer();
        ArrayList<Integer> valsCommon = getNotCommonValues(vals);
        
        if(!summarized){
            if(includeSize)
                sb.append("- Not Found in Geo: "+valsCommon.size()+" out of "+vals.size()+" - ");

            for (Integer value : valsCommon) {
                sb.append(value+",");
            }
        }else{
        
            if(includeSize)
                sb.append(""+values.size()+","+valsCommon.size()+","+vals.size()+"");
        }
        
        return sb.toString();
    }
    
    public String getValuesString(){
        StringBuffer sb = new StringBuffer();
        
        for (Integer value : values) {
            sb.append(value+" ");
        }
        
        return sb.toString();
    }

    public TypeExperiment getType() {
        return type;
    }

    String getVariables() {
        return tableName+","+scale+","+correction+","+distance+","+type+","+outputTime+","+distanceAVG+","+distanceSTDDEV;
    }
    
    
}
