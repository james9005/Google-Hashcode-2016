package google.hashcode.pkg2016;

public class Drone extends GridItem {
    
    public int id;
    public int busyCount;

    public Drone(int id, int x, int y) {
        super(x, y);
        this.id = id;
        
    }  
    public boolean isBusy(){
        return busyCount !=0;
    }
}