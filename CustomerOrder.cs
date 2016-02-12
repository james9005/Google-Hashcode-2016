namespace HashCode2016 {
    public class CustomerOrder : GridItem {

        public CustomerOrder(int id, int x, int y) : base(x, y) {
            Id = id;
        }

        public int Id { get; set; }
    }
}
