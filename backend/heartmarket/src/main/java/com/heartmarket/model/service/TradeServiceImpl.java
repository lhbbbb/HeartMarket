package com.heartmarket.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.heartmarket.model.dao.CartRepository;
import com.heartmarket.model.dao.MailRepository;
import com.heartmarket.model.dao.MannerRepository;
import com.heartmarket.model.dao.TradeImgRepository;
import com.heartmarket.model.dao.TradeRepository;
import com.heartmarket.model.dao.UserRepository;
import com.heartmarket.model.dto.Cart;
import com.heartmarket.model.dto.Mail;
import com.heartmarket.model.dto.Manner;
import com.heartmarket.model.dto.Trade;
import com.heartmarket.model.dto.TradeImg;
import com.heartmarket.model.dto.User;
import com.heartmarket.model.dto.response.TradeDetail;
import com.heartmarket.util.ResultMap;

import io.swagger.annotations.ApiOperation;

@Service
public class TradeServiceImpl implements TradeService {

	@Autowired
	TradeRepository tr;

	@Autowired
	TradeImgRepository tir;

	@Autowired
	UserRepository ur;

	@Autowired
	CartRepository cr;

	@Autowired
	MannerRepository mr;
	
	@Autowired
	MailRepository mailr;

	// 모든 자료 조회
	@Transactional
	public List<Trade> findAll() {
		List<Trade> tList = tr.findAll();
		if (tList.size() == 0)
			return null;
		return tList;
	}

	// 지역별 게시글 조회
	@Transactional
	public List<Trade> findAllByAddr(String address) {
		List<Trade> aList = tr.findAllByTradeArea(address);
		if (aList.size() == 0)
			return null;
		return aList;
	}

