package google.hashcode.pkg2016;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
    public List<OrderPlan> orderPlans;

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
        orderPlans = new ArrayList<>();
                
        ParseFile(inputPath);

        int currentTurn = 0;
        int currentOrder = 0;

        // TODO: We can sort the OrderPlan based on quickest time to complete
        
        Drone dummyDrone = new Drone(0, 0, 0);
        
        List<Order> ordersBySmallestQuantity = orders.stream()
          .sorted((Order a, Order b) -> a.items.size() - b.items.size())
          .collect(Collectors.toList());

        for (Order o : ordersBySmallestQuantity) {
          orderPlans.add(getOrderPlan(dummyDrone, o));
          
          System.out.println(String.format("Generated plan for Order %d", o.id));
        }
        
        System.out.println("Order plans generated");

        while(currentOrder < orderPlans.size()) {
          for (Drone d : drones) {
            if (!d.isBusy()) {
              // TODO: We want to get all the order plans (which will be warehouse to customer)
              // Calculate the closest warehouse and find the shortest plan for a drone to run with
              OrderPlan op = getClosestOrderPlanToDrone(d);
              
              if (op != null) {
                d.addOrderPlan(op);
              }
              
              // System.out.println(String.format("Drone %d working on order %d", d.id, op.order.id));
              
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
    
    public OrderPlan getClosestOrderPlanToDrone(Drone d) {
      List<OrderPlan> remainingPlans = orderPlans.stream()
        .filter((OrderPlan o) -> !o.completed)
        .collect(Collectors.toList());
      
      if (remainingPlans.isEmpty()) {
        return null;
      }
      
      OrderPlan closestOrderPlan = remainingPlans.stream().reduce((OrderPlan a, OrderPlan b) -> {
        int distanceA = a.firstWarehouse.distanceBetween(d);
        int distanceB = b.firstWarehouse.distanceBetween(d);
        
        if (distanceA < distanceB)
          return a;
        else
          return b;
      }).get();
      
      closestOrderPlan.completed = true;
      
      return closestOrderPlan;
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
      Multimap<Integer, Warehouse> warehouseDistances = HashMultimap.create();
      
      // Get all the warehouse distances and store them in the Multimap
      for (Warehouse w : warehouses) {
        int warehouseToCustomer = w.distanceBetween(o);

        warehouseDistances.put(warehouseToCustomer, w);
      }
      
      // Order our data by the shortest distance
      TreeSet<Integer> keys = new TreeSet(warehouseDistances.keySet());
      
      for(Integer k : keys) {
        Collection<Warehouse> closestWarehouses = warehouseDistances.get(k);

        for (Warehouse w : closestWarehouses) {
          if (w.getQuantity(pt) > 0) {
            return w;
          }
        }
      }
      
      return null; 
    }
    
    public OrderPlan getOrderPlan(Drone d, Order o) {
      List<Action> actions = new ArrayList<Action>();
      List<Command> droneCommands = new ArrayList<Command>();
      
      Multimap<Warehouse, OrderItem> itemsAtWarehouses = HashMultimap.create();

      // TODO: Initially we can get the closest warehouse to a customer
      // From there we can calculate the time taken to complete the single order
      // For an order we also want to store what the closest warehouse is,
      // so that we can determine which drone is best suited
      
      Warehouse firstWarehouse = null;

      // Calculate the warehouses we need to go to for the roducts
      for (ProductType ptKey : o.items.keySet()) {
        int quantityRequired = o.items.get(ptKey);
        int quantityObtained = 0;

        while (quantityObtained < quantityRequired) {
          // TODO: We can also try to get the closest warehouse to the customer, to improve delivery time
          Warehouse closestAvailableWarehouse = getClosestWarehouseWithProductType(o, ptKey);
          
          if (firstWarehouse == null) {
            firstWarehouse = closestAvailableWarehouse;
          }
          
          int currentWarehouseQuantity = closestAvailableWarehouse.getQuantity(ptKey);
          int quantityRemaining = (quantityRequired - quantityObtained);

          if (currentWarehouseQuantity >= quantityRemaining) {
            // If the warehouse has the required quantity we need
            List<OrderItem> ordersAtWarehouse = getOrdersForQuantity(closestAvailableWarehouse, ptKey, quantityRemaining);
            
            itemsAtWarehouses.putAll(closestAvailableWarehouse, ordersAtWarehouse);
            
            closestAvailableWarehouse.reserve(ptKey, quantityRemaining);
            
            quantityObtained += quantityRemaining;
          } else if(currentWarehouseQuantity < quantityRemaining && currentWarehouseQuantity > 0) {
            // The warehouse has some of what we require
            List<OrderItem> ordersAtWarehouse = getOrdersForQuantity(closestAvailableWarehouse, ptKey, currentWarehouseQuantity);
            
            itemsAtWarehouses.putAll(closestAvailableWarehouse, ordersAtWarehouse);
            
            closestAvailableWarehouse.reserve(ptKey, currentWarehouseQuantity);
            
            quantityObtained += currentWarehouseQuantity;
          }
        }
      }
      
      d.x = firstWarehouse.x;
      d.y = firstWarehouse.y;

      // Get the warehouses that we need to go to
      List<Warehouse> warehousesToGoTo = itemsAtWarehouses.values().stream()
        .map((OrderItem a) -> a.warehouse)
        .distinct()
        .sorted((Warehouse a, Warehouse b) -> a.distanceBetween(d) - b.distanceBetween(d))
        .collect(Collectors.toList());

      // Loop through each warehouse
      for (Warehouse w : warehousesToGoTo) {
        boolean completedWarehouse = false;

        while (!completedWarehouse) {
          Multimap<Integer, Set<OrderItem>> bestCombinations = HashMultimap.create();

          Set<OrderItem> remainingItemsAtWarehouse = itemsAtWarehouses.get(w).stream()
            .filter((OrderItem a) -> !a.accounted)
            .collect(Collectors.toSet());

          // Check if we have completed
          completedWarehouse = remainingItemsAtWarehouse.isEmpty();

          if (completedWarehouse) continue;

          // Calculate the best case based on all possible combinations
          Set<Set<OrderItem>> powerSet = Sets.powerSet(remainingItemsAtWarehouse);

          for (Set<OrderItem> combination : powerSet) {
            Integer totalWeight = combination.stream()
              .map((OrderItem a) -> a.productType.weight)
              .reduce(new Integer(0), (Integer a, Integer b) -> a + b);

            if (totalWeight <= maxDroneWeight) {
              bestCombinations.put(totalWeight, combination);
            }
          }

          // Get all the keys for the best combinations
          // These will be the total weights for the drone
          TreeSet<Integer> keys = new TreeSet(bestCombinations.keySet());

          Set<OrderItem> bestCurrentCombination = bestCombinations.get(keys.first()).stream()
            .findAny()
            .get();

          // Fly to the warehouse
          int distanceToWarehouse = w.distanceBetween(d);
          
          List<Action> flyingActions = createFlyingActions(distanceToWarehouse);
          actions.addAll(flyingActions);
          
          // Fly to customer
          int distanceToCustomer  = o.distanceBetween(d);
          
          flyingActions = createFlyingActions(distanceToCustomer);
          actions.addAll(flyingActions);
          
          // Actions for delivering and loading
          for (OrderItem oi : bestCurrentCombination) {
            droneCommands.add(new Command("L", w.id, oi.productType.id, 1));
            actions.add(new Action("L"));
            
            oi.accounted = true;
            
            droneCommands.add(new Command("D", o.id, oi.productType.id, 1));
            actions.add(new Action("D"));
          }
        }
      }
      
      d.setLocationBasedOnOrder(o);

      return new OrderPlan(new ArrayList<OrderItem>(itemsAtWarehouses.values()), actions, droneCommands, firstWarehouse, o);
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