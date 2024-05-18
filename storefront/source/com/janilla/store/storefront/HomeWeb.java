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
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;

import com.janilla.persistence.Persistence;
import com.janilla.reflect.Flatten;
import com.janilla.store.backend.MoneyAmount;
import com.janilla.store.backend.Product;
import com.janilla.store.backend.ProductVariant;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class HomeWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/")
	public @Render("Home.html") List<Product2> getProducts() {
		var ii = persistence.getCrud(Product.class).list();
		var pp = persistence.getCrud(Product.class).read(ii).map(x -> Product2.of(x, persistence)).toList();
		return pp;
	}

	@Render("Home-product.html")
	public record Product2(@Flatten Product product, BigDecimal price) {

		public static Product2 of(Product product, Persistence persistence) {
			if (product == null)
				return null;
			BigDecimal p;
			{
				var ii = persistence.getCrud(ProductVariant.class).filter("product", product.id());
				var jj = persistence.getCrud(MoneyAmount.class).filter("variant", LongStream.of(ii).boxed().toArray());
				p = persistence.getCrud(MoneyAmount.class).read(jj).map(MoneyAmount::amount)
						.min(Comparator.naturalOrder()).orElse(null);
			}
			return new Product2(product, p);
		}
	}
}
