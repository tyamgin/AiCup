cmake_minimum_required(VERSION 3.5)

project(CodeSide)

set(CMAKE_CXX_STANDARD 17)

if (CMAKE_BUILD_TYPE MATCHES Debug)
    add_definitions(-DDEBUG)
endif()
if (CMAKE_FAST_DEBUG MATCHES true)
    add_definitions(-DDEBUG)
endif()

if (CMAKE_BUILD_TYPE MATCHES Release)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} \
        -fno-optimize-sibling-calls -fno-strict-aliasing \
        -lm -O2 -Wall -Wtype-limits -Wno-unknown-pragmas")
endif()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-inconsistent-missing-override")

add_definitions(-DLOCAL)

file(GLOB HEADERS "*.hpp" "model/*.hpp" "csimplesocket/*.h" "src/*.h" "src/model/*.h")
SET_SOURCE_FILES_PROPERTIES(${HEADERS} PROPERTIES HEADER_FILE_ONLY TRUE)
file(GLOB SRC "*.cpp" "model/*.cpp" "csimplesocket/*.cpp")
add_executable(aicup2019 ${HEADERS} ${SRC})