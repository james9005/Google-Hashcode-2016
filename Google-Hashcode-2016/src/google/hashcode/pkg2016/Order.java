package google.hashcode.pkg2016;

import java.util.HashMap;

public class Order extends GridItem {
    
    public int id;
    public HashMap<ProductType, Integer> items = new HashMap<>();
    
    public Order(int id, int x, int y) {
        super(x, y);
        this.id = id;
    }
    
    public void addItem(ProductType pt) {
      int current = items.getOrDefault(pt, 0);
      items.putIfAbsent(pt, current + 1);
    }
}