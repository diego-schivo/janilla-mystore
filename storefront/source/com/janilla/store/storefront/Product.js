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
export default class Product {

	selector;

	listen() {
		const e = this.selector();
		e.querySelectorAll("input").forEach(x => x.addEventListener("change", this.handleInputChange));
		e.querySelector("form").addEventListener("submit", this.handleFormSubmit);
	}

	handleInputChange = async event => {
		const f = event.target.form;
		const s = await fetch(`${location.pathname}/actions`, {
			method: "PUT",
			body: new URLSearchParams(new FormData(f))
		});
		f.closest("section").innerHTML = await s.text();
		this.listen();
	}

	handleFormSubmit = async event => {
		const d = new FormData(event.currentTarget, event.submitter);
		event.preventDefault();
		const s = await fetch(location.pathname, {
			method: "POST",
			body: new URLSearchParams(d)
		});
		/*
		document.querySelector(".nav .cart-item").outerHTML = await s.text();
		const e = document.querySelector(".nav .cart-dropdown");
		e.classList.add("visible");
		setTimeout(() => e.classList.remove("visible"), 5000);
		*/
		dispatchEvent(new CustomEvent("cartchange"));
	}
}
