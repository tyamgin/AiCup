#ifndef CODEBALL_RANDOMGENERATORS_H
#define CODEBALL_RANDOMGENERATORS_H

class RandomGenerator {
public:
    enum Mode {
        DummyMean,
        Simple
    };

    Mode mode;

    double randDouble() {
        if (mode == DummyMean) {
            return 0.5;
        }

        if (mode == Simple) {
            // https://stackoverflow.com/questions/1640258/need-a-fast-random-generator-for-c
            seed = 214013u * seed + 2531011u;
            return ((seed >> 16) & 0x7FFF) / (0x7FFF + 0.0);
        }
        return 0.0;
    }

    double randDouble(double min, double max) {
        return randDouble() * (max - min) + min;
    }

    explicit RandomGenerator(Mode mode = DummyMean, uint32_t seed = 2707) : mode(mode), seed(seed) {
    }

protected:
    uint32_t seed;
};



#endif //CODEBALL_RANDOMGENERATORS_H
