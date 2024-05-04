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
import Sidebar from "./Sidebar.js";
import Products from "./Products.js";

export default class Layout {

	selector;
	
	sidebar;
	
	products;

	listen() {
		const e = this.selector();
		let s = this.sidebar;
		if (!s) {
			this.sidebar = s = new Sidebar();
			s.selector = () => this.selector().querySelector(".sidebar");
		}
		let p = this.products;
		if (!p && e.querySelector(".products")) {
			this.products = p = new Products();
			p.selector = () => this.selector().querySelector(".products");
		}
		e.addEventListener("click", this.handleClick);
		s.listen();
		p && p.listen();
	}

	handleClick = async event => {
		const e = event.currentTarget;
		const a = event.target.closest("a");
		if (!a)
			return;
		event.preventDefault();
		const u = a.getAttribute("href");
		history.pushState({}, "", u);
		const s = await fetch(u);
		e.querySelector(".content-parent").innerHTML = await s.text();
		delete this.products;
		this.listen();
	}
}
