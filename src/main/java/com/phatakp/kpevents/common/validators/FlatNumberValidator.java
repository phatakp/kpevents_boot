package com.phatakp.kpevents.common.validators;

import com.phatakp.kpevents.common.dto.FlatNumberInput;
import com.phatakp.kpevents.common.enums.Building;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FlatNumberValidator implements ConstraintValidator<ValidFlatNumber, FlatNumberInput> {
    private final Map<Building,Short> FLOORS_PER_BUILDING = new HashMap<>();


    @Override
    public void initialize(ValidFlatNumber constraintAnnotation) {
        // Optional: Initialization logic if needed
        FLOORS_PER_BUILDING.put(Building.A, (short) 12);
        FLOORS_PER_BUILDING.put(Building.B, (short) 12);
        FLOORS_PER_BUILDING.put(Building.C, (short) 11);
        FLOORS_PER_BUILDING.put(Building.D, (short) 11);
        FLOORS_PER_BUILDING.put(Building.E, (short) 12);
        FLOORS_PER_BUILDING.put(Building.F, (short) 12);
        FLOORS_PER_BUILDING.put(Building.G, (short) 12);
    }

    @Override
    public boolean isValid(FlatNumberInput request, ConstraintValidatorContext context) {

        var flat = request.flat();
        var building = request.building();
        var floors = FLOORS_PER_BUILDING.get(building);
        for (int i = 1; i <= floors ; i++) {
            for (int j = 0; j <= 4; j++) {
                short flatNum = (short) (i*100+j);
                if (flatNum == flat) {
                    return true;
                }
            }
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Invalid Flat Number: " + building+flat)
                .addPropertyNode("flat")
                .addConstraintViolation();
        return false;
    }

}


