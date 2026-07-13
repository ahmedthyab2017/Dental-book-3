package com.dantal.clinical.referral;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "referrals")
public class ReferralRecord extends ClinicalResourceEntity {
}
