namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class BitSet
    {
        public readonly int Size;
        private uint[] _bytes;
        public BitSet(int size)
        {
            Size = size;
            _bytes = new uint[(size + 31) / 32];
        }

        public bool this[int idx]
        {
            get { return 1 == (1 & (_bytes[idx >> 5] >> (idx & 0x20))); }
            set
            {
                if (value != (1 == (1 & (_bytes[idx >> 5] >> (idx & 0x20)))))
                    _bytes[idx >> 5] ^= 1u << (idx & 0x20);
            }
        }

    }

    public class BitMask
    {
        private uint _mask;
        

        public BitMask(uint mask = 0)
        {
            _mask = mask;
        }

        public BitMask Clone()
        {
            return new BitMask(_mask);
        }

        public bool this[int idx]
        {
            get { return 1 == (1 & (_mask >> idx)); }
            set
            {
                if (value != (1 == (1 & (_mask >> idx))))
                    _mask ^= 1u << idx;
            }
        }
    }
}
