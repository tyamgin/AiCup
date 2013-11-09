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
        void Debugger(string param, int Turn = -1)
        {
#if DEBUG
            if (param.Length <= 2)
            {
                int turn = int.Parse(param);
                if (turn == world.MoveIndex)
                {
                    turn = turn;
                }
            }
            else
            {
                // example: "FieldMedic Move 11 15"
                string[] args = param.Split(' ');
                if (self.Type.ToString() == args[0] &&
                    move.Action.ToString() == args[1] &&
                    move.X.ToString() == args[2] &&
                    move.Y.ToString() == args[3] &&
                    (Turn == -1 || Turn == world.MoveIndex)
                   )
                {
                    param = param;
                }
            }
#endif
        }



        void validateMove()
        {
#if DEBUG
            if (file == null)
            {
                string path = "TestFolder\\" + "log" + game.MoveCount + ".txt";
                FileStream fs = File.Create(path);
                fs.Close();
                file = new StreamWriter(path, true);
            }
            file.WriteLine(self.Type.ToString() + " " + move.Action.ToString() + " " + move.X + " " + move.Y);
            Console.WriteLine(self.Type.ToString() + " " + move.Action.ToString() + " " + move.X + " " + move.Y);
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
                // TODO: implement
                throw new NotImplementedException();
            }
            else if (move.Action == ActionType.Move)
            {
                if (self.ActionPoints < getMoveCost())
                    throw new Exception("");
                Point to = new Point(move.X, move.Y);
                Point ths = new Point(self.X, self.Y);
                if (!to.Nearest(ths) || to.X < 0 || to.Y < 0 || to.X >= width || to.Y >= height || map[to.X, to.Y] != 0)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.RaiseStance)
            {
                // TODO: implement
                throw new NotImplementedException();
            }
            else if (move.Action == ActionType.Shoot)
            {
                if (self.ShootCost > self.ActionPoints)
                    throw new Exception("");
                if (!world.IsVisible(self.ShootingRange, self.X, self.Y, self.Stance, move.X, move.Y, getTrooperAt(move.X, move.Y).Stance))
                    throw new Exception("");
                if (move.X == self.X && move.Y == self.Y)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= width || move.Y >= height || cells[move.X][move.Y] != 0)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.ThrowGrenade)
            {
                if (self.ActionPoints < game.GrenadeThrowCost)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= width || move.Y >= height || cells[move.X][move.Y] != 0)
                    throw new Exception("");
                if (!self.IsHoldingGrenade || game.GrenadeThrowRange < self.GetDistanceTo(move.X, move.Y))
                    throw new Exception("");
            }
            else if (move.Action == ActionType.UseMedikit)
            {
                if (game.MedikitUseCost > self.ActionPoints || !self.IsHoldingMedikit)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= width || move.Y >= height || !new Point(self.X, self.Y).Nearest(new Point(move.X, move.Y)))
                    throw new Exception("");

                // TODO:
            }
        }

    }
}
