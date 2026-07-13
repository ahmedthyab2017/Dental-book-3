package com.dantal.clinical.plan;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "treatment_plans")
public class PlanRecord extends ClinicalResourceEntity {
}
