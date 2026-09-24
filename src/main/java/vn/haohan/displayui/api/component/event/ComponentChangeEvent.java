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
package vn.haohan.displayui.api.component.event;

import org.bukkit.entity.Player;
import vn.haohan.displayui.api.component.Component;

import java.util.Objects;

/**
 * Event fired when a value control component (such as {@code SliderComponent} or {@code CheckboxComponent})
 * changes its value.
 */
public record ComponentChangeEvent(
        Component component,
        Player player,
        double oldValue,
        double newValue
) {
    public ComponentChangeEvent {
        Objects.requireNonNull(component, "component cannot be null");
    }

    public static ComponentChangeEvent of(Component component, Player player, double oldValue, double newValue) {
        return new ComponentChangeEvent(component, player, oldValue, newValue);
    }

    /**
     * Convenience boolean value check (e.g. for checkboxes).
     */
    public boolean isChecked() {
        return newValue >= 0.5;
    }
}
