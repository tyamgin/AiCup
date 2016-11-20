using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class ABuilding : ACombatUnit
    {
        public double Damage;
        public bool IsBesieded;
        public int OpponentsCount;
        public bool _isAssailable;

        public ABuilding(Building unit) : base(unit)
        {
            Damage = unit.Damage;
        }

        public ABuilding(ABuilding unit) : base(unit)
        {
            Damage = unit.Damage;
            IsBesieded = unit.IsBesieded;
            OpponentsCount = unit.OpponentsCount;
            _isAssailable = unit._isAssailable;
        }

        public override void SkipTick()
        {
            if (RemainingActionCooldownTicks > 0)
                RemainingActionCooldownTicks--;
        }

        public override void EthalonMove(ACircularUnit target)
        {
            SkipTick();
        }

        public override bool EthalonCanHit(ACircularUnit target)
        {
            return GetDistanceTo2(target) <= Geom.Sqr(CastRange + target.Radius);
        }

        public bool IsBase => Utility.Equals(MyStrategy.Game.FactionBaseRadius, Radius);

        public LaneType Lane => MyStrategy.GetLane(this);

        public int Order
        {
            get
            {
                if (IsBase)
                    return 2;

                if (BuildingsObserver.MyBase.Faction == Faction)
                    return BuildingsObserver.MyBase.GetDistanceTo2(this) < Geom.Sqr(Const.MapSize / 2 - Const.BaseBuildingDistance) ? 1 : 0;

                return BuildingsObserver.OpponentBase.GetDistanceTo2(this) < Geom.Sqr(Const.MapSize / 2 - Const.BaseBuildingDistance) ? 1 : 0;
            }
        }

        public override bool IsAssailable => _isAssailable;
    }
}
