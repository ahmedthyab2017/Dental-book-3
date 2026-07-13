package com.dantal.clinical.voicenote;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/voice-notes")
@Tag(name = "Voice note")
@SecurityRequirement(name = "bearerAuth")
public class VoiceNoteRecordController extends ClinicalResourceController {
    public VoiceNoteRecordController(VoiceNoteRecordService service) {
        super(service, "voiceNotes", "voiceNote");
    }
}
