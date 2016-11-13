using System;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class FinalMove
    {
        private Move _move;

        public FinalMove(Move move)
        {
            _move = move;
        }

        public double Turn
        {
            get { return _move.Turn; }
            set { _move.Turn = value; }
        }

        public double Speed
        {
            get { return _move.Speed; }
            set { _move.Speed = value; }
        }

        public double StrafeSpeed
        {
            get { return _move.StrafeSpeed; }
            set { _move.StrafeSpeed = value; }
        }

        public ActionType? Action
        {
            get { return _move.Action; }
            set { _move.Action = value; }
        }

        public double CastAngle
        {
            get { return _move.CastAngle; }
            set { _move.CastAngle = value; }
        }

        public double MinCastDistance
        {
            get { return _move.MinCastDistance; }
            set { _move.MinCastDistance = value; }
        }

        public double MaxCastDistance
        {
            get { return _move.MaxCastDistance; }
            set { _move.MaxCastDistance = value; }
        }

        public Message[] Messages
        {
            get { return _move.Messages; }
            set { _move.Messages = value; }
        }

        public void MoveTo(Point to, Point turnTo)
        {
            if (turnTo != null)
            {
                _move.Turn = MyStrategy.EnsureInterval(MyStrategy.Self.GetAngleTo(turnTo.X, turnTo.Y), MyStrategy.Game.WizardMaxTurnAngle);
            }

            if (to != null)
            {
                var angle = MyStrategy.Self.GetAngleTo(to.X,  to.Y);
                var cos = Math.Cos(angle);
                var fs = cos * (cos >= 0 ? MyStrategy.Game.WizardForwardSpeed : MyStrategy.Game.WizardBackwardSpeed);
                var ss = Math.Sin(angle) * MyStrategy.Game.WizardStrafeSpeed;
                _move.Speed = fs;
                _move.StrafeSpeed = ss;
            }
        }
    }
}
