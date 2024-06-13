/*
 * MIT License
 *
 * Copyright (c) 2024 Diego Schivo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.janilla.mystore.storefront;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.janilla.http.FilterHttpRequest;
import com.janilla.http.HttpExchange;
import com.janilla.http.HttpHeader;
import com.janilla.http.HttpRequest;
import com.janilla.http.HttpResponse.Status;
import com.janilla.http.HttpServer;
import com.janilla.web.WebHandlerFactory;

public class CountryHandlerFactory implements WebHandlerFactory {

	private static Set<String> countries = Arrays.stream(Locale.getISOCountries()).map(String::toLowerCase)
			.collect(Collectors.toCollection(HashSet::new));

	public WebHandlerFactory mainFactory;

	@Override
	public HttpServer.Handler createHandler(Object object, HttpExchange exchange) {
		if (object == null)
			return null;
		switch (object) {
		case HttpRequest x:
			var e = (CustomExchange) exchange;
			if (e.country != null)
				return null;
			String p, c; {
			var u = x.getUri();
			p = u != null ? u.getPath() : null;
			var i = p != null ? p.lastIndexOf('/') : -1;
			var s = i >= 0 ? p.substring(i + 1) : p;
			if (s == null || s.contains("."))
				return null;
			c = p.startsWith("/") && (p.length() == 3 || p.indexOf('/', 1) == 3) ? p.substring(1, 3) : null;
		}
			if (c != null && countries.contains(c)) {
				e.country = c;
				var u = URI.create(p.length() > 3 ? p.substring(3) : "/");
				var q = new FilterHttpRequest(x) {

					@Override
					public URI getUri() {
						return u;
					}
				};
				return forward(q);
			}
			return redirect(p);
		default:
			return null;
		}
	}

	protected HttpServer.Handler forward(HttpRequest request) {
		return ex -> {
			var h = mainFactory.createHandler(request, ex);
			return h != null && h.handle(ex);
		};
	}

	protected HttpServer.Handler redirect(String p) {
		String l;
		{
			var b = new StringBuilder();
			b.append("/us");
			if (!p.isEmpty() && !p.equals("/")) {
				if (!p.startsWith("/"))
					b.append("/");
				b.append(p);
			}
			l = b.toString();
		}
		return ex -> {
			var rs = ex.getResponse();
			rs.setStatus(new Status(302, "Found"));
			rs.getHeaders().add(new HttpHeader("Cache-Control", "no-cache"));
			rs.getHeaders().add(new HttpHeader("Location", l));
			return true;
		};
	}
}
