/**
 * Created by James Coggan on 24/10/2015.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jamescoggan.teatimer.utils;

import rx.Observable;
import rx.subjects.PublishSubject;

public class DataLayer {

    private final static PublishSubject<Long> publisher = PublishSubject.create();

    public static void send(Long o) {
        publisher.onNext(o);
    }

    public static Observable<Long> toObserverable() {
        return publisher;
    }

    public static boolean hasObservers() {
        return publisher.hasObservers();
    }
}
