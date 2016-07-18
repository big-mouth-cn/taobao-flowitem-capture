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
package org.bigmouth.tfc.v1;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bigmouth.nvwa.utils.StringHelper;
import org.bigmouth.tfc.Constants;
import org.bigmouth.tfc.Item;
import org.bigmouth.tfc.JsoupHelper;
import org.bigmouth.tfc.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author Allen Hu 
 * 2016-7-15
 */
public class PageImpl implements Page {

	private static final Logger LOGGER = LoggerFactory.getLogger(PageImpl.class);
	
	private String url;
	private String host;
	private Document document;
	private String pageTitle;
	
	private String asynSearchUrl;
	private Document asynSearchDoc;
	
	private int pageNo;

	public PageImpl(String url, int pageNo) {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("url");
		}
		this.url = url;
		this.pageNo = pageNo;
		try {
			this.init();
		} catch (IOException e) {
			throw new IllegalStateException("init:", e);
		}
	}

	private void init() throws IOException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Start initialization: {}", url);
		}
		this.host = parseHost(url);
		this.document = JsoupHelper.get(url);
		this.pageTitle = document.title();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("This page title is: {}", pageTitle);
		}
		this.asynSearch();
	}

	@Override
	public String getTitle() {
		return this.pageTitle;
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	@Override
	public int getPageNo() {
		return this.pageNo;
	}

	@Override
	public List<Item> getItems() {
		if (null == this.asynSearchDoc) {
			throw new IllegalStateException("Please do init.");
		}
		List<Item> result = Lists.newArrayList();
		Elements itemLines = asynSearchDoc.select("div.J_TItems div");
		for (Element line : itemLines) {
			Elements items = line.select(".item");
			for (Element item : items) {
				Elements data = item.select(".J_TGoldData");
				if (CollectionUtils.isNotEmpty(data)) {
					// 页面正文列表
					Item o = new Item();
					Elements a = item.select(".detail a");
					String name = a.text();
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Captured item: {}", name);
					}
					o.setName(name);
					
					Elements priceElements = item.select(".detail .attribute .cprice-area .c-price");
					if (CollectionUtils.isNotEmpty(priceElements)) {
						Element price = priceElements.get(0);
						o.setPrice(new BigDecimal(price.text()));
					}
					
					Element datainf = data.get(0);
					String href = Constants.PROTOCOL_PREFIX + datainf.attr("href");
					
					o.setUrl(href);
					
					DetailPage dp = new DetailPage(href);
					o.parseAttributes(dp.getAttributes());
					
					result.add(o);
				} 
				else {
					// 页脚推荐列表
				}
			}
		}
		return result;
	}

	@Override
	public String getCurrentPageHtml() {
		return this.document.html();
	}
	
	protected String parseHost(String url) {
		int index = url.indexOf(Constants.PROTOCOL_STRING);
		if (index == -1) {
			throw new IllegalStateException("Illegal url. " + url);
		}
		return url.substring(0, StringUtils.indexOf(url, '/', Constants.PROTOCOL_STRING.length()));
	}
	
	protected void asynSearch() throws IOException {
		Element asynUrl = document.getElementById("J_ShopAsynSearchURL");
		this.asynSearchUrl = host + asynUrl.val();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ShopAsycnSearchURL: {}", this.asynSearchUrl);
		}
		this.asynSearchDoc = JsoupHelper.get(asynSearchUrl);
		String html = JsoupHelper.escape(this.asynSearchDoc.html());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("shopasycnsearch source: {}", html);
		}
		this.asynSearchDoc = Jsoup.parse(html);
	}
	
	static class DetailPage {
		
		private static final Logger LOGGER = LoggerFactory.getLogger(DetailPage.class);
		private String url;
		private HashMap<String, String> attributes = Maps.newHashMap();

		public DetailPage(String url) {
			this.url = url;
			try {
				this.init();
			} catch (IOException e) {
				LOGGER.warn("Cannot doInit: {}", e.getMessage());
			}
		}
		
		private void init() throws IOException {
			if (StringHelper.isBlank(url))
				return;
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Start capture detail page: {}", url);
			}
			Document detailsDoc = JsoupHelper.get(url);
			Elements attrElements = detailsDoc.select("#J_AttrUL");
			String html = attrElements.html();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Detail page attrul source: {}", html);
			}
			String[] lines = StringHelper.split(html, "\n");
			for (String l : lines) {
				String attr = l.replaceAll("<.+?>", "");
				if (StringUtils.isBlank(attr)) {
					continue;
				}
				attr = JsoupHelper.escape(attr);
				String[] item = StringHelper.split(attr, ':');
				attributes.put(item[0], item.length > 1 ? StringHelper.trim(item[1]) : null);
			}
		}
		
		public HashMap<String, String> getAttributes() {
			return this.attributes;
		}
	}
}
