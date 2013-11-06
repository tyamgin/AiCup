using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Threading;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static int Inf = 0x3f3f3f3f;

        World world;
        Move move;
        Trooper self;
        Game game;
        Trooper[] troopers;
        ArrayList team, friend;
        Bonus[] bonuses;
        int[,] map;
        CellType[][] cells;

        void Go(int toX, int toY)
        {
            move.X = toX;
            move.Y = toY;
            if (move.Action == ActionType.Move && self.X == toX && self.Y == toY)
                move.Action = ActionType.EndTurn; // TODO:
            if (map[move.X, move.Y] != 0 && move.Action == ActionType.Move) // TODO: это костыль
                move.Action = ActionType.EndTurn;
#if DEBUG
            Console.WriteLine(move.Action.ToString() + " " + move.X + " " + move.Y);
            //Thread.Sleep(100);
#endif
            validateMove();
        }

        void Go(Point to)
        {
            Go(to.X, to.Y);
        }

        int getMoveCost()
        {
            if (self.Stance == TrooperStance.Prone)
                return game.ProneMoveCost;
            if (self.Stance == TrooperStance.Kneeling)
                return game.KneelingMoveCost;
            if (self.Stance == TrooperStance.Standing)
                return game.StandingMoveCost;
            throw new Exception("something wrong");
        }

        Trooper get(int x, int y)
        {
            foreach(Trooper tr in troopers)
                if (tr.X == x && tr.Y == y)
                    return tr;
            return null;
        }

        TrooperType[] commanderPriority = { TrooperType.Commander, TrooperType.Sniper, TrooperType.Soldier, TrooperType.FieldMedic, TrooperType.Scout };

        Trooper getCommander()
        {
            foreach(TrooperType type in commanderPriority)
                foreach (Trooper tr in team)
                    if (tr.Type == type)
                        return tr;
            throw new Exception("Have no player in my team");
        }

        void validateMove()
        {
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
                if (!to.Nearest(ths) || to.X < 0 || to.Y < 0 || to.X >= world.Width || to.Y >= world.Height || map[to.X, to.Y] != 0)
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
                if (!world.IsVisible(self.ShootingRange, self.X, self.Y, self.Stance, move.X, move.Y, TrooperStance.Standing))
                    throw new Exception("");
                if (move.X == self.X && move.Y == self.Y)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= world.Width || move.Y >= world.Height || cells[move.X][move.Y] != 0)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.ThrowGrenade)
            {
                if (self.ActionPoints < game.GrenadeThrowCost)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= world.Width || move.Y >= world.Height || cells[move.X][move.Y] != 0)
                    throw new Exception("");
                if (!self.IsHoldingGrenade || game.GrenadeThrowRange < self.GetDistanceTo(move.X, move.Y))
                    throw new Exception("");
            }
            else if (move.Action == ActionType.UseMedikit)
            {
                if (game.MedikitUseCost > self.ActionPoints || !self.IsHoldingMedikit)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= world.Width || move.Y >= world.Height || !new Point(self.X, self.Y).Nearest(new Point(move.X, move.Y)))
                    throw new Exception("");

                // TODO:
            }
        }
    }
}
