package net.createmod.catnip.api.client.gui;

import java.util.Collection;
import java.util.List;

import net.createmod.catnip.impl.client.mixin.ScreenAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * Non-container Create/Ponder screen base with window layout helpers ({@code guiLeft}/{@code guiTop})
 * and the classic {@code renderWindow*} pipeline adapted to MC 26.1 {@code extractRenderState}.
 */
public abstract class AbstractSimiScreen extends Screen implements CatnipScreenExtensions {
	protected int windowWidth, windowHeight;
	protected int windowXOffset, windowYOffset;
	protected int guiLeft, guiTop;

	protected AbstractSimiScreen(Component title) {
		super(title);
	}

	protected AbstractSimiScreen() {
		this(CommonComponents.EMPTY);
	}

	/**
	 * This method must be called before {@code super.init()}!
	 */
	protected void setWindowSize(int width, int height) {
		windowWidth = width;
		windowHeight = height;
	}

	/**
	 * This method must be called before {@code super.init()}!
	 */
	protected void setWindowOffset(int xOffset, int yOffset) {
		windowXOffset = xOffset;
		windowYOffset = yOffset;
	}

	@Override
	protected void init() {
		guiLeft = (this.width - windowWidth) / 2;
		guiTop = (this.height - windowHeight) / 2;
		guiLeft += windowXOffset;
		guiTop += windowYOffset;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean shouldCloseOnE() {
		return true;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		prepareFrame();
		renderWindowBackground(graphics, mouseX, mouseY, partialTicks);
		renderWindow(graphics, mouseX, mouseY, partialTicks);
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		renderWindowForeground(graphics, mouseX, mouseY, partialTicks);
		endFrame();
	}

	/** Draw dim/backdrop behind the window contents. */
	protected void renderWindowBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		// default: rely on Screen.extractBackground via extractRenderStateWithTooltipAndSubtitles
	}

	/** Draw the main window chrome / contents. */
	protected void renderWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
	}

	/** Draw overlays / tooltips after widgets. */
	protected void renderWindowForeground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
	}

	protected void prepareFrame() {
	}

	protected void endFrame() {
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		return super.keyPressed(event);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		return super.mouseClicked(event, doubleClick);
	}

	protected final List<Renderable> getRenderables() {
		return ((ScreenAccessor) this).catnip$getRenderables();
	}

	@SafeVarargs
	protected final <W extends GuiEventListener & Renderable & NarratableEntry> void addRenderableWidgets(W... widgets) {
		for (W widget : widgets) {
			addRenderableWidget(widget);
		}
	}

	protected final <W extends GuiEventListener & Renderable & NarratableEntry> void addRenderableWidgets(Collection<W> widgets) {
		for (W widget : widgets) {
			addRenderableWidget(widget);
		}
	}

	protected final void removeWidgets(GuiEventListener... widgets) {
		for (GuiEventListener widget : widgets) {
			removeWidget(widget);
		}
	}

	protected final void removeWidgets(Collection<? extends GuiEventListener> widgets) {
		for (GuiEventListener widget : widgets) {
			removeWidget(widget);
		}
	}
}
