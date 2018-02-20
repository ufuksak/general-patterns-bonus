package com.aurea.testgenerator.value

import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import com.github.javaparser.resolution.types.ResolvedPrimitiveType
import com.github.javaparser.resolution.types.ResolvedReferenceType
import com.github.javaparser.resolution.types.ResolvedType


class Types {

    static final OBJECT = new ClassOrInterfaceType("Object")

    static final Set<String> KNOWN_COLLECTION_TYPES = [
            'Iterable',
            'java.util.Iterable',
            'Collection',
            'java.util.Collection',
            'List',
            'java.util.List',
            'Set',
            'java.util.Set',
            'SortedSet',
            'java.util.SortedSet',
    ]

    static final Set<String> KNOWN_ITERABLE_TYPES = [
            'Iterable',
            'java.util.Iterable',
    ]

    static final Set<String> KNOWN_COMPARABLE_TYPES = [
            'BigDecimal',
            'java.math.BigDecimal']

    static final Set<String> KNOWN_MAP_TYPES = [
            'Map',
            'java.util.Map',
            'HashMap',
            'java.util.HashMap',
    ]

    static final Set<String> KNOWN_LIST_TYPES = [
            'List',
            'java.util.List',
            'ArrayList',
            'java.util.ArrayList',
    ]

    static final Set<String> KNOWN_SET_TYPES = [
            'Set',
            'java.util.Set',
            'HashSet',
            'java.util.HashSet',
    ]

    static boolean isString(Type type) {
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType coit = type.asClassOrInterfaceType()
            return coit.nameAsString in ['String', 'java.lang.String']
        }
        return false
    }

    static boolean isString(ResolvedType type) {
        type.referenceType && type.asReferenceType().qualifiedName in ['String', 'java.lang.String']
    }

    static boolean isDate(ClassOrInterfaceType type) {
        type.nameAsString in ['Date', 'java.util.Date', 'java.sql.Date']
    }

    static boolean isDate(ResolvedReferenceType type) {
        type.qualifiedName in ['Date', 'java.util.Date', 'java.sql.Date']
    }

    static boolean isSqlDate(ClassOrInterfaceType type) {
        type.nameAsString == 'java.sql.Date'
    }

    static boolean isSqlDate(ResolvedReferenceType type) {
        type.qualifiedName == 'java.sql.Date'
    }

    static boolean isException(ClassOrInterfaceType type) {
        type.nameAsString == 'Exception'
    }

    static boolean isException(ResolvedReferenceType type) {
        type.qualifiedName == 'java.lang.Exception'
    }

    static boolean isThrowable(ClassOrInterfaceType type) {
        type.nameAsString == 'Throwable'
    }

    static boolean isThrowable(ResolvedReferenceType type) {
        type.qualifiedName == 'java.lang.Throwable'
    }

    static boolean isRuntimeException(ClassOrInterfaceType type) {
        type.nameAsString == 'RuntimeException'
    }

    static boolean isRuntimeException(ResolvedReferenceType type) {
        type.qualifiedName == 'java.lang.RuntimeException'
    }

    static boolean isLocale(ClassOrInterfaceType type) {
        type.nameAsString == 'Locale'
    }

    static boolean isLocale(ResolvedReferenceType type) {
        type.qualifiedName == 'java.util.Locale'
    }

    static boolean isObject(ClassOrInterfaceType type) {
        type.nameAsString == 'java.lang.Object'
    }

    static boolean isObject(ResolvedReferenceType type) {
        type.qualifiedName == 'java.lang.Object'
    }

    static boolean isCollection(ClassOrInterfaceType type) {
        type.nameAsString in KNOWN_COLLECTION_TYPES
    }

    static boolean isCollection(ResolvedReferenceType type) {
        type.qualifiedName in KNOWN_COLLECTION_TYPES
    }

    static boolean isCollection(ResolvedType type) {
        type.referenceType && (type.asReferenceType().qualifiedName in KNOWN_COLLECTION_TYPES)
    }

    static boolean isIterable(ClassOrInterfaceType type) {
        type.nameAsString in KNOWN_ITERABLE_TYPES
    }

    static boolean isIterable(ResolvedReferenceType type) {
        type.qualifiedName in KNOWN_ITERABLE_TYPES
    }

    static boolean isIterable(ResolvedType type) {
        type.referenceType && (type.asReferenceType().qualifiedName in KNOWN_ITERABLE_TYPES)
    }

    static boolean isList(ClassOrInterfaceType type) {
        type.nameAsString in KNOWN_LIST_TYPES
    }

    static boolean isList(ResolvedReferenceType type) {
        type.qualifiedName in KNOWN_LIST_TYPES
    }

    static boolean isList(ResolvedType type) {
        type.referenceType && (type.asReferenceType().qualifiedName in KNOWN_LIST_TYPES)
    }

    static boolean isSet(ClassOrInterfaceType type) {
        type.nameAsString in KNOWN_SET_TYPES
    }

    static boolean isSet(ResolvedReferenceType type) {
        type.qualifiedName in KNOWN_SET_TYPES
    }

    static boolean isSet(ResolvedType type) {
        type.referenceType && (type.asReferenceType().qualifiedName in KNOWN_SET_TYPES)
    }

    static boolean isComparable(ClassOrInterfaceType type) {
        type.nameAsString in KNOWN_COMPARABLE_TYPES
    }

    static boolean isComparable(ResolvedReferenceType type) {
        type.qualifiedName in KNOWN_COMPARABLE_TYPES
    }

    static boolean isComparable(ResolvedType type) {
        type.referenceType && (type.asReferenceType().qualifiedName in KNOWN_COMPARABLE_TYPES)
    }

    static boolean isMap(ClassOrInterfaceType type) {
        type.nameAsString in KNOWN_MAP_TYPES
    }

    static boolean isMap(ResolvedReferenceType type) {
        type.qualifiedName in KNOWN_MAP_TYPES
    }

    static boolean isMap(ResolvedType type) {
        type.referenceType && (type.asReferenceType().qualifiedName in KNOWN_MAP_TYPES)
    }

    static boolean isEnumeration(ResolvedReferenceType type) {
        type.typeDeclaration.isEnum()
    }

    static boolean isEnumeration(ResolvedType type) {
        type.referenceType && (type.asReferenceType().typeDeclaration.isEnum())
    }

    static boolean isBoxedPrimitive(ResolvedReferenceType type) {
        for (ResolvedPrimitiveType primitive : ResolvedPrimitiveType.ALL) {
            if (primitive.boxTypeQName == type.qualifiedName) {
                return true
            }
        }
        return false
    }

    static ResolvedPrimitiveType unbox(ResolvedReferenceType type) {
        for (ResolvedPrimitiveType primitive : ResolvedPrimitiveType.ALL) {
            if (primitive.boxTypeQName == type.qualifiedName) {
                return primitive
            }
        }
        return null
    }

    static boolean ofBooleanType(ResolvedType type) {
        (type.primitive && type.asPrimitive() == ResolvedPrimitiveType.BOOLEAN) ||
                (type.referenceType && (type.asReferenceType().qualifiedName in ['Boolean', 'java.lang.Boolean']))
    }

    static boolean areSameOrBoxedSame(ResolvedType left, ResolvedType right) {
        unboxIfBoxed(left) == unboxIfBoxed(right)
    }

    static ResolvedType unboxIfBoxed(ResolvedType type) {
        if (type.referenceType) {
            if (isBoxedPrimitive(type.asReferenceType())) {
                return unbox(type.asReferenceType())
            }
        }
        type
    }
}
