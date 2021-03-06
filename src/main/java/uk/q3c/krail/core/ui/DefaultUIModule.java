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

package uk.q3c.krail.core.ui;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.server.WebBrowser;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.i18n.I18NKey;

public class DefaultUIModule extends AbstractModule {

    private I18NKey applicationTitleKey;
    private Class<? extends ScopedUI> uiClass;

    private MapBinder<String, Class<? extends ScopedUI>> uiMapClass;
    private MapBinder<String, ScopedUI> uiMapProvider;

    public DefaultUIModule() {
        uiClass = DefaultApplicationUI.class;
        applicationTitleKey = LabelKey.Krail;
    }

    @Override
    protected void configure() {
        TypeLiteral<String> annotationTypeLiteral = new TypeLiteral<String>() {
        };

        TypeLiteral<Class<? extends ScopedUI>> scopedUIClassLiteral = new TypeLiteral<Class<? extends ScopedUI>>() {
        };

        uiMapClass = MapBinder.newMapBinder(binder(), annotationTypeLiteral, scopedUIClassLiteral);
        uiMapProvider = MapBinder.newMapBinder(binder(), String.class, ScopedUI.class);

        bindApplicationTitle();
        bind(WebBrowser.class).toProvider(BrowserProvider.class);
        bindUIProvider();
        define();


    }

    /**
     * Override this method to bind your own UI class(es). If you will only be using a single UI class, it is easier to call {@link #uiClass(Class)}, which you
     * can do from your Binding Manager. If you wish to use more than one UI class, you will also need to provide a custom {@link
     * ScopedUIProvider}, and bind it by overriding {@link #bindUIProvider()}
     */
    protected void define() {
        addUIBinding(uiClass);
    }

    /**
     * There are two bindings created, because the Vaadin UIProvider requires a Class in response to a ClassSelectionEvent, followed by an instance of that class.
     * {@link #uiMapClass} binds the former, and {@link #uiMapProvider} binds Providers to enable lazy instantiation
     *
     * @param aClass the UI class to bind
     */
    protected void addUIBinding(Class<? extends ScopedUI> aClass) {
        uiMapClass.addBinding(aClass.getName())
                .toInstance(aClass);
        uiMapProvider.addBinding(aClass.getName()).to(uiClass);
    }

    private void bindApplicationTitle() {
        ApplicationTitle title = new ApplicationTitle(applicationTitleKey);
        bind(ApplicationTitle.class).toInstance(title);
    }


    /**
     * Override to bind your ScopedUIProvider implementation
     */
    protected void bindUIProvider() {
        bind(ScopedUIProvider.class);
    }

    /**
     * Sets a single UI class.  If you need multiple UI classes override {@link #define()} and refer to the javadoc for that method.  Typically this method is
     * called by:<br><br> new DefaultUIModule().uiClass(aClass)<br><br>
     *
     * @param uiClass
     *         the UI class to use for the whole application
     *
     * @return this, for fluency
     */
    public DefaultUIModule uiClass(Class<? extends ScopedUI> uiClass) {
        this.uiClass = uiClass;
        return this;
    }

    public DefaultUIModule applicationTitleKey(I18NKey applicationTitleKey) {
        this.applicationTitleKey = applicationTitleKey;
        return this;
    }

}
