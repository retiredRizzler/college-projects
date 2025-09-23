cmake_minimum_required(VERSION 3.5)

add_executable(Tetris_projectTests
    ../src/model/PieceBag.h
    ../src/model/PieceBag.cpp
    ../src/model/pieces/ZPiece.cpp
    ../src/model/pieces/Piece.h
    ../src/model/Position.hpp
    ../src/model/pieces/LPiece.cpp
    ../src/model/pieces/TPiece.cpp
    ../src/model/pieces/JPiece.cpp
    ../src/model/pieces/SPiece.cpp
    ../src/model/pieces/SquarePiece.cpp
    ../src/model/pieces/LPiece.h
    ../src/model/pieces/Piece.cpp
    ../src/model/pieces/IPiece.h
    ../src/model/pieces/IPiece.cpp
    ../src/model/pieces/ZPiece.h
    ../src/model/pieces/SPiece.h
    ../src/model/pieces/SquarePiece.h
    ../src/model/pieces/JPiece.h
    ../src/model/pieces/TPiece.h
    ../src/model/GameBoard.h
    ../src/model/GameBoard.cpp
    ../src/model/GameState.h
    ../src/model/GameState.cpp
        ../src/model/TetrisModel.h
        ../src/model/TetrisModel.cpp
        ../src/model/observer.cpp
        ../src/model/observable.cpp

    testGameBoard.cpp
    testPieceBag.cpp
    testPiece.cpp
        testTetrisModel.cpp
)
target_include_directories(Tetris_projectTests PRIVATE
    ../src
)

Include(FetchContent)

FetchContent_Declare(
  Catch2
  GIT_REPOSITORY https://github.com/catchorg/Catch2.git
  GIT_TAG        v3.3.1 # or a later release
)

FetchContent_MakeAvailable(Catch2)
#list(APPEND CMAKE_MODULE_PATH ${catch2_SOURCE_DIR}/extras)

target_link_libraries(Tetris_projectTests Catch2::Catch2WithMain)
