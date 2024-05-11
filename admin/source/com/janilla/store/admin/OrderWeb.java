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

import com.janilla.persistence.Persistence;
import com.janilla.store.backend.Order;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class OrderWeb {

	public Persistence persistence;

	@Handle(method = "GET", path = "/admin/orders")
	public Orders getOrders() {
		var ii = persistence.getCrud(Order.class).list();
		var oo = persistence.getCrud(Order.class).read(ii).toList();
		return new Orders(oo);
	}

	@Handle(method = "GET", path = "/admin/orders/(\\d+)")
	public @Render("Order.html") Order getOrder(Long id) {
		var o = persistence.getCrud(Order.class).read(id);
		return o;
	}
}
