package com.dantal.clinical.casedoc;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "case_docs")
public class CaseRecord extends ClinicalResourceEntity {
}
