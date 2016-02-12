namespace HashCode2016 {
    public class Drone : GridItem {

        public Drone(int id, int x, int y) : base(x, y) {
            Id = id;
        }

        public int Id { get; set; }
    }
}
