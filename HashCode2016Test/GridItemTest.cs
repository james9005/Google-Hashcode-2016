using Microsoft.VisualStudio.TestTools.UnitTesting;
using HashCode2016;

namespace HashCode2016Test {
    [TestClass]
    public class GridItemTest {
        [TestMethod]
        public void TestDistanceBetween() {

            // 3x3 grid.
            GridItem gi00 = new GridItem(0, 0);
            GridItem gi10 = new GridItem(1, 0);
            GridItem gi20 = new GridItem(2, 0);
            GridItem gi01 = new GridItem(0, 1);
            GridItem gi11 = new GridItem(1, 1);
            GridItem gi21 = new GridItem(2, 1);
            GridItem gi02 = new GridItem(0, 2);
            GridItem gi12 = new GridItem(1, 2);
            GridItem gi22 = new GridItem(2, 2);

            // Same position.
            Assert.AreEqual(0, gi00.DistanceBetween(gi00));

            // Straight lines.
            Assert.AreEqual(1, gi00.DistanceBetween(gi10));
            Assert.AreEqual(1, gi00.DistanceBetween(gi01));
            Assert.AreEqual(2, gi00.DistanceBetween(gi02));
            Assert.AreEqual(2, gi00.DistanceBetween(gi20));
            Assert.AreEqual(2, gi20.DistanceBetween(gi00));
            Assert.AreEqual(2, gi02.DistanceBetween(gi00));
            Assert.AreEqual(1, gi01.DistanceBetween(gi00));
            Assert.AreEqual(1, gi10.DistanceBetween(gi00));

            // Diagonals.
            Assert.AreEqual(2, gi00.DistanceBetween(gi11)); // 1.41 rounded up
            Assert.AreEqual(2, gi11.DistanceBetween(gi00)); // 1.41 rounded up
            Assert.AreEqual(2, gi12.DistanceBetween(gi21)); // 1.41 rounded up
            Assert.AreEqual(3, gi00.DistanceBetween(gi12)); // 2.24 rounded up
            Assert.AreEqual(3, gi00.DistanceBetween(gi21)); // 2.24 rounded up
            Assert.AreEqual(3, gi00.DistanceBetween(gi22)); // 2.83 rounded up
        }
    }
}
