package com.example.my_book_shop_app.struct.enums;

public enum Book2UserRelationType {
    KEPT(1),
    CART(2),
    PAID(3),
    ARCHIVED(4);

    private final int typeId;

    Book2UserRelationType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
}
