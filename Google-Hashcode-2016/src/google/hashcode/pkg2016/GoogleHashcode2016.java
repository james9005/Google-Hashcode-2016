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
        
        int currentTurn = 0;
        int currentOrder = 0;
        
        while(currentOrder < orders.size()) {
          for (Drone d : drones) {
            if (!d.isBusy()) {
              Order o = orders.get(currentOrder++);
              d.addOrderPlan(getOrderPlan(d, o));
            }
          }
          
          currentTurn++;
        }
        
        for (Drone d : drones) {
            outputCommands(d.id, d.getCommands());
        }
    }
    
    public void outputCommands(int droneId, List<Command> commands) {
        for (Command c : commands) {
            System.out.println(String.format("%d %s", droneId, c.toString()));
        }
    }
    
    public OrderPlan getOrderPlan(Drone d, Order o) {
      List<OrderItem> orderItems = new ArrayList<OrderItem>();
      List<Action> actions = new ArrayList<Action>();
      List<Command> droneCommands = new ArrayList<Command>();
      
      // Calculate the warehouses we need to go to for the products
      for (ProductType ptKey : o.items.keySet()) {
        int quantityRequired = o.items.get(ptKey);
        int quantityObtained = 0;
        
        while (quantityObtained < quantityRequired) {
          for (Warehouse w : warehouses) {
            int currentWarehouseQuantity = w.getQuantity(ptKey);
            int quantityRemaining = (quantityRequired - quantityObtained);
            
            if (currentWarehouseQuantity >= quantityRemaining) {
              // If the warehouse has the required quantity we need
              orderItems.addAll(getOrdersForQuantity(w, ptKey, quantityRemaining));
              w.reserve(ptKey, quantityRemaining);
              quantityObtained += quantityRemaining;
            } else if(currentWarehouseQuantity < quantityRemaining && currentWarehouseQuantity > 0) {
              // The warehouse has some of what we require
              orderItems.addAll(getOrdersForQuantity(w, ptKey, currentWarehouseQuantity));
              w.reserve(ptKey, currentWarehouseQuantity);
              quantityObtained += currentWarehouseQuantity;
            }
          }
        }
      }
      
      // For each item on the order
      for(OrderItem oi : orderItems) {
        int distanceToWarehouse = oi.warehouse.distanceBetween(d);
        
        List<Action> flyingActions = createFlyingActions(distanceToWarehouse);
        actions.addAll(flyingActions);
        
        droneCommands.add(new Command("L", oi.warehouse.id, oi.productType.id));
        actions.add(new Action("L"));
        
        int distanceToCustomer  = o.distanceBetween(d);
        
        flyingActions = createFlyingActions(distanceToCustomer);
        actions.addAll(flyingActions);
        
        droneCommands.add(new Command("D", o.id, oi.productType.id));
        actions.add(new Action("D"));
      }
      
      d.setLocationBasedOnOrder(o);
      
      return new OrderPlan(orderItems, actions, droneCommands);
    }
    
    public List<Action> createFlyingActions(int distance) {
      List<Action> flyActions = new ArrayList<Action>();
      
      for (int i = 0; i < distance; i++) {
        flyActions.add(new Action("F"));
      }
      
      return flyActions;
    }

    public List<OrderItem> getOrdersForQuantity(Warehouse w, ProductType pt, int quantity) {
      List<OrderItem> items = new ArrayList<OrderItem>();
      
      for(int i = 0; i < quantity; i++) {
        items.add(new OrderItem(w, pt));
      }
      
      return items;
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