using HashCode2016;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace HashCode2016Test {
    [TestClass]
    public class OrderTest {
        [TestMethod]
        public void TestItems() {
            Order order = new Order(0, 0, 0);

            order.AddItem(0);
            order.AddItem(0);
            order.AddItem(1);
            order.AddItem(2);

            Assert.AreEqual(4, order.GetTotalNumberOfItems());

            Assert.AreEqual(2, order.GetQuantityToDeliver(0));
            Assert.AreEqual(1, order.GetQuantityToDeliver(1));
            Assert.AreEqual(1, order.GetQuantityToDeliver(2));
            Assert.AreEqual(0, order.GetQuantityToDeliver(3));

            order.DeliverItems(0, 1);
            Assert.AreEqual(1, order.GetQuantityToDeliver(0));

            order.DeliverItems(1, 1);
            Assert.AreEqual(0, order.GetQuantityToDeliver(1));
        }
    }
}
