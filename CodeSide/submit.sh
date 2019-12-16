#!/usr/bin/env bash

rm -rf submit.zip
files="CMakeLists.txt Debug.hpp MyStrategy.hpp Stream.hpp TcpStream.hpp main.cpp src MyStrategy.cpp Stream.cpp TcpStream.cpp model"
zip -r submit.zip $files
