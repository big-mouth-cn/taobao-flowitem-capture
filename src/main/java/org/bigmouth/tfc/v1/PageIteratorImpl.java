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
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bigmouth.nvwa.utils.StringHelper;
import org.bigmouth.tfc.Constants;
import org.bigmouth.tfc.JsoupHelper;
import org.bigmouth.tfc.Page;
import org.bigmouth.tfc.PageIterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * 
 * @author Allen Hu 
 * 2016-7-15
 */
public class PageIteratorImpl implements PageIterator {
	
	private List<Page> elementData = Lists.newArrayList();
	private int modCount = -1;
	
	@Override
	public boolean hasNext() {
		modCount++;
		
		if (elementData.size() > modCount) {
			return true;
		}
		return false;
	}

	@Override
	public Page next() {
		return elementData.get(modCount);
	}

	@Override
	public void remove() {
		elementData.remove(modCount);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PageIteratorImpl.class);
	
	private String url;
	private String host;
	private Document document;
	private String pageTitle;
	
	private String asynSearchUrl;
	private Document asynSearchDoc;
	
	public PageIteratorImpl(String url) {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("url");
		}
		this.url = url;
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
		// Added current page.
		this.elementData.add(new PageImpl(url, 1));
		this.initPagination();
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
	
	protected void initPagination() {
		Elements paginationEle = this.asynSearchDoc.select("div.J_TItems .pagination");
		if (CollectionUtils.isNotEmpty(paginationEle)) {
			Element firstPagination = paginationEle.get(0);
			Elements aEles = firstPagination.select("a");
			if (CollectionUtils.isNotEmpty(aEles)) {
				for (Element element : aEles) {
					String href = element.attr("href");
					if (StringHelper.isNotBlank(href)) {
						String url = Constants.PROTOCOL_PREFIX + href;
						String text = element.text();
						int pageNo = NumberUtils.toInt(text, -1);
						if (pageNo != -1) {
							this.elementData.add(new PageImpl(url, pageNo));
						}
					}
				}
			}
		}
	}
}
