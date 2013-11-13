using System;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public sealed class World
    {
        private readonly int moveIndex;
        private readonly int width;
        private readonly int height;
        private readonly Player[] players;
        private readonly Trooper[] troopers;
        private readonly Bonus[] bonuses;
        private readonly CellType[][] cells;
        private readonly bool[] cellVisibilities;

        public World(int moveIndex, int width, int height, Player[] players, Trooper[] troopers, Bonus[] bonuses,
            CellType[][] cells, bool[] cellVisibilities)
        {
            this.moveIndex = moveIndex;
            this.width = width;
            this.height = height;

            this.players = new Player[players.Length];
            Array.Copy(players, this.players, players.Length);

            this.troopers = new Trooper[troopers.Length];
            Array.Copy(troopers, this.troopers, troopers.Length);

            this.bonuses = new Bonus[bonuses.Length];
            Array.Copy(bonuses, this.bonuses, bonuses.Length);

            this.cells = new CellType[width][];
            for (int x = 0; x < width; ++x)
            {
                this.cells[x] = new CellType[cells[x].Length];
                Array.Copy(cells[x], this.cells[x], cells[x].Length);
            }

            this.cellVisibilities = cellVisibilities;
        }

        public int MoveIndex
        {
            get { return moveIndex; }
        }

        public int Width
        {
            get { return width; }
        }

        public int Height
        {
            get { return height; }
        }

        public Player[] Players
        {
            get
            {
                Player[] players = new Player[this.players.Length];
                Array.Copy(this.players, players, this.players.Length);
                return players;
            }
        }

        public Trooper[] Troopers
        {
            get
            {
                Trooper[] troopers = new Trooper[this.troopers.Length];
                Array.Copy(this.troopers, troopers, this.troopers.Length);
                return troopers;
            }
        }

        public Bonus[] Bonuses
        {
            get
            {
                Bonus[] bonuses = new Bonus[this.bonuses.Length];
                Array.Copy(this.bonuses, bonuses, this.bonuses.Length);
                return bonuses;
            }
        }

        public CellType[][] Cells
        {
            get
            {
                CellType[][] copiedCells = new CellType[cells.Length][];
                for (int x = 0; x < cells.Length; ++x)
                {
                    copiedCells[x] = new CellType[cells[x].Length];
                    Array.Copy(cells[x], copiedCells[x], cells[x].Length);
                }
                return copiedCells;
            }
        }

        public bool[] CellVisibilities
        {
            get
            {
                return (bool[]) cellVisibilities.Clone();
            }
        }

        public bool IsVisible(
            double maxRange,
            int viewerX, int viewerY, TrooperStance viewerStance,
            int objectX, int objectY, TrooperStance objectStance)
        {
            int minStanceIndex = Math.Min((int) viewerStance, (int) objectStance);
            int xRange = objectX - viewerX;
            int yRange = objectY - viewerY;

            return xRange * xRange + yRange * yRange <= maxRange * maxRange
                && cellVisibilities[
                viewerX * height * width * height * StanceCountHolder.STANCE_COUNT
                + viewerY * width * height * StanceCountHolder.STANCE_COUNT
                + objectX * height * StanceCountHolder.STANCE_COUNT
                + objectY * StanceCountHolder.STANCE_COUNT
                + minStanceIndex
                ];
        }

        private static class StanceCountHolder
        {
            internal static readonly int STANCE_COUNT = Enum.GetNames(typeof(TrooperStance)).Length;
        }
    }
}