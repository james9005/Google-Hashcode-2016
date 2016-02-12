using System;
using System.Collections.Generic;

namespace HashCode2016 {
    public class Drone : GridItem {

        private int busyCounter;
        private Dictionary<int, int> inventory;
        private List<string> commandHistory;

        public Drone(int id, int x, int y) : base(x, y) {
            Id = id;
            inventory = new Dictionary<int, int>();
            commandHistory = new List<string>();
        }

        public int Id { get; set; }

        public bool IsBusy {
            get { return busyCounter > 0; }
        }

        public IEnumerable<string> CommandHistory {
            get { return commandHistory; }
        }

        public void Move(GridItem gridItem) {
            X = gridItem.X;
            Y = gridItem.Y;
        }

        public void Move(int x, int y) {
            X = x;
            Y = y;
        }

        public void MakeBusy(int numberOfTurns) {
            busyCounter = numberOfTurns;
        }

        public void Load(int productType, int quantity) {
            inventory[productType] = GetCurrentQuantityOfProductType(productType) + quantity;
            CheckPayload();
        }

        public int GetCurrentQuantityOfProductType(int productType) {
            int quantity;
            inventory.TryGetValue(productType, out quantity);
            return quantity;
        }

        public void Unload(int productType, int quantity) {
            int newQuantity = GetCurrentQuantityOfProductType(productType) - quantity;

            if (newQuantity < 0) {
                throw new Exception("Can't unload that much stock - drone doesn't have enough on board.");
            }

            inventory[productType] = newQuantity;
        }

        private void CheckPayload() {
            if (CalculatePayload() > Program.maxDronePayload) {
                throw new Exception("Payload of drone has exceeded the limit.");
            }
        }

        public int CalculatePayload() {
            int weight = 0;
            foreach (var productType in inventory) {
                weight += Program.productTypeWeights[productType.Key] * productType.Value;
            }

            return weight;
        }

        public void AddCommandsToHistory(IEnumerable<string> commands) {
            commandHistory.AddRange(commands);
        }

        public void Update() {
            if (IsBusy) {
                busyCounter--;
            }
        }
    }
}
