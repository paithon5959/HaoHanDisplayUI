/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 *
 * HaoHanDisplayUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HaoHanDisplayUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HaoHanDisplayUI. If not, see <https://www.gnu.org/licenses/>.
 */
package vn.haohan.displayui.api.debug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.component.*;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.container.DropdownContainer;
import vn.haohan.displayui.api.layer.Layer;
import vn.haohan.displayui.api.layer.LayerManager;
import vn.haohan.displayui.api.layout.UiRect;

import java.util.*;

/**
 * Inspection utility that extracts, formats, and displays all detailed properties
 * and layout geometries for Layers, Containers, and Components in the UI hierarchy.
 */
public final class UiDebugInspector {

    public static final String DEFAULT_COMMAND_PREFIX = "/hhdui debug";
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private UiDebugInspector() {}

    /**
     * Inspects a target by specific type or by auto-discovery across layers, containers, and components.
     */
    public static boolean inspectAndSend(CommandSender sender, LayerManager manager, String typeOrId, String optionalId, UiDebugState state) {
        return inspectAndSend(sender, manager, typeOrId, optionalId, state, DEFAULT_COMMAND_PREFIX);
    }

    /**
     * Inspects a target with a customizable command prefix for chat interaction buttons.
     */
    public static boolean inspectAndSend(CommandSender sender, LayerManager manager, String typeOrId, String optionalId, UiDebugState state, String commandPrefix) {
        if (manager == null) {
            sender.sendMessage(Component.text("LayerManager không tồn tại hoặc UI chưa được tạo!", NamedTextColor.RED));
            return false;
        }

        if (optionalId != null && !optionalId.isBlank()) {
            String type = typeOrId.toLowerCase(Locale.ROOT);
            String id = optionalId.toLowerCase(Locale.ROOT);

            switch (type) {
                case "layer" -> {
                    Optional<Layer> found = findLayer(manager, id);
                    if (found.isPresent()) {
                        sendMessages(sender, inspectLayer(found.get(), state, commandPrefix));
                        return true;
                    }
                    sender.sendMessage(Component.text("Không tìm thấy Layer có ID '" + id + "'!", NamedTextColor.RED));
                    return false;
                }
                case "container" -> {
                    Optional<Container> found = findContainer(manager, id);
                    if (found.isPresent()) {
                        sendMessages(sender, inspectContainer(found.get(), state, commandPrefix));
                        return true;
                    }
                    sender.sendMessage(Component.text("Không tìm thấy Container có ID '" + id + "'!", NamedTextColor.RED));
                    return false;
                }
                case "component" -> {
                    Optional<vn.haohan.displayui.api.component.Component> found = findComponent(manager, id);
                    if (found.isPresent()) {
                        sendMessages(sender, inspectComponent(found.get(), state, commandPrefix));
                        return true;
                    }
                    sender.sendMessage(Component.text("Không tìm thấy Component có ID '" + id + "'!", NamedTextColor.RED));
                    return false;
                }
            }
        }

        String targetId = typeOrId.toLowerCase(Locale.ROOT);
        List<Component> inspected = inspectAny(manager, state, targetId, commandPrefix);
        if (inspected != null) {
            sendMessages(sender, inspected);
            return true;
        }

        sender.sendMessage(Component.text("Không tìm thấy Layer, Container, hoặc Component nào có ID '" + targetId + "'!", NamedTextColor.RED));
        return false;
    }

    /**
     * Auto-discovers and inspects a target across Layers, Containers, and Components by ID.
     * Returns null if not found.
     */
    public static List<Component> inspectAny(LayerManager manager, UiDebugState state, String targetId) {
        return inspectAny(manager, state, targetId, DEFAULT_COMMAND_PREFIX);
    }

