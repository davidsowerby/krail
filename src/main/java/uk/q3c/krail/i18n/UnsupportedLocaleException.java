/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.i18n;

import java.util.Locale;

/**
 * Created by David Sowerby on 26/10/14.
 */
public class UnsupportedLocaleException extends RuntimeException {
    private Locale locale;


    public UnsupportedLocaleException(Locale locale) {
        super("Locale " + locale.getDisplayName() + " is not supported");
        this.locale = locale;

    }

    public UnsupportedLocaleException(String msg) {

    }
}
