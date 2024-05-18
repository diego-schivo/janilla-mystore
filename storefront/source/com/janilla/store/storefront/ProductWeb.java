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

import java.math.BigDecimal;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.Renderer;
import com.janilla.persistence.Persistence;
import com.janilla.reflect.Flatten;
import com.janilla.store.backend.Cart;
import com.janilla.store.backend.LineItem;
import com.janilla.store.backend.MoneyAmount;
import com.janilla.store.backend.Product;
import com.janilla.store.backend.ProductOption;
import com.janilla.store.backend.ProductOptionValue;
import com.janilla.store.backend.ProductVariant;
import com.janilla.store.storefront.Nav.CartItem;
import com.janilla.web.Handle;
import com.janilla.web.Bind;
import com.janilla.web.Render;

public class ProductWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/products/([\\w-]+)")
	public Product2 get(String handle, Select select) {
		Product p;
		{
			var i = persistence.getCrud(Product.class).find("handle", handle);
			p = persistence.getCrud(Product.class).read(i);
		}
		var s = select != null && select.options != null
				? select.options.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
				: null;
		return Product2.of(p, s, persistence);
	}

	@Handle(method = "PUT", path = "/products/([\\w-]+)/actions")
	public @Render("Product-actions.html") Product2 updateActions(String handle, Select select) {
		return get(handle, select);
	}

	@Handle(method = "POST", path = "/products/([\\w-]+)")
	public CartItem addToCart(String handle, @Bind("variant") Long variant) {
		var v = persistence.getCrud(ProductVariant.class).read(variant);
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

		c = persistence.getCrud(Cart.class).update(c.id(),
				x -> x.withTotal(x.total().add(a.i.unitPrice())).withSubtotal(x.subtotal().add(a.i.unitPrice())));
		return CartItem.of(c, persistence);
	}

	public record Select(List<Map.Entry<Long, Long>> options) {
	}

	public record Option(@Flatten ProductOption option, List<@Render("Product-value.html") ProductOptionValue> values) {

		public static Option of(ProductOption option, Persistence persistence) {
			if (option == null)
				return null;
			List<ProductOptionValue> vv;
			{
				var ii = persistence.getCrud(ProductOptionValue.class).filter("option", option.id());
				vv = persistence.getCrud(ProductOptionValue.class).read(ii).toList();
			}
			return new Option(option, vv);
		}
	}

	@Render("Product.html")
	public record Product2(@Flatten Product product, List<@Render("Product-option.html") Option> options,
			Map<Long, Long> select, ProductVariant variant, BigDecimal price) implements Renderer {

		public static Product2 of(Product product, Map<Long, Long> select, Persistence persistence) {
			if (product == null)
				return null;
			List<Option> oo;
			{
				var ii = persistence.getCrud(ProductOption.class).filter("product", product.id());
				oo = persistence.getCrud(ProductOption.class).read(ii).map(x -> Option.of(x, persistence)).toList();
			}
			ProductVariant v;
			BigDecimal p;
			{
				var ii = persistence.getCrud(ProductVariant.class).filter("product", product.id());
				var vv = persistence.getCrud(ProductVariant.class).read(ii).toList();
				v = select != null ? vv.stream().filter(x -> {
					var jj = persistence.getCrud(ProductOptionValue.class).filter("variant", x.id());
					return persistence.getCrud(ProductOptionValue.class).read(jj)
							.allMatch(y -> y.id().equals(select.get(y.option())));
				}).findFirst().orElse(null) : null;
				if (v != null) {
					ii = persistence.getCrud(MoneyAmount.class).filter("variant", v.id());
					var pp = persistence.getCrud(MoneyAmount.class).read(ii).toList();
					p = pp.get(0).amount();
				} else {
					ii = persistence.getCrud(MoneyAmount.class).filter("variant",
							vv.stream().map(ProductVariant::id).toArray());
					p = persistence.getCrud(MoneyAmount.class).read(ii).map(MoneyAmount::amount)
							.min(Comparator.naturalOrder()).orElse(null);
				}
			}
			return new Product2(product, oo, select, v, p);
		}

		@Override
		public boolean evaluate(RenderEngine engine) {
			record A(URI[] images, int index) {
			}
			record B(Object actions) {
			}
			record C(ProductOptionValue value, Object checked) {
			}
			record D(Object price) {
			}
			record E(Object addToCart) {
			}
			return engine.match(A.class, (i, o) -> o.setTemplate("Product-image.html"))
					|| engine.match(B.class, (i, o) -> {
						o.setValue("");
						o.setTemplate("Product-actions.html");
					}) || engine.match(C.class, (i, o) -> {
						o.setValue(
								select != null && i.value.id().equals(select.get(i.value.option())) ? "checked" : null);
					}) || engine.match(D.class, (i, o) -> {
						o.setTemplate(variant != null ? "Product-price.html" : "Product-price-from.html");
					}) || engine.match(E.class, (i, o) -> {
						o.setValue("");
						o.setTemplate(variant != null ? "Product-addToCart.html" : "Product-addToCart-disabled.html");
					});
		}
	}
}
