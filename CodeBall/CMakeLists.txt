cmake_minimum_required(VERSION 3.12)
project(CodeBall)

set(CMAKE_CXX_STANDARD 17)

set(SOURCE_FILES
        Runner.cpp
        Strategy.cpp
        MyStrategy.cpp
        csimplesocket/ActiveSocket.cpp
        csimplesocket/HTTPActiveSocket.cpp
        csimplesocket/PassiveSocket.cpp
        csimplesocket/SimpleSocket.cpp
        RemoteProcessClient.cpp
        )

add_executable(CodeBall ${SOURCE_FILES})


if (CMAKE_BUILD_TYPE MATCHES Debug)
    add_definitions(-DDEBUG)
endif()

add_definitions(-D_GLIBCXX_USE_CXX11_ABI=0)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -static-libstdc++")