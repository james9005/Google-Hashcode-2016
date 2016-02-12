namespace HashCode2016 {
    public class Drone : GridItem {

        private int busyCounter;

        public Drone(int id, int x, int y) : base(x, y) {
            Id = id;
        }

        public int Id { get; set; }

        public bool IsBusy {
            get { return busyCounter > 0; }
        }

        public void MakeBusy(int numberOfTurns) {
            busyCounter = numberOfTurns;
        }

        public void Update() {
            if (IsBusy) {
                busyCounter--;
            }
        }
    }
}