    public static List<Component> inspectAny(LayerManager manager, UiDebugState state, String targetId, String commandPrefix) {
        if (manager == null || targetId == null) return null;
        String id = targetId.toLowerCase(Locale.ROOT);
        if (id.startsWith("__inspect_comp_")) {
            id = id.substring("__inspect_comp_".length());
        } else if (id.startsWith("__inspect_cont_")) {
            id = id.substring("__inspect_cont_".length());
        } else if (id.startsWith("__inspect_layer_")) {
            id = id.substring("__inspect_layer_".length());
        }

        Optional<Layer> layerOpt = findLayer(manager, id);
        if (layerOpt.isPresent()) {
            return inspectLayer(layerOpt.get(), state, commandPrefix);
        }

        Optional<Container> contOpt = findContainer(manager, id);
        if (contOpt.isPresent()) {
            return inspectContainer(contOpt.get(), state, commandPrefix);
        }

        Optional<vn.haohan.displayui.api.component.Component> compOpt = findComponent(manager, id);
        if (compOpt.isPresent()) {
            return inspectComponent(compOpt.get(), state, commandPrefix);
        }

        return null;
    }

    // --- Inspection Formatters ---

    // --- Inspection Formatters ---

    public static List<Component> inspectLayer(Layer layer, UiDebugState state) {
        return inspectLayer(layer, state, DEFAULT_COMMAND_PREFIX);
    }

    public static List<Component> inspectLayer(Layer layer, UiDebugState state, String commandPrefix) {
        List<Component> lines = new ArrayList<>();
        lines.add(header("LAYER", layer.id(), NamedTextColor.YELLOW));

        lines.add(property("Layer ID", layer.id()));
        lines.add(visibilityProperty("Layer", layer.id(), layer.isVisible(), state));
        lines.add(property("Z-Index / Base Depth", layer.zIndex() + " (" + String.format(Locale.ROOT, "%.4f blocks", layer.baseDepth()) + ")"));
        lines.add(property("Layer Bounds", String.format(Locale.ROOT, "x: %.1f, y: %.1f, w: %.1f, h: %.1f",
                layer.x(), layer.y(), layer.width(), layer.height())));
        if (layer.doubleSided()) {
            lines.add(property("Rendering", "Double-Sided Enabled"));
        }

        // Live Animation Status
        lines.addAll(animationSection("Layer", layer.isAnimating(), false, layer.activeAnimation().orElse(null)));

        List<String> contIds = layer.containers().stream().map(Container::id).toList();
        lines.add(property("Root Containers (" + contIds.size() + ")", contIds.isEmpty() ? "None" : String.join(", ", contIds)));

        lines.add(actionButtons("layer", layer.id(), layer.isVisible(), commandPrefix));
        lines.add(animationButtons("layer", layer.id(), commandPrefix));
        lines.add(footer());
        return lines;
    }

    public static List<Component> inspectContainer(Container container, UiDebugState state) {
        return inspectContainer(container, state, DEFAULT_COMMAND_PREFIX);
    }

