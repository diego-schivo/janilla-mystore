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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.RenderParticipant;
import com.janilla.persistence.Persistence;
import com.janilla.reflect.Flatten;
import com.janilla.mystore.backend.Cart;
import com.janilla.mystore.backend.LineItem;
import com.janilla.mystore.backend.Product;
import com.janilla.mystore.backend.ProductVariant;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class CartWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/cart")
	public Cart2 getCart() {
		var c = persistence.crud(Cart.class).read(1);
		return Cart2.of(c, persistence);
	}

	@Handle(method = "POST", path = "/cart")
	public Cart2 updateLineItem(Update update) {
		var c = persistence.crud(Cart.class).read(1);
		int z;
		List<LineItem> ii;
		{
			var jj = persistence.crud(LineItem.class).filter("cart", c.id());
			z = IntStream.range(0, jj.length).filter(x -> jj[x] == update.item).findFirst().getAsInt();
			ii = persistence.crud(LineItem.class).read(jj).collect(Collectors.toCollection(ArrayList::new));
		}
		var i = ii.get(z);
		if (update.quantity > 0) {
			var t = i.unitPrice().multiply(BigDecimal.valueOf(update.quantity));
			i = persistence.crud(LineItem.class).update(i.id(), x -> x.withQuantity(update.quantity).withTotal(t));
			ii.set(z, i);
		} else {
			persistence.crud(LineItem.class).delete(i.id());
			ii.remove(z);
		}
		var t = ii.stream().map(x -> x.total()).reduce(BigDecimal.valueOf(0, 2), BigDecimal::add);
		c = persistence.crud(Cart.class).update(c.id(), x -> x.withTotal(t).withSubtotal(t));
		return Cart2.of(c, persistence);
	}

	@Render("Cart.html")
	public record Cart2(@Flatten Cart cart, List<LineItem2> items) {

		public static Cart2 of(Cart cart, Persistence persistence) {
			if (cart == null)
				return null;
			List<LineItem2> ii;
			{
				var jj = persistence.crud(LineItem.class).filter("cart", cart.id());
				ii = persistence.crud(LineItem.class).read(jj).map(x -> LineItem2.of(x, persistence)).toList();
			}
			return new Cart2(cart, ii);
		}
	}

	@Render("Cart-item.html")
	public record LineItem2(@Flatten LineItem item, ProductVariant variant, Product product) implements RenderParticipant {

		private static List<Integer> quantityOptions = IntStream.rangeClosed(1, 10).boxed().toList();

		public List<@Render("Cart-quantityOption.html") Integer> quantityOptions() {
			return quantityOptions;
		}

		public static LineItem2 of(LineItem item, Persistence persistence) {
			if (item == null)
				return null;
			var v = persistence.crud(ProductVariant.class).read(item.variant());
			var p = persistence.crud(Product.class).read(item.product());
			return new LineItem2(item, v, p);
		}

		@Override
		public boolean render(RenderEngine engine) {
			record A(Integer quantity, Object selected) {
			}
			return engine.match(A.class, (i, o) -> {
				if (i.quantity.equals(item.quantity()))
					o.setValue("selected");
			});
		}
	}

	public record Update(long item, int quantity) {
	}
}
