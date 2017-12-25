using System;
using System.Collections.Generic;
using System.Linq;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class DangerResult
    {
        public struct ScoreDistancePair
        {
            public double Score;
            public double Distance;

            public ScoreDistancePair(double score, double distance)
            {
                Score = score;
                Distance = distance;
            }

            public double Computed => Score * Distance;

            public override string ToString()
            {
                return Computed + " (" + Score + " * " + Distance + ")";
            }
        }

        public double MyDurabilityDiff;
        public double OppDurabilityDiff;
        public double MyDeadsCount;
        public double OppDeadsCount;
        public double SumMaxAlmostAttacks;
        public double NuclearsPotentialDamage;
        public double RectanglesIntersects1;
        public double RectanglesIntersects2;
        public double FacilitiesPointsDiff;
        public List<Tuple<double, int>> RectanglesAreas = new List<Tuple<double, int>>();
        public List<Tuple<MyGroup, int, List<ScoreDistancePair>>> MoveToFacilitiesInfo = new List<Tuple<MyGroup, int, List<ScoreDistancePair>>>();
        public Dictionary<int, Point> TargetFacility = new Dictionary<int, Point>();
        public List<Tuple<MyGroup, int, List<ScoreDistancePair>>> MoveToInfo = new List<Tuple<MyGroup, int, List<ScoreDistancePair>>>();

        public string DebugDiffInfo(DangerResult other)
        {
            string res = "";
            if (!Geom.DoublesEquals(Score, other.Score))
                res += $"Score {Score - other.Score}\n\n";
            if (!Geom.DoublesEquals(MyDurabilityDiff, other.MyDurabilityDiff))
                res += $"MyDurabilityDiff {MyDurabilityDiff - other.MyDurabilityDiff}\n";
            if (!Geom.DoublesEquals(OppDurabilityDiff, other.OppDurabilityDiff))
                res += $"OppDurabilityDiff {OppDurabilityDiff - other.OppDurabilityDiff}\n";
            if (!Geom.DoublesEquals(MyDeadsCount, other.MyDeadsCount))
                res += $"MyDeadsCount {MyDeadsCount - other.MyDeadsCount}\n";
            if (!Geom.DoublesEquals(OppDeadsCount, other.OppDeadsCount))
                res += $"OppDeadsCount {OppDeadsCount - other.OppDeadsCount}\n";
            if (!Geom.DoublesEquals(SumMaxAlmostAttacks, other.SumMaxAlmostAttacks))
                res += $"SumMaxAlmostAttacks {SumMaxAlmostAttacks - other.SumMaxAlmostAttacks}\n";
            if (!Geom.DoublesEquals(NuclearsPotentialDamage, other.NuclearsPotentialDamage))
                res += $"NuclearsPotentialDamage {NuclearsPotentialDamage - other.NuclearsPotentialDamage}\n";
            if (!Geom.DoublesEquals(RectanglesIntersects1, other.RectanglesIntersects1))
                res += $"RectanglesIntersects1 {RectanglesIntersects1 - other.RectanglesIntersects1}\n";
            if (!Geom.DoublesEquals(RectanglesIntersects2, other.RectanglesIntersects2))
                res += $"RectanglesIntersects2 {RectanglesIntersects2 - other.RectanglesIntersects2}\n";
            if (!Geom.DoublesEquals(FacilitiesPointsDiff, other.FacilitiesPointsDiff))
                res += $"FacilitiesPointsDiff {FacilitiesPointsDiff - other.FacilitiesPointsDiff}\n";
            if (!Geom.DoublesEquals(MoveToSum, other.MoveToSum))
                res += $"MoveToSum {MoveToSum - other.MoveToSum}\n";
            if (!Geom.DoublesEquals(MoveToFacilitySum, other.MoveToFacilitySum))
                res += $"MoveToFacilitySum {MoveToFacilitySum - other.MoveToFacilitySum}\n";

            return res;
        }

        public double Score
        {
            get
            {
                var res = MyDurabilityDiff*1.15 - OppDurabilityDiff;
                res += MyDeadsCount*100;
                res -= OppDeadsCount*60;
                res += SumMaxAlmostAttacks/3;
                res += NuclearsPotentialDamage;
                res += RectanglesIntersects1*7000;
                res += RectanglesIntersects2*1000;
                res += MoveToSum/3;
                res += FacilitiesPointsDiff*110;
                res += MoveToFacilitySum*900;
                return res;
            }
        }

        public double GetMoveToSum(List<ScoreDistancePair> arr)
        {
            return arr.Sum(x => x.Computed);
        }

        public double[] MoveToSumByGroup
        {
            get { return MoveToInfo.Select(tpl => GetMoveToSum(tpl.Item3)).ToArray(); }
        }

        public static double AreaCoeff(double area, int count)
        {
            var r = Geom.Sqr(2 * G.VehicleRadius) * count / area;
            return Math.Pow(r, 0.25);
        }

        public double MoveToSum
        {
            get
            {
                double sum = 0;
                var arr = MoveToSumByGroup;
                for (var i = 0; i < arr.Length; i++)
                {
                    var idx = MoveToInfo[i].Item2;
                    sum += arr[i] * AreaCoeff(RectanglesAreas[idx].Item1, RectanglesAreas[idx].Item2);
                }
                return sum;
            }
        }

        public double[] MoveToFacilityByGroup
        {
            get { return MoveToFacilitiesInfo.Select(lst => lst.Item3.Sum(x => x.Computed)).ToArray(); }
        }

        public double MoveToFacilitySum
        {
            get
            {
                double sum = 0;
                var arr = MoveToFacilityByGroup;
                for (var i = 0; i < arr.Length; i++)
                {
                    var idx = MoveToFacilitiesInfo[i].Item2;
                    sum += arr[i] * AreaCoeff(RectanglesAreas[idx].Item1, RectanglesAreas[idx].Item2);
                }
                return sum;
            }
        }
    }
}