    public static List<Component> inspectContainer(Container container, UiDebugState state, String commandPrefix) {
        List<Component> lines = new ArrayList<>();
        boolean isDropdown = container instanceof DropdownContainer;
        lines.add(header("CONTAINER" + (isDropdown ? " (Dropdown)" : ""), container.id(), NamedTextColor.AQUA));

        lines.add(property("Container ID", container.id()));
        lines.add(visibilityProperty("Container", container.id(), container.isVisible(), state));
        lines.add(property("Hierarchy", "Layer: " + container.layer().map(Layer::id).orElse("None") +
                " | Parent: " + container.parentContainer().map(Container::id).orElse("Root")));

        // Dynamic Position & Geometry
        float[] globalPos = container.computeGlobalPosition();
        UiRect bounds = container.computeGlobalBounds();
        lines.add(property("Local Offset", String.format(Locale.ROOT, "(x: %.1f, y: %.1f) [Anchor: %s, Origin: %s]",
                container.x(), container.y(), container.anchor().name(), container.origin().name())));
        lines.add(property("Global Position", String.format(Locale.ROOT, "[x: %.2f, y: %.2f]", globalPos[0], globalPos[1])));
        lines.add(property("Global Bounds", String.format(Locale.ROOT, "x: %.2f, y: %.2f, w: %.2f, h: %.2f",
                bounds.x(), bounds.y(), bounds.width(), bounds.height())));

        // Dynamic Cascade Metrics
        lines.add(property("Scale Factor", String.format(Locale.ROOT, "%.2f (Effective Cascade: %.2f)", container.scale(), container.effectiveScale())));
        lines.add(property("Opacity", String.format(Locale.ROOT, "%.2f (Effective Cascade: %.2f)", container.opacity(), container.effectiveOpacity())));

        if (container.borderRound() > 0.0f) {
            lines.add(property("Border Radius", String.format(Locale.ROOT, "%.1f px", container.borderRound())));
        }
        if (container.backgroundColor() != null) {
            lines.add(property("Background", formatColor(container.backgroundColor())));
        }
        if (container.gradient() != null) {
            lines.add(property("Gradient", container.gradient().toString()));
        }

        if (isDropdown) {
            DropdownContainer dd = (DropdownContainer) container;
            lines.add(property("Dropdown State", dd.isExpanded() ? "§a[▼ EXPANDED]" : "§e[▶ COLLAPSED]"));
            lines.add(property("Header Title", "\"" + PLAIN.serialize(dd.headerTitle()) + "\" (H: " + String.format(Locale.ROOT, "%.1f px", dd.headerHeight()) + ")"));
        }

        // Live Animation Status
        boolean isDirectAnim = container.isAnimating();
        boolean isCascadeAnim = container.isCascadeAnimating();
        UiAnimation activeAnim = container.activeAnimation().orElse(null);
        lines.addAll(animationSection("Container", isDirectAnim, isCascadeAnim, activeAnim));

        List<String> childConts = container.childContainers().stream().map(Container::id).toList();
        if (!childConts.isEmpty()) {
            lines.add(property("Child Containers (" + childConts.size() + ")", String.join(", ", childConts)));
        }
        List<String> compIds = container.components().stream()
                .filter(c -> !c.id().startsWith("__inspect_"))
                .map(vn.haohan.displayui.api.component.Component::id)
                .toList();
        lines.add(property("Direct Components (" + compIds.size() + ")", compIds.isEmpty() ? "None" : String.join(", ", compIds)));

        lines.add(actionButtons("container", container.id(), container.isVisible(), commandPrefix));
        lines.add(animationButtons("container", container.id(), commandPrefix));
        lines.add(footer());
        return lines;
    }

    public static List<Component> inspectComponent(vn.haohan.displayui.api.component.Component component, UiDebugState state) {
        return inspectComponent(component, state, DEFAULT_COMMAND_PREFIX);
    }

