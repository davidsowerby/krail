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

package uk.q3c.krail.core.shiro.aop;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationHandler;
import org.apache.shiro.subject.Subject;
import uk.q3c.krail.core.shiro.SubjectProvider;

/**
 * An AOP MethodInterceptor to detect whether a {@link Subject} is authenticated or not.
 * <p>
 * Detection logic is a copy of the native Shiro version in {@link AuthenticatedAnnotationHandler}
 * <p>
 * Created by David Sowerby on 10/06/15.
 */
public class AuthenticatedMethodInterceptor extends ShiroMethodInterceptor<RequiresAuthentication> {

    @Inject
    public AuthenticatedMethodInterceptor(Provider<SubjectProvider> subjectProviderProvider, Provider<AnnotationResolver> annotationResolverProvider) {
        super(RequiresAuthentication.class, subjectProviderProvider, annotationResolverProvider);
    }


    /**
     * Ensures that the calling <code>Subject</code> is authenticated, and if not, calls {@link #exception()} indicating the method is not allowed to be
     * executed.
     *
     * @param a
     *         the annotation to inspect
     */
    public void assertAuthorized(RequiresAuthentication a) {
        if (!getSubject().isAuthenticated()) {
            throw new AuthenticationException();
        }
    }
}
