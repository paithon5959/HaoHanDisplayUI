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
package vn.haohan.displayui.runtime.debug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.debug.UiDebugInspector;
import vn.haohan.displayui.api.debug.UiDebugManager;
import vn.haohan.displayui.api.debug.UiDebugRegistry;
import vn.haohan.displayui.api.debug.UiDebugSession;
import vn.haohan.displayui.api.debug.UiDebugState;
import vn.haohan.displayui.api.layer.LayerManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Reusable command execution engine and auto-completion provider for Display UI debugging.
 */
public final class UiDebugCommandHandler {

    private final UiDebugManager debugManager;

    public UiDebugCommandHandler(UiDebugManager debugManager) {
        this.debugManager = Objects.requireNonNull(debugManager, "debugManager cannot be null");
    }

    public boolean execute(CommandSender sender, String[] args, String commandPrefix) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender, commandPrefix);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "tree" -> handleTree(sender, args, commandPrefix);
            case "inspect" -> handleInspect(sender, args, commandPrefix);
            case "clickinspect" -> handleClickInspect(sender, args);
            case "layer" -> handleLayer(sender, args, commandPrefix);
            case "container" -> handleContainer(sender, args, commandPrefix);
            case "component" -> handleComponent(sender, args, commandPrefix);
            case "anim", "animate" -> handleAnim(sender, args);
            case "solo" -> handleSolo(sender, args, commandPrefix);
            case "unsolo" -> handleUnsolo(sender, args, commandPrefix);
            case "showall" -> handleShowAll(sender, args, commandPrefix);
            case "hideall" -> handleHideAll(sender, args, commandPrefix);
            case "reset" -> handleReset(sender, args, commandPrefix);
            case "list" -> handleList(sender, args);
            default -> {
                sender.sendMessage(Component.text("Lệnh con không hợp lệ: " + sub + ". Gõ " + commandPrefix + " help để xem trợ giúp.", NamedTextColor.RED));
                yield true;
            }
        };
    }

    // --- Subcommand Handlers ---

    private boolean handleTree(CommandSender sender, String[] args, String commandPrefix) {
        Player target = resolveTargetPlayer(sender, args, 1);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        sendTree(sender, session, commandPrefix);
        return true;
    }

    private void sendTree(CommandSender sender, UiDebugSession session, String commandPrefix) {
        LayerManager lm = session.getCurrentLayerManager();
        List<UiDebugState.UiTreeNode> roots = session.getDebugState().buildTree(lm);

        sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("  DISPLAY UI TREE (" + session.name() + ")", NamedTextColor.GOLD, TextDecoration.BOLD));
        sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.DARK_GRAY));

        for (var layerNode : roots) {
            printTreeNode(sender, layerNode, "  ", commandPrefix);
        }
        sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.DARK_GRAY));
    }

    private void printTreeNode(CommandSender sender, UiDebugState.UiTreeNode node, String indent, String commandPrefix) {
        NamedTextColor typeColor = switch (node.type()) {
            case "LAYER" -> NamedTextColor.YELLOW;
            case "CONTAINER" -> NamedTextColor.AQUA;
            default -> NamedTextColor.GRAY;
        };

        String subcmd = switch (node.type()) {
            case "LAYER" -> "layer";
            case "CONTAINER" -> "container";
            default -> "component";
        };

        Component line = Component.text(indent)
                .append(Component.text("[" + node.type() + "] ", typeColor, TextDecoration.BOLD))
                .append(Component.text(node.id(), NamedTextColor.WHITE))
                .append(Component.text(" "));

        Component toggleBtn = Component.text(node.visible() ? "[ON]" : "[OFF]", node.visible() ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Bấm để " + (node.visible() ? "ẨN" : "HIỆN") + " " + node.id(), NamedTextColor.YELLOW)))
                .clickEvent(ClickEvent.runCommand(commandPrefix + " " + subcmd + " " + node.id() + " toggle"));

        Component soloBtn = Component.text(" [SOLO]", NamedTextColor.DARK_AQUA)
                .hoverEvent(HoverEvent.showText(Component.text("Bấm để chỉ hiển thị duy nhất " + node.id(), NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand(commandPrefix + " solo " + subcmd + " " + node.id()));

        Component inspectBtn = Component.text(" [INFO]", NamedTextColor.LIGHT_PURPLE)
                .hoverEvent(HoverEvent.showText(Component.text("Bấm để xem toàn bộ thuộc tính của " + node.id(), NamedTextColor.LIGHT_PURPLE)))
                .clickEvent(ClickEvent.runCommand(commandPrefix + " inspect " + subcmd + " " + node.id()));

        line = line.append(toggleBtn).append(soloBtn).append(inspectBtn);
        sender.sendMessage(line);

        for (var child : node.children()) {
            printTreeNode(sender, child, indent + "   ", commandPrefix);
        }
    }

    private boolean handleInspect(CommandSender sender, String[] args, String commandPrefix) {
        if (args.length == 1 || (args.length >= 2 && args[1].equalsIgnoreCase("click"))) {
            return handleClickInspect(sender, args);
        }

        String arg1 = args[1];
        String arg2 = (args.length >= 3 && !isPlayerName(args[2])) ? args[2] : null;
        int playerIdx = (arg2 != null) ? 3 : 2;
        Player target = resolveTargetPlayer(sender, args, playerIdx);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        LayerManager lm = session.getCurrentLayerManager();
        UiDebugInspector.inspectAndSend(sender, lm, arg1, arg2, session.getDebugState(), commandPrefix);
        return true;
    }

    private boolean handleClickInspect(CommandSender sender, String[] args) {
        int actionIdx = (args.length >= 2 && args[0].equalsIgnoreCase("inspect") && args[1].equalsIgnoreCase("click")) ? 2 : 1;
        String action = (args.length > actionIdx && !isPlayerName(args[actionIdx])) ? args[actionIdx].toLowerCase(Locale.ROOT) : "toggle";
        int playerIdx = (args.length > actionIdx && !isPlayerName(args[actionIdx])) ? actionIdx + 1 : actionIdx;

        Player target = resolveTargetPlayer(sender, args, playerIdx);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        boolean newState;
        if (action.equals("on") || action.equals("enable") || action.equals("true")) {
            newState = true;
        } else if (action.equals("off") || action.equals("disable") || action.equals("false")) {
            newState = false;
        } else {
            newState = !session.getDebugState().isClickInspectEnabled();
        }

        session.getDebugState().setClickInspectEnabled(newState);
        session.forceUpdate();

        if (newState) {
            sender.sendMessage(Component.text("[DisplayUI] Đã ", NamedTextColor.GOLD)
                    .append(Component.text("BẬT", NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" chế độ Bấm Để Inspect (Click-to-Inspect)! Bấm vào bất kỳ vị trí nào trên Hologram để xuất thông tin ra chat.", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(Component.text("[DisplayUI] Đã ", NamedTextColor.GOLD)
                    .append(Component.text("TẮT", NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Component.text(" chế độ Bấm Để Inspect (Click-to-Inspect). Giao diện trở về tương tác bình thường.", NamedTextColor.GRAY)));
        }
        return true;
    }

    private boolean handleLayer(CommandSender sender, String[] args, String commandPrefix) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Cú pháp: layer <layerId> [show|hide|toggle|status|anim] [preset/player] [duration/player] [player]", NamedTextColor.RED));
            return true;
        }

        String layerId = args[1];
        String action = (args.length >= 3) ? args[2].toLowerCase(Locale.ROOT) : "toggle";

        if (action.equals("anim") || action.equals("animate")) {
            String preset = (args.length >= 4 && !isPlayerName(args[3])) ? args[3] : "fadein";
            Integer duration = (args.length >= 5 && !isPlayerName(args[4])) ? tryParseInt(args[4]) : null;
            int playerIdx = (duration != null) ? 5 : ((args.length >= 4 && !isPlayerName(args[3])) ? 4 : 3);
            Player target = resolveTargetPlayer(sender, args, playerIdx);
            if (target == null) return true;

            UiDebugSession session = getSessionOrWarn(sender, target);
            if (session == null) return true;
            return triggerAnimation(sender, session, "layer", layerId, preset, duration);
        }

        Player target = resolveTargetPlayer(sender, args, 3);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        UiDebugState debugState = session.getDebugState();
        LayerManager lm = session.getCurrentLayerManager();

        boolean newState;
        switch (action) {
            case "show" -> {
                debugState.setLayerVisibility(layerId, true);
                newState = true;
            }
            case "hide" -> {
                debugState.setLayerVisibility(layerId, false);
                newState = false;
            }
            case "status" -> {
                boolean cur = debugState.getEffectiveLayerVisibility(layerId, lm);
                sender.sendMessage(Component.text("[DisplayUI] Layer '" + layerId + "' hiện đang: ", NamedTextColor.GOLD)
                        .append(Component.text(cur ? "[HIỆN / ON]" : "[ẨN / OFF]", cur ? NamedTextColor.GREEN : NamedTextColor.RED)));
                return true;
            }
            default -> {
                newState = debugState.toggleLayer(layerId, lm);
            }
        }

        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Layer '", NamedTextColor.GOLD)
                .append(Component.text(layerId, NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("' -> ", NamedTextColor.GOLD))
                .append(Component.text(newState ? "[HIỆN]" : "[ẨN]", newState ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD)));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleContainer(CommandSender sender, String[] args, String commandPrefix) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Cú pháp: container <containerId> [show|hide|toggle|status|anim] [preset/player] [duration/player] [player]", NamedTextColor.RED));
            return true;
        }

        String containerId = args[1];
        String action = (args.length >= 3) ? args[2].toLowerCase(Locale.ROOT) : "toggle";

        if (action.equals("anim") || action.equals("animate")) {
            String preset = (args.length >= 4 && !isPlayerName(args[3])) ? args[3] : "fadein";
            Integer duration = (args.length >= 5 && !isPlayerName(args[4])) ? tryParseInt(args[4]) : null;
            int playerIdx = (duration != null) ? 5 : ((args.length >= 4 && !isPlayerName(args[3])) ? 4 : 3);
            Player target = resolveTargetPlayer(sender, args, playerIdx);
            if (target == null) return true;

            UiDebugSession session = getSessionOrWarn(sender, target);
            if (session == null) return true;
            return triggerAnimation(sender, session, "container", containerId, preset, duration);
        }

        Player target = resolveTargetPlayer(sender, args, 3);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        UiDebugState debugState = session.getDebugState();
        LayerManager lm = session.getCurrentLayerManager();

        boolean newState;
        switch (action) {
            case "show" -> {
                debugState.setContainerVisibility(containerId, true);
                newState = true;
            }
            case "hide" -> {
                debugState.setContainerVisibility(containerId, false);
                newState = false;
            }
            case "status" -> {
                boolean cur = debugState.getEffectiveContainerVisibility(containerId, lm);
                sender.sendMessage(Component.text("[DisplayUI] Container '" + containerId + "' hiện đang: ", NamedTextColor.GOLD)
                        .append(Component.text(cur ? "[HIỆN / ON]" : "[ẨN / OFF]", cur ? NamedTextColor.GREEN : NamedTextColor.RED)));
                return true;
            }
            default -> {
                newState = debugState.toggleContainer(containerId, lm);
            }
        }

        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Container '", NamedTextColor.GOLD)
                .append(Component.text(containerId, NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text("' -> ", NamedTextColor.GOLD))
                .append(Component.text(newState ? "[HIỆN]" : "[ẨN]", newState ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD)));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleComponent(CommandSender sender, String[] args, String commandPrefix) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Cú pháp: component <componentId> [show|hide|toggle|status|anim] [preset/player] [duration/player] [player]", NamedTextColor.RED));
            return true;
        }

        String componentId = args[1];
        String action = (args.length >= 3) ? args[2].toLowerCase(Locale.ROOT) : "toggle";

        if (action.equals("anim") || action.equals("animate")) {
            String preset = (args.length >= 4 && !isPlayerName(args[3])) ? args[3] : "fadein";
            Integer duration = (args.length >= 5 && !isPlayerName(args[4])) ? tryParseInt(args[4]) : null;
            int playerIdx = (duration != null) ? 5 : ((args.length >= 4 && !isPlayerName(args[3])) ? 4 : 3);
            Player target = resolveTargetPlayer(sender, args, playerIdx);
            if (target == null) return true;

            UiDebugSession session = getSessionOrWarn(sender, target);
            if (session == null) return true;
            return triggerAnimation(sender, session, "component", componentId, preset, duration);
        }

        Player target = resolveTargetPlayer(sender, args, 3);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        UiDebugState debugState = session.getDebugState();
        LayerManager lm = session.getCurrentLayerManager();

        boolean newState;
        switch (action) {
            case "show" -> {
                debugState.setComponentVisibility(componentId, true);
                newState = true;
            }
            case "hide" -> {
                debugState.setComponentVisibility(componentId, false);
                newState = false;
            }
            case "status" -> {
                boolean cur = debugState.getEffectiveComponentVisibility(componentId, lm);
                sender.sendMessage(Component.text("[DisplayUI] Component '" + componentId + "' hiện đang: ", NamedTextColor.GOLD)
                        .append(Component.text(cur ? "[HIỆN / ON]" : "[ẨN / OFF]", cur ? NamedTextColor.GREEN : NamedTextColor.RED)));
                return true;
            }
            default -> {
                newState = debugState.toggleComponent(componentId, lm);
            }
        }

        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Component '", NamedTextColor.GOLD)
                .append(Component.text(componentId, NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text("' -> ", NamedTextColor.GOLD))
                .append(Component.text(newState ? "[HIỆN]" : "[ẨN]", newState ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD)));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleAnim(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Cú pháp: anim <layer|container|component|id> <id/preset> [preset/duration] [duration/player] [player]", NamedTextColor.RED));
            return true;
        }

        String first = args[1].toLowerCase(Locale.ROOT);
        boolean isExplicitType = first.equals("layer") || first.equals("container") || first.equals("component");

        String targetType = isExplicitType ? first : "auto";
        String targetId = isExplicitType ? (args.length >= 3 ? args[2] : null) : first;
        if (targetId == null) {
            sender.sendMessage(Component.text("Cần chỉ định ID của " + targetType + "!", NamedTextColor.RED));
            return true;
        }

        int presetIdx = isExplicitType ? 3 : 2;
        String preset = (args.length > presetIdx && !isPlayerName(args[presetIdx])) ? args[presetIdx] : "fadein";

        int durationIdx = presetIdx + 1;
        Integer duration = (args.length > durationIdx && !isPlayerName(args[durationIdx])) ? tryParseInt(args[durationIdx]) : null;

        int playerIdx = duration != null ? durationIdx + 1 : durationIdx;
        Player target = resolveTargetPlayer(sender, args, playerIdx);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        return triggerAnimation(sender, session, targetType, targetId, preset, duration);
    }

    private boolean triggerAnimation(CommandSender sender, UiDebugSession session, String targetType, String targetId, String preset, Integer duration) {
        LayerManager lm = session.getCurrentLayerManager();
        UiDebugState debugState = session.getDebugState();

        String resolvedType = targetType;
        if (resolvedType.equals("auto")) {
            if (UiDebugRegistry.getAvailableLayerIds(lm).contains(targetId)) {
                resolvedType = "layer";
            } else if (UiDebugRegistry.getAvailableContainerIds(lm).contains(targetId)) {
                resolvedType = "container";
            } else if (UiDebugRegistry.getAvailableComponentIds(lm).contains(targetId)) {
                resolvedType = "component";
            } else {
                sender.sendMessage(Component.text("Không tìm thấy Layer, Container, hoặc Component nào có ID '" + targetId + "'!", NamedTextColor.RED));
                return true;
            }
        }

        String p = preset.toLowerCase(Locale.ROOT);
        if (p.equals("stop") || p.equals("reset")) {
            switch (resolvedType) {
                case "layer" -> debugState.setLayerAnimation(targetId, null);
                case "container" -> debugState.setContainerAnimation(targetId, null);
                case "component" -> debugState.setComponentAnimation(targetId, null);
            }
            session.forceUpdate();
            sender.sendMessage(Component.text("[DisplayUI] Đã dừng animation trên " + resolvedType + " '", NamedTextColor.GOLD)
                    .append(Component.text(targetId, NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text("'.", NamedTextColor.GOLD)));
            return true;
        }

        vn.haohan.displayui.api.animation.UiAnimation anim = UiDebugRegistry.createAnimation(preset, duration);
        if (anim == null) {
            sender.sendMessage(Component.text("Preset animation không hợp lệ: '" + preset + "'. Chọn từ: " +
                    String.join(", ", UiDebugRegistry.KNOWN_ANIMATION_PRESETS), NamedTextColor.RED));
            return true;
        }

        switch (resolvedType) {
            case "layer" -> debugState.setLayerAnimation(targetId, anim);
            case "container" -> debugState.setContainerAnimation(targetId, anim);
            case "component" -> debugState.setComponentAnimation(targetId, anim);
        }

        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Đã kích hoạt animation '", NamedTextColor.GOLD)
                .append(Component.text(preset.toLowerCase(Locale.ROOT), NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text("' (" + anim.durationTicks() + " ticks, " + anim.easing().name() + ") trên " + resolvedType + " '", NamedTextColor.GOLD))
                .append(Component.text(targetId, NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("'.", NamedTextColor.GOLD)));
        return true;
    }

    private static Integer tryParseInt(String s) {
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean handleSolo(CommandSender sender, String[] args, String commandPrefix) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("Cú pháp: solo <layer|container|component> <id> [player]", NamedTextColor.RED));
            return true;
        }

        String typeStr = args[1].toLowerCase(Locale.ROOT);
        String id = args[2];
        Player target = resolveTargetPlayer(sender, args, 3);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        UiDebugState.SoloType soloType = switch (typeStr) {
            case "layer" -> UiDebugState.SoloType.LAYER;
            case "container" -> UiDebugState.SoloType.CONTAINER;
            case "component" -> UiDebugState.SoloType.COMPONENT;
            default -> UiDebugState.SoloType.NONE;
        };

        if (soloType == UiDebugState.SoloType.NONE) {
            sender.sendMessage(Component.text("Loại solo không hợp lệ (hỗ trợ: layer, container, component).", NamedTextColor.RED));
            return true;
        }

        session.getDebugState().setSolo(soloType, id);
        session.forceUpdate();

        sender.sendMessage(Component.text("[DisplayUI] Đã kích hoạt SOLO " + soloType.name() + " '", NamedTextColor.GOLD)
                .append(Component.text(id, NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("'. Tất cả phần tử khác đã bị ẩn!", NamedTextColor.GREEN)));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleUnsolo(CommandSender sender, String[] args, String commandPrefix) {
        Player target = resolveTargetPlayer(sender, args, 1);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        session.getDebugState().clearSolo();
        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Đã tắt chế độ SOLO. Khôi phục hiển thị.", NamedTextColor.GREEN));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleShowAll(CommandSender sender, String[] args, String commandPrefix) {
        Player target = resolveTargetPlayer(sender, args, 1);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        session.getDebugState().showAll(session.getCurrentLayerManager());
        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Đã bật hiển thị TẤT CẢ các phần tử UI.", NamedTextColor.GREEN));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleHideAll(CommandSender sender, String[] args, String commandPrefix) {
        Player target = resolveTargetPlayer(sender, args, 1);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        session.getDebugState().hideAll(session.getCurrentLayerManager());
        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Đã ẩn TẤT CẢ các phần tử UI.", NamedTextColor.YELLOW));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleReset(CommandSender sender, String[] args, String commandPrefix) {
        Player target = resolveTargetPlayer(sender, args, 1);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        session.getDebugState().reset();
        session.forceUpdate();
        sender.sendMessage(Component.text("[DisplayUI] Đã đặt lại toàn bộ cài đặt debug về mặc định.", NamedTextColor.GREEN));
        sendTree(sender, session, commandPrefix);
        return true;
    }

    private boolean handleList(CommandSender sender, String[] args) {
        String category = (args.length > 1 && !isPlayerName(args[1])) ? args[1].toLowerCase(Locale.ROOT) : "all";
        int playerIdx = (args.length > 1 && !isPlayerName(args[1])) ? 2 : 1;
        Player target = resolveTargetPlayer(sender, args, playerIdx);
        if (target == null) return true;

        UiDebugSession session = getSessionOrWarn(sender, target);
        if (session == null) return true;

        LayerManager lm = session.getCurrentLayerManager();
        sender.sendMessage(Component.text("══════════════ [DISPLAY UI ID REGISTRY] ══════════════", NamedTextColor.GOLD, TextDecoration.BOLD));

        if (category.equals("all") || category.equals("layers")) {
            Set<String> layers = UiDebugRegistry.getAvailableLayerIds(lm);
            sender.sendMessage(Component.text("Layers (" + layers.size() + "): ", NamedTextColor.YELLOW)
                    .append(Component.text(String.join(", ", layers), NamedTextColor.WHITE)));
        }
        if (category.equals("all") || category.equals("containers")) {
            Set<String> containers = UiDebugRegistry.getAvailableContainerIds(lm);
            sender.sendMessage(Component.text("Containers (" + containers.size() + "): ", NamedTextColor.AQUA)
                    .append(Component.text(String.join(", ", containers), NamedTextColor.WHITE)));
        }
        if (category.equals("all") || category.equals("components")) {
            Set<String> components = UiDebugRegistry.getAvailableComponentIds(lm);
            sender.sendMessage(Component.text("Components (" + components.size() + "): ", NamedTextColor.GRAY)
                    .append(Component.text(String.join(", ", components), NamedTextColor.WHITE)));
        }
        sender.sendMessage(Component.text("═══════════════════════════════════════════════════════", NamedTextColor.GOLD));
        return true;
    }

    private void sendHelp(CommandSender sender, String commandPrefix) {
        sender.sendMessage(Component.text("══════════ [DISPLAY UI DEBUG COMMANDS] ══════════", NamedTextColor.GOLD, TextDecoration.BOLD));
        sender.sendMessage(Component.text(commandPrefix + " tree [player] ", NamedTextColor.YELLOW).append(Component.text("- Xem cây phân cấp trực quan", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " inspect click [on|off|toggle] [player] ", NamedTextColor.YELLOW).append(Component.text("- Bật/tắt chế độ Click trên Hologram để Inspect", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " inspect <layer|container|component|id> [id] [player] ", NamedTextColor.YELLOW).append(Component.text("- Xem thuộc tính chi tiết & animation runtime", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " anim <layer|container|component|id> <preset> [duration] [player] ", NamedTextColor.YELLOW).append(Component.text("- Chạy animation test cho phần tử", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " layer <layerId> [show|hide|toggle|status|anim] [player] ", NamedTextColor.YELLOW).append(Component.text("- Bật/tắt/Animate Layer", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " container <containerId> [show|hide|toggle|status|anim] [player] ", NamedTextColor.YELLOW).append(Component.text("- Bật/tắt/Animate Container", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " component <componentId> [show|hide|toggle|status|anim] [player] ", NamedTextColor.YELLOW).append(Component.text("- Bật/tắt/Animate Component", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " solo <layer|container|component> <id> [player] ", NamedTextColor.YELLOW).append(Component.text("- Ẩn tất cả, chỉ hiện phần tử này", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " unsolo [player] ", NamedTextColor.YELLOW).append(Component.text("- Hủy chế độ solo", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " showall / hideall / reset [player] ", NamedTextColor.YELLOW).append(Component.text("- Điều khiển hiển thị hàng loạt", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(commandPrefix + " list [layers|containers|components] [player] ", NamedTextColor.YELLOW).append(Component.text("- Liệt kê ID có sẵn", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("═══════════════════════════════════════════════════════", NamedTextColor.GOLD));
    }

    // --- Tab Completion ---

    public List<String> suggest(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            String prefix = (args.length == 1) ? args[0].toLowerCase(Locale.ROOT) : "";
            List<String> subs = List.of(
                    "tree", "inspect", "clickinspect", "anim", "layer", "container", "component",
                    "solo", "unsolo", "showall", "hideall", "reset", "list", "help"
            );
            return subs.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        Player targetPlayer = (sender instanceof Player p) ? p : null;
        UiDebugSession session = (targetPlayer != null) ? debugManager.getSession(targetPlayer.getUniqueId()).orElse(null) : null;
        LayerManager lm = (session != null) ? session.getCurrentLayerManager() : null;

        if (sub.equals("clickinspect")) {
            if (args.length == 2) {
                return filterPrefix(List.of("on", "off", "toggle"), args[1]);
            }
            if (args.length == 3) {
                return suggestPlayers(args[2]);
            }
            return Collections.emptyList();
        }

        if (sub.equals("inspect")) {
            if (args.length == 2) {
                Set<String> all = new LinkedHashSet<>();
                all.add("click");
                all.add("layer");
                all.add("container");
                all.add("component");
                all.addAll(UiDebugRegistry.getAvailableLayerIds(lm));
                all.addAll(UiDebugRegistry.getAvailableContainerIds(lm));
                all.addAll(UiDebugRegistry.getAvailableComponentIds(lm));
                return filterPrefix(all, args[1]);
            }
            if (args.length == 3) {
                String type = args[1].toLowerCase(Locale.ROOT);
                if (type.equals("click")) {
                    return filterPrefix(List.of("on", "off", "toggle"), args[2]);
                }
                return switch (type) {
                    case "layer" -> filterPrefix(UiDebugRegistry.getAvailableLayerIds(lm), args[2]);
                    case "container" -> filterPrefix(UiDebugRegistry.getAvailableContainerIds(lm), args[2]);
                    case "component" -> filterPrefix(UiDebugRegistry.getAvailableComponentIds(lm), args[2]);
                    default -> suggestPlayers(args[2]);
                };
            }
            if (args.length == 4) {
                return suggestPlayers(args[3]);
            }
            return Collections.emptyList();
        } else if (sub.equals("anim") || sub.equals("animate")) {
            if (args.length == 2) {
                Set<String> choices = new LinkedHashSet<>();
                choices.add("layer");
                choices.add("container");
                choices.add("component");
                choices.addAll(UiDebugRegistry.getAvailableLayerIds(lm));
                choices.addAll(UiDebugRegistry.getAvailableContainerIds(lm));
                choices.addAll(UiDebugRegistry.getAvailableComponentIds(lm));
                return filterPrefix(choices, args[1]);
            }
            if (args.length == 3) {
                String type = args[1].toLowerCase(Locale.ROOT);
                return switch (type) {
                    case "layer" -> filterPrefix(UiDebugRegistry.getAvailableLayerIds(lm), args[2]);
                    case "container" -> filterPrefix(UiDebugRegistry.getAvailableContainerIds(lm), args[2]);
                    case "component" -> filterPrefix(UiDebugRegistry.getAvailableComponentIds(lm), args[2]);
                    default -> filterPrefix(UiDebugRegistry.KNOWN_ANIMATION_PRESETS, args[2]);
                };
            }
            if (args.length == 4) {
                String type = args[1].toLowerCase(Locale.ROOT);
                boolean isType = type.equals("layer") || type.equals("container") || type.equals("component");
                if (isType) {
                    return filterPrefix(UiDebugRegistry.KNOWN_ANIMATION_PRESETS, args[3]);
                } else {
                    List<String> combined = new ArrayList<>(UiDebugRegistry.COMMON_DURATIONS);
                    combined.addAll(suggestPlayers(""));
                    return filterPrefix(combined, args[3]);
                }
            }
            if (args.length == 5) {
                String type = args[1].toLowerCase(Locale.ROOT);
                boolean isType = type.equals("layer") || type.equals("container") || type.equals("component");
                if (isType) {
                    List<String> combined = new ArrayList<>(UiDebugRegistry.COMMON_DURATIONS);
                    combined.addAll(suggestPlayers(""));
                    return filterPrefix(combined, args[4]);
                } else {
                    return suggestPlayers(args[4]);
                }
            }
            if (args.length == 6) {
                return suggestPlayers(args[5]);
            }
            return Collections.emptyList();
        } else if (sub.equals("layer")) {
            if (args.length == 2) {
                return filterPrefix(UiDebugRegistry.getAvailableLayerIds(lm), args[1]);
            }
            if (args.length == 3) {
                return filterPrefix(UiDebugRegistry.KNOWN_ACTIONS, args[2]);
            }
            if (args.length == 4) {
                if (args[2].equalsIgnoreCase("anim") || args[2].equalsIgnoreCase("animate")) {
                    return filterPrefix(UiDebugRegistry.KNOWN_ANIMATION_PRESETS, args[3]);
                }
                return suggestPlayers(args[3]);
            }
            if (args.length == 5) {
                if (args[2].equalsIgnoreCase("anim") || args[2].equalsIgnoreCase("animate")) {
                    List<String> combined = new ArrayList<>(UiDebugRegistry.COMMON_DURATIONS);
                    combined.addAll(suggestPlayers(""));
                    return filterPrefix(combined, args[4]);
                }
            }
            if (args.length == 6) {
                return suggestPlayers(args[5]);
            }
        } else if (sub.equals("container")) {
            if (args.length == 2) {
                return filterPrefix(UiDebugRegistry.getAvailableContainerIds(lm), args[1]);
            }
            if (args.length == 3) {
                return filterPrefix(UiDebugRegistry.KNOWN_ACTIONS, args[2]);
            }
            if (args.length == 4) {
                if (args[2].equalsIgnoreCase("anim") || args[2].equalsIgnoreCase("animate")) {
                    return filterPrefix(UiDebugRegistry.KNOWN_ANIMATION_PRESETS, args[3]);
                }
                return suggestPlayers(args[3]);
            }
            if (args.length == 5) {
                if (args[2].equalsIgnoreCase("anim") || args[2].equalsIgnoreCase("animate")) {
                    List<String> combined = new ArrayList<>(UiDebugRegistry.COMMON_DURATIONS);
                    combined.addAll(suggestPlayers(""));
                    return filterPrefix(combined, args[4]);
                }
            }
            if (args.length == 6) {
                return suggestPlayers(args[5]);
            }
        } else if (sub.equals("component")) {
            if (args.length == 2) {
                return filterPrefix(UiDebugRegistry.getAvailableComponentIds(lm), args[1]);
            }
            if (args.length == 3) {
                return filterPrefix(UiDebugRegistry.KNOWN_ACTIONS, args[2]);
            }
            if (args.length == 4) {
                if (args[2].equalsIgnoreCase("anim") || args[2].equalsIgnoreCase("animate")) {
                    return filterPrefix(UiDebugRegistry.KNOWN_ANIMATION_PRESETS, args[3]);
                }
                return suggestPlayers(args[3]);
            }
            if (args.length == 5) {
                if (args[2].equalsIgnoreCase("anim") || args[2].equalsIgnoreCase("animate")) {
                    List<String> combined = new ArrayList<>(UiDebugRegistry.COMMON_DURATIONS);
                    combined.addAll(suggestPlayers(""));
                    return filterPrefix(combined, args[4]);
                }
            }
            if (args.length == 6) {
                return suggestPlayers(args[5]);
            }
        } else if (sub.equals("solo")) {
            if (args.length == 2) {
                return filterPrefix(UiDebugRegistry.KNOWN_SOLO_TYPES, args[1]);
            }
            if (args.length == 3) {
                String type = args[1].toLowerCase(Locale.ROOT);
                return switch (type) {
                    case "layer" -> filterPrefix(UiDebugRegistry.getAvailableLayerIds(lm), args[2]);
                    case "container" -> filterPrefix(UiDebugRegistry.getAvailableContainerIds(lm), args[2]);
                    case "component" -> filterPrefix(UiDebugRegistry.getAvailableComponentIds(lm), args[2]);
                    default -> Collections.emptyList();
                };
            }
            if (args.length == 4) {
                return suggestPlayers(args[3]);
            }
        } else if (sub.equals("list")) {
            if (args.length == 2) {
                return filterPrefix(List.of("all", "layers", "containers", "components"), args[1]);
            }
            if (args.length == 3) {
                return suggestPlayers(args[2]);
            }
        } else if (sub.equals("tree") || sub.equals("unsolo") || sub.equals("showall") || sub.equals("hideall") || sub.equals("reset")) {
            if (args.length == 2) {
                return suggestPlayers(args[1]);
            }
        }

        return Collections.emptyList();
    }

    private List<String> filterPrefix(Collection<String> items, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return items.stream()
                .filter(i -> i.toLowerCase(Locale.ROOT).startsWith(lower))
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> suggestPlayers(String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(lower))
                .sorted()
                .collect(Collectors.toList());
    }

    private boolean isPlayerName(String arg) {
        if (arg == null) return false;
        return Bukkit.getPlayerExact(arg) != null;
    }

    private Player resolveTargetPlayer(CommandSender sender, String[] args, int playerArgIndex) {
        if (args.length > playerArgIndex) {
            String name = args[playerArgIndex];
            Player target = Bukkit.getPlayerExact(name);
            if (target == null) {
                sender.sendMessage(Component.text("Không tìm thấy người chơi '" + name + "' online!", NamedTextColor.RED));
                return null;
            }
            return target;
        }
        if (sender instanceof Player p) {
            return p;
        }
        sender.sendMessage(Component.text("Lệnh từ console cần chỉ định tên người chơi mục tiêu!", NamedTextColor.RED));
        return null;
    }

    private UiDebugSession getSessionOrWarn(CommandSender sender, Player player) {
        Optional<UiDebugSession> opt = debugManager.getSession(player.getUniqueId());
        if (opt.isEmpty()) {
            sender.sendMessage(Component.text("Người chơi " + player.getName() + " hiện không có giao diện Display UI nào đang mở!", NamedTextColor.RED));
            return null;
        }
        return opt.get();
    }
}
