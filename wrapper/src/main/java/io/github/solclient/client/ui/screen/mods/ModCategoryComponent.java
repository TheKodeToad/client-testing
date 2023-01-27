package io.github.solclient.client.ui.screen.mods;

import org.lwjgl.nanovg.*;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.Identifier;

public final class ModCategoryComponent extends ListComponent {

	private final ModCategory category;
	private final AnimatedFloatController expandProgress;
	private boolean expand;

	public ModCategoryComponent(ModCategory category, ModsScreenComponent screen) {
		this.category = category;

		expandProgress = new AnimatedFloatController((component, ignored) -> expand ? 0F : 1F, 200);

		Header header = new Header();
		add(header, Controller.none());

		for (Mod mod : category.getMods()) {
			add(new ModEntry(mod, screen, category == ModCategory.PINNED));
		}
	}

	@Override
	public void render(ComponentRenderInfo info) {
		NanoVG.nvgSave(nvg);
		NanoVG.nvgIntersectScissor(nvg, 0, 0, getBounds().getWidth(), getBounds().getHeight());
		super.render(info);
		NanoVG.nvgRestore(nvg);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		int height = ((subComponents.size() - 1) * (30 + getSpacing()));
		int firstHeight = subComponents.get(0).getBounds().getHeight();
		return Rectangle.ofDimensions(230, firstHeight + (int) (height * expandProgress.get(this)));
	}

	@Override
	public int getSpacing() {
		return 5;
	}

	private final class Header extends ColouredComponent {

		public Header() {
			super(theme.fgButton());
			add(new LabelComponent(category.toString()).scaled(0.8F), new AlignedBoundsController(Alignment.START,
					Alignment.CENTRE, (component, bounds) -> bounds.offset(8, 0)));
		}

		@Override
		public void render(ComponentRenderInfo info) {
			int arrowOffset = getBounds().getHeight() / 2 - 4;

			NanoVG.nvgSave(nvg);
			NanoVG.nvgTranslate(nvg, 4, arrowOffset + 4);
			NanoVG.nvgRotate(nvg, (float) (expandProgress.get(this) * Math.PI / 2));
			NanoVG.nvgTranslate(nvg, -4, -(arrowOffset + 4));

			NanoVG.nvgBeginPath(nvg);

			NVGPaint paint = MinecraftUtils.nvgMinecraftTexturePaint(nvg,
					new Identifier("sol_client", "textures/gui/collapsed.png"), 0, arrowOffset, 8, 8, 0);

			NanoVG.nvgRect(nvg, 0, arrowOffset, 8, 8);
			NanoVG.nvgFillPaint(nvg, paint);
			NanoVG.nvgFill(nvg);
			NanoVG.nvgRestore(nvg);

			super.render(info);
		}

		@Override
		public boolean mouseClicked(ComponentRenderInfo info, int button) {
			if (button != 0)
				return super.mouseClicked(info, button);

			MinecraftUtils.playClickSound(true);
			expand = !expand;
			return true;
		}

		@Override
		protected Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(230, 8);
		}

	}

}
