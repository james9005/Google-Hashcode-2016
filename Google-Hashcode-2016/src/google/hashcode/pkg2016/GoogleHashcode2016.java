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
    
    public int currentDroneId = 0;
    
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
        PerformIterations();
    }
    
    public Drone getCurrentDrone() {
      int getCurrentDrone = currentDroneId++;
      
      if (getCurrentDrone > drones.size()) {
        getCurrentDrone = 0;
      }
      
      return drones.get(getCurrentDrone);
    }

    public void PerformIterations() {
      for (Order o in orders) {
        List<OrderAvailable> availablity = new ArrayList<OrderAvailable>();

        Drone currentDrone = getCurrentDrone();
        
        Set<ProductType> keys = o.items.keySet();
        
        for (ProductType ptKey in keys) {
          int itemQuantity = o.items.get(ptKey);
          
          for (int i = 0; i < itemQuantity; i++) {
            for (Warehouse w in warehouses) {
              int currentWarehouseQuantity = w.getQuantity(ptKey);
              
              if (currentWarehouseQuantity > itemQuantity) {
                
              }
              OrderAvailable oa = new OrderAvailable(w, ptKey);
              
              availablity.add(oa);
            }
          }
          
          if // when no warehouses have quantity
        }
        
        
        
        // When we have the list go perform actions
      }
    }
    
    public 

    public void ParseFile(String path) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        GetGeneralInfo(scanner.nextLine());
        
        int numProdTypes = Integer.parseInt(scanner.nextLine());
        String productTypeString = scanner.nextLine();
        ParseProductTypes(numProdTypes, productTypeString);
        
        int numWarehouses = Integer.parseInt(scanner.nextLine());
        
        for (int i = 0; i < numWarehouses; i++) {
            Warehouse w = CreateWarehouse(i, scanner.nextLine());
            warehouses.add(w);
            
            String stockLine = scanner.nextLine();
            ParseStock(w, stockLine);
        }
        
        int numOrders = Integer.parseInt(scanner.nextLine());
        
        for (int i = 0; i < numOrders; i++) {
            Order o = CreateOrder(i, scanner.nextLine());
            orders.add(o);
            
            int numItems = Integer.parseInt(scanner.nextLine());
            String orderItemsLine = scanner.nextLine();
            
            ParseOrderItems(o, numItems, orderItemsLine);
        }
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
    
    public void ParseProductTypes(int num, String line) {
        String[] weights = line.split(" ");
        
        for (int i = 0; i < num; i++) {
            productTypes.add(new ProductType(i, Integer.parseInt(weights[i])));
        }
    }
    
    public Warehouse CreateWarehouse(int id, String line) {
        String[] pos = line.split(" ");
        return new Warehouse(id, Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
    }
    
    public void ParseStock(Warehouse w, String stockLine) {
        String[] stock = stockLine.split(" ");
        
        for (int i = 0; i < stock.length; i++) {
            w.addStock(productTypes.get(i), Integer.parseInt(stock[i]));
        }
    }
    
    public Order CreateOrder(int id, String line) {
        String[] pos = line.split(" ");
        return new Order(id, Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
    }
    
    public void ParseOrderItems(Order o, int numItems, String line) {
        String[] items = line.split(" ");
        
        for (int i = 0; i < numItems; i++) {
            int productType = Integer.parseInt(items[i]);
            o.addItem(productTypes.get(productType));
        }
    }
}