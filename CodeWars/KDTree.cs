/**
 * https://github.com/Codeforces/codeforces-commons/blob/6169b31876d035782d4fcad6b819676d6102a7a2/code/src/main/java/com/codeforces/commons/collection/QuadTree.java
 */

using System;
using System.Collections.Generic;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class QuadTree<T> where T : Point
    {
        private class Node<T>
        {
            public T value;

            public bool hasValueBelow;

            public Node<T> leftTop;
            public Node<T> rightTop;
            public Node<T> leftBottom;
            public Node<T> rightBottom;

            public void initializeChildren()
            {
                if (leftTop == null)
                {
                    leftTop = new Node<T>();
                    rightTop = new Node<T>();
                    leftBottom = new Node<T>();
                    rightBottom = new Node<T>();
                }
            }
        }

        public static readonly double DEFAULT_EPSILON = 1.0E-6D;

        private readonly Node<T> root = new Node<T>();

        private readonly double left;
        private readonly double top;
        private readonly double right;
        private readonly double bottom;

        private readonly double epsilon;

        public QuadTree(double left, double top, double right, double bottom, double epsilon)
        {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;

            this.epsilon = epsilon;
        }

        public void add(T value)
        {
            double x = value.X;
            double y = value.Y;

            if (x < left || y < top || x > right || y > bottom)
            {
                throw new ArgumentException(String.Format(
                    "The point ({0}, {1}) is outside of bounding box ({2}, {3}, {4}, {5}).",
                    x, y, left, top, right, bottom
                    ));
            }

            add(value, x, y, root, left, top, right, bottom);
        }

        public void addAll(T[] values)
        {
            foreach (T value in values)
            {
                add(value);
            }
        }

        public void addAll(IEnumerable<T> values)
        {
            foreach (T value in values)
            {
                add(value);
            }
        }

        private void add(
            T value, double x, double y, Node<T> node,
            double left, double top, double right, double bottom
            )
        {
            T currentValue = node.value;

            if (currentValue == null)
            {
                if (node.hasValueBelow)
                {
                    addAsChild(value, x, y, node, left, top, right, bottom);
                }
                else
                {
                    node.value = value;
                }
            }
            else
            {
                double currentX = currentValue.X;
                double currentY = currentValue.Y;

                if (x.CompareTo(currentX) == 0 && y.CompareTo(currentY) == 0)
                {
                    return;
                }

                node.value = null;
                node.hasValueBelow = true;
                node.initializeChildren();

                addAsChild(currentValue, currentX, currentY, node, left, top, right, bottom);
                addAsChild(value, x, y, node, left, top, right, bottom);
            }
        }

        private void addAsChild(
            T value, double x, double y, Node<T> node,
            double left, double top, double right, double bottom
            )
        {
            double centerX = (left + right)/2.0D;
            double centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    add(value, x, y, node.leftTop, left, top, centerX, centerY);
                }
                else
                {
                    add(value, x, y, node.leftBottom, left, centerY, centerX, bottom);
                }
            }
            else
            {
                if (y < centerY)
                {
                    add(value, x, y, node.rightTop, centerX, top, right, centerY);
                }
                else
                {
                    add(value, x, y, node.rightBottom, centerX, centerY, right, bottom);
                }
            }
        }


        public T findNearest(T value)
        {
            return findNearest(value.X, value.Y);
        }


        public T findNearest(double x, double y)
        {
            return findNearest(x, y, root, left, top, right, bottom);
        }


        public T findNearest(T value, Predicate<T> matcher)
        {
            return findNearest(value.X, value.Y, matcher);
        }


        public T findNearest(double x, double y, Predicate<T> matcher)
        {
            return findNearest(x, y, root, left, top, right, bottom, matcher);
        }

        // Equal to call of findNearest(..., Predicate<T> matcher) with (value -> true), but copied for performance reason

        private T findNearest(double x, double y, Node<T> node, double left, double top, double right, double bottom)
        {
            if (node.value != null)
            {
                return node.value;
            }

            if (!node.hasValueBelow)
            {
                return null;
            }

            double centerX = (left + right)/2.0D;
            double centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    T nearestValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        T otherValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    T nearestValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        T otherValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

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
                    T nearestValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        T otherValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    T nearestValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y);

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        T otherValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
            }
        }

        private T findNearest(
            double x, double y, Node<T> node,
            double left, double top, double right, double bottom, Predicate<T> matcher

            )
        {
            if (node.value != null)
            {
                return matcher(node.value) ? node.value : null;
            }

            if (!node.hasValueBelow)
            {
                return null;
            }

            double centerX = (left + right)/2.0D;
            double centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    T nearestValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY, matcher);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        T otherValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    T nearestValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom, matcher);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        T otherValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

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
                    T nearestValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY, matcher);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        T otherValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        T otherValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
                else
                {
                    T nearestValue = findNearest(x, y, node.rightBottom, centerX, centerY, right, bottom, matcher);
                    double nearestSquaredDistance = getSquaredDistanceTo(nearestValue, x, y, matcher);

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        T otherValue = findNearest(x, y, node.leftBottom, left, centerY, centerX, bottom, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.rightTop, centerX, top, right, centerY, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                            nearestSquaredDistance = otherSquaredDistance;
                        }
                    }

                    if (nearestSquaredDistance + epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        T otherValue = findNearest(x, y, node.leftTop, left, top, centerX, centerY, matcher);
                        double otherSquaredDistance = getSquaredDistanceTo(otherValue, x, y, matcher);

                        if (otherSquaredDistance < nearestSquaredDistance)
                        {
                            nearestValue = otherValue;
                        }
                    }

                    return nearestValue;
                }
            }
        }

        /*public void check()
        {
            check(root);
        }

        private void check(Node<T> node)
        {
            if (node == null)
                return;
            if (node.value != null)
            {
                if (node.leftTop != null || node.rightTop != null || node.leftBottom != null && node.rightBottom != null)
                    throw new Exception();
            }
            else
            {
                check(node.leftTop);
                check(node.rightTop);
                check(node.leftBottom);
                check(node.rightBottom);
            }
        }*/

        private void _remove(double left, double top, double right, double bottom, Node<T> node, T value)
        {
            if (node.value != null)
            {
                if (node.value.Equals(value))
                {
                    node.value = null;
                }
                return;
            }
            if (!node.hasValueBelow)
            {
                // value not found
                return;
            }

            var x = value.X;
            var y = value.Y;

            double centerX = (left + right) / 2.0D;
            double centerY = (top + bottom) / 2.0D;

            if (y < centerY)
            {
                if (x < centerX)
                {
                    _remove(left, top, centerX, centerY, node.leftTop, value);
                }
                else
                {
                    _remove(centerX, top, right, centerY, node.rightTop, value);
                }
            }
            else
            {
                if (x < centerX)
                {
                    _remove(left, centerY, centerX, bottom, node.leftBottom, value);
                }
                else
                {
                    _remove(centerX, centerY, right, bottom, node.rightBottom, value);
                }
            }

            node.hasValueBelow =
                node.leftTop != null && (node.leftTop.hasValueBelow || node.leftTop.value != null) ||
                node.rightTop != null && (node.rightTop.hasValueBelow || node.rightTop.value != null) ||
                node.leftBottom != null && (node.leftBottom.hasValueBelow || node.leftBottom.value != null) ||
                node.rightBottom != null && (node.rightBottom.hasValueBelow || node.rightBottom.value != null);

            if (!node.hasValueBelow && node.value == null)
                node.leftTop = node.rightTop = node.leftBottom = node.rightBottom = null;
        }

        public void Remove(T value)
        {
            _remove(left, top, right, bottom, root, value);
        }


        public List<T> findAllNearby(T value, double squaredDistance)
        {
            return findAllNearby(value.X, value.Y, squaredDistance);
        }


        public List<T> findAllNearby(double x, double y, double squaredDistance)
        {
            List<T> values = new List<T>();
            findAllNearby(x, y, squaredDistance, values, root, left, top, right, bottom);
            return values;
        }


        public List<T> findAllNearby(T value, double squaredDistance, Predicate<T> matcher)
        {
            return findAllNearby(value.X, value.Y, squaredDistance, matcher);
        }


        public List<T> findAllNearby(double x, double y, double squaredDistance, Predicate<T> matcher)
        {
            List<T> values = new List<T>();
            findAllNearby(x, y, squaredDistance, values, root, left, top, right, bottom, matcher);
            return values;
        }

        private void findAllNearby(
            double x, double y, double squaredDistance, List<T> values, Node<T> node,
            double left, double top, double right, double bottom
            )
        {
            if (node.value != null)
            {
                if (getSquaredDistanceTo(node.value, x, y) <= squaredDistance)
                {
                    values.Add(node.value);
                }
                return;
            }

            if (!node.hasValueBelow)
            {
                return;
            }

            double centerX = (left + right)/2.0D;
            double centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY);

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY);
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom);
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom);
                    }
                }
                else
                {
                    findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom);

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom);
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY);
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY);
                    }
                }
            }
            else
            {
                if (y < centerY)
                {
                    findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY);

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY);
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom);
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom);
                    }
                }
                else
                {
                    findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom);

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom);
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY);
                    }

                    if (squaredDistance + epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY);
                    }
                }
            }
        }

        private void findAllNearby(
            double x, double y, double squaredDistance, List<T> values, Node<T> node,
            double left, double top, double right, double bottom, Predicate<T> matcher
            )
        {
            if (node.value != null)
            {
                if (getSquaredDistanceTo(node.value, x, y, matcher) <= squaredDistance)
                {
                    values.Add(node.value);
                }
                return;
            }

            if (!node.hasValueBelow)
            {
                return;
            }

            double centerX = (left + right)/2.0D;
            double centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY, matcher);

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY,
                            matcher);
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom,
                            matcher);
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom,
                            matcher);
                    }
                }
                else
                {
                    findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom,
                        matcher);

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom,
                            matcher);
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY, matcher);
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY,
                            matcher);
                    }
                }
            }
            else
            {
                if (y < centerY)
                {
                    findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY, matcher);

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY, matcher);
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom,
                            matcher);
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom,
                            matcher);
                    }
                }
                else
                {
                    findAllNearby(x, y, squaredDistance, values, node.rightBottom, centerX, centerY, right, bottom,
                        matcher);

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftBottom, left, centerY, centerX, bottom,
                            matcher);
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.rightTop, centerX, top, right, centerY,
                            matcher);
                    }

                    if (squaredDistance + epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        findAllNearby(x, y, squaredDistance, values, node.leftTop, left, top, centerX, centerY, matcher);
                    }
                }
            }
        }

        public bool hasNearby(T value, double squaredDistance)
        {
            return hasNearby(value.X, value.Y, squaredDistance);
        }

        public bool hasNearby(double x, double y, double squaredDistance)
        {
            return hasNearby(x, y, squaredDistance, root, left, top, right, bottom);
        }

        public bool hasNearby(T value, double squaredDistance, Predicate<T> matcher)
        {
            return hasNearby(value.X, value.Y, squaredDistance, matcher);
        }

        public bool hasNearby(double x, double y, double squaredDistance, Predicate<T> matcher)
        {
            return hasNearby(x, y, squaredDistance, root, left, top, right, bottom, matcher);
        }

        // Equal to call of hasNearby(..., Predicate<T> matcher) with (value -> true), but copied for performance reason
        private bool hasNearby(
            double x, double y, double squaredDistance, Node<T> node,
            double left, double top, double right, double bottom
            )
        {
            if (node.value != null)
            {
                return getSquaredDistanceTo(node.value, x, y) <= squaredDistance;
            }

            if (!node.hasValueBelow)
            {
                return false;
            }

            double centerX = (left + right)/2.0D;
            double centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY))
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
                    if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY))
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }
        }

        private bool hasNearby(
            double x, double y, double squaredDistance, Node<T> node,
            double left, double top, double right, double bottom, Predicate<T> matcher
            )
        {
            if (node.value != null)
            {
                return getSquaredDistanceTo(node.value, x, y, matcher) <= squaredDistance;
            }

            if (!node.hasValueBelow)
            {
                return false;
            }

            double centerX = (left + right)/2.0D;
            double centerY = (top + bottom)/2.0D;

            if (x < centerX)
            {
                if (y < centerY)
                {
                    if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerX - x))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(centerX - x, y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY, matcher))
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
                    if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.Sqr(centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon >= Geom.SumSqr(x - centerX, centerY - y))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    return false;
                }
                else
                {
                    if (hasNearby(x, y, squaredDistance, node.rightBottom, centerX, centerY, right, bottom, matcher))
                    {
                        return true;
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(x - centerX))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftBottom, left, centerY, centerX, bottom, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon > Geom.Sqr(y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.rightTop, centerX, top, right, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    if (squaredDistance + epsilon > Geom.SumSqr(x - centerX, y - centerY))
                    {
                        if (hasNearby(x, y, squaredDistance, node.leftTop, left, top, centerX, centerY, matcher))
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }
        }

        public void clear()
        {
            clear(root);
        }

        private static void clear<T>(Node<T> node)
        {
            node.value = default(T);

            if (node.hasValueBelow)
            {
                node.hasValueBelow = false;

                clear(node.leftTop);
                clear(node.rightTop);
                clear(node.leftBottom);
                clear(node.rightBottom);
            }
        }

        private double getSquaredDistanceTo(T value, double x, double y)
        {
            return value == null
                ? Double.PositiveInfinity
                : Geom.SumSqr(
                    value.X - x, value.Y - y
                    );
        }

        private double getSquaredDistanceTo(T value, double x, double y, Predicate<T> matcher)
        {
            return value == null || !matcher(value)
                ? Double.PositiveInfinity
                : Geom.SumSqr(
                    value.X - x, value.Y - y
                    );
        }
    }
}