package fr.redboxing.cosmeticsmod.screen;

import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.api.API;
import fr.redboxing.cosmeticsmod.api.responses.LoginResponse;
import fr.redboxing.cosmeticsmod.utils.Multithreading;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class CosmeticLoginScreen extends Screen {
    private final API api = CosmeticsMod.getApi();

    private static final Text ENTER_USERNAME_TEXT = new TranslatableText("login.enterUsername");
    private static final Text ENTER_PASSWORD_TEXT = new TranslatableText("login.enterPassword");

    private TextFieldWidget usernameField;
    private TextFieldWidget passwordField;
    private ButtonWidget loginButton;
    private ButtonWidget registerButton;

    private Text error = Text.of("");

    public CosmeticLoginScreen() {
        super(new TranslatableText("login.login"));
    }

    public void tick() {
        this.usernameField.tick();
        this.passwordField.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);

        this.usernameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 66, 200, 20, ENTER_USERNAME_TEXT);
        this.usernameField.setTextFieldFocused(true);
        this.usernameField.setChangedListener((serverName) -> {
            this.updateAddButton();
        });

        this.addSelectableChild(this.usernameField);

        this.passwordField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 106, 200, 20, ENTER_PASSWORD_TEXT);
        this.passwordField.setChangedListener((address) -> {
            this.updateAddButton();
        });

        this.addSelectableChild(this.passwordField);

        this.loginButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, new TranslatableText("login.login"), (button) -> {
            error = new LiteralText("");
            Multithreading.runAsync(() -> {
                try {
                    LoginResponse response = this.api.login(this.usernameField.getText(), this.passwordField.getText());
                    if(!response.success) {
                        error = new LiteralText((response.message) == null ? "Failed to login" : "Failed to login: " + response.message).formatted(Formatting.RED);
                    } else {
                        CosmeticsMod.setDisplayNextTick(new CosmeticMainScreen());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }));

        this.registerButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, new TranslatableText("login.register"), (button) -> {
            error = new LiteralText("");
            Multithreading.runAsync(() -> {
                try {
                    LoginResponse response = this.api.register(this.usernameField.getText(), this.passwordField.getText(), this.client.getSession().getProfile().getId());
                    if(!response.success) {
                        error = new LiteralText((response.message) == null ? "Failed to register" : "Failed to register: " + response.message).formatted(Formatting.RED);
                    } else {
                        CosmeticsMod.setDisplayNextTick(new CosmeticMainScreen());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }));

        this.updateAddButton();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.usernameField.getText();
        String string2 = this.passwordField.getText();
        this.init(client, width, height);
        this.usernameField.setText(string);
        this.passwordField.setText(string2);
    }

    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    private void updateAddButton() {
        this.loginButton.active = !this.usernameField.getText().isEmpty() && !this.passwordField.getText().isEmpty();
        this.registerButton.active = !this.usernameField.getText().isEmpty() && !this.passwordField.getText().isEmpty();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 16777215);
        drawCenteredText(matrices, this.textRenderer, this.error, this.width / 2, 25, 16777215);
        drawTextWithShadow(matrices, this.textRenderer, ENTER_USERNAME_TEXT, this.width / 2 - 100, 53, 10526880);
        drawTextWithShadow(matrices, this.textRenderer, ENTER_PASSWORD_TEXT, this.width / 2 - 100, 94, 10526880);
        this.usernameField.render(matrices, mouseX, mouseY, delta);
        this.passwordField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
