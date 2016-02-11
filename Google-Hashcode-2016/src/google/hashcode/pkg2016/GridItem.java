package google.hashcode.pkg2016;

public class GridItem {
    public int x;
    public int y;
    
    public GridItem(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int distanceBetween(GridItem other) {
        double rowDiff = this.x - other.x;
        double colDiff = this.y - other.y;
        double sum = Math.pow(rowDiff, 2) + Math.pow(colDiff, 2);
        return (int)Math.ceil(Math.sqrt(sum));
    }
}
