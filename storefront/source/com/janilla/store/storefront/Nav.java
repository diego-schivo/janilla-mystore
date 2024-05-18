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
package com.janilla.store.storefront;

import com.janilla.persistence.Persistence;
import com.janilla.store.backend.Cart;
import com.janilla.web.Render;

@Render("Nav.html")
public record Nav(CartItem cartItem) {

	public static Nav of(Persistence persistence) {
		var c = persistence.getCrud(Cart.class).read(1);
		var d = CartItem.of(c, persistence);
		return new Nav(d);
	}

	@Render("Nav-CartItem.html")
	public record CartItem(CartDropdown dropdown) {

		public static CartItem of(Cart cart, Persistence persistence) {
			var d = CartDropdown.of(cart, persistence);
			return new CartItem(d);
		}
	}
}
