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

import java.util.List;

import com.janilla.persistence.Persistence;
import com.janilla.reflect.Flatten;
import com.janilla.store.backend.Cart;
import com.janilla.store.backend.LineItem;
import com.janilla.store.backend.Product;
import com.janilla.store.backend.ProductVariant;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class CartWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/cart")
	public Cart2 getCart() {
		var c = persistence.getCrud(Cart.class).read(1);
		return Cart2.of(c, persistence);
	}

	@Handle(method = "POST", path = "/cart")
	public Cart2 removeItem(Remove remove) {
		persistence.getCrud(LineItem.class).delete(remove.item);
		return getCart();
	}

	@Render("Cart.html")
	public record Cart2(@Flatten Cart cart, List<LineItem2> items) {

		public static Cart2 of(Cart cart, Persistence persistence) {
			List<LineItem2> ii;
			{
				var jj = persistence.getCrud(LineItem.class).filter("cart", cart.id());
				ii = persistence.getCrud(LineItem.class).read(jj).map(x -> LineItem2.of(x, persistence)).toList();
			}
			return new Cart2(cart, ii);
		}
	}

	@Render("Cart-item.html")
	public record LineItem2(@Flatten LineItem item, ProductVariant variant, Product product) {

		public static LineItem2 of(LineItem item, Persistence persistence) {
			var v = persistence.getCrud(ProductVariant.class).read(item.variant());
			var p = persistence.getCrud(Product.class).read(item.product());
			return new LineItem2(item, v, p);
		}
	}

	public record Remove(Long item) {
	}
}
