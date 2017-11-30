using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static AMove[] FacilitiesStrategy()
        {
            if (MoveObserver.AvailableActions == 0)
                return null;
            if (Environment.Nuclears.Any(x => x.IsMy))
                return null;

            return _setFactoryProduction() ?? _completeGroup();
        }


        private static AMove[] _setFactoryProduction()
        {
            var newFactories = Environment.MyVehicleFactories.Where(f => f.VehicleType == null).ToArray();
            if (newFactories.Length == 0)
                return null;

            var requiredType = Environment.GetVehicles(true, VehicleType.Fighter).Count <
                               Environment.GetVehicles(false, VehicleType.Fighter).Count
                ? VehicleType.Fighter
                : VehicleType.Helicopter;

            return new[]
            {
                new AMove
                {
                    Action = ActionType.SetupVehicleProduction,
                    VehicleType = requiredType,
                    FacilityId = newFactories[0].Id,
                }
            };
        }

        private static AMove[] _completeGroup()
        {
            if (MoveObserver.AvailableActions < 2)
                return null;

            var lowerBound = Environment.MyVehicles.Count*40/500.0;
            var newVehicles = Environment.MyVehicles.Where(x => x.Groups == 0).ToArray();
            if (newVehicles.Length < lowerBound)
                return null;

            var ungroupedEnv = new Sandbox(
                newVehicles,
                new ANuclear[] {},
                new AFacility[] {},
                clone: true
                );

            var clusters = ungroupedEnv.GetClusters(true, Const.ClusteringMargin*2);
            if (clusters.Count == 0)
                return null;

            var largestSize = clusters.Max(c => c.CountByType.Max());
            if (largestSize < lowerBound)
                return null;

            foreach (var cl in clusters)
            {
                for (var i = 0; i < cl.CountByType.Length; i++)
                {
                    if (cl.CountByType[i] == largestSize)
                    {
                        return new[]
                        {
                            new AMove
                            {
                                Action = ActionType.ClearAndSelect,
                                VehicleType = (VehicleType) i,
                                Rect = Utility.BoundingRect(cl),
                            },
                            new AMove
                            {
                                Action = ActionType.Assign,
                                Group = GroupsManager.NextGroupId,
                            },
                        };
                    }
                }
            }
            return null;
        }
    }
}
