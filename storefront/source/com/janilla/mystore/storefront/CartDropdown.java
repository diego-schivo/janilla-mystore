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

import java.util.List;

import com.janilla.persistence.Persistence;
import com.janilla.reflect.Flatten;
import com.janilla.mystore.backend.Cart;
import com.janilla.mystore.backend.LineItem;
import com.janilla.mystore.backend.Product;
import com.janilla.mystore.backend.ProductVariant;
import com.janilla.web.Render;

@Render("CartDropdown.html")
public record CartDropdown(@Flatten Cart cart, List<LineItem2> items, Integer quantity) {

	public static CartDropdown of(Cart cart, Persistence persistence) {
		List<LineItem2> ii;
		{
			var jj = cart != null ? persistence.crud(LineItem.class).filter("cart", cart.id()) : null;
			ii = jj != null
					? persistence.crud(LineItem.class).read(jj).map(x -> LineItem2.of(x, persistence)).toList()
					: null;
		}
		var q = ii != null ? ii.stream().mapToInt(x -> x.item.quantity()).sum() : 0;
		return new CartDropdown(cart, ii, q);
	}

	@Render("CartDropdown-item.html")
	public record LineItem2(@Flatten LineItem item, ProductVariant variant, Product product) {

		public static LineItem2 of(LineItem item, Persistence persistence) {
			if (item == null)
				return null;
			var v = persistence.crud(ProductVariant.class).read(item.variant());
			var p = persistence.crud(Product.class).read(item.product());
			return new LineItem2(item, v, p);
		}
	}
}
