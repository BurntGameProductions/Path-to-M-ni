

package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Input;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class OptionsScreen implements ManiUiScreen {
  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myBackCtrl;
  private final ManiUiControl myResoCtrl;
  private final ManiUiControl myVolCtrl;
  private final ManiUiControl myControlTypeCtrl;
  private final ManiUiControl inputMapCtrl;

  public OptionsScreen(MenuLayout menuLayout, GameOptions gameOptions) {

    myControls = new ArrayList<ManiUiControl>();

    myVolCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 0), true);
    myVolCtrl.setDisplayName("Vol");
    myControls.add(myVolCtrl);

    myResoCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myControlTypeCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.C);
    myControlTypeCtrl.setDisplayName("Control Type");
    myControls.add(myControlTypeCtrl);

    inputMapCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.M);
    inputMapCtrl.setDisplayName("Controls");
    myControls.add(inputMapCtrl);

    myBackCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myBackCtrl.setDisplayName("Back");
    myControls.add(myBackCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiInputManager im = cmp.getInputMan();
    MenuScreens screens = cmp.getMenuScreens();
    GameOptions options = cmp.getOptions();
    myVolCtrl.setDisplayName("Volume: " + getVolName(options));
    if (myVolCtrl.isJustOff()) {
      options.advanceVolMul();
    }
    if (myResoCtrl.isJustOff()) {
      im.setScreen(cmp, screens.resolutionScreen);
    }

    int ct = cmp.getOptions().controlType;
    String ctName = "Keyboard";
    if (ct == GameOptions.CONTROL_MIXED) ctName = "KB + Mouse";
    if (ct == GameOptions.CONTROL_MOUSE) ctName = "Mouse";
    if (ct == GameOptions.CONTROL_CONTROLLER) ctName = "Controller";
    myControlTypeCtrl.setDisplayName("Input: " + ctName);
    if (myControlTypeCtrl.isJustOff()) {
      cmp.getOptions().advanceControlType(false);
    }
    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, screens.main);
    }


    if (inputMapCtrl.isJustOff()) {
      if (ct == GameOptions.CONTROL_MIXED) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
      } else if (ct == GameOptions.CONTROL_KB) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
      } else if (ct == GameOptions.CONTROL_CONTROLLER) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
      }
      im.setScreen(cmp, screens.inputMapScreen);
    }
  }
  private String getVolName(GameOptions options) {
    float volMul = options.volMul;
    if (volMul == 0) return "Off";
    if (volMul < .4f) return "Low";
    if (volMul < .7f) return "High";
    return "Max";
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}