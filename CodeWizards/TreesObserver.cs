using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class TreesObserver
    {
        private static Dictionary<long, ATree> _prevState = new Dictionary<long, ATree>();
        public static List<ATree> NewTrees = new List<ATree>(), DisappearedTrees = new List<ATree>();

        public static void Update(World world)
        {
            var newState = new Dictionary<long, ATree>();
            NewTrees.Clear();
            DisappearedTrees.Clear();

            foreach (var tree in world.Trees)
            {
                var a = new ATree(tree);
                if (!_prevState.ContainsKey(tree.Id))
                {
                    // новое дерево
                    NewTrees.Add(a);
                }
                newState[tree.Id] = a;
            }

            foreach (var it in _prevState)
            {
                if (!MyStrategy.IsPointVisible(it.Value))
                {
                    // его не видно, считаем что осталось
                    newState[it.Key] = it.Value;
                }
                else if (!newState.ContainsKey(it.Key))
                {
                    // видно, но исчезло
                    DisappearedTrees.Add(it.Value);
                }
            }

            _prevState = newState;
        }

        public static IEnumerable<ATree> Trees => _prevState.Values;
    }
}
