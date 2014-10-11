using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public class Pair<TFirst, TSecond> : IComparable<Pair<TFirst, TSecond>>
        where TFirst : IComparable<TFirst>
        where TSecond : IComparable<TSecond>
    {
        public TFirst First;
        public TSecond Second;

        public int CompareTo(Pair<TFirst, TSecond> other)
        {
            if (First.CompareTo(other.First) == 0)
                return Second.CompareTo(other.Second);
            return First.CompareTo(other.First);
        }

        public Pair(TFirst first, TSecond second)
        {
            this.First = first;
            this.Second = second;
        }

        public override string ToString()
        {
            return "(" + First + "; " + Second + ")";
        }
    }

    public class Tuple<TFirst, TSecond, TThird> : Pair<TFirst, TSecond>
        where TFirst : IComparable<TFirst>
        where TSecond : IComparable<TSecond>
        where TThird : IComparable<TThird>
    {
        public TThird Third;

        public Tuple(TFirst first, TSecond second, TThird third) : base(first, second)
        {
            this.Third = third;
        }

        public int CompareTo(Tuple<TFirst, TSecond, TThird> other)
        {
            if (First.CompareTo(other.First) != 0)
                return First.CompareTo(other.First);
            if (Second.CompareTo(other.Second) != 0)
                return Second.CompareTo(other.Second);
            return Third.CompareTo(other.Third);
        }

        public override string ToString()
        {
            return "(" + First + "; " + Second + "; " + Third + ")";
        }
    }
}

