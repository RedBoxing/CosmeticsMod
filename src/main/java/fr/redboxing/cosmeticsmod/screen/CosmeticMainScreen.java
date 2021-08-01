package fr.redboxing.cosmeticsmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.api.API;
import fr.redboxing.cosmeticsmod.api.responses.LoginResponse;
import fr.redboxing.cosmeticsmod.utils.Multithreading;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import java.util.function.Consumer;

public class CosmeticMainScreen extends Screen {
    private final API api = CosmeticsMod.getApi();

    public CosmeticMainScreen() {
        super(new LiteralText("menu.cosmeticsmod.main.title"));
    }

    @Override
    protected void init() {
        checkLogin((loginResponse) -> {
            if(!loginResponse.success) {
                CosmeticsMod.setDisplayNextTick(new CosmeticLoginScreen());
            }
        });

        this.addDrawableChild(new ButtonWidget(this.width - 5 - 100, 5, 100, 20, new TranslatableText("cosmeticsmod.logout"), (button) -> {
            this.api.logout();
            CosmeticsMod.setDisplayNextTick(new CosmeticLoginScreen());
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        matrices.push();
        draw(delta, matrices);
        matrices.pop();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    private void draw(float delta, MatrixStack matrixStack) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Matrix4f matrix4f = matrixStack.peek().getModel();

        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        bufferBuilder.vertex(matrix4f, 100, -100, 0);
        bufferBuilder.vertex(matrix4f, -100, 100, 0);

        tessellator.draw();
    }

    private void checkLogin(Consumer<LoginResponse> callback) {
        Multithreading.runAsync(() -> {
            try {
                callback.accept(api.login());
            } catch (Exception ex) {
                ex.printStackTrace();
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.success = false;
                callback.accept(loginResponse);
            }
        });
    }

    private void refreshData() {
        Multithreading.runAsync(() -> {

        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
