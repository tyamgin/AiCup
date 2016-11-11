using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        private AProjectile _proj;

        void _testMagicMissile()
        {
            if (World.TickIndex < 10)
            {
                FinalMove.Turn = 10;
                return;
            }

            if (_proj == null)
            {
                FinalMove.Action = ActionType.MagicMissile;
                _proj = new AProjectile(new AWizard(Self), 0, ProjectileType.MagicMissile);
                var path = EmulateMagicMissile(_proj);
                return;
            }
            //_proj.Move();
            return;
        }

        private AWizard _wz;

        bool _testWizardMove()
        {
            if (World.TickIndex < 200)
                return false;

            if (_wz == null)
            {
                _wz = new AWizard(Self);
                _wz.Move(1.5, -0.7);
                FinalMove.Speed = 1.5;
                FinalMove.StrafeSpeed = -0.7;
                return true;
            }
            var nw = new AWizard(Self);
            return true;
        }
    }
}
