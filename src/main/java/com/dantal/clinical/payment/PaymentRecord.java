package com.dantal.clinical.payment;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class PaymentRecord extends ClinicalResourceEntity {
}
