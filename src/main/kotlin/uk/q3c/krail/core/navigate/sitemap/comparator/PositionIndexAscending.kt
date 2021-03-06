/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap.comparator

import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.i18n.I18NKey

/**
 * Comparator which can be used to sort SitemapNode by insertion order, based on [MasterSitemapNode.getId]
 */
class PositionIndexAscending : UserSitemapNodeComparator {
    /**
     * @see java.util.Comparator.compare
     */
    override fun compare(o1: UserSitemapNode, o2: UserSitemapNode): Int {
        return o1.positionIndex - o2.positionIndex
    }

    override fun nameKey(): I18NKey {
        return LabelKey.Position_Index_Ascending
    }

}
