package google.hashcode.pkg2016;

import java.util.List;

public class OrderPlan {
  public Order order;
  
  public List<Action> actions;
  public List<OrderItem> orderItems;
  public List<Command> commands;
  
  public Warehouse firstWarehouse;
  
  public boolean completed = false;

  public OrderPlan(List<OrderItem> oi, List<Action> a, List<Command> c, Warehouse first, Order o) {
    actions = a;
    orderItems = oi;
    commands = c;
    firstWarehouse = first;
    order = o;
  }
}