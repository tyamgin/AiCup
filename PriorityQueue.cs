using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public class PriorityQueue<T>
    {
        #region private members

        private readonly List<T> _data = new List<T>();
        private readonly IComparer<T> _comparer;

        #endregion

        #region constructors

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

        #endregion

        #region public members

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
                if (max == curPlace) break;
                var item = _data[max - 1];
                _data[max - 1] = _data[curPlace - 1];
                _data[curPlace - 1] = item;
                curPlace = max;
            }
        }

        #endregion
    }

    public class Pair<First, Second> : IComparable<Pair<First, Second>> where First : IComparable<First>
                                                                        where Second : IComparable<Second>
    {
        public First first;
        public Second second;

        public int CompareTo(Pair<First, Second> other)
        {
            if (first.CompareTo(other.first) == 0)
                return second.CompareTo(other.second);
            return first.CompareTo(other.first);
        }

        public Pair(First first, Second second)
        {
            this.first = first;
            this.second = second;
        }
    }
}
