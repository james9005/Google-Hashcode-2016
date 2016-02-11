package google.hashcode.pkg2016;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class GoogleHashcode2016 {

    public static int rows;
    public static int cols;
    public static int maxTurns;
    public static int maxDroneWeight;
    public static List<Drone> drones;
    public static List<Warehouse> warehouses;
    public static List<Order> orders;
    public static List<ProductType> productTypes;
    
    public static void main(String[] args) {
        ParseFile("\\Inputs\\sample_data.in");
    }
    
    public static void ParseFile(String path) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        GetGeneralInfo(scanner.nextLine());
    }
    
    public static void GetGeneralInfo(String line) {
        String[] generalInfo = line.split(" ");
        rows = Integer.parseInt(generalInfo[0]);
        cols = Integer.parseInt(generalInfo[1]);
                
        maxTurns = Integer.parseInt(generalInfo[3]);
        maxDroneWeight = Integer.parseInt(generalInfo[4]);
        
        int numberOfDrones = Integer.parseInt(generalInfo[2]);
        for (int i = 0; i < numberOfDrones; i++) {
            Drone d = new Drone(i, 0, 0);
        }
    }
}