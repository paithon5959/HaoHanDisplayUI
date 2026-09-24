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
package vn.haohan.displayui.demo.pages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.CustomNodeComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.node.MobEntityNode;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

public final class MobShowcase3DDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "3D MOB SHOWCASE";
    }

    @Override
    public void build(Container container, DemoContext context) {
        final float slotW = 54.0f;
        final float slotH = 46.0f;
        final float[] colsX = {0.0f, 60.0f, 120.0f};
        final float rowY = 0.0f;

        addMobSlot(container, "mob_allay", colsX[0], rowY, slotW, slotH,
                EntityType.ALLAY, "ALLAY", NamedTextColor.AQUA, 0.36f);

        addMobSlot(container, "mob_warden", colsX[1], rowY, slotW, slotH,
                EntityType.WARDEN, "WARDEN", NamedTextColor.DARK_AQUA, 0.46f);

        addMobSlot(container, "mob_bee", colsX[2], rowY, slotW, slotH,
                EntityType.BEE, "BEE", NamedTextColor.GOLD, 0.60f);

        container.addComponent(TextComponent.builder("mob_subtitle")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 49.0f)
                .size(174.0f, 6.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text("Real-time 3D Minecraft mob entities integrated seamlessly into Display UI",
                        NamedTextColor.GRAY))
                .fontSize(3.8f)
                .build());

        container.addComponent(ButtonComponent.builder("mob_cmd_info")
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, 0.0f)
                .size(110.0f, 13.0f)
                .label("MODEL PACKS INFO")
                .borderRound(3.0f)
                .build());
    }

    @Override
    public boolean onClick(DemoContext context, String buttonId, Player player) {
        if ("mob_cmd_info".equals(buttonId)) {
            player.sendMessage("§d[DisplayUI] §7Mob models use custom item models or native display entities.");
            return true;
        }
        return false;
    }

    private void addMobSlot(Container container, String id, float x, float y,
                            float w, float h, EntityType entityType, String label,
                            NamedTextColor accent, float scale) {
        Container slot = Container.builder(id + "_slot")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(x, y)
                .size(w, h)
                .backgroundColor(Color.fromRGB(20, 24, 32))
                .borderRound(4.0f)
                .build();

        // 3D Mob Entity Node via CustomNodeComponent
        slot.addComponent(CustomNodeComponent.builder(id + "_mob")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 0.0f)
                .size(w, h)
                .factory((gx, gy, sw, sh, d, s) -> new MobEntityNode(entityType, gx + sw * 0.5f, gy + 14.0f, scale * s))
                .build());

        // Label
        slot.addComponent(TextComponent.builder(id + "_title")
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -11.0f)
                .size(w, 7.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text(label, accent, TextDecoration.BOLD))
                .fontSize(4.5f)
                .shadow(true)
                .build());

        // Action button
        slot.addComponent(ButtonComponent.builder(id)
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -2.0f)
                .size(w - 8.0f, 7.5f)
                .label("SELECT")
                .borderRound(2.0f)
                .build());

        container.addContainer(slot);
    }
}
