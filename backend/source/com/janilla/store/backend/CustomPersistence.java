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

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.janilla.persistence.Persistence;
import com.janilla.util.Randomize;
import com.janilla.util.Util;

public class CustomPersistence extends Persistence {

	static Pattern nonWord = Pattern.compile("[^\\p{L}]+", Pattern.UNICODE_CHARACTER_CLASS);

	static Set<String> safeWords = nonWord.splitAsStream(
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
					+ " Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
					+ " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
					+ " Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
			.map(String::toLowerCase).sorted().collect(Collectors.toCollection(LinkedHashSet::new));

	static List<String> w = new ArrayList<>(safeWords);

	static List<String> images = """
			black_hoodie_front
			shorts-vintage-front
			sweatpants-gray-front
			sweatshirt-vintage-front
			coffee-mug
			ls-black-front
			tee-black-front""".lines().map(x -> "/images/" + x + ".webp").toList();

	public void seed() {
		var r = ThreadLocalRandom.current();

//		var c = getCrud(Currency.class).create(new Currency(null, Instant.now(), "USD", "$", "US Dollar"));
//		var s = getCrud(Region.class).create(new Region(null, Instant.now(), "NA", c.id()));
//		var d = getCrud(Country.class).create(new Country(null, Instant.now(), "United States", s.id()));
		var sc = getCrud(SalesChannel.class).create(SalesChannel.of("Default Sales Channel"));

		for (var i = r.nextInt(5, 11); i > 0; i--) {
			var t = Util.capitalizeFirstChar(Randomize.phrase(2, 2, () -> Randomize.element(w)));
			var s = Randomize.sentence(20, 30, () -> Randomize.element(w));
			var h = t.toLowerCase().replace(' ', '-');
			var u = Randomize.element(images);
			var ii = Stream.of(u)
					.flatMap(x -> x.contains("front") ? Stream.of(x, x.replace("front", "back")) : Stream.of(x))
					.map(URI::create).toList();
			var p = getCrud(Product.class).create(new Product(null, Instant.now(), t, null, s, h, "Published", ii,
					ii.get(0), true, null, "Merch", null, sc.id()));
			var o = getCrud(ProductOption.class).create(ProductOption.of(p).withTitle("Size"));
			for (var v : u.equals("coffee-mug") ? new String[] { "One Size" } : new String[] { "S", "M", "L", "XL" }) {
				var w = getCrud(ProductVariant.class)
						.create(ProductVariant.of(p).withTitle(v).withInventoryQuantity(100));
				getCrud(ProductOptionValue.class).create(ProductOptionValue.of(o).withValue(v).withVariant(w.id()));
				getCrud(MoneyAmount.class)
						.create(MoneyAmount.of("usd", BigDecimal.valueOf(1199, 2)).withVariant(w.id()));
			}
		}
	}
}
