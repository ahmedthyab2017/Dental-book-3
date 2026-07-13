package com.dantal.clinical.vendor;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendors")
public class VendorRecord extends ClinicalResourceEntity {
}
