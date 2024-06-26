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

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.RenderParticipant;
import com.janilla.persistence.Persistence;
import com.janilla.web.Render;

@Render("Layout.html")
public record Layout(Nav nav, RenderEngine.Entry entry) implements RenderParticipant {

	public static Layout of(RenderEngine.Entry entry, Persistence persistence) {
		var n = Nav.of(persistence);
		return new Layout(n, entry);
	}

	@Override
	public boolean render(RenderEngine engine) {
		record A(Layout layout, Object content) {
		}
		return engine.match(A.class, (i, o) -> {
			o.setValue(entry.getValue());
			o.setType(entry.getType());
		});
	}
}
