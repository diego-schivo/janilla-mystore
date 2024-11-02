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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.janilla.http.HeaderField;
import com.janilla.http.HttpExchange;
import com.janilla.http.HttpHandler;
import com.janilla.http.HttpRequest;
import com.janilla.web.WebHandlerFactory;

public class CountryHandlerFactory implements WebHandlerFactory {

	private static Set<String> countries = Arrays.stream(Locale.getISOCountries()).map(String::toLowerCase)
			.collect(Collectors.toCollection(HashSet::new));

	public WebHandlerFactory mainFactory;

	@Override
	public HttpHandler createHandler(Object object, HttpExchange exchange) {
		if (object == null)
			return null;
		switch (object) {
		case HttpRequest x:
			var e = (CustomExchange) exchange;
			if (e.country != null)
				return null;
			String p, c; {
			p = x.getPath();
			var i = p != null ? p.lastIndexOf('/') : -1;
			var s = i >= 0 ? p.substring(i + 1) : p;
			if (s == null || s.contains("."))
				return null;
			c = p.startsWith("/") && (p.length() == 3 || p.indexOf('/', 1) == 3) ? p.substring(1, 3) : null;
		}
			if (c != null && countries.contains(c)) {
				e.country = c;
				x.setTarget(x.getTarget().length() > 3 ? x.getTarget().substring(3) : "/");
				return forward(x);
			}
			return redirect(p);
		default:
			return null;
		}
	}

	protected HttpHandler forward(HttpRequest request) {
		return ex -> {
			var h = mainFactory.createHandler(request, (HttpExchange) ex);
			return h != null && h.handle(ex);
		};
	}

	protected HttpHandler redirect(String p) {
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
			var rs = ((HttpExchange) ex).getResponse();
//			rs.setStatus(new Status(302, "Found"));
			rs.setStatus(302);
			rs.getHeaders().add(new HeaderField("cache-control", "no-cache"));
			rs.getHeaders().add(new HeaderField("location", l));
			return true;
		};
	}
}
