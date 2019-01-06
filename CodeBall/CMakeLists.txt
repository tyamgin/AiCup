cmake_minimum_required(VERSION 3.12)
project(CodeBall)

set(CMAKE_CXX_STANDARD 17)

file(GLOB strategy_SRC "*.cpp" "model/*.cpp" "csimplesocket/*.cpp")

add_executable(CodeBall ${strategy_SRC})

if (CMAKE_BUILD_TYPE MATCHES Debug)
    add_definitions(-DDEBUG)
endif()

add_definitions(-DLOCAL)

#if (CMAKE_BUILD_TYPE MATCHES Release)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} \
        -fno-optimize-sibling-calls -fno-strict-aliasing \
        -lm -O2 -Wall -Wtype-limits -Wno-unknown-pragmas")
#endif()

#add_definitions(-D_GLIBCXX_USE_CXX11_ABI=0)