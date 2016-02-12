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

        public void AddItem(int type) {
            AdjustQuantity(type, 1);
        }

        public int GetQuantityToDeliver(int type) {
            int quantity;
            items.TryGetValue(type, out quantity);
            return quantity;
        }

        public int GetTotalNumberOfItems() {
            return items.Values.Sum();
        }

        public void DeliverItems(int type, int quantity) {
            AdjustQuantity(type, -quantity);
        }

        private void AdjustQuantity(int type, int adjustment) {
            int current = 0;

            if (items.ContainsKey(type)) {
                current = items[type];
            }

            int newQuantity = current + adjustment;

            if (newQuantity < 0) {
                throw new Exception("Delivered too many.");
            }

            items[type] = newQuantity;
        }
    }
}
