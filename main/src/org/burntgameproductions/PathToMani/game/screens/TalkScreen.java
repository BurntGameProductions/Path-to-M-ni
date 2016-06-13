

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.math.Rectangle;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.common.ManiColor;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;
import org.burntgameproductions.PathToMani.menu.MenuLayout;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class TalkScreen implements ManiUiScreen {

  public static final float MAX_TALK_DIST = 1f;
  private final List<ManiUiControl> myControls;
  private final ManiUiControl mySellCtrl;
  public final ManiUiControl buyCtrl;
  private final ManiUiControl myShipsCtrl;
  private final ManiUiControl myHireCtrl;
  private final Rectangle myBg;
  public final ManiUiControl closeCtrl;
  private ManiShip myTarget;

  public TalkScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    mySellCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 0), true, gameOptions.getKeySellMenu());
    mySellCtrl.setDisplayName("Sell");
    myControls.add(mySellCtrl);

    buyCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyBuyMenu());
    buyCtrl.setDisplayName("Buy");
    myControls.add(buyCtrl);

    myShipsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyChangeShipMenu());
    myShipsCtrl.setDisplayName("Change Ship");
    myControls.add(myShipsCtrl);

    myHireCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, gameOptions.getKeyHireShipMenu());
    myHireCtrl.setDisplayName("Hire");
    myControls.add(myHireCtrl);

    closeCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);

    myBg = menuLayout.bg(-1, 0, 5);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (clickedOutside) {
      closeCtrl.maybeFlashPressed(cmp.getOptions().getKeyClose());
      return;
    }
    ManiGame g = cmp.getGame();
    ManiShip hero = g.getHero();
    ManiInputManager inputMan = cmp.getInputMan();
    if (closeCtrl.isJustOff() || isTargetFar(hero))
    {
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      return;
    }

    boolean station = myTarget.getHull().config.getType() == HullConfig.Type.STATION;
    myShipsCtrl.setEnabled(station);
    myHireCtrl.setEnabled(station);

    InventoryScreen is = g.getScreens().inventoryScreen;
    boolean sell = mySellCtrl.isJustOff();
    boolean buy = buyCtrl.isJustOff();
    boolean sellShips = myShipsCtrl.isJustOff();
    boolean hire = myHireCtrl.isJustOff();
    if (sell || buy || sellShips || hire) {
      is.setOperations(sell ? is.sellItems : buy ? is.buyItems : sellShips ? is.changeShip : is.hireShips);
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      inputMan.addScreen(cmp, is);
    }
  }

  public boolean isTargetFar(ManiShip hero) {
    if (hero == null || myTarget == null || myTarget.getLife() <= 0) return true;
    float dst = myTarget.getPosition().dst(hero.getPosition()) - hero.getHull().config.getApproxRadius() - myTarget.getHull().config.getApproxRadius();
    return MAX_TALK_DIST < dst;
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
    uiDrawer.draw(myBg, ManiColor.UI_BG);
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return true;
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return myBg.contains(ptr.x, ptr.y);
  }

  @Override
  public void onAdd(ManiApplication cmp) {
  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }

  public void setTarget(ManiShip target) {
    myTarget = target;
  }

  public ManiShip getTarget() {
    return myTarget;
  }
}