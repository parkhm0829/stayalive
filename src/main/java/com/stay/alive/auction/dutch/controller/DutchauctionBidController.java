package com.stay.alive.auction.dutch.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.stay.alive.auction.dutch.service.DutchauctionBidService;
import com.stay.alive.auction.dutch.service.DutchauctionService;
import com.stay.alive.auction.dutch.vo.DutchAuction;
import com.stay.alive.auction.dutch.vo.DutchAuctionBid;


@Controller
@RequestMapping("/auction/dutch")
public class DutchauctionBidController {
	@Autowired
	DutchauctionService dutchauctionService;
	@Autowired
	DutchauctionBidService dutchauctionBidService;
	
	@GetMapping("successfulbid")
	public String DutchauctionBid(int dutchauctionNo, HttpSession session) {
		/* String memberId = (String)session.getAttribute("memberId"); */
		String memberId = "ID1";
		DutchAuction dutchAuction = dutchauctionService.getDutchAuctionFromNo(dutchauctionNo);
		if(dutchAuction.getAuctionStateCategoryName().equals("낙찰대기중") && dutchAuction.getAuctionStateCategoryNo() == 1) {
			dutchauctionBidService.removeJobInCurrentScheduler(dutchauctionNo); // 현재 진행되고 있는 스케줄링 종료
			dutchAuction.setAuctionStateCategoryNo(2);
			dutchAuction.setAuctionStateCategoryName("낙찰완료");
			dutchauctionService.modifyStateCategory(dutchAuction); //등록한 역경매 상태 데이터베이스에서 변경
			DutchAuctionBid dutchAuctionBid = new DutchAuctionBid();
			dutchAuctionBid.setDutchauctionRegisterNo(dutchAuction.getDutchauctionNo());
			dutchAuctionBid.setMemberId(memberId);
			dutchAuctionBid.setDutchauctionSuccessfulbidPrice(dutchAuction.getDutchauctionUpdatePrice());
			dutchAuctionBid.setDutchauctionCheckinDate(dutchAuction.getDutchauctionCheckinDate());
			dutchAuctionBid.setDutchauctionChechoutDate(dutchAuction.getDutchauctionCheckoutDate());
			dutchAuctionBid.setAuctionStateCategoryNo(2);
			dutchAuctionBid.setAuctionStateCategoryName("낙찰완료");
			dutchauctionBidService.addDutchauctionSuccessfulbid(dutchAuctionBid); //낙찰정보 데이터베이스에 추가
		}
		
		
		
		
		return "redirect:/main";
	}
}
