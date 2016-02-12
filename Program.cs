using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace HashCode2016 {
    public class Program {

        public static int rows;
        public static int cols;
        public static int numberOfDrones;
        public static int maxTurns;
        public static int maxDronePayload;
        public static int numberOfProductTypes;
        public static int[] productTypeWeights;

        private static List<Drone> drones;
        public static List<Warehouse> warehouses;
        public static List<Order> orders;

        public static void Main(string[] args) {
            ParseFile(@"Files\Inputs\sample_data.in");
            Go();
            // TODO: output commands to a file.
        }

        #region Parse

        private static void ParseFile(string file) {
            using (StreamReader reader = new StreamReader(file)) {
                var info = Split(reader.ReadLine());
                rows = info[0];
                cols = info[1];
                numberOfDrones = info[2];
                maxTurns = info[3];
                maxDronePayload = info[4];

                numberOfProductTypes = Convert.ToInt32(reader.ReadLine());
                productTypeWeights = Split(reader.ReadLine());

                warehouses = new List<Warehouse>();
                int numberOfWarehouses = Convert.ToInt32(reader.ReadLine());

                for (int i = 0; i < numberOfWarehouses; i++) {
                    var pos = Split(reader.ReadLine());
                    var warehouse = new Warehouse(i, pos[0], pos[1]);
                    warehouses.Add(warehouse);

                    var stock = Split(reader.ReadLine());
                    for (int j = 0; j < stock.Length; j++) {
                        warehouse.AddStock(j, stock[j]);
                    }
                }

                drones = new List<Drone>();
                for (int i = 0; i < numberOfDrones; i++) {
                    // Drones start the simulation at the first warehouse.
                    drones.Add(new Drone(i, warehouses[0].X, warehouses[0].Y));
                }

                orders = new List<Order>();
                int numberOfOrders = Convert.ToInt32(reader.ReadLine());

                for (int i = 0; i < numberOfOrders; i++) {
                    var pos = Split(reader.ReadLine());
                    var order = new Order(i, pos[0], pos[1]);
                    orders.Add(order);

                    int numberOfOrderItems = Convert.ToInt32(reader.ReadLine());
                    var orderItems = Split(reader.ReadLine());
                    foreach (int item in orderItems) {
                        // 'Item' is the product type.
                        order.AddItem(item);
                    }
                }

                // Sanity check
                if (!reader.EndOfStream) {
                    throw new Exception("There are still more lines to read...");
                }
            }
        }

        private static int[] Split(string line) {
            return line
                .Split(new char[] { ' ' })
                .Select(i => Convert.ToInt32(i))
                .ToArray();
        }

        #endregion

        private static void Go() {
            int currentTurn = 0;
            while (currentTurn < maxTurns) {
                // We need to find a task for the drones that aren't busy.
                foreach (var drone in drones.Where(d => !d.IsBusy)) {
                    // 1. Find an order for the drone.
                    // 2. Locate the warehouses that we'll need to go to and create the drone commands.
                    // 3. Remove the stock from those warehouses (effectively pre-ordering items).
                    // 4. Calculate the number of turns it'll take to process this order.
                    // 5. Assuming there are enough turns left, move the drone to the order location.
                    // 6. Make the drone busy for the number of turns it'll take to process this order.
                    // 7. Remove the order from the list.
                }

                if (!orders.Any() && !drones.Any(d => d.IsBusy)) {
                    // There are no orders left and all drones have finished their tasks.
                    break;
                }

                currentTurn++;
                UpdateDrones();
            }
        }

        private static void UpdateDrones() {
            foreach (var drone in drones) {
                drone.Update();
            }
        }
    }
}
