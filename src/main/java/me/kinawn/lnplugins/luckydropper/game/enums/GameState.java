package me.kinawn.lnplugins.luckydropper.game.enums;

public enum GameState {
    WAITING("Waiting"),
    STARTING("Starting"),
    IN_PROGRESS("In Progress"),
    ENDING("Ending"),
    ENDED("Ended");

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == STARTING || this == IN_PROGRESS;
    }

    public boolean canJoin() {
        return this == WAITING || this == STARTING;
    }

    public boolean isEnding() {
        return this == ENDING || this == ENDED;
    }
}