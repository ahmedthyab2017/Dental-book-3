package com.dantal.clinical.settlement;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "settlements")
public class SettlementRecord extends ClinicalResourceEntity {
}
