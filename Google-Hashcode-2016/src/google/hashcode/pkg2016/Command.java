package google.hashcode.pkg2016;

public class Command {
  public String commandName;
  public int thingId;
  public int productTypeId;
  public int quantity;

  public Command(String commandName, int thingId, int productTypeId, int quantity) {
    this.commandName = commandName;
    this.thingId = thingId;
    this.productTypeId = productTypeId;
    this.quantity = quantity;
  }

  @Override
  public String toString() {
    return String.format("%s %d %d %d", commandName, thingId, productTypeId, quantity);
  }
}