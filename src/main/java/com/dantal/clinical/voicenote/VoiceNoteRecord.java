package com.dantal.clinical.voicenote;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "voice_notes")
public class VoiceNoteRecord extends ClinicalResourceEntity {
}
