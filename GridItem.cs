using System;

namespace HashCode2016 {
    public class GridItem {

        public int X { get; set; }
        public int Y { get; set; }

        public GridItem(int x, int y) {
            X = x;
            Y = y;
        }

        public int DistanceBetween(GridItem other) {
            double sum = Math.Pow(X - other.X, 2) + Math.Pow(Y - other.Y, 2);
            return (int)Math.Ceiling(Math.Sqrt(sum));
        }
    }
}
