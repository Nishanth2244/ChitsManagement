package com.project.chitti.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.chitti.dto.ChitAddRequestDTO;
import com.project.chitti.dto.ChitLiftRequestDTO;
import com.project.chitti.dto.ChitMemberDetailsDTO;
import com.project.chitti.dto.ChitResponseDTO;
import com.project.chitti.dto.ChitUpdateRequestDTO;
import com.project.chitti.dto.InstallmentDetailsDTO;
import com.project.chitti.dto.LoanCreateRequestDTO;
import com.project.chitti.dto.LoanInstallmentResponseDTO;
import com.project.chitti.dto.LoanPaymentRequestDTO;
import com.project.chitti.dto.LoanSummaryResponseDTO;
import com.project.chitti.dto.LoanTransactionResponseDTO;
import com.project.chitti.dto.LoanUpdateDto;
import com.project.chitti.dto.MonthlyFilterResponseDTO;
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
	
	
	@PatchMapping("/chit/{chitId}/update")
	public String updateChit(@PathVariable Long chitId,
							@RequestBody ChitUpdateRequestDTO dto) {
		
		return adminService.updateChitDetails(chitId, dto);
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
	
	
	
	@PatchMapping("/chit/{chitId}/delete")
	public String softDelete(@PathVariable Long chitId,
							@RequestParam boolean status) {
		
		return adminService.statusChange(chitId, status);
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
	
	
	@PostMapping("/chit/lift")
	public String liftChit(@RequestBody ChitLiftRequestDTO dto) {
	    return adminService.recordChitLift(dto);
	}
	
	
	@GetMapping("/chits/{chitId}/filter-by-month")
	public List<MonthlyFilterResponseDTO> filterChitByMonth(@PathVariable Long chitId,
															@RequestParam Integer monthnumber,
															@RequestParam(defaultValue = "ALL") String filterType){
		
		return adminService.getChitMonthReport(chitId, monthnumber, filterType);
	}
	
	@PatchMapping("/user/chit/{chitMemberId}/remove")
	public String removeUserFromChit(@PathVariable Long chitMemberId) {
		
		return adminService.removeUserFromChit(chitMemberId);
	}

	
	@PostMapping("/loan/create")
	public String createLoan(@RequestBody LoanCreateRequestDTO dto) {
	    return adminService.createLoan(dto);
	}
	
//	@PostMapping("/loan/create")
//	public ResponseEntity<String> createLoan(@RequestBody LoanCreateRequestDTO dto){
//		
//		ResponseEntity.status(HttpStatus.CREATED)
//						.body(adminService.createLoan(dto));.
//	}
	
	
	
	@PatchMapping("/loan/{loanId}/delete")
	public String softDeleteLoan(@PathVariable Long loanId,
								@RequestParam boolean status) {
		
		return adminService.softDeleteLoan(loanId, status);
	}
	
	
	@PatchMapping("/loan/{loanId}/update")
	public String loanUpdateDetails(@PathVariable Long loanId,
									@RequestBody LoanUpdateDto dto) {
		
		return adminService.loanDetailsUpdate(loanId, dto);
	}
	
	
	@GetMapping("/user/{userId}/loans")
	public List<LoanSummaryResponseDTO> getLoanBUserId(@PathVariable Long userId,
														@RequestParam (defaultValue = "true") boolean status){
		
		return adminService.getUserLoans(userId, status);
	}
	
	
	@GetMapping("/loan/{loanId}/installments")
	public List<LoanInstallmentResponseDTO> installmentByLoanId(@PathVariable Long loanId){
		
		return adminService.getLoanInstallments(loanId);
	}
	
	
	@GetMapping("/loan/installments/{installmentId}/transactions")
	public List<LoanTransactionResponseDTO> getTraByInstId(@PathVariable Long installmentId){
		
		return adminService.getLoanTransactions(installmentId);
	}
	
	
	@PostMapping("/loan/pay")
	public String payLoan(@RequestBody LoanPaymentRequestDTO dto) {
		
		return adminService.payLoan(dto);
	}
	
	
	@PatchMapping("/user/{userId}/delete-account")
	public String softDeleteUserAcc(@PathVariable Long userId,
									@RequestParam(defaultValue = "false") boolean status) {
		
		return adminService.softDeleteUser(userId, status);
	}
		
	
	
	
}
