/* Copyright (C) 2013-2020 TU Dortmund
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
package de.learnlib.util;

import java.util.Collection;
import java.util.Objects;

import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.oracle.OmegaQueryAnswerer;
import de.learnlib.api.oracle.QueryAnswerer;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.api.query.OmegaQuery;
import de.learnlib.api.query.Query;
import de.learnlib.setting.LearnLibProperty;
import de.learnlib.setting.LearnLibSettings;
import net.automatalib.automata.concepts.SuffixOutput;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Word;

public final class MQUtil {

    public static final int PARALLEL_THRESHOLD;

    static {
        LearnLibSettings settings = LearnLibSettings.getInstance();
        PARALLEL_THRESHOLD = settings.getInt(LearnLibProperty.PARALLEL_QUERIES_THRESHOLD, -1);
    }

    private MQUtil() {
        // prevent instantiation
    }

    public static <I, D> DefaultQuery<I, D> normalize(MembershipOracle<I, D> oracle, DefaultQuery<I, D> query) {
        if (query.isNormalized()) {
            return query;
        }
        return query(oracle, Word.epsilon(), query.getInput());
    }

    public static <I, D> DefaultQuery<I, D> query(MembershipOracle<I, D> oracle, Word<I> prefix, Word<I> suffix) {
        DefaultQuery<I, D> qry = new DefaultQuery<>(prefix, suffix);
        oracle.processQuery(qry);
        return qry;
    }

    public static <I, D> DefaultQuery<I, D> query(MembershipOracle<I, D> oracle, Word<I> queryWord) {
        return query(oracle, Word.epsilon(), queryWord);
    }

    public static <I, D> void answerQueriesAuto(QueryAnswerer<I, D> answerer,
                                                Collection<? extends Query<I, D>> queries) {
        if (PARALLEL_THRESHOLD < 0 || queries.size() < PARALLEL_THRESHOLD) {
            answerQueries(answerer, queries);
        } else {
            answerQueriesParallel(answerer, queries);
        }
    }

    public static <S, I, D> void answerOmegaQueriesAuto(OmegaQueryAnswerer<S, I, D> answerer,
                                                        Collection<? extends OmegaQuery<I, D>> queries) {
        if (PARALLEL_THRESHOLD < 0 || queries.size() < PARALLEL_THRESHOLD) {
            answerOmegaQueries(answerer, queries);
        } else {
            answerOmegaQueriesParallel(answerer, queries);
        }
    }

    public static <I, D> void answerQueries(QueryAnswerer<I, D> answerer, Collection<? extends Query<I, D>> queries) {
        for (Query<I, D> query : queries) {
            Word<I> prefix = query.getPrefix();
            Word<I> suffix = query.getSuffix();
            D answer = answerer.answerQuery(prefix, suffix);
            query.answer(answer);
        }
    }

    public static <S, I, D> void answerOmegaQueries(OmegaQueryAnswerer<S, I, D> answerer,
                                                    Collection<? extends OmegaQuery<I, D>> queries) {
        for (OmegaQuery<I, D> query : queries) {
            final Word<I> prefix = query.getPrefix();
            final Word<I> loop = query.getLoop();
            final int repeat = query.getRepeat();
            Pair<D, Integer> answer = answerer.answerQuery(prefix, loop, repeat);
            query.answer(answer.getFirst(), answer.getSecond());
        }
    }

    public static <I, D> void answerQueriesParallel(QueryAnswerer<I, D> answerer,
                                                    Collection<? extends Query<I, D>> queries) {
        queries.parallelStream().forEach(q -> {
            Word<I> prefix = q.getPrefix();
            Word<I> suffix = q.getSuffix();
            D answer = answerer.answerQuery(prefix, suffix);
            q.answer(answer);
        });
    }

    public static <S, I, D> void answerOmegaQueriesParallel(OmegaQueryAnswerer<S, I, D> answerer,
                                                            Collection<? extends OmegaQuery<I, D>> queries) {
        queries.parallelStream().forEach(q -> {
            final Word<I> prefix = q.getPrefix();
            final Word<I> loop = q.getLoop();
            final int repeat = q.getRepeat();
            Pair<D, Integer> answer = answerer.answerQuery(prefix, loop, repeat);
            q.answer(answer.getFirst(), answer.getSecond());
        });
    }

    public static <I, D> boolean isCounterexample(DefaultQuery<I, D> query, SuffixOutput<I, D> hyp) {
        D qryOut = query.getOutput();
        D hypOut = hyp.computeSuffixOutput(query.getPrefix(), query.getSuffix());
        return !Objects.equals(qryOut, hypOut);
    }
}
