using System.Collections.Generic;
using System.Linq;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class Clustering
    {
        public static List<VehiclesCluster> GetClustersSimple(AVehicle[] targetVehicles, double margin)
        {
            Logger.CumulativeOperationStart("Clustering2");

            var res = new List<VehiclesCluster>();
            var opened = new List<VehiclesCluster>();

            foreach (var type in Const.AllTypes)
            {
                var vehicles = targetVehicles.VehicleType(type).OrderBy(x => x.X).ThenBy(x => x.Y);
                foreach (var cur in vehicles)
                {
                    bool found = false;
                    for (var i = opened.Count - 1; i >= 0 && !found; i--)
                    {
                        var c = opened[i];
                        if (c.Last().X + margin < cur.X)
                        {
                            res.Add(c);
                            opened.RemoveAt(i);
                            continue;
                        }

                        for (var j = 0; j < 20 && j < c.Count; j++)
                        {
                            var nr = c[c.Count - 1 - j].GetDistanceTo2(cur) < margin * margin;
                            if (nr)
                            {
                                c.Add(cur);
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found)
                    {
                        var emptyCl = new VehiclesCluster();
                        emptyCl.Add(cur);
                        opened.Add(emptyCl);
                    }
                }
                res.AddRange(opened);
                opened.Clear();

                foreach (var cl in res)
                    cl.CompleteCluster();
            }

            Logger.CumulativeOperationEnd("Clustering2");
            return res;
        }
    }

    public class VehiclesCluster : List<AVehicle>
    {
        public Point Avg;
        public Rect BoundingRect;
        public double[] DurabilitySumByType = new double[5];
        public int[] CountByType = new int[5];

        public new void Add(AVehicle veh)
        {
            base.Add(veh);
            var type = (int)veh.Type;
            DurabilitySumByType[type] += veh.Durability;
            CountByType[type]++;
        }

        public void CompleteCluster()
        {
            Avg = Utility.Average(this);
            BoundingRect = Utility.BoundingRect(this);
        }
    }
}
