package google.hashcode.pkg2016;

public class Order extends GridItem {
    
    public int id;
    public HashMap<ProductType, int> items = new HashMap<ProductType, int>();
    
    public Order(int id, int x, int y) {
        super(x, y);
        this.id = id;
    }
    
    public void addItem(ProductType pt) {
      int current = items.getOrDefault(pt, 0);
      items.putIfAbsent(pt, current + 1);
    }
}