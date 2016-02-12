using System;
using System.Linq;
using System.Collections.Generic;

namespace HashCode2016 {
    public class DeliveryPlan {

        private Drone drone;
        private Order order;
        private List<Tuple<int, int, int>> stockPreorders;
        private List<string> droneCommands;

        public DeliveryPlan(Drone drone, Order order) {
            this.drone = drone;
            this.order = order;
            stockPreorders = new List<Tuple<int, int, int>>();
            droneCommands = new List<string>();
        }

        public int NumberOfTurns { get; set; }

        public IEnumerable<Tuple<int, int, int>> StockPreorders {
            get { return stockPreorders; }
        }

        public IEnumerable<string> DroneCommands {
            get { return droneCommands; }
        }

        public int GetAmountPreordered(int warehouseId, int productType) {
            return stockPreorders
                .Where(i => i.Item1 == warehouseId && i.Item2 == productType)
                .Sum(i => i.Item3);
        }

        public void AddLoadDroneCommand(int warehouseId, int productType, int quantity) {
            stockPreorders.Add(Tuple.Create(warehouseId, productType, quantity));

            droneCommands.Add(
                string.Format("{0} {1} {2} {3} {4}",
                    drone.Id,
                    "L",
                    warehouseId,
                    productType,
                    quantity));
        }

        public void AddDeliverDroneCommand(int productType, int quantity) {
            droneCommands.Add(
                string.Format("{0} {1} {2} {3} {4}",
                    drone.Id,
                    "D",
                    order.Id,
                    productType,
                    quantity));
        }
    }
}
