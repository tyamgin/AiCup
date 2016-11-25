using System;
using System.Collections.Generic;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
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
            First = first;
            Second = second;
        }

        public override string ToString()
        {
            return "(" + First + "; " + Second + ")";
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
            while (curPlace > 1 && _comparer.Compare(item, _data[curPlace / 2 - 1]) > 0)
            {
                _data[curPlace - 1] = _data[curPlace / 2 - 1];
                _data[curPlace / 2 - 1] = item;
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
                if (Count >= curPlace * 2 && _comparer.Compare(_data[max - 1], _data[2 * curPlace - 1]) < 0)
                    max = 2 * curPlace;
                if (Count >= curPlace * 2 + 1 && _comparer.Compare(_data[max - 1], _data[2 * curPlace]) < 0)
                    max = 2 * curPlace + 1;
                if (max == curPlace)
                    break;
                var item = _data[max - 1];
                _data[max - 1] = _data[curPlace - 1];
                _data[curPlace - 1] = item;
                curPlace = max;
            }
        }
    }
}
