/**
 * https://github.com/Codeforces/codeforces-commons/blob/6169b31876d035782d4fcad6b819676d6102a7a2/code/src/main/java/com/codeforces/commons/collection/QuadTree.java
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.CompilerServices;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class QuadTree<T> : IEnumerable<T> where T : AUnit
    {
        #region Private Members
        private class Node
        {
            public T Value;

            public bool HasValueBelow;

            public Node LeftTop;
            public Node RightTop;
            public Node LeftBottom;
            public Node RightBottom;

            public void InitializeChildren()
            {
                if (LeftTop == null)
                {
                    LeftTop = new Node();
                    RightTop = new Node();
                    LeftBottom = new Node();
                    RightBottom = new Node();
                }
            }
        }

        private Node _root = new Node();

        private readonly double _left;
        private readonly double _top;
        private readonly double _right;
        private readonly double _bottom;
        private readonly double _epsilon;
        private Func<T, T> _cloneFunc;

        // arguments
        private double _x, _y, _squaredDistance, _squaredDistanceE;
        private long _id;
        private List<T> _values;
        private T _value;

        private IEnumerable<T> _traverse(Node node)
        {
            if (node == null)
                yield break;

            if (node.Value != null)
            {
                yield return node.Value;
            }
            else
            {
                foreach (var r in _traverse(node.LeftTop))
                    yield return r;
                foreach (var r in _traverse(node.RightTop))
                    yield return r;
                foreach (var r in _traverse(node.LeftBottom))
                    yield return r;
                foreach (var r in _traverse(node.RightBottom))
                    yield return r;
            }
        }

        private void _clone(Node src, out Node dest)
        {
            if (src == null)
            {
                dest = null;
                return;
            }
            dest = new Node {HasValueBelow = src.HasValueBelow};
            if (src.Value != null)
            {
                dest.Value = _cloneFunc(src.Value);
                _values?.Add(dest.Value);
            }
            else
            {
                _clone(src.LeftTop, out dest.LeftTop);
                _clone(src.LeftBottom, out dest.LeftBottom);
                _clone(src.RightTop, out dest.RightTop);
                _clone(src.RightBottom, out dest.RightBottom);
            }
        }

        private void _add(T value, Node node, double left, double top, double right, double bottom)
        {
            var currentValue = node.Value;

            if (currentValue == null)
            {
                if (node.HasValueBelow)
                    _addAsChild(value, node, left, top, right, bottom);
                else
                    node.Value = value;
            }
            else
            {
                var currentX = currentValue.X;
                var currentY = currentValue.Y;

                if (value.X.CompareTo(currentX) == 0 && value.Y.CompareTo(currentY) == 0)
                    throw new PointAlreadyExistsException($"Trying to add point with same coordinates ({currentX}, {currentY})");

                node.Value = null;
                node.HasValueBelow = true;
                node.InitializeChildren();

                _addAsChild(currentValue, node, left, top, right, bottom);
                _addAsChild(value, node, left, top, right, bottom);
            }
        }

        private void _addAsChild(T value, Node node, double left, double top, double right, double bottom)
        {
            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (value.X < centerX)
            {
                if (value.Y < centerY)
                    _add(value, node.LeftTop, left, top, centerX, centerY);
                else
                    _add(value, node.LeftBottom, left, centerY, centerX, bottom);
            }
            else
            {
                if (value.Y < centerY)
                    _add(value, node.RightTop, centerX, top, right, centerY);
                else
                    _add(value, node.RightBottom, centerX, centerY, right, bottom);
            }
        }

        // 0 - ОК, работа завершена
        // 1 - Удалилось - нужно вставить
        // 2 - Элемент не найден
        private int _changeXY(double left, double top, double right, double bottom, Node node)
        {
            if (node.Value != null)
            {
                if (node.Value.Equals(_value))
                {
                    _value.X = _x;
                    _value.Y = _y;
                    if (_x >= left && _x < right && _y >= top && _y < bottom)
                        return 0;

                    node.Value = null;
                    return 1;
                }
                return 2;
            }
            if (!node.HasValueBelow)
            {
                return 2;
            }

            var centerX = (left + right) / 2.0D;
            var centerY = (top + bottom) / 2.0D;

            int done;

            if (_value.Y < centerY)
            {
                done = _value.X < centerX
                    ? _changeXY(left, top, centerX, centerY, node.LeftTop)
                    : _changeXY(centerX, top, right, centerY, node.RightTop);
            }
            else
            {
                done = _value.X < centerX
                    ? _changeXY(left, centerY, centerX, bottom, node.LeftBottom)
                    : _changeXY(centerX, centerY, right, bottom, node.RightBottom);
            }

            if (done == 1)
            {
                if (_x >= left && _x < right && _y >= top && _y < bottom)
                {
                    _add(_value, node, left, top, right, bottom);
                    return 0;
                }

                // normalize tree
                node.HasValueBelow =
                    node.LeftTop != null && (node.LeftTop.HasValueBelow || node.LeftTop.Value != null) ||
                    node.RightTop != null && (node.RightTop.HasValueBelow || node.RightTop.Value != null) ||
                    node.LeftBottom != null && (node.LeftBottom.HasValueBelow || node.LeftBottom.Value != null) ||
                    node.RightBottom != null && (node.RightBottom.HasValueBelow || node.RightBottom.Value != null);

                if (!node.HasValueBelow && node.Value == null)
                    node.LeftTop = node.RightTop = node.LeftBottom = node.RightBottom = null;

                return 1;
            }
            return done;
        }


        private T _findNearest(Node node, double left, double top, double right, double bottom)
        {
            if (node.Value != null)
            {
                if (_getSquaredDistanceTo(node.Value, _squaredDistance) <= _squaredDistanceE)
                    return node.Value;
                return null;
            }

            if (!node.HasValueBelow)
                return null;

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (_x < centerX)
            {
                if (_y < centerY)
                {
                    var nearestValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, _squaredDistance);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - _x))
                    {
                        var otherValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - _y))
                    {
                        var otherValue = _findNearest(node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - _x, centerY - _y))
                    {
                        var otherValue = _findNearest(node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    var nearestValue = _findNearest(node.LeftBottom, left, centerY, centerX, bottom);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, _squaredDistance);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - _x))
                    {
                        var otherValue = _findNearest(node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _x, _y, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(_y - centerY))
                    {
                        var otherValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - _x, _y - centerY))
                    {
                        var otherValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
            }
            else
            {
                if (_y < centerY)
                {
                    var nearestValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, _squaredDistance);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(_x - centerX))
                    {
                        var otherValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - _y))
                    {
                        var otherValue = _findNearest(node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(_x - centerX, centerY - _y))
                    {
                        var otherValue = _findNearest(node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    var nearestValue = _findNearest(node.RightBottom, centerX, centerY, right, bottom);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, _squaredDistance);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(_x - centerX))
                    {
                        var otherValue = _findNearest(node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(_y - centerY))
                    {
                        var otherValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.SumSqr(_x - centerX, _y - centerY))
                    {
                        var otherValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, _squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
            }
        }

        private bool _remove(double left, double top, double right, double bottom, Node node, T value)
        {
            if (node.Value != null)
            {
                if (node.Value.Equals(value))
                {
                    node.Value = null;
                    Count--;    
                    return true;
                }
                return false;
            }
            if (!node.HasValueBelow)
            {
                // value not found
                return false;
            }

            var x = value.X;
            var y = value.Y;

            var centerX = (left + right) / 2.0D;
            var centerY = (top + bottom) / 2.0D;

            bool removed;

            if (y < centerY)
            {
                if (x < centerX)
                {
                    removed = _remove(left, top, centerX, centerY, node.LeftTop, value);
                }
                else
                {
                    removed = _remove(centerX, top, right, centerY, node.RightTop, value);
                }
            }
            else
            {
                if (x < centerX)
                {
                    removed = _remove(left, centerY, centerX, bottom, node.LeftBottom, value);
                }
                else
                {
                    removed = _remove(centerX, centerY, right, bottom, node.RightBottom, value);
                }
            }

            if (removed)
            {
                // normalize tree
                node.HasValueBelow =
                    node.LeftTop != null && (node.LeftTop.HasValueBelow || node.LeftTop.Value != null) ||
                    node.RightTop != null && (node.RightTop.HasValueBelow || node.RightTop.Value != null) ||
                    node.LeftBottom != null && (node.LeftBottom.HasValueBelow || node.LeftBottom.Value != null) ||
                    node.RightBottom != null && (node.RightBottom.HasValueBelow || node.RightBottom.Value != null);

                if (!node.HasValueBelow && node.Value == null)
                    node.LeftTop = node.RightTop = node.LeftBottom = node.RightBottom = null;
            }

            return removed;
        }

        private T _findFirstNearby(Node node, double left, double top, double right, double bottom)
        {
            if (node.Value != null)
            {
                if (_getSquaredDistanceTo(node.Value) <= _squaredDistanceE)
                    return node.Value;
                return null;
            }

            if (!node.HasValueBelow)
                return null;

            var centerX = (left + right) / 2.0D;
            var centerY = (top + bottom) / 2.0D;
            T result;

            var cx2 = (centerX - _x)*(centerX - _x);
            var cy2 = (centerY - _y)*(centerY - _y);

            if (_x < centerX)
            {
                if (_y < centerY)
                {
                    result = _findFirstNearby(node.LeftTop, left, top, centerX, centerY);

                    if (result == null && _squaredDistanceE >= cx2)
                    {
                        result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (result == null && _squaredDistanceE >= cy2)
                    {
                        result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (result == null && _squaredDistanceE >= cx2 + cy2)
                    {
                        result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }
                }
                else
                {
                    result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);

                    if (result == null && _squaredDistanceE >= cx2)
                    {
                        result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (result == null && _squaredDistanceE > cy2)
                    {
                        result = _findFirstNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (result == null && _squaredDistanceE >= cx2 + cy2)
                    {
                        result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);
                    }
                }
            }
            else
            {
                if (_y < centerY)
                {
                    result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);

                    if (result == null && _squaredDistanceE > cx2)
                    {
                        result = _findFirstNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (result == null && _squaredDistanceE >= cy2)
                    {
                        result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (result == null && _squaredDistanceE >= cx2 + cy2)
                    {
                        result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }
                }
                else
                {
                    result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);

                    if (result == null && _squaredDistanceE > cx2)
                    {
                        result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (result == null && _squaredDistanceE > cy2)
                    {
                        result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (result == null && _squaredDistanceE > cx2 + cy2)
                    {
                        result = _findFirstNearby(node.LeftTop, left, top, centerX, centerY);
                    }
                }
            }
            return result;
        }

        private void _findAllNearby(Node node, double left, double top, double right, double bottom)
        {
            if (node.Value != null)
            {
                if (_getSquaredDistanceTo(node.Value) <= _squaredDistanceE)
                    _values.Add(node.Value);
                return;
            }

            if (!node.HasValueBelow)
                return;

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (_x < centerX)
            {
                if (_y < centerY)
                {
                    _findAllNearby(node.LeftTop, left, top, centerX, centerY);

                    if (_squaredDistanceE >= Geom.Sqr(centerX - _x))
                    {
                        _findAllNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (_squaredDistanceE >= Geom.Sqr(centerY - _y))
                    {
                        _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (_squaredDistanceE >= Geom.SumSqr(centerX - _x, centerY - _y))
                    {
                        _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }
                }
                else
                {
                    _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);

                    if (_squaredDistanceE >= Geom.Sqr(centerX - _x))
                    {
                        _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (_squaredDistanceE > Geom.Sqr(_y - centerY))
                    {
                        _findAllNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (_squaredDistanceE >= Geom.SumSqr(centerX - _x, _y - centerY))
                    {
                        _findAllNearby(node.RightTop, centerX, top, right, centerY);
                    }
                }
            }
            else
            {
                if (_y < centerY)
                {
                    _findAllNearby(node.RightTop, centerX, top, right, centerY);

                    if (_squaredDistanceE > Geom.Sqr(_x - centerX))
                    {
                        _findAllNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (_squaredDistanceE >= Geom.Sqr(centerY - _y))
                    {
                        _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (_squaredDistanceE >= Geom.SumSqr(_x - centerX, centerY - _y))
                    {
                        _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }
                }
                else
                {
                    _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);

                    if (_squaredDistanceE > Geom.Sqr(_x - centerX))
                    {
                        _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (_squaredDistanceE > Geom.Sqr(_y - centerY))
                    {
                        _findAllNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (_squaredDistanceE > Geom.SumSqr(_x - centerX, _y - centerY))
                    {
                        _findAllNearby(node.LeftTop, left, top, centerX, centerY);
                    }
                }
            }
        }

        private static void _clear(Node node)
        {
            node.Value = default(T);

            if (node.HasValueBelow)
            {
                node.HasValueBelow = false;

                _clear(node.LeftTop);
                _clear(node.RightTop);
                _clear(node.LeftBottom);
                _clear(node.RightBottom);
            }
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private double _getSquaredDistanceTo(T value, double defaultValue = double.PositiveInfinity)
        {
            return value == null || value.Id == _id
                ? defaultValue
                : Geom.SumSqr(value.X - _x, value.Y - _y);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private static double _getSquaredDistanceTo(T value, double x, double y, double defaultValue = double.PositiveInfinity)
        {
            return value == null
                ? defaultValue
                : Geom.SumSqr(value.X - x, value.Y - y);
        }
        #endregion


        #region Public Members
        public int Count = 0;

        public QuadTree(double left, double top, double right, double bottom, double epsilon, Func<T, T> cloneFunc = null)
        {
            this._left = left;
            this._top = top;
            this._right = right;
            this._bottom = bottom;
            this._epsilon = epsilon;
            this._cloneFunc = cloneFunc;
        }

        public void Add(T value)
        {
            if (value.X < _left || value.Y < _top || value.X >= _right || value.Y >= _bottom)
            {
                throw new ArgumentException(
                    $"The point ({value.X}, {value.Y}) is outside of bounding box ({_left}, {_top}, {_right}, {_bottom}).");
            }

            _add(value, _root, _left, _top, _right, _bottom);
            Count++;
        }

        public void AddRange(IEnumerable<T> values)
        {
            foreach (var value in values)
                Add(value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public T FindNearest(T value, double squaredRadius = double.PositiveInfinity)
        {
            return FindNearest(value.X, value.Y, squaredRadius);
        }

        public T FindNearest(double x, double y, double squaredRadius = double.PositiveInfinity)
        {
            this._x = x;
            this._y = y;
            this._squaredDistanceE = squaredRadius + _epsilon;
            this._squaredDistance = squaredRadius;
            return _findNearest( _root, _left, _top, _right, _bottom);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public bool Remove(T value)
        {
            return _remove(_left, _top, _right, _bottom, _root, value);
        }

        public bool ChangeXY(T value, double newX, double newY)
        {
            this._value = value;
            this._x = newX;
            this._y = newY;
            return _changeXY(_left, _top, _right, _bottom, _root) == 0;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public T FindFirstNearby(T value, double squaredDistance)
        {
            return FindFirstNearby(value.X, value.Y, squaredDistance, value.Id);
        }

        public T FindFirstNearby(double x, double y, double squaredDistance, long id)
        {
            this._x = x;
            this._y = y;
            this._squaredDistanceE = squaredDistance + _epsilon;
            this._id = id;
            return _findFirstNearby(_root, _left, _top, _right, _bottom);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public List<T> FindAllNearby(T value, double squaredDistance)
        {
            return FindAllNearby(value.X, value.Y, squaredDistance, value.Id);
        }

        public List<T> FindAllNearby(double x, double y, double squaredDistance, long id)
        {
            this._values = new List<T>();
            this._x = x;
            this._y = y;
            this._squaredDistanceE = squaredDistance + _epsilon;
            this._id = id;
            _findAllNearby(_root, _left, _top, _right, _bottom);
            return _values;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public void Clear()
        {
            _clear(_root);
            Count = 0;
        }

        public QuadTree<T> Clone()
        {
            var tree = new QuadTree<T>(_left, _top, _right, _bottom, _epsilon, _cloneFunc);
            _values = null;
            _clone(_root, out tree._root);
            tree.Count = Count;
            return tree;
        }

        public QuadTree<T> Clone(ref List<T> createdNodes)
        {
            var tree = new QuadTree<T>(_left, _top, _right, _bottom, _epsilon, _cloneFunc);
            _values = createdNodes;
            _values.Capacity = Count;
            _clone(_root, out tree._root);
            if (Count != createdNodes.Count)
                throw new Exception("Missing nodes in tree clone");
            tree.Count = Count;
            return tree;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public IEnumerator<T> GetEnumerator()
        {
            return _traverse(_root).GetEnumerator();
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public class PointAlreadyExistsException : Exception
        {
            public PointAlreadyExistsException(string message) : base(message)
            {
            }
        }

        #endregion
    }
}