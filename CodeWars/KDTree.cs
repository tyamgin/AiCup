/**
 * https://github.com/Codeforces/codeforces-commons/blob/6169b31876d035782d4fcad6b819676d6102a7a2/code/src/main/java/com/codeforces/commons/collection/QuadTree.java
 */

using System;
using System.Collections.Generic;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class QuadTree<T> where T : Point
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

        private readonly Node _root = new Node();

        private readonly double _left;
        private readonly double _top;
        private readonly double _right;
        private readonly double _bottom;
        private readonly double _epsilon;

        private void _add(T value, double x, double y, Node node, double left, double top, double right, double bottom)
        {
            var currentValue = node.Value;

            if (currentValue == null)
            {
                if (node.HasValueBelow)
                {
                    _addAsChild(value, x, y, node, left, top, right, bottom);
                }
                else
                {
                    node.Value = value;
                }
            }
            else
            {
                var currentX = currentValue.X;
                var currentY = currentValue.Y;

                if (x.CompareTo(currentX) == 0 && y.CompareTo(currentY) == 0)
                {
                    return;
                }

                node.Value = null;
                node.HasValueBelow = true;
                node.InitializeChildren();

                _addAsChild(currentValue, currentX, currentY, node, left, top, right, bottom);
                _addAsChild(value, x, y, node, left, top, right, bottom);
            }
        }

        private void _addAsChild(T value, double x, double y, Node node, double left, double top, double right, double bottom)
        {
            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    _add(value, x, y, node.LeftTop, left, top, centerX, centerY);
                }
                else
                {
                    _add(value, x, y, node.LeftBottom, left, centerY, centerX, bottom);
                }
            }
            else
            {
                if (y < centerY)
                {
                    _add(value, x, y, node.RightTop, centerX, top, right, centerY);
                }
                else
                {
                    _add(value, x, y, node.RightBottom, centerX, centerY, right, bottom);
                }
            }
        }


        // Equal to call of FindNearest(..., Predicate<T> matcher) with (value -> true), but copied for performance reason

        private T FindNearest(double x, double y, Node node, double left, double top, double right, double bottom)
        {
            if (node.Value != null)
                return node.Value;

            if (!node.HasValueBelow)
                return null;

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    var nearestValue = FindNearest(x, y, node.LeftTop, left, top, centerX, centerY);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        var otherValue = FindNearest(x, y, node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        var otherValue = FindNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        var otherValue = FindNearest(x, y, node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    var nearestValue = FindNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        var otherValue = FindNearest(x, y, node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        var otherValue = FindNearest(x, y, node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        var otherValue = FindNearest(x, y, node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

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
                    var nearestValue = FindNearest(x, y, node.RightTop, centerX, top, right, centerY);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        var otherValue = FindNearest(x, y, node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        var otherValue = FindNearest(x, y, node.RightBottom, centerX, centerY, right, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        var otherValue = FindNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    var nearestValue = FindNearest(x, y, node.RightBottom, centerX, centerY, right, bottom);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        var otherValue = FindNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        var otherValue = FindNearest(x, y, node.RightTop, centerX, top, right, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        var otherValue = FindNearest(x, y, node.LeftTop, left, top, centerX, centerY);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
            }
        }

        private T _findNearest(
            double x, double y, Node node,
            double left, double top, double right, double bottom, Predicate<T> matcher
            )
        {
            if (node.Value != null)
            {
                return matcher(node.Value) ? node.Value : null;
            }

            if (!node.HasValueBelow)
            {
                return null;
            }

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    var nearestValue = _findNearest(x, y, node.LeftTop, left, top, centerX, centerY, matcher);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        var otherValue = _findNearest(x, y, node.RightTop, centerX, top, right, centerY, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        var otherValue = _findNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        var otherValue = _findNearest(x, y, node.RightBottom, centerX, centerY, right, bottom, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    var nearestValue = _findNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom, matcher);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        var otherValue = _findNearest(x, y, node.RightBottom, centerX, centerY, right, bottom, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        var otherValue = _findNearest(x, y, node.LeftTop, left, top, centerX, centerY, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        var otherValue = _findNearest(x, y, node.RightTop, centerX, top, right, centerY, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

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
                    var nearestValue = _findNearest(x, y, node.RightTop, centerX, top, right, centerY, matcher);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        var otherValue = _findNearest(x, y, node.LeftTop, left, top, centerX, centerY, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        var otherValue = _findNearest(x, y, node.RightBottom, centerX, centerY, right, bottom, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        var otherValue = _findNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    var nearestValue = _findNearest(x, y, node.RightBottom, centerX, centerY, right, bottom, matcher);
                    var nearestSquaredDistance = _getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        var otherValue = _findNearest(x, y, node.LeftBottom, left, centerY, centerX, bottom, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        var otherValue = _findNearest(x, y, node.RightTop, centerX, top, right, centerY, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + _epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        var otherValue = _findNearest(x, y, node.LeftTop, left, top, centerX, centerY, matcher);
                        var otherSquaredDistance = _getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
            }
        }

        private static bool _remove(double left, double top, double right, double bottom, Node node, T value)
        {
            if (node.Value != null)
            {
                if (node.Value.Equals(value))
                {
                    node.Value = null;
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

        private void _findAllNearby(
            double x, double y, double squaredDistance, List<T> values, Node node,
            double left, double top, double right, double bottom
            )
        {
            if (node.Value != null)
            {
                if (_getSquaredDistanceTo(node.Value, x, y) <= squaredDistance)
                {
                    values.Add(node.Value);
                }
                return;
            }

            if (!node.HasValueBelow)
            {
                return;
            }

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY);

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY);
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom);
                    }
                }
                else
                {
                    _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom);

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY);
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY);
                    }
                }
            }
            else
            {
                if (y < centerY)
                {
                    _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY);

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY);
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom);
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom);
                    }
                }
                else
                {
                    _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom);

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom);
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY);
                    }

                    if (squaredDistance + _epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY);
                    }
                }
            }
        }

        private void _findAllNearby(
            double x, double y, double squaredDistance, List<T> values, Node node,
            double left, double top, double right, double bottom, Predicate<T> matcher
            )
        {
            if (node.Value != null)
            {
                if (_getSquaredDistanceTo(node.Value, x, y, matcher) <= squaredDistance)
                {
                    values.Add(node.Value);
                }
                return;
            }

            if (!node.HasValueBelow)
            {
                return;
            }

            var centerX = (left + right)/2.0D;
            var centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY, matcher);

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY,
                            matcher);
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom,
                            matcher);
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom,
                            matcher);
                    }
                }
                else
                {
                    _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom,
                        matcher);

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom,
                            matcher);
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY, matcher);
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY,
                            matcher);
                    }
                }
            }
            else
            {
                if (y < centerY)
                {
                    _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY, matcher);

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY, matcher);
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom,
                            matcher);
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom,
                            matcher);
                    }
                }
                else
                {
                    _findAllNearby(x, y, squaredDistance, values, node.RightBottom, centerX, centerY, right, bottom,
                        matcher);

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftBottom, left, centerY, centerX, bottom,
                            matcher);
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.RightTop, centerX, top, right, centerY,
                            matcher);
                    }

                    if (squaredDistance + _epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        _findAllNearby(x, y, squaredDistance, values, node.LeftTop, left, top, centerX, centerY, matcher);
                    }
                }
            }
        }


        // Equal to call of hasNearby(..., Predicate<T> matcher) with (value -> true), but copied for performance reason
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

        private bool _hasNearby(
            double x, double y, double squaredDistance, Node node,
            double left, double top, double right, double bottom, Predicate<T> matcher
            )
        {
            if (node.Value != null)
            {
                return _getSquaredDistanceTo(node.Value, x, y, matcher) <= squaredDistance;
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
                    if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY, matcher))
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
                    if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (_hasNearby(x, y, squaredDistance, node.RightBottom, centerX, centerY, right, bottom, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(x - centerX))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftBottom, left, centerY, centerX, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon > Geom.Sqr(y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.RightTop, centerX, top, right, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + _epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        if (_hasNearby(x, y, squaredDistance, node.LeftTop, left, top, centerX, centerY, matcher))
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

        private static double _getSquaredDistanceTo(T value, double x, double y)
        {
            return value == null
                ? double.PositiveInfinity
                : Geom.SumSqr(value.X - x, value.Y - y);
        }

        private static double _getSquaredDistanceTo(T value, double x, double y, Predicate<T> matcher)
        {
            return value == null || !matcher(value)
                ? double.PositiveInfinity
                : Geom.SumSqr(value.X - x, value.Y - y);
        }
        #endregion


        #region Public Members
        public QuadTree(double left, double top, double right, double bottom, double epsilon)
        {
            this._left = left;
            this._top = top;
            this._right = right;
            this._bottom = bottom;

            this._epsilon = epsilon;
        }

        public void Add(T value)
        {
            var x = value.X;
            var y = value.Y;

            if (x < _left || y < _top || x > _right || y > _bottom)
            {
                throw new ArgumentException(
                    $"The point ({x}, {y}) is outside of bounding box ({_left}, {_top}, {_right}, {_bottom}).");
            }

            _add(value, x, y, _root, _left, _top, _right, _bottom);
        }

        public void AddAll(IEnumerable<T> values)
        {
            foreach (var value in values)
                Add(value);
        }

        public T FindNearest(T value)
        {
            return FindNearest(value.X, value.Y);
        }

        public T FindNearest(double x, double y)
        {
            return FindNearest(x, y, _root, _left, _top, _right, _bottom);
        }

        public T FindNearest(T value, Predicate<T> matcher)
        {
            return FindNearest(value.X, value.Y, matcher);
        }

        public T FindNearest(double x, double y, Predicate<T> matcher)
        {
            return _findNearest(x, y, _root, _left, _top, _right, _bottom, matcher);
        }

        public bool Remove(T value)
        {
            return _remove(_left, _top, _right, _bottom, _root, value);
        }

        public List<T> FindAllNearby(T value, double squaredDistance)
        {
            return FindAllNearby(value.X, value.Y, squaredDistance);
        }

        public List<T> FindAllNearby(double x, double y, double squaredDistance)
        {
            var values = new List<T>();
            _findAllNearby(x, y, squaredDistance, values, _root, _left, _top, _right, _bottom);
            return values;
        }

        public List<T> FindAllNearby(T value, double squaredDistance, Predicate<T> matcher)
        {
            return FindAllNearby(value.X, value.Y, squaredDistance, matcher);
        }

        public List<T> FindAllNearby(double x, double y, double squaredDistance, Predicate<T> matcher)
        {
            var values = new List<T>();
            _findAllNearby(x, y, squaredDistance, values, _root, _left, _top, _right, _bottom, matcher);
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

        public bool HasNearby(T value, double squaredDistance, Predicate<T> matcher)
        {
            return HasNearby(value.X, value.Y, squaredDistance, matcher);
        }

        public bool HasNearby(double x, double y, double squaredDistance, Predicate<T> matcher)
        {
            return _hasNearby(x, y, squaredDistance, _root, _left, _top, _right, _bottom, matcher);
        }

        public void Clear()
        {
            _clear(_root);
        }
        #endregion
    }
}