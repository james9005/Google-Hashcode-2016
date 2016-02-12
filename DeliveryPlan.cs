using System;
using System.Collections.Generic;

namespace HashCode2016 {
    public class DeliveryPlan {

        private List<Tuple<int, int, int>> stockPreorders;

        public DeliveryPlan() {
            stockPreorders = new List<Tuple<int, int, int>>();
        }

        public int NumberOfTurns { get; set; }

        public IEnumerable<Tuple<int, int, int>> StockPreorders {
            get { return stockPreorders; }
        }

        public void PreorderStock(int warehouseId, int productType, int quantity) {
            stockPreorders.Add(Tuple.Create(warehouseId, productType, quantity));
        }
    }
}
