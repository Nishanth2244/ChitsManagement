package com.project.chitti.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class ChitLifts {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "chit_id")
    private Chits chit; 
    
    @ManyToOne
    @JoinColumn(name = "chit_member_id", unique = true) 
    private ChitMembers chitMember;
    
    private Integer monthNumber; 
    
    private Long liftedAmount; 
        
    private LocalDateTime liftedOn;

    private LocalDate liftedDate;
//    private boolean isLifted;
    
    private String paymentMethod; 
}