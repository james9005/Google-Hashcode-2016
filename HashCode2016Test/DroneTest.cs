using HashCode2016;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace HashCode2016Test {
    [TestClass]
    public class DroneTest {
        [TestMethod]
        public void TestBusy() {
            Drone drone = new Drone(0, 0, 0);

            Assert.IsFalse(drone.IsBusy);

            drone.MakeBusy(2);
            Assert.IsTrue(drone.IsBusy);

            drone.Update();
            Assert.IsTrue(drone.IsBusy);

            drone.Update();
            Assert.IsFalse(drone.IsBusy);
        }

        [TestMethod]
        public void TestMove() {
            Drone drone = new Drone(0, 0, 0);

            drone.Move(3, 5);
            Assert.AreEqual(3, drone.X);
            Assert.AreEqual(5, drone.Y);
        }

        [TestMethod]
        public void TestPayload() {
            Program.maxDronePayload = 70;
            Program.productTypeWeights = new int[] { 5, 10, 20 };

            Drone drone = new Drone(0, 0, 0);

            // Load 10 of type 0 (weigh 5 per unit).
            drone.Load(0, 10);
            Assert.AreEqual(50, drone.CalculatePayload());

            // Load 2 of type 1 (weigh 10 per unit).
            drone.Load(1, 2);
            Assert.AreEqual(70, drone.CalculatePayload());

            Assert.AreEqual(10, drone.GetCurrentQuantityOfProductType(0));
            Assert.AreEqual(2, drone.GetCurrentQuantityOfProductType(1));
            Assert.AreEqual(0, drone.GetCurrentQuantityOfProductType(2));

            // Unload 10 of type 0.
            drone.Unload(0, 10);
            Assert.AreEqual(20, drone.CalculatePayload());

            // Unload 1 of type 1.
            drone.Unload(1, 1);
            Assert.AreEqual(10, drone.CalculatePayload());

            Assert.AreEqual(0, drone.GetCurrentQuantityOfProductType(0));
            Assert.AreEqual(1, drone.GetCurrentQuantityOfProductType(1));
            Assert.AreEqual(0, drone.GetCurrentQuantityOfProductType(2));
        }
    }
}
