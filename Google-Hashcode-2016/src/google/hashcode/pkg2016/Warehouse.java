package google.hashcode.pkg2016;

import java.util.HashMap;

public class Warehouse extends GridItem {

    public int id;
    private HashMap<ProductType, Integer> stock;
    
    public Warehouse(int id, int x, int y) {
        super(x, y);
        this.id = id;
        this.stock = new HashMap<>();
    }
    
    public void addStock(ProductType type, int q) {
        int curr = stock.getOrDefault(type, 0);
        stock.putIfAbsent(type, curr + q);
    }
    
    public int getQuantity(ProductType type) {
        return stock.get(type);
    }
}