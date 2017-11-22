/**
 * https://github.com/Codeforces/codeforces-commons/blob/6169b31876d035782d4fcad6b819676d6102a7a2/code/src/main/java/com/codeforces/commons/collection/QuadTree.java
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Threading;

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
                dest.Value = _cloneFunc(src.Value);
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
                    return;

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
                if (node.Value.Equals(value))
                {
                    value.X = x;
                    value.Y = y;
                    if (x >= left && x < right && y >= top && y < bottom)
                        return 0;

                    node.Value = null;
                    return 1;
                }
                return 0;
            }
            if (!node.HasValueBelow)
            {
                // value not found
                return 2;
            }

            var centerX = (left + right) / 2.0D;
            var centerY = (top + bottom) / 2.0D;

            int done;

            if (value.Y < centerY)
            {
                done = value.X < centerX
                    ? _changeXY(left, top, centerX, centerY, node.LeftTop)
                    : _changeXY(centerX, top, right, centerY, node.RightTop);
            }
            else
            {
                done = value.X < centerX
                    ? _changeXY(left, centerY, centerX, bottom, node.LeftBottom)
                    : _changeXY(centerX, centerY, right, bottom, node.RightBottom);
            }

            if (done == 1)
            {
                if (x >= left && x < right && y >= top && y < bottom)
                {
                    _add(value, node, left, top, right, bottom);
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
                if (_getSquaredDistanceTo(node.Value, squaredDistance) <= squaredDistanceE)
                    return node.Value;
                return null;
            }

            if (!node.HasValueBelow)
                return null;

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    var nearestValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, squaredDistance);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        var otherValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        var otherValue = _findNearest(node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        var otherValue = _findNearest(node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

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
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, squaredDistance);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        var otherValue = _findNearest(node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        var otherValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        var otherValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

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
                if (y < centerY)
                {
                    var nearestValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, squaredDistance);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        var otherValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        var otherValue = _findNearest(node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        var otherValue = _findNearest(node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

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
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, squaredDistance);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        var otherValue = _findNearest(node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        var otherValue = _findNearest(node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        var otherValue = _findNearest(node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, squaredDistance);

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

        private double x, y, squaredDistance, squaredDistanceE;
        private long id;
        private List<T> values;
        private T value;

        private T _findFirstNearby(Node node, double left, double top, double right, double bottom)
        {
            if (node.Value != null)
            {
                if (_getSquaredDistanceTo(node.Value) <= squaredDistanceE)
                    return node.Value;
                return null;
            }

            if (!node.HasValueBelow)
                return null;

            var centerX = (left + right) / 2.0D;
            var centerY = (top + bottom) / 2.0D;
            T result;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    result = _findFirstNearby(node.LeftTop, left, top, centerX, centerY);

                    if (result == null && squaredDistanceE >= Geom.Sqr(centerX - x))
                    {
                        result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (result == null && squaredDistanceE >= Geom.Sqr(centerY - y))
                    {
                        result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (result == null && squaredDistanceE >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }
                }
                else
                {
                    result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);

                    if (result == null && squaredDistanceE >= Geom.Sqr(centerX - x))
                    {
                        result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (result == null && squaredDistanceE > Geom.Sqr(y - centerY))
                    {
                        result = _findFirstNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (result == null && squaredDistanceE >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);
                    }
                }
            }
            else
            {
                if (y < centerY)
                {
                    result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);

                    if (result == null && squaredDistanceE > Geom.Sqr(x - centerX))
                    {
                        result = _findFirstNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (result == null && squaredDistanceE >= Geom.Sqr(centerY - y))
                    {
                        result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (result == null && squaredDistanceE >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }
                }
                else
                {
                    result = _findFirstNearby(node.RightBottom, centerX, centerY, right, bottom);

                    if (result == null && squaredDistanceE > Geom.Sqr(x - centerX))
                    {
                        result = _findFirstNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (result == null && squaredDistanceE > Geom.Sqr(y - centerY))
                    {
                        result = _findFirstNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (result == null && squaredDistanceE > Geom.SumSqr(x - centerX, y - centerY))
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
                if (_getSquaredDistanceTo(node.Value) <= squaredDistanceE)
                    values.Add(node.Value);
                return;
            }

            if (!node.HasValueBelow)
                return;

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    _findAllNearby(node.LeftTop, left, top, centerX, centerY);

                    if (squaredDistanceE >= Geom.Sqr(centerX - x))
                    {
                        _findAllNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (squaredDistanceE >= Geom.Sqr(centerY - y))
                    {
                        _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (squaredDistanceE >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }
                }
                else
                {
                    _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);

                    if (squaredDistanceE >= Geom.Sqr(centerX - x))
                    {
                        _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (squaredDistanceE > Geom.Sqr(y - centerY))
                    {
                        _findAllNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (squaredDistanceE >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        _findAllNearby(node.RightTop, centerX, top, right, centerY);
                    }
                }
            }
            else
            {
                if (y < centerY)
                {
                    _findAllNearby(node.RightTop, centerX, top, right, centerY);

                    if (squaredDistanceE > Geom.Sqr(x - centerX))
                    {
                        _findAllNearby(node.LeftTop, left, top, centerX, centerY);
                    }

                    if (squaredDistanceE >= Geom.Sqr(centerY - y))
                    {
                        _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (squaredDistanceE >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }
                }
                else
                {
                    _findAllNearby(node.RightBottom, centerX, centerY, right, bottom);

                    if (squaredDistanceE > Geom.Sqr(x - centerX))
                    {
                        _findAllNearby(node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (squaredDistanceE > Geom.Sqr(y - centerY))
                    {
                        _findAllNearby(node.RightTop, centerX, top, right, centerY);
                    }

                    if (squaredDistanceE > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        _findAllNearby(node.LeftTop, left, top, centerX, centerY);
                    }
                }
            }
        }


        private bool _hasNearby(
            double x, double y, double squaredDistance, Node node,
            double left, double top, double right, double bottom
            )
        {
            if (node.Value != null)
            {
                return _getSquaredDistanceTo(node.Value, x, y) <= squaredDistance;
            }

            if (!node.HasValueBelow)
            {
                return false;
            }

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY))
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }
            else
            {
                if (y < centerY)
                {
                    if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY))
                        {
                            return true;
                        }
                    }

                    return false;
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

        private double _getSquaredDistanceTo(T value, double defaultValue = double.PositiveInfinity)
        {
            return value == null || value.Id == id
                ? defaultValue
                : Geom.SumSqr(value.X - x, value.Y - y);
        }

        private static double _getSquaredDistanceTo(T value, double x, double y, double defaultValue = double.PositiveInfinity)
        {
            return value == null
                ? defaultValue
                : Geom.SumSqr(value.X - x, value.Y - y);
        }
        #endregion


        #region Public Members
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

        public T FindNearest(T value, double squaredRadius = double.PositiveInfinity)
        {
            return FindNearest(value.X, value.Y, squaredRadius);
        }

        public T FindNearest(double x, double y, double squaredRadius = double.PositiveInfinity)
        {
            this.x = x;
            this.y = y;
            this.squaredDistanceE = squaredRadius + _epsilon;
            this.squaredDistance = squaredRadius;
            return _findNearest( _root, _left, _top, _right, _bottom);
        }

        public bool Remove(T value)
        {
            return _remove(_left, _top, _right, _bottom, _root, value);
        }

        public bool ChangeXY(T value, double x, double y)
        {
            this.value = value;
            this.x = x;
            this.y = y;
            return _changeXY(_left, _top, _right, _bottom, _root) == 0;
        }

        public T FindFirstNearby(T value, double squaredDistance)
        {
            return FindFirstNearby(value.X, value.Y, squaredDistance, value.Id);
        }

        public T FindFirstNearby(double x, double y, double squaredDistance, long id)
        {
            this.x = x;
            this.y = y;
            this.squaredDistanceE = squaredDistance + _epsilon;
            this.id = id;
            return _findFirstNearby(_root, _left, _top, _right, _bottom);
        }

        public List<T> FindAllNearby(T value, double squaredDistance)
        {
            return FindAllNearby(value.X, value.Y, squaredDistance, value.Id);
        }

        public List<T> FindAllNearby(double x, double y, double squaredDistance, long id)
        {
            this.values = new List<T>();
            this.x = x;
            this.y = y;
            this.squaredDistanceE = squaredDistance + _epsilon;
            this.id = id;
            _findAllNearby(_root, _left, _top, _right, _bottom);
            return values;
        }

        public bool HasNearby(T value, double squaredDistance)
        {
            return HasNearby(value.X, value.Y, squaredDistance);
        }

        public bool HasNearby(double x, double y, double squaredDistance)
        {
            return _hasNearby(x, y, squaredDistance, _root, _left, _top, _right, _bottom);
        }

        public void Clear()
        {
            _clear(_root);
            Count = 0;
        }

        public QuadTree<T> Clone()
        {
            var tree = new QuadTree<T>(_left, _top, _right, _bottom, _epsilon, _cloneFunc);
            _clone(_root, out tree._root);
            tree.Count = Count;
            return tree;
        }

        public IEnumerator<T> GetEnumerator()
        {
            return _traverse(_root).GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public int Count = 0;

        #endregion
    }
}