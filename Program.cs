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

        public static List<Warehouse> warehouses;
        public static List<Order> orders;

        public static void Main(string[] args) {
            ParseFile(@"Files\Inputs\sample_data.in");
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
    }
}
