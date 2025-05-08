package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.TalentProfileDTO;
import com.Ivan.Rwalent.model.User;

public interface TalentProfileService {

    /**
     * Retrieves the comprehensive profile for a given talent user.
     * If a TalentProfile doesn't exist for the user, a new one will be initialized.
     * @param talentUser The talent user whose profile is to be retrieved.
     * @return TalentProfileDTO containing combined User and TalentProfile information.
     */
    TalentProfileDTO getTalentProfile(User talentUser);

    /**
     * Updates the profile for a given talent user.
     * This includes details from both User and TalentProfile entities.
     * @param talentUser The talent user whose profile is to be updated.
     * @param talentProfileDTO DTO containing the new profile information.
     * @return Updated TalentProfileDTO.
     */
    TalentProfileDTO updateTalentProfile(User talentUser, TalentProfileDTO talentProfileDTO);
} 