/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.gooeyDefence.towerBlocks.targeters;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;

/**
 * Targets the enemy that is closest to the goal.
 *
 * @see SingleTargeterSystem
 */
public class SingleTargeterComponent extends TowerTargeter {
    private EntityRef lastTarget;

    @Override
    public float getMultiplier() {
        return 1;
    }

    public EntityRef getLastTarget() {
        return lastTarget;
    }

    public void setLastTarget(EntityRef lastTarget) {
        this.lastTarget = lastTarget;
    }
}