	// 상세 페이지 조회
	// 로그인 안했을 때, 판매여부 확인
	@Transactional
	@Override
	public TradeDetail findDetail(int tradeNo) {

		try {
			Trade trade = tr.findByTradeNo(tradeNo);
			Manner manner = mr.findBymUserUserNo(trade.getTUser().getUserNo());
			if (Objects.isNull(trade.getBUser())) {
				return new TradeDetail(trade, (int) manner.getHeartGauge(), 0, 0);
			} else {
				return new TradeDetail(trade, (int) manner.getHeartGauge(), 0, 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 로그인 되어있는 상태.
	// 찜 목록 표현
	// 거래 완료되었는지 아닌지 확인
	@Transactional
	@Override
	public TradeDetail findDetailByEmail(int tradeNo, int userNo) {
		try {
			// 게시글 정보를 가져오기
			Trade trade = tr.findByTradeNo(tradeNo);
			Cart cart = cr.findBycTradeTradeNoAndcUserUserNo(tradeNo, userNo);
			Manner manner = mr.findBymUserUserNo(trade.getTUser().getUserNo());
			if (Objects.isNull(cart)) {
				return Objects.isNull(trade.getBUser()) ? new TradeDetail(trade, (int) manner.getHeartGauge(), 0, 0)
						: new TradeDetail(trade, (int) manner.getHeartGauge(), 0, 1);
			} else {
				return Objects.isNull(trade.getBUser()) ? new TradeDetail(trade, (int) manner.getHeartGauge(), 1, 0)
						: new TradeDetail(trade, (int) manner.getHeartGauge(), 1, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 게시글 추가
	@Transactional
	public ResultMap<Integer> addTrade(Trade trade, List<TradeImg> fList, int userNo) {
		try {
			User user = ur.findByUserNo(userNo);
			for (TradeImg tradeImg : fList) {
				tradeImg.setTiTrade(trade);
			}
			trade.setTUser(user);
			trade.settTradeImg(fList);
			tr.save(trade);
			return new ResultMap<Integer>("SUCCSS", "게시글 추가 완료", 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 게시글 수정
	@Transactional
	@ApiOperation(value = "게시글 수정")
	public ResultMap<Object> updateTrade(Trade trade, List<TradeImg> fList) {
		try {
			List<TradeImg> tradeOrigin = tir.findAllBytiTradeTradeNo(trade.getTradeNo());
			int oldlen = tradeOrigin.size();
			System.out.println("올드 : " + oldlen);
			int newlen = fList.size();
			System.out.println("뉴 : " + newlen);
			if(oldlen < newlen) {
				for (int i = oldlen; i < newlen; i++) {
					tradeOrigin.add(new TradeImg(trade,fList.get(i).getOrgImg()));
				}
			} else if (oldlen > newlen) {
				for (int i = 0; i < oldlen; i++) {
					if (i >= newlen)
						tir.delete(tradeOrigin.get(i));
				}
				tradeOrigin = tradeOrigin.subList(0, newlen);
			}
			for (int i = 0; i < tradeOrigin.size(); i++) {
				if (i < oldlen) {
					System.out.println("뭔데 : "+fList.get(i).getOrgImg());
					tradeOrigin.get(i).setOrgImg(fList.get(i).getOrgImg());
				}
			}
			trade.settTradeImg(tradeOrigin);
			tir.saveAll(tradeOrigin);
			tr.save(trade);
			return new ResultMap<Object>("SUCCSS", "게시글 수정 완료", trade);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}  

	// 게시글 삭제
	@Transactional
	public ResultMap<Object> deleteTrade(int no) {
		try {
			Trade trade = tr.findById(no).orElse(null);
			if (Objects.isNull(trade)) { 
				return new ResultMap<Object>("FAIL", "게시글 삭제 실패", null);
			} else {
				trade.setTUser(null);
				trade.setBUser(null);
			
				List<TradeImg>	tradeImg = tir.findAllBytiTradeTradeNo(no);
				for (TradeImg ti : tradeImg) {
//					System.out.println(ti.toString());
					tir.delete(ti);
				}
				List<Cart> carts = cr.findBycTradeTradeNo(no);
				for (Cart cart : carts) {
					cr.delete(cart);
				}
				List<Mail> mail = mailr.findAllByTradeTradeNo(trade.getTradeNo());
				for (Mail mail2 : mail) {
					mail2.setTrade(null);
//					System.out.println(mail2.getMailNo());
					mailr.save(mail2);
				}
				tr.delete(trade);
				return new ResultMap<Object>("SUCCESS", "게시글 삭제 성공", 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 서울시 전체 목록 가져오기
	@Override
	public Page<Trade> getList(int no, int size, int filter) {
		if (filter == 4) {
			PageRequest pr = PageRequest.of(no, size, Sort.by("productPrice").ascending());
			return tr.findAll(pr);
		} else if (filter == 5) { // 판매중
			PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
			return tr.findBybUserUserNoIsNull(pr);
		} else if (filter == 6) { // 판매완료
			PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
			return tr.findBybUserUserNoIsNotNull(pr);
		} else {
			PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
			return tr.findAll(pr);
		}
	}

	// 사용자가 설정한 지역을 기준으로 페이징
	@Override
	public Page<Trade> fetPages(int no, int size, String area) {
		PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
		return tr.findByTradeArea(area, pr);
	}

	// 사용자가 설정한 지역을 기준으로 카테고리를 선택했을 때, 페이징
	// filter 1이 최신순 2 가격순
	@Override
	public Page<Trade> fetPageAC(int no, int size, String area, String category, int filter) {
		List<Trade> tList = tr.findAllByTradeArea(area);
		if (tList.size() == 0)
			return null;

		if (filter == 4) {
			PageRequest pr = PageRequest.of(no, size, Sort.by("productPrice").ascending());
			return tr.findAllByTradeAreaAndTradeCategory(area, category, pr);
		}else if(filter == 1) {
			PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
			return tr.findAllByTradeCategory(category, pr);
		}
		else if(filter == 5) { // 판매중
			PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
			return tr.findAllByTradeAreaAndTradeCategoryAndBUserUserNoIsNull(area, category, pr);
		} else if (filter == 6) { // 판매완료
			PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
			return tr.findAllByTradeAreaAndTradeCategoryAndBUserUserNoIsNotNull(area, category, pr);
		} else {
			PageRequest pr = PageRequest.of(no, size, Sort.by("tradeNo").descending());
			return tr.findAllByTradeAreaAndTradeCategory(area, category, pr);
		}
	}

	@Override
	// 검색했을 때, 결과 불러오기
	public Page<Trade> fetPageTP(int no, int size, List<String> sList, String area, int filter, String category) {
		// 둘 중 하나 입니당....
		// 로그인을 안 했을 때,
//		if()
		Specification<Trade> spec = new Specification<Trade>() {

			@Override
			public Predicate toPredicate(Root<Trade> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (sList != null) {
					for (String str : sList) {
						predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("tradeTitle"), "%" + str + "%"),
								criteriaBuilder.like(root.get("productInfo"), "%" + str + "%")));
					}
				}
				
				if (!area.equals("none"))
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("tradeArea"), area)));
				if (Integer.parseInt(category) > 0) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("tradeCategory"), category)));
				}
				if (filter == 5) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.isNull(root.get("bUser").get("userNo"))));
				} else if (filter == 6) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("bUser").get("userNo"))));
				}
				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}

		};
		// 로그인을 했을 때,
		List<Trade> tList = new ArrayList<Trade>();
		if (!area.equals("none"))
			tList = tr.findAllByTradeArea(area);
		else {
			tList = tr.findAll();
		}
		if (filter == 4) {
			return tr.findAll(spec, PageRequest.of(no, size, Sort.by("productPrice").ascending()));
		} else {
			return tr.findAll(spec, PageRequest.of(no, size, Sort.by("tradeNo").descending()));
		}
	}

	@Override
	public ResultMap<Trade> findByCompleteTrade(int bUserNo, String other, int tradeNo) {
		Trade tmp = new Trade();
		try {
//			// 1. 현재 로그인 중인 유저의 기준으로 게시물을 가져옴.
			// 닉네임을 검색해야 합니다.
			User buyer = ur.findByNickname(other);
			// 2. 게시물에서 구매자 아이디가 null인지 확인
			Trade checkBuyer = tr.findByTradeNo(tradeNo);
			
			if (Objects.isNull(checkBuyer.getBUser())) {
				// 3. null 이라면 구매자 아이디를 검색하여 확인 사살
				checkBuyer.setBUser(buyer);
				tr.save(checkBuyer);
				return new ResultMap<Trade>("SUCCESS", "거래 완료 확정", tmp);
			} else {
				// 2-1. null이 아니라면 거래가 완료된 게시글
				return new ResultMap<Trade>("FAIL", "거래가 완료된 글입니다.", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public Page<Trade> findByTradeType(int no, int size, int type, int userno) {
		return tr.findAll(new Specification<Trade>() {

			@Override
			public Predicate toPredicate(Root<Trade> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<Predicate>();

				// 구매
				if (type == 1) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("bUser").get("userNo"), userno)));
				}
				// 판매
				else if (type == 2) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("tUser").get("userNo"), userno)));
				}

				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}

		}, PageRequest.of(no, size, Sort.by("tradeNo").descending()));
	}

	@Override
	public Trade findByTradeNo(String tradeNo) {
		try {
			int no = Integer.parseInt(tradeNo);
			Trade trade = tr.findByTradeNo(no);
			if (trade != null)
				return trade;
			else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 인기매물 목록 구하기
	@Override
	public List<Trade> getPopularList() {
		try {

			return tr.findTop8All(PageRequest.of(0, 8)).getContent();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