    public static List<Component> inspectComponent(vn.haohan.displayui.api.component.Component component, UiDebugState state, String commandPrefix) {
        List<Component> lines = new ArrayList<>();
        lines.add(header("COMPONENT", component.id(), NamedTextColor.WHITE));

        lines.add(property("Component ID", component.id()));
        lines.add(visibilityProperty("Component", component.id(), component.isVisible(), state));
        lines.add(property("Parent Container", component.parent().map(Container::id).orElse("None")));

        // Dynamic Position & Geometry
        if (component.parent().isPresent()) {
            Container parent = component.parent().get();
            float[] localPos = component.computePosition(parent.width(), parent.height());
            UiRect bounds = component.computeBounds(parent.width(), parent.height());
            lines.add(property("Local Offset", String.format(Locale.ROOT, "(x: %.1f, y: %.1f) [Anchor: %s, Origin: %s]",
                    component.x(), component.y(), component.anchor().name(), component.origin().name())));
            lines.add(property("Resolved In Parent", String.format(Locale.ROOT, "[left: %.2f, top: %.2f, w: %.1f, h: %.1f]",
                    localPos[0], localPos[1], bounds.width(), bounds.height())));
        } else {
            lines.add(property("Local Offset", String.format(Locale.ROOT, "(x: %.1f, y: %.1f, w: %.1f, h: %.1f)",
                    component.x(), component.y(), component.width(), component.height())));
        }

        // Dynamic Cascade Metrics
        lines.add(property("Scale Factor", String.format(Locale.ROOT, "%.2f (Effective Cascade: %.2f)", component.scale(), component.effectiveScale())));
        lines.add(property("Opacity", String.format(Locale.ROOT, "%.2f (Effective Cascade: %.2f)", component.opacity(), component.effectiveOpacity())));

        if (component.borderRound() > 0.0f) {
            lines.add(property("Border Radius", String.format(Locale.ROOT, "%.1f px", component.borderRound())));
        }
        if (component.color() != null) {
            lines.add(property("Foreground Color", formatColor(component.color())));
        }
        if (component.backgroundColor() != null) {
            lines.add(property("Background Color", formatColor(component.backgroundColor())));
        }
        if (component.gradient() != null) {
            lines.add(property("Gradient", component.gradient().toString()));
        }

        // Concrete Live Component specific states
        switch (component) {
            case TextComponent text -> {
                lines.add(property("Text Content", "\"" + PLAIN.serialize(text.text()) + "\" (Font Size: " + String.format(Locale.ROOT, "%.1f", text.fontSize()) + ", Align: " + text.alignment().name() + ")"));
            }
            case ButtonComponent btn -> {
                lines.add(property("Button Label", "\"" + PLAIN.serialize(btn.label()) + "\""));
                if (btn.outline()) {
                    lines.add(property("Outline", formatColor(btn.outlineColor()) + " (" + btn.outlineThickness() + "px)"));
                }
            }
            case SliderComponent slider -> {
                lines.add(property("Slider Value", String.format(Locale.ROOT, "%.2f / %.2f (§a%.1f%%§f) [Min: %.1f, Step: %.2f]",
                        slider.value(), slider.maximum(), slider.normalizedProgress() * 100.0, slider.minimum(), slider.step())));
            }
            case CheckboxComponent checkbox -> {
                lines.add(property("Checkbox State", checkbox.checked() ? "§a[✓ TRUE / CHECKED]" : "§c[✗ FALSE / UNCHECKED]"));
            }
            case ShapeComponent shape -> {
                lines.add(property("Shape Details", shape.shapeType() + " (Rotation: " + String.format(Locale.ROOT, "%.1f°", shape.rotation()) + ")"));
            }
            case IconComponent icon -> {
                lines.add(property("Icon Item", icon.material().name() + " [Transform: " + icon.transform().name() + "]"));
            }
            default -> {}
        }

        // Live Animation Status
        boolean isDirectAnim = component.isAnimating();
        boolean isCascadeAnim = component.parent().map(Container::isCascadeAnimating).orElse(false);
        UiAnimation activeAnim = component.activeAnimation().orElse(null);
        lines.addAll(animationSection("Component", isDirectAnim, isCascadeAnim, activeAnim));

        lines.add(actionButtons("component", component.id(), component.isVisible(), commandPrefix));
        lines.add(animationButtons("component", component.id(), commandPrefix));
        lines.add(footer());
        return lines;
    }

    // --- Search Helpers ---

    private static Optional<Layer> findLayer(LayerManager manager, String id) {
        for (Layer l : manager.layers()) {
            if (l.id().equalsIgnoreCase(id)) return Optional.of(l);
        }
        return Optional.empty();
    }

    private static Optional<Container> findContainer(LayerManager manager, String id) {
        for (Layer l : manager.layers()) {
            for (Container c : l.containers()) {
                Optional<Container> found = findContainerRecursive(c, id);
                if (found.isPresent()) return found;
            }
        }
        return Optional.empty();
    }

