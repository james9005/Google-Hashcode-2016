using Microsoft.VisualStudio.TestTools.UnitTesting;
using HashCode2016;

namespace HashCode2016Test {
    [TestClass]
    public class DeliveryPlanTest {
        [TestMethod]
        public void TestPreorder() {
            Drone drone = new Drone(0, 0, 0);
            Order order = new Order(0, 0, 0);

            order.AddItem(1);
            order.AddItem(1);
            order.AddItem(2);
            order.AddItem(2);
            order.AddItem(2);

            var plan = new DeliveryPlan(drone, order);

            Assert.AreEqual(2, plan.GetAmountStillToPreorder(1));
            Assert.AreEqual(3, plan.GetAmountStillToPreorder(2));

            plan.AddLoadDroneCommand(0, 1, 2);

            Assert.AreEqual(2, plan.GetAmountPreorderedAtWarehouse(0, 1));
            Assert.AreEqual(0, plan.GetAmountStillToPreorder(1));

            plan.AddLoadDroneCommand(5, 2, 1);

            Assert.AreEqual(0, plan.GetAmountPreorderedAtWarehouse(0, 2));
            Assert.AreEqual(1, plan.GetAmountPreorderedAtWarehouse(5, 2));
            Assert.AreEqual(2, plan.GetAmountStillToPreorder(2));


            Assert.IsFalse(plan.IsDeliveryPlanComplete());
            plan.AddLoadDroneCommand(5, 2, 2);
            Assert.IsTrue(plan.IsDeliveryPlanComplete());
        }
    }
}
