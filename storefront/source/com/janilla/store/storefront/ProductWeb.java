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

public class ProductWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/products/([\\w-]+)")
	public Product2 getProduct(String handle) {
		Product p;
		{
			var i = persistence.getCrud(Product.class).find("handle", handle);
			p = persistence.getCrud(Product.class).read(i);
		}
		ProductVariant v;
		{
			var i = persistence.getCrud(ProductVariant.class).find("product", p.id());
			v = persistence.getCrud(ProductVariant.class).read(i);
		}
		return Product2.of(p, v);
	}

	@Handle(method = "POST", path = "/products/([\\w-]+)")
	public void addToCart(String handle, Add add) {
		var v = persistence.getCrud(ProductVariant.class).read(add.variant);
		var c = persistence.getCrud(Cart.class).read(1);
		if (c == null)
			c = persistence.getCrud(Cart.class).create(Cart.of());
		class A {
			LineItem i;
		}
		var a = new A();
		{
			var ii = persistence.getCrud(LineItem.class).filter("cart", c.id());
			a.i = persistence.getCrud(LineItem.class).read(ii).filter(x -> x.variant() == v.id()).findFirst()
					.orElse(null);
		}
		if (a.i != null)
			a.i = persistence.getCrud(LineItem.class).update(a.i.id(), x -> x.withQuantity(x.quantity() + 1));
		else
			a.i = persistence.getCrud(LineItem.class).create(LineItem.of(v, persistence).withCart(c.id()));

		persistence.getCrud(Cart.class).update(c.id(), x -> x.withTotal(x.total().add(a.i.unitPrice())));
	}

	@Render("Product.html")
	public record Product2(@Flatten Product product, List<@Render("Product-image.html") String> images,
			ProductVariant variant) {

		public static Product2 of(Product product, ProductVariant variant) {
			return new Product2(product, product.images(), variant);
		}
	}

	public record Add(Long variant) {
	}
}
