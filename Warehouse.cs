namespace HashCode2016 {
    public class Warehouse : GridItem {

        public Warehouse(int id, int x, int y) : base(x, y) {
            Id = id;
        }

        public int Id { get; set; }
    }
}
