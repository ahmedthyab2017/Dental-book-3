package com.dantal.clinical.staff;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "staff_members")
public class StaffRecord extends ClinicalResourceEntity {
}
