package com.dantal.clinical.voicenote;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class VoiceNoteRecordService extends ClinicalResourceService<VoiceNoteRecord> {
    public VoiceNoteRecordService(VoiceNoteRecordRepository repository) {
        super(repository, "Voice note");
    }

    @Override
    protected VoiceNoteRecord newEntity() {
        return new VoiceNoteRecord();
    }
}
