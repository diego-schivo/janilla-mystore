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
package com.janilla.store.backend;

import java.time.Instant;

import com.janilla.persistence.Index;
import com.janilla.persistence.Store;

@Store
public record ProductVariant(Long id, Instant createdAt, String title, @Index Long product, String sku, String ean,
		Integer inventoryQuantity) {

	public static ProductVariant of(Product product) {
		return new ProductVariant(null, Instant.now(), null, product.id(), null, null, 0);
	}

	public ProductVariant withTitle(String title) {
		return new ProductVariant(id, createdAt, title, product, sku, ean, inventoryQuantity);
	}

	public ProductVariant withInventoryQuantity(Integer inventoryQuantity) {
		return new ProductVariant(id, createdAt, title, product, sku, ean, inventoryQuantity);
	}
}
