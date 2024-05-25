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
package com.janilla.mystore.backend;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;

import com.janilla.persistence.Index;
import com.janilla.persistence.Persistence;
import com.janilla.persistence.Store;

@Store
public record LineItem(Long id, Instant createdAt, @Index Long cart, @Index Long order, String title, URI thumbnail,
		BigDecimal unitPrice, Long variant, Long product, Integer quantity, BigDecimal total) {

	public static LineItem of(ProductVariant variant, Persistence persistence) {
		var p = persistence.crud(Product.class).read(variant.product());
		MoneyAmount a;
		{
			var i = persistence.crud(MoneyAmount.class).find("variant", variant.id());
			a = persistence.crud(MoneyAmount.class).read(i);
		}
		return new LineItem(null, Instant.now(), null, null, p.title(), p.thumbnail(), a.amount(), variant.id(), p.id(),
				1, a.amount());
	}

	public LineItem withCart(Long cart) {
		return new LineItem(id, createdAt, cart, order, title, thumbnail, unitPrice, variant, product, quantity, total);
	}

	public LineItem withOrder(Long order) {
		return new LineItem(id, createdAt, cart, order, title, thumbnail, unitPrice, variant, product, quantity, total);
	}

	public LineItem withQuantity(Integer quantity) {
		return new LineItem(id, createdAt, cart, order, title, thumbnail, unitPrice, variant, product, quantity, total);
	}

	public LineItem withTotal(BigDecimal total) {
		return new LineItem(id, createdAt, cart, order, title, thumbnail, unitPrice, variant, product, quantity, total);
	}
}
