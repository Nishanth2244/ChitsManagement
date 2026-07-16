package com.project.chitti.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.chitti.dto.ChitAddRequestDTO;
import com.project.chitti.dto.ChitMemberDetailsDTO;
import com.project.chitti.dto.ChitResponseDTO;
import com.project.chitti.dto.InstallmentDetailsDTO;
import com.project.chitti.dto.PaymentReceiptDTO;
import com.project.chitti.dto.PaymentRequestDTO;
import com.project.chitti.dto.TransactionDetailsDTO;
import com.project.chitti.dto.UserAddRequestDTO;
import com.project.chitti.dto.UserResponseDTO;
import com.project.chitti.entity.ChitMembers;
import com.project.chitti.entity.Chits;
import com.project.chitti.entity.Installments;
import com.project.chitti.entity.Transactions;
import com.project.chitti.entity.Users;
import com.project.chitti.exceptionHandler.AlreadyExistsException;
import com.project.chitti.exceptionHandler.ResourceNotFoundException;
import com.project.chitti.repository.ChitMemberRepository;
import com.project.chitti.repository.ChitRepository;
import com.project.chitti.repository.InstallmentRepository;
import com.project.chitti.repository.TransactionRepository;
import com.project.chitti.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	
	private final ChitRepository chitRepository;
	private final UserRepository userRepository;
	private final ChitMemberRepository chitMemberRepository ;
	private final InstallmentRepository installmentRepository;
	private final TransactionRepository transactionRepository;

	
	public String crateChit(ChitAddRequestDTO chitAddRequestDTO) {
		
		if(chitRepository.existsByName(chitAddRequestDTO.getChitName())) {
			throw new AlreadyExistsException("Chit already exists with Name");
		}
		
		Chits chits = new Chits();
		chits.setCreatedAt(LocalDateTime.now());
		chits.setInstallmentAmt(chitAddRequestDTO.getInstallmentAmt());
		chits.setName(chitAddRequestDTO.getChitName());
		chits.setStatus(true);
		chits.setTotalAmount(chitAddRequestDTO.getTotalAmount());
		chits.setTotalMonths(chitAddRequestDTO.getTotalMonths());
		
		chitRepository.save(chits);
		
		return "Chit created succesfully";
	}


	public String createUser(UserAddRequestDTO dto) {
		
		if(userRepository.existsByName(dto.getName())) {
			throw new AlreadyExistsException("User already exists with name: "+ dto.getName());
		}
		
		Users user = new Users();
		user.setAddress(dto.getAddress());
		user.setCreatedAt(LocalDateTime.now());
		user.setName(dto.getName());
		user.setPhoneNo(dto.getPhoneNo());
		user.setRole("USER");
		user.setStatus(true);

		userRepository.save(user);
		
		return "User crated succesfully";
	}


	public String addMember(Long chitId, Long userId) {
		
		Chits chit = chitRepository.findById(chitId)
				.orElseThrow(() -> new ResourceNotFoundException("No chit found to add member: "+ chitId));
		
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("No memeber found to add in chit: "+ userId));
		
		ChitMembers chitMembers = new ChitMembers();
		chitMembers.setChit(chit);
		chitMembers.setUser(user);
		chitMembers.setJoinedAt(LocalDateTime.now());
		chitMembers.setStatus(true);

		ChitMembers savedMember = chitMemberRepository.save(chitMembers);
		
		
		List<Installments> installmentsList = new ArrayList<>();
	    
	    for(int i = 1; i <= chit.getTotalMonths(); i++) {
	        Installments inst = new Installments();
	        inst.setChitMember(savedMember);
	        inst.setMonthNumber(i);
	        inst.setExpectedAmt(chit.getInstallmentAmt());
	        inst.setPaidAmt(0L); 
	        inst.setStatus("PENDING"); 
	        
	        installmentsList.add(inst);
	    }
	    
	    
	    installmentRepository.saveAll(installmentsList);

	    return "Member added to chit and " + chit.getTotalMonths() + " installment rows generated successfully.";
	}
	
	
	public PaymentReceiptDTO processPayment(PaymentRequestDTO dto) {
	    
	    // 1. Validate if user is in that chit
	    ChitMembers chitMember = chitMemberRepository.findByChitIdAndUserId(dto.getChitId(), dto.getUserId())
	            .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this chit"));

	    
	    // 2. Fetch the exact month selected by the admin
	    Installments selectedInstallment = installmentRepository
	            .findByChitMemberIdAndMonthNumber(chitMember.getId(), dto.getMonthNumber())
	            .orElseThrow(() -> new ResourceNotFoundException("Invalid month number for this chit"));

	    
//	    3. create a trasaction of that installment.
	    Transactions transactions = new Transactions();
	    transactions.setInstallment(selectedInstallment);
	    transactions.setPaidAmount(dto.getAmount());	    
	    transactions.setPaidOn(LocalDateTime.now());
	    transactions.setPaymentMethod(dto.getPaymentMethod());
	    
	    Transactions savedTransaction = transactionRepository.save(transactions);
	    
//	    4.Update the installment paid Amt.
	    Long totalPaidAfterThis = selectedInstallment.getPaidAmt() + dto.getAmount();
	    selectedInstallment.setPaidAmt(totalPaidAfterThis);
	    
	    
	 // 5. Status update
	    if (totalPaidAfterThis >= selectedInstallment.getExpectedAmt()) {
	        selectedInstallment.setStatus("PAID");
	    } else {
	        selectedInstallment.setStatus("PARTIAL");
	    }
	    
	    
	    installmentRepository.save(selectedInstallment);

	    return PaymentReceiptDTO.builder()
	    		.transactionId(savedTransaction.getId())
	    		.chitName(chitMember.getChit().getName())
	    		.memberName(chitMember.getUser().getName())
	    		.phoneNo(chitMember.getUser().getPhoneNo())
	    		.monthNumber(selectedInstallment.getMonthNumber())
	    		.paidAmount(dto.getAmount())
	            .paymentMethod(dto.getPaymentMethod())
	            .paidOn(savedTransaction.getPaidOn())
	            .monthExpectedAmount(selectedInstallment.getExpectedAmt())
	            .monthBalanceDue(selectedInstallment.getExpectedAmt() - selectedInstallment.getPaidAmt())
	            .installmentStatus(selectedInstallment.getStatus())
	            .build();
	    
	}


	public List<ChitResponseDTO> getAllChits(Pageable pageable, boolean status) {
		
		Page<Chits> chits = chitRepository.findByStatus(status,pageable);
		
		if(chits.isEmpty()) {
			throw new ResourceNotFoundException("No Chits Found");
		}
		
		return chits.stream()
				.map(chit ->ChitResponseDTO.builder()
						.id(chit.getId())
						.name(chit.getName())
						.totalAmount(chit.getTotalAmount())
						.totalMonths(chit.getTotalMonths())
						.installmentAmt(chit.getInstallmentAmt())
						.status(chit.isStatus())
						.createdAt(chit.getCreatedAt())
						.build())
				.toList();
				
	
	}


	public List<UserResponseDTO> getAllUsers(Pageable pageable, boolean status) {
		
		Page<Users> users = userRepository.findByStatusAndRoleNot(status, "ADMIN", pageable);
		
		if(users.isEmpty()) {
			throw new ResourceNotFoundException("Users not Founs");
		}
		
		return users.stream()
				.filter(user -> !user.getRole().equals("ADMIN"))
				.map(user -> UserResponseDTO.builder()
						.id(user.getId())
						.name(user.getName())
						.phoneNo(user.getPhoneNo())
						.address(user.getAddress())
						.status(user.isStatus())
						.createdAt(user.getCreatedAt())
						.build())
				.toList();
		
	}


	public List<ChitMemberDetailsDTO> getChitMembers(Long chitId) {

		if (!chitRepository.existsById(chitId)) {
	        throw new ResourceNotFoundException("Chit not found with id: " + chitId);
	    }
		
		List<ChitMembers> members = chitMemberRepository.findByChitId(chitId);
		
		
		return members.stream()
		        .map(member -> ChitMemberDetailsDTO.builder()
		                .chitMemberId(member.getId())
		                .userId(member.getUser().getId())
		                .name(member.getUser().getName())
		                .phoneNo(member.getUser().getPhoneNo())
		                .joinedAt(member.getJoinedAt())
		                .status(member.isStatus())
		                .build())
		        .toList();
		
	}


	public List<InstallmentDetailsDTO> getInstallmentByChitMemberId(Long chitMemberId) {
		
		if (!chitMemberRepository.existsById(chitMemberId)) {
	        throw new ResourceNotFoundException("Chit Member record not found with ID: " + chitMemberId);
	    }
		
		
		List<Installments> installments = installmentRepository.findByChitMemberIdOrderByMonthNumberAsc(chitMemberId);
		
		
		return installments.stream()
				.map(installment -> InstallmentDetailsDTO.builder()
						.installmentId(installment.getId())
						.monthNumber(installment.getMonthNumber())
						.expectedAmt(installment.getExpectedAmt())
						.paidAmt(installment.getPaidAmt())
						.status(installment.getStatus())
						.build())
				.toList();

	}


	
	public List<TransactionDetailsDTO> getTransactionsByInstId(Long installmentId) {
		
		if (!installmentRepository.existsById(installmentId)) {
	        throw new ResourceNotFoundException("Installment record not found with ID: " + installmentId);
	    }

		
		List<Transactions> transactions = transactionRepository.findByInstallmentIdOrderByIdAsc(installmentId);

		return transactions.stream()
				.map(transaction -> TransactionDetailsDTO.builder()
						.transactionId(transaction.getId())
						.paidAmount(transaction.getPaidAmount())
						.paymentMethod(transaction.getPaymentMethod())
						.paidOn(transaction.getPaidOn())
						.build())
				.toList();
	}

}
