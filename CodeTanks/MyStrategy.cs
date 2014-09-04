/*
 * 
 * 1)  done ---- Реализовать фанкцию Нужно(Bonus), которая определяет нужен ли данный бонус
 * 2)  ??? Анализ стрельбы на опережение
 * 3)  done ---- Пересмотреть повороты
 * 4)  done ---- НЕ ИДТИ ЧЕРЕЗ ЦЕНТР когда двигаюсь к максимально удалённой точке
 * 5)  done ---- Уварачивание от снарядов
 * 6)  done ---- обзор поблизости кто направил пушку
 * 7)  done ---- узнать почему я стреляю по бонусам!!!
 * 8)  ??? overkill - не нужно
 * 9)  done ---- если у границы, то не ходить за бонусом если он не необходим
 * 10) подставить под рикошет
 * 11) ??? если я хочу стрелять далеко, и есть очень близкие танки, то стрелять в них
 * 12) ??? если я возле стены и существует танк который на меня нацелился, то ехать перпендикулярно ему.
 * 13) done ---- проверять что я не стреляю в рикошет!!!
 * 14) проверять что я не стреляю очень близко к краю танка в стороны 0, 2 (иначе оно рикошетит!)
 *
 * 
 * 
 * 
 * */

using System;
using Com.CodeGame.CodeTanks2012.DevKit.CSharpCgdk.Model;


