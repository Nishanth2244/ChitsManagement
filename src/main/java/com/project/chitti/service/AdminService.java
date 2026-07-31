package com.project.chitti.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.project.chitti.entity.ChitLifts;
import com.project.chitti.entity.ChitMembers;
import com.project.chitti.entity.Chits;
import com.project.chitti.entity.Installments;
import com.project.chitti.entity.LoanInstallments;
import com.project.chitti.entity.LoanTransactions;
import com.project.chitti.entity.Loans;
import com.project.chitti.entity.Transactions;
import com.project.chitti.entity.Users;
import com.project.chitti.exceptionHandler.AlreadyExistsException;
import com.project.chitti.exceptionHandler.BadRequestException;
import com.project.chitti.exceptionHandler.ResourceNotFoundException;
import com.project.chitti.repository.ChitLiftRepository;
import com.project.chitti.repository.ChitMemberRepository;
import com.project.chitti.repository.ChitRepository;
import com.project.chitti.repository.InstallmentRepository;
import com.project.chitti.repository.LoanInstallmentRepository;
import com.project.chitti.repository.LoanRepository;
import com.project.chitti.repository.LoanTransactionRepository;
import com.project.chitti.repository.TransactionRepository;
import com.project.chitti.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
	
	private final ChitRepository chitRepository;
	private final UserRepository userRepository;
	private final ChitMemberRepository chitMemberRepository ;
	private final InstallmentRepository installmentRepository;
	private final TransactionRepository transactionRepository;
	private final ChitLiftRepository chitLiftRepository;
	private final LoanInstallmentRepository loanInstallmentRepository;
	private final LoanRepository loanRepository;
	private final LoanTransactionRepository loanTransactionRepository;
	
	
	public String crateChit(ChitAddRequestDTO chitAddRequestDTO) {
		
		if(chitRepository.existsByName(chitAddRequestDTO.getChitName())) {
			throw new AlreadyExistsException("Chit already exists with Name");
		}
		
		Chits chits = new Chits();
		chits.setCreatedAt(LocalDateTime.now());
		chits.setStartDate(chitAddRequestDTO.getStartDate() != null ? chitAddRequestDTO.getStartDate() : LocalDate.now());
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
		user.setCareOf(dto.getCareOf());
		user.setPhoneNo(dto.getPhoneNo());
		user.setRole("USER");
		user.setStatus(true);

		userRepository.save(user);
		
		return "User crated succesfully";
	}


	@Transactional
	public String addMember(Long chitId, Long userId) {
		
		Chits chit = chitRepository.findById(chitId)
				.orElseThrow(() -> new ResourceNotFoundException("No chit found to add member: "+ chitId));
		
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("No memeber found to add in chit: "+ userId));
		
