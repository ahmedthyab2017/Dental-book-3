package com.dantal.clinical.prescription;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "prescriptions")
public class PrescriptionRecord extends ClinicalResourceEntity {
}
