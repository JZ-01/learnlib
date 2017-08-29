/* Copyright (C) 2017 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.learnlib.algorithms.dhc.mealy;

import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.commons.util.mappings.MutableMapping;
import net.automatalib.words.Word;

import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * Class that contains all data that represent the internal state of the {@link MealyDHC} learner.
 *
 * @param <I> The input alphabet type.
 * @param <O> The output alphabet type.
 * @author bainczyk
 */
class MealyDHCState<I, O> implements Serializable {

    private final LinkedHashSet<Word<I>> splitters;
    private final CompactMealy<I, O> hypothesis;
    private final MutableMapping<Integer, MealyDHC.QueueElement<I, O>> accessSequences;

    MealyDHCState(final LinkedHashSet<Word<I>> splitters,
                  final CompactMealy<I, O> hypothesis,
                  final MutableMapping<Integer, MealyDHC.QueueElement<I, O>> accessSequences) {
        this.splitters = splitters;
        this.hypothesis = hypothesis;
        this.accessSequences = accessSequences;
    }

    LinkedHashSet<Word<I>> getSplitters() {
        return splitters;
    }

    CompactMealy<I, O> getHypothesis() {
        return hypothesis;
    }

    MutableMapping<Integer, MealyDHC.QueueElement<I, O>> getAccessSequences() {
        return accessSequences;
    }
}