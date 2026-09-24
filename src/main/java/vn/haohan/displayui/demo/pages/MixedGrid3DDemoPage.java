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
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.CustomNodeComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.node.EntityModelNode;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

public final class MixedGrid3DDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "3D ROTATING GRID (ITEMS & MOBS)";
    }

    @Override
    public void build(Container container, DemoContext context) {
        final float slotW = 40.0f;
        final float slotH = 33.0f;
        final float[] colsX = {0.0f, 44.5f, 89.0f, 133.5f};
        final float row1Y = 0.0f;
        final float row2Y = 36.0f;

        // Row 1: Item Models
        addModelSlot(container, "showcase_helmet", colsX[0], row1Y, slotW, slotH,
                "HELMET (TILT)", NamedTextColor.WHITE,
                (gx, gy, w, h, d, s) -> new EntityModelNode(new ItemStack(Material.NETHERITE_HELMET),
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 20.0f * s, 0.22f)
                        .withTransform(ItemDisplay.ItemDisplayTransform.FIXED)
                        .cursorTrack());

        addModelSlot(container, "showcase_trident", colsX[1], row1Y, slotW, slotH,
                "TRIDENT (SPIN)", NamedTextColor.AQUA,
                (gx, gy, w, h, d, s) -> new EntityModelNode(new ItemStack(Material.TRIDENT),
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 20.0f * s, 0.18f)
                        .withTransform(ItemDisplay.ItemDisplayTransform.FIXED)
                        .autoSpin(2.5f));

        addModelSlot(container, "showcase_sword", colsX[2], row1Y, slotW, slotH,
                "SWORD (TILT)", NamedTextColor.LIGHT_PURPLE,
                (gx, gy, w, h, d, s) -> new EntityModelNode(new ItemStack(Material.DIAMOND_SWORD),
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 20.0f * s, 0.18f)
                        .withTransform(ItemDisplay.ItemDisplayTransform.FIXED)
                        .yawRange(-45, 45).pitchRange(-25, 25));

        addModelSlot(container, "showcase_spyglass", colsX[3], row1Y, slotW, slotH,
                "SPYGLASS", NamedTextColor.GOLD,
                (gx, gy, w, h, d, s) -> new EntityModelNode(new ItemStack(Material.SPYGLASS),
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 20.0f * s, 0.18f)
                        .withTransform(ItemDisplay.ItemDisplayTransform.FIXED));

        // Row 2: Mob Models
        addModelSlot(container, "showcase_cow", colsX[0], row2Y, slotW, slotH,
                "COW", NamedTextColor.GREEN,
                (gx, gy, w, h, d, s) -> EntityModelNode.forMob("cow",
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 18.0f * s, 0.28f)
                        .hoverSpin(3.0f));

        addModelSlot(container, "showcase_pig", colsX[1], row2Y, slotW, slotH,
                "PIG", NamedTextColor.LIGHT_PURPLE,
                (gx, gy, w, h, d, s) -> EntityModelNode.forMob("pig",
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 18.0f * s, 0.28f)
                        .autoSpin(2.0f));

        addModelSlot(container, "showcase_zombie", colsX[2], row2Y, slotW, slotH,
                "ZOMBIE", NamedTextColor.RED,
                (gx, gy, w, h, d, s) -> EntityModelNode.forMob("zombie",
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 18.0f * s, 0.30f)
                        .yawRange(-50, 50).pitchRange(-30, 30));

        addModelSlot(container, "showcase_creeper", colsX[3], row2Y, slotW, slotH,
                "CREEPER", NamedTextColor.DARK_GREEN,
                (gx, gy, w, h, d, s) -> EntityModelNode.forMob("creeper",
                        gx + w * 0.5f, gy + 10.0f, 20.0f * s, 18.0f * s, 0.30f)
                        .autoSpin(2.5f));

        // Footer hint
        container.addComponent(TextComponent.builder("grid_hint")
                .anchor(UiAnchorPoint.BOTTOM_LEFT)
                .origin(UiAnchorPoint.BOTTOM_LEFT)
                .offset(0.0f, 3.0f)
                .size(174.0f, 5.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text("Hold left-click to drag & rotate · 3D Models tilt, pitch and roll seamlessly", NamedTextColor.GRAY))
                .fontSize(3.5f)
                .build());
    }

    private void addModelSlot(Container container, String id, float x, float y,
                              float w, float h, String label, NamedTextColor accent,
                              CustomNodeComponent.NodeFactory factory) {
        Container slot = Container.builder(id + "_slot")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(x, y)
                .size(w, h)
                .backgroundColor(Color.fromRGB(20, 24, 32))
                .borderRound(4.0f)
                .build();

        // 3D Model Node via CustomNodeComponent
        slot.addComponent(CustomNodeComponent.builder(id + "_node")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 0.0f)
                .size(w, h)
                .factory(factory)
                .build());

        // Label
        slot.addComponent(TextComponent.builder(id + "_lbl")
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -8.0f)
                .size(w, 6.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text(label, accent, TextDecoration.BOLD))
                .fontSize(3.4f)
                .shadow(true)
                .build());

        // Click interaction button
        slot.addComponent(ButtonComponent.builder(id)
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -1.0f)
                .size(w - 6.0f, 6.5f)
                .label("VIEW")
                .borderRound(2.0f)
                .build());

        container.addContainer(slot);
    }
}
