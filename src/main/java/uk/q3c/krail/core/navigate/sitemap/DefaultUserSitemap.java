/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LocaleChangeListener;
import uk.q3c.krail.i18n.Translate;

import javax.annotation.Nonnull;
import java.text.Collator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * The {@link MasterSitemap} provides the overall structure of the site, and is Singleton scoped. This class refines
 * that by presenting only those pages that the user is authorised to see, and is therefore {@link
 * VaadinSessionScoped}.
 * It also maintains locale-aware labels and sort order, so that the navigation components are presented to the user in
 * the language and sort order of their choice.
 *
 * The standard page nodes are sometimes not in the user sitemap (for example, the login node is not there after
 * login). Use the isxxxUri methods to test a uri for a match to a standard page
 *
 * @author David Sowerby
 * @date 17 May 2014
 */
@VaadinSessionScoped
public class DefaultUserSitemap extends DefaultSitemapBase<UserSitemapNode> implements UserSitemap,
        LocaleChangeListener {
    private static Logger log = LoggerFactory.getLogger(DefaultUserSitemap.class);

    private final Translate translate;

    private final List<UserSitemapChangeListener> changeListeners;

    private String loginUri;

    @Inject
    public DefaultUserSitemap(Translate translate, URIFragmentHandler uriHandler, CurrentLocale currentLocale) {
        super(uriHandler);
        this.translate = translate;
        changeListeners = new LinkedList<>();
        currentLocale.addListener(this);
    }

    public String getLoginUri() {
        return loginUri;
    }

    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }

    /**
     * Iterates through contained nodes and resets the label and collation key properties to reflect a change in
     * {@link CurrentLocale}. There is no need to reload all the nodes, no change of page authorisation is dealt with
     * here}
     */
    @Override
    public synchronized void localeChanged(Locale locale) {
        log.debug("responding to locale change to {}", locale);
        List<UserSitemapNode> nodeList = getAllNodes();
        Collator collator = translate.collator();
        for (UserSitemapNode userNode : nodeList) {
            String label = translate.from(userNode.getMasterNode()
                                                  .getLabelKey());
            userNode.setLabel(label);
            userNode.setCollationKey(collator.getCollationKey(userNode.getLabel()));
        }

        fireLabelsChanged();
    }

    private void fireLabelsChanged() {
        for (UserSitemapChangeListener listener : changeListeners) {
            listener.labelsChanged();
        }
    }

    /**
     * Returns the userNode which contains {@code masterNode}. Note that this method is not very efficient for larger
     * instances, it has to scan the {@link UserSitemap} until it finds a match. Returns null if no match found (and
     * will have scanned the entire {@link UserSitemap}
     *
     * @param masterNode
     *
     * @return
     */
    @Override
    public synchronized UserSitemapNode userNodeFor(SitemapNode masterNode) {
        for (UserSitemapNode candidate : getAllNodes()) {
            if (candidate.getMasterNode() == masterNode) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * The {@link UserSitemap} never creates a node this way
     */
    @Override
    public UserSitemapNode createNode(String segment) {
        return null;
    }

    /**
     * Does nothing in the {@link UserSitemap}
     */
    @Override
    protected void setId(UserSitemapNode node) {

    }

    @Override
    public synchronized void setLoaded(boolean loaded) {
        super.setLoaded(loaded);
        buildUriMap();
        if (loaded) {
            fireStructureChanged();
        }
    }

    @Override
    public synchronized void buildUriMap() {
        uriMap.clear();
        for (UserSitemapNode node : forest.getAllNodes()) {
            uriMap.put(uri(node), node);
        }

    }

    private void fireStructureChanged() {
        for (UserSitemapChangeListener listener : changeListeners) {
            listener.structureChanged();
        }

    }

    public void addChangeListener(UserSitemapChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(UserSitemapChangeListener listener) {
        changeListeners.remove(listener);
    }

    public Translate getTranslate() {
        return translate;
    }

    @Override
    public void addListener(UserSitemapChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeListener(UserSitemapChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * The standard page nodes are sometimes not in the user sitemap (for example, the login node is not there after
     * login). Use the isxxxUri methods to test a uri for a match to a standard page
     *
     * @param navigationState
     *         the navigation state to test
     *
     * @return true if the navigation state represents the login uri
     */
    @Override
    public boolean isLoginUri(@Nonnull NavigationState navigationState) {
        return isStandardUri(StandardPageKey.Log_In, navigationState);
    }

    private boolean isStandardUri(StandardPageKey key, NavigationState navigationState) {
        UserSitemapNode node = standardPageNode(key);
        if (node == null) {
            return false;
        } else {
            return uri(node).equals(navigationState.getVirtualPage());
        }
    }

    /**
     * The standard page nodes are sometimes not in the user sitemap (for example, the login node is not there after
     * login). Use the isxxxUri methods to test a uri for a match to a standard page
     *
     * @param navigationState
     *         the navigation state to test
     *
     * @return true if the navigation state represents the logout uri
     */
    @Override
    public boolean isLogoutUri(@Nonnull NavigationState navigationState) {
        return isStandardUri(StandardPageKey.Log_Out, navigationState);
    }

    /**
     * The standard page nodes are sometimes not in the user sitemap (for example, the login node is not there after
     * login). Use the isxxxUri methods to test a uri for a match to a standard page
     *
     * @param navigationState
     *         the navigation state to test
     *
     * @return true if the navigation state represents the private home uri
     */
    @Override
    public boolean isPrivateHomeUri(@Nonnull NavigationState navigationState) {
        return isStandardUri(StandardPageKey.Private_Home, navigationState);
    }

    /**
     * The standard page nodes are sometimes not in the user sitemap (for example, the login node is not there after
     * login). Use the isxxxUri methods to test a uri for a match to a standard page
     *
     * @param navigationState
     *         the navigation state to test
     *
     * @return true if the navigation state represents the public home uri
     */
    @Override
    public boolean isPublicHomeUri(@Nonnull NavigationState navigationState) {
        return isStandardUri(StandardPageKey.Public_Home, navigationState);
    }

}