//		if (chitMemberRepository.existsByUserAndChit(user, chit)) {
//		    throw new RuntimeException("User already joined this chit");
//		}
		
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
	
	
	@Transactional
	public PaymentReceiptDTO processPayment(PaymentRequestDTO dto) {
	    
	    // 1. Validate if user is in that chit
	    ChitMembers chitMember = chitMemberRepository.findByChitIdAndUserId(dto.getChitId(), dto.getUserId())
	            .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this chit"));

	    
	    // 2. Fetch the exact month selected by the admin
	    Installments selectedInstallment = installmentRepository
	            .findByChitMemberIdAndMonthNumber(chitMember.getId(), dto.getMonthNumber())
	            .orElseThrow(() -> new ResourceNotFoundException("Invalid month number for this chit"));

	    Long actualAmount = (dto.getAmount() != null) ? dto.getAmount() : 0L;
	    Long actualFine = (dto.getFineAmount() != null) ? dto.getFineAmount() : 0L;
	    
	    
//	    3. create a trasaction of that installment.
	    Transactions transactions = new Transactions();
	    transactions.setInstallment(selectedInstallment);
	    transactions.setPaidAmount(actualAmount);	
	    transactions.setFineAmount(actualFine);
	    transactions.setPaidOn(LocalDateTime.now());
	    transactions.setPaymentMethod(dto.getPaymentMethod());
	    
	    Transactions savedTransaction = transactionRepository.save(transactions);
	    
//	    4.Update the installment paid Amt.
	    Long totalPaidAfterThis = selectedInstallment.getPaidAmt() + actualAmount;
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
	    		.paidAmount(actualAmount)
	    		.fineAmount(actualFine)
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
						.startDate(chit.getStartDate())
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
						.careOf(user.getCareOf())
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
		
		List<ChitMembers> members = chitMemberRepository.findByChitIdAndStatus(chitId, true);
		
		
		return members.stream()
		        .map(member -> ChitMemberDetailsDTO.builder()
		                .chitMemberId(member.getId())
		                .userId(member.getUser().getId())
		                .name(member.getUser().getName())
		                .careOf(member.getUser().getCareOf())
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
	    
	    Optional<ChitLifts> memberLiftOpt = chitLiftRepository.findByChitMemberId(chitMemberId);
	    
	    return installments.stream()
	            .map(installment -> {
	                
	                boolean isLiftedThisMonth = false;
	                Long liftAmt = null;
	                LocalDateTime liftDate = null;
	                
	                // 3. Mapping exact which month he lifted the chit
	                if (memberLiftOpt.isPresent() && memberLiftOpt.get().getMonthNumber().equals(installment.getMonthNumber())) {
	                    isLiftedThisMonth = true;
	                    liftAmt = memberLiftOpt.get().getLiftedAmount();
	                    liftDate = memberLiftOpt.get().getLiftedOn();
	                }
	                
	                return InstallmentDetailsDTO.builder()
	                        .installmentId(installment.getId())
	                        .monthNumber(installment.getMonthNumber())
	                        .expectedAmt(installment.getExpectedAmt())
	                        .paidAmt(installment.getPaidAmt())
	                        .status(installment.getStatus())
	                        
	                        // Mapping the new lift fields
//	                        .isLifted(isLiftedThisMonth)
	                        .liftedAmount(liftAmt)
	                        .liftedDate(liftDate)
	                        .build();
	            })
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
						.fineAmount(transaction.getFineAmount())
						.paymentMethod(transaction.getPaymentMethod())
						.paidOn(transaction.getPaidOn())
						.build())
				.toList();
	}


	public List<UserSearchResponseDTO> searchUser(String name, String phoneNo) {
		
		
	    if (name == null || name.trim().isEmpty() || phoneNo == null || phoneNo.trim().isEmpty()) {
	        throw new BadRequestException("Both Name and Phone Number are required for searching");
	    }

	    List<Users> users = userRepository.findByNameContainingIgnoreCaseAndPhoneNoContaining(name, phoneNo);

	    return users.stream().map(u -> new UserSearchResponseDTO(
	            u.getId(),
	            u.getName(),
	            u.getCareOf(),
	            u.getPhoneNo(),
	            u.getAddress(),
	            u.isStatus()
	    )).collect(Collectors.toList());
	}
	
	
	
	
	public String recordChitLift(ChitLiftRequestDTO dto) {
	    

		ChitMembers chitMember = chitMemberRepository.findByChitIdAndUserId(dto.getChitId(), dto.getUserId())
	            .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this chit"));

	    if (chitLiftRepository.existsByChitMemberId(chitMember.getId())) {
	        throw new BadRequestException("Blunder! This member has already lifted the chit. A member can only lift once.");
	    }

	    if (chitLiftRepository.existsByChitIdAndMonthNumber(dto.getChitId(), dto.getMonthNumber())) {
	        throw new BadRequestException("Month " + dto.getMonthNumber() + " has already been lifted by another member in this chit.");
	    }

	    Long actualLiftedAmount = (dto.getLiftedAmount() != null) ? dto.getLiftedAmount() : 0L;

	    ChitLifts chitLift = new ChitLifts();
	    chitLift.setChit(chitMember.getChit());
	    chitLift.setChitMember(chitMember);
	    chitLift.setMonthNumber(dto.getMonthNumber());
	    chitLift.setLiftedAmount(actualLiftedAmount);
//	    chitLift.setLifted(true);;
	    chitLift.setPaymentMethod(dto.getPaymentMethod());
	    chitLift.setLiftedOn(java.time.LocalDateTime.now());

	    chitLiftRepository.save(chitLift);

	    return "Success: Chit lifted by " + chitMember.getUser().getName() + " for Month " + dto.getMonthNumber();
	}
	
	
	
	public List<MonthlyFilterResponseDTO> getChitMonthReport(Long chitId, Integer monthNumber, String filterType) {
	    
	    if (!chitRepository.existsById(chitId)) {
	        throw new ResourceNotFoundException("Chit not found with ID: " + chitId);
	    }

	    List<Installments> installments = installmentRepository.findByChitMemberChitIdAndMonthNumber(chitId, monthNumber);

	    // 3. Map to DTO array
	    return installments.stream()
	    		.filter(inst -> inst.getChitMember().isStatus())
	            .filter(inst -> {
	                if (filterType == null || filterType.equalsIgnoreCase("ALL")) {
	                    return true;
	                }
	                if (filterType.equalsIgnoreCase("PAID")) {
	                    return inst.getStatus().equals("PAID");
	                }
	                if (filterType.equalsIgnoreCase("DUE")) {
	                    return inst.getStatus().equals("PENDING") || inst.getStatus().equals("PARTIAL");
	                }
	                return true; // Default fallback
	            })
	            
	            .map(inst -> {
	                Long dueAmount = inst.getExpectedAmt() - inst.getPaidAmt();
	                
	                return MonthlyFilterResponseDTO.builder()
	                        .chitId(inst.getChitMember().getChit().getId())
	                        .chitName(inst.getChitMember().getChit().getName())
	                        .userId(inst.getChitMember().getUser().getId())
	                        .memberName(inst.getChitMember().getUser().getName())
	                        .phoneNo(inst.getChitMember().getUser().getPhoneNo())
	                        .monthNumber(inst.getMonthNumber())
	                        .expectedAmt(inst.getExpectedAmt())
	                        .paidAmt(inst.getPaidAmt())
	                        .dueAmt(dueAmount)
	                        .status(inst.getStatus())
	                        .build();
	            }).collect(Collectors.toList());
	    
	}
	
	
	
	
	
	@Transactional
	public String createLoan(LoanCreateRequestDTO dto) {
	    
	    if (dto.getLoanAmount() == null || dto.getLoanAmount() <= 0) {
	        throw new BadRequestException("Loan amount must be greater than zero");
	    }
	    if (dto.getTotalMonths() == null || dto.getTotalMonths() <= 0) {
	        throw new BadRequestException("Total months must be at least 1");
	    }
	    if (dto.getEmiAmount() == null || dto.getEmiAmount() <= 0) {
	        throw new BadRequestException("EMI amount must be provided and greater than zero");
	    }

	    Users user = userRepository.findById(dto.getUserId())
	            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId()));

	    // 2. Create the Master Loan Record
	    Loans loan = new Loans();
	    loan.setUser(user);
	    loan.setLoanAmount(dto.getLoanAmount());
	    loan.setTotalMonths(dto.getTotalMonths());
	    loan.setEmiAmount(dto.getEmiAmount()); 
	    loan.setIssuedDate(LocalDateTime.now());
	    loan.setStatus(true);
	    
	    Loans savedLoan = loanRepository.save(loan);

	    List<LoanInstallments> installmentsList = new ArrayList<>();
	    
	    for (int i = 1; i <= dto.getTotalMonths(); i++) {
	        LoanInstallments inst = new LoanInstallments();
	        inst.setLoan(savedLoan);
	        inst.setMonthNumber(i);
	        
	        inst.setExpectedAmt(dto.getEmiAmount()); 
	        
	        inst.setPaidAmt(0L);
	        inst.setStatus("PENDING");
	        
	        inst.setDueDate(LocalDateTime.now().plusMonths(i));
	        
	        installmentsList.add(inst);
	    }
	    
	    loanInstallmentRepository.saveAll(installmentsList);

	    return "Successfully created loan. Total repayment will be " + (dto.getEmiAmount() * dto.getTotalMonths()) + " over " + dto.getTotalMonths() + " months.";
	}
	
	
	
	public List<LoanSummaryResponseDTO> getUserLoans(Long userId, boolean status) {
	    
	    if (!userRepository.existsById(userId)) {
	        throw new ResourceNotFoundException("User not found with ID: " + userId);
	    }
	    
	    List<Loans> userLoans = loanRepository.findByUserIdAndStatus(userId, status);
	    
	    if (userLoans.isEmpty()) {
	        throw new ResourceNotFoundException("No loans found for this user");
	    }

	    // 3. Map to DTO
	    return userLoans.stream().map(loan -> LoanSummaryResponseDTO.builder()
	            .loanId(loan.getId())
	            .loanAmount(loan.getLoanAmount())
	            .totalMonths(loan.getTotalMonths())
	            .emiAmount(loan.getEmiAmount())
	            .issuedDate(loan.getIssuedDate())
	            .status(loan.isStatus())
	            .build()
	    ).collect(Collectors.toList());
	}
	
	
	
	public List<LoanInstallmentResponseDTO> getLoanInstallments(Long loanId) {
		
	    // 1. Fetch all EMI rows for this loan
	    List<LoanInstallments> installments = loanInstallmentRepository.findByLoanIdOrderByMonthNumberAsc(loanId);
	    
	    if (installments.isEmpty()) {
	        throw new ResourceNotFoundException("No installments found for Loan ID: " + loanId);
	    }

	    // 2. Map to DTO so frontend can show the table
	    return installments.stream().map(inst -> {
	        Long due = inst.getExpectedAmt() - inst.getPaidAmt();
	        
	        return LoanInstallmentResponseDTO.builder()
	                .installmentId(inst.getId())
	                .monthNumber(inst.getMonthNumber())
	                .expectedAmt(inst.getExpectedAmt())
	                .paidAmt(inst.getPaidAmt())
	                .dueAmt(due)
	                .status(inst.getStatus())
	                .dueDate(inst.getDueDate())
	                .build();
	    }).collect(Collectors.toList());
	}


	public List<LoanTransactionResponseDTO> getLoanTransactions(Long installmentId) {
		
		if (!loanInstallmentRepository.existsById(installmentId)) {
	        throw new ResourceNotFoundException("Loan Installment not found with ID: " + installmentId);
	    }

		List<LoanTransactions> transactions = loanTransactionRepository.findByLoanInstallmentIdOrderByIdAsc(installmentId);

	    return transactions.stream().map(txn -> LoanTransactionResponseDTO.builder()
	            .transactionId(txn.getId())
	            .paidAmount(txn.getPaidAmount())
	            .paymentMethod(txn.getPaymentMethod())
	            .paidOn(txn.getPaidOn())
	            .build()
	    ).collect(Collectors.toList());
	}

	

	@Transactional
	public String payLoan(LoanPaymentRequestDTO dto) {
		
		LoanInstallments installment = loanInstallmentRepository.findById(dto.getLoanInstallmentId())
	            .orElseThrow(() -> new ResourceNotFoundException("Loan EMI not found with ID: " + dto.getLoanInstallmentId()));
		
		
		if (installment.getStatus().equals("PAID")) {
	        throw new BadRequestException("This EMI is already fully paid.");
	    }

	    Long paymentAmt = (dto.getAmount() != null) ? dto.getAmount() : 0L;
	    
	    if (paymentAmt <= 0) {
	        throw new BadRequestException("Payment amount must be greater than zero.");
	    }

	    
	 // Step A: Insert into Transaction Table
	    LoanTransactions transaction = new LoanTransactions();
	    transaction.setLoanInstallment(installment);
	    transaction.setPaidAmount(paymentAmt);
	    transaction.setPaymentMethod(dto.getPaymentMethod());
	    transaction.setPaidOn(LocalDateTime.now());
	    loanTransactionRepository.save(transaction);

	    Long totalPaidNow = installment.getPaidAmt() + paymentAmt;
	    installment.setPaidAmt(totalPaidNow);

	    if (totalPaidNow >= installment.getExpectedAmt()) {
	        installment.setStatus("PAID");
	    } else {
	        installment.setStatus("PARTIAL");
	    }
	    
	    loanInstallmentRepository.save(installment);

	    return "Successfully processed payment of " + paymentAmt + " for Loan EMI Month " + installment.getMonthNumber();
	}


	
	public String statusChange(Long chitId, boolean status) {
		
		Chits chit = chitRepository.findById(chitId)
				.orElseThrow(() -> new ResourceNotFoundException("Chit Not found to delete: "+ chitId));
		
		chit.setStatus(status);	
		chitRepository.save(chit);		
		
		return "Chit status changed succesfully to: "+ status;
	}


	@Transactional
	public String updateChitDetails(Long chitId, ChitUpdateRequestDTO dto) {
	    
	    Chits chit = chitRepository.findById(chitId)
	            .orElseThrow(() -> new ResourceNotFoundException("Chit not found with ID: " + chitId));
	    
	    Long oldTotalMonths = chit.getTotalMonths();
	    Long oldInstallmentAmt = chit.getInstallmentAmt();
	    
	    // 1. THE FIX: Resolve actual new values (fallback to old DB value if DTO is null)
	    Long newTotalMonths = dto.getTotalMonths() != null ? dto.getTotalMonths() : oldTotalMonths;
	    Long newInstallmentAmt = dto.getInstallmentAmt() != null ? dto.getInstallmentAmt() : oldInstallmentAmt;

	    // 2. Update Master Chit Table
	    if (dto.getChitName() != null) {
	        chit.setName(dto.getChitName());
	    }
	    if (dto.getTotalAmount() != null) {
	        chit.setTotalAmount(dto.getTotalAmount());	    	
	    }
	    chit.setTotalMonths(newTotalMonths);
	    chit.setInstallmentAmt(newInstallmentAmt);
	    
	    chitRepository.save(chit);
	    
	    // 3. SAFE FLAG CHECKS: Now we compare non-null values
	    boolean isAmountChanged = !oldInstallmentAmt.equals(newInstallmentAmt);
	    boolean isMonthsChanged = !oldTotalMonths.equals(newTotalMonths);
	    
	    if (isAmountChanged || isMonthsChanged) {
	        
	        List<ChitMembers> members = chitMemberRepository.findByChitId(chitId);
	        
	        for (ChitMembers member : members) {
	            List<Installments> existingInstallments = installmentRepository.findByChitMemberIdOrderByMonthNumberAsc(member.getId());

	            // A. UPDATE AMOUNT
	            if (isAmountChanged) {
	                for (Installments inst : existingInstallments) {
	                    inst.setExpectedAmt(newInstallmentAmt); // Safe variable used
	                }
	                installmentRepository.saveAll(existingInstallments);
	            }

	            // B. DELETE EXTRA MONTHS
	            if (newTotalMonths < oldTotalMonths) { // Safe variable used
	                List<Installments> toDelete = existingInstallments.stream()
	                        .filter(inst -> inst.getMonthNumber() > newTotalMonths) // Safe variable used
	                        .collect(Collectors.toList());
	                
	                installmentRepository.deleteAll(toDelete);
	            }

	            // C. ADD NEW MONTHS
	            if (newTotalMonths > oldTotalMonths) { // Safe variable used
	                List<Installments> newInstallments = new ArrayList<>();
	                for (long i = oldTotalMonths + 1; i <= newTotalMonths; i++) { // Safe variable used
	                    Installments inst = new Installments();
	                    inst.setChitMember(member);
	                    inst.setMonthNumber((int) i);
	                    inst.setExpectedAmt(newInstallmentAmt); // Safe variable used
	                    inst.setPaidAmt(0L);
	                    inst.setStatus("PENDING");
	                    newInstallments.add(inst);
	                }
	                installmentRepository.saveAll(newInstallments);
	            }
	        }
	    }

	    log.info("Chit updation completed and installment will updated depend on instal amt and months");
	    return "Chit and its dependent installments updated successfully.";
	}


	public String softDeleteLoan(Long loanId, boolean status) {
		
		Loans loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException("Loan not found to delete: "+ loanId));
		
		
		loan.setStatus(status);
		loanRepository.save(loan);
		
		log.info("Loan status changed succesfully: {}", status);
		
		return "Loan "+status+" Succesfully";
	}


	
	@Transactional
	public String loanDetailsUpdate(Long loanId, LoanUpdateDto dto) {
		
		Loans loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException("Loan not found to update Details: "+ loanId));
		
		Long oldEmiAmount = loan.getEmiAmount();
		Long oldTotalMonths = loan.getTotalMonths();
		
		Long newEmiAmount = dto.getEmiAmount() != null ? dto.getEmiAmount() : oldEmiAmount;
		Long newTotalMonths = dto.getTotalMonths() != null ? dto.getTotalMonths() : oldTotalMonths;
		
		
		if(dto.getLoanAmount() != null) {
			loan.setLoanAmount(dto.getLoanAmount());	
		}
		
		loan.setEmiAmount(newEmiAmount);	
		loan.setTotalMonths(newTotalMonths);
		
		loanRepository.save(loan);
		
		boolean isEmiAmountChanged = !oldEmiAmount.equals(newEmiAmount);
		boolean isNewTotalMonths = !oldTotalMonths.equals(newTotalMonths);	
		
		if(isEmiAmountChanged || isNewTotalMonths) {
			
			List<LoanInstallments> loanInstallments = loanInstallmentRepository.findByLoanId(loanId);
			
			if(isEmiAmountChanged) {
				
				for(LoanInstallments lanInstallments : loanInstallments) {
					lanInstallments.setExpectedAmt(newEmiAmount);
				}
				loanInstallmentRepository.saveAll(loanInstallments);				
			}
			
			
			if(newTotalMonths < oldTotalMonths) {
	            List<LoanInstallments> toDelete = loanInstallments.stream()
	            		.filter(loanInst -> loanInst.getMonthNumber() > newTotalMonths)
	            		.collect(Collectors.toList());
	            
	            loanInstallmentRepository.deleteAll(toDelete);
	            
			}
			
			if(newTotalMonths > oldTotalMonths) {
				
				List<LoanInstallments> addRows = new ArrayList<>();
				
				for (long i = oldTotalMonths +1; i <= newTotalMonths; i++) {
					LoanInstallments inst = new LoanInstallments();
	                inst.setLoan(loan);
	                inst.setMonthNumber((int) i);
	                inst.setExpectedAmt(newEmiAmount);
	                inst.setPaidAmt(0L);
	                inst.setStatus("PENDING");
	                inst.setDueDate(loan.getIssuedDate().plusMonths(i));
	                
	                addRows.add(inst);
	                
				}
				
				loanInstallmentRepository.saveAll(addRows);
				
			}
			
		}
		
		return "Loan details and installments updated successfully.";
	}


	public String softDeleteUser(Long userId, boolean status) {


		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found to delete Account: "+ userId));
		
		List<Loans> userLoans = loanRepository.findByUserIdAndStatus(userId, true);
	            
	    if (userLoans == null) {
	        throw new BadRequestException("Account deletion failed. You have active loans that must be cleared first.");
	    }
		
	    user.setStatus(status);
	    userRepository.save(user)	    ;
	    
		return "User Deleted Succesfully";
	}


	public String removeUserFromChit(Long chitMemberId) {
		
		ChitMembers chitMember = chitMemberRepository.findById(chitMemberId).
				orElseThrow(() -> new ResourceNotFoundException("Chit member Not found to remove from chit: "+ chitMemberId));
				
		
		chitMember.setStatus(false);	
		chitMemberRepository.save(chitMember);
		
//		List<Installments> allInstallments = installmentRepository.findByChitMemberIdOrderByMonthNumberAsc(chitMemberId);
//	    
//	    List<Installments> pendingToCancel = allInstallments.stream()
//	            .filter(inst -> inst.getPaidAmt() == 0 && inst.getStatus().equals("PENDING"))
//	            .collect(Collectors.toList());
//	            
//	    installmentRepository.deleteAll(pendingToCancel);
//		
		return "User removed from the chit Succesfully";
	}
}
