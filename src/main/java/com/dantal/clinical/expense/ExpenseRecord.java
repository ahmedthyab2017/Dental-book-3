package com.dantal.clinical.expense;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class ExpenseRecord extends ClinicalResourceEntity {
}
