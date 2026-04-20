package me.alpha432.oyvey.features.modules.render;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.render.RenderUtil;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StorageESP extends Module {

    public StorageESP() {
        super("StorageESP", "Highlights storage containers like chests, shulkers, barrels, etc.", Category.RENDER, true, false, false);
    }

    private final List<BlockPos> storages = new ArrayList<>();

    @Override
    public void onEnable() {
        storages.clear();
    }

    @Override
    public void onDisable() {
        storages.clear();
    }

    // You should also subscribe to a tick or chunk load event to update the list efficiently.
    // For simplicity, we update every render (not optimal but works for starters).

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        storages.clear(); // Refresh every frame (you can optimize this later)

        // Scan nearby chunks (increase range if you want farther ESP)
        int range = 6; // chunks

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                int cx = (int) mc.player.getX() / 16 + x;
                int cz = (int) mc.player.getZ() / 16 + z;

                var chunk = mc.world.getChunk(cx, cz);
                if (chunk == null) continue;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (isStorage(be)) {
                        BlockPos pos = be.getPos();
                        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), 
                                          pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

                        Color color = getColor(be);
                        RenderUtil.drawBox(event.getMatrix(), box, color, 1.5f); // thickness
                    }
                }
            }
        }
    }

    private boolean isStorage(BlockEntity be) {
        return be instanceof ChestBlockEntity ||
               be instanceof ShulkerBoxBlockEntity ||
               be instanceof BarrelBlockEntity ||
               be instanceof EnderChestBlockEntity ||
               be instanceof FurnaceBlockEntity ||      // optional
               be instanceof DispenserBlockEntity ||
               be instanceof HopperBlockEntity;
    }

    private Color getColor(BlockEntity be) {
        if (be instanceof ShulkerBoxBlockEntity) {
            return new Color(255, 0, 255, 255);     // magenta
        } else if (be instanceof EnderChestBlockEntity) {
            return new Color(0, 255, 255, 255);     // cyan
        } else if (be instanceof BarrelBlockEntity) {
            return new Color(139, 69, 19, 255);     // brown
        } else {
            return new Color(255, 165, 0, 255);     // orange for normal chests/furnaces etc.
        }
    }
}
