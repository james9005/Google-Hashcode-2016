package google.hashcode.pkg2016;

public class Command {
  public String commandName; 
  public int thingId;
  public int productTypeId;
  
  public Command(String commandName, int thingId, int productTypeId) {
    this.commandName = commandName;
    this.thingId = thingId;
    this.productTypeId = productTypeId;
  }
  
  @Override
  public String toString() {
    return String.format("%s %d %d", commandName, thingId, productTypeId);
  }
}