namespace Com.CodeGame.CodeTanks2012.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private double x1, y1, x2, y2;
        private double[] rectX, rectY;

        private void GetCoordinates(Unit u, double[] X, double[] Y, double error = 1.0)
        {
            double x = u.X;
            double y = u.Y;
            double w = error * u.Width / 2;
            double h = error * u.Height / 2;
            double angle = u.Angle;

            X[0] = w;
            Y[0] = h;
            X[1] = w;
            Y[1] = -h;
            X[2] = -w;
            Y[2] = -h;
            X[3] = -w;
            Y[3] = h;

            for (int i = 0; i < 4; i++)
            {
                double nx = RotatedX(X[i], Y[i], angle) + x;
                Y[i] = RotatedY(X[i], Y[i], angle) + y;
                X[i] = nx;
            }
        }

        private bool Intersected(double x1, double y1, double x2, double y2, double[] X, double[] Y, double error = 1.0)
        {
            for (int i = 0; i < X.Length; i++)
                if (SegmentIntersected(X[i], Y[i], X[(i + 1) % X.Length], Y[(i + 1) % X.Length], x1, y1, x2, y2))
                    return true;
            return false;
        }

        private bool Intersected(Unit unit, Tank self, Unit goal, double error = 1.0)
        {
            double[] X = new double[4];
            double[] Y = new double[4];
            GetCoordinates(unit, X, Y, error);
            return Intersected(goal.X, goal.Y, self.X, self.Y, X, Y, error);
        }

        private int TryFindBonus(Tank self, BonusType btype, bool any = false)
        {
            int bonusId = world.Bonuses.Length;
            double maxK = 0;

            for (int i = 0; i < world.Bonuses.Length; i++)
            {
                if ((any || world.Bonuses[i].Type == btype) && IsNeed(self, world.Bonuses[i]))
                {
                    double x = world.Bonuses[i].X;
                    double y = world.Bonuses[i].Y;
                    if ((!InRect(x1, x2, y1, y2, x, y) && !Intersected(self.X, self.Y, x, y, rectX, rectY)) || CountAlive() < 4)
                    {
                        int interCount = 0;
                        Intersected(self, world.Bonuses[i], ref interCount);
                        double k = TankCoeff(self, world.Bonuses[i]);
                        double d = self.GetDistanceTo(world.Bonuses[i]);

                        if (interCount == 0 && k > maxK && (d < self.Width * 6 || CountAlive() < 3))
                        {
                            maxK = k;
                            bonusId = i;
                        }
                    }
                }
            }
            return bonusId;
        }

        private bool InRect(double x1, double x2, double y1, double y2, double x, double y)
        {
            return x1 <= x && x <= x2 && y1 <= y && y <= y2;
        }

        private int SelectBonus(Tank self)
        {
            int bonusId = world.Bonuses.Length;

            // ищем здоровье
            if (self.CrewHealth / (double)self.CrewMaxHealth < 0.7)
            {
                int selected = TryFindBonus(self, BonusType.Medikit);
                if (selected != world.Bonuses.Length)
                    return selected;
            }

            // ищем броню
            if (self.HullDurability / (double)self.HullMaxDurability < 0.7)
            {
                int selected = TryFindBonus(self, BonusType.RepairKit);
                if (selected != world.Bonuses.Length)
                    return selected;
            }

            // ищем боеприпасы
            if (self.PremiumShellCount < 2)
            {
                int selected = TryFindBonus(self, BonusType.AmmoCrate);
                if (selected != world.Bonuses.Length)
                    return selected;
            }

            if (InRect(x1, x2, y1, y2, self.X, self.Y) && CountAlive() > 2)
                return world.Bonuses.Length;

            if (DistanseToBorder(self) < self.Width * 1.7 && CountAlive() > 2)
                return world.Bonuses.Length;

            // ищем что лучше
            return TryFindBonus(self, BonusType.AmmoCrate, true);
        }

        private bool Intersected(Tank self, Unit goal)
        {
            int tmp = 0;
            return Intersected(self, goal, ref tmp);
        }

        private bool FFF(Tank self, Unit goal, Tank en)
        {
            for (int i = 0; i < world.Tanks.Length; i++)
            {
                if (self.Id != world.Tanks[i].Id && world.Tanks[i].Id != en.Id && Intersected(world.Tanks[i], self, goal, 1.2) && self.GetDistanceTo(world.Tanks[i]) < self.GetDistanceTo(en))
                {
                    return true;
                }
            }

            // for (int i = 0; i < world.Obstacles.Length; i++) if (goal.Id != world.Obstacles[i].Id && Intersected(world.Obstacles[i], self, goal)) return true;
            for (int i = 0; i < world.Bonuses.Length; i++)
                if (goal.Id != world.Bonuses[i].Id && Intersected(world.Bonuses[i], self, goal, 1.3) && self.GetDistanceTo(world.Bonuses[i]) < self.GetDistanceTo(en))
                    return true;
            return false;
        }

        // count - количество препятствующих танков
        private bool Intersected(Tank self, Unit goal, ref int count)
        {
            count = 0;
            bool result = false;
            for (int i = 0; i < world.Tanks.Length; i++)
            {
                if (self.Id != world.Tanks[i].Id && world.Tanks[i].Id != goal.Id && Intersected(world.Tanks[i], self, goal, 1.2))
                {
                    result = true;
                    count++;
                }
            }

            for (int i = 0; i < world.Obstacles.Length; i++) 
                if (goal.Id != world.Obstacles[i].Id && Intersected(world.Obstacles[i], self, goal, 1.05)) 
                    result = true;
            for (int i = 0; i < world.Bonuses.Length; i++)
                if (goal.Id != world.Bonuses[i].Id && Intersected(world.Bonuses[i], self, goal, 1.3))
                    result = true;
            return result;
        }

        private double NewAngle(double angle)
        {
            if (angle - Eps > Angle[180] || angle + Eps < -Angle[180])
                throw new Exception("");
            if (Math.Abs(angle) > Angle[90])
            {
                if (angle > 0)
                    angle -= Math.PI;
                else
                    angle += Math.PI;
            }
            return angle;
        }

        private void Go(Tank self, double x, double y)
        {
            double angle = self.GetAngleTo(x, y);

            // Если слишком близко к цели (то есть с точностью до размера танка уже на ней)
            if (self.GetDistanceTo(x, y) < self.Width * 0.7)
                return;

            bool reverse = false;
            if (Math.Abs(angle) > Angle[90])
            {
                reverse = true;
                angle = NewAngle(angle);
            }

            if (world.Tick < 200)
            {
                if (angle > Angle[30])
                {         // если угол сильно положительный,
                    SetMove(1, -1);
                }
                else if (angle < -Angle[30])
                {  // если угол сильно отрицательный,
                    SetMove(-1, 1);
                }
                else
                {
                    SetMove(1, 1);
                }
            }
            else
            {
                if (angle > 0)
                    SetMove(1, Math.Cos(angle));
                else
                    SetMove(Math.Cos(angle), 1);

                if (self.GetDistanceTo(x, y) < self.Width * 5)
                {
                    if (move.LeftTrackPower < 0.8)
                        move.LeftTrackPower = -1;
                    if (move.RightTrackPower < 0.8)
                        move.RightTrackPower = -1;
                }
            }

            if (reverse)
            {
                SetMove(-move.RightTrackPower, -move.LeftTrackPower);
            }
        }

        // returns - 0 - не пересекает, 1 - пересекает, 2 - пересекает, но есть шанс уклониться
        private int CheckToBeWoundedBy(Shell shell, Tank self, int speedK = 2)
        {
            // speedK == 2 - двигаться как обычно
            // speedK == 0 - стоять
            // speedL == 1 - вперёд
            // speedK == -1 - назад

            int steps = 0;
            double x = shell.X, y = shell.Y;
            Tank t = null;
            double selfDx = SelfSpeed * Math.Cos(self.Angle);
            double selfDy = SelfSpeed * Math.Sin(self.Angle);

            while (x >= 0 && x < world.Width && y >= 0 && y < world.Height)
            {
                if (t == null || !OutOfRange(t, 0.9))
                {
                    if (speedK == 2)
                        t = new Tank(0, "", 0, steps * self.SpeedX + self.X, steps * self.SpeedY + self.Y, self.SpeedX, self.SpeedY, self.Angle, self.AngularSpeed, self.TurretRelativeAngle, self.CrewHealth, self.HullDurability, self.ReloadingTime, self.RemainingReloadingTime, self.PremiumShellCount, self.IsTeammate, self.Type);
                    else if (speedK == 0)
                        t = self;
                    else
                        t = new Tank(0, "", 0, steps * selfDx * speedK + self.X, steps * selfDy * speedK + self.Y, self.SpeedX, self.SpeedY, self.Angle, self.AngularSpeed, self.TurretRelativeAngle, self.CrewHealth, self.HullDurability, self.ReloadingTime, self.RemainingReloadingTime, self.PremiumShellCount, self.IsTeammate, self.Type);
                }
                int inunit = InUnit(t, x, y, 1.2);
                if (inunit != -1)
                {
                    if (shell.Type == ShellType.Premium)
                        return 1;
                    double angle = Math.Abs(NormAngle(self.Angle - shell.Angle));
                    if (angle > Angle[90])
                        angle = Angle[180] - angle;

                    if (inunit == 0 || inunit == 2)
                        return 2;
                    if (angle > Angle[20])
                        return 1;
                    return 0;
                }
                x += shell.SpeedX;
                y += shell.SpeedY;
                steps++;
            }
            return 0;
        }

        private int CheckToBeWounded(Tank self, int speedK)
        {
            double maxDist = 0;
            int pos = world.Shells.Length;
            for (int i = 0; i < world.Shells.Length; i++)
            {
                int check = CheckToBeWoundedBy(world.Shells[i], self, speedK);
                if (check != 0)
                {
                    double dist = self.GetDistanceTo(world.Shells[i]);
                    if (maxDist < dist)
                    {
                        maxDist = dist;
                        pos = i;
                    }
                }
            }
            if (pos == world.Shells.Length)
                return 0;
            return CheckToBeWoundedBy(world.Shells[pos], self, speedK);
        }

        private bool InCorner(Tank tank)
        {
            double[] d = new double[] { tank.X, tank.Y, world.Width - tank.X, world.Height - tank.Y };
            int cnt = 0;
            for (int i = 0; i < 4; i++)
                if (d[i] < tank.Width)
                    cnt++;
            return cnt >= 2;
        }

        private bool IsFreePosition(double x, double y, Tank self)
        {
            for (int i = 0; i < world.Tanks.Length; i++)
            {
                if (world.Tanks[i].Id != self.Id)
                {
                    if (world.Tanks[i].GetDistanceTo(x, y) < self.Width)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        private double[] FindBestPosition(Tank self)
        {
            // ищу точку куда ехать
            double maxX = 0, maxY = 0, mx = 0;
            int countAlive = CountAlive();
            int add = countAlive > 3 ? 10 : 1;
            double Dist = 10000;

            if (InCorner(self) && countAlive >= 4 && gameType == 0)
            {
                return new double[] { self.X, self.Y };
            }

            if (countAlive > 3 && gameType == 0)
            {
                for (int i = 0; i < 2; i++)
                {
                    for (int j = 0; j < 2; j++)
                    {
                        double x = world.Width * i;
                        double y = world.Height * j;

                        if (InRect(x1, x2, y1, y2, self.X, self.Y) || !Intersected(self.X, self.Y, x, y, rectX, rectY))
                        {
                            if (IsFreePosition(x, y, self) && self.GetDistanceTo(x, y) < Dist)
                            {
                                Dist = self.GetDistanceTo(x, y);
                                maxX = x;
                                maxY = y;
                            }
                        }
                    }
                }
                if (Dist != 10000)
                    return new double[] { maxX, maxY };
            }

            for (int i = 0; i <= 10; i += 1)
            {
                for (int j = 0; j <= 10; j += add)
                {
                    double x = world.Width / 10 * i;
                    double y = world.Height / 10 * j;

                    bool ok = true;
                    foreach (Tank t in world.Tanks)
                    {
                        if (IsAlive(t) && t.Id != self.Id && t.GetDistanceTo(x, y) < self.Width * 2.5)
                        {
                            ok = false;
                            break;
                        }
                    }
                    if (!ok)
                        continue;


                    if (!InRect(x1, x2, y1, y2, x, y) && (InRect(x1, x2, y1, y2, self.X, self.Y) || !Intersected(self.X, self.Y, x, y, rectX, rectY) || countAlive < 4))
                    {
                        if (/*self.GetDistanceTo(x, y) < world.Height * 0.7*/true)
                        {
                            double minV = 10000;
                            foreach (Tank tank in world.Tanks)
                            {
                                if (!tank.IsTeammate && IsAlive(tank))
                                {
                                    double d = tank.GetDistanceTo(x, y);
                                    minV = Math.Min(minV, d);
                                }
                            }
                            if (minV > mx)
                            {
                                mx = minV;
                                maxX = x;
                                maxY = y;
                            }
                            if (minV == mx && self.GetDistanceTo(maxX, maxY) > self.GetDistanceTo(x, y))
                            {
                                mx = minV;
                                maxX = x;
                                maxY = y;
                            }
                        }
                    }
                }
            }
            return new double[] { maxX, maxY };
        }

        private bool End()
        {
            int countOpponents = 0;
            int countI = 0;
            for (int i = 0; i < world.Tanks.Length; i++)
            {
                if (IsAlive(world.Tanks[i]))
                {
                    if (world.Tanks[i].IsTeammate)
                        countI++;
                    else
                        countOpponents++;
                }
            }
            return countI > countOpponents;
        }

        int noFire = 0;

        public void Move(Tank self, World world, Move move)
        {
            if (start == -1)
            {
                start = self.TeammateIndex == 0 ? 10 : 20;
                int cnt = 0;
                foreach (Tank t in world.Tanks)
                    if (t.IsTeammate)
                        cnt++;
                gameType = cnt - 1;
            }

            // определяю центральный прямоугольник
            x1 = world.Width * 0.25;
            x2 = world.Width - x1;
            y1 = world.Height * 0.25;
            y2 = world.Height - y1;
            rectX = new double[] { x1, x2, x2, x1 };
            rectY = new double[] { y1, y1, y2, y2 };
            this.world = world;
            this.move = move;

            int bonusId = SelectBonus(self);

            int selectedTank = world.Tanks.Length;

            if (bonusId != world.Bonuses.Length)
            {
                Go(self, world.Bonuses[bonusId].X, world.Bonuses[bonusId].Y);
            }
            else
            {
                double[] bestPos = FindBestPosition(self);
                Go(self, bestPos[0], bestPos[1]);
            }

            // теперь находим максимально удобную цель для стрельбы
            selectedTank = world.Tanks.Length;
            double maxK = 0;
            bool goalFinded = false;
            for (int i = 0; i < world.Tanks.Length; i++)
            {
                if (!world.Tanks[i].IsTeammate && IsAlive(world.Tanks[i]) && !Intersected(self, world.Tanks[i]))
                {
                    double k = TurretCoeff(self, world.Tanks[i]);
                    if (k > maxK)
                    {
                        maxK = k;
                        selectedTank = i;
                        goalFinded = true;
                    }
                }
            }

            if (!goalFinded)
            {
                // если не нашли цель для стрельбы - ищем не обращая внимания на препятствия
                for (int i = 0; i < world.Tanks.Length; i++)
                {
                    if (!world.Tanks[i].IsTeammate && IsAlive(world.Tanks[i]))
                    {
                        double k = TurretCoeff(self, world.Tanks[i]);
                        if (k > maxK)
                        {
                            maxK = k;
                            selectedTank = i;
                        }
                    }
                }
            }
            if (selectedTank == world.Tanks.Length)
                return;


            bool gogogo = false;
            if (world.Tick > 200)
            {
                // Проверяю что в меня могут попасть
                int check = CheckToBeWounded(self, 2);
                if (check != 0)
                {
                    int c0 = CheckToBeWounded(self, 0);
                    // Стоять на месте
                    if (c0 == 0)
                    {
                        SetMove(0, 0);
                        gogogo = true;
                    }
                    else
                    {
                        double speed = GetSpeed(self);
                        int c1 = CheckToBeWounded(self, 1);
                        // Вперёд
                        double AngleBetweenSelfAndSpeed = self.Angle - Math.Atan2(self.SpeedY, self.SpeedX); // угол между скоростью и мной
                        AngleBetweenSelfAndSpeed = NormAngle(AngleBetweenSelfAndSpeed);
                        if (AngleBetweenSelfAndSpeed < 0)
                            AngleBetweenSelfAndSpeed = -AngleBetweenSelfAndSpeed;

                        if (c1 == 0 && (speed < 0.15 || AngleBetweenSelfAndSpeed < Angle[90]))
                        {
                            SetMove(1, 1);
                            gogogo = true;
                        }
                        else
                        {
                            int c2 = CheckToBeWounded(self, -1);
                            // Назад
                            if (c2 == 0)
                            {
                                SetMove(-1, -1);
                                gogogo = true;
                            }
                            else
                            {
                                if (DistanseToBorder(self) < self.Width)
                                {
                                    if (!OutOfRange(FictiveTank(self.X + 20 * Math.Cos(self.Angle), self.Y + 20 * Math.Sin(self.Angle), 3452343), 0.85))
                                        SetMove(1, 1);
                                    else
                                        SetMove(-1, -1);
                                    gogogo = true;
                                }
                                // Если приходится ставиться под рикошет
                                else if (c1 == 2 || c2 == 2)
                                {
                                    SetMove(1, -1);
                                    gogogo = true;
                                }
                            }
                        }
                    }
                }
            }

            /*
            // Поиск тех кто направил на меня пушку
            int fireTo = -1;
            double mindist = 10000;
            for (int i = 0; i < world.Tanks.Length; i++)
            {
                if (!world.Tanks[i].IsTeammate && IsAlive(world.Tanks[i]))
                {
                    double angle = world.Tanks[i].GetTurretAngleTo(self);
                    if (Math.Abs(angle) < Angle[5])
                    {
                        double d = self.GetDistanceTo(world.Tanks[i]);
                        if (d < mindist)
                        {
                            mindist = d;
                            fireTo = i;
                        }
                    }
                }
            }
            if (fireTo != -1 && self.GetDistanceTo(world.Tanks[fireTo]) < self.Width * 5)
            {
                selectedTank = fireTo;
                goalFinded = true;
            }*/

            if (!gogogo && CountAlive() == 2 && bonusId != world.Bonuses.Length)
            {

                // Поиск тех кто направил пушку и стоит перпендикулярно мне
                for (int i = 0; i < world.Tanks.Length; i++)
                {
                    if (!world.Tanks[i].IsTeammate && IsAlive(world.Tanks[i]))
                    {
                        double AngleBetweenSelfAndHisTurret = Math.Abs(NormAngle(self.Angle - GetTurretAngle(world.Tanks[i])));
                        if (AngleBetweenSelfAndHisTurret > Angle[90])
                            AngleBetweenSelfAndHisTurret = Angle[180] - AngleBetweenSelfAndHisTurret;


                        // если он под углом меньше 30
                        if (AngleBetweenSelfAndHisTurret < Angle[30] && world.Tanks[i].GetTurretAngleTo(self) < Angle[20])
                        {
                            // Поворачиваемся перпендикулярно и отъезжаем
                            double an1 = self.Angle + Angle[90];
                            double an2 = self.Angle - Angle[90];
                            double X1 = self.X + 300 * Math.Cos(an1);
                            double Y1 = self.Y + 300 * Math.Sin(an1);
                            double X2 = self.X + 300 * Math.Cos(an2);
                            double Y2 = self.Y + 300 * Math.Sin(an2);
                            if (!OutOfRange(X1, Y1))
                            {
                                Go(self, X1, Y1);
                                /*xGoAway = X1;
                                yGoAway = Y1;
                                goAway = 50;*/
                            }
                            else if (!OutOfRange(X2, Y2))
                            {
                                Go(self, x2, y2);
                                /*xGoAway = X2;
                                yGoAway = Y2;
                                goAway = 50;*/
                            }
                        }
                    }
                }

            }

            if (End())
            {
                foreach (Tank t in world.Tanks)
                {
                    if (!t.IsTeammate && IsAlive(t))
                    {
                        Go(self, t.X, t.Y);
                        break;
                    }
                }
            }

            double Dist = self.GetDistanceTo(world.Tanks[selectedTank]);
            move.TurretTurn = self.GetTurretAngleTo(world.Tanks[selectedTank]);

            if (goalFinded && Hit(self, world.Tanks[selectedTank]))
            {
                double angle = Math.Abs(NormAngle(world.Tanks[selectedTank].Angle - NormAngle(self.GetTurretAngleTo(world.Tanks[selectedTank]) + self.Angle + self.TurretRelativeAngle)));
                if (angle > Angle[90])
                    angle = Angle[180] - angle;

                if (!(angle > Angle[60] && Dist > world.Height * 0.7) || noFire > 300)
                {
                    noFire = 0;
                    if (Dist < world.Width / 2 || (Dist < world.Height && angle < Angle[20]) || world.Tick > 4500)
                        move.FireType = FireType.PremiumPreferred;
                    else
                        move.FireType = FireType.Regular;
                }
                else
                {
                    noFire++;
                }
            }

            if (world.Tick < 200)
            {
                if (!selectedPoint)
                {
                    double minDist = 10000;
                    minX = 10000;
                    minY = 10000;
                    for (double x = world.Width * 0.03; x < world.Width; x += world.Width * 0.94)
                    {
                        for (double y = world.Height * 0.03; y < world.Height; y += world.Height * 0.94)
                        {
                            double d = self.GetDistanceTo(x, y);
                            if (d < minDist)
                            {
                                minDist = d;
                                minX = x;
                                minY = y;
                            }
                        }
                    }
                    selectedPoint = true;
                }
                Go(self, minX, minY);
            }

            if (world.Tick < start)
            {
                move.FireType = FireType.None;
            }
        }

        bool selectedPoint = false;
        double minX, minY;
        int goAway = 0;
        double xGoAway, yGoAway;

        private bool Hit(Tank self, Tank another)
        {
            // если есть препятствия
            double turangle = GetTurretAngle(self);
            if (FFF(self, FictiveTank(self.X + Math.Cos(turangle) * 2000, self.Y + Math.Sin(turangle) * 2000, 23423), another))
                return false;
            //if (Intersected(self, FictiveTank(self.X + Math.Cos(turangle) * 2000, self.Y + Math.Sin(turangle) * 2000, 23423)))
            //    return false;

            double shellX = self.X, shellY = self.Y;
            double dx = ShellStartSpeed * Math.Cos(self.TurretRelativeAngle + self.Angle);
            double dy = ShellStartSpeed * Math.Sin(self.TurretRelativeAngle + self.Angle);

            int steps = 0;
            while (shellX >= 0 && shellX < world.Width && shellY >= 0 && shellY < world.Height)
            {
                Tank tank = new Tank(0, "", 0, steps * another.SpeedX + another.X, steps * another.SpeedY + another.Y, another.SpeedX, another.SpeedY, another.Angle, another.AngularSpeed,
                    another.TurretRelativeAngle, another.CrewHealth, another.HullDurability, another.ReloadingTime, another.RemainingReloadingTime, another.PremiumShellCount, another.IsTeammate, another.Type);


                int inunit = InUnit(tank, shellX, shellY, 0.85);
                if (inunit != -1)
                {
                    if (self.PremiumShellCount != 0)
                        return true;
                    double angle = Math.Abs(NormAngle(another.Angle - NormAngle(self.Angle + self.TurretRelativeAngle)));
                    if (angle > Angle[90])
                        angle = Angle[180] - angle;

                    if (inunit == 0 || inunit == 2)
                        return true;
                    if (angle > Angle[30])
                        return true;
                    return false;
                }
                shellX += dx;
                shellY += dy;
                steps++;
            }
            return false;
        }

        private int InUnit(Unit unit, double x, double y, double error = 1.0)
        {
            double[] X = new double[4];
            double[] Y = new double[4];
            GetCoordinates(unit, X, Y, error);
            bool result = InPolygon(x, y, X, Y);
            if (!result)
                return -1;
            double minD = 10000;
            int res = -1;
            for (int i = 0; i < 4; i++)
            {
                double d = DistFromPointToLine(x, y, X[i], Y[i], X[(i + 1) % 4], Y[(i + 1) % 4]) - (i % 2 == 0 ? 0 : 5);
                if (d < minD)
                {
                    minD = d;
                    res = i;
                }
            }
            return res;
        }

        private double TankCoeff(Tank self, Unit goal)
        {
            return 1 / /*(Math.Abs(NewAngle(self.GetAngleTo(goal))) * */Math.Pow(self.GetDistanceTo(goal), 0.05);
        }

        private double TurretCoeff(Tank self, Unit goal)
        {
            double minDist = 10000;
            for (int i = 0; i < world.Tanks.Length; i++)
                if (!world.Tanks[i].IsTeammate && IsAlive(world.Tanks[i]))
                    if (minDist > self.GetDistanceTo(world.Tanks[i]))
                        minDist = self.GetDistanceTo(world.Tanks[i]);

            if (minDist > self.Width * 6)
            {
                double angle = Math.Abs(NormAngle(goal.Angle - NormAngle(self.GetTurretAngleTo(goal) + self.Angle + self.TurretRelativeAngle)));
                if (angle > Angle[90])
                    angle = Angle[180] - angle;
                return 1 / angle;
            }

            if (world.Tick > 30)
                return 1 / self.GetDistanceTo(goal);

            return 1 / Math.Abs(self.GetTurretAngleTo(goal));
        }
    }
}