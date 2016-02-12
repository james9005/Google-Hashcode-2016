using HashCode2016;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace HashCode2016Test {
    [TestClass]
    public class WarehouseTest {
        [TestMethod]
        public void TestStock() {
            Warehouse warehouse = new Warehouse(0, 0, 0);

            Assert.IsFalse(warehouse.IsInStock(0));

            warehouse.AddStock(0, 10);
            Assert.AreEqual(10, warehouse.GetStockLevel(0));

            warehouse.RemoveStock(0, 5);
            Assert.AreEqual(5, warehouse.GetStockLevel(0));

            Assert.IsTrue(warehouse.IsInStock(0));
        }
    }
}
