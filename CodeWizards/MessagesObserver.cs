using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class MessagesObserver
    {
        public static Message LastMessage;

        public static void Update()
        {
            var msges = MyStrategy.Self.Messages;
            if (msges != null && msges.Length > 0)
                LastMessage = msges[msges.Length - 1];
        }


        public static ALaneType GetLane()
        {
            if (LastMessage != null)
                return (ALaneType) LastMessage.Lane;

            var arr = new [] { LaneType.Top, LaneType.Bottom };
            return (ALaneType) arr[MyStrategy.Self.Id%arr.Length];
        }
    }
}
