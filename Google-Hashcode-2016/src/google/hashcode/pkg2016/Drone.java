package google.hashcode.pkg2016;

import java.util.List;
import java.util.ArrayList;

public class Drone extends GridItem {

    public int id;
    public int busyCount;

    public List<OrderPlan> orderPlans = new ArrayList<OrderPlan>();

    public Drone(int id, int x, int y) {
        super(x, y);
        this.id = id;
    }

    public boolean isBusy() {
        return (busyCount--) != 0;
    }

    public void addOrderPlan(OrderPlan op) {
      busyCount = op.actions.size();
      orderPlans.add(op);
    }

    public void setLocationBasedOnOrder(Order o) {
      this.x = o.x;
      this.y = o.y;
    }

    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<Command>();

        for (OrderPlan op : orderPlans) {
            commands.addAll(op.commands);
        }

        return commands;
    }
}