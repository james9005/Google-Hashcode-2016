using System;
using System.Collections.Generic;

namespace HashCode2016 {
    public class Warehouse : GridItem {

        // Code (product type) + Quantity
        private Dictionary<int, int> stock;

        public Warehouse(int id, int x, int y) : base(x, y) {
            Id = id;
            stock = new Dictionary<int, int>();
        }

        public int Id { get; set; }

        public void AddStock(int type, int quantity) {
            AdjustStock(type, quantity);
        }

        public void RemoveStock(int type, int quantity) {
            AdjustStock(type, -quantity);
        }

        public int GetStockLevel(int type) {
            int quantity;
            stock.TryGetValue(type, out quantity);
            return quantity;
        }

        private void AdjustStock(int type, int adjustment) {
            int current = 0;

            if (stock.ContainsKey(type)) {
                current = stock[type];
            }

            int newQuantity = current + adjustment;

            if (newQuantity < 0) {
                throw new Exception("Can't have negative stock.");
            }

            stock[type] = newQuantity;
        }

        public bool IsInStock(int type) {
            return GetStockLevel(type) > 0;
        }
    }
}
