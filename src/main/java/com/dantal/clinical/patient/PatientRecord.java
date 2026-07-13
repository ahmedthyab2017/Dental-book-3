package com.dantal.clinical.patient;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class PatientRecord extends ClinicalResourceEntity {
}
