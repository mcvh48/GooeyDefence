/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.gooeyDefence.ui.towers;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.upgrading.BlockUpgradesComponent;
import org.terasology.gooeyDefence.upgrading.UpgradeInfo;
import org.terasology.gooeyDefence.upgrading.UpgradeList;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIUpgrader extends CoreWidget {
    private UIComponentFields componentFields = new UIComponentFields();
    private UIUpgradePaths upgradePaths = new UIUpgradePaths();


    private Binding<EntityRef> entity = new DefaultBinding<>(EntityRef.NULL);
    private UpgradeInfo currentUpgrade = null;
    private UpgradingSystem upgradingSystem;

    public UIUpgrader() {
        componentFields.bindFields(new ReadOnlyBinding<Map<String, String>>() {
            @Override
            public Map<String, String> get() {
                return isEnabled() ? upgradingSystem.getComponentValues(getTargetComponent()) : new HashMap<>();
            }
        });
        componentFields.bindShowUpgrade(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return currentUpgrade != null;
            }
        });
        componentFields.bindUpgrade(new ReadOnlyBinding<UpgradeInfo>() {
            @Override
            public UpgradeInfo get() {
                return currentUpgrade;
            }
        });
        upgradePaths.bindUpgradesComponent(new ReadOnlyBinding<BlockUpgradesComponent>() {
            @Override
            public BlockUpgradesComponent get() {
                return entity.get().getComponent(BlockUpgradesComponent.class);
            }
        });
        upgradePaths.subscribe(this::upgradePressed);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Vector2i canvasSize = canvas.size();

        Vector2i fieldsSize = canvas.calculateRestrictedSize(componentFields, canvasSize);
        canvas.drawWidget(componentFields, Rect2i.createFromMinAndSize(0, 0, canvasSize.x, fieldsSize.y));

        canvasSize.subY(fieldsSize.y);

        Vector2i pathsSize = canvas.calculateRestrictedSize(upgradePaths, canvasSize);
        canvas.drawWidget(upgradePaths, Rect2i.createFromMinAndSize(0, fieldsSize.y, canvasSize.x, pathsSize.y));
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Vector2i fieldsSize = canvas.calculateRestrictedSize(componentFields, sizeHint);
        Vector2i pathsSize = canvas.calculateRestrictedSize(upgradePaths, sizeHint);
        return new Vector2i(Math.max(fieldsSize.x, pathsSize.x), fieldsSize.y + pathsSize.x);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && upgradingSystem != null;
    }

    private void upgradePressed(UpgradeList upgrade) {
        List<UpgradeInfo> stages = upgrade.getStages();
        /* Stages can never be empty because button is disabled if it is */
        UpgradeInfo upgradeInfo = stages.get(0);

        if (currentUpgrade == upgradeInfo) {
            upgradingSystem.applyUpgrade(getTargetComponent(), upgradeInfo);
            stages.remove(0);
            currentUpgrade = stages.isEmpty() ? null : stages.get(0);
        } else {
            currentUpgrade = upgradeInfo;
        }
    }

    private Component getTargetComponent() {
        return isEnabled() ? upgradingSystem.getComponentToUpgrade(entity.get(), getUpgraderComponent()) : null;
    }

    private BlockUpgradesComponent getUpgraderComponent() {
        return isEnabled() ? entity.get().getComponent(BlockUpgradesComponent.class) : null;
    }

    public void setUpgradingSystem(UpgradingSystem upgradingSystem) {
        this.upgradingSystem = upgradingSystem;
    }

    public void bindEntity(Binding<EntityRef> entity) {
        this.entity = entity;
    }
}
