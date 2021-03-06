package com.heartmarket.model.dto.response;

import java.util.List;

import com.heartmarket.model.dto.Trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OtherResponse {

	private int otherNo;
	private String otherImg;
	private String otherNickname;
	private double otherHg;
	private List<OtherTrade> otherList;
//	private List<Trade> otherList;
	
}
