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
package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;

public abstract class ViewBaseI18N extends ViewBase {

	private I18NKey<?> nameKey;
	private final Translate translate;

	@Inject
	protected ViewBaseI18N(Translate translate) {
		super();
		this.translate = translate;
	}

	public I18NKey<?> getNameKey() {
		return nameKey;
	}

	public void setNameKey(I18NKey<?> nameKey) {
		this.nameKey = nameKey;
	}

	@Override
	public String viewName() {
		return translate.from(nameKey);
	}

}
