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
package com.janilla.mystore.admin;

import java.util.List;

import com.janilla.persistence.Persistence;
import com.janilla.reflect.Flatten;
import com.janilla.mystore.backend.Product;
import com.janilla.mystore.backend.ProductVariant;
import com.janilla.mystore.backend.SalesChannel;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class ProductsWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/admin/products")
	public Products getProducts() {
		var ii = persistence.crud(Product.class).list();
		var pp = persistence.crud(Product.class).read(ii);
		return new Products(pp.map(x -> Product2.of(x, persistence)).toList());
	}

	@Render("Products.html")
	public record Products(List<Product2> items) {
	}

	@Render("Products-item.html")
	public record Product2(@Flatten Product product, SalesChannel salesChannel, List<ProductVariant> variants,
			Integer inventoryQuantity) {

		public static Product2 of(Product product, Persistence persistence) {
			var c = persistence.crud(SalesChannel.class).read(product.salesChannel());
			List<ProductVariant> vv;
			{
				var ii = persistence.crud(ProductVariant.class).filter("product", product.id());
				vv = persistence.crud(ProductVariant.class).read(ii).toList();
			}
			var q = vv.stream().mapToInt(ProductVariant::inventoryQuantity).sum();
			return new Product2(product, c, vv, q);
		}
	}
}
