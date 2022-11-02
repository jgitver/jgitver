/**
 * Copyright (C) 2016 Matthieu Brouillard [http://oss.brouillard.fr/jgitver] (matthieu@brouillard.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.brouillard.oss.jgitver.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Supplier;

import fr.brouillard.oss.jgitver.LookupPolicy;
import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios.Scenario;
import fr.brouillard.oss.jgitver.Scenarios.ScenarioBuilder;
import org.eclipse.jgit.api.MergeCommand.FastForwardMode;
import org.junit.jupiter.api.Test;

public class DistanceCalculatorBackportMergeTest extends ScenarioTest {

    //  *   fd57532 - (HEAD -> master) E :: merge D into B
    //  |\  
    //  | * 175a35d - (backport) D :: merge C into B
    //  |/| 
    //  | * c34d37e - (tag: 2.0.0, feature) content C
    //  * | 1a8e3a9 - (tag: 1.0.0) content B
    //  |/  
    //  * 6b003e3 - content A
    private static final Supplier<Scenario> scenario = () -> new ScenarioBuilder()
            .commit("content", "A")
            .commit("content", "B")
            .tagLight("1.0.0")
            .branchOnAppId("feature", "A")
            .commit("content", "C")
            .tagLight("2.0.0")
            .branchOnAppId("backport", "B")
            .merge("C", "D", FastForwardMode.NO_FF)
            .master()
            .merge("D", "E", FastForwardMode.NO_FF)
            .getScenario();

    public DistanceCalculatorBackportMergeTest() {
        super(scenario, vc -> vc.setLookupPolicy(LookupPolicy.NEAREST).setUseDistance(true));
    }

    @Test
    public void testTagDistance() {
        assertDistanceIs("E", "C", 2);
    }

    @Test
    public void testVersion() {
        assertEquals("1.0.0-1", versionCalculator.getVersion());
    }
}
