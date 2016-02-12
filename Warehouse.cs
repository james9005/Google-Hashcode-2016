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

        public void AddStock(int productType, int quantity) {
            AdjustStock(productType, quantity);
        }

        public void RemoveStock(int productType, int quantity) {
            AdjustStock(productType, -quantity);
        }

        public int GetStockLevel(int productType) {
            int quantity;
            stock.TryGetValue(productType, out quantity);
            return quantity;
        }

        private void AdjustStock(int productType, int adjustment) {
            int newQuantity = GetStockLevel(productType) + adjustment;

            if (newQuantity < 0) {
                throw new Exception("Can't have negative stock.");
            }

            stock[productType] = newQuantity;
        }

        public bool IsInStock(int productType) {
            return GetStockLevel(productType) > 0;
        }
    }
}
