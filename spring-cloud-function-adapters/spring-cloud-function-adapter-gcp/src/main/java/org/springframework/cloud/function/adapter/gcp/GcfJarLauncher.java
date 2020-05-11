/*
 * Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.function.adapter.gcp;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.jar.JarFile;

/**
 * The launcher class written at the top-level of the output JAR to be deployed to
 * Google Cloud Functions. This is the entry point to the function when run from JAR.
 *
 * @author Daniel Zou
 */
public class GcfJarLauncher extends JarLauncher implements HttpFunction {

	private final ClassLoader loader;

	private final HttpFunction delegate;

	public GcfJarLauncher() throws Exception {
		JarFile.registerUrlProtocolHandler();

		this.loader = createClassLoader(getClassPathArchivesIterator());

		Class<?> clazz = this.loader
			.loadClass("org.springframework.cloud.function.adapter.gcp.FunctionInvoker");
		this.delegate = (HttpFunction) clazz.getConstructor().newInstance();
	}
	@Override
	public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
		Thread.currentThread().setContextClassLoader(this.loader);
		delegate.service(httpRequest, httpResponse);
	}
}

