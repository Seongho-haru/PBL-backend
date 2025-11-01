package com.PBL.ai.enums;

public enum CollectionType {
    LECTURE("PBL_lecture"),
    CURRICILUM("PBL_curriculum"),
    PYTHON("programming_books_python"),
    JAVA("programming_books_java"),
    ALGORITHM("programming_books_algorithms");

    private String collectionName;

    CollectionType(String programmingBooksAlgorithms) {
        this.collectionName = programmingBooksAlgorithms;
    }
}
