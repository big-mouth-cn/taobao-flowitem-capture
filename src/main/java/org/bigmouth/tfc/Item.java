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

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.bigmouth.nvwa.utils.JsonHelper;
import org.bigmouth.nvwa.utils.StringHelper;

/**
 * 
 * @author Allen Hu 2016-7-15
 */
public class Item {

	private String url;
	
	private String name;
	private BigDecimal price;

	private String region;
	private String limit;
	private String effectOfMon;
	private String effective;
	private String op;
	private String amount;
	private String expiry;
	private String chargeType;
	private String dataType;

	public void parseAttributes(Map<String, String> attrs) {
		if (null == attrs)
			return;
		for (Entry<String, String> entry : attrs.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringHelper.equals("充值地区", key)) {
				this.region = value;
			}
			else if (StringHelper.equals("当月限充（次）", key)) {
				this.limit = value;
			}
			else if (StringHelper.equals("是否当月失效", key)) {
				this.effectOfMon = value;
			}
			else if (StringHelper.equals("生效时间", key)) {
				this.effective = value;
			}
			else if (StringHelper.equals("运营商", key)) {
				this.op = value;
			}
			else if (StringHelper.equals("流量", key)) {
				this.amount = value;
			}
			else if (StringHelper.equals("有效期", key)) {
				this.expiry = value;
			}
			else if (StringHelper.equals("充值方式", key)) {
				this.chargeType = value;
			}
			else if (StringHelper.equals("流量类型", key)) {
				this.dataType = value;
			}
		}
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getEffectOfMon() {
		return effectOfMon;
	}

	public void setEffectOfMon(String effectOfMon) {
		this.effectOfMon = effectOfMon;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return JsonHelper.convert(this);
	}
}
