/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 *
 * HaoHanDisplayUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package vn.haohan.displayui.api.container;

import vn.haohan.displayui.api.animation.Easings;
import vn.haohan.displayui.api.animation.UiAnimation;

/**
 * Built-in animation styles for {@link DropdownContainer} when expanding or collapsing child containers.
 */
public enum DropdownAnimationType {
    /**
     * Child containers slide downwards from the header into position.
     */
    SLIDE_DOWN,

    /**
     * Child containers smoothly fade in from 0% to 100% opacity.
     */
    FADE_IN,

    /**
     * Combines downwards sliding with smooth opacity fade-in.
     */
    SLIDE_AND_FADE,

    /**
     * Child containers scale up smoothly into place.
     */
    SCALE_IN,

    /**
     * Child containers bounce downwards with an elastic overshoot.
     */
    BOUNCE_OUT,

    /**
     * Custom caller-specified {@link UiAnimation}.
     */
    CUSTOM;

    /**
     * Creates a {@link UiAnimation} representing this animation type for expanding child containers.
     *
     * @param durationTicks animation duration in Minecraft ticks
     * @param easing movement easing curve
     * @param slideDistance distance in logical UI pixels for slide movement
     * @return constructed {@link UiAnimation}
     */
    public UiAnimation createExpandAnimation(int durationTicks, Easings easing, float slideDistance) {
        return switch (this) {
            case SLIDE_DOWN -> UiAnimation.slideIn(durationTicks, UiAnimation.Direction.TOP, slideDistance, easing);
            case FADE_IN -> UiAnimation.fadeIn(durationTicks, easing);
            case SLIDE_AND_FADE -> UiAnimation.builder()
                    .durationTicks(durationTicks)
                    .easing(easing)
                    .offset(UiAnimation.Direction.TOP, slideDistance)
                    .opacity(0.0f, 1.0f)
                    .build();
            case SCALE_IN -> UiAnimation.builder()
                    .durationTicks(durationTicks)
                    .easing(easing)
                    .scale(0.7f, 1.0f)
                    .opacity(0.0f, 1.0f)
                    .build();
            case BOUNCE_OUT -> UiAnimation.slideIn(durationTicks, UiAnimation.Direction.TOP, slideDistance, Easings.BackOut);
            case CUSTOM -> UiAnimation.fadeIn(durationTicks, easing);
        };
    }
}
