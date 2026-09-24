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
package vn.haohan.displayui.api.layout;

import java.util.Objects;

/**
 * Normalized 2D reference points used for container anchoring and element origin alignment.
 *
 * <p>Each point defines normalized coordinates:
 * <ul>
 *   <li>{@code xFactor}: {@code 0.0} (Left), {@code 0.5} (Center), {@code 1.0} (Right)</li>
 *   <li>{@code yFactor}: {@code 0.0} (Top), {@code 0.5} (Center), {@code 1.0} (Bottom)</li>
 * </ul>
 */
public enum UiAnchorPoint {
    TOP_LEFT(0.0f, 0.0f),
    CENTER_TOP(0.5f, 0.0f),
    TOP_RIGHT(1.0f, 0.0f),
    CENTER_LEFT(0.0f, 0.5f),
    CENTER(0.5f, 0.5f),
    CENTER_RIGHT(1.0f, 0.5f),
    BOTTOM_LEFT(0.0f, 1.0f),
    CENTER_BOTTOM(0.5f, 1.0f),
    BOTTOM_RIGHT(1.0f, 1.0f);

    private final float xFactor;
    private final float yFactor;

    UiAnchorPoint(float xFactor, float yFactor) {
        this.xFactor = xFactor;
        this.yFactor = yFactor;
    }

    /**
     * Normalized horizontal factor from 0.0 (left) to 1.0 (right).
     */
    public float xFactor() {
        return xFactor;
    }

    /**
     * Normalized vertical factor from 0.0 (top) to 1.0 (bottom).
     */
    public float yFactor() {
        return yFactor;
    }

    /**
     * Calculates the horizontal top-left coordinate of a child within a parent container.
     *
     * @param parentWidth  the width of the parent container
     * @param offsetX      the local offset from the anchor
     * @param childWidth   the width of the child element
     * @param childOrigin  the origin (pivot) of the child element
     * @return the resolved top-left X coordinate in parent space
     */
    public float resolveX(float parentWidth, float offsetX, float childWidth, UiAnchorPoint childOrigin) {
        Objects.requireNonNull(childOrigin, "childOrigin cannot be null");
        float anchorX = parentWidth * this.xFactor;
        float originOffsetX = childWidth * childOrigin.xFactor;
        return anchorX + offsetX - originOffsetX;
    }

    /**
     * Calculates the vertical top-left coordinate of a child within a parent container.
     *
     * @param parentHeight the height of the parent container
     * @param offsetY      the local offset from the anchor
     * @param childHeight  the height of the child element
     * @param childOrigin  the origin (pivot) of the child element
     * @return the resolved top-left Y coordinate in parent space
     */
    public float resolveY(float parentHeight, float offsetY, float childHeight, UiAnchorPoint childOrigin) {
        Objects.requireNonNull(childOrigin, "childOrigin cannot be null");
        float anchorY = parentHeight * this.yFactor;
        float originOffsetY = childHeight * childOrigin.yFactor;
        return anchorY + offsetY - originOffsetY;
    }

    /**
     * Resolves the full top-left coordinates [left, top] of a child within a parent container.
     *
     * @param parentWidth  width of the parent container
     * @param parentHeight height of the parent container
     * @param offsetX      local offset X
     * @param offsetY      local offset Y
     * @param childWidth   width of child
     * @param childHeight  height of child
     * @param childOrigin  origin (pivot) of child
     * @return float array containing [left, top]
     */
    public float[] resolve(float parentWidth, float parentHeight, float offsetX, float offsetY,
                           float childWidth, float childHeight, UiAnchorPoint childOrigin) {
        return new float[]{
                resolveX(parentWidth, offsetX, childWidth, childOrigin),
                resolveY(parentHeight, offsetY, childHeight, childOrigin)
        };
    }

    /**
     * Converts a legacy {@link UiAnchor} to {@link UiAnchorPoint}.
     */
    public static UiAnchorPoint fromLegacy(UiAnchor anchor) {
        if (anchor == null) return CENTER;
        return switch (anchor) {
            case TOP_LEFT -> TOP_LEFT;
            case TOP_CENTER -> CENTER_TOP;
            case TOP_RIGHT -> TOP_RIGHT;
            case CENTER_LEFT -> CENTER_LEFT;
            case CENTER -> CENTER;
            case CENTER_RIGHT -> CENTER_RIGHT;
            case BOTTOM_LEFT -> BOTTOM_LEFT;
            case BOTTOM_CENTER -> CENTER_BOTTOM;
            case BOTTOM_RIGHT -> BOTTOM_RIGHT;
        };
    }

    /**
     * Converts this anchor point to legacy {@link UiAnchor}.
     */
    public UiAnchor toLegacy() {
        return switch (this) {
            case TOP_LEFT -> UiAnchor.TOP_LEFT;
            case CENTER_TOP -> UiAnchor.TOP_CENTER;
            case TOP_RIGHT -> UiAnchor.TOP_RIGHT;
            case CENTER_LEFT -> UiAnchor.CENTER_LEFT;
            case CENTER -> UiAnchor.CENTER;
            case CENTER_RIGHT -> UiAnchor.CENTER_RIGHT;
            case BOTTOM_LEFT -> UiAnchor.BOTTOM_LEFT;
            case CENTER_BOTTOM -> UiAnchor.BOTTOM_CENTER;
            case BOTTOM_RIGHT -> UiAnchor.BOTTOM_RIGHT;
        };
    }
}
