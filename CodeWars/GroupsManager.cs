using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class GroupsManager
    {
        public const int StartingFightersGroupId = 1;
        public const int StartingHelicoptersGroupId = 2;
        public const int StartingTanksGroupId = 3;
        public const int StartingIfvsGroupId = 4;
        public const int StartingArrvsGroupId = 5;

        private static int _firstFreeId;

        public static List<VehicleType> GroupLeaders = new List<VehicleType>
        {
            VehicleType.Fighter, //fictive value at 0 index
            VehicleType.Fighter,
            VehicleType.Helicopter,
            VehicleType.Tank,
            VehicleType.Ifv,
            VehicleType.Arrv,
        };

        public static List<MyGroup> MyGroups = new List<MyGroup>();
        private static List<MyGroup> _pendingGroups = new List<MyGroup>();

        public static VehicleType GroupLeader(MyGroup group)
        {
            if (group.Type != null)
                return (VehicleType)group.Type;
            if (group.Group == null)
                throw new Exception("Trying to use invalid group");
            var g = (int)group.Group;

            return GroupLeaders[g];
        }

        public static void Update(Sandbox env)
        {
            var used = new bool[G.MaxUnitGroup + 1];
            foreach (var veh in env.MyVehicles)
                foreach (var g in veh.GroupsList)
                    used[g] = true;

            _firstFreeId = -1;
            for (var i = 1; i < used.Length; i++)
            {
                if (!used[i])
                {
                    _firstFreeId = i;
                    break;
                }
            }

            if (_firstFreeId == 31)
                throw new Exception("Need no increase groups capacity");
            if (_firstFreeId == -1)
                throw new Exception("Has no more free group Id's");

            // remove empty groups
            for (var i = MyGroups.Count - 1; i >= 0; i--)
            {
                if (env.GetVehicles(true, MyGroups[i]).Count == 0)
                {
                    MyGroups.RemoveAt(i);
                }
            }

            for (var i = _pendingGroups.Count - 1; i >= 0; i--)
            {
                var g = _pendingGroups[i];
                if (env.MyVehicles.Any(x => x.IsGroup(g)))
                {
                    MyGroups.Add(g);
                    _pendingGroups.RemoveAt(i);
                }
            }
        }

        public static void AddPendingGroup(MyGroup group, VehicleType leader)
        {
            _pendingGroups.Add(group);
            if (group.Group != null)
            {
                while (GroupLeaders.Count - 1 < group.Group)
                    GroupLeaders.Add(VehicleType.Fighter);//default value
                GroupLeaders[(int) group.Group] = leader;
            }
        }

        public static int NextGroupId => _firstFreeId;
    }
}
