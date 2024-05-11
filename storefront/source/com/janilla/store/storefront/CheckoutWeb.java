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

import java.util.Arrays;
import java.util.List;

import com.janilla.persistence.Persistence;
import com.janilla.store.backend.Cart;
import com.janilla.store.backend.LineItem;
import com.janilla.store.backend.Order;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class CheckoutWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/checkout")
	public @Render("Checkout.html") Object getCheckout() {
		return "";
	}

	@Handle(method = "POST", path = "/checkout")
	public @Render("Checkout-order.html") Order placeOrder() {
		var c = persistence.getCrud(Cart.class).delete(1);
		var o = persistence.getCrud(Order.class).create(Order.of());
		List<LineItem> ii;
		{
			var jj = persistence.getCrud(LineItem.class).filter("cart", c.id());
			ii = Arrays.stream(jj)
					.mapToObj(
							x -> persistence.getCrud(LineItem.class).update(x, y -> y.withCart(null).withOrder(o.id())))
					.toList();
		}
		return o;
	}
}
