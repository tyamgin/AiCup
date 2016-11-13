using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class MessagesObserver
    {
        public static void Update(World world)
        {
            // TODO        
        }


        public LaneType GetLane()
        {
            var arr = new LaneType[] { LaneType.Top, LaneType.Bottom };
            return arr[MyStrategy.Self.Id%arr.Length];
        }
    }
}