    private static Optional<Container> findContainerRecursive(Container parent, String id) {
        if (parent.id().equalsIgnoreCase(id)) return Optional.of(parent);
        if (parent instanceof DropdownContainer dd && dd.toggleButtonId().equalsIgnoreCase(id)) return Optional.of(parent);
        for (Container child : parent.childContainers()) {
            Optional<Container> found = findContainerRecursive(child, id);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }

    private static Optional<vn.haohan.displayui.api.component.Component> findComponent(LayerManager manager, String id) {
        for (Layer l : manager.layers()) {
            for (Container c : l.containers()) {
                Optional<vn.haohan.displayui.api.component.Component> found = findComponentRecursive(c, id);
                if (found.isPresent()) return found;
            }
        }
        return Optional.empty();
    }

    private static Optional<vn.haohan.displayui.api.component.Component> findComponentRecursive(Container parent, String id) {
        for (vn.haohan.displayui.api.component.Component comp : parent.components()) {
            if (comp.id().equalsIgnoreCase(id)) return Optional.of(comp);
            if (comp.id().equalsIgnoreCase("__inspect_comp_" + id)) return Optional.of(comp);
        }
        for (Container child : parent.childContainers()) {
            Optional<vn.haohan.displayui.api.component.Component> found = findComponentRecursive(child, id);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }

    // --- Formatting Utilities ---

    private static Component header(String type, String id, NamedTextColor typeColor) {
        return Component.text("══════════ [INSPECT " + type + ": ", NamedTextColor.GOLD, TextDecoration.BOLD)
                .append(Component.text(id, typeColor, TextDecoration.BOLD))
                .append(Component.text("] ══════════", NamedTextColor.GOLD, TextDecoration.BOLD));
    }

    private static Component footer() {
        return Component.text("═══════════════════════════════════════════════════════════", NamedTextColor.GOLD);
    }

    private static Component property(String label, String value) {
        return Component.text(" • ", NamedTextColor.DARK_GRAY)
                .append(Component.text(label + ": ", NamedTextColor.YELLOW))
                .append(Component.text(value != null ? value : "None", NamedTextColor.WHITE));
    }

    private static Component visibilityProperty(String type, String id, boolean isVisible, UiDebugState state) {
        boolean isSolo = state != null && state.isSoloActive() && id.equalsIgnoreCase(state.getSoloId());
        Component visComp = Component.text(isVisible ? "[HIỆN / VISIBLE]" : "[ẨN / HIDDEN]", isVisible ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD);

        if (isSolo) {
            visComp = visComp.append(Component.text(" (SOLO ACTIVE)", NamedTextColor.AQUA, TextDecoration.BOLD));
        }

        return Component.text(" • ", NamedTextColor.DARK_GRAY)
                .append(Component.text("Visibility: ", NamedTextColor.YELLOW))
                .append(visComp);
    }

    private static List<Component> animationSection(String type, boolean isDirect, boolean isCascade, UiAnimation anim) {
        List<Component> lines = new ArrayList<>();
        if (isDirect && anim != null) {
            lines.add(Component.text(" • ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Animation: ", NamedTextColor.YELLOW))
                    .append(Component.text("[ACTIVE RUNNING]", NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" (" + anim.durationTicks() + " ticks, " + anim.easing().name() + ")", NamedTextColor.WHITE)));

            StringBuilder metrics = new StringBuilder();
            if (anim.offsetX() != 0 || anim.offsetY() != 0 || anim.offsetZ() != 0) {
                metrics.append(String.format(Locale.ROOT, "Δ(%.1f, %.1f, %.1f) ", anim.offsetX(), anim.offsetY(), anim.offsetZ()));
            }
            if (anim.fromScale() != 1.0f || anim.toScale() != 1.0f) {
                metrics.append(String.format(Locale.ROOT, "Scale(%.2f→%.2f) ", anim.fromScale(), anim.toScale()));
            }
            if (anim.fromOpacity() != 1.0f || anim.toOpacity() != 1.0f) {
                metrics.append(String.format(Locale.ROOT, "Opacity(%.2f→%.2f) ", anim.fromOpacity(), anim.toOpacity()));
            }
            if (anim.delayTicks() > 0) {
                metrics.append("Delay(").append(anim.delayTicks()).append("t) ");
            }
            if (!metrics.isEmpty()) {
                lines.add(Component.text("   ↳ ", NamedTextColor.DARK_GRAY)
                        .append(Component.text("Transforms: ", NamedTextColor.GRAY))
                        .append(Component.text(metrics.toString().trim(), NamedTextColor.AQUA)));
            }
        } else if (isCascade) {
            lines.add(Component.text(" • ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Animation: ", NamedTextColor.YELLOW))
                    .append(Component.text("[CASCADE INHERITED FROM PARENT]", NamedTextColor.GOLD, TextDecoration.BOLD)));
        } else {
            lines.add(Component.text(" • ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Animation: ", NamedTextColor.YELLOW))
                    .append(Component.text("[IDLE / NONE]", NamedTextColor.GRAY)));
        }
        return lines;
    }

    private static Component actionButtons(String type, String id, boolean isVisible, String commandPrefix) {
        String pfx = (commandPrefix != null && !commandPrefix.isBlank()) ? commandPrefix : DEFAULT_COMMAND_PREFIX;

        Component toggleBtn = Component.text("[TOGGLE " + (isVisible ? "HIDE" : "SHOW") + "]", isVisible ? NamedTextColor.RED : NamedTextColor.GREEN, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Bấm để " + (isVisible ? "ẨN" : "HIỆN") + " " + id, NamedTextColor.YELLOW)))
                .clickEvent(ClickEvent.runCommand(pfx + " " + type + " " + id + " toggle"));

        Component soloBtn = Component.text(" [SOLO THIS]", NamedTextColor.AQUA, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Bấm để chỉ hiển thị duy nhất " + id, NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand(pfx + " solo " + type + " " + id));

        Component treeBtn = Component.text(" [VIEW TREE]", NamedTextColor.GOLD, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Bấm để xem cây UI", NamedTextColor.GOLD)))
                .clickEvent(ClickEvent.runCommand(pfx + " tree"));

        return Component.text(" Actions: ", NamedTextColor.GRAY)
                .append(toggleBtn)
                .append(soloBtn)
                .append(treeBtn);
    }

    private static Component animationButtons(String type, String id, String commandPrefix) {
        String pfx = (commandPrefix != null && !commandPrefix.isBlank()) ? commandPrefix : DEFAULT_COMMAND_PREFIX;

        Component fadeBtn = Component.text("[⚡ FADE]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Chạy animation Fade-In trên " + id, NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand(pfx + " anim " + type + " " + id + " fadein"));

        Component slideBtn = Component.text(" [⚡ SLIDE]", NamedTextColor.AQUA, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Chạy animation Slide-In trên " + id, NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand(pfx + " anim " + type + " " + id + " slideleft"));

        Component popBtn = Component.text(" [⚡ POP]", NamedTextColor.YELLOW, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Chạy animation Pop-In trên " + id, NamedTextColor.YELLOW)))
                .clickEvent(ClickEvent.runCommand(pfx + " anim " + type + " " + id + " popin"));

        Component bounceBtn = Component.text(" [⚡ BOUNCE]", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Chạy animation Bounce-In trên " + id, NamedTextColor.LIGHT_PURPLE)))
                .clickEvent(ClickEvent.runCommand(pfx + " anim " + type + " " + id + " bouncein"));

        Component scaleBtn = Component.text(" [⚡ SCALE]", NamedTextColor.GOLD, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Chạy animation Scale-In trên " + id, NamedTextColor.GOLD)))
                .clickEvent(ClickEvent.runCommand(pfx + " anim " + type + " " + id + " scalein"));

        Component stopBtn = Component.text(" [⏹ STOP]", NamedTextColor.RED, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Dừng animation trên " + id, NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand(pfx + " anim " + type + " " + id + " stop"));

        return Component.text(" Anim Test: ", NamedTextColor.GOLD)
                .append(fadeBtn)
                .append(slideBtn)
                .append(popBtn)
                .append(bounceBtn)
                .append(scaleBtn)
                .append(stopBtn);
    }

    private static String formatColor(Color color) {
        if (color == null) return "None";
        return String.format(Locale.ROOT, "#%02X%02X%02X (Alpha: %d)", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static void sendMessages(CommandSender sender, List<Component> messages) {
        for (Component c : messages) {
            sender.sendMessage(c);
        }
    }
}
