using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace HashCode2016 {
    public class Program {

        public static string[] scenarios = new string[] {
            "redundancy",
            "busy_day",
            "mother_of_all_warehouses",
        };

        public static int score;

        public static int rows;
        public static int cols;
        public static int numberOfDrones;
        public static int numberOfTurns;
        public static int maxDronePayload;
        public static int numberOfProductTypes;
        public static int[] productTypeWeights;

        private static List<Drone> drones;
        public static List<Warehouse> warehouses;
        public static List<Order> remainingOrders;

        public static void Main(string[] args) {
            foreach (string scenario in scenarios) {
                ParseFile(scenario);
                Go();
                OutputCommandsToFile(scenario);
            }

            Console.WriteLine("Score: " + score);
            Console.ReadLine();
        }

        #region Parse

        private static void ParseFile(string scenario) {
            string inputPath = string.Format(@"Files\Inputs\{0}.in", scenario);

            using (StreamReader reader = new StreamReader(inputPath)) {
                var info = Split(reader.ReadLine());
                rows = info[0];
                cols = info[1];
                numberOfDrones = info[2];
                numberOfTurns = info[3];
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

                remainingOrders = new List<Order>();
                int numberOfOrders = Convert.ToInt32(reader.ReadLine());

                for (int i = 0; i < numberOfOrders; i++) {
                    var pos = Split(reader.ReadLine());
                    var order = new Order(i, pos[0], pos[1]);
                    remainingOrders.Add(order);

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

        #region Solve

        private static void Go() {
            int currentTurn = 0;
            while (currentTurn < numberOfTurns) {
                // We need to find a task for the drones that aren't busy.
                foreach (var drone in drones.Where(d => !d.IsBusy)) {
                    // Find an order for the drone. We'll do the orders with
                    // the fewest items to try get orders completed as early
                    // as possible. The size of the order doesn't affect how
                    // many points you get.
                    var order = remainingOrders
                        .OrderBy(o => o.GetTotalNumberOfItems())
                        .FirstOrDefault();

                    if (order == null) {
                        // No orders left - nothing for the drones to do.
                        break;
                    }

                    var deliveryPlan = GetDeliveryPlan(drone, order);

                    var remainingTurns = numberOfTurns - currentTurn - 1;
                    if (remainingTurns >= deliveryPlan.NumberOfTurns) {
                        // We can complete this order.

                        // Remove the stock from those warehouses (effectively pre-ordering items).
                        foreach (var preorder in deliveryPlan.StockPreorders) {
                            warehouses[preorder.Item1].RemoveStock(preorder.Item2, preorder.Item3);
                        }

                        // Make the drone busy for the number of turns it'll take to process this order.
                        drone.Move(order);
                        drone.AddCommandsToHistory(deliveryPlan.DroneCommands);
                        drone.BusyCounter = deliveryPlan.NumberOfTurns;

                        // Remove the order from the list.
                        remainingOrders.Remove(order);
                    }

                    // TODO: if we don't have time to process the order, the drone should be assigned a new order.
                }

                if (!remainingOrders.Any() && !drones.Any(d => d.IsBusy)) {
                    // There are no orders left and all drones have finished their tasks.
                    break;
                }

                currentTurn++;
                UpdateDrones(currentTurn);
            }
        }

        private static DeliveryPlan GetDeliveryPlan(Drone drone, Order order) {
            int startX = drone.X;
            int startY = drone.Y;

            int turns = 0;

            var plan = new DeliveryPlan(drone, order);

            var productTypesToDeliver = order.GetProductTypesToDeliver();

            foreach (var productType in productTypesToDeliver) {
                int quantityToPreorder = order.GetQuantityToDeliver(productType);
                int maxDroneCanCarry = maxDronePayload / productTypeWeights[productType];
                int quantityOnBoard = drone.GetCurrentQuantityOfProductType(productType);

                while (quantityToPreorder > 0 || quantityOnBoard > 0) {

                    if (quantityOnBoard == maxDroneCanCarry || quantityToPreorder == 0) {
                        // Don't need to preorder any more stock or we're full.
                        turns += drone.DistanceBetween(order) + 1;
                        drone.Move(order);

                        plan.AddDeliverDroneCommand(productType, quantityOnBoard);
                        drone.Unload(productType, quantityOnBoard);
                    } else {
                        // Find the closest warehouse that has it in stock.
                        var warehouseDetails = warehouses
                            .Select(w => new {
                                Warehouse = w,
                                Distance = drone.DistanceBetween(w),
                                // Figure out how much stock would be left after we collect our preorders.
                                // I don't actually want to remove the stock from the warehouse, because
                                // we're not actually processing this order yet. There might not be
                                // enough time to do so.
                                StockRemaining = w.GetStockLevel(productType) - plan.GetAmountPreordered(w.Id, productType),
                            })
                            .Where(w => w.StockRemaining > 0)
                            .OrderBy(w => w.Distance)
                            .First();

                        var warehouse = warehouseDetails.Warehouse;
                        int totalQuantityAtWarehouse = warehouseDetails.StockRemaining;

                        // Move the drone to the warehouse.
                        turns += drone.DistanceBetween(warehouse) + 1;
                        drone.Move(warehouse);

                        // Figure out how much we'll be picking up from the warehouse.
                        int warehousePreorderQuantity = Math.Min(Math.Min(quantityToPreorder, totalQuantityAtWarehouse), maxDroneCanCarry);

                        // Move the stock from the warehouse to the drone.
                        plan.AddLoadDroneCommand(warehouse.Id, productType, warehousePreorderQuantity);
                        drone.Load(productType, warehousePreorderQuantity);

                        // Update the amount still to collect.
                        quantityToPreorder -= warehousePreorderQuantity;
                    }

                    quantityOnBoard = drone.GetCurrentQuantityOfProductType(productType);
                }
            }

            // Set the number of turns the order will take.
            plan.NumberOfTurns = turns;

            // Move the drone back to where it started.
            drone.Move(startX, startY);

            return plan;
        }

        private static void UpdateDrones(int currentTurn) {
            foreach (var drone in drones) {
                if (drone.IsBusy) {
                    drone.BusyCounter--;

                    if (!drone.IsBusy) {
                        // The drone has finished its order. Add to the total score.
                        score += (int)Math.Ceiling(100 * (decimal)(numberOfTurns - currentTurn) / numberOfTurns);
                    }
                }
            }
        }

        #endregion

        #region Output

        private static void OutputCommandsToFile(string scenario) {
            string outputPath = string.Format(@"..\..\Files\Outputs\{0}.txt", scenario);

            using (StreamWriter writer = new StreamWriter(outputPath)) {
                var allCommands = drones.SelectMany(d => d.CommandHistory);

                writer.Write(allCommands.Count());

                foreach (string command in allCommands) {
                    writer.Write(Environment.NewLine + command);
                }
            }
        }

        #endregion
    }
}
