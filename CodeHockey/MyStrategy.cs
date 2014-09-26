using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk 
{
    public partial class MyStrategy : IStrategy
    {
        public Puck puck;
        public Move move;
        public static Player Opp, My;
        public static Hockeyist OppGoalie;
        public static Hockeyist MyGoalie;
        public static World World;
        public static Game Game;
        
        public static double HoRadius;
        public static double RinkWidth, RinkHeight;
        public static Point RinkCenter;
        public static double PuckRadius;
        public static double HoPuckDist = 55.0;

        public int GetTicksToUp(AHo _ho, Point to, double take = -1)
        {
            var ho = _ho.Clone();
            var result = 0;
            for (; take < 0 ? !CanStrike(ho, to) : ho.GetDistanceTo2(to) > take*take; result++)
            {
                var turn = ho.GetAngleTo(to);
                var speedUp = GetSpeedTo(turn);
                ho.Move(speedUp, TurnNorm(turn));

                if (result > 500)
                    return result; // TODO: временный костыль, ибо почему-то падает
            }
            return result;
        }

        public int GetTicksToDown(AHo _ho, Point to, double take = -1)
        {
            var ho = _ho.Clone();
            var result = 0;
            const int limit = 40;
            for (; result < limit && (take < 0 ? !CanStrike(ho, to) : ho.GetDistanceTo2(to) > take * take); result++)
            {
                var turn = RevAngle(ho.GetAngleTo(to));
                var speedUp = -GetSpeedTo(turn);
                ho.Move(speedUp, TurnNorm(turn));
            }
            if (result >= limit)
                return Inf;
            return result;
        }

        public int GetTicksTo(Point to, Hockeyist my)
        {
            var ho = new AHo(my);
            var up = GetTicksToUp(ho, to);
            var down = GetTicksToDown(ho, to);
            if (up <= down)
                return up;
            return -down;
        }

        public Tuple<Point, int, int> GoToPuck(Hockeyist my, APuck pk)
        {
            var res = Inf;
            Point result = null;
            var dir = 1;
            var owner = World.Hockeyists.FirstOrDefault(x => x.Id == puck.OwnerHockeyistId);
            var ho = owner == null ? null : new AHo(owner);
            if (pk == null)
                pk = new APuck(Get(puck), GetSpeed(puck), Get(OppGoalie));
            else
                ho = null;

            int tLeft = 0, tRight = 300;
            var pks = new APuck[tRight + 1];
            var hhs = new AHo[tRight + 1];
            pks[0] = pk.Clone();
            hhs[0] = ho;
            for (int i = 1; i <= tRight; i++)
            {
                pks[i] = pks[i - 1].Clone();
                hhs[i] = ho == null ? null : hhs[i - 1].Clone();
                PuckMove(1, pks[i], hhs[i]);
            }
            while (tLeft <= tRight)
            {
                var c = (tLeft + tRight)/2;
                var needTicks = GetTicksTo(PuckMove(0, pks[c], hhs[c]), my);
                if (Math.Abs(needTicks) < c)
                {
                    tRight = c - 1;
                    res = c;
                    result = PuckMove(0, pks[c], hhs[c]);
                    dir = needTicks >= 0 ? 1 : -1;
                }
                else
                {
                    tLeft = c + 1;
                }
            }
            const int by = 16;
            for (var c = 0; c <= 70; c += c < by ? 1 : by)
            {
                var needTicks = GetTicksTo(PuckMove(0, pks[c], hhs[c]), my);
                if (Math.Abs(needTicks) <= c)
                {
                    for (var i = 0; i < by; i++, c--)
                    {
                        if (Math.Abs(needTicks) <= c)
                        {
                            res = c;
                            result = PuckMove(0, pks[c], hhs[c]);
                            dir = needTicks >= 0 ? 1 : -1;
                        }
                    }
                    break;
                }
            }
            if (result == null)
                result = Get(puck);
            return new Tuple<Point, int, int>(result, dir, res);
        }

        public void Move(Hockeyist self, World world, Game game, Move move)
        {
            ShowWindow();
            this.puck = world.Puck;
            this.move = move;
            MyStrategy.World = world;
            MyStrategy.Game = game;
            MyStrategy.Opp = world.GetOpponentPlayer();
            MyStrategy.My = world.GetMyPlayer();
            MyStrategy.RinkWidth = game.RinkRight - game.RinkLeft;
            MyStrategy.RinkHeight = game.RinkBottom - game.RinkTop;
            MyStrategy.OppGoalie = world.Hockeyists.FirstOrDefault(x => !x.IsTeammate && x.Type == HockeyistType.Goalie);
            MyStrategy.MyGoalie = world.Hockeyists.FirstOrDefault(x => x.IsTeammate && x.Type == HockeyistType.Goalie);
            MyStrategy.HoRadius = self.Radius;
            MyStrategy.RinkCenter = new Point(game.RinkLeft + RinkWidth/2, game.RinkTop + RinkHeight/2);
            MyStrategy.PuckRadius = puck.Radius;
            var friend =
                world.Hockeyists.FirstOrDefault(x => x.IsTeammate && x.Id != self.Id && x.Type != HockeyistType.Goalie);
            FillWayPoints();

            if (My.IsJustMissedGoal || My.IsJustScoredGoal)
            {

            }
            else
            {

                move.SpeedUp = Inf;
                var power = GetPower(self.SwingTicks);

                if (self.State == HockeyistState.Swinging && self.Id != puck.OwnerHockeyistId)
                {
                    move.Action = ActionType.CancelStrike;
                }
                else if (puck.OwnerHockeyistId == self.Id)
                {
                    drawInfo.Enqueue(
                        StrikeProbability(Get(puck), GetSpeed(self), GetPower(self.SwingTicks), self.Angle, Get(OppGoalie)) + "");

                    var wait = Inf;
                    double selTurn = 0, selSpeedUp = 0;
                    var willSwing = false;
                    var maxProb = 0.35;

                    if (self.State != HockeyistState.Swinging)
                    {
                        // если не замахнулся
                        for (var ticks = 0; ticks < 50; ticks++)
                        {
                            // если буду замахиваться (ТО В КОНЦЕ!!!), то нужно подождать минимум game.SwingActionCooldownTicks
                            var da = 0.01;

                            var moveDir = MyRight() && self.Y > RinkCenter.Y || MyLeft() && self.Y < RinkCenter.Y ? 1 : -1;
                            for (var moveTurn = 0.0; moveTurn <= 3 * da; moveTurn += da)
                            {
                                var turn = moveDir * moveTurn;

                                var end = ticks + game.SwingActionCooldownTicks;
                                var start = Math.Max(0, end - game.MaxEffectiveSwingTicks);
                                // когда начинаем замахиваться
                                var p = ProbabStrikeAfter(end - start, self, new[]
                                {
                                    new Tuple<int, double, double>(start, 1, turn),
                                    new Tuple<int, double, double>(end - start, 0, 0)
                                });
                                if (p > maxProb)
                                {
                                    wait = start;
                                    willSwing = true;
                                    maxProb = p;
                                    selTurn = turn;
                                    selSpeedUp = 1;
                                }

                                // если не буду
                                p = ProbabStrikeAfter(0, self,
                                    new[] {new Tuple<int, double, double>(ticks, 1, turn)});
                                if (p > maxProb)
                                {
                                    wait = ticks;
                                    willSwing = false;
                                    maxProb = p;
                                    selTurn = turn;
                                    selSpeedUp = 1;
                                }
                            }
                        }
                    }
                    else
                    {
                        // если уже замахнулся
                        for (var ticks = Math.Max(0, game.SwingActionCooldownTicks - self.SwingTicks);
                            ticks < 80;
                            ticks++)
                        {
                            var p = ProbabStrikeAfter(ticks + self.SwingTicks, self,
                                new[] {new Tuple<int, double, double>(ticks, 0, 0)});
                            if (p > maxProb)
                            {
                                wait = ticks;
                                willSwing = true;
                                maxProb = p;
                            }
                        }
                    }
                    drawInfo.Enqueue((wait == Inf ? 0 : maxProb) + "");
                    if (!willSwing && self.State == HockeyistState.Swinging)
                    {
                        move.Action = ActionType.CancelStrike;
                    }
                    else if (willSwing && wait == 0 && self.State != HockeyistState.Swinging)
                    {
                        move.Action = ActionType.Swing;
                    }
                    else if (wait == Inf)
                    {
                        var wayPoint = FindWayPoint(self);
                        if (wayPoint == null)
                        {
                            needPassQueue.Enqueue(Get(self));
                            if (!TryPass(Get(self), GetSpeed(self), self.Angle, self))
                            {
                                move.SpeedUp = 0;
                                move.Turn = self.GetAngleTo(friend);
                            }
                        }
                        else
                        {
                            move.Turn = self.GetAngleTo(wayPoint.X, wayPoint.Y);
                            move.SpeedUp = GetSpeedTo(move.Turn);
                        }
                    }
                    else if (wait == 0)
                    {
                        move.Action = ActionType.Strike;
                    }
                    else
                    {
                        move.SpeedUp = selSpeedUp;
                        move.Turn = selTurn;
                    }
                }
                else
                {
                    var owner = world.Hockeyists.FirstOrDefault(x => x.Id == puck.OwnerHockeyistId);
                    var pk = new APuck(Get(puck), GetSpeed(puck), Get(MyGoalie)) {IsDefend = true};

                    if (puck.OwnerPlayerId == Opp.Id && (CanStrike(self, owner) || CanStrike(self, puck)))
                    {
                        move.Action = ActionType.Strike;
                    }
                    else if (puck.OwnerPlayerId != My.Id && CanStrike(self, puck)
                             && Strike(new Point(puck), GetSpeed(self), power, self.Angle, Get(OppGoalie)))
                    {
                        move.Action = ActionType.Strike;
                    }
                    else if (puck.OwnerPlayerId != self.PlayerId && CanStrike(self, puck))
                    {
                        if (pk.Move(200, goalCheck: true) == 1) // если вратарь не отобьёт
                            move.Action = ActionType.Strike;
                        else
                            move.Action = ActionType.TakePuck;
                    }
                    else
                    {
                        var to = GetDefendPos2();
                        if (puck.OwnerPlayerId == My.Id || GoToPuck(self, null).Third > GoToPuck(friend, null).Third)
                        {
                            var puckBe = Get(puck);
                            StayOn(self, to, self.GetAngleTo(puckBe.X, puckBe.Y));
                        }
                        else
                        {
                            var To = GoToPuck(self, null);

                            if (To.Second > 0)
                            {
                                move.Turn = self.GetAngleTo(To.First.X, To.First.Y);
                                move.SpeedUp = GetSpeedTo(move.Turn);
                            }
                            else
                            {
                                move.Turn = RevAngle(self.GetAngleTo(To.First.X, To.First.Y));
                                move.SpeedUp = -GetSpeedTo(move.Turn);
                            }
                        }
                        drawGoalQueue.Enqueue(new Point(to));
                    }
                }
                if (Eq(move.SpeedUp, Inf))
                    move.SpeedUp = 1;
            }
#if DEBUG
            draw();
            Thread.Sleep(15);
#endif
            drawPathQueue.Clear();
            drawGoalQueue.Clear();
            drawGoal2Queue.Clear();
            drawInfo.Clear();
            needPassQueue.Clear();
        }
    }
}