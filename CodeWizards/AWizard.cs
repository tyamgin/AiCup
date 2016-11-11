using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AWizard : ACombatUnit
    {
        public AWizard(Wizard unit) : base(unit)
        {
        }

        public AWizard(AWizard unit) : base(unit)
        {
        }

        public void Move(double forwardSpeed, double strafeSpeed)
        {
            Y += Math.Sin(Angle)*forwardSpeed + Math.Cos(Angle)*strafeSpeed;
            X += Math.Cos(Angle)*forwardSpeed - Math.Sin(Angle)*strafeSpeed;
        }
    }
}
