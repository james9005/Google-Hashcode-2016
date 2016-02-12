using System;
using System.Linq;
using System.Collections.Generic;

namespace HashCode2016 {
    public class Order : GridItem {

        private Dictionary<int, int> items;

        public Order(int id, int x, int y) : base(x, y) {
            Id = id;
            items = new Dictionary<int, int>();
        }

        public int Id { get; set; }

        public void AddItem(int productType) {
            AdjustQuantity(productType, 1);
        }

        public int GetQuantityToDeliver(int productType) {
            int quantity;
            items.TryGetValue(productType, out quantity);
            return quantity;
        }

        public int GetTotalNumberOfItems() {
            return items.Values.Sum();
        }

        public void DeliverItems(int productType, int quantity) {
            AdjustQuantity(productType, -quantity);
        }

        private void AdjustQuantity(int productType, int adjustment) {
            int newQuantity = GetQuantityToDeliver(productType) + adjustment;

            if (newQuantity < 0) {
                throw new Exception("Delivered too many.");
            }

            items[productType] = newQuantity;
        }
    }
}
