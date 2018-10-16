package me.andreww7985.connectplus.dfu

enum class DfuState {
    FILE_NOT_SELECTED,
    LOADING_FILE,
    FILE_LOADED,
    BAD_FILE,
    INITIALIZING_FLASHING,
    FLASHING,
    FLASHING_DONE,
    FLASHING_ERROR;
}