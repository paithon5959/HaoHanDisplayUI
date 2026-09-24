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
import vn.haohan.displayui.api.component.ButtonComponent;

import java.util.Objects;

/**
 * Event fired when a {@link ButtonComponent} is clicked by a player.
 */
public record ComponentClickEvent(
        ButtonComponent button,
        Player player,
        float localX,
        float localY
) {
    public ComponentClickEvent {
        Objects.requireNonNull(button, "button cannot be null");
    }

    public static ComponentClickEvent of(ButtonComponent button, Player player) {
        return new ComponentClickEvent(button, player, 0.0f, 0.0f);
    }
}
