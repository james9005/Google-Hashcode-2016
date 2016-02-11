package google.hashcode.pkg2016;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GoogleHashcode2016 {

    public int rows;
    public int cols;
    public int maxTurns;
    public int maxDroneWeight;
    public List<Drone> drones;
    public List<Warehouse> warehouses;
    public List<Order> orders;
    public List<ProductType> productTypes;
    
    public static void main(String[] args) {
        new GoogleHashcode2016();
    }
    
    public GoogleHashcode2016() {
        drones = new ArrayList<>();
        warehouses = new ArrayList<>();
        orders = new ArrayList<>();
        productTypes = new ArrayList<>();
        
        URL url = getClass().getResource("Inputs/sample_data.in");
        ParseFile(url.getPath());
    }
    
    public void ParseFile(String path) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        GetGeneralInfo(scanner.nextLine());
    }
    
    public void GetGeneralInfo(String line) {
        String[] generalInfo = line.split(" ");
        rows = Integer.parseInt(generalInfo[0]);
        cols = Integer.parseInt(generalInfo[1]);
                
        maxTurns = Integer.parseInt(generalInfo[3]);
        maxDroneWeight = Integer.parseInt(generalInfo[4]);
        
        int numberOfDrones = Integer.parseInt(generalInfo[2]);
        for (int i = 0; i < numberOfDrones; i++) {
            drones.add(new Drone(i, 0, 0));
        }
    }
}