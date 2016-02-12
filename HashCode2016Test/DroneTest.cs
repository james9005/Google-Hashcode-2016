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
    }
}
