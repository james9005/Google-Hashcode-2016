package google.hashcode.pkg2016;

import java.util.List;

public class OrderPlan {
  public List<Action> actions;
  public List<OrderItem> orderItems;
  public List<Command> commands;
  
  public OrderPlan(List<OrderItem> oi, List<Action> a, List<Command> c) {
    actions = a;
    orderItems = oi;
    commands = c;
  }
}