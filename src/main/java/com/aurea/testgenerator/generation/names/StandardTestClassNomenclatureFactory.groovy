package com.aurea.testgenerator.generation.names

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(["manual", "casting"])
class StandardTestClassNomenclatureFactory implements TestClassNomenclatureFactory {
    @Override
    TestClassNomenclature newTestClassNomenclature() {
        new StandardTestClassNomenclature()
    }
}
