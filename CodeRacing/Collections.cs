using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    //public class BitSet
    //{
    //    public readonly int Size;
    //    private uint[] _bytes;
    //    public BitSet(int size)
    //    {
    //        Size = size;
    //        _bytes = new uint[(size + 31) / 32];
    //    }

    //    public bool this[int idx]
    //    {
    //        get { return 1 == (1 & (_bytes[idx >> 5] >> (idx & 0x20))); }
    //        set
    //        {
    //            if (value != (1 == (1 & (_bytes[idx >> 5] >> (idx & 0x20)))))
    //                _bytes[idx >> 5] ^= 1u << (idx & 0x20);
    //        }
    //    }

    //}

    public class BitSet
    {
        public readonly int Size;
        private bool[] _bytes;
        public BitSet(int size)
        {
            Size = size;
            _bytes = new bool[size];
        }

        public bool this[int idx]
        {
            get { return _bytes[idx]; }
            set { _bytes[idx] = value; }
        }

    }

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
            First = first;
            Second = second;
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

        public Tuple(TFirst first, TSecond second, TThird third)
            : base(first, second)
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

    public class PriorityQueue<T>
    {
        private readonly List<T> _data = new List<T>();
        private readonly IComparer<T> _comparer;

        public PriorityQueue()
        {
            _comparer = Comparer<T>.Default;
        }

        public PriorityQueue(IComparer<T> comparer)
        {
            if (comparer == null)
            {
                _comparer = Comparer<T>.Default;
                return;
            }
            _comparer = comparer;
        }

        public int Count
        {
            get { return _data.Count; }
        }

        public bool Empty()
        {
            return Count == 0;
        }

        public T Top()
        {
            return _data[0];
        }

        public void Push(T item)
        {
            _data.Add(item);
            var curPlace = Count;
            while (curPlace > 1 && _comparer.Compare(item, _data[curPlace/2 - 1]) > 0)
            {
                _data[curPlace - 1] = _data[curPlace/2 - 1];
                _data[curPlace/2 - 1] = item;
                curPlace /= 2;
            }
        }

        public void Pop()
        {
            _data[0] = _data[Count - 1];
            _data.RemoveAt(Count - 1);
            var curPlace = 1;
            while (true)
            {
                var max = curPlace;
                if (Count >= curPlace*2 && _comparer.Compare(_data[max - 1], _data[2*curPlace - 1]) < 0)
                    max = 2*curPlace;
                if (Count >= curPlace*2 + 1 && _comparer.Compare(_data[max - 1], _data[2*curPlace]) < 0)
                    max = 2*curPlace + 1;
                if (max == curPlace) break;
                var item = _data[max - 1];
                _data[max - 1] = _data[curPlace - 1];
                _data[curPlace - 1] = item;
                curPlace = max;
            }
        }
    }
}
