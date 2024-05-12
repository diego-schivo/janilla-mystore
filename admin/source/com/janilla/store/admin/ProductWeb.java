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
package com.janilla.store.admin;

import java.net.URI;
import java.util.List;

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.Renderer;
import com.janilla.persistence.Persistence;
import com.janilla.reflect.Flatten;
import com.janilla.store.backend.Product;
import com.janilla.store.backend.ProductOption;
import com.janilla.store.backend.ProductOptionValue;
import com.janilla.store.backend.ProductVariant;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class ProductWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/admin/products/(\\d+)")
	public Product2 getProduct(Long id) {
		var p = persistence.getCrud(Product.class).read(id);
		return Product2.of(p, persistence);
	}

	public record Option(@Flatten ProductOption option,
			List<@Render("Product-optionValue.html") ProductOptionValue> values) {

		public static Option of(ProductOption option, Persistence persistence) {
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
			List<@Render("Product-variant.html") ProductVariant> variants) implements Renderer {

		public static Product2 of(Product product, Persistence persistence) {
			List<Option> oo;
			{
				var ii = persistence.getCrud(ProductOption.class).filter("product", product.id());
				oo = persistence.getCrud(ProductOption.class).read(ii).map(x -> Option.of(x, persistence)).toList();
			}
			List<ProductVariant> vv;
			{
				var ii = persistence.getCrud(ProductVariant.class).filter("product", product.id());
				vv = persistence.getCrud(ProductVariant.class).read(ii).toList();
			}
			return new Product2(product, oo, vv);
		}

		@Override
		public boolean evaluate(RenderEngine engine) {
			record A(URI thumbnail) {
			}
			record B(URI[] images, int index) {
			}
			return engine.match(A.class, (i, o) -> o.setTemplate("Product-image.html"))
					|| engine.match(B.class, (i, o) -> o.setTemplate("Product-image.html"));
		}
	}
}
