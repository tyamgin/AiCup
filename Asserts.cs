using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.IO;
using System.Threading;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        void ValidateMove()
        {
#if DEBUG
            Console.WriteLine(world.MoveIndex + ") " + self.Type.ToString() + " " + move.Action.ToString() + " " + move.X + " " + move.Y);
            Thread.Sleep(50);
#endif

            if (move.Action == ActionType.EatFieldRation)
            {
                if (!self.IsHoldingFieldRation || game.FieldRationEatCost > self.ActionPoints)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.EndTurn)
            {

            }
            else if (move.Action == ActionType.Heal)
            {
                // TODO: implement
                if (self.Type != TrooperType.FieldMedic)
                    throw new Exception("");
                if (!new Point(self.X, self.Y).Nearest(new Point(move.X, move.Y)))
                    throw new Exception("");
            }
            else if (move.Action == ActionType.LowerStance)
            {
                if (self.ActionPoints < game.StanceChangeCost || self.Stance == TrooperStance.Prone)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.Move)
            {
                if (self.ActionPoints < GetMoveCost())
                    throw new Exception("");
                Point to = new Point(move.X, move.Y);
                Point ths = new Point(self.X, self.Y);
                if (!to.Nearest(ths) || to.X < 0 || to.Y < 0 || to.X >= Width || to.Y >= Height || map[to.X, to.Y] != 0)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.RaiseStance)
            {
                if (self.ActionPoints < game.StanceChangeCost || self.Stance == TrooperStance.Standing)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.Shoot)
            {
                if (self.ShootCost > self.ActionPoints)
                    throw new Exception("");
                if (!world.IsVisible(self.ShootingRange, self.X, self.Y, self.Stance, move.X, move.Y, GetTrooperAt(move.X, move.Y).Stance))
                    throw new Exception("");
                if (move.X == self.X && move.Y == self.Y)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= Width || move.Y >= Height || Cells[move.X][move.Y] != 0)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.ThrowGrenade)
            {
                if (self.ActionPoints < game.GrenadeThrowCost)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= Width || move.Y >= Height || Cells[move.X][move.Y] != 0)
                    throw new Exception("");
                if (!self.IsHoldingGrenade || game.GrenadeThrowRange < self.GetDistanceTo(move.X, move.Y))
                    throw new Exception("");
            }
            else if (move.Action == ActionType.UseMedikit)
            {
                if (game.MedikitUseCost > self.ActionPoints || !self.IsHoldingMedikit)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= Width || move.Y >= Height || !new Point(self.X, self.Y).Nearest(new Point(move.X, move.Y)))
                    throw new Exception("");

                // TODO:
            }
            else if (move.Action == ActionType.RequestEnemyDisposition)
            {
                if (self.Type != TrooperType.Commander || self.ActionPoints < game.CommanderRequestEnemyDispositionCost)
                    throw new Exception("");
            }
        }

    }
}
