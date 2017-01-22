package com.faforever.gw.validation;

import com.faforever.gw.model.BattleParticipant;
import com.faforever.gw.model.BattleStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;


public class CharacterFreeForBattleValidator implements ConstraintValidator<ValidateCharacterFreeForBattle, BattleParticipant> {
    private static final EnumSet<BattleStatus> OCCUPIED_BATTLE_STATUSSES = EnumSet.of(BattleStatus.INITIATED, BattleStatus.RUNNING);

    @Override
    public void initialize(ValidateCharacterFreeForBattle constraintAnnotation) {

    }

    @Override
    public boolean isValid(BattleParticipant value, ConstraintValidatorContext context) {
        if (value.getCharacter().getBattleParticipantList().stream()
                .filter(battleParticipant -> OCCUPIED_BATTLE_STATUSSES.contains(battleParticipant.getBattle().getStatus()))
                .count() <= 1)
            return true;
        else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("This character is still bound in a different battle.")
                    .addConstraintViolation();
            return false;
        }
    }
}
