/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package fixture.testviews2;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;

public class ViewB11 implements V7View {

    private final Label label = new Label("not used");

    @Override
    public void enter(V7ViewChangeEvent event) {
    }

    @Override
    public Component getRootComponent() {
        return label;
    }

    @Override
    public String viewName() {

        return getClass().getSimpleName();
    }

    @Override
    public void init() {
    }

}
