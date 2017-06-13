/*
 * Copyright 2017 TheNightForum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tnf.ptm.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tnf.ptm.SolApplication;
import com.tnf.ptm.common.SolColor;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.ui.FontSize;
import com.tnf.ptm.ui.SolInputManager;
import com.tnf.ptm.ui.SolUiControl;
import com.tnf.ptm.ui.SolUiScreen;
import com.tnf.ptm.ui.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class LoadingScreen implements SolUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private boolean loadTutorial;
    private boolean usePreviousShip;

    LoadingScreen() {
        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        solApplication.startNewGame(loadTutorial, usePreviousShip);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Loading...", uiDrawer.r / 2, .5f, FontSize.MENU, true, SolColor.WHITE);
    }

    public void setMode(boolean loadTutorial, boolean usePreviousShip) {
        this.loadTutorial = loadTutorial;
        this.usePreviousShip = usePreviousShip;
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.WHITE);
    }
}