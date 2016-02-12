package google.hashcode.pkg2016;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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
        if (args.length == 2) {
            new GoogleHashcode2016(args[0], args[1]);
        }
    }

    public GoogleHashcode2016(String inputPath, String outputPath) {
        drones = new ArrayList<>();
        warehouses = new ArrayList<>();
        orders = new ArrayList<>();
        productTypes = new ArrayList<>();

        ParseFile(inputPath);

        int currentTurn = 0;
        int currentOrder = 0;
        
        List<OrderPlan> orderPlans = new ArrayList<OrderPlan>();
        
        // TODO: We can sort the OrderPlan based on quickest time to complete
        
        Drone dummyDrone = new Drone(0, 0, 0);
        
        for (Order o : orders) {
          orderPlans.add(getOrderPlan(dummyDrone, o));
        }
        
        System.out.println("Order plans generated");

        while(currentOrder < orderPlans.size()) {
          for (Drone d : drones) {
            if (!d.isBusy()) {
              // TODO: We want to get all the order plans (which will be warehouse to customer)
              // Calculate the closest warehouse and find the shortest plan for a drone to run with
              OrderPlan op = orderPlans.get(currentOrder);
              d.addOrderPlan(op);
              
              System.out.println(String.format("Drone %d working on order %d", d.id, op.order.id));
              
              currentOrder++;
            }
          }

          currentTurn++;
        }
    
        List<String> allCommands = new ArrayList<>();
        
        for (Drone d : drones) {
            allCommands.addAll(outputCommands(d.id, d.getCommands()));
        }
        
        WriteFile(outputPath, allCommands);
    }

    public List<String> outputCommands(int droneId, List<Command> commands) {
        List<String> commandsForDrone = new ArrayList<>();
        
        for (Command c : commands) {
            commandsForDrone.add(String.format("%d %s", droneId, c.toString()));
        }
        
        return commandsForDrone;
    }
    
    public void WriteFile(String path, List<String> commands) {
        BufferedWriter writer = null;
                
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            
            writer.write(String.format("%d\n", commands.size()));
            
            for(String c : commands) {
                writer.write(String.format("%s\n", c));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }

    public Warehouse getClosestWarehouseWithProductType(Order o, ProductType pt) {
      Warehouse closestWarehouse = warehouses.get(0);
      int closestWarehouseDistance = closestWarehouse.distanceBetween(o);
                    
      for (Warehouse w : warehouses) {
        int warehouseToCustomer = w.distanceBetween(o);
          
        if (warehouseToCustomer < closestWarehouseDistance && w.getQuantity(pt) > 0) {
           closestWarehouse = w;
           closestWarehouseDistance = warehouseToCustomer;
        }
      }
      
      return closestWarehouse;
    }
    
    public Warehouse getClosestWarehouseForAllProductTypes(Order o) {
      Warehouse closestWarehouse = warehouses.get(0);
      int closestWarehouseDistance = closestWarehouse.distanceBetween(o);
                    
      for (ProductType pt : o.items.keySet()) {
        for (Warehouse w : warehouses) {
          int warehouseToCustomer = w.distanceBetween(o);

          if (warehouseToCustomer < closestWarehouseDistance && w.getQuantity(pt) > 0) {
              closestWarehouse = w;
              closestWarehouseDistance = warehouseToCustomer;
          }
        }
      }
      
      return closestWarehouse; 
    }
    
    public OrderPlan getOrderPlan(Drone d, Order o) {
      List<OrderItem> orderItems = new ArrayList<OrderItem>();
      List<Action> actions = new ArrayList<Action>();
      List<Command> droneCommands = new ArrayList<Command>();

      // TODO: Initially we can get the closest warehouse to a customer
      // From there we can calculate the time taken to complete the single order
      // For an order we also want to store what the closest warehouse is,
      // so that we can determine which drone is best suited
      Warehouse firstWarehouse = getClosestWarehouseForAllProductTypes(o);

      d.x = firstWarehouse.x;
      d.y = firstWarehouse.y;

      // Calculate the warehouses we need to go to for the products
      for (ProductType ptKey : o.items.keySet()) {
        int quantityRequired = o.items.get(ptKey);
        int quantityObtained = 0;

        while (quantityObtained < quantityRequired) {
          // TODO: We can also try to get the closest warehouse to the customer, to improve delivery time
          Warehouse closestAvailableWarehouse = getClosestWarehouseWithProductType(o, ptKey);
          
          int currentWarehouseQuantity = closestAvailableWarehouse.getQuantity(ptKey);
          int quantityRemaining = (quantityRequired - quantityObtained);

          if (currentWarehouseQuantity >= quantityRemaining) {
            // If the warehouse has the required quantity we need
            orderItems.addAll(getOrdersForQuantity(closestAvailableWarehouse, ptKey, quantityRemaining));
            closestAvailableWarehouse.reserve(ptKey, quantityRemaining);
            quantityObtained += quantityRemaining;
          } else if(currentWarehouseQuantity < quantityRemaining && currentWarehouseQuantity > 0) {
            // The warehouse has some of what we require
            orderItems.addAll(getOrdersForQuantity(closestAvailableWarehouse, ptKey, currentWarehouseQuantity));
            closestAvailableWarehouse.reserve(ptKey, currentWarehouseQuantity);
            quantityObtained += currentWarehouseQuantity;
          }
        }
      }

      // For each item on the order
      for(OrderItem oi : orderItems) {
        int distanceToWarehouse = oi.warehouse.distanceBetween(d);

        List<Action> flyingActions = createFlyingActions(distanceToWarehouse);
        actions.addAll(flyingActions);

        // TODO: Check if we can load up with more than one OrderItem
        droneCommands.add(new Command("L", oi.warehouse.id, oi.productType.id));
        actions.add(new Action("L"));

        int distanceToCustomer  = o.distanceBetween(d);

        flyingActions = createFlyingActions(distanceToCustomer);
        actions.addAll(flyingActions);

        droneCommands.add(new Command("D", o.id, oi.productType.id));
        actions.add(new Action("D"));
      }

      d.setLocationBasedOnOrder(o);

      return new OrderPlan(orderItems, actions, droneCommands, firstWarehouse, o);
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