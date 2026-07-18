package com.project.chitti.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.chitti.dto.ChitAddRequestDTO;
import com.project.chitti.dto.ChitMemberDetailsDTO;
import com.project.chitti.dto.ChitResponseDTO;
import com.project.chitti.dto.InstallmentDetailsDTO;
import com.project.chitti.dto.PaymentReceiptDTO;
import com.project.chitti.dto.PaymentRequestDTO;
import com.project.chitti.dto.TransactionDetailsDTO;
import com.project.chitti.dto.UserAddRequestDTO;
import com.project.chitti.dto.UserResponseDTO;
import com.project.chitti.dto.UserSearchResponseDTO;
import com.project.chitti.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminService adminService;
	
	
	@PostMapping("/createChit")
	public String createChit(@RequestBody ChitAddRequestDTO chitAddRequestDTO) {
		
		return adminService.crateChit(chitAddRequestDTO);
	}
	
	
	@PostMapping("/user/createUser")
	public String createUser(@RequestBody UserAddRequestDTO dto) {
		
		return adminService.createUser(dto);
	}

	
	@PostMapping("/chit/addMember")
	public String addMember(@RequestParam Long chitId,
							@RequestParam Long userId) {
		
		return adminService.addMember(chitId, userId);
	}
	
	@PostMapping("/chit/pay")
	public PaymentReceiptDTO makePayment(@RequestBody PaymentRequestDTO dto) {
	    return adminService.processPayment(dto);
	}
	
	
	
	@GetMapping("/chits/ByStatus")
	public List<ChitResponseDTO> getAllChits(@RequestParam (defaultValue = "0") int page,
											@RequestParam (defaultValue = "15") int size,
											@RequestParam (defaultValue = "true") boolean status){
		
		Pageable pageable = PageRequest.of(page, size);
		
		return adminService.getAllChits(pageable, status);
	}
	
	
	@GetMapping("/users/ByStatus")
	public List<UserResponseDTO> getAllUser(@RequestParam (defaultValue = "0") int page,
											@RequestParam (defaultValue = "15") int size,
											@RequestParam (defaultValue = "true") boolean status){
		
		Pageable pageable = PageRequest.of(page, size);
		return adminService.getAllUsers(pageable, status);
	}
	
	
	@GetMapping("/chits/{chitId}/members")
	public List<ChitMemberDetailsDTO> getChitMembers(@PathVariable Long chitId) {
		
	    return adminService.getChitMembers(chitId);
	}
	
	
	
	@GetMapping("/members/{chitMemberId}/installments")
	public List<InstallmentDetailsDTO> getInstallmentByChitMemberId(@PathVariable Long chitMemberId){
		
		return adminService.getInstallmentByChitMemberId(chitMemberId);
	}
	
	
	@GetMapping("/installments/{installmentId}/transactions")
	public List<TransactionDetailsDTO> getInstallmentTransactions(@PathVariable Long installmentId){
		
		return adminService.getTransactionsByInstId(installmentId);
	}
	
	
	@GetMapping("/user/search")
	public List<UserSearchResponseDTO> searchUser(@RequestParam String name,
													@RequestParam String phoneNo){
		
		return adminService.searchUser(name, phoneNo);
		
	}
	
	
	@GetMapping("/hello")
	public String hello() {
		log.info("schedular fcorm cotnroller");
		return "hello";
	}
}
