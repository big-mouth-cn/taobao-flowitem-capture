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
package org.bigmouth.tfc;

import java.io.IOException;

import org.bigmouth.nvwa.utils.StringHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 * @author Allen Hu 
 * 2016-7-15
 */
public final class JsoupHelper {

	private JsoupHelper() {
	}
	
	public static Document get(String url) throws IOException {
		return Jsoup.connect(url).timeout(60000).get();
	}
	
	public static String escape(String html) {
		if (StringHelper.isBlank(html))
			return null;
		return html.replaceAll("&amp;", "&").replaceAll("&quot;", "").replaceAll("\\\\", "")
			.replaceAll("&nbsp;", " ");
	}
}
