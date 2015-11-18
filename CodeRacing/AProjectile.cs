using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AProjectile : ACircle
    {
        public ProjectileType Type;
        public Point Speed;
        public bool Exists = true;

        public void Move()
        {
            X += Speed.X;
            Y += Speed.Y;
        }
    }
}
