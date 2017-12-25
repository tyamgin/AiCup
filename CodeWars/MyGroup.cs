using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class MyGroup
    {
        public readonly int Group;
        public readonly VehicleType VehicleType;

        public MyGroup(int group, VehicleType type)
        {
            Group = group;
            VehicleType = type;
        }

        public override string ToString()
        {
            return VehicleType + "(" + Group + ")";
        }
    }
}