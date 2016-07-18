/*
 * Copyright 2016 big-mouth.cn
 *
 * The Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.bigmouth.tfc.test;

import org.bigmouth.tfc.Page;
import org.bigmouth.tfc.PageIterator;
import org.bigmouth.tfc.v1.PageIteratorImpl;

/**
 * 
 * @author Allen Hu 
 * 2016-7-15
 */
public class PageTest {

	public static void main(String[] args) {
		PageIterator page = new PageIteratorImpl("https://wytx.tmall.com/search.htm");
		while (page.hasNext()) {
			Page next = page.next();
			System.out.println(next.getUrl());
			System.out.println(next.getItems());
		}
	}
}
