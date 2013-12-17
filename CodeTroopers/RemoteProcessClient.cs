using System;
using System.IO;
using System.Net.Sockets;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public sealed class RemoteProcessClient
    {
        private const int BufferSizeBytes = 1 << 20;

        private readonly TcpClient client;
        private readonly BinaryReader reader;
        private readonly BinaryWriter writer;

        private CellType[][] cells;
        private bool[] cellVisibilities;

        public RemoteProcessClient(string host, int port)
        {
            client = new TcpClient(host, port) {SendBufferSize = BufferSizeBytes, ReceiveBufferSize = BufferSizeBytes};

            reader = new BinaryReader(client.GetStream());
            writer = new BinaryWriter(client.GetStream());
        }

        public void WriteToken(string token)
        {
            WriteEnum((sbyte?) MessageType.AuthenticationToken);
            WriteString(token);
            writer.Flush();
        }

        public int ReadTeamSize()
        {
            EnsureMessageType((MessageType) ReadEnum(), MessageType.TeamSize);
            return ReadInt();
        }

        public void WriteProtocolVersion()
        {
            WriteEnum((sbyte?) MessageType.ProtocolVersion);
            WriteInt(2);
            writer.Flush();
        }

        public Game readGameContext()
        {
            EnsureMessageType((MessageType) ReadEnum(), MessageType.GameContext);
            if (!ReadBoolean())
            {
                return null;
            }

            return new Game(ReadInt(),
                ReadInt(), ReadInt(),
                ReadInt(), ReadDouble(),
                ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                ReadInt(), ReadDouble(),
                ReadInt(), ReadInt(),
                ReadInt(), ReadInt(), ReadInt(),
                ReadDouble(), ReadDouble(), ReadDouble(),
                ReadDouble(), ReadDouble(),
                ReadDouble(), ReadDouble(),
                ReadInt(), ReadDouble(), ReadInt(), ReadInt(),
                ReadInt(), ReadInt(), ReadInt(),
                ReadInt(), ReadInt());
        }

        public PlayerContext ReadPlayerContext()
        {
            MessageType messageType = (MessageType) ReadEnum();
            if (messageType == MessageType.GameOver)
            {
                return null;
            }

            EnsureMessageType(messageType, MessageType.PlayerContext);
            return ReadBoolean() ? new PlayerContext(ReadTrooper(), ReadWorld()) : null;
        }

        public void WriteMove(Move move)
        {
            WriteEnum((sbyte?) MessageType.Move);

            if (move == null)
            {
                WriteBoolean(false);
            }
            else
            {
                WriteBoolean(true);

                WriteEnum((sbyte?) move.Action);
                WriteEnum((sbyte?) move.Direction);
                WriteInt(move.X);
                WriteInt(move.Y);
            }

            writer.Flush();
        }

        public void Close()
        {
            client.Close();
        }

        private World ReadWorld()
        {
            if (!ReadBoolean())
            {
                return null;
            }

            return new World(ReadInt(), ReadInt(), ReadInt(), ReadPlayers(), ReadTroopers(), ReadBonuses(), ReadCells(),
                ReadCellVisibilities());
        }

        private Player[] ReadPlayers()
        {
            int playerCount = ReadInt();
            if (playerCount < 0)
            {
                return null;
            }

            Player[] players = new Player[playerCount];

            for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex)
            {
                if (ReadBoolean())
                {
                    players[playerIndex] = new Player(ReadLong(), ReadString(), ReadInt(), ReadBoolean(),
                        ReadInt(), ReadInt());
                }
            }

            return players;
        }

        private Trooper[] ReadTroopers()
        {
            int trooperCount = ReadInt();
            if (trooperCount < 0)
            {
                return null;
            }

            Trooper[] troopers = new Trooper[trooperCount];

            for (int trooperIndex = 0; trooperIndex < trooperCount; ++trooperIndex)
            {
                troopers[trooperIndex] = ReadTrooper();
            }

            return troopers;
        }

        private Trooper ReadTrooper()
        {
            if (!ReadBoolean())
            {
                return null;
            }

            return new Trooper(ReadLong(), ReadInt(), ReadInt(), ReadLong(),
                ReadInt(), ReadBoolean(), (TrooperType) ReadEnum(), (TrooperStance) ReadEnum(),
                ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                ReadDouble(), ReadDouble(), ReadInt(),
                ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                ReadBoolean(), ReadBoolean(), ReadBoolean());
        }

        private Bonus[] ReadBonuses()
        {
            int bonusCount = ReadInt();
            if (bonusCount < 0)
            {
                return null;
            }

            Bonus[] bonuses = new Bonus[bonusCount];

            for (int bonusIndex = 0; bonusIndex < bonusCount; ++bonusIndex)
            {
                if (ReadBoolean())
                {
                    bonuses[bonusIndex] = new Bonus(ReadLong(), ReadInt(), ReadInt(), (BonusType) ReadEnum());
                }
            }

            return bonuses;
        }

        private CellType[][] ReadCells()
        {
            if (cells != null)
            {
                return cells;
            }

            int width = ReadInt();
            if (width < 0)
            {
                return null;
            }

            cells = new CellType[width][];

            for (int x = 0; x < width; ++x)
            {
                int height = ReadInt();
                if (height < 0)
                {
                    continue;
                }

                cells[x] = new CellType[height];

                for (int y = 0; y < height; ++y)
                {
                    cells[x][y] = (CellType) ReadEnum();
                }
            }

            return cells;
        }

        private bool[] ReadCellVisibilities()
        {
            if (cellVisibilities != null)
            {
                return cellVisibilities;
            }

            int worldWidth = ReadInt();
            if (worldWidth < 0)
            {
                return null;
            }

            int worldHeight = ReadInt();
            if (worldHeight < 0)
            {
                return null;
            }

            int stanceCount = ReadInt();
            if (stanceCount < 0)
            {
                return null;
            }

            int rawVisibilityCount = worldWidth * worldHeight * worldWidth * worldHeight * stanceCount;
            byte[] rawVisibilities = ReadBytes(rawVisibilityCount);
            cellVisibilities = new bool[rawVisibilityCount];

            for (int rawVisibilityIndex = 0; rawVisibilityIndex < rawVisibilityCount; ++rawVisibilityIndex)
            {
                cellVisibilities[rawVisibilityIndex] = rawVisibilities[rawVisibilityIndex] != 0;
            }

            return cellVisibilities;
        }

        private static void EnsureMessageType(MessageType actualType, MessageType expectedType)
        {
            if (actualType != expectedType)
            {
                throw new ArgumentException(string.Format("Received wrong message [actual={0}, expected={1}].",
                    actualType, expectedType));
            }
        }

        private sbyte? ReadEnum()
        {
            sbyte value = reader.ReadSByte();
            return value < 0 ? null : (sbyte?) value;
        }

        private void WriteEnum(sbyte? value)
        {
            writer.Write(value ?? -1);
        }

        private string ReadString()
        {
            int length = ReadInt();
            if (length == -1)
            {
                return null;
            }

            return Encoding.UTF8.GetString(ReadBytes(length));
        }

        private void WriteString(string value)
        {
            if (value == null)
            {
                WriteInt(-1);
                return;
            }

            byte[] bytes = Encoding.UTF8.GetBytes(value);

            WriteInt(bytes.Length);
            WriteBytes(bytes);
        }

        private bool ReadBoolean()
        {
            return reader.ReadSByte() != 0;
        }

        private void WriteBoolean(bool value)
        {
            writer.Write((sbyte) (value ? 1 : 0));
        }

        private int ReadInt()
        {
            return reader.ReadInt32();
        }

        private void WriteInt(int value)
        {
            writer.Write(value);
        }

        private long ReadLong()
        {
            return reader.ReadInt64();
        }

        private void WriteLong(long value)
        {
            writer.Write(value);
        }

        private double ReadDouble()
        {
            return reader.ReadDouble();
        }

        private void WriteDouble(double value)
        {
            writer.Write(value);
        }

        private byte[] ReadBytes(int byteCount)
        {
            return reader.ReadBytes(byteCount);
        }

        private void WriteBytes(byte[] bytes)
        {
            writer.Write(bytes);
        }

        private enum MessageType
        {
            Unknown,
            GameOver,
            AuthenticationToken,
            TeamSize,
            ProtocolVersion,
            GameContext,
            PlayerContext,
            Move
        }
    }
}