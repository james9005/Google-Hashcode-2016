package google.hashcode.pkg2016;

public class OrderItem {
  public Warehouse warehouse;
  public ProductType productType;
  
  public boolean accounted = false;

  public OrderItem(Warehouse w, ProductType pt) {
    warehouse = w;
    productType = pt;
  }
}